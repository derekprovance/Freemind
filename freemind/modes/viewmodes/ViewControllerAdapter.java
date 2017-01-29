package freemind.modes.viewmodes;

import java.awt.event.MouseEvent;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHookSubstituteUnknown;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.common.CommonNodeKeyListener;
import freemind.modes.common.CommonToggleFoldedAction;
import freemind.modes.common.actions.FindAction;
import freemind.modes.common.actions.FindAction.FindNextAction;
import freemind.modes.common.listeners.CommonMouseMotionManager;
import freemind.modes.common.listeners.CommonNodeMouseMotionListener;
import freemind.view.mindmapview.MainView;
import freemind.view.mindmapview.NodeView;

public abstract class ViewControllerAdapter extends ControllerAdapter {

	public CommonToggleFoldedAction toggleFolded = null;
	public CommonToggleChildrenFoldedAction toggleChildrenFolded = null;
	public FindAction find = null;
	public FindNextAction findNext = null;

	public ViewControllerAdapter(Mode mode) {
		super(mode);
		toggleFolded = new CommonToggleFoldedAction(this);
		toggleChildrenFolded = new CommonToggleChildrenFoldedAction(this);
		find = new FindAction(this);
		findNext = new FindNextAction(this, find);
	}

	public void doubleClick(MouseEvent e) {
	}

	public void plainClick(MouseEvent e) {
	}

	public void setFolded(MindMapNode node, boolean folded) {
		if (node == null)
			throw new IllegalArgumentException("setFolded was called with a null node.");
		if (node.isRoot() && folded) {
			return;
		}
		if (node.isFolded() != folded) {
			node.setFolded(folded);
			nodeStructureChanged(node);
		}
	}

	public void startupController() {
		super.startupController();
		getNodeMouseMotionListener().register(new CommonNodeMouseMotionListener(this));
		getMapMouseMotionListener().register(new CommonMouseMotionManager(this));
		getNodeKeyListener().register(new CommonNodeKeyListener(this, (e, addNew, editLong) -> {}));

	}

	public void shutdownController() {
		super.shutdownController();
		getNodeMouseMotionListener().deregister();
		getMapMouseMotionListener().deregister();
		getNodeKeyListener().deregister();
	}

	protected void setAllActions(boolean enabled) {
		super.setAllActions(enabled);
		find.setEnabled(enabled);
		findNext.setEnabled(enabled);
		toggleFolded.setEnabled(enabled);
		toggleChildrenFolded.setEnabled(enabled);
	}

	@Override
	public void paste(MindMapNode pNode, MindMapNode pParent) {
	}

	@Override
	public NodeHook createNodeHook(String pLoadName, MindMapNode pNode) {
		return new PermanentNodeHookSubstituteUnknown(pLoadName);
	}
}
