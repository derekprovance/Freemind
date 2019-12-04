package newChanges;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ExtKeyListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        System.out.println(keyEvent.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        System.out.println(keyEvent.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        System.out.println(keyEvent.getKeyChar());
    }
}
