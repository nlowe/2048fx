package eecs1510.Game.Gui.Screen;

import eecs1510.Game.GameRules;
import eecs1510.Game.Gui.RulesPane;
import eecs1510.Game.Gui.MainWindow;
import eecs1510.Game.Randomizer;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

/**
 * Created by nathan on 4/9/15
 */
public class RaceScreen extends ControlledScreen {

    public RaceScreen(MainWindow controller){
        super(controller);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30, 50, 30, 50));
        grid.setVgap(30);
        grid.setHgap(30);

        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ColumnConstraints left = new ColumnConstraints();
        left.setHgrow(Priority.SOMETIMES);
        left.setPercentWidth(50);

        ColumnConstraints center = new ColumnConstraints();
        center.setHgrow(Priority.NEVER);
        center.setFillWidth(false);
        center.setHalignment(HPos.CENTER);

        ColumnConstraints right = new ColumnConstraints();
        right.setHgrow(Priority.SOMETIMES);
        right.setPercentWidth(50);

        grid.getColumnConstraints().addAll(left, center, right);

        GameRules rules = new GameRules();

        Label title = new Label("Race");
        title.getStyleClass().add("title-label");
        grid.add(title, 0, 0);

        Label desc = new Label("Compete against friends! Enter the seed " +
                "and rules that your friend chose on the " +
                "left! First one to the winning score is " +
                "the winner!");
        desc.setWrapText(true);
        grid.add(desc, 0, 1);

        grid.add(new Separator(Orientation.VERTICAL), 1, 0, 1, 3);

        Label seed = new Label("Seed");
        seed.getStyleClass().add("title-label");
        grid.add(seed, 2, 0);
        TextField seedField = new TextField(rules.getSeed()){
            @Override
            public void replaceText(int start, int end, String text) {
                if (text.matches("[0-9A-Za-z ]+")) {
                    super.replaceText(start, end, text.toUpperCase());
                }
            }

            @Override
            public void replaceSelection(String text) {
                if (text.matches("[0-9A-Za-z ]+")) {
                    super.replaceSelection(text.toUpperCase());
                }
            }
        };
        seedField.textProperty().bindBidirectional(rules.seedProperty());
        grid.add(seedField, 2, 1);

        grid.add(new RulesPane(rules), 2, 2);

        Button play = new Button("Start the Game!");
        play.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        play.setOnAction((e) -> {
            controller.play(rules);
        });
        grid.add(play, 0, 3, 3, 1);

        this.getChildren().add(grid);
        setAlignment(grid, Pos.CENTER);
        HBox.setHgrow(this, Priority.ALWAYS);

    }

}
