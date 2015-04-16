package eecs1510.Game.Gui.Screen;

import eecs1510.Game.Gui.BoardRenderer;
import eecs1510.Game.Gui.MainWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

/**
 * Created by nathan on 4/11/15
 */
public class GameScreen extends ControlledScreen {

    public GameScreen(MainWindow controller) {
        super(controller);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ColumnConstraints c = new ColumnConstraints();
        c.setFillWidth(true);
        c.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().add(c);

        RowConstraints r = new RowConstraints();
        r.setFillHeight(true);
        r.setVgrow(Priority.ALWAYS);

        grid.getRowConstraints().add(r);

        BoardRenderer board = new BoardRenderer(controller);

        grid.add(board, 0, 0);
        getChildren().add(grid);

        board.forceDraw();
        setAlignment(grid, Pos.CENTER);
        HBox.setHgrow(this, Priority.ALWAYS);
    }


}
