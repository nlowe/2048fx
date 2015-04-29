package eecs1510.Game;

import eecs1510.Game.Gui.MainWindow;
import eecs1510.Game.Gui.NotificationType;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by nathan on 4/22/15
 */
public class StatsManager {

    private final RingBuffer<Integer> score = new RingBuffer<>(11);
    private final ReadOnlyIntegerWrapper scoreProperty = new ReadOnlyIntegerWrapper(0);

    private final ReadOnlyIntegerWrapper turnCount = new ReadOnlyIntegerWrapper(0);

    private final RingBuffer<Integer> totalMerged = new RingBuffer<>(11);
    private final ReadOnlyIntegerWrapper totalMergedProperty = new ReadOnlyIntegerWrapper(0);

    private final ReadOnlyIntegerWrapper highScoreProperty = new ReadOnlyIntegerWrapper();

    private final ReadOnlyBooleanWrapper newGame = new ReadOnlyBooleanWrapper(true);

    private boolean notifiedHighScore = false;

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
            turnCount.set(turnCount.get() + 1);
            totalMerged.push(getTotalMerged() + move.mergeCount);

            updateProperties();
            if(isNewGame()){
                newGame.set(false);
            }
        }
    }

    private void updateProperties(){
        scoreProperty.set(score.count() > 0 ? score.peek() : 0);
        totalMergedProperty.set(totalMerged.count() > 0 ? totalMerged.peek() : 0);
    }

    public int getScore() {
        return scoreProperty.get();
    }

    public ReadOnlyIntegerProperty scorePropertyProperty() {
        return scoreProperty.getReadOnlyProperty();
    }

    public int getTurnCount() {
        return turnCount.get();
    }

    public ReadOnlyIntegerProperty turnCountProperty() {
        return turnCount.getReadOnlyProperty();
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

    public void reset(int resetHighScore) {
        score.clear();
        totalMerged.clear();
        turnCount.set(0);
        if(resetHighScore >= 0){
            highScoreProperty.set(resetHighScore);
            notifiedHighScore = false;
        }

        updateProperties();
        newGame.set(true);
    }

    public void rollBack(){
        score.pop();
        totalMerged.pop();
        turnCount.set(turnCount.get() - 1);

        updateProperties();
    }

    public void loadFromFile(DataInput in, int highScore) throws IOException {
        reset(highScore);

        turnCount.set(in.readInt());

        int count = in.readInt();
        for(int i=0; i<count; i++){
            score.push(in.readInt());
        }

        count = in.readInt();
        for(int i=0; i<count; i++){
            totalMerged.push(in.readInt());
        }

        newGame.set(true);
        updateProperties();

        System.out.println("Loaded stats " + this.toString());
    }

    public void save(DataOutputStream out) throws IOException {
        out.writeInt(getTurnCount());

        out.writeInt(score.count());
        for(int i=score.count()-1; i >= 0; i--){
            out.writeInt(score.getElement(i));
        }

        out.writeInt(totalMerged.count());
        for(int i=totalMerged.count()-1; i >= 0; i--){
            out.writeInt(totalMerged.getElement(i));
        }
    }

    public boolean isNewGame() {
        return newGame.get();
    }

    public ReadOnlyBooleanProperty newGameProperty() {
        return newGame.getReadOnlyProperty();
    }

    @Override
    public String toString(){
        return "{turn: " + getTurnCount() + ", score: " + getScore() + ", best: " + getHighScore() + ", totalMerged: " + getTotalMerged() + "}";
    }
}
