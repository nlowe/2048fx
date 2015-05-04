package eecs1510.Game.Gui;

import eecs1510.Game.GameController;
import eecs1510.Game.Gui.Notification.NotificationType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by nathan on 4/7/15
 *
 * The main class and application for 2048fx
 */
public class MainWindow extends Application {

    /** The Game Controller: Responsible for the actual game logic */
    private GameController controller;
    /** The Key Manager for the GUI: Handles key events */
    private KeyManager keyManager;
    /** The Board View: Displays the board presented by the game controller */
    private BoardView board;
    /** The primary stage associated with this application. Used to set the modality on the help dialog */
    private Stage primaryStage;

    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Setup the controllers
        controller = new GameController(this);
        keyManager = new KeyManager(this);

        // Layout Components
        BorderPane root = new BorderPane();
        MenuBar menu = new MenuBar(this);
        root.setTop(menu);

        board = new BoardView(this);
        root.setCenter(board);

        // Create the scene at the needed size
        // TODO: Figure out why this fudge factor of 6 is needed for the height
        Scene gameScene = new Scene(root, 618, menu.getPrefHeight()+618+6);

        // Setup the theme
        gameScene.getStylesheets().add("eecs1510/Game/Gui/res/theme.css");

        // Always save the high score when closing down
        primaryStage.setOnCloseRequest(request -> {
            shutdownGame();
        });

        // Add the scene to the stage, make the stage not resizeable, set the title, and show
        primaryStage.setResizable(false);
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("2048fx");
        primaryStage.show();
    }

    public void shutdownGame(){
        controller.saveHighScore();
        Platform.exit();
    }

    /**
     * Tries to load a saved game from the file '2048.dat' in the current directory
     */
    public void tryStartSavedGame(){
        boolean result = controller.startGameFromFile("2048.dat");
        if(!result){
            board.displayNotification("Error Loading Game", 1, NotificationType.ERROR);
        }else{
            board.updateView(null);
        }
    }

    /**
     * Tries to save the current game to the file '2048.dat' in the current directory
     */
    public void trySaveGame(){
        // Try to save a game
        boolean result = controller.saveGame("2048.dat");
        if(result){
            board.displayNotification("Game Saved", 1, NotificationType.INFO);
        }else{
            board.displayNotification("Error Saving Game", 1, NotificationType.ERROR);
        }
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
