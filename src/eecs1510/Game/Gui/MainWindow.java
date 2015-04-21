package eecs1510.Game.Gui;

import eecs1510.Game.GameController;
import eecs1510.Game.GameRules;
import eecs1510.Game.Gui.Screen.*;
import eecs1510.Game.Randomizer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Created by nathan on 4/7/15
 */
public class MainWindow extends Application {

    private Scene gameScene;
    private MenuBar menu;
    private BorderPane root;
    private GameController controller;
    private KeyBindings keyBindings;

    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new GameController();
        keyBindings = new KeyBindings(this);

        root = new BorderPane();
        menu = new MenuBar(this);
        root.setTop(menu);

        root.setCenter(new WelcomeScreen(this));

        gameScene = new Scene(root, 800, 600);

        gameScene.getStylesheets().add("eecs1510/Game/Gui/res/theme.css");

        primaryStage.setMinHeight(480);
        primaryStage.setMinWidth(640);
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("2048fx");
        primaryStage.show();
    }

    public Node getDisplayedScreen(){
        return root.getCenter();
    }

    public void displayNewGameScreen(){
        if(!(root.getCenter() instanceof NewGameScreen)){
            root.setCenter(new NewGameScreen(this));
        }
    }

    public void displayMainMenuScreen(){
        if(!(root.getCenter() instanceof WelcomeScreen)){
            root.setCenter(new WelcomeScreen(this));
        }
    }

    public void displayRaceScreen(){
        if(!(root.getCenter() instanceof RaceScreen)){
            root.setCenter(new RaceScreen(this));
        }
    }

    public void displayHelpScreen(){
        if(!(root.getCenter() instanceof HelpScreen)){
            root.setCenter(new HelpScreen(this));
        }
    }

    public void play(GameRules rules) {
        if(!(root.getCenter() instanceof GameScreen)){
            try {
                controller.startGame(rules);
                root.setCenter(new GameScreen(this)); //TODO: Persist
            } catch(Randomizer.InvalidSeedException e) {
                e.printStackTrace();
            }
        }
    }

    public GameController getGameController() {
        return controller;
    }

    public KeyBindings getKeyBindings() {
        return keyBindings;
    }
}
