package freemind.modes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import freemind.controller.Controller;
import freemind.controller.LastStateStorageManagement;
import freemind.controller.MapModuleManager;
import freemind.controller.MapMouseMotionListener;
import freemind.controller.MapMouseWheelListener;
import freemind.controller.MindMapNodesSelection;
import freemind.controller.NodeDragListener;
import freemind.controller.NodeDropListener;
import freemind.controller.NodeKeyListener;
import freemind.controller.NodeMotionListener;
import freemind.controller.NodeMouseMotionListener;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.MindmapLastStateStorage;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.extensions.PermanentNodeHook;
import freemind.main.FreeMindCommon;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.FreeMindFileDialog.DirectoryResultListener;
import freemind.modes.common.listeners.MindMapMouseWheelEventHandler;
import freemind.view.MapModule;
import freemind.view.mindmapview.*;

public abstract class ControllerAdapter extends MapFeedbackAdapter implements ModeController, DirectoryResultListener {
	private Mode mode;
	private Color selectionColor = new Color(200, 220, 200);
	private MapAdapter mModel;
	private HashSet mNodeSelectionListeners = new HashSet();
	private HashSet mNodeLifetimeListeners = new HashSet();
	private File lastCurrentDir = null;

	public ControllerAdapter(Mode mode) {
		this.setMode(mode);
	}

	public void setModel(MapAdapter model) {
		mModel = model;
	}

	public abstract MindMapNode newNode(Object userObject, MindMap map);

	public MapAdapter newModel(ModeController modeController) {
		throw new java.lang.UnsupportedOperationException();
	}

	protected FileFilter getFileFilter() {
		return null;
	}

	public void nodeChanged(MindMapNode node) {
		setSaved(false);
		nodeRefresh(node, true);
	}

	public void setSaved(boolean pIsClean) {
		boolean stateChanged = getMap().setSaved(pIsClean);
		if (stateChanged) {
			getController().setTitle();
		}
	}

	public void nodeRefresh(MindMapNode node) {
		nodeRefresh(node, false);
	}

	private void nodeRefresh(MindMapNode node, boolean isUpdate) {
		logger.finest("nodeChanged called for node " + node + " parent=" + node.getParentNode());
		if (isUpdate) {
			if (node.getHistoryInformation() != null) {
				node.getHistoryInformation().setLastModifiedAt(new Date());
			}
			updateNode(node);
		}

		((MapAdapter) getMap()).nodeChangedInternal(node);
	}

	public void refreshMap() {
		final MindMapNode root = getMap().getRootNode();
		refreshMapFrom(root);
	}

	public void refreshMapFrom(MindMapNode node) {
		for (Object o : node.getChildren()) {
			MindMapNode child = (MindMapNode) o;
			refreshMapFrom(child);
		}
		((MapAdapter) getMap()).nodeChangedInternal(node);

	}

	public void nodeStructureChanged(MindMapNode node) {
		getMap().nodeStructureChanged(node);
	}

	protected void updateNode(MindMapNode node) {
		for (Object mNodeSelectionListener : mNodeSelectionListeners) {
			NodeSelectionListener listener = (NodeSelectionListener) mNodeSelectionListener;
			listener.onUpdateNodeHook(node);
		}
	}

	public void onLostFocusNode(NodeView node) {
		try {
			HashSet copy = new HashSet(mNodeSelectionListeners);
			for (Object aCopy : copy) {
				NodeSelectionListener listener = (NodeSelectionListener) aCopy;
				listener.onLostFocusNode(node);
			}
			for (Object o : node.getModel().getActivatedHooks()) {
				PermanentNodeHook hook = (PermanentNodeHook) o;
				hook.onLostFocusNode(node);
			}
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error in node selection listeners", e);
		}

	}

	public void onFocusNode(NodeView node) {
		try {
			HashSet copy = new HashSet(mNodeSelectionListeners);
			for (Object aCopy : copy) {
				NodeSelectionListener listener = (NodeSelectionListener) aCopy;
				listener.onFocusNode(node);
			}
			for (Object o : node.getModel().getActivatedHooks()) {
				PermanentNodeHook hook = (PermanentNodeHook) o;
				hook.onFocusNode(node);
			}
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error in node selection listeners", e);
		}

	}

	public void changeSelection(NodeView pNode, boolean pIsSelected) {
		try {
			HashSet copy = new HashSet(mNodeSelectionListeners);
			for (Object aCopy : copy) {
				NodeSelectionListener listener = (NodeSelectionListener) aCopy;
				listener.onSelectionChange(pNode, pIsSelected);
			}
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error in node selection listeners", e);
		}

	}

