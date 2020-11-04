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

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objects.TableSettings;
import org.miradi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static org.miradi.main.Miradi.isWindows;

public class TableRowHeightManager implements MouseListener, MouseMotionListener
{
	public TableRowHeightManager(Project projectToUse, int defaultRowHeightToUse)
	{
		project = projectToUse;
		defaultRowHeight = getAdjustedDefaultRowHeightForWindows(defaultRowHeightToUse);
	}
	
	public void manage(MainWindow mainWindowToUse, TableWithRowHeightManagement tableToManage, String uniqueTableIdentifierToUse)
	{
		mainWindow = mainWindowToUse;
		table = tableToManage.asTable();
		tableWithRowManagement = tableToManage;
		uniqueTableIdentifier = uniqueTableIdentifierToUse;

		if(tableWithRowManagement.allowUserToSetRowHeight())
		{
			table.addMouseListener(this);
			table.addMouseMotionListener(this);
		}

		if (tableWithRowManagement.shouldSaveRowHeight())
			restoreRowHeight();
	}
	
	public void setMultiTableRowHeightController(MultiTableRowHeightController controller)
	{
		multiTableController = controller;
	}

	public void rowHeightChanged(int newRowHeight)
	{
		if(multiTableController != null)
			multiTableController.rowHeightChanged(newRowHeight);
	}
	
	public void rowHeightChanged(int row, int newRowHeight)
	{
		if(multiTableController != null)
			multiTableController.rowHeightChanged(row, newRowHeight);
	}
	
	public void restoreRowHeight()
	{
		if(isRowHeightAutomatic())
			return;
		
		if (!tableWithRowManagement.shouldSaveRowHeight())
			return;
		
		try
		{
			TableSettings tableSettings = TableSettings.find(getProject(), getUniqueTableIdentifier());
			if (tableSettings == null)
				return;
			
			int rowHeight = tableSettings.getRowHeight();
			if(rowHeight == 0)
				rowHeight = defaultRowHeight;
				
			table.setRowHeight(rowHeight);
			EAM.logVerbose("restoreRowHeight " + getUniqueTableIdentifier() + ": " + table.getRowHeight());
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	public void saveRowHeightIgnoreExceptions(int newRowHeight)
	{
		try
		{
			saveRowHeight(newRowHeight);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}
	
	private void saveRowHeight(int newRowHeight) throws Exception
	{
		EAM.logVerbose("saveRowHeight " + getUniqueTableIdentifier() + ": " + newRowHeight);
		TableSettings tableSettings = TableSettings.findOrCreate(getProject(), getUniqueTableIdentifier());
		CommandSetObjectData setColumnWidths = new CommandSetObjectData(tableSettings.getRef(), TableSettings.TAG_ROW_HEIGHT, Integer.toString(newRowHeight));
		getProject().executeCommand(setColumnWidths);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		if(resizeInProgress)
			e.consume();
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if(isRowHeightAutomatic())
			return;
		
		if(!inRowResizeArea(e))
			return;
		
		beginResizing(e);
		e.consume();
	}

	public void mouseReleased(MouseEvent e)
	{
		if(!resizeInProgress)
			return;
		
		endResizing(e);
	}

	public void mouseDragged(MouseEvent e)
	{
		if(isRowHeightAutomatic())
			return;
		
		if(!resizeInProgress)
			return;
		
		int eventY = e.getY() + table.getY();
		sizeDeltaY = eventY - dragStartedY;
		table.setRowHeight(rowBeingResized, getNewRowHeight());
		e.consume();
	}

	public void mouseMoved(MouseEvent event)
	{
		if(isRowHeightAutomatic())
			return;
		
		if(resizeInProgress)
			return;
		
		if(inRowResizeArea(event))
			setResizeCursor();
		else
			restoreDefaultCursor();
	}

	private boolean isRowHeightAutomatic()
	{
		return mainWindow.isRowHeightModeAutomatic();
	}

	private boolean inRowResizeArea(MouseEvent event)
	{
		Point point = event.getPoint();
		
		int y = event.getY();
		int row = table.rowAtPoint(point);

		int height = table.getRowHeight();
		int rowStartY = row * height;
		int withinRowY = y - rowStartY;
		int border = ROW_RESIZE_MARGIN;
		
		boolean inBorderChangeArea = (withinRowY >= height - border);
		return inBorderChangeArea;
	}
	
	void beginResizing(MouseEvent event)
	{
		table.setEnabled(false);
		int eventY = event.getY() + table.getY();
		dragStartedY = eventY;
		originalRowHeight = table.getRowHeight();
		rowBeingResized = table.rowAtPoint(event.getPoint());
		resizeInProgress = true;
		sizeDeltaY = 0;
		setResizeCursor();
	}

	private void endResizing(MouseEvent event)
	{
		resizeInProgress = false;
		restoreDefaultCursor();
		int newHeight = getNewRowHeight();
		table.setRowHeight(newHeight);
		try
		{
			if (tableWithRowManagement.shouldSaveRowHeight())
				saveRowHeightAsTransaction(newHeight);
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}

		Point point = new Point(0, rowBeingResized * newHeight);
		Rectangle resized = new Rectangle(point, new Dimension(1, newHeight));
		table.scrollRectToVisible(resized);
		table.getSelectionModel().setSelectionInterval(rowBeingResized, rowBeingResized);
		table.getTopLevelAncestor().repaint();
		table.setEnabled(true);
	}

	private void saveRowHeightAsTransaction(int newHeight) throws Exception
	{
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			saveRowHeightIgnoreExceptions(newHeight);
			if(multiTableController != null)
				multiTableController.saveNewRowHeight(newHeight);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}

	private int getNewRowHeight()
	{
		int newRowHeight = originalRowHeight + sizeDeltaY;
		newRowHeight = Math.max(newRowHeight, 10);
		return newRowHeight;
	}

	private void setResizeCursor()
	{
		if(oldCursor != null)
			return;
		
		oldCursor = table.getCursor();
		table.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
	}
	
	private void restoreDefaultCursor()
	{
		table.setCursor(oldCursor);
		oldCursor = null;
	}

	private int getAdjustedDefaultRowHeightForWindows(int defaultRowHeightToUse)
	{
		// re BasicTableUI.installDefaults default rowHeight for Windows is incorrect
		if (isWindows() && defaultRowHeightToUse < WINDOWS_DEFAULT_ROW_HEIGHT)
			return WINDOWS_DEFAULT_ROW_HEIGHT;

		return defaultRowHeightToUse;
	}

	private Project getProject()
	{
		return project;
	}
	
	private String getUniqueTableIdentifier()
	{
		return uniqueTableIdentifier;
	}
	
    public final static int ROW_RESIZE_MARGIN = 2;
    private final static int WINDOWS_DEFAULT_ROW_HEIGHT = 19;

    private Project project;
    private int defaultRowHeight;
    private MainWindow mainWindow;
    private TableWithRowHeightManagement tableWithRowManagement;
    private JTable table;
	private String uniqueTableIdentifier;
	
	private boolean resizeInProgress;
	private int dragStartedY;
	private int rowBeingResized;
	private int originalRowHeight;
	private int sizeDeltaY;
	private Cursor oldCursor;
	
	private MultiTableRowHeightController multiTableController;
}

