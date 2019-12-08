package newChanges;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExtKeyListener{

    private Set<Integer> keysPressed = new HashSet<Integer>();
    private AtomicBoolean hotkeyBlocked = new AtomicBoolean(false);

    public ExtKeyListener(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {

                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        switch(e.getID()){
                            case KeyEvent.KEY_PRESSED:
                                keysPressed.add(e.getKeyCode());
                                break;
                            case KeyEvent.KEY_RELEASED:
                                keysPressed.remove(e.getKeyCode());
                                break;
                        }
                        onKeyEvent();
                        return false;
                    }
                });
    }

    private void onKeyEvent(){
        if(keysPressed.contains(17) && !hotkeyBlocked.get()){// CTRL / STRG

            if(keysPressed.contains(82) && !keysPressed.contains(16)){   // r
                System.out.println("Hotkey detected! Blocking for 2 seconds.");
                hotkeyBlocked.set(true);
                if(ANSManager.getLastNodeSelected() != null){
                    ANSManager.getLastNodeSelected().setResourceFlag(!ANSManager.getLastNodeSelected().getResourceFlag());
                }else{
                    System.out.println("IsNull - Ignore");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            TimeUnit.SECONDS.sleep(2);
                        }catch (Exception e){}
                        hotkeyBlocked.set(false);
                    }
                }).start();
            }
            if(keysPressed.contains(82) && keysPressed.contains(16)){
                System.out.println("Hotkey detected! Blocking for 2 seconds.");
                hotkeyBlocked.set(true);
                // create from list
                NodeConverter.updateNodesFromRootData();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            TimeUnit.SECONDS.sleep(2);
                        }catch (Exception e){}
                        hotkeyBlocked.set(false);
                    }
                }).start();
            }
        }
    }
}