	public void onViewCreatedHook(NodeView node) {
		for (Object o : node.getModel().getActivatedHooks()) {
			PermanentNodeHook hook = (PermanentNodeHook) o;
			hook.onViewCreatedHook(node);
		}
	}

	public void onViewRemovedHook(NodeView node) {
		for (Object o : node.getModel().getActivatedHooks()) {
			PermanentNodeHook hook = (PermanentNodeHook) o;
			hook.onViewRemovedHook(node);
		}
	}

	public void registerNodeSelectionListener(NodeSelectionListener listener,
			boolean pCallWithCurrentSelection) {
		mNodeSelectionListeners.add(listener);
		if (pCallWithCurrentSelection) {
			try {
				listener.onFocusNode(getSelectedView());
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
			for (NodeView view : getView().getSelecteds()) {
				try {
					listener.onSelectionChange(view, true);
				} catch (Exception e) {
					Resources.getInstance().logException(e);
				}
			}
		}
	}

	public void deregisterNodeSelectionListener(NodeSelectionListener listener) {
		mNodeSelectionListeners.remove(listener);
	}

	public void registerNodeLifetimeListener(NodeLifetimeListener listener, boolean pFireCreateEvent) {
		mNodeLifetimeListeners.add(listener);
		if (pFireCreateEvent) {
			fireRecursiveNodeCreateEvent(getRootNode());
		}
	}

	public void deregisterNodeLifetimeListener(NodeLifetimeListener listener) {
		mNodeLifetimeListeners.remove(listener);
	}

	public HashSet getNodeLifetimeListeners() {
		return mNodeLifetimeListeners;
	}

	public void fireNodePreDeleteEvent(MindMapNode node) {
		for (Object mNodeLifetimeListener : mNodeLifetimeListeners) {
			NodeLifetimeListener listener = (NodeLifetimeListener) mNodeLifetimeListener;
			listener.onPreDeleteNode(node);
		}
	}

	public void fireNodePostDeleteEvent(MindMapNode node, MindMapNode parent) {
		for (Object mNodeLifetimeListener : mNodeLifetimeListeners) {
			NodeLifetimeListener listener = (NodeLifetimeListener) mNodeLifetimeListener;
			listener.onPostDeleteNode(node, parent);
		}
	}

	public void fireRecursiveNodeCreateEvent(MindMapNode node) {
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			NodeAdapter child = (NodeAdapter) i.next();
			fireRecursiveNodeCreateEvent(child);
		}
		for (Object mNodeLifetimeListener : mNodeLifetimeListeners) {
			NodeLifetimeListener listener = (NodeLifetimeListener) mNodeLifetimeListener;
			listener.onCreateNodeHook(node);
		}
	}

	public void firePreSaveEvent(MindMapNode node) {
		HashSet listenerCopy = new HashSet(mNodeSelectionListeners);
		for (Object aListenerCopy : listenerCopy) {
			NodeSelectionListener listener = (NodeSelectionListener) aListenerCopy;
			listener.onSaveNode(node);
		}
	}

	public String getText(String textId) {
		return getController().getResourceString(textId);
	}

	public ModeController newMap() {
		ModeController newModeController = getMode().createModeController();
		MapAdapter newModel = newModel(newModeController);
		newMap(newModel, newModeController);
		newModeController.getView().moveToRoot();
		return newModeController;
	}

	public void newMap(final MindMap mapModel, ModeController pModeController) {
		getController().getMapModuleManager().newMapModule(mapModel,
				pModeController);
		pModeController.setSaved(false);
	}

	public MapFeedback load(URL file) throws FileNotFoundException,
			IOException, XMLParseException, URISyntaxException {
		String mapDisplayName = getController().getMapModuleManager()
				.checkIfFileIsAlreadyOpened(file);
		if (null != mapDisplayName) {
			getController().getMapModuleManager().changeToMapModule(
					mapDisplayName);
			return getController().getModeController();
		} else {
			final ModeController newModeController = getMode().createModeController();
			final MapAdapter model = newModel(newModeController);
			((ControllerAdapter) newModeController).loadInternally(file, model);
			newMap(model, newModeController);
			newModeController.setSaved(true);
			restoreMapsLastState(newModeController, model);
			return newModeController;
		}
	}

	/**
	 * @param model 
	 * @param pFile
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws XMLParseException 
	 */
	abstract protected void loadInternally(URL url, MapAdapter model) throws URISyntaxException, XMLParseException, IOException;

	public MapFeedback load(File file) throws FileNotFoundException,
			IOException {
		try {
			return load(Tools.fileToUrl(file));
		} catch (XMLParseException | URISyntaxException e) {
			freemind.main.Resources.getInstance().logException(e);
			throw new RuntimeException(e);
		}
	}

