package de.pspaeth.dla;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.ConcurrentModificationException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import de.pspaeth.dla.data.Raum;
import de.pspaeth.dla.data.Staub;

/**
 * Draws a DLA calculated via Main.
 */
public class Paint extends JPanel {
	private static final long serialVersionUID = 5016308273662376401L;

	private File inputFile; 
	protected static int NUM_ZEIT = 1000;
	protected static int NUM_TON = 1000;
	protected static final int W = 1000;
	protected static final int H = 1000;

	protected Raum rm = new Raum(NUM_ZEIT, NUM_TON);
	protected BufferedImage bi;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Paint paint = new Paint();
				setupFrame(paint);
				paint.go();
			}
		});
	}

	protected Paint() {
		super(true);
		readIn();
		initGraphics();
	}
	

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bi, 0, 0, null);
	}

	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	private void readIn() {
		chooseFile();
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
			String line;
			int cnt = 1;
			while ((line = br.readLine()) != null) {
				String[] spl = line.split(",");
				double zeit = Double.parseDouble(spl[0]);
				double ton = Double.parseDouble(spl[1]);
				int festCnt = Integer.parseInt(spl[2]);

				Staub stb = new Staub("" + cnt, zeit, ton, festCnt);
				rm.addFest(stb);

				cnt++;
			}
			System.err.println("Read Lines: " + (cnt - 1));
			System.err.println("Raum Größe: " + rm.anzahlFest());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private void initGraphics() {
		this.setLayout(new GridLayout());
		this.setPreferredSize(new Dimension(W, H));
		bi = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
	}
	
	protected static void setupFrame(Paint paint) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(paint);
		frame.pack();
		frame.setVisible(true);
	}

	private void go() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				drawCanvas();
				repaint();
			}
		});
	}
	
	private void drawCanvas() {
		Graphics g = bi.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, W, H);
		g.setColor(Color.blue);

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
	
	private void chooseFile() {
		final JFileChooser fc = new JFileChooser(new File("."));
		FileFilter ff = new FileFilter(){
			public boolean accept(File f) {
				return f.getName().startsWith("dla.") && f.getName().endsWith(".csv");
			}
			public String getDescription() {
				return "DLA data files";
			}};
		fc.setFileFilter(ff);
		fc.showOpenDialog(this);
		inputFile = fc.getSelectedFile();
	}
}
