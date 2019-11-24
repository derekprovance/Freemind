package newChanges;

import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.modes.mindmapmode.MindMapController;

public class NewMindMapController extends MindMapController {

    public NewMindMapController(Mode mode) {
        super(mode);
    }

    public interface NewNodeCreator {
        NewMindMapNode createNode(Object userObject, MindMap map);
    }

    public class DefaultMindMapNodeCreator implements NewNodeCreator{
        public NewMindMapNode createNode(Object userObject, MindMap map) {
            return new NewMindMapNodeModel(userObject, map);
        }
    }
}
