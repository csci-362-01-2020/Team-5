/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.utils;


public class OptionalDouble
{	
	public OptionalDouble()
	{
		this(null);
	}
	
	public OptionalDouble(Double doubleToUse)
	{
		optionalDouble = doubleToUse;
		
		resetHasValue(optionalDouble);
	}
	
	private void resetHasValue(Double valueToUse)
	{
		if (valueToUse != null)
			hasValue = true;
	}
	
	public OptionalDouble addValue(Double doubleToAdd)
	{
		return add(new OptionalDouble(doubleToAdd));
	}
	
	public OptionalDouble subtract(OptionalDouble optionalDoubleToSubtract)
	{
		/* original correct code 
		if (isNonNullAndHasValue(optionalDoubleToSubtract))
			return new OptionalDouble(getSafeRawValue() - optionalDoubleToSubtract.getSafeRawValue());
		*/ 
		// correct code ends
		
		/* code with fault inserted (returns absolute value of difference) (makes test 7 fail) */
		if (isNonNullAndHasValue(optionalDoubleToSubtract)) {
		
			double val = getSafeRawValue() - optionalDoubleToSubtract.getSafeRawValue();
			// absolute value
			double valAbsVal = (val < 0) ? (val * -1) : val;
			return new OptionalDouble(valAbsVal);
		}
		
		
		return createCopy();
	}

	public OptionalDouble add(OptionalDouble optionalDoubleToAdd)
	{ 
	
		 /*Original, correct code 
		if (isNonNullAndHasValue(optionalDoubleToAdd))
			return new OptionalDouble(getSafeRawValue() + optionalDoubleToAdd.getSafeRawValue()); 
		*/
		// original code ends
		
		/* Code with fault (rounds sum to nearest integer) (makes tests 2, 3, 4, and 5 fail */
		int valInt;
		double val;
		if (isNonNullAndHasValue(optionalDoubleToAdd)) {
			valInt = (int) ( Math.round(getSafeRawValue()) ) + (int) ( Math.round(optionalDoubleToAdd.getSafeRawValue()) );
			val = valInt + 0.0;
			return new OptionalDouble(val); 
		}
		//  faulty code ends
		
		return createCopy();
	}
	
	public OptionalDouble multiply(OptionalDouble optionalDoubleToMultiply)
	{
		/* original correct code
		if (isNonNullAndHasValue(optionalDoubleToMultiply) && hasValue())
			return new OptionalDouble(optionalDoubleToMultiply.getRawValue() * getRawValue());
		
		*/
		// correct code ends
		
		/* code with fault inserted (multiplies result by -1) (makes tests 16, 17, 19, and 20 fail) */
		if (isNonNullAndHasValue(optionalDoubleToMultiply) && hasValue())
			return new OptionalDouble((-1)*optionalDoubleToMultiply.getRawValue() * getRawValue());
		// faulty code ends
		
		return new OptionalDouble();
	}
	
	public OptionalDouble divideBy(OptionalDouble optionalDoubleToDivideBy)
	{
		/* Original Correct code
		if (isNonNullAndHasValue(optionalDoubleToDivideBy) && hasValue())
			return new OptionalDouble(getRawValue() / optionalDoubleToDivideBy.getRawValue());
		*/
		// original code ends
		
		/* code with fault (switches dividend and divisor) (tests 22, 23, 24, and 25 fail) */
		if (isNonNullAndHasValue(optionalDoubleToDivideBy) && hasValue())
			return new OptionalDouble(optionalDoubleToDivideBy.getRawValue() / getRawValue());
			
		
		return new OptionalDouble();
	}
	
	private double getSafeRawValue()
	{
		if (hasNoValue())
			return 0.0;
		
		return getRawValue();
	}
	
	private OptionalDouble createCopy()
	{
		return new OptionalDouble(getRawValue());
	}

	public OptionalDouble multiplyValue(Double doubleToMultiply)
	{
		return multiply(new OptionalDouble(doubleToMultiply));
	}
	
	private boolean isNonNullAndHasValue(OptionalDouble optionalDoubleToUse)
	{
		if (optionalDoubleToUse == null)
			return false;
		
		return optionalDoubleToUse.hasValue();
	}
	
	public boolean hasValue()
	{
		return hasValue;
	}
	
	public boolean hasNoValue()
	{
		return !hasValue();
	}
	
	private Double getRawValue()
	{
		return optionalDouble;
	}
	
	public double getValue()
	{
		return getRawValue().doubleValue();
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if (! (rawOther instanceof OptionalDouble))
			return false;
	
		OptionalDouble other = (OptionalDouble) rawOther;
		if (!other.hasValue() && !hasValue())
			return true;
		
		if (!other.hasValue())
			return false;
		
		return other.getRawValue().equals(getRawValue());
	}
	
	@Override
	public int hashCode()
	{
		if (hasValue())
			return optionalDouble.hashCode();
		
		return 0;
	}
	
	@Override
	public String toString()
	{
		if(hasValue())
			return FloatingPointFormatter.formatEditableValue(getRawValue());
		
		return "";
	}
	
	public String toUnformattedString()
	{
		return getRawValue().toString();
	}
	
	public boolean hasNonZeroValue()
	{
		if (hasNoValue())
			return false;
		
		return !isValuePresentButZero();
	}
	
	public boolean isZeroValue()
	{
		if (hasNoValue())
			return false;
		
		return isValuePresentButZero();
	}

	private boolean isValuePresentButZero()
	{
		return getRawValue() == 0.0;
	}
	
	private boolean hasValue;
	private Double optionalDouble;
}
