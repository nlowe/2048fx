package eecs1510.Game.Gui.Notification;

import eecs1510.Game.Gui.MainWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * Created by nathan on 5/1/15
 *
 * A simple overlay for Winning or Game Over notifications
 */
public class BoardOverlay extends GridPane{

    public BoardOverlay(MainWindow controller){
        getStyleClass().add("overlay");

        setMinSize(controller.getBoardRenderer().getWidth(), controller.getBoardRenderer().getHeight());
        setMaxSize(controller.getBoardRenderer().getWidth(), controller.getBoardRenderer().getHeight());
        setPadding(new Insets(50, 50, 50, 50));
        setVgap(15);
        setHgap(15);
        setAlignment(Pos.CENTER);

    }
}
