package newChanges;

import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.modes.mindmapmode.MindMapController;

public class NewMindMapController extends MindMapController {

    public NewMindMapController(Mode mode) {
        super(mode);
    }

    private NewNodeCreator myNewNodeCreator = null;

    public interface NewNodeCreator {
        NewMindMapNode createNode(Object userObject, MindMap map);
    }

    public class DefaultMindMapNodeCreator implements NewNodeCreator{
        public NewMindMapNode createNode(Object userObject, MindMap map) {
            return new NewMindMapNodeModel(userObject, map);
        }
    }

    public NewMindMapNode newNode(Object userObject, MindMap map) {
        if (myNewNodeCreator == null) {
            myNewNodeCreator = new DefaultMindMapNodeCreator();
        }

        return myNewNodeCreator.createNode(userObject, map);
    }
}
