/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2007  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: JTripleCalendar.java,v 1.1.2.2 2007/02/25 21:12:50 christianfoltin Exp $*/

package accessories.plugins.time;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.LineBorder;

import tests.freemind.FreeMindMainMock;
import freemind.main.Resources;
import freemind.main.Tools;

/** 
 * Formerly, it has only three calendar widgets at once.
 * Now, it has 9 in total, but we keep the naming.
 * */
public class JTripleCalendar extends JPanel implements PropertyChangeListener {

	private static final int AMOUNT_OF_ROWS = 3;
	private static final int AMOUNT_OF_COLUMNS = 4;
	/** Contains a mapping info panel -> month distance of the panel to upper left corner.*/
	private HashMap<JSwitchableCalendar, Integer> mInfoPanels = new HashMap<JTripleCalendar.JSwitchableCalendar, Integer>();
	private int mCurrentMonthPosition;
	private JSwitchableCalendar mCurrentlyActivePanel;
	private Calendar mCurrentDate;
	private GridBagLayout mGridLayout;

	public JTripleCalendar(int pCurrentMonthPosition, Calendar pCurrentDate) {
		mCurrentMonthPosition = pCurrentMonthPosition;
		mCurrentDate = pCurrentDate;
		if(mCurrentDate == null) {
			mCurrentDate = GregorianCalendar.getInstance();
		}
		Calendar panelDate = (Calendar) mCurrentDate.clone();
		panelDate.add(Calendar.MONTH, -pCurrentMonthPosition);
		this.setName("JTripleCalendar");
		mGridLayout = new GridBagLayout();
		setLayout(mGridLayout);
		int monthIndex = 0;
		mIgnoreChangeEvent = true;
		try {
			for(int column=0; column < AMOUNT_OF_COLUMNS; ++column) {
				for(int row=0; row < AMOUNT_OF_ROWS; ++row) {
					JSwitchableCalendar infoPanel = createInfoPanel();
					infoPanel.setCalendar(panelDate);
					panelDate.add(Calendar.MONTH, 1);
					infoPanel.addPropertyChangeListener(this);
					mInfoPanels.put(infoPanel, monthIndex);
					GridBagConstraints constraints = getConstraints(row, column);
					add(infoPanel, constraints);
					if (monthIndex == pCurrentMonthPosition) {
						mCurrentlyActivePanel = infoPanel;
						markSelectedMonth(infoPanel, true);
					} else {
						markSelectedMonth(infoPanel, false);
					}
					monthIndex++;
				}
				if(column < AMOUNT_OF_COLUMNS-1) {
					GridBagConstraints constraints = new GridBagConstraints(2*column+1, 0, 1, AMOUNT_OF_ROWS, 
							0, 0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 
							new Insets(0, 0, 0, 10), 0, 0);
					JSeparator sep = new JSeparator();
					sep.setPreferredSize(new Dimension(5,2));
					sep.setOrientation(JSeparator.VERTICAL);
					add(sep, constraints);
				}
			}
			propagateDate(mCurrentDate);
		} finally {
			mIgnoreChangeEvent=false;
		}
	}

	protected GridBagConstraints getConstraints(int row, int column) {
		GridBagConstraints constraints = new GridBagConstraints(2 * column,
				row, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 50, 10);
		return constraints;
	}

	/**
	 * A calendar widget that can be switched on/off (activated/deactivated).
	 */
	public static class JSwitchableCalendar extends JCalendar {

		public void setDate(Calendar calendar) {
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			if(monthChooser.getMonth() != month) {
				monthChooser.setMonth(month);
			}
			if(yearChooser.getYear() != year) {
				yearChooser.setYear(year);
			}
			if(dayChooser.getYear() != year) {
				dayChooser.setYear(year);
			}
			if(dayChooser.getMonth() != month) {
				dayChooser.setMonth(month);
			}
			dayChooser.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		}

		
		public JSwitchableCalendar() {
			super();
			setEnabled(false);
		}
		
		/* (non-Javadoc)
		 * @see accessories.plugins.time.JCalendar#createJDayChooser(boolean)
		 */
		@Override
		protected JDayChooser createJDayChooser(boolean pWeekOfYearVisible) {
			return new JDayChooser(weekOfYearVisible){
				Color mStandardSelectedColor;
				protected void init() {
					super.init();
					mStandardSelectedColor = new Color(160, 160, 160);
					/**
					 * This is needed as sometimes the current selected date is equal to
					 * the one, the user presses. Thus, without this statement, no
					 * property change event is issued.
					 */
					setAlwaysFireDayProperty(true);
				}
				/* (non-Javadoc)
				 * @see accessories.plugins.time.JDayChooser#setEnabled(boolean)
				 */
				@Override
				public void setEnabled(boolean pEnabled) {
					if(pEnabled) {
						selectedColor = mStandardSelectedColor;
					} else {
						// no color selection
						selectedColor = oldDayBackgroundColor;
					}
					for (short i = 0; i < days.length; i++) {
						if (days[i] != null) {
							days[i].setFocusable(pEnabled);
						}
					}

					super.setEnabled(true);
				}

			};
		};
	
		/**
		 * Returns the calendar property.
		 * 
		 * @return the value of the calendar property.
		 */
		public Calendar getCalendar() {
			return dayChooser.getTemporaryCalendar();
		}

