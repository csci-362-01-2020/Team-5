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

import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import org.miradi.main.MainWindow;

public class MiradiTextPane extends JTextPane
{
	public MiradiTextPane(MainWindow mainWindowToUse, int fixedApproximateColumnCount, int initialApproximateRowCount)
	{
		mainWindow = mainWindowToUse;
		setFont(mainWindow.getUserDataPanelFont());
		int fontWidth = getFontMetrics(getFont()).stringWidth("W");
		int fontHeight = getFontMetrics(getFont()).getHeight();
		setMinimumSize(new Dimension(fixedApproximateColumnCount*fontWidth, initialApproximateRowCount*fontHeight));
		setMaximumSize(new Dimension(fixedApproximateColumnCount*fontWidth, Integer.MAX_VALUE));
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getMinimumSize();
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		Dimension preferredSize = super.getPreferredSize();

		Dimension minimumSize = getMinimumSize();
		preferredSize.width = Math.max(preferredSize.width, minimumSize.width);
		preferredSize.height = Math.max(preferredSize.height, minimumSize.height);

		Dimension maximumSize = getMaximumSize();
		preferredSize.width = Math.min(preferredSize.width, maximumSize.width);
		preferredSize.height = Math.min(preferredSize.height, maximumSize.height);

		return preferredSize;
	}
	
	/* NOTE: Crude hack to get around a weird quirk in Swing.
	 * We used to set focus traversal keys, but in some cases, 
	 * when the UiTextArea was added to a panel that was added to a panel
	 * (not sure of the exact trigger), someone else would RESET our 
	 * focus traversal keys later, so TAB would not exit the UiTextArea.
	 * Instead, we will just always return our TAB preferences.
	 */
	@Override
	public Set<AWTKeyStroke> getFocusTraversalKeys(int category)
	{
		int UNSHIFTED = 0;
		if(category == KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS)
		    return buildTabKeySet(UNSHIFTED);
		    
		if(category == KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS)
		    return buildTabKeySet(java.awt.event.InputEvent.SHIFT_MASK);

		return super.getFocusTraversalKeys(category);
	}

	private Set<AWTKeyStroke> buildTabKeySet(int shiftMask)
	{
		Set<AWTKeyStroke> set = new HashSet<AWTKeyStroke>();
		set.add(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, shiftMask));
		return set;
	}
	
	protected MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	private MainWindow mainWindow;
}