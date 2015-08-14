package de.pspaeth.dla;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Timer;

import de.pspaeth.dla.data.Staub;
import de.pspaeth.dla.util.Holder;

/**
 * Draws a DLA calculated via Main. Draws step-by-step, but fast-forward
 */
public class Paint2 extends Paint implements ActionListener {
	private static final long serialVersionUID = 5016308273662376401L;

	private static final int UPDATE_PERIOD = 100;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Paint2 paint = new Paint2();
				setupFrame(paint);
				paint.go();
			}
		});
	}

	protected Paint2() {
		super();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	private void go() {
		graphicsThread();
		paintThread();		
	}

	private void graphicsThread() {
		Timer t = new Timer(UPDATE_PERIOD, this);
		t.start();
	}
	
	private void paintThread() {
		Thread thr = new Thread(new Runnable() {
			public void run() {
				paintMe();
			}
		});
		thr.start();
	}

	private void paintMe() {
		Graphics g = bi.getGraphics();
		g.setColor(Color.blue);

		List<Staub> l = rm.getFest().values().stream().collect(Collectors.toList());
		l.sort(new Comparator<Staub>() {
			public int compare(Staub o1, Staub o2) {
				int t1 = o1.getFestGeworden();
				int t2 = o2.getFestGeworden();
				return (t1 < t2) ? -1 : (t2 < t1 ? 1 : 0);
			}
		});

		Holder<Integer> cnt = Holder.make(0);
		l.stream().forEach(stb -> {
			cnt.incr();
			int x = (int) (W * stb.getZeit());
			int y = (int) (H * stb.getTon());
			int w = Math.max(1, (int) (W / NUM_ZEIT));
			int h = Math.max(1, (int) (W / NUM_TON));
			g.fillRect(x, y, w, h);
			try {
				if (cnt.v % 2 == 0)
					Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		});

	}
}
