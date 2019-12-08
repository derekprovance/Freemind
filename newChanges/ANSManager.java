package newChanges;

public class ANSManager {

    /*
        containing information about latest state changes
     */

    private static NodeWrapper lastCreated;
    private static NodeWrapper lastSelected;

    // catching wrapper from basic events

    public static void setLastCreated(NodeWrapper nodeWrapper){
        if(nodeWrapper != null){
            System.out.println("LastNodeCreated set to "+nodeWrapper.hashCode());
            lastCreated = nodeWrapper;
        }else{
            System.out.println("Could not update LastNodeCreated");
        }
    }

    public static void setLastSelected(NodeWrapper nodeWrapper){
        if(nodeWrapper != null){
            System.out.println("Selection set to "+nodeWrapper.hashCode());
            lastSelected = nodeWrapper;
        }else{
            System.out.println("Could not update selection");
        }
    }

    public static NodeWrapper getLastNodeCreated(){
        return lastCreated;
    }

    public static NodeWrapper getLastNodeSelected(){
        return lastSelected;
    }
}
