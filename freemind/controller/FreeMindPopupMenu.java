package freemind.controller;

import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JPopupMenu;
import javax.swing.event.MenuListener;

public class FreeMindPopupMenu extends JPopupMenu implements StructuredMenuHolder.MenuEventSupplier {
	private HashSet listeners = new HashSet();

	protected static java.util.logging.Logger logger = null;
	
	public FreeMindPopupMenu() {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}
	
	protected void firePopupMenuWillBecomeVisible() {
		super.firePopupMenuWillBecomeVisible();

		for (Object listener1 : listeners) {
			MenuListener listener = (MenuListener) listener1;
			listener.menuSelected(null);
		}
	}

	public void addMenuListener(MenuListener listener) {
		listeners.add(listener);
	}

	public void removeMenuListener(MenuListener listener) {
		listeners.remove(listener);
	}

	protected void firePopupMenuCanceled() {
		super.firePopupMenuCanceled();
		for (Object listener1 : listeners) {
			MenuListener listener = (MenuListener) listener1;
			listener.menuCanceled(null);
		}
	}

	protected void firePopupMenuWillBecomeInvisible() {
		super.firePopupMenuWillBecomeInvisible();
		for (Object listener1 : listeners) {
			MenuListener listener = (MenuListener) listener1;
			listener.menuDeselected(null);
		}
	}

}
