
/*
** Tests the public int compare(Object object1, Object object2)
** function from IgnoreCaseStringComparator class
*/


package org.miradi.utils;

import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class TestStringComparator {

        public static void main(String[] args) {
                
                args = args[0].split(" ");
                String firstString = args[0];
                String secondString = args[1];
		int oracle = Integer.valueOf(args[2]);
		
                IgnoreCaseStringComparator comparator = new IgnoreCaseStringComparator();
                
		int comparisonResult = comparator.compare(firstString, secondString);
		String pass = (Integer.signum(oracle) == Integer.signum(comparisonResult)) ? "Passed" : "Failed";
                
                
                System.out.print(String.valueOf(Integer.signum(comparisonResult)) + ";" + pass);
                
                
                
                
                
        
        
        
        }



}

