package eecs1510.Game.Gui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Created by nathan on 4/22/15
 */
public class NotificationBar extends GridPane{

    private int duration;
    private NotificationType type;

    public NotificationBar(String text){
        this(text, 15, NotificationType.INFO);
    }

    public NotificationBar(String text, int duration, NotificationType priority){
        getStyleClass().add("notification");
        switch(priority){
            case WARNING: getStyleClass().add("warning"); break;
            case ERROR: getStyleClass().add("error"); break;
        }

        this.duration = duration;
        this.type = priority;

        setMinSize(618, 100);
        setMaxSize(618, 100);

        setPadding(new Insets(10, 10, 10, 10));

        ColumnConstraints c = new ColumnConstraints();
        c.setHgrow(Priority.ALWAYS);
        c.setFillWidth(true);
        c.setHalignment(HPos.CENTER);

        getColumnConstraints().add(c);

        add(new Label(text), 0, 0);
    }

    public int getDuration(){
        return duration;
    }
}
