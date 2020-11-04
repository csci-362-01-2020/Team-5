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

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.miradi.objecthelpers.DateUnit;

public class DateUnitEffortList
{
	public DateUnitEffortList()
	{
		this(new Vector<DateUnitEffort>());
	}
	
	public DateUnitEffortList(String listAsJsonString) throws Exception
	{
		this(new EnhancedJsonObject(listAsJsonString));
	}
	
	public DateUnitEffortList(EnhancedJsonObject json) throws Exception
	{
		this();
		EnhancedJsonArray array = json.optJsonArray(TAG_DATEUNIT_EFFORTS);
		for(int index = 0; index < array.length(); ++index)
		{
			add(new DateUnitEffort(array.getJson(index)));
		}
	}
	
	private DateUnitEffortList(List<DateUnitEffort> listToUse)
	{
		data = new Vector<DateUnitEffort>(listToUse);
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		JSONArray array = new JSONArray();
		for(DateUnitEffort dateUnitEffort : data)
		{
			array.put(dateUnitEffort.toJson());
		}
		json.put(TAG_DATEUNIT_EFFORTS, array);
		
		return json;
	}
	
	public void add(DateUnitEffort dateUnitEffortToUse)
	{
		data.add(dateUnitEffortToUse);
	}

	@Override
	public String toString()
	{
		if(size() == 0)
			return "";
		
		Collections.sort(data);
		return toJson().toString();
	}
	
	public DateUnitEffort getDateUnitEffortForSpecificDateUnit(DateUnit dateUnitToUse)
	{
		for(DateUnitEffort dateUnitEffort : data)
		{
			if (dateUnitEffort.getDateUnit().equals(dateUnitToUse))
				return dateUnitEffort;
		}
		
		return null;
	}
	
	public void setDateUnitEffort(DateUnitEffort dateUnitEffortToUse)
	{
		DateUnitEffort dre = getDateUnitEffortForSpecificDateUnit(dateUnitEffortToUse.getDateUnit());
		if(dre != null)
			data.remove(dre);

		data.add(dateUnitEffortToUse);
	}
		
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
			
	@Override
	public boolean equals(Object other)
	{	
		if (! (other instanceof DateUnitEffortList))
			return false;
		
		return other.toString().equals(toString());	
	}
	
	public int size()
	{
		return data.size();
	}
	
	public DateUnitEffort getDateUnitEffort(int index)
	{
		return data.get(index);
	}
	
	public void remove(DateUnit dateUnit)
	{
		for(DateUnitEffort dateUnitEffort : data)
		{
			if(dateUnitEffort.getDateUnit().equals(dateUnit))
			{
				data.remove(dateUnitEffort);
				return;
			}
		}
	}
	
	private Vector<DateUnitEffort> data;
	private static final String TAG_DATEUNIT_EFFORTS = "DateUnitEfforts";
}