		public JDayChooser getCalendarWidget() {
			return dayChooser;
		}

	}

	private JSwitchableCalendar createInfoPanel() {
		JSwitchableCalendar panel = new JSwitchableCalendar();
		return panel;
	}

	public static void main(String[] args) {
		Resources.createInstance(new FreeMindMainMock());
		final JFrame frame = new JFrame("JTripleCalendar");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JTripleCalendar jcalendar = new JTripleCalendar(4, null);
		frame.getContentPane().add(jcalendar);
		frame.pack();
		// focus fix after startup.
		frame.addWindowFocusListener(new WindowAdapter() {

			public void windowGainedFocus(WindowEvent e) {
				frame.removeWindowFocusListener(this);
				jcalendar.getDayChooser().getSelectedDay().requestFocus();
			}
		});

		frame.setVisible(true);

	}

	private boolean mIgnoreChangeEvent = false;
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(mIgnoreChangeEvent) {
			return;
		}
		try {
			mIgnoreChangeEvent = true;
			Object source = evt.getSource();
			if (Tools.safeEquals(evt.getPropertyName(), JCalendar.CALENDAR_PROPERTY) ) {
				Calendar calendar = (Calendar) evt.getNewValue();
//				System.out.println("Property change to " + calendar.getTime());
//				Tools.printStackTrace();
				if (source == mCurrentlyActivePanel) {
					// on the calendar itself, there was probably only a different day clicked
					setCalendarInternally(calendar);
				} else {
					for(JSwitchableCalendar infoPanel : mInfoPanels.keySet()) {
						if (source == infoPanel) {
							exchangeCalendars(infoPanel, calendar);
							break;
						}
					}
				}
			}
		} finally {
			mIgnoreChangeEvent = false;
		}
	}
	public void setCalendar(Calendar gregorianCalendar) {
		try {
			mIgnoreChangeEvent = true;
			setCalendarInternally(gregorianCalendar);
		} finally {
			mIgnoreChangeEvent = false;
		}
		
	}

	protected void setCalendarInternally(Calendar gregorianCalendar) {
		// test for the same month/year as before:
//		System.out.println("Comparing new date " + gregorianCalendar.getTime() + " with active panel time " + mCurrentDate.getTime());
		int dist = mCurrentDate.get(Calendar.MONTH)- gregorianCalendar
				.get(Calendar.MONTH) +
				12*( mCurrentDate.get(Calendar.YEAR) - gregorianCalendar
						.get(Calendar.YEAR));
		if (dist==0	) {
			propagateDate(gregorianCalendar);
		} else {
			// different month/year selected. Test, if present on the screen:
			int newIndex = mCurrentMonthPosition -dist;
			if(newIndex< 0 || newIndex >= AMOUNT_OF_COLUMNS*AMOUNT_OF_ROWS) {
				// not on the screen, just adjust the rest:
				propagateDate(gregorianCalendar);
			} else {
				// move to a different position on the screen:
				for(JSwitchableCalendar infoPanel : mInfoPanels.keySet()) {
					if(mInfoPanels.get(infoPanel) == newIndex) {
						exchangeCalendars(infoPanel, gregorianCalendar);
						break;
					}
				}
			}
		}
	}

	public void exchangeCalendars(JSwitchableCalendar infoPanel,
			Calendar gregorianCalendar) {
		markSelectedMonth(mCurrentlyActivePanel, false);
		mCurrentlyActivePanel = infoPanel;
		mCurrentMonthPosition = mInfoPanels.get(infoPanel);
		markSelectedMonth(infoPanel, true);
		infoPanel.setCalendar(gregorianCalendar);
		propagateDate(gregorianCalendar);
		infoPanel.getCalendarWidget().setFocus();
	}

	public void markSelectedMonth(JSwitchableCalendar infoPanel, boolean pSelected) {
		if (pSelected) {
			infoPanel.setEnabled(true);
			infoPanel.setBorder(new LineBorder(Color.red, 4));
		} else {
			infoPanel.setEnabled(false);
			infoPanel.setBackground(null);
			infoPanel.setBorder(null);
		}
	}

	public void propagateDate(Calendar gregorianCalendar) {
		for(JSwitchableCalendar infoPanel : mInfoPanels.keySet()) {
			Integer monthDistance = mInfoPanels.get(infoPanel);
			Calendar tmpCalendar = (Calendar) gregorianCalendar.clone();
			tmpCalendar.add(Calendar.MONTH, -mCurrentMonthPosition + monthDistance);
			infoPanel.setDate(tmpCalendar);
		}
		mCurrentDate = mCurrentlyActivePanel.getCalendar();
	}

	public Calendar getCalendar() {
		return mCurrentlyActivePanel.getCalendar();
	}

	public Date getDate() {
		return mCurrentlyActivePanel.getDate();
	}

	public JDayChooser getDayChooser() {
		return mCurrentlyActivePanel.getDayChooser();
	}

	public void setDate(Date date) {
		mCurrentlyActivePanel.setDate(date);
	}

	public JYearChooser getYearChooser() {
		return mCurrentlyActivePanel.getYearChooser();
	}

	public int getCurrentMonthPosition() {
		return mCurrentMonthPosition;
	}
}

// private static java.util.logging.Logger logger =
// freemind.main.Resources.getInstance().getLogger(JTripleCalendar.class.getName());