	protected void restoreMapsLastState(final ModeController newModeController,
			final MapAdapter model) {
		String lastStateMapXml = getFrame().getProperty(FreeMindCommon.MINDMAP_LAST_STATE_MAP_STORAGE);
		LastStateStorageManagement management = new LastStateStorageManagement(lastStateMapXml);
		MindmapLastStateStorage store = management.getStorage(model.getRestorable());
		if (store != null) {
			ModeController modeController = newModeController;
			getController().setZoom(store.getLastZoom());
			MindMapNode sel;
			try {
				sel = modeController.getNodeFromID(store.getLastSelected());
				modeController.centerNode(sel);
				List selected = new Vector();
				for (Object o : store.getListNodeListMemberList()) {
					NodeListMember member = (NodeListMember) o;
					NodeAdapter selNode = modeController.getNodeFromID(member.getNode());
					selected.add(selNode);
				}
				modeController.select(sel, selected);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
				newModeController.getView().moveToRoot();
			}
		} else {
			newModeController.getView().moveToRoot();
		}
	}

	public boolean save() {
		if (getModel().isSaved())
			return true;
		if (getModel().getFile() == null || getModel().isReadOnly()) {
			return saveAs();
		} else {
			return save(getModel().getFile());
		}
	}

	public void loadURL(String relative) {
		try {
			logger.info("Trying to open " + relative);
			URL absolute;
			if (Tools.isAbsolutePath(relative)) {
				absolute = Tools.fileToUrl(new File(relative));
			} else if (relative.startsWith("#")) {
				logger.finest("found relative link to " + relative);
				String target = relative.substring(1);
				try {
					centerNode(getNodeFromID(target));
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
					getFrame().out(Tools.expandPlaceholders(getText("link_not_found"), target));
				}
				return;

			} else {
				absolute = new URL(getMap().getURL(), relative);
			}
			URL originalURL = absolute;
			String ref = absolute.getRef();
			if (ref != null) {
				absolute = Tools.getURLWithoutReference(absolute);
			}
			String extension = Tools.getExtension(absolute.toString());
			if ((extension != null) && extension .equals(freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION_WITHOUT_DOT)) {
				logger.info("Trying to open mind map " + absolute);
				MapModuleManager mapModuleManager = getController().getMapModuleManager();
				String mapExtensionKey = mapModuleManager
						.checkIfFileIsAlreadyOpened(absolute);
				if (mapExtensionKey == null) {
					setWaitingCursor(true);
					load(absolute);
				} else {
					mapModuleManager.tryToChangeToMapModule(mapExtensionKey);
				}
				if (ref != null) {
					try {
						ModeController newModeController = getController().getModeController();
						newModeController.centerNode(newModeController.getNodeFromID(ref));
					} catch (Exception e) {
						freemind.main.Resources.getInstance().logException(e);
						getFrame().out(
								Tools.expandPlaceholders(
										getText("link_not_found"), ref));
					}
				}
			} else {
				getFrame().openDocument(originalURL);
			}
		} catch (MalformedURLException ex) {
			freemind.main.Resources.getInstance().logException(ex);
			getController().errorMessage(getText("url_error") + "\n" + ex);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		} finally {
			setWaitingCursor(false);
		}
	}

	public void setWaitingCursor(boolean pWaiting) {
		getFrame().setWaitingCursor(pWaiting);
	}

	public List getSelecteds() {
		LinkedList selecteds = new LinkedList();
		ListIterator it = getView().getSelecteds().listIterator();
		while (it.hasNext()) {
            NodeView selected = (NodeView) it.next();
            selecteds.add(selected.getModel());
        }
		return selecteds;
	}

	@Override
	public void select(NodeView node) {
		getView().select(node);
	}

	public void select(MindMapNode primarySelected, List selecteds) {
		for (Object selected1 : selecteds) {
			MindMapNode node = (MindMapNode) (selected1);
			displayNode(node);
		}
		final NodeView focussedNodeView = getNodeView(primarySelected);
		if (focussedNodeView != null) {
			getView().selectAsTheOnlyOneSelected(focussedNodeView);
			getView().scrollNodeToVisible(focussedNodeView);
			for (Object selected : selecteds) {
				MindMapNode node = (MindMapNode) selected;
				NodeView nodeView = getNodeView(node);
				if (nodeView != null) {
					getView().makeTheSelected(nodeView);
				}
			}
		}
		getController().obtainFocusForSelected();
	}

