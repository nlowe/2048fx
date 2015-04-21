package eecs1510.Game.Gui.Screen;

import eecs1510.Game.Gui.KeyBindingsPane;
import eecs1510.Game.Gui.MainWindow;
import eecs1510.Game.Gui.KeyBindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

import java.io.*;

/**
 * Created by nathan on 4/9/15
 */
public class HelpScreen extends ControlledScreen{

    public HelpScreen(MainWindow controller){
        super(controller);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30, 50, 30, 50));
        grid.setVgap(30);
        grid.setHgap(20);

        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ColumnConstraints left = new ColumnConstraints();
        left.setHgrow(Priority.SOMETIMES);
        left.setPercentWidth(50);

        ColumnConstraints right = new ColumnConstraints();
        right.setHgrow(Priority.SOMETIMES);
        right.setPercentWidth(50);

        grid.getColumnConstraints().addAll(left, right);

        RowConstraints normal = new RowConstraints();
        normal.setVgrow(Priority.NEVER);

        RowConstraints expand = new RowConstraints();
        expand.setVgrow(Priority.ALWAYS);
        expand.setFillHeight(true);

        grid.getRowConstraints().addAll(normal, normal, normal, expand);

        Label title = new Label("Help & About");
        title.getStyleClass().add("title-label");
        grid.add(title, 0, 0);
        Label about = new Label("2048fx by Nathan Lowe is a clone of the game 2048 by " +
                "Gabriele Cirulli and is based on 1024 by Veewo Studio (which is " +
                "conceptually similar to Threes by Asher Vollmer) written in Java " +
                "utilizing the JavaFX Framework.\n\n" +
                "Merge tiles to increase their value. New tiles are added " +
                "each turn. Try to get a tile to 2048!");
        about.setWrapText(true);
        grid.add(about, 0, 1);

        grid.add(new Label("2048fx is Licensed under the MIT License"), 0, 2);
        TextArea mit = new TextArea();
        mit.setEditable(false);
        mit.setText(getLicenseContent("res/licenses/MIT.txt"));
        mit.setWrapText(false);
        grid.add(mit, 0, 3);

        Label controls = new Label("Controls");
        controls.getStyleClass().add("title-label");
        grid.add(controls, 1, 0);
        grid.add(new KeyBindingsPane(controller.getKeyBindings()), 1, 1); //TODO: Use existing keybindings

        Label ccLabel = new Label("Icons used in 2048fx are from the Material Design Project by Google. Licensed under the " +
                "Creative Commons Attribution International 4.0 License");
        ccLabel.setWrapText(true);
        grid.add(ccLabel, 1, 2);
        TextArea cc = new TextArea();
        cc.setEditable(false);
        cc.setWrapText(false);
        cc.setText(getLicenseContent("res/licenses/CC-BY-4.0.txt"));
        grid.add(cc, 1, 3);

        this.getChildren().add(grid);
        setAlignment(grid, Pos.CENTER);
        HBox.setHgrow(this, Priority.ALWAYS);
    }

    private String getLicenseContent(String resourcePath){
        StringBuilder sb = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(MainWindow.class.getResourceAsStream(resourcePath)))){
            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
        }catch(Exception ex){
            sb.append("Unable to read license content:\n\n");

            StringWriter stackTraceString = new StringWriter();
            PrintWriter stackTrace = new PrintWriter(stackTraceString);
            ex.printStackTrace(stackTrace);

            sb.append(stackTraceString.toString());
        }

        return sb.toString().trim();
    }
}
