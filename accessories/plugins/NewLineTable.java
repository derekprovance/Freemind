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

package accessories.plugins;

import java.awt.EventQueue;

import accessories.plugins.NodeAttributeTableRegistration.AttributeHolder;
import accessories.plugins.NodeAttributeTableRegistration.AttributeTableModel;
import freemind.common.ScalableJTable;

/**
 * Taken from http://stackoverflow.com/questions/2316563/how-can-i-sort-java-jtable-with-an-empty-row-and-force-the-empty-row-always-be-l
 * @date 11.03.2015
 */
public class NewLineTable extends ScalableJTable {

    @Override
    public int getRowCount() {
        // fake an additional row
        return super.getRowCount() + 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(row < super.getRowCount()) {
            return super.getValueAt(row, column);
        }
        return ""; // value to display in new line
    }

    @Override
    public int convertRowIndexToModel(int viewRowIndex) {
        if(viewRowIndex < super.getRowCount()) {
            return super.convertRowIndexToModel(viewRowIndex);
        }
        return super.getRowCount(); // can't convert our faked row
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if(row < super.getRowCount()) {
            super.setValueAt(aValue, row, column);
        }
        else {
        	AttributeHolder attribute = new AttributeHolder();
			switch (column) {
			case NodeAttributeTableRegistration.KEY_COLUMN:
				attribute.mKey = (String) aValue;
				attribute.mValue = "";
				break;
			case NodeAttributeTableRegistration.VALUE_COLUMN:
				// this couldn't be happen, as the new-value-field is write protected.
				attribute.mKey = "";
				attribute.mValue = (String) aValue;
				break;
			}
            final int position = ((AttributeTableModel)getModel()).addAttributeHolder(attribute, true);
            // fix selection after sorting
            EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					NewLineTable table = NewLineTable.this;
					int selRow = table.convertRowIndexToView(position);
					table.getSelectionModel().setSelectionInterval(selRow, selRow);
				}});
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JTable#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int pRow, int pColumn) {
    	if(pRow < super.getRowCount()) {
    		return super.isCellEditable(pRow, pColumn);
    	}
    	return pColumn == NodeAttributeTableRegistration.KEY_COLUMN;
    }
}
