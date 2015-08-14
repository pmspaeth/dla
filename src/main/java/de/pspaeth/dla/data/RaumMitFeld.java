package de.pspaeth.dla.data;

/**
 * Raum bestehend aus Dimensionen Tonhöhe und Zeit. Wie <code>Raum</code>, hat aber zusätzlich ein Feld.
 */
public class RaumMitFeld extends Raum {

	private double feldAng;
	private double feldStaerke;

	private double cos;
	private double sin;

	public RaumMitFeld(int zeitCoords, int tonCoords, double feldAng, double feldStaerke) {
		super(zeitCoords, tonCoords);
		this.feldAng = feldAng;
		this.feldStaerke = feldStaerke;
		this.cos = Math.cos(feldAng * Math.PI / 180);
		this.sin = Math.sin(feldAng * Math.PI / 180);
	}

	@Override
	public void brown() {
		double xp = 1d + feldStaerke * cos;
		double xm = 1d - feldStaerke * cos;
		double yp = 1d + feldStaerke * sin;
		@SuppressWarnings("unused")
		double ym = 1d - feldStaerke * sin;

		double rnd1 = xp / 4;
		double rnd2 = (xp + xm) / 4;
		double rnd3 = (xp + xm + yp) / 4;

		beweglich.forEach(stb -> {
			double zeit = stb.getZeit();
			double ton = stb.getTon();
			double rnd = Math.random();
			if (rnd < rnd1) {
				zeit += quantZeit;
			} else if (rnd < rnd2) {
				zeit -= quantZeit;
			} else if (rnd < rnd3) {
				ton += quantTon;
			} else {
				ton -= quantTon;
			}
			if (zeit < 0d)
				zeit += 1d;
			if (zeit >= 1d)
				zeit -= 1d;
			if (ton < 0d)
				ton += 1d;
			if (ton >= 1d)
				ton -= 1d;
			stb.setZeit(zeit);
			stb.setTon(ton);
			quantisiere(stb);
		});
	}
}
