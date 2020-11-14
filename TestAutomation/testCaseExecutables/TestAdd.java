/*
** Tests the public OptionalDouble add(OptionalDouble optionalDoubleToAdd)
** function from OptionalDouble class
*/


package org.miradi.utils;

import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class TestAdd {

	public static void main(String[] args) {
		
		args = args[0].split(" ");
		double valueOfDouble = Double.valueOf(args[0]);
		double valueToAdd = Double.valueOf(args[1]);
		OptionalDouble oracle = new OptionalDouble(Double.valueOf(args[2]));
		
		
		OptionalDouble oD = new OptionalDouble(valueOfDouble);
		OptionalDouble oDToAdd = new OptionalDouble(valueToAdd);
		
		OptionalDouble sum = oD.add(oDToAdd);
		double sumValue = sum.getValue();
		String pass = (oracle.equals(sum)) ? "Passed" : "Failed";
		
		
		System.out.print(String.valueOf(sumValue) + ";" + pass);
		
		
		
		
		
		/*
		
		// send sum to temp file so that script can read it
		try {
			File file = new File(outFile);
			FileWriter fileWriter = new FileWriter(file);
			//System.out.print(String.valueOf(sumValue));
			fileWriter.write(String.valueOf(sumValue));
			fileWriter.flush();
			fileWriter.close();
		} catch(IOException e) {
			System.out.println("IO error");
		}
		
		*/
		
		
		
		
		
		
	
	
	
	}



}
