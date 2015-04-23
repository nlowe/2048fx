package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import eecs1510.Game.GameController;
import eecs1510.Game.Rules;
import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by nathan on 4/11/15
 */
public class BoardView extends Pane{

    private final MainWindow controller;
    private ArrayList<CellView> cellViews = new ArrayList<>();

    private Stack<NotificationBar> notifications = new Stack<>();
    private SequentialTransition notificationTransition = null;

    public BoardView(MainWindow controller){
        super();

        getStyleClass().add("board");

        //Computed max size (4 cells of 132 pixels each + 5 boarders at 18 pixels each)
        setMaxSize(618, 627);
        // Explicitly set the clip to correct the Z-Order for the notification drop-down
        setClip(new Rectangle(0, 0, 618, 627));

        this.controller = controller;

        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col=0; col < Rules.BOARD_SIZE; col++){
                Pane emptyCell = new Pane();
                emptyCell.getStyleClass().add("empty-cell-view");

                emptyCell.setLayoutX((col * 132) + (col * 18) + 18);
                emptyCell.setLayoutY((row * 132) + (row * 18) + 18);
                emptyCell.setMaxSize(132, 132);
                emptyCell.setMinSize(132, 132);

                getChildren().add(emptyCell);
            }
        }

        // Events
        setOnKeyReleased(e -> {
            if(controller.getKeyManager().handleKey(e)){
                e.consume();
            }
        });

        updateView();
        requestFocus();

        controller.getGameController().onMoveComplete((move) -> updateView());
    }

    protected void updateView() {
        System.out.println("Updating Cell Views");

        GameController game = controller.getGameController();

        getChildren().removeAll(cellViews);
        cellViews.clear();

        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col=0; col < Rules.BOARD_SIZE; col++){
                Cell c = game.cellAt(row, col);
                if(c == null) continue;

                CellView view = new CellView(c);
                view.setLayoutX((col * 132) + (col*18) + 18);
                view.setLayoutY((row * 132) + (row*18) + 18);
                view.setMaxSize(132, 132);
                view.setMinSize(132, 132);
                cellViews.add(view);
            }
        }

        getChildren().addAll(cellViews);
        layoutChildren();
    }

    public void displayNotification(String text, int duration, NotificationType priority){
        notifications.push(new NotificationBar(text, duration, priority));
        updateDisplayedNotifications();
    }

    private void updateDisplayedNotifications(){
        if(notificationTransition == null || notificationTransition.getStatus().equals(Animation.Status.STOPPED) && notifications.size() > 0){
            NotificationBar b = notifications.pop();
            getChildren().add(b);
            b.setLayoutX(0);
            b.setLayoutY(-100);

            Point2D tl = this.localToScene(Point2D.ZERO);

            PathTransition in = new PathTransition();
            in.setPath(new Path(new MoveTo(618/2, -100), new LineTo(618/2, tl.getY()+100)));
            in.setNode(b);
            in.setCycleCount(1);

            PathTransition out = new PathTransition();
            out.setPath(new Path(new MoveTo(618/2, tl.getY()+100), new LineTo(618/2, -100)));
            out.setNode(b);
            out.setCycleCount(1);

            notificationTransition = new SequentialTransition(
                    in, new PauseTransition(Duration.seconds(b.getDuration())), out
            );

            notificationTransition.setOnFinished((e) -> {
                getChildren().remove(b);
                updateDisplayedNotifications();
            });

            notificationTransition.setCycleCount(1);
            notificationTransition.play();
        }else{
            System.out.println("Not stopped or no remaining notifications");
        }
    }
}
