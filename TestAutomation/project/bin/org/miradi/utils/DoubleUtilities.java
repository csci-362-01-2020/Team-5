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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DoubleUtilities
{
	public static double toDoubleFromDataFormat(String doubleAsString) throws Exception
	{
		NumberFormat formatter = NumberFormat.getInstance(createUsLocale());
		return formatter.parse(doubleAsString).doubleValue();
	}

	public static double toDoubleFromHumanFormat(String doubleAsString) throws Exception
	{
		NumberFormat formatter = NumberFormat.getInstance();
		return formatter.parse(doubleAsString).doubleValue();
	}

	public static String toStringForData(double doubleToConvert)
	{
		DecimalFormat formatter = (DecimalFormat) DecimalFormat.getNumberInstance(createUsLocale());
		applyPatternToAvoidScientificNotation(formatter);

		return formatter.format(doubleToConvert).toString();
	}

	public static String toStringForData(OptionalDouble doubleToConvert)
	{
		if (doubleToConvert.hasValue())
			return toStringForData(doubleToConvert.getValue());

		return "";
	}

	private static void applyPatternToAvoidScientificNotation(DecimalFormat formatter)
	{
		formatter.applyPattern("#.#");
		formatter.setMaximumFractionDigits(Integer.MAX_VALUE);
	}

	public static String toStringForHumans(double doubleToConvert)
	{
		NumberFormat formatter = NumberFormat.getInstance();
		return formatter.format(doubleToConvert);
	}
	
	private static Locale createUsLocale()
	{
		return Locale.US;
	}
}
