package newChanges.newNodes;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

public class NewNodeRequest {

    /*
        contains the request to create a new node
     */

    private MindMapController controller;
    private MindMapNode parent;
    private String title;
    private String description;
    private boolean resourceFlag;

    public NewNodeRequest(MindMapController controller, MindMapNode parent, String title, String description, boolean resourceFlag){
        this.controller = controller;
        this.parent = parent;
        this.title = title;
        this.description = description;
        this.resourceFlag = resourceFlag;
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

    public boolean getResourceFlag() {
        return resourceFlag;
    }
}
