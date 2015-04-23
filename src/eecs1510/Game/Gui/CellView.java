package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Created by nathan on 4/21/15
 */
public class CellView extends Pane{

    public static final int[] CELL_COLORS = {
            0x000000, //UNDEFINED TODO: Sensible color
            0xeee4da, //2
            0xede0c8, //4
            0xf2b179, //8
            0xf59563, //16
            0xf67c5f, //32
            0xf67c5f, //64
            0xedcf72, //128
            0xedcc61, //256
            //TODO: 512, 1024, 2048
    };

    private final Cell model;

    public CellView(Cell model){
        setMaxSize(132, 132);
        setMinSize(132, 132);

        this.model = model;

        getStyleClass().add("cell-view");
        if(model.getCellValue() >= 256) getStyleClass().add("glow");

        String labelText = String.valueOf(model.getCellValue());

        Text label = new Text(labelText);
        label.getStyleClass().add("cell-view-label");

        double textWidth = label.getLayoutBounds().getWidth();
        double textHeight = label.getLayoutBounds().getHeight();

        double aspectRatio = textWidth / textHeight;

        double scaledWidth = (132.0 - 80.0)/textWidth;
        double scaledHeight = (132.0 - 80.0)/textHeight;

        label.setScaleX(scaledWidth > scaledHeight ? scaledWidth : scaledWidth / aspectRatio);
        label.setScaleY(scaledHeight > scaledWidth ? scaledHeight : scaledHeight / aspectRatio);

        if(model.getCellValue() <= 4) label.getStyleClass().add("dark-text");

        //TODO: Works fine for two digets, but screws up three or more digits
        label.setLayoutX(132.0 / 2.0 - label.getScaleX() / 2.0);
        //TODO: Where did this fudge factor of 6 come from?
        label.setLayoutY(132.0 / 2.0 - label.getScaleY() / 2.0 + 6);
        getChildren().add(label);

        int colorIndex = (int)(Math.log(model.getCellValue()) / Math.log(2));
        int colorCode = CELL_COLORS[colorIndex > CELL_COLORS.length ? 0 : colorIndex];
        setStyle("-fx-background-color: #" + Integer.toHexString(colorCode));
    }

}
