package edu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author berjaude Class that implements the conversion of a graph from NFA to
 *         DFA Prints the NFA along with its Sigma, accepting state and final
 *         states set Converts it to DFA Prints the DFA along with its Sigma,
 *         accepting state and final states set Parse the results of strings in
 *         a given text file from the graph
 */
public class NFA {
	// No. of states in the graph, size of alphabet set "Sigma"
	private int states = 0, alpha = 0;
	private int inState; // initial state
	int lambdaIndex; // index of lambda
	private List<String> sigma; // set of alphabet
	// accepting set for NFA and DFA
	private Set<Integer> accpetingSetNFA, accpetingSetDFA;
	private List<Set<Integer>> P; // set of new states for DFA
	// actual NFA and DFA graphs
	private Graph nfa, dfa;

	public static void main(String[] args) throws FileNotFoundException {
		// Scanner sc = new Scanner(args[0]);
		System.out.print("Enter file name ('file.txt'): ");
		Scanner in = new Scanner(System.in);
		String file = in.next();

		NFA dfa = new NFA(file);

		dfa.createDFAGraph(); // creates a DFA graph

		System.out.println();
		System.out.println("Parsing results of strings in strings.txt:");
		dfa.parseResult("strings.txt");

		in.close(); // close the scanner
	}

