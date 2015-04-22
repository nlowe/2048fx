package eecs1510.Game.Gui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by nathan on 4/21/15
 */
public class HelpDialog extends Stage {

    public HelpDialog(MainWindow controller){
        initOwner(controller.getPrimaryStage());
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        GridPane grid = new GridPane();
        grid.setMaxHeight(Double.MAX_VALUE);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);

        ColumnConstraints center = new ColumnConstraints();
        center.setHalignment(HPos.CENTER);
        center.setFillWidth(true);
        center.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(center);

        int row = 0;

        Label title = new Label("About 2048fx");
        title.getStyleClass().add("title-label");
        grid.add(title, 0, ++row);

        Label about = new Label("2048fx by Nathan Lowe is a clone of the game 2048 by " +
                "Gabriele Cirulli and is based on 1024 by Veewo Studio (which is " +
                "conceptually similar to Threes by Asher Vollmer) written in Java " +
                "utilizing the JavaFX Framework.\n\n" +

                "Merge tiles to increase their value. New tiles are added " +
                "each turn. Try to get a tile to 2048!\n\n" +

                "Controls:\n" +
                "\tMovement: Arrow Keys\n" +
                "\tUndo: Ctrl+Z\n" +
                "\tSave Game: Alt+S\n" +
                "\tLoad Game: Alt+L\n" +
                "\tDisplay This Dialog: Alt+H\n" +
                "\tQuit Game: Alt+K"
        );
        about.setWrapText(true);
        grid.add(about, 0, ++row);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, ++row);

        grid.add(new Label("2048fx is Licensed under the MIT License"), 0, ++row);
        Label mit = new Label(getLicenseContent("res/licenses/MIT.txt"));
        mit.setWrapText(true);
        mit.getStyleClass().add("license-label");
        grid.add(mit, 0, ++row);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, ++row);

        Label ccLicense = new Label("Icons used in 2048fx are from the Material Design Project by Google. " +
                "Licensed under the Creative Commons Attribution International 4.0 License");
        ccLicense.setWrapText(true);
        ccLicense.setTextAlignment(TextAlignment.CENTER);
        grid.add(ccLicense, 0, ++row);
        Label cc = new Label(getLicenseContent("res/licenses/CC-BY-4.0.txt"));
        cc.setWrapText(true);
        cc.getStyleClass().add("license-label");

        grid.add(cc, 0, ++row);

        setTitle("Help & About");

        ScrollPane wrapper = new ScrollPane(grid);
        wrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        grid.maxWidthProperty().bind(wrapper.widthProperty().subtract(10).subtract(10));

        Scene helpScene = new Scene(wrapper, 600, 650);
        helpScene.getStylesheets().add("eecs1510/Game/Gui/res/theme.css");

        setScene(helpScene);
        setResizable(false);
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
