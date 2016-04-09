/*
 * FreeMind - A Program for creating and viewing MindmapsCopyright (C) 2000-2015
 * Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and
 * others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/ormodify it under
 * the terms of the GNU General Public Licenseas published by the Free Software
 * Foundation; either version 2of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See theGNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public Licensealong with
 * this program; if not, write to the Free SoftwareFoundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package freemind.modes.mindmapmode.actions;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemSelectedListener;
import freemind.controller.actions.generated.instance.StrikethroughNodeAction;
import freemind.modes.mindmapmode.MindMapController;

public class StrikethroughAction extends NodeGeneralAction implements
		MenuItemSelectedListener {
	/**
	 */
	public StrikethroughAction(MindMapController modeController) {
		super(modeController, "Strikethrough", "images/format-text-strikethrough.png");
		setDoActionClass(StrikethroughNodeAction.class);
	}
	public boolean isSelected(JMenuItem item, Action action) {
		return modeController.getSelected().isStrikethrough();
	}

}
