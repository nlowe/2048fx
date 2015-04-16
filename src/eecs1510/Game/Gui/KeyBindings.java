package eecs1510.Game.Gui;

import eecs1510.Game.Direction;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;

/**
 * Created by nathan on 4/9/15
 */
public class KeyBindings {

    public static final int GAME_MOVE_NORTH = 0;
    public static final int GAME_MOVE_EAST = 1;
    public static final int GAME_MOVE_SOUTH = 2;
    public static final int GAME_MOVE_WEST = 3;
    public static final int GAME_UNDO = 4;
    public static final int GAME_REDO = 5;

    private final MainWindow controller;

    private KeyBinding[] bindings;

    public KeyBindings(MainWindow controller){
        this.controller = controller;
        bindings = new KeyBinding[]{
                new KeyBinding(KeyCode.W, KeyCode.UP),
                new KeyBinding(KeyCode.D, KeyCode.RIGHT),
                new KeyBinding(KeyCode.S, KeyCode.DOWN),
                new KeyBinding(KeyCode.A, KeyCode.LEFT),
                new KeyBinding(KeyCode.Z, null),
                new KeyBinding(KeyCode.Y, null)
        };
    }

    public boolean handleKey(KeyEvent e){
        System.out.println("Key Event: " + e.getText());
        for(int i=0; i<bindings.length; i++){
            if(bindings[i].match(e)){
                switch(i){
                    case GAME_MOVE_NORTH:controller.getGameController().takeMove(Direction.NORTH);break;
                    case GAME_MOVE_EAST:controller.getGameController().takeMove(Direction.EAST);break;
                    case GAME_MOVE_SOUTH:controller.getGameController().takeMove(Direction.SOUTH);break;
                    case GAME_MOVE_WEST:controller.getGameController().takeMove(Direction.WEST);break;
                    case GAME_UNDO:break;
                    case GAME_REDO:break;
                }
            }
        }
        return false;
    }
}
