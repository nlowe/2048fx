package eecs1510.Game.Gui.Notification;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Created by nathan on 4/22/15
 *
 * A simple pop-over pane inspired by Gtk+'s InfoBar combined with the Revealer animation
 */
public class NotificationBar extends GridPane
{

    /** The duration, in seconds, that this notification should be visible **/
    private final int duration;
    /** The type or priority of the notification*/
    private final NotificationType type;

    public NotificationBar(String text)
    {
        this(text, 15, NotificationType.INFO);
    }

    public NotificationBar(String text, int duration, NotificationType priority)
    {
        getStyleClass().add("notification");
        switch(priority)
        {
            case WARNING: getStyleClass().add("warning"); break;
            case ERROR: getStyleClass().add("error"); break;
        }

        this.duration = duration;
        this.type = priority;

        setMinSize(618, 100);
        setMaxSize(618, 100);

        setPadding(new Insets(10, 10, 10, 10));

        // Grow and center all the things
        ColumnConstraints c = new ColumnConstraints();
        c.setHgrow(Priority.ALWAYS);
        c.setFillWidth(true);
        c.setHalignment(HPos.CENTER);

        getColumnConstraints().add(c);

        add(new Label(text), 0, 0);
    }

    /**
     * @return The duration, in secopnds, that the notification remains stationary on the screen
     */
    public int getDuration()
    {
        return duration;
    }

    /**
     * @return the type or priority of the notification
     */
    public NotificationType getType()
    {
        return type;
    }
}
