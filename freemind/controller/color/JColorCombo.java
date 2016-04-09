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
package freemind.controller.color;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;

import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapToolBar;


public class JColorCombo extends JComboBox<ColorPair> {


	public static class ColorIcon extends ImageIcon {

		private static final int ICON_SIZE = (int) (Tools.getScalingFactor()*16);
		private Color mColor;
		private BufferedImage mImage;

		public ColorIcon(Color pColor) {
			super(new BufferedImage(ICON_SIZE,ICON_SIZE,BufferedImage.TYPE_INT_RGB));
			mColor = pColor;
			mImage = (BufferedImage) getImage();
			Graphics g = mImage.getGraphics();
			g.setColor(pColor);
			g.fillRect(0, 0, ICON_SIZE, ICON_SIZE);
			g.dispose();
		}

		
		
	}

	public JColorCombo() {
		ColorPair[] colorList = sColorList;
		for (int i = 0; i < colorList.length; i++) {
			ColorPair colorPair = colorList[i];
			addItem(colorPair);
		}
		ComboBoxRenderer renderer = new ComboBoxRenderer();
		setRenderer(renderer);
		setMaximumRowCount(20);
	}
	/** See {@link MindMapToolBar} */
	public java.awt.Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
	public class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		public ComboBoxRenderer() {
			setOpaque(true);
	        setHorizontalAlignment(LEFT);
	        setVerticalAlignment(CENTER);
		}
		
		/*
	     * This method finds the image and text corresponding
	     * to the selected value and returns the label, set up
	     * to display the text and image.
	     */
	    public Component getListCellRendererComponent(
	                                       JList list,
	                                       Object value,
	                                       int index,
	                                       boolean isSelected,
	                                       boolean cellHasFocus) {
	        if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }

	        ColorPair pair = (ColorPair) value;
	        ImageIcon icon = new ColorIcon(pair.color);
	        setIcon(icon);
	        if (icon != null) {
	            setText(pair.displayName);
	        } else {
	        	System.err.println("baeh");
	        }

