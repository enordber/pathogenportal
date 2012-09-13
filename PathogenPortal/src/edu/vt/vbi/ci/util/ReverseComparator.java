package edu.vt.vbi.ci.util;

import java.util.Comparator;

/**
 * Takes an existing Comparator, and returns the opposite answer, so 
 * the array will end up sorted in opposite order.
 * 
 * @author enordber
 *
 */
public class ReverseComparator implements Comparator{

	private Comparator original;
	
	public ReverseComparator(Comparator original) {
		this.original = original;
	}
	
	public int compare(Object arg0, Object arg1) {
		return -original.compare(arg0, arg1);
	}

}
