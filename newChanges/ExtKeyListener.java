package newChanges;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class ExtKeyListener{

    private Set<Character> keysPressed = new HashSet<Character>();

    public ExtKeyListener(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {

                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        switch(e.getID()){
                            case KeyEvent.KEY_PRESSED:
                                if(!keysPressed.contains(e.getKeyChar())){
                                    keysPressed.add(e.getKeyChar());
                                }
                                break;
                            case KeyEvent.KEY_RELEASED:
                                if(keysPressed.contains(e.getKeyChar())){
                                    keysPressed.remove(e.getKeyChar());
                                }
                                break;
                        }
                        return false;
                    }
                });
    }

    public Set<Character> getKeysPressed() {
        return keysPressed;
    }
}
