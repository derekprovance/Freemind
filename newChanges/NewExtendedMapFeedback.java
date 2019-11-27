package newChanges;

import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMap;

public interface NewExtendedMapFeedback extends ExtendedMapFeedback {

    NewMindMapNode newNode(Object pUserObject, MindMap pMap);
}
