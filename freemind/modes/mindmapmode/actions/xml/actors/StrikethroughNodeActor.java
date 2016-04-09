/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2015 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
*/

package freemind.modes.mindmapmode.actions.xml.actors;

import freemind.controller.actions.generated.instance.StrikethroughNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 */
public class StrikethroughNodeActor extends NodeXmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public StrikethroughNodeActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void act(XmlAction action) {
		if (action instanceof StrikethroughNodeAction) {
			StrikethroughNodeAction strikethroughact = (StrikethroughNodeAction) action;
			NodeAdapter node = getNodeFromID(strikethroughact.getNode());
			if (node.isStrikethrough() != strikethroughact.getStrikethrough()) {
				node.setStrikethrough(strikethroughact.getStrikethrough());
				mMapFeedback.nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return StrikethroughNodeAction.class;
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		// every node is set to the inverse of the focussed node.
		boolean Strikethrough = getSelected().isStrikethrough();
		return getActionPair(selected, !Strikethrough);
	}

	private ActionPair getActionPair(MindMapNode selected, boolean Strikethrough) {
		StrikethroughNodeAction StrikethroughAction = toggleStrikethrough(selected, Strikethrough);
		StrikethroughNodeAction undoStrikethroughAction = toggleStrikethrough(selected, selected.isStrikethrough());
		return new ActionPair(StrikethroughAction, undoStrikethroughAction);
	}

	private StrikethroughNodeAction toggleStrikethrough(MindMapNode selected, boolean strikethrough) {
		StrikethroughNodeAction StrikethroughAction = new StrikethroughNodeAction();
		StrikethroughAction.setNode(getNodeID(selected));
		StrikethroughAction.setStrikethrough(strikethrough);
		return StrikethroughAction;
	}

	public void setStrikethrough(MindMapNode node, boolean Strikethrough) {
		execute(getActionPair(node, Strikethrough));
	}
}
