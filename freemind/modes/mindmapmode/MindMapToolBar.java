package freemind.modes.mindmapmode;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

public class MindMapToolBar extends FreeMindToolBar {

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

	private static final String[] sizes = { "8", "10", "12", "14", "16", "18", "20", "24", "28" };
	private static final String FONT_COMBO_BOX_DISPLAY_TEXT = "xxxxxxxxxx";
	private MindMapController controller;
	private JComboBox fonts, size;
	private JAutoScrollBarPane iconToolBarScrollPane;
	private JToolBar iconToolBar;
	private boolean fontSize_IgnoreChangeEvent = false;
	private boolean fontFamily_IgnoreChangeEvent = false;
	private boolean color_IgnoreChangeEvent = false;
	private ItemListener fontsListener;
	private ItemListener sizeListener;
	private JColorCombo colorCombo;
	private int userDefinedCounter = 1;

	protected static java.util.logging.Logger logger = null;
	
	public MindMapToolBar(MindMapController controller) {
		super();
		this.controller = controller;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(this.getClass().getName());
		}

		this.setRollover(true);

		createIconToolbar();
		createFontsComboBox();
		createFontSizeComboBox();
		createColorComboBox();
	}

	private void createIconToolbar() {
		iconToolBar = new FreeMindToolBar();
		iconToolBarScrollPane = new JAutoScrollBarPane(iconToolBar);
		iconToolBar.setOrientation(JToolBar.VERTICAL);
		iconToolBar.setRollover(true);
		iconToolBar.setLayout(new GridLayout(0, getController().getIntProperty(FreeMind.ICON_BAR_COLUMN_AMOUNT, 1)));
		iconToolBarScrollPane.getVerticalScrollBar().setUnitIncrement(100);
	}

	private void createFontSizeComboBox() {
		size = new FreeMindComboBox(sizes);
		size.setFocusable(false);

		sizeListener = e -> {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			if (fontSize_IgnoreChangeEvent) {
				return;
			}
			controller.fontSize.actionPerformed((String) e.getItem());
		};
		size.addItemListener(sizeListener);
	}

	private void createFontsComboBox() {
		fonts = new FreeMindComboBox(Tools.getAvailableFontFamilyNamesAsVector());
		fonts.setFocusable(false);

		fontsListener = e -> {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			if (fontFamily_IgnoreChangeEvent) {
				return;
			}
			fontFamily_IgnoreChangeEvent = true;
			controller.fontFamily.actionPerformed((String) e.getItem());
			fontFamily_IgnoreChangeEvent = false;
		};
		fonts.addItemListener(fontsListener);
	}

	private void createColorComboBox() {
		colorCombo = new JColorCombo();
		colorCombo.setFocusable(false);
		fonts.setPrototypeDisplayValue(FONT_COMBO_BOX_DISPLAY_TEXT);
		colorCombo.setPrototypeDisplayValue(new ColorPair(Color.BLACK, ""));
		colorCombo.addItemListener(e -> {
			if(color_IgnoreChangeEvent){
				return;
			}

			if (e.getStateChange() == ItemEvent.SELECTED) {
				color_IgnoreChangeEvent = true;
				setFontColorByItem((ColorPair) e.getItem());
				color_IgnoreChangeEvent = false;
			}
		});
	}

	private void setFontColorByItem(ColorPair pItem) {
		for (Object o : controller.getSelecteds()) {
			MindMapNode node = (MindMapNode) o;
			controller.setNodeColor(node, pItem.color);
		}
	}
	
	protected Controller getController() {
		return controller.getController();
	}
	
	public void update(StructuredMenuHolder holder) {
		this.removeAll();
		holder.updateMenus(this, "mindmapmode_toolbar/");
		
		addIcon("images/list-add-font.png");
		fonts.setMaximumRowCount(30);
		add(fonts);

		addIcon("images/format-font-size-more.png");
		add(size);
		JLabel label = addIcon("images/format-text-color.png");
		label.setToolTipText(Resources.getInstance().getText("mindmapmode_toolbar_font_color"));
		add(colorCombo);
		add(Box.createHorizontalGlue());

		// button tool bar.
		iconToolBar.removeAll();
		iconToolBar.add(controller.removeLastIconAction);
		iconToolBar.add(controller.removeAllIconsAction);
		iconToolBar.addSeparator();
		for (int i = 0; i < controller.iconActions.size(); ++i) {
			iconToolBar.add((Action) controller.iconActions.get(i));
		}
	}

	public JLabel addIcon(String iconPath) {
		add(new JToolBar.Separator());
		JLabel label = new JLabel(ImageFactory.getInstance().createIcon(iconPath));
		label.setText(" ");
		add(label);
		return label;
	}

	void selectFontSize(String fontSize)
	{
		fontSize_IgnoreChangeEvent = true;
		size.setSelectedItem(fontSize);
		fontSize_IgnoreChangeEvent = false;
	}

	Component getLeftToolBar() {
		return iconToolBarScrollPane;
	}

	void selectFontName(String fontName)
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

	void selectColor(Color pColor) {
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
		ColorPair pair = new ColorPair(pColor, "user" + userDefinedCounter, Resources.getInstance().format(
						"mindmapmode_toolbar_font_color_user_defined",
						new Object[] { userDefinedCounter }));
		userDefinedCounter++;
		colorCombo.addItem(pair);
		colorCombo.setSelectedItem(pair);
		color_IgnoreChangeEvent = false;
	}
}
