package freemind.modes.filemode;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

class FilePopupMenu extends JPopupMenu {

	private FileController c;

	protected void add(Action action, String keystroke) {
		JMenuItem item = add(action);
		item.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getAdjustableProperty(keystroke)));
	}

	FilePopupMenu(FileController c) {
		this.c = c;

		// Node menu
		this.add(c.center);
		this.addSeparator();
		this.add(c.find, "keystroke_find");
		this.add(c.findNext, "keystroke_find_next");
	}
}
