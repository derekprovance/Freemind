package newChanges;

import freemind.modes.NodeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ANSManager {

    /*
        containing information about latest state changes
     */

    private static NodeWrapper lastCreated;
    private static NodeWrapper lastSelected;
    private static NodeWrapper lastResourceNode;


    // catching wrapper from basic events

    public static void setLastCreated(NodeWrapper nodeWrapper){
        System.out.println("LastNodeCreated set to "+nodeWrapper);
        lastCreated = nodeWrapper;
    }

    public static void setLastSelected(NodeWrapper nodeWrapper){
        System.out.println("Selection set to "+nodeWrapper);
        lastSelected = nodeWrapper;
    }

    public static void setLastResourceNode(NodeWrapper nodeWrapper){
        System.out.println("LastResourceNode set to "+nodeWrapper);
        lastResourceNode = nodeWrapper;
    }

    public static NodeWrapper getLastNodeCreated(){
        return lastCreated;
    }

    public static NodeWrapper getLastNodeSelected(){
        return lastSelected;
    }

    public static NodeWrapper getLastResourceNode(){
        return lastResourceNode;
    }


    // some more advanced things

    private static List<NodeWrapper> getAllMarkedAsResource(){
        List<NodeWrapper> list = new ArrayList<>();
        for(Map.Entry<NodeAdapter, NodeWrapper> entry : NodeWrapper.getAll().entrySet()){
            if(entry.getValue().getResourceFlag()){
                list.add(entry.getValue());
            }
        }
        return list;
    }
}
