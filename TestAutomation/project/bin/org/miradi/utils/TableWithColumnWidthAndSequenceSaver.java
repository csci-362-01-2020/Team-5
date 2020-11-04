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

import java.util.EventObject;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.miradi.dialogfields.FieldSaver;
import org.miradi.dialogs.treetables.MiradiTableColumn;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Goal;
import org.miradi.objects.ProgressPercent;
import org.miradi.objects.ProgressReport;
import org.miradi.objects.SubTarget;
import org.miradi.objects.TableSettings;

abstract public class TableWithColumnWidthAndSequenceSaver extends TableWithRowHeightSaver implements CommandExecutedListener, TableWithColumnManagement, ColumnWidthProvider
{
	public TableWithColumnWidthAndSequenceSaver(MainWindow mainWindowToUse, TableModel model, String uniqueTableIdentifierToUse, ModelColumnTagProvider columnTagProviderToUse)
	{
		super(mainWindowToUse, model, uniqueTableIdentifierToUse);
	
		columnTagProvider = columnTagProviderToUse;
		mainWindowToUse.getProject().addCommandExecutedListener(this);
		safelyHandleAddingRenderersAndEditors();
		addColumnWidthSaver();
		addColumnSequenceSaver();
	}

	private void safelyHandleAddingRenderersAndEditors()
	{
		try
		{
			rebuildColumnEditorsAndRenderers();
		}
		catch(Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}
	
	//FIXME medium - inspect overriding methods and pull up duplication
	protected void rebuildColumnEditorsAndRenderers() throws Exception
	{
	}

	@Override
	public void setTableHeader(JTableHeader tableHeader)
	{
		super.setTableHeader(tableHeader);
		
		tableHeader.addMouseListener(columnWidthSaver);
		tableHeader.addMouseListener(columnSequenceSaver);
	}
	
	public void dispose()
	{
		FieldSaver.setEditingTable(null);
		getProject().removeCommandExecutedListener(this);
	}
	
	@Override
	public boolean editCellAt(int row, int column, EventObject e)
	{
		FieldSaver.setEditingTable(this);		
		return super.editCellAt(row, column, e);
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		try
		{
			//NOTE: Switching on/off columns does not trigger a save column sequence.
			ORef tableSettingRef = getTableSettingsRef();
			if (event.isSetDataCommandFor(tableSettingRef))
			{
				if (event.isSetDataCommandWithThisTag(TableSettings.TAG_COLUMN_SEQUENCE_CODES))
					reloadColumnSequences();

				if (event.isSetDataCommandWithThisTag(TableSettings.TAG_COLUMN_WIDTHS))
					reloadColumnWidths();

				if (event.isSetDataCommandWithThisTag(TableSettings.TAG_ROW_HEIGHT))
					reloadRowHeights();
			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	private ORef getTableSettingsRef()
	{
		TableSettings tableSettings = TableSettings.find(getProject(), getUniqueTableIdentifier());
		if (tableSettings == null)
			return ORef.INVALID;
		
		return tableSettings.getRef();
	}
	
	private void addColumnWidthSaver()
	{
		if (! shouldSaveColumnWidth())
			return; 
		
		columnWidthSaver = new ColumnWidthSaver(getProject(), this, this, getUniqueTableIdentifier());
		getTableHeader().addMouseListener(columnWidthSaver);
		columnWidthSaver.restoreColumnWidths();
	}
	
	private void addColumnSequenceSaver()
	{
		if (! shouldSaveColumnSequence())
			return;
		
		try
		{
			columnSequenceSaver = new ColumnSequenceSaver(getProject(), this, getUniqueTableIdentifier());
			getTableHeader().addMouseListener(columnSequenceSaver);
			columnSequenceSaver.restoreColumnSequences();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			//TODO when storing column sequence is finished try throwing further up this exception
		}
	}
	
	public String getColumnGroupCode(int tableColumn)
	{
		final int modelColumn = convertColumnIndexToModel(tableColumn);
		
		return columnTagProvider.getColumnTag(modelColumn);
	}
	
	public String getColumnIdentifier(int tableColumn)
	{
		return createColumnIdentifier(this, this, columnTagProvider, tableColumn);
	}

	public static String createColumnIdentifier(JTable tableToUse, ColumnWidthProvider provider, ModelColumnTagProvider columnTagProvider, int tableColumn)
	{
		int modelColumn = tableToUse.convertColumnIndexToModel(tableColumn);
		String columnTag = columnTagProvider.getColumnTag(modelColumn);
		String columnGroupCode = provider.getColumnGroupCode(tableColumn);
		if (columnTag.equals(columnGroupCode))
			return columnTag;
		
		return columnGroupCode + "." + columnTag;
	}
	
	protected void saveColumnState() throws Exception
	{
		if(columnSequenceSaver != null)
			columnSequenceSaver.saveColumnSequence();
		if(columnWidthSaver != null)
			columnWidthSaver.saveColumnWidths();
	}

	public void updateToReflectNewColumns() throws Exception
	{
		reloadColumnSequences();
		reloadColumnWidths();
	}
	
	protected void reloadColumnSequences() throws Exception
	{
		if(columnSequenceSaver != null)
			columnSequenceSaver.restoreColumnSequences();
		
		invalidate();
	}

	protected void reloadColumnWidths()
	{
		if(columnWidthSaver != null)
			columnWidthSaver.restoreColumnWidths();
		
		invalidate();
	}
	
	public boolean shouldSaveColumnWidth()
	{
		return true;
	}
	
	public boolean shouldSaveColumnSequence()
	{
		return true;
	}
	
	public int getDefaultColumnWidth(int tableColumn, String columnTag, int columnHeaderWidth)
	{
		int defaultWidth = getColumnModel().getColumn(tableColumn).getWidth();
		return Math.max(columnHeaderWidth, defaultWidth);
	}

	@Override
	public void tableChanged(TableModelEvent e)
	{
		if (e.getFirstRow() == TableModelEvent.HEADER_ROW)
			createDefaultColumnsFromModel();
		super.tableChanged(e);
	}

	//NOTE: This method is duplicated from JTable.
	//We want to create our own custom TableColumn

	@Override
	public void createDefaultColumnsFromModel() 
	{
        TableModel model = getModel();
        if (model == null)
        	return;

        TableColumnModel columModel = getColumnModel();
        while (columModel.getColumnCount() > 0) 
        {
        	columModel.removeColumn(columModel.getColumn(0));
        }

        ModelColumnTagProvider provider = (ModelColumnTagProvider) getModel();
        for (int column = 0; column < model.getColumnCount(); column++) 
        {
        	final String columnTag = provider.getColumnTag(column);
        	TableColumn newColumn = new MiradiTableColumn(column, getColumnTagToDefaultWidthMap(), columnTag);
        	addColumn(newColumn);
        }
    }
	
	public HashMap<String, Integer> getColumnTagToDefaultWidthMap()
	{
		if (columnTagToDefaultWidthMap == null)
			columnTagToDefaultWidthMap = createDefaultColumnTagToWidthMap();
		
		return columnTagToDefaultWidthMap;
	}
	
	private HashMap<String, Integer> createDefaultColumnTagToWidthMap()
	{
		columnTagToDefaultWidthMap = new HashMap<String, Integer>();
		
		columnTagToDefaultWidthMap.put(SubTarget.TAG_SHORT_LABEL, NARROW_WIDTH);
		columnTagToDefaultWidthMap.put(SubTarget.TAG_DETAIL, ULTRA_WIDE_WIDTH);

		columnTagToDefaultWidthMap.put(ProgressReport.TAG_DETAILS, ULTRA_WIDE_WIDTH);
		columnTagToDefaultWidthMap.put(ProgressReport.TAG_PROGRESS_STATUS, WIDE_WIDTH);
		columnTagToDefaultWidthMap.put(ProgressReport.TAG_PROGRESS_DATE, NORMAL_WIDTH);
		
		columnTagToDefaultWidthMap.put(ProgressPercent.TAG_PERCENT_COMPLETE_NOTES, ULTRA_WIDE_WIDTH);
		columnTagToDefaultWidthMap.put(ProgressPercent.TAG_PERCENT_COMPLETE, NARROW_WIDTH);
		columnTagToDefaultWidthMap.put(ProgressPercent.TAG_DATE, NORMAL_WIDTH);
		
		columnTagToDefaultWidthMap.put(BaseObject.TAG_LABEL, WIDE_WIDTH);
		
		columnTagToDefaultWidthMap.put(Goal.TAG_FULL_TEXT, ULTRA_WIDE_WIDTH);
		
		return columnTagToDefaultWidthMap;
	}
		
	private static final int NARROW_WIDTH = 50;
	public static final int DEFAULT_WIDTH = 75;
	public static final int NORMAL_WIDTH = 100;
	private static final int WIDE_WIDTH = 200;
	private static final int ULTRA_WIDE_WIDTH = 400;
	private HashMap<String, Integer> columnTagToDefaultWidthMap;
	private ColumnWidthSaver columnWidthSaver;
	private ColumnSequenceSaver columnSequenceSaver;
	private ModelColumnTagProvider columnTagProvider;
}
