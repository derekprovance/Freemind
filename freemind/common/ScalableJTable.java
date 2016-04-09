/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2015 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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

package freemind.common;

import javax.swing.JTable;

import freemind.main.FreeMind;
import freemind.main.Resources;

/**
 * @author foltin
 * @date 18.06.2015
 */
public class ScalableJTable extends JTable {
	public ScalableJTable() {
		int scale = Resources.getInstance().getIntProperty(FreeMind.SCALING_FACTOR_PROPERTY, 100);
		setRowHeight(getRowHeight()*scale/100);
	}
}
