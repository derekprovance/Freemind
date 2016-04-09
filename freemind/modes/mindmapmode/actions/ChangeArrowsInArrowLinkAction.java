package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;

import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.modes.mindmapmode.MindMapController;

public class ChangeArrowsInArrowLinkAction extends MindmapAction {
	private MindMapArrowLinkModel arrowLink;

	private boolean hasStartArrow;
	private boolean hasEndArrow;

	private final MindMapController controller;

	public ChangeArrowsInArrowLinkAction(MindMapController controller, String text, String iconPath, MindMapArrowLinkModel arrowLink, boolean hasStartArrow, boolean hasEndArrow) {
		super("change_arrows_in_arrow_link", iconPath, controller);
		this.controller = controller;
		this.arrowLink = arrowLink;
		this.hasStartArrow = hasStartArrow;
		this.hasEndArrow = hasEndArrow;
	}

	public void actionPerformed(ActionEvent e) {
		controller.getActorFactory().getChangeArrowsInArrowLinkActor()
				.changeArrowsOfArrowLink(arrowLink, hasStartArrow, hasEndArrow);
	}

}
