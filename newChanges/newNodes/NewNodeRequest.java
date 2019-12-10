package newChanges.newNodes;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

public class NewNodeRequest {

    private MindMapController controller;
    private MindMapNode parent;
    private String title;
    private String description;

    public NewNodeRequest(MindMapController controller, MindMapNode parent, String title, String description){
        this.controller = controller;
        this.parent = parent;
        this.title = title;
        this.description = description;
    }

    public MindMapController getController() {
        return controller;
    }

    public MindMapNode getParent() {
        return parent;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}
