package eecs1510.Game.Gui.Screen;

import eecs1510.Game.Gui.MainWindow;
import javafx.scene.layout.StackPane;

/**
 * Created by nathan on 4/9/15
 */
public abstract class ControlledScreen extends StackPane {

    protected final MainWindow controller;

    public ControlledScreen(MainWindow controller){
        super();

        this.controller = controller;
    }

}
