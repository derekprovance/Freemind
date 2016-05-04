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
package freemind.common;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;

/**
 * @author foltin
 * @date 19.06.2015
 * @see http://stackoverflow.com/questions/8183949/swing-scale-a-text-font-of-component 
 */
public class ScalableJButton extends JButton implements ComponentListener {
	protected static java.util.logging.Logger logger = null;
//	int mCurrentSize = 0;
//	Font mInitialFont = null;
//	int mInitialHeight;
	
	private static final long serialVersionUID = 1L;

	public ScalableJButton(String pString) {
		super(pString);
		init();
	}

	public ScalableJButton() {
		super();
		init();
	}

	private void init() {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(this.getClass().getName());
		}

//		mInitialFont = getFont();
//		addComponentListener(this);
		
	}

	@Override
	public void componentResized(ComponentEvent pE) {
//		// FIXME: This doesn't work in GridBagLayout and GroupLayout.
//		if (mInitialHeight == 0) {
//			mInitialHeight = getHeight();
//		}
//		int resizal = this.getHeight() * mInitialFont.getSize() / mInitialHeight;
//		if(resizal != mCurrentSize){
//			setFont(mInitialFont.deriveFont((float) resizal));
//			mCurrentSize = resizal;
//		}
	}

	@Override
	public void componentMoved(ComponentEvent pE) {
	}

	@Override
	public void componentShown(ComponentEvent pE) {
	}

	@Override
	public void componentHidden(ComponentEvent pE) {
	}
}
