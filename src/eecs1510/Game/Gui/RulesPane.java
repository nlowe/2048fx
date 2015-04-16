package eecs1510.Game.Gui;

import eecs1510.Game.GameRules;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Created by nathan on 4/9/15
 */
public class RulesPane extends GridPane{

    private GameRules rules;

    public RulesPane(){
        this(new GameRules());
    }

    public RulesPane(GameRules rules){
        super();

        this.rules = rules;

        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setHgap(10);

        ColumnConstraints left = new ColumnConstraints();
        left.setHalignment(HPos.RIGHT);
        left.setHgrow(Priority.NEVER);

        ColumnConstraints right = new ColumnConstraints();
        right.setHalignment(HPos.LEFT);
        right.setHgrow(Priority.ALWAYS);

        getColumnConstraints().addAll(left, right);

        add(new Label("Winning Score:"), 0, 0);
        Spinner<Integer> scoreSpinner = new Spinner<>();
        scoreSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, Integer.MAX_VALUE, rules.getWinningScore(), 1));
        rules.winningScoreProperty().bind(scoreSpinner.valueProperty());
        add(scoreSpinner, 1, 0);

        add(new Label("Board Size:"), 0, 1);
        Spinner<Integer> sizeSpinner = new Spinner<>();
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 64, rules.getBoardSize(), 1));
        rules.boardSizeProperty().bind(sizeSpinner.valueProperty());
        add(sizeSpinner, 1, 1);

        add(new Label("Maximum Undo Depth:"), 0, 2);
        Spinner<Integer> undoSpinner = new Spinner<>();
        undoSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, rules.getMaxUndoSize(), 1));
        rules.maxUndoSizeProperty().bind(undoSpinner.valueProperty());
        add(undoSpinner, 1, 2);

        add(new Label("Tiles Added per Turn:"), 0, 3);
        Spinner<Integer> tilesSpinner = new Spinner<>();
        tilesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, rules.getTilesAddedPerTurn(), 1)); //TODO: Is this a sensible max?
        rules.tilesAddedPerTurnProperty().bind(tilesSpinner.valueProperty());
        add(tilesSpinner, 1, 3);

    }

}
