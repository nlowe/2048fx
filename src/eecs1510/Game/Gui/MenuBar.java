package eecs1510.Game.Gui;

import eecs1510.Game.Gui.Screen.GameScreen;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * Created by nathan on 4/7/15
 */
public class MenuBar extends ToolBar {

    private Label score;
    private final MainWindow controller;

    private Button createButton(String resourcePath, String tooltip, EventHandler<ActionEvent> eventHandler){
        Button b = new Button("", new ImageView(new Image(MainWindow.class.getResourceAsStream(resourcePath))));

        if(tooltip != null){
            Tooltip t = new Tooltip();
            t.setText(tooltip);
            b.setTooltip(t);
        }

        if(eventHandler != null){
            b.setOnAction(eventHandler);
        }

        return b;
    }

    public MenuBar(MainWindow controller){
        super();

        this.controller = controller;

        this.setPrefHeight(40);

        Button menu = createButton("icons/ic_menu_black_24dp.png", "Open Menu", (e) -> {
            controller.displayMainMenuScreen();
        });

        Button saveGame = createButton("icons/ic_save_black_24dp.png", "Save Game", (e) -> {
            System.out.println("FIXME: TODO: Save Game");
        });
        saveGame.setDisable(true);

        Button newGame  = createButton("icons/ic_add_box_black_24dp.png", "New Game", (e) -> {
            controller.displayNewGameScreen();
        });

        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Label scoreLabel = new Label("Score:");
        score = new Label("0");

        controller.getGameController().onMoveComplete(MoveResult -> {
            //TODO: Update Score
        });

        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Button undo = createButton("icons/ic_undo_black_24dp.png", "Undo Last Move", (e) -> {
            System.out.println("FIXME: TODO: Undo");
        });
        undo.setDisable(true);

        Button redo = createButton("icons/ic_redo_black_24dp.png", "Redo Previously Undone Move", (e) -> {
            System.out.println("FIXME: TODO: Redo");
        });
        redo.setDisable(true);

        Button help = createButton("icons/ic_help_black_24dp.png", "Help", (e) -> {
            controller.displayHelpScreen();
        });

        Button prefs = createButton("icons/ic_settings_black_24dp.png", "Preferences", (e) -> {
            System.out.println("FIXME: TODO: Preferences");
        });

        this.getItems().addAll(menu, new Separator(Orientation.VERTICAL), saveGame, newGame,
                leftSpacer, scoreLabel, score, rightSpacer,
                undo, redo, new Separator(Orientation.VERTICAL), help, prefs
        );

        // Hijack key events if a game is being played
        EventHandler<KeyEvent> keyHandler = (e) -> {
            if(controller.getDisplayedScreen() instanceof GameScreen){
                if(controller.getKeyBindings().handleKey(e)){
                    e.consume();
                }
            }
        };

        getItems().stream().forEach((node) -> node.setOnKeyTyped(keyHandler));
        this.setOnKeyTyped(keyHandler);
    }
}
