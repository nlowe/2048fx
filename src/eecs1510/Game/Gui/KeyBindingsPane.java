package eecs1510.Game.Gui;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Created by nathan on 4/9/15
 */
public class KeyBindingsPane extends GridPane {

    private final KeyBindings bindings;

    public KeyBindingsPane(KeyBindings bindings){
        this.bindings = bindings;

        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setHgap(10);

        ColumnConstraints labels = new ColumnConstraints();
        labels.setHalignment(HPos.RIGHT);
        labels.setHgrow(Priority.SOMETIMES);
        labels.setMinWidth(100); //TODO: Is there a better way to ensure that the labels have enough space?

        ColumnConstraints keys = new ColumnConstraints();
        keys.setHgrow(Priority.ALWAYS);
        keys.setFillWidth(true);

        getColumnConstraints().addAll(labels, keys, keys);

        //TODO: Adjust actual bindings

        add(new Label("Primary"), 1, 0);
        add(new Label("Alternate"), 2, 0);

        add(new Label("Move Up:"), 0, 1);
        add(new TextField(), 1, 1);
        add(new TextField(), 2, 1);

        add(new Label("Move Right:"), 0, 2);
        add(new TextField(), 1, 2);
        add(new TextField(), 2, 2);

        add(new Label("Move Down:"), 0, 3);
        add(new TextField(), 1, 3);
        add(new TextField(), 2, 3);

        add(new Label("Move Left:"), 0, 4);
        add(new TextField(), 1, 4);
        add(new TextField(), 2, 4);

        add(new Separator(Orientation.HORIZONTAL), 0, 5, 3, 1);

        add(new Label("Undo"), 0, 6);
        add(new TextField(), 1, 6);
        add(new TextField(), 2, 6);

        add(new Label("Redo"), 0, 7);
        add(new TextField(), 1, 7);
        add(new TextField(), 2, 7);
    }
}
