/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: MindMapToolBar.java,v 1.12.18.1.12.5 2009/07/04 20:38:27 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import freemind.controller.Controller;
import freemind.controller.FreeMindToolBar;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.ZoomListener;
import freemind.controller.color.ColorPair;
import freemind.controller.color.JColorCombo;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.view.ImageFactory;
import freemind.view.mindmapview.MapView;

public class MindMapToolBar extends FreeMindToolBar implements ZoomListener {

	/**
	 * A combo box that doesn't fill the complete screen.
	 * See http://stackoverflow.com/questions/13345640/does-anyone-know-how-to-layout-a-jtoolbar-that-doest-move-or-re-size-any-compon
	 */
	private final class FreeMindComboBox extends JComboBox {
		private FreeMindComboBox(Vector pItems) {
			super(pItems);
		}

		public FreeMindComboBox(String[] pItems) {
			super(pItems);
		}

		public java.awt.Dimension getMaximumSize() {
			return getPreferredSize();
		}
	}

	private static final String[] sizes = { "8", "10", "12", "14", "16", "18",
			"20", "24", "28" };
	private MindMapController c;
	private JComboBox fonts, size;
	private JAutoScrollBarPane iconToolBarScrollPane;
	private JToolBar iconToolBar;
	private boolean fontSize_IgnoreChangeEvent = false;
	private boolean fontFamily_IgnoreChangeEvent = false;
	private boolean color_IgnoreChangeEvent = false;
	private ItemListener fontsListener;
	private ItemListener sizeListener;
	private JComboBox zoom;
	private String userDefinedZoom;
	private JColorCombo colorCombo;
	private int userDefinedCounter = 1;

	protected static java.util.logging.Logger logger = null;
	
