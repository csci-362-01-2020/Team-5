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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogfields.FieldSaver;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.CodeToCodeMap;
import org.miradi.objects.TableSettings;
import org.miradi.project.Project;

public class ColumnWidthSaver extends MouseAdapter
{
	public ColumnWidthSaver(Project projectToUse, JTable tableToUse, ColumnWidthProvider columnWidthProviderToUse, String uniqueTableIdentifierToUse)
	{
		project = projectToUse;
		table = tableToUse;
		columnWidthProvider = columnWidthProviderToUse;
		uniqueTableIdentifier = uniqueTableIdentifierToUse;
	}
	
	public void restoreColumnWidths()
	{
		try
		{
			for (int tableColumn = 0; tableColumn < table.getColumnCount(); ++tableColumn)
			{	
				TableColumn column = table.getColumnModel().getColumn(tableColumn);
				int width = getColumnWidth(tableColumn);
				column.setWidth(width);
				column.setPreferredWidth(width);
			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	protected int getColumnWidth(int tableColumn) throws Exception
	{
		String columnKey = getColumnUniqueCode(tableColumn);
		
		String columnWidthAsString = "";
		TableSettings tableSettings = TableSettings.find(getProject(), getUniqueTableIdentifier());
		if (tableSettings != null)
		{
			CodeToCodeMap columnWidthMap = tableSettings.getColumnWidthMap();
			columnWidthAsString = columnWidthMap.getCode(columnKey);
		}
		
		return getColumnWidth(tableColumn, columnKey, columnWidthAsString);
	}

	private int getColumnWidth(int tableColumn, String columnTag, String columnWidthAsString)
	{
		int columnHeaderWidth = TableWithHelperMethods.getColumnHeaderWidth(table, tableColumn);
		int defaultColumnWidth = columnWidthProvider.getDefaultColumnWidth(tableColumn, columnTag, columnHeaderWidth);
		if (columnWidthAsString.length() == 0)
			return defaultColumnWidth;
		
		int columnWidth = Integer.parseInt(columnWidthAsString);
		if (columnWidth > 0)
			return columnWidth;
		
		return defaultColumnWidth;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		try
		{
			FieldSaver.savePendingEdits();
			saveColumnWidths();
		}
		catch(Exception e1)
		{
			EAM.logException(e1);
		}
	}
	
	public void saveColumnWidths() throws Exception
	{
		CodeToCodeMap columnWidthMap = new CodeToCodeMap();
		for (int tableColumn = 0; tableColumn < table.getColumnCount(); ++tableColumn)
		{		
			TableColumn column = table.getColumnModel().getColumn(tableColumn);
			columnWidthMap.putInteger(getColumnUniqueCode(tableColumn), column.getWidth());
		}
		
		TableSettings tableSettings = TableSettings.findOrCreate(getProject(), getUniqueTableIdentifier());
		CommandSetObjectData setColumnWidths = new CommandSetObjectData(tableSettings.getRef(), TableSettings.TAG_COLUMN_WIDTHS, columnWidthMap.toJsonString());
		getProject().executeCommand(setColumnWidths);
	}
	
	private Project getProject()
	{
		return project;
	}
	
	private String getColumnUniqueCode(int tableColumn)
	{
		return columnWidthProvider.getColumnIdentifier(tableColumn);
	}
	
	private String getUniqueTableIdentifier()
	{
		return uniqueTableIdentifier;
	}

	private Project project;
	private JTable table;
	private ColumnWidthProvider columnWidthProvider;
	private String uniqueTableIdentifier;
	public static final int DEFAULT_NARROW_COLUMN_WIDTH = 75;
	public static final int DEFAULT_WIDE_COLUMN_WIDTH = 200;
}
