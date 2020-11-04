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

import org.miradi.dialogs.base.AbstractObjectTableModel;
import org.miradi.dialogs.tablerenderers.SortableTableHeaderRenderer;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

import javax.swing.table.TableColumn;
import java.util.Comparator;

abstract public class SortableRowTable extends TableWithColumnWidthAndSequenceSaver
{
	public SortableRowTable(MainWindow mainWindowToUse, SortableTableModel model, String uniqueTableIdentifierToUse)
	{
		super(mainWindowToUse, model, uniqueTableIdentifierToUse, model);
		
		addAsSortableTable();
	}

	private void addAsSortableTable()
	{
		try
		{
			rowSortController = new MultiTableRowSortController(getProject());
			rowSortController.addTableToSort(this);

			SortableTableHeaderRenderer sortTableHeaderRenderer = new SortableTableHeaderRenderer(this);
			for (int columnIndex = 0; columnIndex < getColumnCount(); ++columnIndex)
			{
				final TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
				tableColumn.setHeaderRenderer(sortTableHeaderRenderer);
			}
		}
		catch(Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}
	
	public void sortTable()
	{
		try
		{
			rowSortController.sortAllTables();
		}
		catch (Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}
	
	@Override
	public void dispose()
	{
		rowSortController.dispose();
		
		super.dispose();
	}
	
	protected Comparator getComparator(int sortByTableColumn)
	{
		int sortByModelColumn = convertColumnIndexToModel(sortByTableColumn);
		return getAbstractObjectTableModel().createComparator(sortByModelColumn);
	}

	public MultiTableRowSortController getRowSortController()
	{
		return rowSortController;
	}

	private AbstractObjectTableModel getAbstractObjectTableModel()
	{
		return (AbstractObjectTableModel) getModel();
	}
	
	private MultiTableRowSortController rowSortController;
}