	public void selectBranch(NodeView selected, boolean extend) {
		displayNode(selected.getModel());
		getView().selectBranch(selected, extend);
	}

	public List getSelectedsByDepth() {
		List result = getSelecteds();
		sortNodesByDepth(result);
		return result;
	}

	public boolean save(File file) {
		boolean result = false;
		try {
			setWaitingCursor(true);
			result = getModel().save(file);
			if (result && "true"
					.equals(getProperty(FreeMindCommon.CREATE_THUMBNAIL_ON_SAVE))) {
				File baseFileName = getModel().getFile();
				String fileName = Resources.getInstance().createThumbnailFileName(baseFileName);
				Tools.makeFileHidden(new File(fileName), false);
				IndependantMapViewCreator.printToFile(getView(), fileName,
						true,
						getIntProperty(FreeMindCommon.THUMBNAIL_SIZE, 800));
				Tools.makeFileHidden(new File(fileName), true);
			}
		} catch (FileNotFoundException e) {
			freemind.main.Resources.getInstance().logException(e);
			String message = Tools.expandPlaceholders(getText("save_failed"),
					file.getName());
			getController().errorMessage(message);
		} catch (Exception e) {
			logger.severe("Error in MindMapMapModel.save(): ");
			freemind.main.Resources.getInstance().logException(e);
		} finally {
			setWaitingCursor(false);
		}
		if(result) {
			setSaved(true);
		}
		return result;
	}

	/** @return returns the new JMenuItem. */
	protected JMenuItem add(JMenu menu, Action action, String keystroke) {
		JMenuItem item = menu.add(action);
		item.setAccelerator(KeyStroke.getKeyStroke(getFrame()
				.getAdjustableProperty(keystroke)));
		return item;
	}

	/**
	 * @return returns the new JMenuItem.
	 * @param keystroke
	 *            can be null, if no keystroke should be assigned.
	 */
	protected JMenuItem add(StructuredMenuHolder holder, String category,
			Action action, String keystroke) {
		JMenuItem item = holder.addAction(action, category);
		if (keystroke != null) {
			String keyProperty = getFrame().getAdjustableProperty(keystroke);
			logger.finest("Found key stroke: " + keyProperty);
			item.setAccelerator(KeyStroke.getKeyStroke(keyProperty));
		}
		return item;
	}

	/**
	 * @return returns the new JCheckBoxMenuItem.
	 * @param keystroke
	 *            can be null, if no keystroke should be assigned.
	 */
	protected JMenuItem addCheckBox(StructuredMenuHolder holder,
			String category, Action action, String keystroke) {
		JCheckBoxMenuItem item = (JCheckBoxMenuItem) holder.addMenuItem(
				new JCheckBoxMenuItem(action), category);
		if (keystroke != null) {
			item.setAccelerator(KeyStroke.getKeyStroke(getFrame()
					.getAdjustableProperty(keystroke)));
		}
		return item;
	}

	protected JMenuItem addRadioItem(StructuredMenuHolder holder,
			String category, Action action, String keystroke, boolean isSelected) {
		JRadioButtonMenuItem item = (JRadioButtonMenuItem) holder.addMenuItem(
				new JRadioButtonMenuItem(action), category);
		if (keystroke != null) {
			item.setAccelerator(KeyStroke.getKeyStroke(getFrame()
					.getAdjustableProperty(keystroke)));
		}
		item.setSelected(isSelected);
		return item;
	}

	protected void add(JMenu menu, Action action) {
		menu.add(action);
	}

