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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

public class PointList
{
	public PointList()
	{
		this(new Vector<Point>());
	}
	
	public PointList(PointList copyFrom)
	{
		this(copyFrom.createClone().points);
	}
	
	public PointList(EnhancedJsonObject json) throws Exception
	{
		this();
		EnhancedJsonArray array = json.optJsonArray(TAG_POINTS);
		if(array == null)
			array = new EnhancedJsonArray();
		
		for(int i = 0; i < array.length(); ++i)
		{
			Point point = EnhancedJsonObject.convertToPoint(array.getString(i));
			add(point);
		}
	}
	
	public PointList(String listAsJsonString) throws Exception
	{
		this(new EnhancedJsonObject(listAsJsonString));
	}
	
	private PointList(List<Point> dataToUse)
	{
		points = new Vector<Point>(dataToUse);
	}
	
	public int size()
	{
		return points.size();
	}
	
	public void insertAt(Point point, int index)
	{
		points.insertElementAt(point, index);
	}
	
	public void add(Point point)
	{
		points.add(point);
	}
	
	public void addAll(List<Point> listToAdd)
	{
		points.addAll(listToAdd);
	}
		
	public Point get(int index)
	{
		return points.get(index);
	}
	
	public void set(int index, Point point)
	{
		points.set(index, point);
	}
	
	public Vector<Point> getAllPoints()
	{
		return points;
	}
	
	public boolean contains(Point point)
	{
		return points.contains(point);
	}
	
	public int find(Point point)
	{
		return points.indexOf(point);
	}
	
	public void removePoint(int index)
	{
		points.remove(index);
	}
	
	public void removePoint(Point point)
	{
		if(!points.contains(point))
			throw new RuntimeException("Attempted to remove non-existant point: " + point + " from: " + toString());
		
		points.remove(point);
	}
	
	public void subtract(PointList other)
	{
		for(int i = 0; i < other.size(); ++i)
		{
			Point point = other.get(i);
			if (contains(point))
				removePoint(point);
		}
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		EnhancedJsonArray array = new EnhancedJsonArray();
		for(int i = 0; i < size(); ++i)
		{
			String pointAsString = EnhancedJsonObject.convertFromPoint(get(i));
			array.put(pointAsString);
		}
		json.put(TAG_POINTS, array);

		return json;
	}
	
	@Override
	public String toString()
	{
		if(size() == 0)
			return "";
		
		return toJson().toString();
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if(! (rawOther instanceof PointList))
			return false;
		
		PointList other = (PointList)rawOther;
		return points.equals(other.points);
	}
	
	@Override
	public int hashCode()
	{
		return points.hashCode();
	}
	
	public PointList createClone()
	{
		PointList clonedList = new PointList();
		for (int i = 0; i < size(); ++i)
		{
			Point pointToClone = get(i);
			Point clonedPoint = new Point(pointToClone);
			
			clonedList.add(clonedPoint);
		}
		return clonedList;
	}
	
	public Point getClosestPoint(Point point)
	{
		if (size() == 0)
			return new Point(0, 0);
		
		Point closestPoint = points.get(0);
		for (int i = 0; i < points.size(); ++i)
		{
			Point currentPoint = points.get(i);
			double currentDistance2Point = currentPoint.distance(point);
			double closestDistance2Point = closestPoint.distance(point);
			if (currentDistance2Point < closestDistance2Point)
				closestPoint = currentPoint;
		}
		return closestPoint;
	}
	
	public Line2D.Double createLineSegment(Point2D fromBendPoint, Point2D toBendPoint)
	{
		Point point1 = Utility.convertPoint2DToPoint(fromBendPoint);
		Point point2 = Utility.convertPoint2DToPoint(toBendPoint);
		
		return new Line2D.Double(point1, point2);
	}
	
	public Line2D.Double[] convertToLineSegments()
	{
		if (size() <= 0)
			return new Line2D.Double[0];
		
		Line2D.Double[] allLineSegments = new Line2D.Double[size() - 1];
		for (int i = 0 ; i < size() - 1; ++i)
		{
			Point fromPoint = get(i);
			Point toPoint = get(i + 1);
			allLineSegments[i] = createLineSegment(fromPoint, toPoint);
		}
		
		return allLineSegments;
	}
	
	public void translateAll(int deltaX, int deltaY)
	{
		for (int index = 0; index < points.size(); ++index)
		{
			points.get(index).translate(deltaX, deltaY);
		}
	}
	
	public Rectangle getBounds()
	{
		Rectangle pointBounds = null;
		for (int index = 0; index < points.size(); ++index)
		{
			Point point = points.get(index);
			if (pointBounds == null)
				pointBounds = new Rectangle(point.x, point.y, 0 , 0);
			
			pointBounds.add(point);
		}
		
		return pointBounds;
	}
	
	protected static final String TAG_POINTS = "Points";
	private Vector<Point> points;
}
