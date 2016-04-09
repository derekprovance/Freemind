/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 04.02.2005
 */

package accessories.plugins.time;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import freemind.common.XmlBindingTools;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.CalendarMarking;
import freemind.controller.actions.generated.instance.CalendarMarkings;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.main.FreeMindCommon;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.common.plugins.ReminderHookBase;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import freemind.view.MapModule;

//FIXME: Reminder: more than once. (later)

/**
 * @author foltin
 * 
 */
public class TimeManagement extends MindMapHookAdapter implements
		PropertyChangeListener, ActionListener, MapModuleChangeObserver {

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = TimeManagement.class
			.getName() + "_properties";

	
	private interface NodeFactory {
		MindMapNode getNode(MindMapNode pNode);
	}

	private class AppendDateAbstractAction extends AbstractAction {
		private NodeFactory mFactory;

		public AppendDateAbstractAction() {

		}

		public void init(NodeFactory pFactory, String pText) {
			putValue(Action.NAME, getMindMapController().getText(pText));
			mFactory = pFactory;
		}

		public void actionPerformed(ActionEvent actionEvent) {
			MindMapNode lastElement = null;
			Vector sel = new Vector();
			for (Iterator i = getMindMapController().getSelecteds().iterator(); i
					.hasNext();) {
				MindMapNode element = mFactory.getNode((MindMapNode) i.next());
				DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
				String dateAsString = df.format(getCalendarDate());
				getMindMapController().setNodeText(element,
						element.getText() + " " + dateAsString);
				lastElement = element;
				sel.add(element);
			}
			getMindMapController().select(lastElement, sel);
			requestFocusForDay();
		}

	}

	private class AppendDateAction extends AppendDateAbstractAction {
		public AppendDateAction() {
			init(new NodeFactory() {

				public MindMapNode getNode(MindMapNode pNode) {
					return pNode;
				}
			}, "plugins/TimeManagement.xml_appendButton");
		}

	}

	private class AppendDateToChildAction extends AppendDateAbstractAction {
		public AppendDateToChildAction() {
			init(new NodeFactory() {

				public MindMapNode getNode(MindMapNode pNode) {
					return getMindMapController().addNewNode(pNode,
							pNode.getChildCount(), pNode.isLeft());
				}
			}, "plugins/TimeManagement.xml_appendAsNewButton");
		}
	}

	private class AppendDateToSiblingAction extends AppendDateAbstractAction {
		public AppendDateToSiblingAction() {
			init(new NodeFactory() {

				public MindMapNode getNode(MindMapNode pNode) {
					MindMapNode parent = pNode;
					if (!pNode.isRoot()) {
						parent = pNode.getParentNode();
					}
					return getMindMapController().addNewNode(parent,
							parent.getIndex(pNode) + 1, parent.isLeft());
				}
			}, "plugins/TimeManagement.xml_appendAsNewSiblingButton");
		}
	}

	private class RemindAction extends AbstractAction {
		public RemindAction() {
			super(getMindMapController().getText(
					"plugins/TimeManagement.xml_reminderButton"));
		}

		public void actionPerformed(ActionEvent pE) {
			TimeManagement.this.actionPerformed(pE);
		}
	}

	private final class RemoveReminders extends AbstractAction {
		public RemoveReminders() {
			super(getMindMapController().getText(
					"plugins/TimeManagement.xml_removeReminderButton"));
		}

		public void actionPerformed(ActionEvent e) {
			for (Iterator i = getMindMapController().getSelecteds().iterator(); i
					.hasNext();) {
				MindMapNode node = (MindMapNode) i.next();

				ReminderHookBase alreadyPresentHook = TimeManagementOrganizer
						.getHook(node);
				if (alreadyPresentHook != null) {
					addHook(node, 0L); // means remove hook, as it is already
					// present.
				}
			}
		}
	}

	private class TodayAction extends AbstractAction {
		public TodayAction() {
			super(getMindMapController().getText(
					"plugins/TimeManagement.xml_todayButton"));
		}

		public void actionPerformed(ActionEvent arg0) {
			calendar.setCalendar(Calendar.getInstance());
		}
	}

	private class CloseAction extends AbstractAction {
		public CloseAction() {
			super(getMindMapController().getText(
					"plugins/TimeManagement.xml_closeButton"));
		}

		public void actionPerformed(ActionEvent arg0) {
			disposeDialog();
		}
	}

	private class AppendDailyMarkAction extends AbstractAction {

		private ModeController mModeController;

		public AppendDailyMarkAction() {
			putValue(Action.NAME, getMindMapController().getText("plugins/TimeManagement.xml_appendDailyButton"));
			mModeController = getController();

		}

		public void actionPerformed(ActionEvent actionEvent) {
			Date cal = getCalendarDate();
			Resources res = Resources.getInstance();
			String xml = res.getProperty(FreeMindCommon.TIME_MANAGEMENT_MARKING_XML);
			XmlBindingTools bind = XmlBindingTools.getInstance();
			CalendarMarkings result = (CalendarMarkings) bind.unMarshall(xml);
			CalendarMarking mark = new CalendarMarking();
			mark.setStartDate(cal.getTime());
			mark.setName(DateFormat.getDateInstance().format(cal));
			mark.setColor(Tools.colorToXml(Color.red));
			mark.setRepeatEachNOccurence(1);
			mark.setRepeatType(CalendarMarking.DAILY);
			result.addCalendarMarking(mark);
			mModeController.getController().setProperty(FreeMindCommon.TIME_MANAGEMENT_MARKING_XML, bind.marshall(result));
			calendar.repaint();
		}

	}

	private class AddMarkAction extends AbstractAction {
		
		public AddMarkAction() {
			putValue(Action.NAME, getMindMapController().getText("plugins/TimeManagement.xml_addMarkingsButton"));
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			Calendar cal = getCalendar();
			Resources res = Resources.getInstance();
			String xml = res.getProperty(FreeMindCommon.TIME_MANAGEMENT_MARKING_XML);
			XmlBindingTools bind = XmlBindingTools.getInstance();
			CalendarMarkings result = (CalendarMarkings) bind.unMarshall(xml);
			CalendarMarkingDialog dialog = new CalendarMarkingDialog(getMindMapController());
			dialog.setDates(cal);
			dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
			dialog.setVisible(true);
			if(dialog.getResult() == CalendarMarkingDialog.OK){
				result.addCalendarMarking(dialog.getCalendarMarking());
				getMindMapController().setProperty(FreeMindCommon.TIME_MANAGEMENT_MARKING_XML, bind.marshall(result));
				calendar.repaint();
			}
		}
		
	}
	private class RemoveMarkAction extends AbstractAction {
		
		public RemoveMarkAction() {
			putValue(Action.NAME, getMindMapController().getText("plugins/TimeManagement.removeMarkingsButton"));
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			Calendar cal = getCalendar();
			Resources res = Resources.getInstance();
			String xml = res.getProperty(FreeMindCommon.TIME_MANAGEMENT_MARKING_XML);
			XmlBindingTools bind = XmlBindingTools.getInstance();
			CalendarMarkings result = (CalendarMarkings) bind.unMarshall(xml);
			CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
			CalendarMarking marking = ev.isMarked(cal);
			if(marking != null){
				for (int i = 0; i < result.sizeCalendarMarkingList(); i++) {
					CalendarMarking mark = result.getCalendarMarking(i);
					if(mark == marking){
						result.removeFromCalendarMarkingElementAt(i);
						break;
					}
				}
			}
			getMindMapController().setProperty(FreeMindCommon.TIME_MANAGEMENT_MARKING_XML, bind.marshall(result));
			calendar.repaint();
		}
		
	}
	
	public final static String REMINDER_HOOK_NAME = "plugins/TimeManagementReminder.xml";

	private static Calendar lastDate = null;

	private static int lastActivePosition = 4;

	private JTripleCalendar calendar;

	private JDialog mDialog;

	private JPanel timePanel;

	private JTextField hourField;

	private JTextField minuteField;

	private MindMapController mController;

	private static TimeManagement sCurrentlyOpenTimeManagement = null;

	public void startupMapHook() {
		super.startupMapHook();
		if (sCurrentlyOpenTimeManagement != null) {
			sCurrentlyOpenTimeManagement.mDialog.getContentPane().setVisible(
					true);
			return;
		}
		sCurrentlyOpenTimeManagement = this;
		this.mController = super.getMindMapController();
		getMindMapController().getController().getMapModuleManager()
				.addListener(this);
		mDialog = new JDialog(getMindMapController().getFrame().getJFrame(),
				false /*
					 * not modal
					 */);
		mDialog.setTitle(getResourceString("plugins/TimeManagement.xml_WindowTitle"));
		mDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				disposeDialog();
			}
		});
		Action closeAction = new CloseAction();
		Tools.addEscapeActionToDialog(mDialog, closeAction);
		/** Menu **/
		StructuredMenuHolder menuHolder = new StructuredMenuHolder();
		JMenuBar menu = new JMenuBar();
		menuHolder.addMenu(new JMenu(getMindMapController().getText(
				"TimeManagement.Actions")), "main/actions/.");
		addAccelerator(menuHolder.addAction(new AppendDateAction(),
				"main/actions/append"),
				"keystroke_plugins/TimeManagement_append");
		addAccelerator(menuHolder.addAction(new AppendDateToChildAction(),
				"main/actions/appendAsChild"),
				"keystroke_plugins/TimeManagement_appendAsChild");
		addAccelerator(menuHolder.addAction(new AppendDateToSiblingAction(),
				"main/actions/appendAsSibling"),
				"keystroke_plugins/TimeManagement_appendAsSibling");
		JMenuItem remindMenuItem = addAccelerator(
				menuHolder.addAction(new RemindAction(), "main/actions/remind"),
				"keystroke_plugins/TimeManagementRemind");
		remindMenuItem
				.setToolTipText(getResourceString("plugins/TimeManagement.xml_reminderButton_tooltip"));
		JMenuItem removeRemindersItem = addAccelerator(menuHolder.addAction(
				new RemoveReminders(), "main/actions/removeReminders"),
				"keystroke_plugins/TimeManagementRemoveReminders");
		removeRemindersItem
				.setToolTipText(getResourceString("plugins/TimeManagement.xml_removeReminderButton_tooltip"));

		addAccelerator(
				menuHolder.addAction(new TodayAction(), "main/actions/today"),
				"keystroke_plugins/TimeManagementToday");
		menuHolder.addAction(new CloseAction(), "main/actions/close");
		menuHolder.addMenu(new JMenu(getMindMapController().getText(
				"TimeManagement.Markings")), "main/markings/.");
		addAccelerator(
				menuHolder.addAction(new AddMarkAction(), "main/markings/add"),
				"keystroke_plugins/TimeManagement_add_marking");
		addAccelerator(
				menuHolder.addAction(new RemoveMarkAction(), "main/markings/remove"),
				"keystroke_plugins/TimeManagement_remove_marking");
		menuHolder.updateMenus(menu, "main/");
		mDialog.setJMenuBar(menu);

		calendar = new JTripleCalendar(lastActivePosition, lastDate);
		Container contentPane = mDialog.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx = 0;
		gb1.gridwidth = 4;
		gb1.fill = GridBagConstraints.BOTH;
		gb1.gridy = 0;
		gb1.weightx = 1;
		gb1.weighty = 1;
		calendar.getDayChooser().addPropertyChangeListener(this);
		contentPane.add(calendar, gb1);
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 0;
			gb2.gridy = 1;
			gb2.gridwidth = 4;
			gb2.weightx = 0;
			gb2.weighty = 0;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			contentPane.add(getTimePanel(), gb2);
		}
		mDialog.pack();
		// focus fix after startup.
		mDialog.addWindowFocusListener(new WindowAdapter() {

			public void windowGainedFocus(WindowEvent e) {
				requestFocusForDay();
				mDialog.removeWindowFocusListener(this);
			}
		});
		WindowConfigurationStorage storage = getMindMapController()
				.decorateDialog(mDialog, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		mDialog.setVisible(true);

	}

	/**
	 */
	private JPanel getTimePanel() {
		if (timePanel == null) {
			timePanel = new JPanel();
			timePanel.setLayout(new GridBagLayout());
			{
				GridBagConstraints gb2 = new GridBagConstraints();
				gb2.gridx = 0;
				gb2.gridy = 0;
				gb2.fill = GridBagConstraints.HORIZONTAL;
				timePanel.add(new JLabel(
						getResourceString("plugins/TimeManagement.xml_hour")),
						gb2);
			}
			{
				GridBagConstraints gb2 = new GridBagConstraints();
				gb2.gridx = 1;
				gb2.gridy = 0;
				gb2.fill = GridBagConstraints.HORIZONTAL;
				hourField = new JTextField(2);
				hourField.setText(""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
				timePanel.add(hourField, gb2);
			}
			{
				GridBagConstraints gb2 = new GridBagConstraints();
				gb2.gridx = 2;
				gb2.gridy = 0;
				gb2.fill = GridBagConstraints.HORIZONTAL;
				timePanel
						.add(new JLabel(
								getResourceString("plugins/TimeManagement.xml_minute")),
								gb2);
			}
			{
				GridBagConstraints gb2 = new GridBagConstraints();
				gb2.gridx = 3;
				gb2.gridy = 0;
				gb2.fill = GridBagConstraints.HORIZONTAL;
				minuteField = new JTextField(2);
				String minuteString = new Integer(Calendar.getInstance().get(
						Calendar.MINUTE)).toString();
				// padding with "0"
				if (minuteString.length() < 2) {
					minuteString = "0" + minuteString;
				}
				minuteField.setText(minuteString);
				timePanel.add(minuteField, gb2);
			}

		}
		return timePanel;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(JDayChooser.DAY_PROPERTY)) {
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		Date date = getCalendarDate();
		// add permanent node hook to the nodes and this hook checks
		// permanently.
		for (Iterator i = getMindMapController().getSelecteds().iterator(); i
				.hasNext();) {
			MindMapNode node = (MindMapNode) i.next();

			ReminderHookBase alreadyPresentHook = TimeManagementOrganizer
					.getHook(node);
			if (alreadyPresentHook != null) {
				// already present:
				Object[] messageArguments = {
						new Date(alreadyPresentHook.getRemindUserAt()), date };
				MessageFormat formatter = new MessageFormat(
						getMindMapController()
								.getText(
										"plugins/TimeManagement.xml_reminderNode_onlyOneDate"));
				String message = formatter.format(messageArguments);
				logger.info(messageArguments.length + ", " + message);
				int result = JOptionPane.showConfirmDialog(
						getMindMapController().getFrame().getJFrame(), message,
						"FreeMind", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION)
					return;
				// here, the old has to be removed and the new one installed.
				addHook(node, 0L); // means remove hook, as it is already
									// present.

			}
			addHook(node, date.getTime());
			ReminderHookBase rh = TimeManagementOrganizer.getHook(node);
			if (rh == null) {
				throw new IllegalArgumentException(
						"hook not found although it is present!!");
			}
			node.invokeHook(rh);
			getMindMapController().nodeChanged(node);
		}
		// disposeDialog();
	}

	/**
	 * @param pRemindAt
	 *            TODO
	 */
	private void addHook(MindMapNode node, long pRemindAt) {
		// add the hook:
		Properties properties = new Properties();
		if (pRemindAt != 0L) {
			properties.put(ReminderHookBase.REMINDUSERAT,
					new Long(pRemindAt).toString());
		}
		getMindMapController().addHook(node,
				Tools.getVectorWithSingleElement(node), REMINDER_HOOK_NAME,
				properties);
	}

	/**
	 *
	 */
	private void disposeDialog() {
		WindowConfigurationStorage storage = new WindowConfigurationStorage();
		getMindMapController().storeDialogPositions(mDialog, storage,
				WINDOW_PREFERENCE_STORAGE_PROPERTY);
		mDialog.setVisible(false);
		mDialog.dispose();
		lastDate = getCalendar();
		lastActivePosition = calendar.getCurrentMonthPosition();
		sCurrentlyOpenTimeManagement = null;
	}

	/**
	 */
	private Date getCalendarDate() {
		Calendar cal = getCalendar();
		try {
			int value = 0;
			value = Integer.parseInt(hourField.getText());
			cal.set(Calendar.HOUR_OF_DAY, value);
			value = Integer.parseInt(minuteField.getText());
			cal.set(Calendar.MINUTE, value);
			cal.set(Calendar.SECOND, 0);
		} catch (Exception e) {
		}
		return cal.getTime();
	}

	protected Calendar getCalendar() {
		return calendar.getCalendar();
	}

	public void afterMapClose(MapModule oldMapModule, Mode oldMode) {
	}

	public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
	}

	public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
		getMindMapController().getController().getMapModuleManager()
				.removeListener(this);
		disposeDialog();
	}

	public boolean isMapModuleChangeAllowed(MapModule oldMapModule,
			Mode oldMode, MapModule newMapModule, Mode newMode) {
		return true;
	}

	public void numberOfOpenMapInformation(int number, int pIndex) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.mindmapmode.hooks.MindMapHookAdapter#getMindMapController
	 * ()
	 */
	public MindMapController getMindMapController() {
		return mController;
	}

	/**
	 * 
	 */
	private void requestFocusForDay() {
		calendar.getDayChooser().getSelectedDay().requestFocus();
	}
}
