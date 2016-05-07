package freemind.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;

public class StructuredMenuHolder {

	public static final String AMOUNT_OF_VISIBLE_MENU_ITEMS = "AMOUNT_OF_VISIBLE_MENU_ITEMS";
	public static final int ICON_SIZE = 16;
	private String mOutputString;
	private static Icon blindIcon = new BlindIcon(ICON_SIZE);
	private static final String SELECTED_ICON_PATH = "images/button_ok.png";

	private static final String SEPARATOR_TEXT = "000";
	private static final String ORDER_NAME = "/order";
	private Map menuMap;
	private static java.util.logging.Logger logger = null;

	private int mIndent;
	private static ImageIcon sSelectedIcon;

	public StructuredMenuHolder() {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		menuMap = new HashMap();
		Vector order = new Vector();
		menuMap.put(ORDER_NAME, order);
		if (sSelectedIcon == null) {
			sSelectedIcon = freemind.view.ImageFactory.getInstance().createIcon(Resources.getInstance().getResource(
					SELECTED_ICON_PATH));
		}

	}

	public JMenu addMenu(JMenu item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		return (JMenu) addMenu(item, tokens);
	}

	public JMenuItem addMenuItem(JMenuItem item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		StructuredMenuItemHolder holder = new StructuredMenuItemHolder();
		holder.setAction(item.getAction());
		holder.setMenuItem(item);
		adjustTooltips(holder);
		addMenu(holder, tokens);
		return item;
	}

	/**
	 * @param item is an action. If it derives from MenuItemSelectedListener, 
	 * a check box is used.
	 */
	public JMenuItem addAction(Action item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		StructuredMenuItemHolder holder = new StructuredMenuItemHolder();
		holder.setAction(item);

		if (item instanceof MenuItemSelectedListener) {
			JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(item);
			holder.setMenuItem(checkBox);
		} else {
			holder.setMenuItem(new JMenuItem(item));
		}
		adjustTooltips(holder);
		addMenu(holder, tokens);
		return holder.getMenuItem();
	}

	/**
	 * Under Mac, no HTML is rendered for menus.
	 * 
	 * @param holder
	 */
	private void adjustTooltips(StructuredMenuItemHolder holder) {
		if (Tools.isMacOsX()) {
			String toolTipText = holder.getMenuItem().getToolTipText();
			if (toolTipText != null) {
				String toolTipTextWithoutTags = HtmlTools
						.removeHtmlTagsFromString(toolTipText);
				logger.finest("Old tool tip: " + toolTipText
						+ ", New tool tip: " + toolTipTextWithoutTags);
				holder.getMenuItem().setToolTipText(toolTipTextWithoutTags);
			}
		}
	}

	public void addCategory(String category) {
		StringTokenizer tokens = new StringTokenizer(category + "/blank", "/");
		// with this call, the category is created.
		MapTokenPair categoryPair = getCategoryMap(tokens, menuMap);
	}

	public void addSeparator(String category) {
		String sep = category;
		if (!sep.endsWith("/")) {
			sep += "/";
		}
		sep += SEPARATOR_TEXT;
		StringTokenizer tokens = new StringTokenizer(sep, "/");
		// separators can occur as doubles.
		MapTokenPair categoryPair = getCategoryMap(tokens, menuMap);
		// add an separator
		categoryPair.map.put(categoryPair.token, new SeparatorHolder());
		categoryPair.order.add(categoryPair.token);
	}

	/**
	 */
	private Object addMenu(Object item, StringTokenizer tokens) {
		MapTokenPair categoryPair = getCategoryMap(tokens, menuMap);
		// add the item:
		categoryPair.map.put(categoryPair.token, item);
		categoryPair.order.add(categoryPair.token);
		return item;
	}

	private final class PrintMenuAdder implements MenuAdder {
		public void addMenuItem(StructuredMenuItemHolder holder) {
			print("JMenuItem '" + holder.getMenuItem().getActionCommand() + "'");
		}

		public void addSeparator() {
			print("Separator '" + "'");
		}
		public void addCategory(String category) {
			print("Category: '" + category + "'");
		}
	}

	private class MapTokenPair {
		Map map;
		String token;
		Vector order;

		MapTokenPair(Map map, String token, Vector order) {
			this.map = map;
			this.token = token;
			this.order = order;
		}
	}

