/*
** Tests the public OptionalDouble multiply(OptionalDouble optionalDoubleToAdd)
** function from OptionalDouble class
*/


package org.miradi.utils;

import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class TestMultiply {

	public static void main(String[] args) {
		
		args = args[0].split(" ");
		//System.out.println(args[0]);
		double valueOfDouble = Double.valueOf(args[0]);
		double valueToMultiply = Double.valueOf(args[1]);
		OptionalDouble oracle = new OptionalDouble(Double.valueOf(args[2]));
		String outFile = args[3];
		//System.out.println(outFile);
		
		OptionalDouble oD = new OptionalDouble(valueOfDouble);
		OptionalDouble oDToMultiply = new OptionalDouble(valueToMultiply);
		
		OptionalDouble product = oD.multiply(oDToMultiply);
		double prodValue = product.getValue();
		
		
		// send sum to temp file so that script can read it
		try {
			File file = new File(outFile);
			FileWriter fileWriter = new FileWriter(file);
			//System.out.print(String.valueOf(sumValue));
			fileWriter.write(String.valueOf(prodValue));
			fileWriter.flush();
			fileWriter.close();
		} catch(IOException e) {
			System.out.println("IO error");
		}
		
		
		
		
	
	
	
	}



}
