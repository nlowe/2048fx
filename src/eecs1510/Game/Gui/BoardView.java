package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import eecs1510.Game.GameController;
import eecs1510.Game.Rules;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;

/**
 * Created by nathan on 4/11/15
 */
public class BoardView extends Pane{

    private final MainWindow controller;
    private ArrayList<CellView> cellViews = new ArrayList<>();

    public BoardView(MainWindow controller){
        super();

        getStyleClass().add("board");

        //Computed max size (4 cells of 132 pixels each + 5 boarders at 18 pixels each)
        setMaxSize(618, 627);

        this.controller = controller;

        // Add Dividers
        for(int i=0; i<=Rules.BOARD_SIZE; i++){
            Line row = new Line(9, (i * 132) + (i*18) + 9, 609, (i * 132) + (i*18) + 9);
            row.setStrokeWidth(18);
            row.getStyleClass().add("board-line");

            Line col = new Line((i * 132) + (i*18) + 9, 9, (i * 132) + (i*18) + 9, 609);
            col.setStrokeWidth(18);
            col.getStyleClass().add("board-line");

            getChildren().addAll(row, col);
        }

        // Events
        setOnKeyReleased(e -> {
            if(controller.getKeyManager().handleKey(e)){
                e.consume();
            }
        });

        updateView();
        requestFocus();

        controller.getGameController().onMoveComplete((move) -> updateView());
    }

    private void updateView() {
        System.out.println("Updating Cell Views");

        GameController game = controller.getGameController();

        getChildren().removeAll(cellViews);
        cellViews.clear();

        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col=0; col < Rules.BOARD_SIZE; col++){
                Cell c = game.cellAt(row, col);
                if(c == null) continue;

                CellView view = new CellView(c);
                view.setLayoutX((col * 132) + (col*18) + 18);
                view.setLayoutY((row * 132) + (row*18) + 18);
                view.setMaxSize(132, 132);
                view.setMinSize(132, 132);
                cellViews.add(view);
            }
        }

        getChildren().addAll(cellViews);
        layoutChildren();
    }


}