	private MapTokenPair getCategoryMap(StringTokenizer tokens, Map thisMap) {
		if (tokens.hasMoreTokens()) {
			String nextToken = tokens.nextToken();
			if (tokens.hasMoreTokens()) {
				if (!thisMap.containsKey(nextToken)) {
					Map newMap = new HashMap();
					Vector newOrder = new Vector();
					newMap.put(ORDER_NAME, newOrder);
					thisMap.put(nextToken, newMap);
				}

				Map nextMap = (Map) thisMap.get(nextToken);
				Vector order = (Vector) thisMap.get(ORDER_NAME);
				if (!order.contains(nextToken)) {
					order.add(nextToken);
				}
				return getCategoryMap(tokens, nextMap);
			} else {
				Vector order = (Vector) thisMap.get(ORDER_NAME);
				return new MapTokenPair(thisMap, nextToken, order);
			}
		}

		return null;
	}

	public void updateMenus(final JMenuBar myItem, String prefix) {

		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"), menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
		updateMenus(new MenuAdder() {

			public void addMenuItem(StructuredMenuItemHolder holder) {
				Tools.setLabelAndMnemonic(holder.getMenuItem(), null);
				myItem.add(holder.getMenuItem());
			}

			public void addSeparator() {
				throw new NoSuchMethodError("addSeparator for JMenuBar");
			}

			public void addCategory(String category) {
			}
		}, myMap, new DefaultMenuAdderCreator());
	}

	public void updateMenus(final JPopupMenu myItem, String prefix) {
		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"), menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
		updateMenus(new MenuAdder() {

			StructuredMenuListener listener = new StructuredMenuListener();

			public void addMenuItem(StructuredMenuItemHolder holder) {
				Tools.setLabelAndMnemonic(holder.getMenuItem(), null);
				JMenuItem menuItem = holder.getMenuItem();
				myItem.add(menuItem);
				if (myItem instanceof MenuEventSupplier) {
					MenuEventSupplier receiver = (MenuEventSupplier) myItem;
					receiver.addMenuListener(listener);
					listener.addItem(holder);
				}

			}

			public void addSeparator() {
				if (lastItemIsASeparator(myItem))
					return;
				myItem.addSeparator();
			}

			public void addCategory(String category) {
			}
		}, myMap, new DefaultMenuAdderCreator());
	}

	public void updateMenus(final JToolBar bar, String prefix) {
		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"), menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
		updateMenus(new MenuAdder() {
			public void addMenuItem(StructuredMenuItemHolder holder) {
				bar.add(holder.getAction());
			}

			public void addSeparator() {
				bar.addSeparator();
			}

			public void addCategory(String category) {
			}
		}, myMap, new DefaultMenuAdderCreator());

	}

	private interface MenuAdder {
		void addMenuItem(StructuredMenuItemHolder holder);
		void addSeparator();
		void addCategory(String category);
	}

	private static class MenuItemAdder implements MenuAdder {

		private int mAmountOfVisibleMenuItems = 20;
		private int mItemCounter = 0;
		private int mMenuCounter = 0;
		
		private JMenu mBaseMenuItem;

		private JMenu myMenuItem;

		private StructuredMenuListener listener;

		public MenuItemAdder(JMenu pMenuItem) {
			this.myMenuItem = pMenuItem;
			this.mBaseMenuItem = myMenuItem;
			mAmountOfVisibleMenuItems = Resources.getInstance().getIntProperty(AMOUNT_OF_VISIBLE_MENU_ITEMS, 20);
			listener = new StructuredMenuListener();
			pMenuItem.addMenuListener(listener);
		}

		public void addMenuItem(StructuredMenuItemHolder holder) {
			mItemCounter++;
			if(mItemCounter > mAmountOfVisibleMenuItems) {
				String label = Resources.getInstance().getResourceString("StructuredMenuHolder.next");
				if(mMenuCounter > 0) {
					label += " " + mMenuCounter;
				}
				JMenu jMenu = new JMenu(label);
				mBaseMenuItem.add(jMenu);
				myMenuItem = jMenu;
				mItemCounter = 0;
				mMenuCounter++;
			}
			Tools.setLabelAndMnemonic(holder.getMenuItem(), null);
			JMenuItem item = holder.getMenuItem();
			listener.addItem(holder);
			myMenuItem.add(item);
		}

		public void addSeparator() {
			if (lastItemIsASeparator(myMenuItem)) {
				return;
			}
			myMenuItem.addSeparator();
		}

		public void addCategory(String category) {
		}
	}

	private interface MenuAdderCreator {
		MenuAdder createAdder(JMenu baseObject);
	}

	private class DefaultMenuAdderCreator implements MenuAdderCreator {
		public MenuAdder createAdder(JMenu baseObject) {
			return new MenuItemAdder(baseObject);
		}
	}

