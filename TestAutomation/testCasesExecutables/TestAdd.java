/*
** Tests the public OptionalDouble add(OptionalDouble optionalDoubleToAdd)
** function from OptionalDouble class
*/


package org.miradi.utils;

import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class TestAdd {

	public static void main(String[] args) {
	
		double valueOfDouble = Double.valueOf(args[0]);
		double valueToAdd = Double.valueOf(args[1]);
		OptionalDouble oracle = OptionalDouble(Double.valueOf(args[2]);
		String outFile = "../temp/" + args[3];
		
		
		OptionalDouble oD = OptionalDouble(valueOfDouble);
		OptionalDouble oDToAdd = OptionalDouble(valueToAdd);
		
		OptionalDouble sum = oD.add(oDToAdd);
		double sumValue = sum.getValue();
		
		
		// send sum to temp file so that script can read it
		try {
			FileWriter fileWriter = new FileWriter(outFile);
			fileWriter.write(sumValue);
		} catch(IOException e) {
			System.out.println("IO error");
		}
		
		
		
		
	
	
	
	}



}
