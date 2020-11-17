/*
** Tests the public OptionalDouble divideBy(OptionalDouble optionalDoubleToDivideBy)
** function from OptionalDouble class
*/


package org.miradi.utils;

import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TestDivideBy {

	public static void main(String[] args) {
		
		DecimalFormat formatter = new DecimalFormat("0.0000000000");
                formatter.setRoundingMode(RoundingMode.HALF_UP);

		args = args[0].split(" ");
		double valueOfDouble = Double.valueOf(args[0]);
		double valueToDivideBy = Double.valueOf(args[1]);
		OptionalDouble oracle = new OptionalDouble(Double.valueOf(args[2]));
		
		
		OptionalDouble oD = new OptionalDouble(valueOfDouble);
		OptionalDouble oDToDivideBy = new OptionalDouble(valueToDivideBy);
		
		OptionalDouble quotient = oD.divideBy(oDToDivideBy);
		double quotValue = quotient.getValue();
		quotValue = Double.valueOf(formatter.format(quotValue));
		String pass = (oracle.getValue() == Double.valueOf(formatter.format(quotient.getValue()))) ? "Passed" : "Failed";
		
		
		System.out.print(String.valueOf(quotValue) + ";" + pass);
		
		
		
		
		
	
	
	
	}



}