	// Constructor
	NFA(String file) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(file));
		String str;

		str = sc.nextLine(); // supposed to read a number
		states = Integer.parseInt(str);
		// Initialize graph
		nfa = new Graph(states);

		str = sc.nextLine(); // supposed to read set of alphabet
		// uses string tokenizer to split the string by space
		StringTokenizer sg = new StringTokenizer(str);
		/*
		 * Initialize sigma and fill it with letters Prints is
		 */
		sigma = new ArrayList<>();
		while (sg.hasMoreTokens()) {
			sigma.add(sg.nextToken());
		}
		sigma.add("λ");
		alpha = sigma.size();
		lambdaIndex = sigma.size() - 1;
		System.out.println("Sigma: " + str);
		System.out.println("-------");

		// Prints out the NFA graph while constructing the graph
		for (int i = 0; i < states; i++) {
			str = sc.nextLine().trim(); // get line and make sure the string is trimmed
			String[] p = str.split("[^0-9{},]+"); // only read numbers,{}, and ,

			System.out.print(i + ": ");
			for (int j = 1; j < p.length; j++) {
				System.out.print("(" + sigma.get(j - 1) + "," + p[j] + ") ");
			}
			System.out.println();

			constructNFAGraph(nfa, i, p); // constructs the graph
		}

		inState = Integer.parseInt(sc.nextLine()); // supposed to read the initial states
		String finalSet = sc.nextLine(); // supposed to read the final states set
		getAcceptingSet(finalSet); // creates the final accepting states set

		System.out.println("-------");
		System.out.println(inState + ":\tInitial state");
		System.out.println(accpetingSetNFA + ":\tAccepting State(s)");
		System.out.println();

		sc.close();
	}

	// Makes NFA's accepting states set
	private void getAcceptingSet(String set) {
		accpetingSetNFA = new HashSet<>();
		// get rid of {} and split by ','
		StringTokenizer st = new StringTokenizer(clean(set), ",");
		while (st.hasMoreTokens()) {
			String temp = st.nextToken();
			int fs = Integer.parseInt(temp);
			accpetingSetNFA.add(fs);
		}
	}

	// creates DFA graph
	public void createDFAGraph() {
		P = new ArrayList<>();
		// list of DFA states
		List<State> DFAstates = new ArrayList<>();
		accpetingSetDFA = new HashSet<>();
		State temp;

		// starts with p0, whihc is the λ-Closure from state 0
		Set<Integer> p0 = nfa.lambdaClosure(0, lambdaIndex);
		P.add(p0);

		int count = 0, ind = 0;
		int prevSize = 0;
		int currentSize = P.size();

		// Loops until no more state can be created
		// At the end the set of DFA states will be totally defined
		while (currentSize > prevSize) {
			for (int j = P.size() - 1; j < P.size(); j++) {
				prevSize = P.size(); // keeps track of P size before each adjustment
				for (int s = 0; s < alpha - 1; s++) {
					Set<Integer> p = new HashSet<>();
					Iterator<Integer> i = P.get(j).iterator();
					while (i.hasNext()) {
						int state = i.next();
						// gets all the states that can be reached from 'state' via 's'
						p.addAll(nfa.transitStates(state, s, lambdaIndex));
					}

					if (!belongTo(p)) {
						P.add(p); // adds new state to P
						count++; // keeps track of index of newly created state
						// store origin state, destination state and transit into DFA
						temp = new State();
						temp.setOrig(j);
						temp.setNum(count);
						temp.setTransit(s);
						DFAstates.add(temp);
					} else {
						ind = indexOf(p); // get the index of the existing state in P
						// store origin state, destination state and transit into DFA
						temp = new State();
						temp.setOrig(j);
						temp.setNum(ind);
						temp.setTransit(s);
						DFAstates.add(temp);
					}
				}
				currentSize = P.size(); // keeps track of P size after each adjustment
			}
		}

		// constructs graph for dfa
		dfa = new Graph(P.size());
		for (int i = 0; i < DFAstates.size(); i++) {
			State t = DFAstates.get(i);
			dfa.addTranist(t.getOrig(), t.getNum(), t.getTransit());
		}

		/*
		 * Make DFA accepting set Adds a state in the accepting set if p x F ins't empty
		 */
		for (int i = 0; i < P.size(); i++) {
			boolean found = false;
			Iterator<Integer> itr = P.get(i).iterator();
			while (itr.hasNext() && !found) {
				int p = itr.next();
				if (accpetingSetNFA.contains(p)) {
					found = true;
					accpetingSetDFA.add(i);
				}
			}
		}

		// Display DFA
		System.out.println();
		System.out.println("TO DFA:");
		System.out.println("-----------");
		display(dfa); // display function
		System.out.println("-----------");

		System.out.println(inState + ":\tInitial State");
		System.out.println(accpetingSetDFA + ":\tDFA Accepting State(s)");
	}

	// Parsing results of strings in a given text file
	public void parseResult(String file) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(file));
		String str;
		List<Integer> list = null;

		// Used for design purposes and visibility ease
		int line = 0;
		for (int i = 1; i <= 15; i++)
			System.out.print("(" + i + ")\t");
		System.out.println();

		while (sc.hasNextLine()) {
			str = sc.nextLine();
			if (str.isEmpty()) { // handle empty string case
				if (accpetingSetDFA.contains(0)) // only accepts it if p0 is an accepting state
					System.out.print("Yes\t");
				else
					System.out.print("No\t");
			} else {
				list = new ArrayList<>();
				// take the string and convert each alpha to its corresponding
				// number in the list
				for (int i = 0; i < str.length(); i++) {
					String temp = str.substring(i, i + 1);
					list.add(sigma.indexOf(temp));
				}

				if (list.contains(-1)) { // rejects a string contains a letter not in Sigma
					System.out.print("No\t");
				} else if (dfa.acceptString(list, accpetingSetDFA)) { // accpets
					System.out.print("Yes\t");
				} else { // doesn't end at an accpeting state
					System.out.print("No\t");
				}
			}

			// Used for design purposes and visibility ease
			if (++line == 15)
				System.out.println();
		}
		sc.close(); // close the scanner
	}

	// constructs the NFA graph by connecting two states via a transit
	private void constructNFAGraph(Graph g, int start, String[] line) {
		StringTokenizer st;

		for (int i = 1; i < line.length; i++) {
			// get rid of {} and split by ','
			st = new StringTokenizer(clean(line[i]), ",");
			while (st.hasMoreTokens()) {
				String val = st.nextToken();
				int end = Integer.parseInt(val);
				g.addTranist(start, end, i - 1);
			}
		}
	}

	// displays the DFA graph
	private void display(Graph g) {
		LinkedList<State> adjacent[] = g.getGraph(); // access the graph
		// starts by printing out the alphabet
		System.out.print("Sigma:\t");
		for (int i = 0; i < sigma.size() - 1; i++) {
			System.out.print(sigma.get(i) + "\t");
		}
		System.out.println();
		// Prints out the origin states along with their adjacent states
		for (int i = 0; i < g.getSize(); i++) {
			System.out.print(i + ":\t");
			for (int j = 0; j < sigma.size(); j++) {
				Iterator<State> itr = adjacent[i].listIterator();
				while (itr.hasNext()) {
					State temp = itr.next();
					int s = temp.getNum();
					int t = temp.getTransit();
					if (t == j)
						System.out.print(s + " ");
				}
				System.out.print("\t");
			}
			System.out.println();
		}
	}

	// returns True if a set p already exists in P
	private boolean belongTo(Set<Integer> p) {
		boolean ret = false;
		Iterator<Set<Integer>> itr = P.iterator();
		while (itr.hasNext() && !ret) {
			Set<Integer> t = itr.next();
			if (t.equals(p))
				ret = true;
		}
		return ret;
	}

	// returns the index of a set p in P
	private int indexOf(Set<Integer> p) {
		boolean ret = false;
		int pos = -1;
		Iterator<Set<Integer>> itr = P.iterator();
		while (itr.hasNext() && !ret) {
			Set<Integer> t = itr.next();
			if (t.equals(p)) {
				ret = true;
				pos = P.indexOf(t);
			}
		}
		return pos;
	}

	// returns a string with no '{}' and no leading and trailing space
	private String clean(String str) {
		String s = str.replace('{', ' ');
		s = s.replace('}', ' ');
		return s.trim();
	}

}
