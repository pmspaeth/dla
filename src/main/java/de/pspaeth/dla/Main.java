package de.pspaeth.dla;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.pspaeth.dla.data.Raum;
import de.pspaeth.dla.data.RaumMitFeld;
import de.pspaeth.dla.data.Staub;

/**
 * Diffusion Limited Aggregation https://en.wikipedia.org/wiki/Diffusion-limited_aggregation This is targeted
 * towards a sound piece.
 */
public class Main extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1335817592134678566L;

	private static final int NUM_ZEIT = 1000; // horizontal
	private static final int NUM_TON = 1000; // vertical
	private static final int NUM_STAUB = 30000; // number of particles
	private static final int REFRESH_MSECS = 10000; // graphics refresh
	private static final int W = 1000; // graphics width
	private static final int H = 1000; // graphics height

	// private Raum rm = new Raum(NUM_ZEIT, NUM_TON);
	private Raum rm = new RaumMitFeld(NUM_ZEIT, NUM_TON, 0, 0.01);

	private final BufferedImage bi;

	private String status = "";

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Main main = new Main();
				setupFrame(main);
				main.go();
			}
		});
	}

	private Main() {
		super(true);
		bi = initGraphics();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		drawCanvas();
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bi, 0, 0, null);
	}

	// ///////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////

	private void go() {
		graphicsThread();
		calculationThread();
		initialPaint();		
	}

	private static void setupFrame(Main main) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(main);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void initialPaint() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				drawCanvas();
				repaint();
			}
		});
	}

	/**
	 * We repaint on a regular basis based on a swing timer
	 * 
	 * @return The timer (started)
	 */
	private Timer graphicsThread() {
		Timer t = new Timer(REFRESH_MSECS, this);
		t.start();
		return t;
	}

	/**
	 * Calculation is done in background. When the calculation is done, a CSV output is written
	 */
	private void calculationThread() {
		Thread calcThread = new Thread(new Runnable() {
			public void run() {
				IntStream.range(0, NUM_STAUB).forEach(i -> {
					rm.randomStaub();
				});
				int cnt = 0;
				while (rm.anzahlBeweglich() > 0) {
					cnt++;
					rm.brown();
					rm.vielleichtAnheften(cnt);
					if (rm.anzahlBeweglich() + rm.anzahlFest() < NUM_STAUB)
						rm.randomStaub();
					if ((cnt % 1000) == 0) {
						status = "Counter=" + cnt + " - beweglich=" + rm.anzahlBeweglich() + " fest=" + rm.anzahlFest();
					}
				}
				writeOut();
				System.err.println("FERTIG.");
			}

		});
		calcThread.start();
	}

	private BufferedImage initGraphics() {
		this.setLayout(new GridLayout());
		this.setPreferredSize(new Dimension(W, H));
		return new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Draw DLA fractal
	 */
	private void drawCanvas() {
		Graphics g = bi.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, W, H);
		g.setColor(Color.blue);
		g.setFont(new Font("serif", Font.PLAIN, 20));

		if (rm.getFest().size() < 1000) {
			String info1 = "Please wait while the fractal is being calculated";
			String info2 = "Especially at the beginning this will take some time";
			g.drawChars(info1.toCharArray(), 0, info1.length(), 10, 100);
			g.drawChars(info2.toCharArray(), 0, info2.length(), 10, 130);
		}
		g.drawChars(status.toCharArray(), 0, status.length(), 10, 60);

		Collection<Staub> fest = rm.getFest().values();
		try {
			fest.stream().forEach(stb -> {
				int x = (int) (W * stb.getZeit());
				int y = (int) (H * stb.getTon());
				int w = Math.max(1, (int) (W / NUM_ZEIT));
				int h = Math.max(1, (int) (W / NUM_TON));
				g.fillRect(x, y, w, h);
			});
		} catch (ConcurrentModificationException e) {
			// Passiert manchmal - tut nicht weh
		}

		g.dispose();
	}

	/**
	 * Write fractal as CSV to file in current working directory
	 */
	private void writeOut() {
		try {
			final String tstamp = (new SimpleDateFormat("yyyy-MM-dd-HHmm").format(new Date()));
			final String foutName = "dla." + tstamp + ".csv";
			final FileWriter fw = new FileWriter(foutName);
			rm.getFest().values().stream().forEach(stb -> {
				try {
					fw.append("" + stb.getZeit() + "," + stb.getTon() + "," + stb.getFestGeworden() + "\n");
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			});
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
}
