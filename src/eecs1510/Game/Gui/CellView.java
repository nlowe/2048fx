package eecs1510.Game.Gui;

import eecs1510.Game.Cell;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Created by nathan on 4/21/15
 *
 * A view for individual 132px x 132px cells
 */
public class CellView extends StackPane
{

    /**
     * The colors for each cell, indexed by power of 2 with the
     * 0th index reserved for cells larger than 2048. Colors taken from
     *
     * http://gabrielecirulli.github.io/2048/
     */
    public static final int[] CELL_COLORS = {
            0x3c3a32, //UNDEFINED
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

    /** The Cell Model that this view is tied to */
    protected final Cell model;

    public CellView(Cell model)
    {
        setMaxSize(132, 132);
        setMinSize(132, 132);

        this.model = model;

        // Add a tooltip for debugging purposes
        Tooltip.install(this, new Tooltip(model.toString()));

        getStyleClass().add("cell-view");

        // If the cell's value is between 256 and 2048 there's a slight glow around the border
        if(model.getCellValue() >= 256 && model.getCellValue() <= 2048)
        {
            getStyleClass().add("glow");
        }

        String labelText = String.valueOf(model.getCellValue());

        Text label = new Text(labelText);
        label.getStyleClass().add("cell-view-label");
        getChildren().add(label);

        // Scale the text to fit in the cell
        double textWidth = label.getLayoutBounds().getWidth();
        double textHeight = label.getLayoutBounds().getHeight();

        double scaleFactor = Math.min((132-60)/textWidth, (132-60)/textHeight);

        label.setScaleX(scaleFactor);
        label.setScaleY(scaleFactor);

        if(model.getCellValue() <= 4)
        {
            label.getStyleClass().add("dark-text");
        }

        // Set the background color of the cell
        int colorIndex = (int)(Math.log(model.getCellValue()) / Math.log(2));
        int colorCode = CELL_COLORS[colorIndex >= CELL_COLORS.length ? 0 : colorIndex];
        setStyle("-fx-background-color: #" + Integer.toHexString(colorCode));
    }

}
