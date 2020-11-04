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

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import org.martus.swing.Utilities;
import org.martus.util.MultiCalendar;
import org.miradi.dialogfields.FieldSaver;
import org.miradi.dialogfields.TimeframeEditorField;
import org.miradi.dialogs.base.UndecoratedModelessDialogWithClose;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateEditorComponent extends JDateChooser
{
	public DateEditorComponent()
	{
		super(new DateTextEditor());
		
		setDateFormatString(CUSTOM_DATE_FORMAT);
		setDateChooserPreferredSizeWithPadding();
		
		setFont(getMainWindow().getUserDataPanelFont());
		
		jcalendar.getMonthChooser().addPropertyChangeListener(new MonthChangeListener());
		jcalendar.getYearChooser().addPropertyChangeListener(new YearChangeListener());
		dateEditor.addPropertyChangeListener(DATE_PROPERTY_NAME, this);
		getDateTextEditor().getDocument().addDocumentListener(new DocumentEventHandler());
		
		clearNeedsSaving();
	}
	
	public void dispose()
	{
		dateEditor.removePropertyChangeListener(DATE_PROPERTY_NAME, this);
		editorFieldChangeHandler = null;
		cleanup();
	}
	
	@Override
	public synchronized void addFocusListener(FocusListener focusListener)
	{
		getDateTextEditor().addFocusListener(focusListener);
	}
	
	@Override
	public synchronized void removeFocusListener(FocusListener focusListener)
	{
		getDateTextEditor().removeFocusListener(focusListener);
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		super.addPropertyChangeListener(listener);
	}

	public void addActionListener(TimeframeEditorField.TimeframeEditorChangeHandler editorFieldChangeHandlerToUse)
	{
		editorFieldChangeHandler = editorFieldChangeHandlerToUse;
	}

	public String getText()
	{
		return convertDateToIsoString(getDate());
	}
	
	public void setText(String text)
	{
		Date time = null;
		if (text.length() > 0)
			time = MultiCalendar.createFromIsoDateString(text).getTime();
		
		dateEditor.setDate(time);
		clearNeedsSaving();
	}
	
	private static String convertDateToIsoString(Date date)
	{
		if (date == null)
			return "";
		
		// NOTE: MultiCalendar(date) can't handle dates before 1970,
		// so go through a GregorianCalendar instead
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		MultiCalendar calendar = new MultiCalendar(cal);
		return calendar.toIsoDateString();
	}
		
	private void setDateChooserPreferredSizeWithPadding()
	{
		Dimension preferredDimension = getPreferredSize();
		//FIXME low: Why do we need EXTRA PADDING....I beleive the JDataChooser for this third party is not inlcuding the icon width in its pref
		preferredDimension.width = preferredDimension.width + EXTRA_PADDING;
		setMinimumSize(preferredDimension);
		setPreferredSize(preferredDimension);
	}
	
	public DateTextEditor getDateTextEditor()
	{
		return ((DateTextEditor) getDateEditor());
	}
	
	private void updateTextFromCalendarAndSetNeedsSave()
	{
		setDate(jcalendar.getDate());
		setNeedsSave();
	}

	private void setNeedsSave()
	{
		needsSave = true;
	}

	public void clearNeedsSaving()
	{
		needsSave = false;
	}
	
	public boolean needsToBeSaved()
	{
		return needsSave;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		possiblySavePendingEdits();
		
		calendarDialog = createCalendarDialogWithCorrectOwner();
		calendarDialog.addWindowListener(new DateFieldRequestFocusAfterDeactivatedHandler());
		calendarDialog.enableCloseWhenFocusLost();

		updateCalenderDate();
		calendarDialog.add(jcalendar);
		calendarDialog.pack();
		Utilities.centerDlg(calendarDialog);
		calendarDialog.setVisible(true);
		
		updateTextFromCalendarAndSetNeedsSave();
	}

	protected void possiblySavePendingEdits()
	{
		// TODO: miradi.utils package should not call into miradi.dialogfields
		FieldSaver.savePendingEdits();
	}

	private UndecoratedModelessDialogWithClose createCalendarDialogWithCorrectOwner()
	{
		Container rawOwner = calendarButton.getTopLevelAncestor();
		if (rawOwner.equals(getMainWindow()))
			return new UndecoratedModelessDialogWithClose(getMainWindow(), "");
		
		return new UndecoratedModelessDialogWithClose((JDialog) rawOwner, getMainWindow(), "");	
	}

	private MainWindow getMainWindow()
	{
		return EAM.getMainWindow();
	}

	private void updateCalenderDate()
	{
		Calendar calendar = Calendar.getInstance();
		Date date = getDate();
		if (date != null) 
			calendar.setTime(date);
		
		jcalendar.setCalendar(calendar);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		if (evt.getPropertyName().equals("day"))
		{
			setDate(jcalendar.getCalendar().getTime());
			calendarDialog.setVisible(false);
		}
	}

	private class MonthChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(MONTH_PROPERTY_NAME)) 
				updateTextFromCalendarAndSetNeedsSave();
		}
	}
	
	private class YearChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(YEAR_PROPERTY_NAME)) 
				updateTextFromCalendarAndSetNeedsSave();
		}
	}
	
	private class DateFieldRequestFocusAfterDeactivatedHandler extends WindowAdapter
	{
		@Override
		public void windowDeactivated(WindowEvent e)
		{
			getDateTextEditor().requestFocus();
			updateTextFromCalendarAndSetNeedsSave();
			possiblySavePendingEdits();
			if (editorFieldChangeHandler != null)
				editorFieldChangeHandler.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "DateEditorComponent"));
		}
	}
		
	private static class DateTextEditor extends JTextFieldDateEditor
	{
		public DateTextEditor()
		{
			super();
		}
		
		@Override
		public void setDate(Date newDate, boolean firePropertyChange)
		{
			super.setDate(newDate, firePropertyChange);
			setForeground(Color.blue);
		}

		@Override
		public void focusLost(FocusEvent focusEvent)
		{
			checkText();
			setForeground(Color.BLUE);
		}

		//NOTE:  focusLost does not call super, because we can not override checkText (private method)
		// this method is a duplicate of the parent class
		private void checkText() 
		{
			try 
			{
				final String YEAR_REGEXP = "\\d{4}";
				final String MONTH_REGEXP = "\\d{2}";

				String newValue = getText();
				if(newValue.matches(YEAR_REGEXP))
					newValue += "-01";
				if(newValue.matches(YEAR_REGEXP + "-" + MONTH_REGEXP))
					newValue += "-01";

				Date thisDate = dateFormatter.parse(newValue);
				setDate(thisDate, true);
			} 
			catch (Exception e) 
			{
				setDate(null, true);
			}
		}

		@Override
		public void caretUpdate(CaretEvent event)
		{
			super.caretUpdate(event);
			setForeground(Color.BLUE);
		}
		
		@Override
		public void setEnabled(boolean b)
		{
			super.setEnabled(b);
			setForeground(Color.blue);
		}
	}
	
	private class DocumentEventHandler implements DocumentListener
	{
		public void changedUpdate(DocumentEvent arg0)
		{
			setNeedsSave();
		}

		public void insertUpdate(DocumentEvent arg0)
		{
			setNeedsSave();
		}

		public void removeUpdate(DocumentEvent arg0)
		{
			setNeedsSave();
		}
	}
		
	public static final String CUSTOM_DATE_FORMAT = "yyyy-MM-dd";
	private static final int EXTRA_PADDING = 20;
	
	private static final String MONTH_PROPERTY_NAME = "month";
	private static final String YEAR_PROPERTY_NAME = "year";
	private static final String DATE_PROPERTY_NAME = "date";	
	private boolean needsSave;
	private UndecoratedModelessDialogWithClose calendarDialog;
	private TimeframeEditorField.TimeframeEditorChangeHandler editorFieldChangeHandler;
}
