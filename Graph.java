package edu;

//Java class uses DFS to traverse a given graph
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


//This class represents a directed graph using adjacency list 
//representation 
class Graph {
	private int V; // Number of vertices

	// Array of lists for Adjacency List Representation
	private LinkedList<State> adjacent[];

	@SuppressWarnings("unchecked")
	// Constructor
	Graph(int v) {
		V = v;
		adjacent = new LinkedList[V];
		for (int i = 0; i < v; ++i)
			adjacent[i] = new LinkedList<>();
		
	}
	
	//graph getter
	public LinkedList<State>[] getGraph(){
		return adjacent;
	}
	
	//return number of vertices
	public int getSize() {
		return V;
	}
	
	//Adds an edge between two states along with the edge tag(called 'transit') 
	public void addTranist(int v, int w, int transit) {
		// Add w and transit to v's list.
		State e = new State(); 
		e.setNum(w);
		e.setTransit(transit);
		adjacent[v].add(e);
	}
	
	/*
	 * Displays the graph.
	 * Shows all the states and the transit that connect them
	 */
	public void displayGraph() {
		//displays a state and its adjacent states along with the associated transit
		for(int i=0; i<V; i++) {
			Iterator<State> itr = adjacent[i].listIterator();
			System.out.print(i + ":\t");
			while(itr.hasNext()) {
				State temp = itr.next();
				int s = temp.getNum();
				int t = temp.getTransit();
				System.out.print("("+s + "," + t+")\t");
			}
			System.out.println();
		}
		
	}
	
	//Recursive function used by lambdaClosure
	private void goLambda(int state, int input, boolean visited[], Set<Integer> p) {
		visited[state] = true;
		//Add state to set p
		p.add(state);
		
		Iterator<State> i = adjacent[state].listIterator();
		while(i.hasNext()) {
			State temp = i.next();
			int s = temp.getNum();
			int t = temp.getTransit();
			if(!visited[s] && t == input) {
				goLambda(s, t, visited, p);
			}
		}
	}
	
	/*
	 * Returns a set of all states reachable via Lambda
	 */
	public Set<Integer> lambdaClosure(int state, int input) {
		boolean visited[] = new boolean[V];
		Set<Integer> p = new HashSet<>();
		goLambda(state, input, visited, p); //call helping function
		return p;
	}
	
	
	/*
	 * Transition function
	 * Returns a set of all states reachable by a given transit ticket
	 * Rule: a valid transit ticket can only be used once for each destination
	 */
	public Set<Integer> transitStates(int state, int input, int lambdaIndex) {
		Set<Integer> p = new HashSet<>();
		Iterator<State> i = adjacent[state].listIterator();
		while(i.hasNext()) {
			State temp = i.next();
			int s = temp.getNum();
			int t = temp.getTransit();
			if(t == input) {
				p.add(s);
				p.addAll(lambdaClosure(s, lambdaIndex));
			}
		}
		return p;
	}
	
	//Returns the next state reachable by parameter "state" via "input"
	private int accept(int state, int input) {
		int ret = -1;
		boolean found = false;
		//condition for safety although it may never happen
		if(input < 0) {
			System.out.println("-1 happened");
			return ret;
		}
		
		Iterator<State> i = adjacent[state].listIterator();
		//Stop the search once the next state is reached
		while(i.hasNext() && !found) {
			State temp = i.next();
			int s = temp.getNum();
			int t = temp.getTransit();
			if(t == input) {
				ret = s;
				found = true;
			}
		}
		return ret;
	}
	
	/*
	 * Returns True if the graph can accept a given string
	 */
	public boolean acceptString(List<Integer> list, Set<Integer> set) {
		boolean ret = false;
		
		//Traverses the string and states that are invovled
		int state = accept(0, list.get(0));
		for(int i=1; i<list.size() && state >= 0; i++) {
			state = accept(state, list.get(i));
		}
		
		//returns only true if the last reachable state is an accepting state
		if(state >= 0 && set.contains(state)) {
			ret = true;
		}
		
		return ret;
	}
	
	public static void main(String args[]) {
		
//		Graph t = new Graph(6);
//		//Add edges associated with transits
//		//Adjacent edges to 0
//		t.addTranist(0, 1, 0);
//		t.addTranist(0, 2, 2);
//		t.addTranist(0, 4, 1);
//		//Adjacent edges to 1
//		t.addTranist(1, 2, 1);
//		t.addTranist(1, 5, 0);
//		//Adjacent edges to 2
//		t.addTranist(2, 5, 0);
//		//Adjacent edges to 3
//		t.addTranist(3, 0, 2);
//		//Adjacent edges to 4
//		t.addTranist(4, 1, 0);
//		t.addTranist(4, 3, 2);
//		//Adjacent edges to 5
//		t.addTranist(5, 4, 2);
		
		Graph t = new Graph(4);
		//Add edges associated with transits
		//Adjacent edges to 0
		t.addTranist(0, 1, 3);
		t.addTranist(0, 2, 3);
		t.addTranist(0, 3, 3);
		//Adjacent edges to 1
		t.addTranist(1, 1, 0);
		t.addTranist(1, 1, 1);
		//Adjacent edges to 2
		t.addTranist(2, 2, 0);
		t.addTranist(2, 2, 2);
		//Adjacent edges to 3
		t.addTranist(3, 3, 1);
		t.addTranist(3, 3, 2);
		
		System.out.println("Following is the graph: ");
		System.out.println();
		t.displayGraph();
		
		System.out.println();
		System.out.println("Tranists:");
		System.out.println("from 0 via a " + t.transitStates(0, 0, 3));
		System.out.println("from 0 via b " + t.transitStates(0, 1, 3));
		System.out.println("from 0 via c " + t.transitStates(0, 2, 3));
		
		System.out.println("from 1 via a " + t.transitStates(1, 0, 3));
		System.out.println("from 1 via b " + t.transitStates(1, 1, 3));
		System.out.println("from 1 via c " + t.transitStates(1, 2, 3));
		
		System.out.println("from 2 via a " + t.transitStates(2, 0, 3));
		System.out.println("from 2 via b " + t.transitStates(2, 1, 3));
		System.out.println("from 2 via c " + t.transitStates(2, 2, 3));
		
		System.out.println("from 3 via a " + t.transitStates(3, 0, 3));
		System.out.println("from 3 via b " + t.transitStates(3, 1, 3));
		System.out.println("from 3 via c " + t.transitStates(3, 2, 3));
		//System.out.println(t.transitStates(0, 3, 3));
	}
}
