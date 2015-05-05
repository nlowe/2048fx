package eecs1510.Game;

import eecs1510.Game.Gui.MainWindow;
import eecs1510.Game.Gui.Notification.NotificationType;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by nathan on 4/22/15
 *
 * Controls and keeps track of stats for the game
 */
public class StatsManager {

    /** A history of the score */
    private final RingBuffer<Integer> score = new RingBuffer<>(11);
    /** A property pointing to the current score */
    private final ReadOnlyIntegerWrapper scoreProperty = new ReadOnlyIntegerWrapper(0);

    /** The number of elapsed turns */
    private final ReadOnlyIntegerWrapper turnCount = new ReadOnlyIntegerWrapper(0);

    /** A history of the number of merged cells */
    private final RingBuffer<Integer> totalMerged = new RingBuffer<>(11);
    /** A property pointing to the current number of merged cells */
    private final ReadOnlyIntegerWrapper totalMergedProperty = new ReadOnlyIntegerWrapper(0);

    /** The highest score ever reached */
    private final ReadOnlyIntegerWrapper highScoreProperty = new ReadOnlyIntegerWrapper();

    /** Whether or not the game this controller is tracking is newly started */
    private final ReadOnlyBooleanWrapper newGame = new ReadOnlyBooleanWrapper(true);

    /** Whether or not the user has been notified of a new high score */
    private boolean notifiedHighScore = false;

    public StatsManager(MainWindow controller, int highScore){

        highScoreProperty.set(highScore);

        // if the score is higher than the high score, update the high score
        // and optionally notify the user
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

    /**
     * Updates stats for the specified move result
     * @param move the move result to apply stats from
     */
    public void applyMove(MoveResult move){
        if(move != null && !move.isInvalid() && !move.wasUndoFlagSet()){
            score.push(getScore() + move.mergeValue);
            turnCount.set(turnCount.get() + 1);
            totalMerged.push(getTotalMerged() + move.mergeCount);

            updateProperties();
            if(isNewGame()){
                newGame.set(false);
            }
        }
    }

    /**
     * Updates the properties tracking the stats buffers
     */
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

    /**
     * Resets the stats back to their initial state and optionally resets the high score
     *
     * @param resetHighScore whether to also reset the high score
     */
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

    /**
     * Rolls back the stats to the previous state
     */
    public void rollBack(){
        score.pop();
        totalMerged.pop();
        turnCount.set(turnCount.get() - 1);

        updateProperties();
    }

    /**
     * Resets stats and then loads them from the specified stream
     *
     * @param in        the stream to read from
     * @param highScore the high score to set
     * @throws IOException
     */
    public void loadFromFile(DataInput in, int highScore) throws IOException {
        reset(highScore);

        notifiedHighScore = in.readBoolean();

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

    /**
     * Saves stats to the specified stream.
     *
     * **Note**: High Scores are saved in a separate stream / file
     *
     * @param out
     * @throws IOException
     */
    public void save(DataOutputStream out) throws IOException {
        out.writeBoolean(notifiedHighScore);

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

    public boolean wasNewHighScoreSet(){
        return notifiedHighScore;
    }

    public ReadOnlyBooleanProperty newGameProperty() {
        return newGame.getReadOnlyProperty();
    }

    @Override
    public String toString(){
        return "{turn: " + getTurnCount() + ", score: " + getScore() + ", best: " + getHighScore() + ", totalMerged: " + getTotalMerged() + "}";
    }
}
