package newChanges;

import freemind.modes.MindMap;

public class NewMindMapNodeModel extends NewNodeAdapter {

    // nodes seem to be created from MindMapNodeModel class -> MindMapController.DefaultMindMapNodeCreator

    protected NewMindMapNodeModel(Object userObject, MindMap pMap) {
        super(userObject, pMap);
    }

    @Override
    public boolean isWriteable() {
        return false;
    }
}
