package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import eecs1510.Game.GameController;
import eecs1510.Game.Rules;
import javafx.scene.layout.Pane;

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

        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col=0; col < Rules.BOARD_SIZE; col++){
                Pane emptyCell = new Pane();
                emptyCell.getStyleClass().add("empty-cell-view");

                emptyCell.setLayoutX((col * 132) + (col * 18) + 18);
                emptyCell.setLayoutY((row * 132) + (row * 18) + 18);
                emptyCell.setMaxSize(132, 132);
                emptyCell.setMinSize(132, 132);

                getChildren().add(emptyCell);
            }
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
