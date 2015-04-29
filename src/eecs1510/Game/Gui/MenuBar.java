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
 *
 * A top menu bar, inspired by Gtk+'s HeaderBar
 */
public class MenuBar extends ToolBar {

    /**
     *
     * A convenience method to create a button with the specified image, tooltip, and event handler
     *
     * @param resourcePath  the path to the icon to use for the button
     * @param tooltip       the tooltip text (if applicable)
     * @param eventHandler  the event handler to assign to the button (if applicable)
     * @return a new <code>Button</code> with the specified properties
     */
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

        this.setPrefHeight(40);

        Button loadGame = createButton("res/icons/ic_folder_open_black_24dp.png", "Open Menu", (e) -> {
            // Try to load a game
            boolean result = controller.getGameController().startGameFromFile("2048.dat");
            if(!result){
                controller.getBoardRenderer().displayNotification("Error Loading Game", 1, NotificationType.ERROR);
            }else{
                controller.getBoardRenderer().updateView(null);
            }
        });

        Button saveGame = createButton("res/icons/ic_save_black_24dp.png", "Save Game", (e) -> {
            // Try to save a game
            boolean result = controller.getGameController().saveGame("2048.dat");
            if(result){
                controller.getBoardRenderer().displayNotification("Game Saved", 1, NotificationType.INFO);
            }else{
                controller.getBoardRenderer().displayNotification("Error Saving Game", 1, NotificationType.ERROR);
            }
        });

        Button newGame  = createButton("res/icons/ic_add_box_black_24dp.png", "New Game", (e) -> {
            //FIXME: Prompt to save
            // Start a new game and update the Board View
            controller.getGameController().startNewGame();
            controller.getBoardRenderer().updateView(null);
        });

        // A spacer to center the labels
        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Label turnsLabel = new Label("Turns:");
        Label turns = new Label("0");
        Label scoreLabel = new Label("Score:");
        Label score = new Label("0");
        Label bestLabel = new Label("Best:");
        Label best = new Label("0");

        // Bind the labels to the stats manager's properties
        turns.textProperty().bind(Bindings.convert(controller.getGameController().getStatsManager().turnCountProperty()));
        score.textProperty().bind(Bindings.convert(controller.getGameController().getStatsManager().scorePropertyProperty()));
        best.textProperty().bind(Bindings.convert(controller.getGameController().getStatsManager().highScorePropertyProperty()));

        // Another spacer to center the labels
        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Button undo = createButton("res/icons/ic_undo_black_24dp.png", "Undo Last Move", (e) -> {
            // Attempt to undo the most recent move
            boolean undone = controller.getGameController().undoMove();
            if(!undone){
                controller.getBoardRenderer().displayNotification("Can't undo move!", 2, NotificationType.ERROR);
            }
        });

        Button help = createButton("res/icons/ic_help_black_24dp.png", "Help", (e) -> {
            // Display the help dialog
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

        // Add the event handler to the menu itself and to each child
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
