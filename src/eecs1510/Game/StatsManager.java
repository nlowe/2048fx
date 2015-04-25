package eecs1510.Game;

import eecs1510.Game.Gui.MainWindow;
import eecs1510.Game.Gui.NotificationType;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

/**
 * Created by nathan on 4/22/15
 */
public class StatsManager {

    private final RingBuffer<Integer> score = new RingBuffer<>(10);
    private final ReadOnlyIntegerWrapper scoreProperty = new ReadOnlyIntegerWrapper(0);

    private final RingBuffer<Integer> turnCount = new RingBuffer<>(10);
    private final ReadOnlyIntegerWrapper turnCountProperty = new ReadOnlyIntegerWrapper(0);

    private final RingBuffer<Integer> totalMerged = new RingBuffer<>(10);
    private final ReadOnlyIntegerWrapper totalMergedProperty = new ReadOnlyIntegerWrapper(0);

    private final ReadOnlyIntegerWrapper highScoreProperty = new ReadOnlyIntegerWrapper();

    private boolean notifiedHighScore = false;

    public StatsManager(MainWindow controller){
        this(controller, 0);
    }

    public StatsManager(MainWindow controller, int highScore){

        highScoreProperty.set(highScore);

        scoreProperty.addListener(((observable, oldValue, newValue) -> {
            if (newValue.intValue() > getHighScore()) {
                highScoreProperty.set(newValue.intValue());
                if (!notifiedHighScore) {
                    controller.getBoardRenderer().displayNotification("New High Score: " + newValue.intValue(), 3, NotificationType.INFO);
                    notifiedHighScore = true;
                }
            }
        }));
    }

    public void applyMove(MoveResult move){
        if(move != null && !move.isInvalid()){
            score.push(getScore() + move.mergeValue);
            turnCount.push(getTurnCount() + 1);
            totalMerged.push(getTotalMerged() + move.mergeCount);

            updateProperties();
        }
    }

    private void updateProperties(){
        scoreProperty.set(score.count() > 0 ? score.peek() : 0);
        turnCountProperty.set(turnCount.count() > 0 ? turnCount.peek() : 0);
        totalMergedProperty.set(totalMerged.count() > 0 ? totalMerged.peek() : 0);
    }

    public int getScore() {
        return scoreProperty.get();
    }

    public ReadOnlyIntegerProperty scorePropertyProperty() {
        return scoreProperty.getReadOnlyProperty();
    }

    public int getTurnCount() {
        return turnCountProperty.get();
    }

    public ReadOnlyIntegerProperty turnCountPropertyProperty() {
        return turnCountProperty.getReadOnlyProperty();
    }

    public int getTotalMerged() {
        return totalMergedProperty.get();
    }

    public ReadOnlyIntegerProperty totalMergedPropertyProperty() {
        return totalMergedProperty.getReadOnlyProperty();
    }

    public int getHighScore() {
        return highScoreProperty.get();
    }

    public ReadOnlyIntegerProperty highScorePropertyProperty() {
        return highScoreProperty.getReadOnlyProperty();
    }

    public void reset(boolean resetHighScore) {
        score.clear();
        totalMerged.clear();
        turnCount.clear();
        if(resetHighScore){
            highScoreProperty.set(0);
            notifiedHighScore = false;
        }

        updateProperties();
    }

    public void rollBack(){
        score.pop();
        totalMerged.pop();
        turnCount.pop();

        updateProperties();
    }
}
