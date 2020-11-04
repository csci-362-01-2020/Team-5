// Written by Jacob Nash for CSCI 362, team 5
//

package org.miradi.utils;

public class TestCaseCompare {
	public static void main(String args[]) {
		if (args.length<2) {
			System.out.println("This driver takes two arguments");
		}
		String input1 = args[0];
		String input2 = args[1];
		IgnoreCaseStringComparator comparator = new IgnoreCaseStringComparator();
		int result = comparator.compare(input1, input2);
		System.out.println(result);
	}
}
