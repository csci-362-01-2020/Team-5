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

import java.util.Vector;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Cause;
import org.miradi.objects.Target;
import org.miradi.objects.ThreatStressRating;
import org.miradi.project.Project;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.ThreatStressRatingSchema;

public class ThreatStressRatingHelper
{
	public ThreatStressRatingHelper(Project projectToUse)
	{
		project = projectToUse;
	}
	
	public ORefList getRelatedThreatStressRatingRefs(ORef threatRef, ORef targetRef)
	{
		Vector<ThreatStressRating> threatStressRatings = getRelatedThreatStressRatings(threatRef, targetRef);
		return new ORefList(threatStressRatings.toArray(new ThreatStressRating[0]));
	}
	
	public Vector<ThreatStressRating> getRelatedThreatStressRatings(ORef threatRef, ORef targetRef)
	{
		threatRef.ensureExactType(CauseSchema.getObjectType());
		targetRef.ensureExactType(TargetSchema.getObjectType());
		
		Target target = Target.find(getProject(), targetRef);
		ORefList stressRefs = target.getStressRefs();
		Cause threat = Cause.find(getProject(), threatRef);
		ORefList threatStressRatingReferrerRefs = threat.findObjectsThatReferToUs(ThreatStressRatingSchema.getObjectType());
		Vector<ThreatStressRating> threatStressRatings = new Vector<ThreatStressRating>();
		for (int index = 0; index < threatStressRatingReferrerRefs.size(); ++index)
		{
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), threatStressRatingReferrerRefs.get(index));
			if (stressRefs.contains(threatStressRating.getStressRef()))
				threatStressRatings.add(threatStressRating);
		}
		
		return threatStressRatings;
	}

	private Project getProject()
	{
		return project;
	}
	
	private Project project;
}
