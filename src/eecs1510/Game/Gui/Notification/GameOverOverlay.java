package eecs1510.Game.Gui.Notification;

import eecs1510.Game.Gui.MainWindow;
import javafx.geometry.HPos;
import javafx.scene.control.Label;

/**
 * Created by nathan on 5/1/15
 */
public class GameOverOverlay extends BoardOverlay
{

    public GameOverOverlay(MainWindow controller)
    {
        super(controller);
        getStyleClass().add("lost");

        Label title = new Label("Game Over!");
        title.getStyleClass().add("title");
        add(title, 0, 0, 2, 1);
        setHalignment(title, HPos.CENTER);

        add(new Label("Total Score:"), 0, 1);
        add(new Label(String.valueOf(controller.getGameController().getStatsManager().getScore())), 1, 1);

        add(new Label("Turns:"), 0, 2);
        add(new Label(String.valueOf(controller.getGameController().getStatsManager().getTurnCount())), 1, 2);

        add(new Label("Total merged Cells:"), 0, 3);
        add(new Label(String.valueOf(controller.getGameController().getStatsManager().getTotalMerged())), 1, 3);

        if(controller.getGameController().getStatsManager().wasNewHighScoreSet())
        {
            add(new Label("New High Score!"), 0, 4);
            add(new Label(String.valueOf(controller.getGameController().getStatsManager().getHighScore())), 1, 4);
        }
    }

}
