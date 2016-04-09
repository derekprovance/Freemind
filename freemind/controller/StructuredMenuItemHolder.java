package freemind.controller;

import javax.swing.Action;
import javax.swing.JMenuItem;

class StructuredMenuItemHolder {
	private JMenuItem menuItem;
	private Action action;
	private MenuItemEnabledListener enabledListener;
	private MenuItemSelectedListener selectionListener;

	StructuredMenuItemHolder() {
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
		if (action instanceof MenuItemEnabledListener) {
			MenuItemEnabledListener listener = (MenuItemEnabledListener) action;
			setEnabledListener(listener);
		}
		if (action instanceof MenuItemSelectedListener) {
			MenuItemSelectedListener listener = (MenuItemSelectedListener) action;
			setSelectedListener(listener);
		}
	}

	MenuItemEnabledListener getEnabledListener() {
		return enabledListener;
	}

	private void setEnabledListener(MenuItemEnabledListener enabledListener) {
		this.enabledListener = enabledListener;
	}

	JMenuItem getMenuItem() {
		return menuItem;
	}

	void setMenuItem(JMenuItem menuItem) {
		this.menuItem = menuItem;
	}

	MenuItemSelectedListener getSelectionListener() {
		return selectionListener;
	}

	private void setSelectedListener(MenuItemSelectedListener selectionListener) {
		this.selectionListener = selectionListener;
	}
}