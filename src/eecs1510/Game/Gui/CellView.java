package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import javafx.scene.Group;
import javafx.scene.Scene;
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
    private final Text label;

    public CellView(Cell model){
        this.model = model;

        String labelText = String.valueOf(model.getCellValue());

        label = new Text(labelText);
        double fontSize = (132d / (double)labelText.length()) - 10;
        System.out.println("Font PX: " + fontSize);
        label.setStyle(
                "-fx-font-size: " + fontSize + "px;\n" +
                (model.getCellValue() <= 4 ? "-fx-fill: #000;" : "-fx-fill: #fff;")
        );

        //TODO: Figure out better layouts for fonts. This is inconsistent
        double offset = (132.0 / 2.0 - (fontSize / labelText.length()) / 4.0);
        System.out.println("Layout: " + String.valueOf(offset));
        label.setLayoutX(offset);
        label.setLayoutY(132.0 - 20 - label.prefHeight(132) / 2.0);

        getChildren().add(label);

        int colorCode = getColorCodeForValue(model.getCellValue());
        setStyle("-fx-background-color: #" + Integer.toHexString(colorCode));
    }

}
