/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Christian Foltin and others
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package accessories.plugins;

import java.awt.KeyboardFocusManager;

import freemind.main.FreeMind;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * Shows or hides the attribute table belonging to each node.
 * 
 * @author foltin
 * 
 */
public class NodeAttributeTable extends MindMapNodeHookAdapter {

	
	
	
	public void startupMapHook() {
		super.startupMapHook();
		String foldingType = getResourceString("command");
		// get registration:
		logger.info("processing command " + foldingType);
		if (foldingType.equals("jump")) {
			// jump to the notes:
			getSplitPaneToScreen();
		} else {
			NodeAttributeTableRegistration registration = getRegistration();
			// show hidden window:
			if (!registration.getSplitPaneVisible()) {
				// the window is currently hidden. show it:
				getSplitPaneToScreen();
			} else {
				// it is shown, hide it:
				registration.hideAttributeTablePanel();
				setShowSplitPaneProperty(false);
				getMindMapController().obtainFocusForSelected();
			}
		}
	}

	/**
	 * @return
	 */
	private NodeAttributeTableRegistration getRegistration() {
		NodeAttributeTableRegistration registration = (NodeAttributeTableRegistration) this
				.getPluginBaseClass();
		return registration;
	}

	private void getSplitPaneToScreen() {
		NodeAttributeTableRegistration registration = getRegistration();
		if (!registration.getSplitPaneVisible()) {
			// the split pane isn't visible. show it.
			registration.showAttributeTablePanel();
			setShowSplitPaneProperty(true);
		}
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.clearGlobalFocusOwner();
		// focus table.
		registration.focusAttributeTable();
	}

	private void setShowSplitPaneProperty(boolean pValue) {
		getMindMapController().setProperty(FreeMind.RESOURCES_SHOW_ATTRIBUTE_PANE,
				pValue ? "true" : "false");
	}
}
