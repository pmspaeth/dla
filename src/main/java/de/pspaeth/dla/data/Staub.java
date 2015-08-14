package de.pspaeth.dla.data;

/**
 * Ein Staubkorn. Bewegt sich im Raum, und kann sich mit anderen Staubkörnchen
 * zusammenballen.
 */
public class Staub {
	private String id;
	private double zeit; // [0;1[
	private double ton; // [0;1[
	private int festGeworden = 0;

	public Staub(String id, double zeit, double ton) {
		this.id = id;
		this.zeit = zeit;
		this.ton = ton;
	}

	public Staub(String id, double zeit, double ton, int festGeworden) {
		this.id = id;
		this.zeit = zeit;
		this.ton = ton;
		this.festGeworden = festGeworden;
	}
	
	public double getZeit() {
		return zeit;
	}

	public double getTon() {
		return ton;
	}
	
	public String getId() {
		return id;
	}
	
	public void setZeit(double zeit) {
		this.zeit = zeit;
	}

	public void setTon(double ton) {
		this.ton = ton;
	}
	
	public void jetztFest(int cnt) {
		festGeworden = cnt;
	}

	public int getFestGeworden() {
		return festGeworden;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (other.getClass() != getClass())
			return false;
		if (! id.equals(((Staub)other).getId()))
			return false;
//		if ( ton != ((Staub) other).getTon())
//			return false;
//		if ( zeit != ((Staub) other).getZeit())
//			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
//		return id.hashCode() +  41 * new Double(zeit + 41 * ton).hashCode();
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "Staub [id=" + id + ", zeit=" + zeit + ", ton=" + ton + ", festGeworden=" + festGeworden + "]";
	}
}
