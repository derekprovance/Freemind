/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 07.10.2004
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * 
 */
public class AddArrowLinkAction extends MindmapAction {

	private final MindMapController modeController;

	/**
     */
	public AddArrowLinkAction(MindMapController modeController) {
		super("paste_as_link", "images/designer.png", modeController);
		this.modeController = modeController;
	}

	public void actionPerformed(ActionEvent e) {
		MindMapNode selected = modeController.getSelected();
		Vector<MindMapNode> nodesFromClipboard = Tools
				.getMindMapNodesFromClipboard(modeController);
		if (nodesFromClipboard.size() == 0) {
			modeController.getController().errorMessage(
					modeController.getText("no_copied_nodes"));
			return;
		}
		boolean identicalError = false;
		for (MindMapNode destination : nodesFromClipboard) {
			if(selected != destination) {
				getMindMapController().addLink(selected, destination);				
			} else {
				// give an error afterwards?
				identicalError = true;
				logger.warning("Can't create a link from the node '"+selected+"' to itself. Skipped.");
			}
		}
		if(identicalError) {
			modeController.getController().errorMessage(
					modeController
							.getText("paste_as_link_identity_not_possible"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.FreemindAction#isEnabled(javax.swing.JMenuItem,
	 * javax.swing.Action)
	 */
	@Override
	public boolean isEnabled(JMenuItem pItem, Action pAction) {
		return super.isEnabled(pItem, pAction)
				&& (modeController != null)
				&& !Tools.getMindMapNodesFromClipboard(modeController).isEmpty();
	}

}
