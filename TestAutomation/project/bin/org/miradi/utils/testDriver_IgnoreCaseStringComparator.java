// Driver to test compare method of IgnoreCaseStringComparator class
//
// Ian Duderar, Jacob Lipsey, Jacob Nash, Jacob Roddam
// Team 5, CSCI 362


public class testDriver_IgnoreCaseStringComparator {

	// inputs: string1, string2
	public static void main(String args[]) {
		IgnoreCaseStringComparator comparator = new IgnoreCaseStringComparator();
		String str1 = args[0];
		String str2 = args[1];
		int resultOfCompare = comparator.compare(str1, str2);
		System.out.println(resultOfCompare);
	}
}
