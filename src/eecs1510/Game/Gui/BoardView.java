package eecs1510.Game.Gui;

import eecs1510.Game.*;
import eecs1510.Game.Gui.Notification.*;

import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by nathan on 4/11/15
 *
 * A view representing the game board, Updated each turn to reflect the
 */
public class BoardView extends Pane{

    /** The primary controller for this application */
    private final MainWindow controller;
    /** All Cell Views currently in this Node's scene graph */
    private final ArrayList<CellView> cellViews = new ArrayList<>();

    /** Notifications to display */
    private final Stack<NotificationBar> notifications = new Stack<>();
    /** The current notification animation, null if there is no notification being displayed */
    private SequentialTransition notificationTransition = null;
    /** The current notification being displayed */
    private volatile NotificationBar notification = null;

    /** The currently displayed overlay */
    private BoardOverlay overlay = null;

    public BoardView(MainWindow controller)
    {
        super();

        getStyleClass().add("board");

        //Computed max size (4 cells of 132 pixels each + 5 boarders at 18 pixels each)
        setMaxSize(618, 627);
        // Explicitly set the clip to correct the Z-Order for the notification drop-down
        setClip(new Rectangle(0, 0, 618, 627));

        this.controller = controller;

        // Add the 'empty' background cells
        for(int row = 0; row < Rules.BOARD_SIZE; row++)
        {
            for(int col=0; col < Rules.BOARD_SIZE; col++)
            {
                Pane emptyCell = new Pane();
                emptyCell.getStyleClass().add("empty-cell-view");

                emptyCell.setLayoutX((col * 132) + (col * 18) + 18);
                emptyCell.setLayoutY((row * 132) + (row * 18) + 18);
                emptyCell.setMaxSize(132, 132);
                emptyCell.setMinSize(132, 132);

                getChildren().add(emptyCell);
            }
        }

        // Events: Consume the event if it's handled by us
        setOnKeyReleased(e -> {
            if (controller.getKeyManager().handleKey(e))
            {
                e.consume();
            }
        });

        // Display the initial state and request focus for events
        updateView(null);
        requestFocus();

        // Update the view after every move
        controller.getGameController().onMoveComplete((moveResult) -> {
            if (moveResult != null && moveResult.isInvalid())
            {
                displayNotification("Invalid Move!", 3, NotificationType.WARNING, true);
            }

            updateView(moveResult);
        });

        controller.getGameController().onGameLost(this::showGameOverOverlay);

        controller.getGameController().onGameWon(() -> {
            overlay = new GameWonOverlay(controller);
            overlay.setOpacity(0.0);
            overlay.requestFocus();

            getChildren().add(overlay);

            // If the user wants to continue, fade out and remove the overlay from the scene graph
            ((GameWonOverlay) overlay).setOnContinue((e) -> {
                FadeTransition fade = new FadeTransition(Duration.millis(250), overlay);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setCycleCount(1);

                fade.setOnFinished((finished) -> {
                    getChildren().remove(overlay);
                });

                requestFocus();
                fade.play();
                controller.getKeyManager().setIgnoreEvents(false);
            });

            // Fade In the overlay
            FadeTransition fade = new FadeTransition(Duration.millis(250), overlay);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.setCycleCount(1);

            if (controller.getGameController().getStatsManager().wasNewHighScoreSet())
            {
                fade.setOnFinished((e) -> {
                    playTaDa();
                });
            }

            // Ignore events while an overlay is present
            controller.getKeyManager().setIgnoreEvents(true);
            fade.play();
        });
    }