	public void open() {
		FreeMindFileDialog chooser = getFileChooser();
		int returnVal = chooser.showOpenDialog(getView());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles;
			if (chooser.isMultiSelectionEnabled()) {
				selectedFiles = chooser.getSelectedFiles();
			} else {
				selectedFiles = new File[] { chooser.getSelectedFile() };
			}
			for (File theFile : selectedFiles) {
				try {
					lastCurrentDir = theFile.getParentFile();
					load(theFile);
				} catch (Exception ex) {
					handleLoadingException(ex);
					break;
				}
			}
		}
		getController().setTitle();
	}

	public void setChosenDirectory(File pDir) {
		lastCurrentDir = pDir;
	}

	public FreeMindFileDialog getFileChooser(FileFilter filter) {
		FreeMindFileDialog chooser = Resources.getInstance().getStandardFileChooser(filter);
		chooser.registerDirectoryResultListener(this);
		File parentFile = getMapsParentFile();
		if (parentFile != null && lastCurrentDir == null) {
			lastCurrentDir = parentFile;
		}
		if (lastCurrentDir != null) {
			chooser.setCurrentDirectory(lastCurrentDir);
		}
		return chooser;
	}

	public FreeMindFileDialog getFileChooser() {
		return getFileChooser(getFileFilter());
	}

	private File getMapsParentFile() {
		if ((getMap() != null) && (getMap().getFile() != null)
				&& (getMap().getFile().getParentFile() != null)) {
			return getMap().getFile().getParentFile();
		}
		return null;
	}

	public void handleLoadingException(Exception ex) {
		String exceptionType = ex.getClass().getName();
		switch (exceptionType) {
			case "freemind.main.XMLParseException":
				int showDetail = JOptionPane.showConfirmDialog(getView(),
						getText("map_corrupted"), "FreeMind",
						JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
				if (showDetail == JOptionPane.YES_OPTION) {
					getController().errorMessage(ex);
				}
				break;
			case "java.io.FileNotFoundException":
				getController().errorMessage(ex.getMessage());
				break;
			default:
				Resources.getInstance().logException(ex);
				getController().errorMessage(ex);
				break;
		}
	}

	public boolean saveAs() {
		File f;
		FreeMindFileDialog chooser = getFileChooser();
		if (getMapsParentFile() == null) {
			chooser.setSelectedFile(new File(getFileNameProposal()
					+ freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION));
		}
		chooser.setDialogTitle(getText("save_as"));
		boolean repeatSaveAsQuestion;
		do {
			repeatSaveAsQuestion = false;
			int returnVal = chooser.showSaveDialog(getView());
			if (returnVal != JFileChooser.APPROVE_OPTION) {// not ok pressed
				return false;
			}

			f = chooser.getSelectedFile();
			lastCurrentDir = f.getParentFile();
			String ext = Tools.getExtension(f.getName());
			if (!ext.equals(freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION_WITHOUT_DOT)) {
				f = new File(f.getParent(), f.getName()
						+ freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION);
			}

			if (f.exists()) {
				int overwriteMap = JOptionPane.showConfirmDialog(getView(),
						getText("map_already_exists"), "FreeMind",
						JOptionPane.YES_NO_OPTION);
				if (overwriteMap != JOptionPane.YES_OPTION) {
					repeatSaveAsQuestion = true;
				}
			}
		} while (repeatSaveAsQuestion);
		try {
			String lockingUser = getModel().tryToLock(f);
			if (lockingUser != null) {
				getFrame().getController().informationMessage(
						Tools.expandPlaceholders(
								getText("map_locked_by_save_as"), f.getName(),
								lockingUser));
				return false;
			}
		} catch (Exception e) {
			getFrame().getController().informationMessage(
					Tools.expandPlaceholders(
							getText("locking_failed_by_save_as"), f.getName()));
			return false;
		}

		save(f);
		getController().getMapModuleManager().updateMapModuleName();
		return true;
	}

	private String getFileNameProposal() {
		return Tools.getFileNameProposal(getMap().getRootNode());
	}

	public boolean close(boolean force, MapModuleManager mapModuleManager) {
		getFrame().out("");
		if (!force && !getModel().isSaved()) {
			String text = getText("save_unsaved") + "\n"
					+ mapModuleManager.getMapModule().toString();
			String title = Tools.removeMnemonic(getText("save"));
			int returnVal = JOptionPane.showOptionDialog(getFrame()
					.getContentPane(), text, title,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (returnVal == JOptionPane.YES_OPTION) {
				boolean savingNotCancelled = save();
				if (!savingNotCancelled) {
					return false;
				}
			} else if ((returnVal == JOptionPane.CANCEL_OPTION)
					|| (returnVal == JOptionPane.CLOSED_OPTION)) {
				return false;
			}
		}
		LastStateStorageManagement management = new LastStateStorageManagement(
				getFrame().getProperty(
						FreeMindCommon.MINDMAP_LAST_STATE_MAP_STORAGE));
		String restorable = getModel().getRestorable();
		if (restorable != null) {
			MindmapLastStateStorage store = management.getStorage(restorable);
			if (store == null) {
				store = new MindmapLastStateStorage();
			}
			store.setRestorableName(restorable);
			store.setLastZoom(getView().getZoom());
			Point viewLocation = getView().getViewPosition();
			if (viewLocation != null) {
				store.setX(viewLocation.x);
				store.setY(viewLocation.y);
			}
			String lastSelected = this.getNodeID(this.getSelected());
			store.setLastSelected(lastSelected);
			store.clearNodeListMemberList();
			List selecteds = this.getSelecteds();
			for (Object selected : selecteds) {
				MindMapNode node = (MindMapNode) selected;
				NodeListMember member = new NodeListMember();
				member.setNode(this.getNodeID(node));
				store.addNodeListMember(member);
			}
			management.changeOrAdd(store);
			getFrame().setProperty(
					FreeMindCommon.MINDMAP_LAST_STATE_MAP_STORAGE,
					management.getXml());
		}

		getModel().destroy();
		return true;
	}

	public void setVisible(boolean visible) {
		NodeView node = getSelectedView();
		if (visible) {
			onFocusNode(node);
		} else {
			if (node != null) {
				onLostFocusNode(node);
			}
		}
		changeSelection(node, !visible);
	}

	protected void setAllActions(boolean enabled) {
		// controller actions:
		getController().zoomIn.setEnabled(enabled);
		getController().zoomOut.setEnabled(enabled);
		getController().showFilterToolbarAction.setEnabled(enabled);
	}

	private class ControllerPopupMenuListener implements PopupMenuListener {
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			setBlocked(true); // block controller
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			setBlocked(false); // unblock controller
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
			setBlocked(false); // unblock controller
		}

	}

	protected final ControllerPopupMenuListener popupListenerSingleton = new ControllerPopupMenuListener();

	public void showPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			JPopupMenu popupmenu = getPopupMenu();
			if (popupmenu != null) {
				popupmenu.addPopupMenuListener(this.popupListenerSingleton);
				popupmenu.show(e.getComponent(), e.getX(), e.getY());
				e.consume();
			}
		}
	}

	public JPopupMenu getPopupForModel(java.lang.Object obj) {
		return null;
	}

	public Component getLeftToolBar() {
		return null;
	}

	public JToolBar getModeToolBar() {
		return null;
	}

	private boolean isBlocked = false;

	private MapView mView;

	public boolean isBlocked() {
		return this.isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public Mode getMode() {
		return mode;
	}

	protected void setMode(Mode mode) {
		this.mode = mode;
	}

	public MindMap getMap() {
		return mModel;
	}

	public MindMapNode getRootNode() {
		return (MindMapNode) getMap().getRoot();
	}

	public URL getResource(String name) {
		return getFrame().getResource(name);
	}

	@Override
	public String getResourceString(String pTextId) {
		return getFrame().getResourceString(pTextId);
	}

	public Controller getController() {
		return getMode().getController();
	}

	public FreeMindMain getFrame() {
		return getController().getFrame();
	}

	public ControllerAdapter getModeController() {
		return this;
	}

	public MapAdapter getModel() {
		return mModel;
	}

	public MapView getView() {
		return mView;
	}

	@Override
	public ViewAbstraction getViewAbstraction() {
		return getView();
	}

	@Override
	public ViewFeedback getViewFeedback() {
		return this;
	}

	public void setView(MapView pView) {
		mView = pView;
	}

	protected void updateMapModuleName() {
		getController().getMapModuleManager().updateMapModuleName();
	}

	public MindMapNode getSelected() {
		final NodeView selectedView = getSelectedView();
		if (selectedView != null)
			return selectedView.getModel();
		return null;
	}

	public NodeView getSelectedView() {
		if (getView() != null)
			return getView().getSelected();
		return null;
	}

	public class OpenAction extends AbstractAction {
		ControllerAdapter mc;

		public OpenAction(ControllerAdapter modeController) {
			super(getText("open"), freemind.view.ImageFactory.getInstance().createIcon(getResource("images/fileopen.png")));
			mc = modeController;
		}

		public void actionPerformed(ActionEvent e) {
			mc.open();
			getController().setTitle(); // Possible update of read-only
		}
	}

	public class SaveAction extends FreemindAction {

		public SaveAction() {
			super(Tools.removeMnemonic(getText("save")), freemind.view.ImageFactory.getInstance().createIcon(
					getResource("images/filesave.png")), ControllerAdapter.this);
		}

		public void actionPerformed(ActionEvent e) {
			boolean success = save();
			if (success) {
				getFrame().out(getText("saved"));
				getController().setTitle();
			}
		}

	}

	public class SaveAsAction extends FreemindAction {

		public SaveAsAction() {
			super(getText("save_as"), freemind.view.ImageFactory.getInstance().createIcon(getResource("images/filesaveas.png")), ControllerAdapter.this);
		}

		public void actionPerformed(ActionEvent e) {
			saveAs();
			getController().setTitle();
		}
	}

	protected class FileOpener implements DropTargetListener {
		private boolean isDragAcceptable(DropTargetDragEvent event) {
			DataFlavor[] flavors = event.getCurrentDataFlavors();
			for (DataFlavor flavor : flavors) {
				if (flavor.isFlavorJavaFileListType()) {
					return true;
				}
			}
			return false;
		}

		private boolean isDropAcceptable(DropTargetDropEvent event) {
			DataFlavor[] flavors = event.getCurrentDataFlavors();
			for (DataFlavor flavor : flavors) {
				if (flavor.isFlavorJavaFileListType()) {
					return true;
				}
			}
			return false;
		}

		public void drop(DropTargetDropEvent dtde) {
			if (!isDropAcceptable(dtde)) {
				dtde.rejectDrop();
				return;
			}
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			try {
				Object data = dtde.getTransferable().getTransferData(
						DataFlavor.javaFileListFlavor);
				if (data == null) {
					dtde.dropComplete(false);
					return;
				}
				for (Object o : ((List) data)) {
					File file = (File) o;
					load(file);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(getView(), "Couldn't open dropped file(s). Reason: " + e.getMessage());
				dtde.dropComplete(false);
				return;
			}
			dtde.dropComplete(true);
		}

		public void dragEnter(DropTargetDragEvent dtde) {
			if (!isDragAcceptable(dtde)) {
				dtde.rejectDrag();
			}
		}

		public void dragOver(DropTargetDragEvent e) {
		}

		public void dragExit(DropTargetEvent e) {
		}

		public void dragScroll(DropTargetDragEvent e) {
		}

		public void dropActionChanged(DropTargetDragEvent e) {
		}
	}

	public Transferable copy(MindMapNode node, boolean saveInvisible) {
		throw new IllegalArgumentException("No copy so far.");
	}

	public Transferable copy() {
		return copy(getView().getSelectedNodesSortedByY(), false);
	}

	public Transferable copySingle() {

		final ArrayList selectedNodes = getView().getSingleSelectedNodes();
		return copy(selectedNodes, false);
	}

	public Transferable copy(List selectedNodes, boolean copyInvisible) {
		try {
			String forNodesFlavor = createForNodesFlavor(selectedNodes,
					copyInvisible);
			List createForNodeIdsFlavor = createForNodeIdsFlavor(selectedNodes,
					copyInvisible);

			String plainText = getMap().getAsPlainText(selectedNodes);
			return new MindMapNodesSelection(forNodesFlavor, null, plainText,
					getMap().getAsRTF(selectedNodes), getMap().getAsHTML(
							selectedNodes), null, null, createForNodeIdsFlavor);
		}

		catch (UnsupportedFlavorException | IOException ex) {
			freemind.main.Resources.getInstance().logException(ex);
		}
		return null;
	}

	public String createForNodesFlavor(List selectedNodes, boolean copyInvisible)
			throws UnsupportedFlavorException, IOException {
		String forNodesFlavor = "";
		boolean firstLoop = true;
		for (Object selectedNode : selectedNodes) {
			MindMapNode tmpNode = (MindMapNode) selectedNode;
			if (firstLoop) {
				firstLoop = false;
			} else {
				forNodesFlavor += NODESEPARATOR;
			}

			forNodesFlavor += copy(tmpNode, copyInvisible).getTransferData(
					MindMapNodesSelection.mindMapNodesFlavor);
		}
		return forNodesFlavor;
	}

	public List createForNodeIdsFlavor(List selectedNodes, boolean copyInvisible)
			throws UnsupportedFlavorException, IOException {
		Vector forNodesFlavor = new Vector();
		boolean firstLoop = true;
		for (Object selectedNode : selectedNodes) {
			MindMapNode tmpNode = (MindMapNode) selectedNode;

			forNodesFlavor.add(getNodeID(tmpNode));
		}
		return forNodesFlavor;
	}

	public Color getSelectionColor() {
		return selectionColor;
	}

	public void updatePopupMenu(StructuredMenuHolder holder) {

	}

	public void shutdownController() {
		setAllActions(false);
		getMapMouseWheelListener().deregister();
	}

	/**
	 * This method is called after and before a change of the map module. Use it
	 * to perform the actions that cannot be performed at creation time.
	 * 
	 */
	public void startupController() {
		setAllActions(true);
		if (getFrame().getView() != null) {
			FileOpener fileOpener = new FileOpener();
			DropTarget dropTarget = new DropTarget(getFrame().getView(), fileOpener);
		}
		getMapMouseWheelListener().register(new MindMapMouseWheelEventHandler(this));
	}

	public String getLinkShortText(MindMapNode node) {
		String adaptedText = node.getLink();
		if (adaptedText == null)
			return null;
		if (adaptedText.startsWith("#")) {
			try {
				MindMapNode dest = getNodeFromID(adaptedText.substring(1));
				return dest.getShortText(this);
			} catch (Exception e) {
				return getText("link_not_available_any_more");
			}
		}
		return adaptedText;
	}

	public boolean extendSelection(MouseEvent e) {
		NodeView newlySelectedNodeView = ((MainView) e.getComponent()).getNodeView();
		boolean extend = e.isControlDown();
		if (Tools.isMacOsX()) {
			extend |= e.isMetaDown();
		}
		boolean range = e.isShiftDown();
		boolean branch = e.isAltGraphDown() || e.isAltDown();
		boolean retValue = false;

		if (extend || range || branch || !getView().isSelected(newlySelectedNodeView)) {
			if (!range) {
				if (extend)
					getView().toggleSelected(newlySelectedNodeView);
				else
					select(newlySelectedNodeView);
				retValue = true;
			} else {
				retValue = getView().selectContinuous(newlySelectedNodeView);
			}
			if (branch) {
				getView().selectBranch(newlySelectedNodeView, extend);
				retValue = true;
			}
		}

		if (retValue) {
			e.consume();

			String link = newlySelectedNodeView.getModel().getLink();
			link = (link != null ? link : " ");
			getController().getFrame().out(link);
		}
		logger.fine("MouseEvent: extend:" + extend + ", range:" + range + ", branch:" + branch + ", event:" + e + ", retValue:" + retValue);
		return retValue;
	}

	public void displayNode(MindMapNode node) {
		displayNode(node, null);
	}

	public void displayNode(MindMapNode node, ArrayList nodesUnfoldedByDisplay) {
		Object[] path = getMap().getPathToRoot(node);
		for (int i = 0; i < path.length - 1; i++) {
			MindMapNode nodeOnPath = (MindMapNode) path[i];
			if (nodeOnPath.isFolded()) {
				if (nodesUnfoldedByDisplay != null)
					nodesUnfoldedByDisplay.add(nodeOnPath);
				setFolded(nodeOnPath, false);
			}
		}
	}

	private void centerNode(NodeView node) {
		getView().centerNode(node);
		getView().selectAsTheOnlyOneSelected(node);
	}

	public void centerNode(MindMapNode node) {
		NodeView view;
		if (node != null) {
			view = getController().getView().getNodeView(node);
		} else {
			return;
		}
		if (view == null) {
			displayNode(node);
			view = getController().getView().getNodeView(node);
		}
		centerNode(view);
	}

	@Override
	public NodeView getNodeView(MindMapNode node) {
		return getView().getNodeView(node);
	}

	public void loadURL() {
		String link = getSelected().getLink();
		if (link != null) {
			loadURL(link);
		}
	}

	public Set getRegisteredMouseWheelEventHandler() {
		return Collections.EMPTY_SET;
	}

	public MapModule getMapModule() {
		return getController().getMapModuleManager().getModuleGivenModeController(this);
	}

	public void setToolTip(MindMapNode node, String key, String value) {
		node.setToolTip(key, value);
		nodeRefresh(node);
	}

	@Override
	public String getProperty(String pResourceId) {
		return getController().getProperty(pResourceId);
	}

	@Override
	public Font getDefaultFont() {
		return getController().getDefaultFont();
	}

	@Override
	public Font getFontThroughMap(Font pFont) {
		return getController().getFontThroughMap(pFont);
	}

	@Override
	public NodeMouseMotionListener getNodeMouseMotionListener() {
		return getController().getNodeMouseMotionListener();
	}

	@Override
	public NodeMotionListener getNodeMotionListener() {
		return getController().getNodeMotionListener();
	}

	@Override
	public NodeKeyListener getNodeKeyListener() {
		return getController().getNodeKeyListener();
	}

	@Override
	public NodeDragListener getNodeDragListener() {
		return getController().getNodeDragListener();
	}

	@Override
	public NodeDropListener getNodeDropListener() {
		return getController().getNodeDropListener();
	}

	@Override
	public MapMouseMotionListener getMapMouseMotionListener() {
		return getController().getMapMouseMotionListener();
	}

	@Override
	public MapMouseWheelListener getMapMouseWheelListener() {
		return getController().getMapMouseWheelListener();
	}

	@Override
	public NodeAdapter getNodeFromID(String nodeID) {
		NodeAdapter node = (NodeAdapter) getMap().getLinkRegistry().getTargetForId(nodeID);
		if (node == null) {
			throw new IllegalArgumentException("Node belonging to the node id "
					+ nodeID + " not found in map " + getMap().getFile());
		}
		return node;
	}

	@Override
	public String getNodeID(MindMapNode selected) {
		return getMap().getLinkRegistry().registerLinkTarget(selected);
	}

	@Override
	public void setProperty(String pProperty, String pValue) {
		getController().setProperty(pProperty, pValue);
	}
}
