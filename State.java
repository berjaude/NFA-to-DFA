package edu;

/**
 * Class that represents a state in the NFA or DFA graph
 * Each state contains information about its origin, its number 
 * and a transit connecting it to its origin state.
 */
public class State {
	private int orig;
	private int num;
	private int transit;
	
	//Constructor
	public State(int orig, int num, int transit) {
		this.orig = orig;
		this.num = num;
		this.transit = transit;
	}
	
	//Constructor
	public State() {}
	
	//Origin getter
	public int getOrig() {
		return orig;
	}
	//Origin setter
	public void setOrig(int orig) {
		this.orig = orig;
	}
	//Number getter
	public int getNum() {
		return num;
	}
	//Number setter
	public void setNum(int num) {
		this.num = num;
	}
	//Transit getter
	public int getTransit() {
		return transit;
	}
	//Transit setter
	public void setTransit(int transit) {
		this.transit = transit;
	}
}
