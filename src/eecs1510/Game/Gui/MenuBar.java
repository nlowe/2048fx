package eecs1510.Game.Gui;

import javafx.beans.binding.Bindings;
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

    private Label turns;
    private Label score;
    private Label best;
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

        Button loadGame = createButton("res/icons/ic_folder_open_black_24dp.png", "Open Menu", (e) -> {
            controller.getBoardRenderer().displayNotification("Open Game Not Implemented!", 2, NotificationType.ERROR);
        });

        Button saveGame = createButton("res/icons/ic_save_black_24dp.png", "Save Game", (e) -> {
            controller.getBoardRenderer().displayNotification("Save Game Not Implemented!", 2, NotificationType.ERROR);
        });

        Button newGame  = createButton("res/icons/ic_add_box_black_24dp.png", "New Game", (e) -> {
            //FIXME: Prompt to save
            controller.getGameController().startNewGame();
            controller.getBoardRenderer().updateView(null);
        });

        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Label turnsLabel = new Label("Turns:");
        turns = new Label("0");
        Label scoreLabel = new Label("Score:");
        score = new Label("0");
        Label bestLabel = new Label("Best:");
        best = new Label("0");

        turns.textProperty().bind(Bindings.convert(controller.getGameController().getStatsManager().turnCountProperty()));
        score.textProperty().bind(Bindings.convert(controller.getGameController().getStatsManager().scoreProperty()));
        best.textProperty().bind(Bindings.convert(controller.getGameController().getStatsManager().highScoreProperty()));

        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Button undo = createButton("res/icons/ic_undo_black_24dp.png", "Undo Last Move", (e) -> {
            controller.getBoardRenderer().displayNotification("Undo Not Implemented!", 2, NotificationType.ERROR);
        });

        Button help = createButton("res/icons/ic_help_black_24dp.png", "Help", (e) -> {
            controller.showHelpDialog();
        });

        this.getItems().addAll(loadGame, saveGame, new Separator(Orientation.VERTICAL), newGame,
                leftSpacer, turnsLabel, turns, scoreLabel, score, bestLabel, best, rightSpacer,
                undo, new Separator(Orientation.VERTICAL), help
        );

        // Handle key events on the off chance that the menu is focused
        EventHandler<KeyEvent> keyHandler = (e) -> {
            if(controller.getKeyManager().handleKey(e)){
                e.consume();
            }
        };

        addEventFilter(KeyEvent.KEY_TYPED, keyHandler);
        getItems().stream().forEach((node) -> {
            node.addEventFilter(KeyEvent.KEY_TYPED, keyHandler);
            node.focusedProperty().addListener((observable, oldValue, newValue) -> {
                // The Game Board should always be focused
                controller.getBoardRenderer().requestFocus();
            });
        });
    }
}
