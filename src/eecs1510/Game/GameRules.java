package eecs1510.Game;

import javafx.beans.property.*;

/**
 * Created by nathan on 4/9/15
 */
public class GameRules {

    private IntegerProperty boardSize;
    private IntegerProperty winningScore;
    private IntegerProperty maxUndoSize;
    private IntegerProperty tilesAddedPerTurn;
    private DoubleProperty fourRatio;
    private StringProperty seed;

    public GameRules(){
        this(4, 2048, 1, 1, 0.9, "");
    }
    
    public GameRules(int boardSize, int winningScore, int maxUndoSize, int tilesAddedPerTurn, double fourRatio, String seed){
        this.boardSize = new SimpleIntegerProperty(boardSize);
        this.winningScore = new SimpleIntegerProperty(winningScore);
        this.maxUndoSize = new SimpleIntegerProperty(maxUndoSize);
        this.tilesAddedPerTurn = new SimpleIntegerProperty(tilesAddedPerTurn);
        this.fourRatio = new SimpleDoubleProperty(fourRatio);
        this.seed = new SimpleStringProperty(seed);
    }

    public int getBoardSize() {
        return boardSize.get();
    }

    public IntegerProperty boardSizeProperty() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize.set(boardSize);
    }

    public int getWinningScore() {
        return winningScore.get();
    }

    public IntegerProperty winningScoreProperty() {
        return winningScore;
    }

    public void setWinningScore(int winningScore) {
        this.winningScore.set(winningScore);
    }

    public int getMaxUndoSize() {
        return maxUndoSize.get();
    }

    public IntegerProperty maxUndoSizeProperty() {
        return maxUndoSize;
    }

    public void setMaxUndoSize(int maxUndoSize) {
        this.maxUndoSize.set(maxUndoSize);
    }

    public int getTilesAddedPerTurn() {
        return tilesAddedPerTurn.get();
    }

    public IntegerProperty tilesAddedPerTurnProperty() {
        return tilesAddedPerTurn;
    }

    public void setTilesAddedPerTurn(int tilesAddedPerTurn) {
        this.tilesAddedPerTurn.set(tilesAddedPerTurn);
    }

    public String getSeed() {
        return seed.get();
    }

    public StringProperty seedProperty() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed.set(seed);
    }

    public double getFourRatio() {
        return fourRatio.get();
    }

    public DoubleProperty fourRatioProperty() {
        return fourRatio;
    }

    public void setFourRatio(double fourRatio) {
        this.fourRatio.set(fourRatio);
    }
}
