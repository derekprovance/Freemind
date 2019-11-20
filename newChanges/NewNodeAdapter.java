package newChanges;

import freemind.modes.MindMap;
import freemind.modes.NodeAdapter;

public abstract class NewNodeAdapter extends NodeAdapter implements NewMindMapNode {

    protected NewNodeAdapter(Object userObject, MindMap pMap) {
        super(userObject, pMap);
    }

    private boolean resourceFlag = false;


    @Override
    public boolean getResourceFlag(){
        return resourceFlag;
    }

    @Override
    public void setResourceFlag(boolean value){
        this.resourceFlag = value;
    }
}
