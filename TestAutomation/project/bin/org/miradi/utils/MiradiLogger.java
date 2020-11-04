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

import java.io.PrintStream;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.miradi.main.EAM;
import org.miradi.main.VersionConstants;

public class MiradiLogger
{
	public MiradiLogger()
	{
		log = Logger.getLogger("org.miradi");
		log.setUseParentHandlers(false);
		consoleLogger = new ConsoleHandler();
		setLogLevel(LOG_NORMAL);
		setLogToConsole();
	}
	
	public void setExceptionLoggingDestination(PrintStream destination)
	{
		exceptionDestination = destination;
	}
	
	public PrintStream getExceptionLoggingDestination()
	{
		return exceptionDestination;
	}
	
	public void setLogToString()
	{
		removeAllHandlers();
		log.addHandler(new StringHandler());
	}
	
	public void setLogToConsole()
	{
		removeAllHandlers();
		log.addHandler(consoleLogger);
	}
	
	public String getLoggedString()
	{
		StringHandler handler = (StringHandler)log.getHandlers()[0];
		return handler.getLoggedString();
	}
	
	class StringHandler extends Handler
	{
		public StringHandler()
		{
			buffer = new StringBuffer();
		}
		
		@Override
		public void close() throws SecurityException
		{
		}

		@Override
		public void flush()
		{
		}

		@Override
		public void publish(LogRecord record)
		{
			buffer.append(record.getLevel().getName());
			buffer.append(": ");
			buffer.append(record.getMessage());
			buffer.append(EAM.NEWLINE);
		}
		
		public String getLoggedString()
		{
			return buffer.toString();
		}
		
		StringBuffer buffer;
	}
	
	public void setLogLevel(int logLevel)
	{
		Level javaLogLevel = levels[logLevel];
		log.setLevel(javaLogLevel);
		consoleLogger.setLevel(javaLogLevel);
	}
	
	public void logException(Exception e)
	{
		log.log(Level.SEVERE, "Exception", e);
		if(exceptionDestination != null)
		{
			logVersions();
			e.printStackTrace(exceptionDestination);
			exceptionDestination.flush();
		}
	}

	private void logVersions()
	{
		try
		{
			exceptionDestination.print(createLogHeader());
		}
		catch(Exception e)
		{
			e.printStackTrace(exceptionDestination);
		}
	}

	public static String createLogHeader() throws Exception
	{
		StringBuffer stringBuffer = new StringBuffer();
		
		Date now = new Date();
		stringBuffer.append("Time Stamp = " + now + "\n");
		stringBuffer.append("Miradi Version = " + VersionConstants.getVersion() + "\n");
		stringBuffer.append("Build = " + VersionConstants.getTimestamp() + "\n");
		stringBuffer.append("Java Version = " + EAM.getJavaVersion() + "\n");
		stringBuffer.append("OS Name = " + System.getProperty("os.name") + "\n");
		stringBuffer.append("OS Version = " + System.getProperty("os.version") + "\n");
		stringBuffer.append("OS Architecture = " + System.getProperty("os.arch") + "\n");
		
		return stringBuffer.toString();
	}

	public void logError(String text)
	{
		log(Level.SEVERE, text);
	}
	
	public void logWarning(String text)
	{
		log(Level.WARNING, text);
	}
	
	public void logDebug(String text)
	{
		log(Level.INFO, text);
	}
	
	public void logVerbose(String text)
	{
		log(Level.FINE, text);
	}
	
	private void log(Level level, String text)
	{
		log.log(level, text);
	}
	
	private void removeAllHandlers()
	{
		Handler[] handlers = log.getHandlers();
		for(int i = 0; i < handlers.length; ++i)
			log.removeHandler(handlers[i]);
	}
	
	private Logger log;
	private Handler consoleLogger;
	private PrintStream exceptionDestination;
	
	private static final Level[] levels = {Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE, };
	public static final int LOG_QUIET = 0;
	public static final int LOG_NORMAL = 1;
	public static final int LOG_DEBUG = 2;
	public static final int LOG_VERBOSE = 3;

}
