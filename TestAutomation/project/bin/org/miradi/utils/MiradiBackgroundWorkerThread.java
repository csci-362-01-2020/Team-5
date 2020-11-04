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

import org.miradi.main.EAM;

abstract public class MiradiBackgroundWorkerThread extends Thread
{
	protected MiradiBackgroundWorkerThread(ProgressInterface progressToNotify)
	{
		progress = progressToNotify;
	}
	
	public void cleanup() throws Exception
	{
		cleanupWasCalled = true;
		if(exception != null)
			throw exception;
	}

	@Override
	public void run()
	{
		try
		{
			doRealWork();
		}
		catch(Exception e)
		{
			exception = e;
		}
		finally
		{
			progress.finished();
		}
	}
	
	public ProgressInterface getProgressIndicator()
	{
		return progress;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		if(!cleanupWasCalled)
			EAM.logWarning("Cleanup never called on background worker " + getClass().getName());
		
		super.finalize();
	}

	abstract protected void doRealWork() throws Exception;
	
	private ProgressInterface progress;
	private Exception exception;
	private boolean cleanupWasCalled;
}