	private class SeparatorHolder {
		SeparatorHolder() {
		}
	}

	public void updateMenus(MenuAdder menuAdder, Map thisMap, MenuAdderCreator factory) {
		Vector myVector = (Vector) thisMap.get(ORDER_NAME);
		for (Object aMyVector : myVector) {
			String category = (String) aMyVector;
			if (category.equals("."))
				continue;
			Object nextObject = thisMap.get(category);
			if (nextObject instanceof SeparatorHolder) {
				menuAdder.addSeparator();
				continue;
			}
			if (nextObject instanceof StructuredMenuItemHolder) {
				StructuredMenuItemHolder holder = (StructuredMenuItemHolder) nextObject;
				menuAdder.addMenuItem(holder);
			} else if (nextObject instanceof Map) {
				menuAdder.addCategory(category);
				Map nextMap = (Map) nextObject;
				MenuAdder nextItem;
				if (nextMap.containsKey(".")) {
					JMenu baseObject = (JMenu) nextMap.get(".");
					StructuredMenuItemHolder holder = new StructuredMenuItemHolder();
					holder.setMenuItem(baseObject);
					menuAdder.addMenuItem(holder);
					nextItem = factory.createAdder(baseObject);
				} else {
					nextItem = menuAdder;
				}
				mIndent++;
				updateMenus(nextItem, nextMap, factory);
				mIndent--;
			}
		}
	}

	public String toString() {
		mIndent = 0;
		mOutputString = "";
		updateMenus(new PrintMenuAdder(), menuMap, new PrintMenuAdderCreator());

		return mOutputString;
	}

	private class PrintMenuAdderCreator implements MenuAdderCreator {
		public MenuAdder createAdder(JMenu baseObject) {
			return new PrintMenuAdder();
		}
	}

	private void print(String string) {
		for (int i = 0; i < mIndent; ++i) {
			mOutputString += ("  ");
		}
		mOutputString += (string) + "\n";
	}

	interface MenuEventSupplier {
		void addMenuListener(MenuListener listener);
	}

	private static class StructuredMenuListener implements javax.swing.event.MenuListener {
		private Vector<StructuredMenuItemHolder> menuItemHolder = new Vector<>();

		StructuredMenuListener() {
		}

		public void menuSelected(MenuEvent arg0) {
			for (StructuredMenuItemHolder holder : menuItemHolder) {
				Action action = holder.getAction();
				boolean isEnabled = false;
				JMenuItem menuItem = holder.getMenuItem();
				if (holder.getEnabledListener() != null) {
					try {
						isEnabled = holder.getEnabledListener().isEnabled(
								menuItem, action);
					} catch (Exception e) {
						Resources.getInstance().logException(e);
					}
					action.setEnabled(isEnabled);
				}
				isEnabled = menuItem.isEnabled();
				if (isEnabled && holder.getSelectionListener() != null) {
					boolean selected = false;
					try {
						selected = holder.getSelectionListener().isSelected(
								menuItem, action);
					} catch (Exception e) {
						Resources.getInstance().logException(e);
					}
					if (menuItem instanceof JCheckBoxMenuItem) {
						JCheckBoxMenuItem checkItem = (JCheckBoxMenuItem) menuItem;
						checkItem.setSelected(selected);
					} else {
						setSelected(menuItem, selected);
					}
				}
			}
		}

		public void menuDeselected(MenuEvent arg0) {
		}

		public void menuCanceled(MenuEvent arg0) {
		}

		void addItem(StructuredMenuItemHolder holder) {
			menuItemHolder.add(holder);
		}
	}

	private static boolean lastItemIsASeparator(JMenu menu) {
		if (menu.getItemCount() > 0) {
			if (menu.getMenuComponents()[menu.getItemCount() - 1] instanceof JSeparator) {
				return true;
			}
		}
		return false;
	}

	private static boolean lastItemIsASeparator(JPopupMenu menu) {
		if (menu.getComponentCount() > 0) {
			if (menu.getComponent(menu.getComponentCount() - 1) instanceof JPopupMenu.Separator) {
				return true;
			}
		}
		return false;
	}

	private static void setSelected(JMenuItem menuItem, boolean state) {
		if (state) {
			menuItem.setIcon(sSelectedIcon);
		} else {
			Icon normalIcon = (Icon) menuItem.getAction().getValue(Action.SMALL_ICON);
			if (normalIcon == null) {
				normalIcon = blindIcon;
			}
			menuItem.setIcon(normalIcon);
		}
	}

}
