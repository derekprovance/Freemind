/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import tests.freemind.FreeMindMainMock;
import freemind.main.Resources;

/**
 * Should display one or two JComponents. If two, it should use a JSplitPane
 * internally. Future: if more than two, it can use a JTabbedPane.
 * 
 * @author foltin
 * @date 26.08.2014
 */
public class JOptionalSplitPane extends JPanel {

	private HashMap<Integer, JComponent> mComponentHash = new HashMap<Integer, JComponent>();
	private JComponent mBasicComponent = null;
	private int mLastDividerPosition = -1; 
	
	
	public JOptionalSplitPane() {
		setLayout(new BorderLayout());
	}
	
	public int getAmountOfComponents() {
		return mComponentHash.size();
	}

	public void setComponent(JComponent pComponent, int index) {
		checkIndex(index);
		JComponent formerComponent = null;
		if(mComponentHash.containsKey(index)) {
			formerComponent = mComponentHash.get(index);
			if (pComponent == formerComponent) {
				// Already present.
				return;
			}
		}
		mComponentHash.put(index, pComponent);
		// correct basic component?
		switch(mComponentHash.size()) {
		case 1:
				if(!(mBasicComponent instanceof JPanel)) {
					setSingleJPanel(pComponent);
				} else {
					// remove former:
					mBasicComponent.remove(formerComponent);
					mBasicComponent.add(pComponent, BorderLayout.CENTER);
					revalidate();
				}
				break;
		case 2:
				if(!(mBasicComponent instanceof JSplitPane)) {
					remove(mBasicComponent);
					// TODO: Make configurable.
					JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
					mBasicComponent = splitPane;
					splitPane.setLeftComponent(mComponentHash.get(0));
					splitPane.setRightComponent(mComponentHash.get(1));
					if (getLastDividerPosition() >= 0) {
						// Restore divider location
						splitPane.setDividerLocation(getLastDividerPosition());
					}
					add(mBasicComponent, BorderLayout.CENTER);
					revalidate();
				} else {
					// some component has changed:
					JSplitPane splitPane = (JSplitPane) mBasicComponent;
					setLastDividerPosition(splitPane.getDividerLocation());
					splitPane.remove(formerComponent);
					splitPane.setLeftComponent(mComponentHash.get(0));
					splitPane.setRightComponent(mComponentHash.get(1));
					splitPane.setDividerLocation(getLastDividerPosition());
					revalidate();
				}
				break;
		default:
				throw new IllegalArgumentException("Too many indices: " + mComponentHash.size());
		}
	}

	/**
	 * @param pComponent
	 */
	private void setSingleJPanel(JComponent pComponent) {
		if (mBasicComponent != null) {
			if(mBasicComponent instanceof JSplitPane) {
				setLastDividerPosition(((JSplitPane) mBasicComponent).getDividerLocation());
			}
			remove(mBasicComponent);
		}
		mBasicComponent = new JPanel();
		mBasicComponent.setLayout(new BorderLayout());
		mBasicComponent.add(pComponent, BorderLayout.CENTER);
		add(mBasicComponent, BorderLayout.CENTER);
		revalidate();
	}
	
	public void removeComponent(int index) {
		checkIndex(index);
		if(!mComponentHash.containsKey(index)) {
			return;
		}
//		JComponent formerComponent = mComponentHash.get(index);
		mComponentHash.remove(index);
		switch(mComponentHash.size()) {
		case 0:
			if (mBasicComponent != null) {
				remove(mBasicComponent);
			}
			mBasicComponent = new JLabel();
			add(mBasicComponent);
			revalidate();
			break;
		case 1:
			// TODO: Too complicated:
			setSingleJPanel(mComponentHash.values().iterator().next());
			break;
		default:
			throw new IllegalArgumentException("Wrong indices: " + mComponentHash.size());
		}
	}

	/**
	 * @param index
	 */
	private void checkIndex(int index) {
		if(index < 0 || index > 1) {
			throw new IllegalArgumentException("Wrong index: " + index);
		}
	}


	public static void main(String[] args) {
		Resources.createInstance(new FreeMindMainMock());
		final JFrame frame = new JFrame("JOptionalSplitPane");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JOptionalSplitPane panel = new JOptionalSplitPane();
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new GridLayout(5, 1));
		frame.pack();
		frame.setSize(800, 400);
//		// focus fix after startup.
//		frame.addWindowFocusListener(new WindowAdapter() {
//
//			public void windowGainedFocus(WindowEvent e) {
//				frame.removeWindowFocusListener(this);
//				jcalendar.getDayChooser().getSelectedDay().requestFocus();
//			}
//		});
		contentPane.add(new JButton(new AbstractAction("Add 0") {
			private int index = 0;
			@Override
			public void actionPerformed(ActionEvent pE) {
				panel.setComponent(new JLabel("links " + index++),0);
				
			}}));
		contentPane.add(new JButton(new AbstractAction("Add 1") {
			private int index = 0;
			
			@Override
			public void actionPerformed(ActionEvent pE) {
				panel.setComponent(new JLabel("rechts " + index++),1);
				
			}}));
		contentPane.add(new JButton(new AbstractAction("Remove 0") {
			
			@Override
			public void actionPerformed(ActionEvent pE) {
				panel.removeComponent(0);
				
			}}));
		contentPane.add(new JButton(new AbstractAction("Remove 1") {
			
			@Override
			public void actionPerformed(ActionEvent pE) {
				panel.removeComponent(1);
				
			}}));
		contentPane.add(panel);
		frame.setVisible(true);

	}

	/**
	 * @return the lastDividerPosition
	 */
	public int getLastDividerPosition() {
		return mLastDividerPosition;
	}

	/**
	 * @return the dividerPosition or last location is currently no split pane is visible
	 */
	public int getDividerPosition() {
		if (mBasicComponent instanceof JSplitPane) {
			JSplitPane pane = (JSplitPane) mBasicComponent;
			return pane.getDividerLocation();
		}
		return mLastDividerPosition;
	}
	
	/**
	 * @param pLastDividerPosition the lastDividerPosition to set
	 */
	public void setLastDividerPosition(int pLastDividerPosition) {
		mLastDividerPosition = pLastDividerPosition;
	}

}
