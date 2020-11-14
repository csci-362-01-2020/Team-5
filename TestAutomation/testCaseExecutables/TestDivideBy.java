/*
** Tests the public OptionalDouble multiply(OptionalDouble optionalDoubleToAdd)
** function from OptionalDouble class
*/


package org.miradi.utils;

import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class TestDivideBy {

	public static void main(String[] args) {
		
		args = args[0].split(" ");
		
		double valueOfDouble = Double.valueOf(args[0]);
		double valueToDivideBy = Double.valueOf(args[1]);
		OptionalDouble oracle = new OptionalDouble(Double.valueOf(args[2]));
		
		
		OptionalDouble oD = new OptionalDouble(valueOfDouble);
		OptionalDouble oDToDivideBy = new OptionalDouble(valueToDivideBy);
		
		OptionalDouble quotient = oD.divideBy(oDToDivideBy);
		double quotValue = quotient.getValue();
		String pass = (oracle.equals(quotient)) ? "Passed" : "Failed";
		
		
		System.out.print(String.valueOf(quotValue) + ";" + pass);
		
		
		
		
		
	
	
	
	}



}
