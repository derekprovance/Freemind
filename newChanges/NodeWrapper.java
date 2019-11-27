package newChanges;

import freemind.modes.NodeAdapter;

import java.util.HashMap;

public class NodeWrapper implements MindMapNodeExt{

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
    }

    public NodeAdapter getNodeAdapter(){
        return this.nodeAdapter;
    }

    public static void register(NodeWrapper nodeWrapper){
        wrapperMap.put(nodeWrapper.getNodeAdapter(), nodeWrapper);
    }

    public static NodeWrapper get(NodeAdapter nodeAdapter){
        if(wrapperMap.containsKey(nodeAdapter)){
            return wrapperMap.get(nodeAdapter);
        }
        return null;
    }
}