    /**
     * Updates the view after a move is taken (skipped on an invalid move) by doing the following:
     *
     * 1) Removes all cell views from the scene graph
     * 2) Adds a cell view for each cell in the game board
     * 3) Creates and plays an animation for each cell depending on it's previous state
     *
     * @param moveResult the result of the move, or null to force a re-draw
     */
    protected void updateView(MoveResult moveResult)
    {
        GameController game = controller.getGameController();

        // If this is a new game or a move was undone, we can remove the overlay
        boolean undo = moveResult != null && moveResult.wasUndoFlagSet();
        if(game.getStatsManager().isNewGame() || undo)
        {
            getChildren().remove(overlay);
        }

        // Remove all nodes from the scene graph
        getChildren().removeAll(cellViews);
        cellViews.clear();

        // Add CellViews
        for(int row = 0; row < Rules.BOARD_SIZE; row++)
        {
            for(int col=0; col < Rules.BOARD_SIZE; col++)
            {
                Cell c = game.cellAt(row, col);
                if(c == null) continue;

                CellView view = new CellView(c);

                Point2D prev = boardToScene(c.getLastBoardX(), c.getLastBoardY());
                Point2D next = boardToScene(col, row);
                
                view.setLayoutX(next.getX());
                view.setLayoutY(next.getY());

                cellViews.add(view);
                getChildren().add(view);

                if(moveResult != null && moveResult.isInvalid())
                {
                    // Invalid Move
                    RotateTransition rl = new RotateTransition(Duration.millis(40), view);
                    rl.setFromAngle(0);
                    rl.setToAngle(-15);
                    rl.setCycleCount(1);

                    RotateTransition rr = new RotateTransition(Duration.millis(80), view);
                    rr.setFromAngle(-15);
                    rr.setToAngle(15);
                    rr.setCycleCount(1);

                    RotateTransition restore = new RotateTransition(Duration.millis(40), view);
                    restore.setFromAngle(15);
                    restore.setToAngle(0);
                    restore.setCycleCount(1);

                    new SequentialTransition(view, rl, rr, restore).play();

                } else if((game.getStatsManager().isNewGame() && moveResult == null) || (c.isOriginCell() && c.getAge() == 0)) {
                    //Newly Created Cell that was spawned randomly
                    ScaleTransition scale = new ScaleTransition();
                    scale.setDuration(Duration.millis(70));
                    scale.setNode(view);

                    scale.setFromX(0.0);
                    scale.setFromY(0.0);

                    scale.setToX(1.0);
                    scale.setToY(1.0);

                    scale.play();
                } else if(!undo && !c.isOriginCell() && c.getAge() == 1)
                {
                    //Newly Merged Cell (Ignores undo)

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

                    // Set this cell view to a scale of 0x0 and set it's location to it's destination
                    // We'll animate the scale after the parents have 'merged'
                    view.setScaleX(0.0);
                    view.setScaleY(0.0);
                    view.setLayoutX(next.getX());
                    view.setLayoutY(next.getY());

                    Timeline move = new Timeline();
                    move.setCycleCount(1);

                    // Move parents into place
                    move.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(70), new KeyValue(fatherView.layoutXProperty(), next.getX(), Interpolator.LINEAR)),
                            new KeyFrame(Duration.millis(70), new KeyValue(fatherView.layoutYProperty(), next.getY(), Interpolator.LINEAR)),
                            new KeyFrame(Duration.millis(70), new KeyValue(motherView.layoutXProperty(), next.getX(), Interpolator.LINEAR)),
                            new KeyFrame(Duration.millis(70), new KeyValue(motherView.layoutYProperty(), next.getY(), Interpolator.LINEAR))
                    );

                    // 'Bloom' animation after a merge
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
                } else {
                    // Regular movement

                    view.setLayoutX(prev.getX());
                    view.setLayoutY(prev.getY());

                    Timeline move = new Timeline();
                    move.setCycleCount(1);

                    move.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(70), new KeyValue(view.layoutXProperty(), next.getX(), Interpolator.LINEAR)),
                            new KeyFrame(Duration.millis(70), new KeyValue(view.layoutYProperty(), next.getY(), Interpolator.LINEAR))
                    );

