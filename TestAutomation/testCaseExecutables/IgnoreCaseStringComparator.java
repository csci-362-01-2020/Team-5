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

import java.util.Comparator;

public class IgnoreCaseStringComparator implements Comparator<Object>
{
	public int compare(Object object1, Object object2)
	{
		 /*Original, CORRECT code */
		return object1.toString().compareToIgnoreCase(object2.toString()); 
		// orginal code ends
		
		
		/* Code with fault injected (doesn't ignore case for comparison) (should cause test number 12 to fail) 
		return object1.toString().compareTo(object2.toString()); */
		// faulty code ends 
	}
}
