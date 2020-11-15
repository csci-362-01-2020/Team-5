/*
** Tests the public OptionalDouble multiply(OptionalDouble optionalDoubleToMultiply)
** function from OptionalDouble class
*/


package org.miradi.utils;

import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class TestMultiply {

	public static void main(String[] args) {
		
		args = args[0].split(" ");
		double valueOfDouble = Double.valueOf(args[0]);
		double valueToMultiply = Double.valueOf(args[1]);
		OptionalDouble oracle = new OptionalDouble(Double.valueOf(args[2]));
		
		OptionalDouble oD = new OptionalDouble(valueOfDouble);
		OptionalDouble oDToMultiply = new OptionalDouble(valueToMultiply);
		
		OptionalDouble product = oD.multiply(oDToMultiply);
		double prodValue = product.getValue();
		String pass = (oracle.equals(product)) ? "Passed" : "Failed";
		
		
		System.out.print(String.valueOf(prodValue) + ";" + pass);
		
		
		
		
		
	
	
	
	}



}
