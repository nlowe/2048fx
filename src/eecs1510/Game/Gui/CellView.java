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
            0xf65e3b, //64
            0xedcf72, //128
            0xedcc61, //256
            0xedc84f, //512
            0xedc53f, //1024
            0xedc22e, //2048
    };

    protected final Cell model;

    public CellView(Cell model){
        setMaxSize(132, 132);
        setMinSize(132, 132);

        this.model = model;

        getStyleClass().add("cell-view");
        if(model.getCellValue() >= 256) getStyleClass().add("glow");

        String labelText = String.valueOf(model.getCellValue());

        Text label = new Text(labelText);
        label.getStyleClass().add("cell-view-label");
        getChildren().add(label);

        double textWidth = label.getLayoutBounds().getWidth();
        double textHeight = label.getLayoutBounds().getHeight();

        double scaleFactor = Math.min((132-60)/textWidth, (132-60)/textHeight);

        label.setScaleX(scaleFactor);
        label.setScaleY(scaleFactor);

        if(model.getCellValue() <= 4) label.getStyleClass().add("dark-text");

        label.setX(132.0 / 2.0 - label.getLayoutBounds().getWidth() / 2.0);
        //TODO: Where did this fudge factor of 15 come from?
        label.setY(132.0 / 2.0 - label.getLayoutBounds().getHeight() / 2.0 + 15);

        int colorIndex = (int)(Math.log(model.getCellValue()) / Math.log(2));
        int colorCode = CELL_COLORS[colorIndex >= CELL_COLORS.length ? 0 : colorIndex];
        setStyle("-fx-background-color: #" + Integer.toHexString(colorCode));
    }

}
