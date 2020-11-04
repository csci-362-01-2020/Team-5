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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.martus.swing.UiLabel;
import org.miradi.dialogfields.ChoiceItemListSelectionEvent;
import org.miradi.dialogfields.ControlPanelRadioButtonEditorComponent;
import org.miradi.dialogfields.FieldSaver;
import org.miradi.dialogs.fieldComponents.PanelButton;
import org.miradi.dialogs.fieldComponents.PanelTextField;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.layout.OneRowGridLayout;
import org.miradi.main.AppPreferences;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;

/**
 * TODO: This class should be reworked to not have the category prompt.
 * It should be a border layout, with selected text plus the button
 * (like TextFieldPopupEditorComponent)
 */
public class QuestionPopupEditorComponent extends PopupEditorComponent
{
	public QuestionPopupEditorComponent(JDialog parentDialog, ChoiceQuestion questionToUse) throws Exception
	{
		this(parentDialog, questionToUse, "");
	}
	
	public QuestionPopupEditorComponent(ChoiceQuestion questionToUse, String translatedPopupButtonTextToUse) throws Exception
	{
		this(null, questionToUse, translatedPopupButtonTextToUse);
	}
	
	public QuestionPopupEditorComponent(JDialog parentDialogToUse, ChoiceQuestion questionToUse, String translatedPopupButtonTextToUse) throws Exception
	{
		parentDialog = parentDialogToUse;
		question = questionToUse;
		translatedPopupButtonText = translatedPopupButtonTextToUse;
		
		setBackground(AppPreferences.getDataPanelBackgroundColor());

		OneRowGridLayout layout = new OneRowGridLayout();
		layout.setMargins(2);
		layout.setGaps(2);
		setLayout(layout);
		
		createComponents();
		addComponents();
		addListeners();
	}
	
	protected void addListeners()
	{
		PopUpEditorHandler popupEditHandler = new PopUpEditorHandler();
		HashSet<JComponent> components = getPopupEditorComponents();
		for(JComponent component : components)
		{
			component.addMouseListener(popupEditHandler);
		}
		
		popupInvokeButton.addActionListener(popupEditHandler);
	}
	
	private void addComponents()
	{
		add(staticLabel);
		add(new UiLabel(" "));
		add(currentSelectionText);
		add(popupInvokeButton);
	}

	private void createComponents()
	{
		staticLabel = new PanelTitleLabel(translatedPopupButtonText);
		currentSelectionText = new PanelTextField(10);
		currentSelectionText.setEditable(false);
		popupInvokeButton = new PopupEditorButton();
		saveAfterSelectionHandler = new SaveAfterSelectionHandler();
	}
	
	public void dispose()
	{
		if (editorPanel != null)
		{
			editorPanel.removeListSelectionListener(editorDialog);
			editorPanel.removeListSelectionListener(saveAfterSelectionHandler);
			editorPanel.dispose();
			editorPanel = null;
		}
	}
	
	@Override
	public synchronized void addFocusListener(FocusListener listener)
	{
		super.addFocusListener(listener);
		currentSelectionText.addFocusListener(listener);
	}
	
	@Override
	public synchronized void removeFocusListener(FocusListener listener)
	{	
		currentSelectionText.removeFocusListener(listener);
		super.removeFocusListener(listener);
	}

	public void setCode(String code)
	{
		currentSelectionCode = code;
		ChoiceItem choice = question.findChoiceByCode(code);
		currentSelectionText.setText(choice.getLabel());
		currentSelectionText.setBackground(choice.getColor());
	}
	
	public String getText()
	{
		return currentSelectionCode;
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
	
	protected void addAdditionalDescriptionPanel(DialogWithCloseAfterSelectionHandler editorDialogToUse)
	{
	}
	
	protected ControlPanelRadioButtonEditorComponent createPopupEditorPanel()
	{
		return new ControlPanelRadioButtonEditorComponent(getQuestion());
	}
	
	protected void invokePopupEditor()
	{
		editorPanel = createPopupEditorPanel();
		selectRating();

		editorDialog = createDialogWithProperParent();
		editorPanel.addListSelectionListener(saveAfterSelectionHandler);
		editorPanel.addListSelectionListener(editorDialog);

		addAdditionalDescriptionPanel(editorDialog);
		
		editorDialog.setMainPanel(editorPanel);
		editorDialog.pack();
		//NOTE: packing twice due to preferences height not being set correctly.
		editorDialog.pack();
		editorDialog.setLocationRelativeTo(this);
		editorDialog.setVisible(true);
	}

	private DialogWithCloseAfterSelectionHandler createDialogWithProperParent()
	{
		if (parentDialog == null)
			return new DialogWithCloseAfterSelectionHandler();
		
		return new DialogWithCloseAfterSelectionHandler(parentDialog);
	}
	
	private void selectRating()
	{
		editorPanel.setText(getText());
	}
	
	protected ChoiceQuestion getQuestion()
	{
		return question;
	}
	
	protected String getTranslatedPopupButtonText()
	{
		return translatedPopupButtonText;
	}
	
	protected HashSet<JComponent> getPopupEditorComponents()
	{
		HashSet<JComponent> components = new HashSet<JComponent>();
		components.add(this);
		components.add(staticLabel);
		components.add(currentSelectionText);
		
		return components;
	}
	
	@Override
	public void setEnabled(boolean isEnabled)
	{
		super.setEnabled(isEnabled);
		
		popupInvokeButton.setEnabled(isEnabled);
	}

	private class PopUpEditorHandler extends MouseAdapter implements ActionListener
	{
		@Override
		public void mouseReleased(MouseEvent e)
		{
			invokePopupEditor();
		}
		
		public void actionPerformed(ActionEvent event)
		{
			invokePopupEditor();	
		}
	}
	
	private class SaveAfterSelectionHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent event)
		{
			currentSelectionText.requestFocus();
			String code = ((ChoiceItemListSelectionEvent)event).getCode();
			setCode(code);
	
			setNeedsSave();
			FieldSaver.savePendingEdits();
		}
	}

	private JDialog parentDialog;
	private PanelButton popupInvokeButton;
	private PanelTextField currentSelectionText;
	private String currentSelectionCode;
	private PanelTitleLabel staticLabel; 
	private boolean needsSave;

	private DialogWithCloseAfterSelectionHandler editorDialog;
	private ChoiceQuestion question;
	private String translatedPopupButtonText;
	private ControlPanelRadioButtonEditorComponent editorPanel;
	private SaveAfterSelectionHandler saveAfterSelectionHandler;
}
