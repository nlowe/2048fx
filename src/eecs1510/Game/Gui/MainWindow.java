package eecs1510.Game.Gui;

import eecs1510.Game.GameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Created by nathan on 4/7/15
 */
public class MainWindow extends Application {

    /** The Game Controller: Responsible for the actual game logic */
    private GameController controller;
    /** The Key Manager for the GUI: Handles key events */
    private KeyManager keyManager;
    /** The Board View: Displays the board presented by the game controller */
    private BoardView board;
    private Stage primaryStage;

    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        controller = new GameController();
        keyManager = new KeyManager(this);

        BorderPane root = new BorderPane();
        MenuBar menu = new MenuBar(this);
        root.setTop(menu);

        board = new BoardView(this);
        root.setCenter(board);

        // Create the scene at the needed size
        // TODO: Figure out why this fudge factor of 6 is needed for the height
        Scene gameScene = new Scene(root, 618, menu.getPrefHeight()+618+6);

        gameScene.getStylesheets().add("eecs1510/Game/Gui/res/theme.css");

        primaryStage.setResizable(false);
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("2048fx");
        primaryStage.show();
    }

    public GameController getGameController() {
        return controller;
    }

    public BoardView getBoardRenderer() {
        return board;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showHelpDialog(){
        new HelpDialog(this).show();
    }
}
