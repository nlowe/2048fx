package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Created by nathan on 4/21/15
 */
public class CellView extends Pane{

    public static int getColorCodeForValue(int value){
        // TODO: Color Codes
        switch((int)(Math.log(value) / Math.log(2))){
            case 2:     return 0xeee4da;
            case 4:     return 0xede0c8;
            case 8:     return 0xf2b179;
            case 16:    return 0xf59563;
            case 32:    return 0xf67c5f;
            case 64:    return 0xf67c5f;
            case 128:   return 0xedcf72;
            case 256:   return 0xedcc61; //TODO: Glow
            case 512: 
            case 1024: 
            case 2048: 
            default:    return 0xeee4da;
        }
    }

    private final Cell model;

    public CellView(Cell model){
        getStyleClass().add("cell-view");

        this.model = model;

        String labelText = String.valueOf(model.getCellValue());

        Text label = new Text(labelText);

        double textWidth = label.getLayoutBounds().getWidth();
        double textHeight = label.getLayoutBounds().getHeight();

        double aspectRatio = textWidth / textHeight;

        double scaledWidth = (132.0 - 80.0)/textWidth;
        double scaledHeight = (132.0 - 80.0)/textHeight;

        label.setScaleX(scaledWidth > scaledHeight ? scaledWidth : scaledWidth / aspectRatio);
        label.setScaleY(scaledHeight > scaledWidth ? scaledHeight : scaledHeight / aspectRatio);

        label.setStyle(model.getCellValue() <= 4 ? "-fx-fill: #000;" : "-fx-fill: #fff;");

        //TODO: Works fine for two digets, but screws up three or more digits
        label.setLayoutX(132.0 / 2.0 - label.getScaleX() / 2.0);
        //TODO: Where did this fudge factor of 6 come from?
        label.setLayoutY(132.0 / 2.0 - label.getScaleY() / 2.0 + 6);
        getChildren().add(label);

        int colorCode = getColorCodeForValue(model.getCellValue());
        setStyle("-fx-background-color: #" + Integer.toHexString(colorCode));
    }

}
