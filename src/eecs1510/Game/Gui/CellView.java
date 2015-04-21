package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Created by nathan on 4/21/15
 */
public class CellView extends Pane{

    private static final int[] BACKGROUND_COLORS = {
        
    };
    
    public static int getColorCodeForValue(int value){
        // TODO: Color Codes
        switch((int)(Math.log(value) / Math.log(2))){
            case 2: 
            case 4: 
            case 8: 
            case 16: 
            case 32: 
            case 64: 
            case 128: 
            case 256: 
            case 512: 
            case 1024: 
            case 2048: 
            default: return 0x000000;
        }
    }

    private final Cell model;
    private final Text label;

    public CellView(Cell model){
        this.model = model;

        label = new Text(String.valueOf(model.getCellValue()));

        int colorCode = getColorCodeForValue(model.getCellValue());
    }

}
