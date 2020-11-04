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

import java.awt.BorderLayout;
import java.util.Vector;

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Cause;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;

public class AbstractThreatRatingQuestionPopupEditorComponent extends QuestionPopupEditorComponent
{
	public AbstractThreatRatingQuestionPopupEditorComponent(Project projectToUse, ChoiceQuestion questionToUse) throws Exception
	{
		this(projectToUse, questionToUse, "");
	}
	
	public AbstractThreatRatingQuestionPopupEditorComponent(Project projectToUse, ChoiceQuestion questionToUse, String translatedPopupButtonText) throws Exception
	{
		super(questionToUse, translatedPopupButtonText);
		
		project = projectToUse;
		threatRef = ORef.INVALID;
		targetRef = ORef.INVALID;
	}
	
	@Override
	protected void addAdditionalDescriptionPanel(DialogWithCloseAfterSelectionHandler editorDialogToUse)
	{
		ControlPanelFlexibleWidthHtmlViewer htmlArea = new ControlPanelFlexibleWidthHtmlViewer(EAM.getMainWindow(), getThreatTargetTitle());
		editorDialogToUse.add(htmlArea, BorderLayout.BEFORE_FIRST_LINE);
	}
	
	private String getThreatTargetTitle()
	{
		Cause threat = Cause.find(getProject(), threatRef);
		Target target = Target.find(getProject(), targetRef);
		
		if (threat == null || target== null)
			return "";
		
		String threatLabel = EAM.substituteSingleString(EAM.text("Threat: %s"), threat.getFullName());
		String targetLabel = EAM.substituteSingleString(EAM.text("Target: %s"), target.getFullName());
		Vector<String> labels = new Vector<String>();
		labels.add(threatLabel);
		labels.add(targetLabel);
		String additionalLabel = getAdditionalLabel();
		if (additionalLabel.length() > 0)
			labels.add(additionalLabel);
		
		String mainLabel = "<html><B>";
		for (int index = 0; index < labels.size(); ++index)
		{
			if (index > 0)
				mainLabel += "<BR>";
			
			mainLabel += labels.get(index);
		}
		
		mainLabel += "</B></html>";
		
		return mainLabel;
	}
	
	protected String getAdditionalLabel()
	{
		return "";
	}

	private Project getProject()
	{
		return project;
	}
	
	public void setTargetRef(ORef targetRefToUse)
	{
		targetRef = targetRefToUse;
	} 
	
	public void setThreatRef(ORef threatToUse)
	{
		threatRef = threatToUse;
	}
	
	private Project project;
	private ORef threatRef;
	private ORef targetRef;
}
