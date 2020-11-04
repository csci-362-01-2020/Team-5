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

import org.miradi.dialogs.threatrating.upperPanel.ThreatSummaryColumnTableModel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ThreatTargetVirtualLinkHelper;
import org.miradi.objects.*;
import org.miradi.project.Project;
import org.miradi.project.threatrating.SimpleThreatRatingFramework;
import org.miradi.project.threatrating.ThreatRatingBundle;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.EmptyChoiceItem;
import org.miradi.questions.TaglessChoiceItem;
import org.miradi.schemas.StressSchema;

public class SimpleThreatRatingDetailsTableExporter extends AbstractThreatRatingDetailsTableExporter
{
	public SimpleThreatRatingDetailsTableExporter(Project projectToUse, Target targetToUse, ORefList threatRefsToUse)
	{
		super(projectToUse, targetToUse, columnTags);
		
		threatRefs = threatRefsToUse;
	}

	@Override
	public int getRowCount()
	{
		return threatRefs.size();
	}

	@Override
	public String getModelColumnName(int modelColumn)
	{
		if (isThreatNameColumn(modelColumn))
			return getThreatColumnName();
		
		if (getColumnTag(modelColumn).equals(Stress.PSEUDO_STRESS_RATING))
			return ThreatSummaryColumnTableModel.getThreatSummaryRatingLabel();
		
		if (isCommentsColumn(modelColumn))
			return getCommentsColumnName();

		return EAM.fieldLabel(StressSchema.getObjectType(), getColumnTag(modelColumn));
	}

	@Override
	public ChoiceItem getModelChoiceItemAt(int row, int modelColumn)
	{
		String columnTag = getColumnTag(modelColumn);
		Cause threatForRow = (Cause) getBaseObjectForRow(row);
		if (isThreatNameColumn(modelColumn))
			return new TaglessChoiceItem(threatForRow.getFullName());
		
		try
		{	
			SimpleThreatRatingFramework simpleThreatRatingFramework = getProject().getSimpleThreatRatingFramework();
			ThreatRatingBundle bundle = simpleThreatRatingFramework.getBundle(threatForRow.getRef(), getTargetRef());
			if (columnTag.equals(Stress.TAG_SEVERITY))
				return simpleThreatRatingFramework.getSeverityChoiceItem(bundle);
			
			if (columnTag.equals(Stress.TAG_SCOPE))
				return simpleThreatRatingFramework.getScopeChoiceItem(bundle);
			
			if (columnTag.equals(ThreatStressRating.TAG_IRREVERSIBILITY))
				return simpleThreatRatingFramework.getIrreversibilityChoiceItem(bundle);
			
			if (columnTag.equals(Stress.PSEUDO_STRESS_RATING))
			{
				String threatRatingBundleValueCode = new ThreatTargetVirtualLinkHelper(getProject()).getCalculatedThreatRatingBundleValue(threatForRow.getRef(), getTargetRef());
				return simpleThreatRatingFramework.convertToChoiceItem(threatRatingBundleValueCode);
			}

			if (isCommentsColumn(modelColumn))
				return getThreatRatingComments(threatForRow.getRef());
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}

		return new EmptyChoiceItem();
	}
	
	@Override
	public BaseObject getBaseObjectForRow(int row)
	{
		ORef threatRef = threatRefs.get(row);
		return Cause.find(getProject(), threatRef);
	}
	
	private ORefList threatRefs;
	private static final String[] columnTags = new String[]{
								Cause.TAG_LABEL,  
								Stress.TAG_SCOPE,
								Stress.TAG_SEVERITY,
								ThreatStressRating.TAG_IRREVERSIBILITY,
								Stress.PSEUDO_STRESS_RATING,
								AbstractThreatRatingData.TAG_COMMENTS,
			};
}
