/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/*
 * Created on 19.04.2004
 *
 */
package accessories.plugins;

import java.awt.datatransfer.Transferable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 */
public class SortNodes extends MindMapNodeHookAdapter {

	private final class NodeTextComparator implements Comparator {
		private boolean mNegative = false;

		public int compare(Object pArg0, Object pArg1) {
			if (pArg0 instanceof MindMapNode) {
				MindMapNode node1 = (MindMapNode) pArg0;
				if (pArg1 instanceof MindMapNode) {
					MindMapNode node2 = (MindMapNode) pArg1;
					String nodeText1 = node1.getPlainTextContent();
					String nodeText2 = node2.getPlainTextContent();
					int retValue = nodeText1.compareToIgnoreCase(nodeText2);
					if(mNegative){
						return -retValue;
					}
					return retValue;
				}
			}
			return 0;
		}
		
		public void setNegative() {
			mNegative  = true;
		}
	}

	/**
	 * 
	 */
	public SortNodes() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode,
	 * java.util.List)
	 */
	public void invoke(MindMapNode node) {
		// we want to sort the children of the node:
		Vector<MindMapNode> sortVector = new Vector<>();
		// put in all children of the node
		sortVector.addAll(node.getChildren());
		NodeTextComparator comparator = new NodeTextComparator();
		MindMapNode last = null;
		boolean isOrdered = true;
		for (MindMapNode listNode : sortVector) {
			if(last != null){
				if(comparator.compare(listNode, last)<0){
					isOrdered=false;
					break;
				}
			}
			last = listNode;
		}
		if(isOrdered){
			comparator.setNegative();
		}
		Collections.sort(sortVector, comparator);
		// now, as it is sorted. we cut the children
		for (Iterator iter = sortVector.iterator(); iter.hasNext();) {
			MindMapNode child = (MindMapNode) iter.next();
			Vector childList = Tools.getVectorWithSingleElement(child);
			Transferable cut = getMindMapController().cut(childList);
			// paste directly again causes that the node is added as the last
			// one.
			getMindMapController().paste(cut, node);
		}
		getController().select(node, Tools.getVectorWithSingleElement(node));
		obtainFocusForSelected();

	}

}
