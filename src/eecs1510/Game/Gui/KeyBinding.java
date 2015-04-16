package eecs1510.Game.Gui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created by nathan on 4/9/15
 */
public class KeyBinding {

    private final KeyCode primaryKey;
    private final KeyCode secondaryKey;

    public KeyBinding(KeyCode primary, KeyCode secondary){
        primaryKey = primary;
        secondaryKey = secondary;
    }

    public boolean match(KeyEvent e){
        return e.getCode().equals(primaryKey) || e.getCode().equals(secondaryKey);
    }

    public KeyCode getPrimaryKey() {
        return primaryKey;
    }

    public KeyCode getSecondaryKey() {
        return secondaryKey;
    }
}