	public MindMapToolBar(MindMapController controller) {
		super();
		this.c = controller;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		this.setRollover(true);
		fonts = new FreeMindComboBox(Tools.getAvailableFontFamilyNamesAsVector());
		fonts.setFocusable(false);
		size = new FreeMindComboBox(sizes);
		size.setFocusable(false);
		iconToolBar = new FreeMindToolBar();
		iconToolBarScrollPane = new JAutoScrollBarPane(iconToolBar);
		iconToolBar.setOrientation(JToolBar.VERTICAL);
		iconToolBar.setRollover(true);
		iconToolBar.setLayout(new GridLayout(0, getController().getIntProperty(FreeMind.ICON_BAR_COLUMN_AMOUNT, 1))); 
		iconToolBarScrollPane.getVerticalScrollBar().setUnitIncrement(100);
		fontsListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) {
					return;
				}
				// TODO: this is super-dirty, why doesn't the toolbar know the
				// model?
				if (fontFamily_IgnoreChangeEvent) {
					// fc, 27.8.2004: I don't understand, why the ignore type is
					// resetted here.
					// let's see: fontFamily_IgnoreChangeEvent = false;
					return;
				}
				fontFamily_IgnoreChangeEvent = true;
				c.fontFamily.actionPerformed((String) e.getItem());
				fontFamily_IgnoreChangeEvent = false;
			}
		};
		fonts.addItemListener(fontsListener);
		sizeListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// System.err.println("ce:"+e);
				if (e.getStateChange() != ItemEvent.SELECTED) {
					return;
				}
				// change the font size
				// TODO: this is super-dirty, why doesn't the toolbar know the
				// model?
				if (fontSize_IgnoreChangeEvent) {
					// fc, 27.8.2004: I don't understand, why the ignore type is
					// resetted here.
					// let's see: fontSize_IgnoreChangeEvent = false;
					return;
				}
				// call action:
				c.fontSize.actionPerformed((String) e.getItem());
			}
		};
		size.addItemListener(sizeListener);
		userDefinedZoom = controller.getText("user_defined_zoom");

		zoom = new FreeMindComboBox(controller.getController().getZooms());
		zoom.setSelectedItem("100%");
		zoom.addItem(userDefinedZoom);
		// Focus fix.
		zoom.setFocusable(false);
		zoom.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// todo: dialog with user zoom value, if user zoom is chosen.
				// change proposed by dimitri:
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setZoomByItem(e.getItem());
				}
			}
		});
		
		colorCombo = new JColorCombo();
		colorCombo.setFocusable(false);
		colorCombo.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(color_IgnoreChangeEvent){
					return;
				}
				if (e.getStateChange() == ItemEvent.SELECTED) {
					color_IgnoreChangeEvent = true;
					setFontColorByItem((ColorPair) e.getItem());
					color_IgnoreChangeEvent = false;
				}
			}

		});
	}

	private void setZoomByItem(Object item) {
		if (((String) item).equals(userDefinedZoom))
			return;
		String dirty = (String) item;
		String cleaned = dirty.substring(0, dirty.length() - 1);
		// change representation ("125" to 1.25)
		float zoomValue = Float.parseFloat(cleaned) / 100F; // nothing to do...
		// remove '%' sign
		getController().setZoom(zoomValue);
	}

	private void setFontColorByItem(ColorPair pItem) {
		for (Iterator it = c.getSelecteds().iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			c.setNodeColor(node, pItem.color);
		}
	}
	

	
	protected Controller getController() {
		return c.getController();
	}
	
	public void update(StructuredMenuHolder holder) {
		this.removeAll();
		holder.updateMenus(this, "mindmapmode_toolbar/");
		
		addIcon("images/list-add-font.png");
		fonts.setMaximumRowCount(30);
		add(fonts);

//		size.setEditor(new BasicComboBoxEditor());
//		size.setEditable(true);
		addIcon("images/format-font-size-more.png");
		add(size);
		JLabel label = addIcon("images/format-text-color.png");
		label.setToolTipText(Resources.getInstance().getText("mindmapmode_toolbar_font_color"));
		add(colorCombo);
		add(Box.createHorizontalGlue());
		addIcon("images/page-zoom.png");
		add(zoom);
		
		// button tool bar.
		iconToolBar.removeAll();
		iconToolBar.add(c.removeLastIconAction);
		iconToolBar.add(c.removeAllIconsAction);
		iconToolBar.addSeparator();
		for (int i = 0; i < c.iconActions.size(); ++i) {
			iconToolBar.add((Action) c.iconActions.get(i));
		}
	}

	public JLabel addIcon(String iconPath) {
		add(new JToolBar.Separator());
		JLabel label = new JLabel(ImageFactory.getInstance().createIcon(iconPath));
		label.setText(" ");
		add(label);
//		add(new JToolBar.Separator());
		return label;
	}

	// Daniel Polansky: both the following methods trigger item listeners above.
	// Those listeners obtain two events: first DESELECTED and then
	// SELECTED. Both events are to be ignored - we don't want to update
	// a node with its own font. The item listeners should react only
	// to a user change, not to our change.

	public void selectFontSize(String fontSize) // (DiPo)
	{
		fontSize_IgnoreChangeEvent = true;
		size.setSelectedItem(fontSize);
		fontSize_IgnoreChangeEvent = false;
	}

	Component getLeftToolBar() {
		return iconToolBarScrollPane;
	}

	public void selectFontName(String fontName) // (DiPo)
	{
		if (fontFamily_IgnoreChangeEvent) {
			return;
		}
		fontFamily_IgnoreChangeEvent = true;
		fonts.setEditable(true);
		fonts.setSelectedItem(fontName);
		fonts.setEditable(false);
		fontFamily_IgnoreChangeEvent = false;
	}

	void setAllActions(boolean enabled) {
		fonts.setEnabled(enabled);
		size.setEnabled(enabled);
	}

	/* (non-Javadoc)
	 * @see freemind.controller.ZoomListener#setZoom(float)
	 */
	public void setZoom(float f) {
		logger.fine("setZoomComboBox is called with " + f + ".");
		String toBeFound = getItemForZoom(f);
		for (int i = 0; i < zoom.getItemCount(); ++i) {
			if (toBeFound.equals((String) zoom.getItemAt(i))) {
				// found
				zoom.setSelectedItem(toBeFound);
				return;
			}
		}
		zoom.setSelectedItem(userDefinedZoom);
		
	}
	
	private String getItemForZoom(float f) {
		return (int) (f * 100F) + "%";
	}

	public void startup() {
		getController().registerZoomListener(this);
	}
		
	public void shutdown() {
		getController().deregisterZoomListener(this);
	}

	public void selectColor(Color pColor) {
		if(pColor == null){
			pColor = MapView.standardNodeTextColor;
		}
		color_IgnoreChangeEvent = true;
		for (int i = 0; i < colorCombo.getModel().getSize(); i++) {
			ColorPair pair = colorCombo.getModel().getElementAt(i);
			if(pair.color.equals(pColor)){
				colorCombo.setSelectedIndex(i);
				color_IgnoreChangeEvent = false;
				return;
			}
		}
		// new color. add it to the combo box:
		ColorPair pair = new ColorPair(pColor, "user" + userDefinedCounter,
				Resources.getInstance().format(
						"mindmapmode_toolbar_font_color_user_defined",
						new Object[] { userDefinedCounter }));
		userDefinedCounter++;
		colorCombo.addItem(pair);
		colorCombo.setSelectedItem(pair);
		color_IgnoreChangeEvent = false;
	}
}
