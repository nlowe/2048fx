package eecs1510.Game.Gui;

import eecs1510.Game.GameController;
import eecs1510.Game.Gui.Notification.NotificationType;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Created by nathan on 4/7/15
 *
 * The main class and application for 2048fx
 */
public class MainWindow extends Application
{

    /** The Game Controller: Responsible for the actual game logic */
    private GameController controller;
    /** The Key Manager for the GUI: Handles key events */
    private KeyManager keyManager;
    /** The Board View: Displays the board presented by the game controller */
    private BoardView board;
    /** The primary stage associated with this application. Used to set the modality on the help dialog */
    private Stage primaryStage;

    /** The file that the game was most recently saved to or loaded from */
    private File saveGameFile = null;

    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Cache the primary stage for modal dialogs
        this.primaryStage = primaryStage;

        // Setup the controllers
        controller = new GameController(this);
        keyManager = new KeyManager(this);

        saveGameFile = new File(controller.getLastGamePath());

        // Reset the ignore flag for new games
        controller.onMoveComplete((move) -> {
            if(controller.getStatsManager().isNewGame()){
                keyManager.setIgnoreEvents(false);
            }
        });

        // Layout Components
        BorderPane root = new BorderPane();
        MenuBar menu = new MenuBar(this);
        root.setTop(menu);

        board = new BoardView(this);
        root.setCenter(board);

        // Create the scene at the needed size
        // TODO: Figure out why this fudge factor of 6 is needed for the height on linux
        Scene gameScene = new Scene(root, 618, menu.getPrefHeight()+618+6);

        // Setup the theme
        gameScene.getStylesheets().add("eecs1510/Game/Gui/res/theme.css");

        // Always save the high score when closing down
        primaryStage.setOnCloseRequest(request -> {
            shutdownGame();
        });

        // Try to resume the most recently saved game
        if(saveGameFile.exists())
        {
            tryStartSavedGame(true);
        }

        // Add the scene to the stage, make the stage not resizeable, set the title, and show
        primaryStage.setResizable(false);
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("2048fx");
        primaryStage.show();
    }

    /**
     * Gracefully shuts down the game, saving the high score and most recently saved game path
     * to disk
     */
    public void shutdownGame()
    {
        controller.saveHighScore();
        Platform.exit();
    }

    /**
     * Tries to load a saved game from the file '2048.dat' in the current directory
     */
    public void tryStartSavedGame(boolean usePreviousGame)
    {
        File f = saveGameFile;
        if(!usePreviousGame || saveGameFile == null || !saveGameFile.exists())
        {
            // We don't yet have a file to load, have the user pick one
            FileChooser dialog = new FileChooser();
            dialog.setInitialDirectory(new File(System.getProperty("user.home")));
            dialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("2048 Files", "*.dat"));
            f = dialog.showOpenDialog(primaryStage);

            // If the user cancelled the dialog, we can't load the game
            if(f == null) return;
        }

        // Try to load a game from the specified file
        boolean result = controller.startGameFromFile(f.getPath());
        if(!result)
        {
            board.displayNotification("Error Loading Game", 1, NotificationType.ERROR, false);
            saveGameFile = null;
        } else {
            saveGameFile = f;
            board.updateView(null);
        }
    }

    /**
     * Tries to save the current game to the file '2048.dat' in the current directory
     */
    public void trySaveGame(boolean forceSaveAs)
    {
        if(forceSaveAs || saveGameFile == null || !saveGameFile.exists())
        {
            // If we don't have a file to save to, have the user pick one

            FileChooser dialog = new FileChooser();
            dialog.setInitialDirectory(new File(System.getProperty("user.home")));
            dialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("2048 Files", "*.dat"));
            dialog.setTitle("Save Game");

            File f = dialog.showSaveDialog(primaryStage);

            // If the user picked a file, update the path to save the game to, otherwise abort
            if(f != null)
            {
                saveGameFile = f;
            } else {
                return;
            }
        }

        // If the file is missing the '.dat' extension, add it
        if(!saveGameFile.getName().toLowerCase().endsWith(".dat"))
        {
            saveGameFile = new File(saveGameFile.getPath() + ".dat");
        }

        // Attempt to save the game
        boolean result = controller.saveGame(saveGameFile.getPath());
        if(result)
        {
            board.displayNotification("Game Saved", 1, NotificationType.INFO, false);
        } else {
            board.displayNotification("Error Saving Game", 1, NotificationType.ERROR, false);
        }
    }

    /**
     * Displays a modal help dialog for the game
     */
    public void showHelpDialog()
    {
        new HelpDialog(this).show();
    }

    public GameController getGameController()
    {
        return controller;
    }

    public BoardView getBoardRenderer()
    {
        return board;
    }

    public KeyManager getKeyManager()
    {
        return keyManager;
    }

    public Stage getPrimaryStage()
    {
        return primaryStage;
    }

}