                    move.play();
                }
            }
        }

        // If there is a notification being displayed, fix the z-order
        if(notification != null)
        {
            notification.toFront();
        }

        layoutChildren();
    }

    /**
     * Converts board coordinates to scene coordinates, where (0,0) is the upper-left corner of this Node
     *
     * @param x The x component or column
     * @param y the y component or row
     * @return
     */
    public Point2D boardToScene(int x, int y)
    {
        return new Point2D((x * 132) + (x*18) + 18, (y * 132) + (y*18) + 18);
    }

    /**
     * Queue's up a notification to be displayed. If there is no other notifications in the queue,
     * the notification will be displayed immediately
     *
     * @param text  the Text of the notification
     * @param duration  the duration in seconds
     * @param priority  the priority (INFO, WARNING, or ERROR)
     * @param overrideExisting
     */
    public void displayNotification(String text, int duration, NotificationType priority, boolean overrideExisting)
    {
        if(overrideExisting)
        {
            notifications.clear();

            if(notification != null)
            {
                notificationTransition.stop();
                getChildren().remove(notification);
                notification = null;
            }
        }

        notifications.push(new NotificationBar(text, duration, priority));
        updateDisplayedNotifications();
    }

    /**
     * Plays the next notification if ther isn't one already or the current one has finished
     */
    private void updateDisplayedNotifications()
    {
        // If no other notifications are being displayed and there is at least one notification in the stack, display it
        if(notificationTransition == null || notificationTransition.getStatus().equals(Animation.Status.STOPPED) && notifications.size() > 0)
        {
            // Add the NotificationBar to the scene and position it above the board
            notification = notifications.pop();
            getChildren().add(notification);
            notification.setLayoutX(0);
            notification.setLayoutY(-100);

            Point2D tl = this.localToScene(Point2D.ZERO);

            // PathTransitions animate on the center of an object
            PathTransition in = new PathTransition();
            in.setPath(new Path(new MoveTo(618/2, -100), new LineTo(618/2, tl.getY()+100)));
            in.setNode(notification);
            in.setCycleCount(1);

            PathTransition out = new PathTransition();
            out.setPath(new Path(new MoveTo(618/2, tl.getY()+100), new LineTo(618/2, -100)));
            out.setNode(notification);
            out.setCycleCount(1);

            // Create the composite transition (animate in, wait, animate out)
            notificationTransition = new SequentialTransition(
                    in, new PauseTransition(Duration.seconds(notification.getDuration())), out
            );

            // Do it all over again when finished
            notificationTransition.setOnFinished((e) -> {
                getChildren().remove(notification);
                updateDisplayedNotifications();
            });

            notificationTransition.setCycleCount(1);
            notificationTransition.play();
        }
    }

    /**
     * Shows the 'Game Over' screen over top of the game board. Internal Key Events are ignored while
     * the overlay is active.
     *
     * As per the project specification, the program exits after 5 seconds of calling this method
     */
    public void showGameOverOverlay()
    {
        overlay = new GameOverOverlay(controller);
        overlay.setOpacity(0.0);
        overlay.requestFocus();

        getChildren().add(overlay);

        // Fade the overlay up from 0 opacity to full opacity
        FadeTransition fade = new FadeTransition(Duration.millis(250), overlay);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setCycleCount(1);

        if(controller.getGameController().getStatsManager().wasNewHighScoreSet())
        {
            fade.setOnFinished((e) -> {
                playTaDa();
            });
        }

        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished((e) -> {
            // Exit after 5 seconds as per assignment specification
            controller.shutdownGame();
        });
        delay.setCycleCount(1);

        new SequentialTransition(overlay, fade, delay).play();
        controller.getKeyManager().setIgnoreEvents(true);
    }

    /**
     * Plays the 'TaDa' sound from windows
     */
    private void playTaDa()
    {
        new AudioClip(MainWindow.class.getResource("res/TADA.WAV").toString()).play(1.0);
    }
}
