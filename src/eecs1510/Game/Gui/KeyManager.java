package eecs1510.Game.Gui;

import eecs1510.Game.Direction;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created by nathan on 4/9/15
 */
public class KeyManager {

    private final MainWindow controller;

    public KeyManager(MainWindow controller){
        this.controller = controller;
    }

    public boolean handleKey(KeyEvent e){
        if(e.isControlDown() && e.getCode().equals(KeyCode.Z)){
            controller.getGameController().undoMove();
            return true;
        }else if(e.isAltDown()){
            switch(e.getCode()){
                case S: return true; //TODO: FIXME: Save Game
                case L: return true; //TODO: FIXME: Load Game
                case H: controller.showHelpDialog(); return true;
                case K: Platform.exit();
                default: return false;
            }
        }else{
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