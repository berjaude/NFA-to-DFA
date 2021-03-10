package edu;

import java.util.List;
import java.util.ArrayList;

public class Arraylist {

	public static void main(String[] args) {
		List<List<Integer>> newList = new ArrayList<>();
		List<Integer> list = new ArrayList<>();
		
		list.add(4);
		list.add(5);
		newList.add(list);
	}

}