	        return this;
	    }
	}

	public static void main(String[] s) {
		JFrame frame = new JFrame("JColorChooser");
		JColorCombo colorChooser = new JColorCombo();

		frame.getContentPane().add(colorChooser);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static ColorPair[] sColorList = new ColorPair[] {
		// taken from http://wiki.selfhtml.org/wiki/Grafik/Farbpaletten#Farbnamen
		// default 16bit colors
		new ColorPair(new Color(0x000000), "black"),
		new ColorPair(new Color(0x808080), "gray"),
		new ColorPair(new Color(0x800000), "maroon"),
		new ColorPair(new Color(0xFF0000), "red"),

		new ColorPair(new Color(0x008000), "green"),
		new ColorPair(new Color(0x00FF00), "lime"),

		new ColorPair(new Color(0x808000), "olive"),
		new ColorPair(new Color(0xFFFF00), "yellow"),

		new ColorPair(new Color(0x000080), "navy"),
		new ColorPair(new Color(0x0000FF), "blue"),

		new ColorPair(new Color(0x800080), "purple"),
		new ColorPair(new Color(0xFF00FF), "fuchsia"),
		new ColorPair(new Color(0x008080), "teal"),
		new ColorPair(new Color(0x00FFFF), "aqua"),

		new ColorPair(new Color(0xC0C0C0), "silver"),
		new ColorPair(new Color(0xFFFFFF), "white"),

		// automatic layout colors:
		new ColorPair(new Color(0x0033ff), "level1"),
		new ColorPair(new Color(0x00b439), "level2"),
		new ColorPair(new Color(0x990000), "level3"),
		new ColorPair(new Color(0x111111), "level4"),

		
		// netscape colors
		new ColorPair(new Color(0xFFC0CB), "pink"),
		new ColorPair(new Color(0xFFB6C1), "lightpink"),
		new ColorPair(new Color(0xFF69B4), "hotpink"),
		new ColorPair(new Color(0xFF1493), "deeppink"),
		new ColorPair(new Color(0xDB7093), "palevioletred"),
		new ColorPair(new Color(0xC71585), "mediumvioletred"),
		new ColorPair(new Color(0xFFA07A), "lightsalmon"),
		new ColorPair(new Color(0xFA8072), "salmon"),
		new ColorPair(new Color(0xE9967A), "darksalmon"),
		new ColorPair(new Color(0xF08080), "lightcoral"),
		new ColorPair(new Color(0xCD5C5C), "indianred"),
		new ColorPair(new Color(0xDC143C), "crimson"),
		new ColorPair(new Color(0xB22222), "firebrick"),
		new ColorPair(new Color(0x8B0000), "darkred"),
		new ColorPair(new Color(0xFF0000), "red"),
		new ColorPair(new Color(0xFF4500), "orangered"),
		new ColorPair(new Color(0xFF6347), "tomato"),
		new ColorPair(new Color(0xFF7F50), "coral"),
		new ColorPair(new Color(0xFF8C00), "darkorange"),
		new ColorPair(new Color(0xFFA500), "orange"),
		new ColorPair(new Color(0xFFFF00), "yellow"),
		new ColorPair(new Color(0xFFFFE0), "lightyellow"),
		new ColorPair(new Color(0xFFFACD), "lemonchiffon"),
		new ColorPair(new Color(0xFFEFD5), "papayawhip"),
		new ColorPair(new Color(0xFFE4B5), "moccasin"),
		new ColorPair(new Color(0xFFDAB9), "peachpuff"),
		new ColorPair(new Color(0xEEE8AA), "palegoldenrod"),
		new ColorPair(new Color(0xF0E68C), "khaki"),
		new ColorPair(new Color(0xBDB76B), "darkkhaki"),
		new ColorPair(new Color(0xFFD700), "gold"),
		new ColorPair(new Color(0xFFF8DC), "cornsilk"),
		new ColorPair(new Color(0xFFEBCD), "blanchedalmond"),
		new ColorPair(new Color(0xFFE4C4), "bisque"),
		new ColorPair(new Color(0xFFDEAD), "navajowhite"),
		new ColorPair(new Color(0xF5DEB3), "wheat"),
		new ColorPair(new Color(0xDEB887), "burlywood"),
		new ColorPair(new Color(0xD2B48C), "tan"),
		new ColorPair(new Color(0xBC8F8F), "rosybrown"),
		new ColorPair(new Color(0xF4A460), "sandybrown"),
		new ColorPair(new Color(0xDAA520), "goldenrod"),
		new ColorPair(new Color(0xB8860B), "darkgoldenrod"),
		new ColorPair(new Color(0xCD853F), "peru"),
		new ColorPair(new Color(0xD2691E), "chocolate"),
		new ColorPair(new Color(0x8B4513), "saddlebrown"),
		new ColorPair(new Color(0xA0522D), "sienna"),
		new ColorPair(new Color(0xA52A2A), "brown"),
		new ColorPair(new Color(0x800000), "maroon"),
		new ColorPair(new Color(0x556B2F), "darkolivegreen"),
		new ColorPair(new Color(0x808000), "olive"),
		new ColorPair(new Color(0x6B8E23), "olivedrab"),
		new ColorPair(new Color(0x9ACD32), "yellowgreen"),
		new ColorPair(new Color(0x32CD32), "limegreen"),
		new ColorPair(new Color(0x00FF00), "lime"),
		new ColorPair(new Color(0x7CFC00), "lawngreen"),
		new ColorPair(new Color(0x7FFF00), "chartreuse"),
		new ColorPair(new Color(0xADFF2F), "greenyellow"),
		new ColorPair(new Color(0x00FF7F), "springgreen"),
		new ColorPair(new Color(0x00FA9A), "mediumspringgreen"),
		new ColorPair(new Color(0x90EE90), "lightgreen"),
		new ColorPair(new Color(0x98FB98), "palegreen"),
		new ColorPair(new Color(0x8FBC8F), "darkseagreen"),
		new ColorPair(new Color(0x3CB371), "mediumseagreen"),
		new ColorPair(new Color(0x2E8B57), "seagreen"),
		new ColorPair(new Color(0x228B22), "forestgreen"),
		new ColorPair(new Color(0x008000), "green"),
		new ColorPair(new Color(0x006400), "darkgreen"),
		new ColorPair(new Color(0x66CDAA), "mediumaquamarine"),
		new ColorPair(new Color(0x00FFFF), "aqua"),
		new ColorPair(new Color(0x00FFFF), "cyan"),
		new ColorPair(new Color(0xE0FFFF), "lightcyan"),
		new ColorPair(new Color(0xAFEEEE), "paleturquoise"),
		new ColorPair(new Color(0x7FFFD4), "aquamarine"),
		new ColorPair(new Color(0x40E0D0), "turquoise"),
		new ColorPair(new Color(0x48D1CC), "mediumturquoise"),
		new ColorPair(new Color(0x00CED1), "darkturquoise"),
		new ColorPair(new Color(0x20B2AA), "lightseagreen"),
		new ColorPair(new Color(0x5F9EA0), "cadetblue"),
		new ColorPair(new Color(0x008B8B), "darkcyan"),
		new ColorPair(new Color(0x008080), "teal"),
		new ColorPair(new Color(0xB0C4DE), "lightsteelblue"),
		new ColorPair(new Color(0xB0E0E6), "powderblue"),
		new ColorPair(new Color(0xADD8E6), "lightblue"),
		new ColorPair(new Color(0x87CEEB), "skyblue"),
		new ColorPair(new Color(0x87CEFA), "lightskyblue"),
		new ColorPair(new Color(0x00BFFF), "deepskyblue"),
		new ColorPair(new Color(0x1E90FF), "dodgerblue"),
		new ColorPair(new Color(0x6495ED), "cornflowerblue"),
		new ColorPair(new Color(0x4682B4), "steelblue"),
		new ColorPair(new Color(0x4169E1), "royalblue"),
		new ColorPair(new Color(0x0000FF), "blue"),
		new ColorPair(new Color(0x0000CD), "mediumblue"),
		new ColorPair(new Color(0x00008B), "darkblue"),
		new ColorPair(new Color(0x000080), "navy"),
		new ColorPair(new Color(0x191970), "midnightblue"),
		new ColorPair(new Color(0xE6E6FA), "lavender"),
		new ColorPair(new Color(0xD8BFD8), "thistle"),
		new ColorPair(new Color(0xDDA0DD), "plum"),
		new ColorPair(new Color(0xEE82EE), "violet"),
		new ColorPair(new Color(0xDA70D6), "orchid"),
		new ColorPair(new Color(0xFF00FF), "fuchsia"),
		new ColorPair(new Color(0xFF00FF), "magenta"),
		new ColorPair(new Color(0xBA55D3), "mediumorchid"),
		new ColorPair(new Color(0x9370DB), "mediumpurple"),
		new ColorPair(new Color(0x8A2BE2), "blueviolet"),
		new ColorPair(new Color(0x9400D3), "darkviolet"),
		new ColorPair(new Color(0x9932CC), "darkorchid"),
		new ColorPair(new Color(0x8B008B), "darkmagenta"),
		new ColorPair(new Color(0x800080), "purple"),
		new ColorPair(new Color(0x4B0082), "indigo"),
		new ColorPair(new Color(0x483D8B), "darkslateblue"),
		new ColorPair(new Color(0x6A5ACD), "slateblue"),
		new ColorPair(new Color(0x7B68EE), "mediumslateblue"),
		new ColorPair(new Color(0xFFFFFF), "white"),
		new ColorPair(new Color(0xFFFAFA), "snow"),
		new ColorPair(new Color(0xF0FFF0), "honeydew"),
		new ColorPair(new Color(0xF5FFFA), "mintcream"),
		new ColorPair(new Color(0xF0FFFF), "azure"),
		new ColorPair(new Color(0xF0F8FF), "aliceblue"),
		new ColorPair(new Color(0xF8F8FF), "ghostwhite"),
		new ColorPair(new Color(0xF5F5F5), "whitesmoke"),
		new ColorPair(new Color(0xFFF5EE), "seashell"),
		new ColorPair(new Color(0xF5F5DC), "beige"),
		new ColorPair(new Color(0xFDF5E6), "oldlace"),
		new ColorPair(new Color(0xFFFAF0), "floralwhite"),
		new ColorPair(new Color(0xFFFFF0), "ivory"),
		new ColorPair(new Color(0xFAEBD7), "antiquewhite"),
		new ColorPair(new Color(0xFAF0E6), "linen"),
		new ColorPair(new Color(0xFFF0F5), "lavenderblush"),
		new ColorPair(new Color(0xFFE4E1), "mistyrose"),
		new ColorPair(new Color(0xDCDCDC), "gainsboro"),
		new ColorPair(new Color(0xD3D3D3), "lightgray"),
		new ColorPair(new Color(0xC0C0C0), "silver"),
		new ColorPair(new Color(0xA9A9A9), "darkgray"),
		new ColorPair(new Color(0x808080), "gray"),
		new ColorPair(new Color(0x696969), "dimgray"),
		new ColorPair(new Color(0x778899), "lightslategray"),
		new ColorPair(new Color(0x708090), "slategray"),
		new ColorPair(new Color(0x2F4F4F), "darkslategray"),
		new ColorPair(new Color(0x000000), "black"),
	};
	
}
