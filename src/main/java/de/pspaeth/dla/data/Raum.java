package de.pspaeth.dla.data;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Raum bestehend aus Dimensionen Tonhöhe und Zeit. Enthält feste und bewegliche Staub Objekte.
 */
public class Raum {
	public static enum Prospektive {
		FREI, BESETZT, KANN_KLEBEN
	};

	// Diagonalen zählen mit
	// private final static int[][] UMRUNDUNG = new int[][] { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 }, { 0,
	// -1 }, { 1, -1 },
	// { 1, 0 }, { 1, 1 } };

	// Diagonalen zählen nicht mit
	private final static int[][] UMRUNDUNG = new int[][] { { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 0 } };

	protected double quantZeit;
	protected double quantTon;
	protected int maxCoordsZeit;
	protected int maxCoordsTon;

	private int idCtr = 1; // Zähler für technische ID

	protected SortedMap<BigInteger, Staub> fest = new TreeMap<>();

	protected Collection<Staub> beweglich = new HashSet<>();

	/**
	 * Konstruktor. Erzeugt ein einzelnes festes Staubkorn in der Mitte. Bewegliche Staubkörner müssen
	 * mit <code>randomStaub()</code> hinzugefügt werden.
	 * 
	 * @param zeitCoords  Raumgröße horizontal (Zeit) 
	 * @param tonCoords Raumgröße vertikal (Anzahl Töne)
	 */
	public Raum(int zeitCoords, int tonCoords) {
		this.maxCoordsZeit = zeitCoords;
		this.maxCoordsTon = tonCoords;
		this.quantZeit = 1d / zeitCoords;
		this.quantTon = 1d / tonCoords;
		addFest(new Staub(makeNewStaubId(), 0.5, 0.5));
	}

	/**
	 * Fügt ein festes Staubkorn hinzu
	 * @param stb Das Staubkorn
	 */
	public void addFest(Staub stb) {
		Staub stb2 = quantisiere(stb);
		fest.put(sortIndex(stb2.getZeit(), stb2.getTon()), stb2);
	}

	/**
	 * Erzeugt und platziert (zufällig) ein bewegliches Staubkorn
	 * @return Das erzeugte Staubkorn
	 */
	public Staub randomStaub() {
		Staub res = null;
		while (true) {
			Staub stb = quantisiere(new Staub(makeNewStaubId(), Math.random(), Math.random()));
			if (ermittleProspektive(stb) == Prospektive.FREI) {
				beweglich.add(stb);
				res = stb;
				break;
			}
		}
		return res;
	}

	/**
	 * Stellt fest, was mit einem Staubkorn gemacht werden kann.
	 * @param stb Das Staubkorn
	 * @return Die Prospektive
	 */
	public Prospektive ermittleProspektive(Staub stb) {
		return ermittleProspektive(stb.getZeit(), stb.getTon());
	}

	/**
	 * Quantisiert auf ganzzahlige Raumkoordinaten (nicht Grafikkoordinaten!)
	 * @param stb Das Staubkorn. Wird verändert!
	 * @return Das quantisierte Staubkorn
	 */
	public Staub quantisiere(Staub stb) {
		double zeit = Math.round(stb.getZeit() / quantZeit) * quantZeit;
		double ton = Math.round(stb.getTon() / quantTon) * quantTon;
		stb.setTon(ton);
		stb.setZeit(zeit);
		return stb;
	}

	public int anzahlFest() {
		return fest.size();
	}

	public int anzahlBeweglich() {
		return beweglich.size();
	}

	public Collection<Staub> getBeweglich() {
		return beweglich;
	}

	public void macheFest(Staub stb, int cnt) {
		boolean removed = beweglich.remove(stb);
		if (!removed)
			throw new RuntimeException("nicht in 'beweglich'");
		fest.put(sortIndex(stb.getZeit(), stb.getTon()), stb);
		stb.jetztFest(cnt);
	}

	/**
	 * Zufällige BROWN'sche Beqwegung aller beweglichen Staubkörner.
	 */
	public void brown() {
		beweglich.forEach(stb -> {
			double zeit = stb.getZeit();
			double ton = stb.getTon();
			double rnd = Math.random();
			if (rnd < 0.25) {
				zeit += quantZeit;
			} else if (rnd < 0.5) {
				zeit -= quantZeit;
			} else if (rnd < 0.75) {
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

	/**
	 * Alle beweglichen Staubkörner eventuell anheften (falls ein festes Staubkorn angrenzt)
	 * @param cnt Aktueller Zähler
	 */
	public void vielleichtAnheften(int cnt) {
		beweglich.stream().filter(stb -> ermittleProspektive(stb) == Prospektive.KANN_KLEBEN)
				.collect(Collectors.toSet()).forEach(stb -> {
					macheFest(stb, cnt);
				});
	}

	public Map<BigInteger, Staub> getFest() {
		return fest;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////

	private String makeNewStaubId() {
		return "" + (idCtr++);
	}

	private int zeitToInt(double zeit) {
		return (int) Math.round(zeit / quantZeit);
	}

	private int tonToInt(double ton) {
		return (int) Math.round(ton / quantTon);
	}

	/**
	 * Ermittle internen Index.
	 */
	private BigInteger sortIndex(double zeit, double ton) {
		double zeit1 = zeit < 0d ? zeit + 1d : (zeit >= 1d ? zeit - 1d : zeit);
		double ton1 = ton < 0d ? ton + 1d : (ton >= 1d ? ton - 1d : ton);
		return BigInteger.valueOf(zeitToInt(zeit1)).multiply(BigInteger.valueOf(maxCoordsTon))
				.add(BigInteger.valueOf(tonToInt(ton1)));
	}

	private Prospektive ermittleProspektive(double zeit, double ton) {
		Prospektive res = Prospektive.FREI;
		BigInteger si = sortIndex(zeit, ton);
		if (fest.containsKey(si)) {
			res = Prospektive.BESETZT;
		} else {
			for (int[] a : UMRUNDUNG) {
				double zeit1 = zeit + 1d * a[0] / maxCoordsZeit;
				double ton1 = ton + 1d * a[1] / maxCoordsTon;
				si = sortIndex(zeit1, ton1);
				if (fest.containsKey(si)) {
					res = Prospektive.KANN_KLEBEN;
					break;
				}
			}
		}
		return res;
	}
}
