package eecs1510.Game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by nathan on 4/22/15
 */
public class StatsManager {
    private IntegerProperty score = new SimpleIntegerProperty(0);
    private IntegerProperty turnCount = new SimpleIntegerProperty(0);
    private IntegerProperty totalMerged = new SimpleIntegerProperty(0);
    private IntegerProperty highScore = new SimpleIntegerProperty(0);

    public StatsManager(){
        this(0);
    }

    public StatsManager(int highScore){
        setHighScore(highScore);

        score.addListener(((observable, oldValue, newValue) -> {
            if (newValue.intValue() > getHighScore()) {
                setHighScore(newValue.intValue());
            }
        }));
    }

    public void applyMove(MoveResult move){
        if(!move.isInvalid()){
            setScore(getScore() + move.mergeValue);
            setTurnCount(getTurnCount() + 1);
            setTotalMerged(getTotalMerged() + move.mergeCount);
        }
    }

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public int getTurnCount() {
        return turnCount.get();
    }

    public IntegerProperty turnCountProperty() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount.set(turnCount);
    }

    public int getTotalMerged() {
        return totalMerged.get();
    }

    public IntegerProperty totalMergedProperty() {
        return totalMerged;
    }

    public void setTotalMerged(int totalMerged) {
        this.totalMerged.set(totalMerged);
    }

    public int getHighScore() {
        return highScore.get();
    }

    public IntegerProperty highScoreProperty() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore.set(highScore);
    }
}
