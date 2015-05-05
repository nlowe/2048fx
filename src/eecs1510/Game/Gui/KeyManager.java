package eecs1510.Game.Gui;

import eecs1510.Game.Direction;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created by nathan on 4/9/15
 *
 * Handles key events from the views and passes them off to the appropriate controllers
 */
public class KeyManager {

    private final MainWindow controller;

    public KeyManager(MainWindow controller){
        this.controller = controller;
    }

    /**
     * Handles the specified key event. If the event is valid (used by the controller), it is consumed
     *
     * @param e the KeyEvent to process
     * @return true Iff the controller should accept the event
     */
    public boolean handleKey(KeyEvent e){
        if(e.isControlDown() && e.getCode().equals(KeyCode.Z)){
            // Ctrl+Z
            controller.getGameController().undoMove();
            return true;
        }else if(e.isAltDown()){
            // Special Keys
            switch(e.getCode()){
                case S: controller.trySaveGame(false); return true;
                case L: controller.tryStartSavedGame(false); return true;
                case H: controller.showHelpDialog(); return true;
                case K: controller.shutdownGame(); return true;
                default: return false;
            }
        }else{
            // Movement
            switch(e.getCode()){
                case UP:    controller.getGameController().takeMove(Direction.NORTH); return true;
                case RIGHT: controller.getGameController().takeMove(Direction.EAST);  return true;
                case DOWN:  controller.getGameController().takeMove(Direction.SOUTH); return true;
                case LEFT:  controller.getGameController().takeMove(Direction.WEST);  return true;
                default: return false;
            }
        }
    }
}