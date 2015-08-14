package de.pspaeth.dla.util;

/**
 * A holder to be used inside closures.
 *
 * @param <T> The type of the held value
 */
public class Holder<T> {
	public T v;

	public Holder(T v) {
		this.v = v;
	}
	
	public static <R> Holder<R> make(R v) {
		return new Holder<R>(v);
	}
	
	/**
	 * Increase by 1. Only works for Integers and Longs
	 * @throws UnsupportedOperationException
	 */
	@SuppressWarnings("unchecked")
	public void incr() throws UnsupportedOperationException {
		if(v instanceof Integer) {
			v = (T)( new Integer( ((Integer)v).intValue() + 1));
		} else if(v instanceof Long) {
			v = (T)( new Long( ((Long)v).longValue() + 1));
		} else {
			throw new UnsupportedOperationException("incr() only works for integers"); 
		}
	}
}
