package accessories.plugins.time;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import accessories.plugins.time.JTripleCalendar.JSwitchableCalendar;
import freemind.controller.actions.generated.instance.CalendarMarking;
import freemind.controller.actions.generated.instance.CalendarMarkings;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapController;

public class CalendarMarkingDialog extends JDialog implements ActionListener, ChangeListener, PropertyChangeListener {

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = "CalendarMarkingDialog_WindowPosition";
	public static final int CANCEL = -1;

	public static final int OK = 1;

	private MindMapController mController;
	private JPanel jContentPane;
	private JButton jOKButton;
	private JButton jCancelButton;
	private int result = CANCEL;
	private JSwitchableCalendar startDate;
	private JSwitchableCalendar endDate;
	private JColorChooser markerColor;
	private JComboBox<String> repetitionType;
	private JSpinner repeatEachNOccurence;
	private JSpinner firstOccurence;
	private JTextField nameField;
	private SpinnerNumberModel mRepeatEachNOccurenceModel;
	private SpinnerNumberModel mFirstOccurenceModel;
	private static String MARKINGS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><calendar_markings><calendar_marking name=\"bla\" color=\"#cc0099\" start_date=\"1443650400000\" end_date=\"1447801200000\" repeat_type=\"yearly\" repeat_each_n_occurence=\"1\" first_occurence=\"2\"/></calendar_markings>";
	private JTextArea mTextArea;
	private boolean mStarted = false;
	protected static java.util.logging.Logger logger = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CalendarMarkingDialog dialog = new CalendarMarkingDialog(null);
		CalendarMarkings markings = (CalendarMarkings) Tools.unMarshall(MARKINGS);
		dialog.setCalendarMarking(markings.getCalendarMarking(0));
		dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
		dialog.setVisible(true);
		System.out.println(dialog.getResult());
		CalendarMarking marking = dialog.getCalendarMarking();
		CalendarMarkings markingsZwo = new CalendarMarkings();
		markingsZwo.addCalendarMarking(marking);
		System.out.println(Tools.marshall(markingsZwo));
	}

	public CalendarMarkingDialog(MindMapController pController) {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(this.getClass().getName());
		}
		mController = pController;
		setTitle(pController.getText("CalendarMarkingDialog.title"));
		JPanel contentPane = getJContentPane();
		this.setContentPane(contentPane);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cancelPressed();
			}
		});
		Action cancelAction = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				cancelPressed();
			}
		};
		Tools.addEscapeActionToDialog(this, cancelAction);
		this.pack();
		if (mController != null) {
			WindowConfigurationStorage decorateDialog = (WindowConfigurationStorage) mController
					.decorateDialog(this, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		}
		mStarted  = true;
	}

	private void close() {
		if (mController != null) {
			WindowConfigurationStorage storage = new WindowConfigurationStorage();
			mController.storeDialogPositions(this, storage,
					WINDOW_PREFERENCE_STORAGE_PROPERTY);
		}
		this.dispose();

	}

	private void okPressed() {
		result = OK;
		// writePatternBackToModel();
		close();
	}

	private void cancelPressed() {
		result = CANCEL;
		close();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			GroupLayout layout = new GroupLayout(jContentPane);
			jContentPane.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			JLabel nameLabel = getLabel("Name");;
			nameField = new JTextField(80);
			JLabel repetitionTypeLabel = getLabel("Repetition_Type");;
			mRepetitionTypesList = new Vector<>();
			mRepetitionTypesList.add(("never"));
			mRepetitionTypesList.add(("yearly"));
			mRepetitionTypesList.add(("yearly_every_nth_day"));
			mRepetitionTypesList.add(("yearly_every_nth_week"));
			mRepetitionTypesList.add(("yearly_every_nth_month"));
			mRepetitionTypesList.add(("monthly"));
			mRepetitionTypesList.add(("monthly_every_nth_day"));
			mRepetitionTypesList.add(("monthly_every_nth_week"));
			mRepetitionTypesList.add(("weekly"));
			mRepetitionTypesList.add(("weekly_every_nth_day"));
			mRepetitionTypesList.add(("daily"));
			Vector<String> items = new Vector<>();
			for (String xmlName : mRepetitionTypesList) {
				items.add(getText(xmlName));
			}
			repetitionType = new JComboBox<String>(items);
			JLabel repeatEachNOccurenceLabel = getLabel("Repeat_Each_N_Occurence");;

			mRepeatEachNOccurenceModel = new SpinnerNumberModel(1, 1, 100, 1);
			repeatEachNOccurence = new JSpinner(mRepeatEachNOccurenceModel);
			JLabel firstOccurenceLabel = getLabel("First_Occurence");;
			mFirstOccurenceModel = new SpinnerNumberModel(0, 0, 100, 1);
			firstOccurence = new JSpinner(mFirstOccurenceModel);
			JLabel startDateLabel = getLabel("Start_Date");;
			startDate = new JSwitchableCalendar();
			startDate.setEnabled(true);
			JLabel endDateLabel = getLabel("End_Date");;
			endDate = new JSwitchableCalendar();
			endDate.setEnabled(true);
			JLabel markerColorLabel = getLabel("Background_Color");
			markerColor = new JColorChooser();
			JButton okButton = getJOKButton();
			JButton cancelButton = getJCancelButton();
			// FIXME: Example output of dates as list/text
			JLabel examplesLabel = getLabel("CalendarMarkings.Examples");
			mTextArea = new JTextArea();
			mTextArea.setEditable(false);
			layout.setHorizontalGroup(
					   layout.createSequentialGroup().addGroup(layout.createParallelGroup()
									   .addComponent(nameLabel)
									   .addComponent(repetitionTypeLabel)
									   .addComponent(repeatEachNOccurenceLabel)
									   .addComponent(firstOccurenceLabel)
									   .addComponent(startDateLabel)
									   .addComponent(endDateLabel)
									   .addComponent(markerColorLabel)
									   .addComponent(examplesLabel)
									   .addComponent(okButton)
							   ).addGroup(layout.createParallelGroup()
									   .addComponent(nameField)
									   .addComponent(repetitionType)
									   .addComponent(repeatEachNOccurence)
									   .addComponent(firstOccurence)
									   .addComponent(startDate)
									   .addComponent(endDate)
									   .addComponent(markerColor)
									   .addComponent(mTextArea)
									   .addComponent(cancelButton)
							   )
					);
			layout.setVerticalGroup(
					   layout.createSequentialGroup()
					      .addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(nameField))
					      .addGroup(layout.createParallelGroup().addComponent(repetitionTypeLabel).addComponent(repetitionType))
					      .addGroup(layout.createParallelGroup().addComponent(repeatEachNOccurenceLabel).addComponent(repeatEachNOccurence))
					      .addGroup(layout.createParallelGroup().addComponent(firstOccurenceLabel).addComponent(firstOccurence))
					      .addGroup(layout.createParallelGroup().addComponent(startDateLabel).addComponent(startDate))
					      .addGroup(layout.createParallelGroup().addComponent(endDateLabel).addComponent(endDate))
					      .addGroup(layout.createParallelGroup().addComponent(markerColorLabel).addComponent(markerColor))
					      .addGroup(layout.createParallelGroup().addComponent(examplesLabel).addComponent(mTextArea))
					      .addGroup(layout.createParallelGroup().addComponent(okButton).addComponent(cancelButton))
					);
			
			getRootPane().setDefaultButton(okButton);
			repetitionType.addActionListener(this);
			repeatEachNOccurence.addChangeListener(this);
			firstOccurence.addChangeListener(this);
			startDate.addPropertyChangeListener(this);
			endDate.addPropertyChangeListener(this);
		}
		return jContentPane;
	}

	private JLabel getLabel(String pString) {
		JLabel label = new JLabel(getText(pString));
		label.setToolTipText(getText(pString+"_description"));
		return label;
	}

	public CalendarMarking getCalendarMarking() {
		CalendarMarking marking = new CalendarMarking();
		marking.setName(nameField.getText());
		marking.setColor(Tools.colorToXml(markerColor.getColor()));
		marking.setStartDate(startDate.getCalendar().getTimeInMillis());
		marking.setEndDate(endDate.getCalendar().getTimeInMillis());
		marking.setFirstOccurence(mFirstOccurenceModel.getNumber().intValue());
		marking.setRepeatEachNOccurence(mRepeatEachNOccurenceModel.getNumber().intValue());
		int selectedIndex = repetitionType.getSelectedIndex();
		if(selectedIndex < 0 || selectedIndex >= mRepetitionTypesList.size()){
			logger.severe("Selected combo box index out of range: " + selectedIndex);
		} else {	
			marking.setRepeatType(mRepetitionTypesList.get(selectedIndex));
		}
		return marking;
	}

	public void setCalendarMarking(CalendarMarking pMarking){
		nameField.setText(pMarking.getName());
		markerColor.setColor(Tools.xmlToColor(pMarking.getColor()));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(pMarking.getStartDate());
		startDate.setCalendar(cal);
		cal.setTimeInMillis(pMarking.getEndDate());
		endDate.setCalendar(cal);
		mFirstOccurenceModel.setValue(pMarking.getFirstOccurence());
		mRepeatEachNOccurenceModel.setValue(pMarking.getRepeatEachNOccurence());
		String repeatTypeString = pMarking.getRepeatType();
		if(mRepetitionTypesList.contains(repeatTypeString)){
			repetitionType.setSelectedIndex(mRepetitionTypesList.indexOf(repeatTypeString));
		} else {
			logger.severe("Repetition type " + repeatTypeString + " not found.");
			repetitionType.setSelectedIndex(0);
		}
	}
	
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJOKButton() {
		if (jOKButton == null) {
			jOKButton = new JButton();

			jOKButton.setAction(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					okPressed();
				}

			});

			Tools.setLabelAndMnemonic(jOKButton, getText("ok"));
		}
		return jOKButton;
	}

	public String getText(String textId) {
		if (mController != null) {
			return mController.getText(textId);
		}
		return textId;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = new JButton();
			jCancelButton.setAction(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					cancelPressed();
				}
			});
			Tools.setLabelAndMnemonic(jCancelButton,
					getText(("cancel")));
		}
		return jCancelButton;
	}

	/**
	 * @return Returns the result.
	 */
	public int getResult() {
		return result;
	}

	@Override
	public void actionPerformed(ActionEvent pE) {
		showExamples();
	}

	@Override
	public void stateChanged(ChangeEvent pE) {
		showExamples();
	}
	boolean ignoreNextEvent = false;
	private Vector<String> mRepetitionTypesList;

	@Override
	public void propertyChange(PropertyChangeEvent pEvt) {
		if (pEvt.getNewValue() instanceof Calendar && pEvt.getSource() instanceof JSwitchableCalendar) {
			if (!ignoreNextEvent) {
				Calendar cal = (Calendar) pEvt.getNewValue();
				ignoreNextEvent = true;
				((JSwitchableCalendar)pEvt.getSource()).setCalendar(cal);
			} else {
				ignoreNextEvent = false;
			}
		} 
		showExamples();
	}

	private void showExamples() {
		if(!mStarted){
			return;
		}
		CalendarMarking marking = getCalendarMarking();
		CalendarMarkings container = new CalendarMarkings();
		container.addCalendarMarking(marking);
		CalendarMarkingEvaluator evaluator = new CalendarMarkingEvaluator(container);
		Set<Calendar> nEntries = evaluator.getAtLeastTheFirstNEntries(10);
		String text = "";
		for (Calendar calendar : nEntries) {
			text +=  DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime())+"\n";
		}
		mTextArea.setText(text);
	}

	/**Sets the dates of both start and end to the specified.
	 * @param pCal
	 */
	public void setDates(Calendar pCal) {
		endDate.setDate(pCal);
		startDate.setDate(pCal);
	}

}
