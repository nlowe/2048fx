package eecs1510.Game.Gui.Screen;

import eecs1510.Game.Gui.MainWindow;
import eecs1510.Game.Gui.WelcomeButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

/**
 * Created by nathan on 4/9/15
 */
public class WelcomeScreen extends ControlledScreen {

    public WelcomeScreen(MainWindow controller){
        super(controller);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30, 50, 30, 50));
        grid.setVgap(30);

        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ColumnConstraints c = new ColumnConstraints();
        c.setHgrow(Priority.ALWAYS);
        c.setFillWidth(true);
        grid.getColumnConstraints().add(c);


        WelcomeButton play = new WelcomeButton(
                new Image(MainWindow.class.getResourceAsStream("icons/ic_gamepad_black_36dp.png")),
                "Play",
                "Start a new Game"
        );
        play.setOnAction((e) -> controller.displayNewGameScreen());
        grid.add(play, 0, 0);

        WelcomeButton resume = new WelcomeButton(
                new Image(MainWindow.class.getResourceAsStream("icons/ic_folder_open_black_36dp.png")),
                "Continue",
                "Resume a previously started game"
        );
        resume.setDisable(true);
        resume.setOnAction((e) -> System.out.println("FIXME: TODO: Resume Game"));
        grid.add(resume, 0, 1);

        WelcomeButton race = new WelcomeButton(
                new Image(MainWindow.class.getResourceAsStream("icons/ic_flag_black_36dp.png")),
                "Race",
                "Play the same seed as a friend. First to 2048 wins!"
        );
        race.setOnAction((e) -> controller.displayRaceScreen());
        grid.add(race, 0, 2);

        this.getChildren().add(grid);
        setAlignment(grid, Pos.CENTER);
        HBox.setHgrow(this, Priority.ALWAYS);
    }
}
