package eecs1510.Game.Gui.Notification;

import eecs1510.Game.Gui.MainWindow;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;

/**
 * Created by nathan on 5/1/15
 */
public class GameWonOverlay extends BoardOverlay{

    private EventHandler<ActionEvent> keepPlayingHandler = null;

    public GameWonOverlay(MainWindow controller){
        super(controller);
        getStyleClass().add("won");

        Label title = new Label("You Win!");
        title.getStyleClass().add("title");
        add(title, 0, 0, 2, 1);
        setHalignment(title, HPos.CENTER);

        add(new Label("Total Score:"), 0, 1);
        add(new Label(String.valueOf(controller.getGameController().getStatsManager().getScore())), 1, 1);

        add(new Label("Turns:"), 0, 2);
        add(new Label(String.valueOf(controller.getGameController().getStatsManager().getTurnCount())), 1, 2);

        add(new Label("Total merged Cells:"), 0, 3);
        add(new Label(String.valueOf(controller.getGameController().getStatsManager().getTotalMerged())), 1, 3);

        Button keepPlaying = new Button("Keep Playing");
        keepPlaying.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        keepPlaying.setMinHeight(60);
        keepPlaying.setOnAction((e) -> {
            if (keepPlayingHandler != null) {
                keepPlayingHandler.handle(e);
            }
        });
        add(keepPlaying, 0, 4);
        setFillWidth(keepPlaying, true);
        setHgrow(keepPlaying, Priority.ALWAYS);

        Button quit = new Button("Quit");
        quit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        quit.setMinHeight(60);
        quit.setOnAction((e) -> {
            Platform.exit();
        });
        add(quit, 1, 4);
        setFillWidth(keepPlaying, true);
        setHgrow(quit, Priority.ALWAYS);
    }

    public void setOnContinue(EventHandler<ActionEvent> handler){
        keepPlayingHandler = handler;
    }
}
