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
 * @author foltin
 * 
 */
public class NodeNote extends MindMapNodeHookAdapter {

	public final static String NODE_NOTE_PLUGIN = "accessories/plugins/NodeNote.properties";

	public final static String EMPTY_EDITOR_STRING = "<html>\n  <head>\n\n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";

	public final static String EMPTY_EDITOR_STRING_ALTERNATIVE = "<html>\n  <head>\n    \n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";
	public final static String EMPTY_EDITOR_STRING_ALTERNATIVE2 = "<html>\n  <head>\n    \n    \n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";

	public void startupMapHook() {
		super.startupMapHook();
		String foldingType = getResourceString("command");
		// get registration:
		logger.info("processing command " + foldingType);
		if (foldingType.equals("jump")) {
			// jump to the notes:
			getSplitPaneToScreen();
		} else {
			NodeNoteRegistration registration = getRegistration();
			// show hidden window:
			if (!registration.getSplitPaneVisible()) {
				// the window is currently hidden. show it:
				getSplitPaneToScreen();
			} else {
				// it is shown, hide it:
				registration.hideNotesPanel();
				setShowSplitPaneProperty(false);
				getMindMapController().obtainFocusForSelected();
			}

		}
	}

	/**
	 * @return
	 */
	private NodeNoteRegistration getRegistration() {
		NodeNoteRegistration registration = (NodeNoteRegistration) this
				.getPluginBaseClass();
		return registration;
	}

	private void getSplitPaneToScreen() {
		NodeNoteRegistration registration = getRegistration();
		if (!registration.getSplitPaneVisible()) {
			// the split pane isn't visible. show it.
			registration.showNotesPanel();
			setShowSplitPaneProperty(true);
		}
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.clearGlobalFocusOwner();
		NodeNoteRegistration.getHtmlEditorPanel().getMostRecentFocusOwner()
				.requestFocus();
	}

	private void setShowSplitPaneProperty(boolean pValue) {
		getMindMapController().setProperty(FreeMind.RESOURCES_SHOW_NOTE_PANE,
				pValue ? "true" : "false");
	}
}
