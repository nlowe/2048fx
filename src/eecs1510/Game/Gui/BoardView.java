package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import eecs1510.Game.GameController;
import eecs1510.Game.MoveResult;
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
    private final ArrayList<CellView> cellViews = new ArrayList<>();

    private final Stack<NotificationBar> notifications = new Stack<>();
    private SequentialTransition notificationTransition = null;
    private volatile NotificationBar notification = null;

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
            if (controller.getKeyManager().handleKey(e)) {
                e.consume();
            }
        });

        updateView(null);
        requestFocus();

        controller.getGameController().onMoveComplete((moveResult) -> {
            if (moveResult != null && moveResult.isInvalid()) {
                displayNotification("Invalid Move!", 3, NotificationType.WARNING);
            } else {
                updateView(moveResult);
            }
        });
    }

    protected void updateView(MoveResult moveResult) {
        if(moveResult != null && moveResult.isInvalid()) return; //No need to update for invalid moves

        GameController game = controller.getGameController();

        getChildren().removeAll(cellViews);
        cellViews.clear();

        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col=0; col < Rules.BOARD_SIZE; col++){
                Cell c = game.cellAt(row, col);
                if(c == null) continue;

                CellView view = new CellView(c);

                Point2D prev = boardToScene(c.getLastBoardX(), c.getLastBoardY());
                Point2D next = boardToScene(col, row);
                
                view.setLayoutX(next.getX());
                view.setLayoutY(next.getY());

                cellViews.add(view);
                getChildren().add(view);

                if((controller.getGameController().getStatsManager().isNewGame() && moveResult == null) || (c.isOriginCell() && c.getAge() == 0)){
                    //Newly Created Cell that was spawned randomly
                    ScaleTransition scale = new ScaleTransition();
                    scale.setDuration(Duration.millis(150));
                    scale.setNode(view);

                    scale.setFromX(0.0);
                    scale.setFromY(0.0);

                    scale.setToX(1.0);
                    scale.setToY(1.0);

                    scale.play();
                }else if(!c.isOriginCell() && c.getAge() == 1){
                    //Newly Merged Cell

                    CellView fatherView = new CellView(c.getFather());
                    Point2D fatherPoint = boardToScene(fatherView.model.getBoardX(), fatherView.model.getBoardY());
                    fatherView.setLayoutX(fatherPoint.getX());
                    fatherView.setLayoutY(fatherPoint.getY());
                    
                    CellView motherView = new CellView(c.getMother());
                    Point2D motherPoint = boardToScene(motherView.model.getBoardX(), motherView.model.getBoardY());
                    motherView.setLayoutX(motherPoint.getX());
                    motherView.setLayoutY(motherPoint.getY());

                    cellViews.add(fatherView);
                    cellViews.add(motherView);
                    getChildren().addAll(fatherView, motherView);
                    
                    view.setScaleX(0.0);
                    view.setScaleY(0.0);
                    view.setLayoutX(next.getX());
                    view.setLayoutY(next.getY());

                    Timeline move = new Timeline();
                    move.setCycleCount(1);

                    move.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(150), new KeyValue(fatherView.layoutXProperty(), next.getX(), Interpolator.LINEAR)),
                            new KeyFrame(Duration.millis(150), new KeyValue(fatherView.layoutYProperty(), next.getY(), Interpolator.LINEAR)),
                            new KeyFrame(Duration.millis(150), new KeyValue(motherView.layoutXProperty(), next.getX(), Interpolator.LINEAR)),
                            new KeyFrame(Duration.millis(150), new KeyValue(motherView.layoutYProperty(), next.getY(), Interpolator.LINEAR))
                    );

                    move.setOnFinished((e) -> {
                        ScaleTransition up = new ScaleTransition(Duration.millis(75), view);
                        up.setFromX(0);
                        up.setFromY(0);
                        up.setToX(1.2);
                        up.setToY(1.2);

                        ScaleTransition restore = new ScaleTransition(Duration.millis(75), view);
                        restore.setFromX(1.2);
                        restore.setFromY(1.2);
                        restore.setToX(1.0);
                        restore.setToY(1.0);

                        cellViews.remove(fatherView);
                        cellViews.remove(motherView);
                        getChildren().removeAll(fatherView, motherView);

                        SequentialTransition scale = new SequentialTransition(up, restore);
                        scale.setCycleCount(1);
                        scale.play();
                    });

                    move.play();
                }else{
                    // Regular movement

                    view.setLayoutX(prev.getX());
                    view.setLayoutY(prev.getY());

                    Timeline move = new Timeline();
                    move.setCycleCount(1);

                    move.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(150), new KeyValue(view.layoutXProperty(), next.getX(), Interpolator.LINEAR)),
                            new KeyFrame(Duration.millis(150), new KeyValue(view.layoutYProperty(), next.getY(), Interpolator.LINEAR))
                    );
                    move.play();
                }
            }
        }

        if(notification != null){
            notification.toFront();
        }

        layoutChildren();
    }

    public Point2D boardToScene(int x, int y){
        return new Point2D((x * 132) + (x*18) + 18, (y * 132) + (y*18) + 18);
    }

    public void displayNotification(String text, int duration, NotificationType priority){
        notifications.push(new NotificationBar(text, duration, priority));
        updateDisplayedNotifications();
    }

    private void updateDisplayedNotifications(){
        if(notificationTransition == null || notificationTransition.getStatus().equals(Animation.Status.STOPPED) && notifications.size() > 0){
            notification = notifications.pop();
            getChildren().add(notification);
            notification.setLayoutX(0);
            notification.setLayoutY(-100);

            Point2D tl = this.localToScene(Point2D.ZERO);

            PathTransition in = new PathTransition();
            in.setPath(new Path(new MoveTo(618/2, -100), new LineTo(618/2, tl.getY()+100)));
            in.setNode(notification);
            in.setCycleCount(1);

            PathTransition out = new PathTransition();
            out.setPath(new Path(new MoveTo(618/2, tl.getY()+100), new LineTo(618/2, -100)));
            out.setNode(notification);
            out.setCycleCount(1);

            notificationTransition = new SequentialTransition(
                    in, new PauseTransition(Duration.seconds(notification.getDuration())), out
            );

            notificationTransition.setOnFinished((e) -> {
                getChildren().remove(notification);
                updateDisplayedNotifications();
            });

            notificationTransition.setCycleCount(1);
            notificationTransition.play();
        }
    }
}
