package newChanges.nodeWrapper;

import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import newChanges.nodeData.ANSManager;
import newChanges.nodeData.NodeConverter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NodeWrapper implements MindMapNodeExt {

    /*
        wraps around & extends MindMapNodes
     */

    private static HashMap<NodeAdapter, NodeWrapper> wrapperMap = new HashMap<>();

    private NodeAdapter nodeAdapter;
    private MindMapController mindMapController;
    private boolean resource_flag;

    public NodeWrapper(NodeAdapter adapter, MindMapController mindMapController){
        this.nodeAdapter = adapter;
        this.mindMapController = mindMapController;
    }

    public MindMapController getController(){
        return this.mindMapController;
    }

    public NodeAdapter getNodeAdapter(){
        return this.nodeAdapter;
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
            NodeConverter.addToRootData(this.nodeAdapter.getText());
        }else{
            this.nodeAdapter.setColor(Color.BLACK);
            this.nodeAdapter.setUnderlined(false);
            this.nodeAdapter.setBold(false);
            NodeConverter.removeFromRootData(this.nodeAdapter.getText());
        }
    }

    public static void register(NodeWrapper nodeWrapper){
        System.out.println("Wrapped new node "+nodeWrapper.hashCode());
        wrapperMap.put(nodeWrapper.getNodeAdapter(), nodeWrapper);
        ANSManager.setLastCreated(nodeWrapper);
    }

    public static void remove(NodeWrapper nodeWrapper){
        System.out.println("Unwrapped node "+nodeWrapper.hashCode());
        wrapperMap.remove(nodeWrapper.getNodeAdapter());
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

    public static NodeWrapper getByTitle(String title){
        for(Map.Entry<NodeAdapter, NodeWrapper> entry : wrapperMap.entrySet()) {
            if(entry.getKey().getText().equals(title)){
                return entry.getValue();
            }
        }
        return null;
    }
}
