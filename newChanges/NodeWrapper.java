package newChanges;

import freemind.modes.NodeAdapter;

import java.awt.*;
import java.util.HashMap;

public class NodeWrapper implements MindMapNodeExt{

    /*
        wraps around & extends MindMapNodes
     */

    private static HashMap<NodeAdapter, NodeWrapper> wrapperMap = new HashMap<>();

    private NodeAdapter nodeAdapter;
    private boolean resource_flag;

    public NodeWrapper(NodeAdapter adapter){
        this.nodeAdapter = adapter;
    }

    @Override
    public boolean getResourceFlag() {
        return resource_flag;
    }

    @Override
    public void setResourceFlag(boolean value) {
        this.resource_flag = value;
        if(this.resource_flag){
            // highlight as a resource
            this.nodeAdapter.setColor(Color.MAGENTA);
            this.nodeAdapter.setUnderlined(true);
            this.nodeAdapter.setBold(true);
            this.nodeAdapter.setNoteText(">> RESOURCE \n"+this.nodeAdapter.getText());
            ANSManager.setLastResourceNode(this);
        }else{
            this.nodeAdapter.setColor(Color.BLACK);
            this.nodeAdapter.setUnderlined(false);
            this.nodeAdapter.setBold(false);
            this.nodeAdapter.setNoteText(this.nodeAdapter.getText().replace(">> RESOURCE\n",""));
            ANSManager.setLastResourceNode(null);
        }
    }

    public NodeAdapter getNodeAdapter(){
        return this.nodeAdapter;
    }

    public static void register(NodeWrapper nodeWrapper){
        System.out.println("Wrapped new node "+nodeWrapper.hashCode());
        wrapperMap.put(nodeWrapper.getNodeAdapter(), nodeWrapper);
        ANSManager.setLastCreated(nodeWrapper);
    }

    public static NodeWrapper get(NodeAdapter nodeAdapter){
        if(wrapperMap.containsKey(nodeAdapter)){
            return wrapperMap.get(nodeAdapter);
        }
        return null;
    }

    public static HashMap<NodeAdapter,NodeWrapper> getAll(){
        return wrapperMap;
    }
}
