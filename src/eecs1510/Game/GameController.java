package eecs1510.Game;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by nathan on 4/7/15
 */
public class GameController {


    private Cell[][] board;
    private Random randomizer;
    private StatsManager statsManager;

    // --- Listener Stores----
    ArrayList<SimpleListener> gameWonListeners = new ArrayList<>();
    ArrayList<Consumer<MoveResult>> moveCompleteListeners = new ArrayList<>();
    // -----------------------

    public GameController(){
        statsManager = new StatsManager();
        onMoveComplete((move) -> {
            Platform.runLater(() -> statsManager.applyMove(move));
        });

        startNewGame();
    }

    public void startNewGame(){
        //Start a game with the specified rules
        board = new Cell[Rules.BOARD_SIZE][Rules.BOARD_SIZE];

        randomizer = new Random();

        placeRandom();
        placeRandom();
    }

    public Cell cellAt(int row, int col){
        return board[row][col];
    }

    private Cell[] slice(int column){
        Cell[] result = new Cell[Rules.BOARD_SIZE];

        for(int i=0; i< Rules.BOARD_SIZE; i++){
            result[i] = board[i][column];
        }

        return result;
    }

    private Cell[] stripNull(Cell[] slice){
        return Arrays.stream(slice).filter((c) -> c != null).toArray(Cell[]::new);
    }

    //TODO: This needs refactored / optimized
    public void takeMove(Direction direction){
        System.out.println("Take Move: " + direction);

        //If there is an active game, execute a move in the direction specified

        boolean LTR = direction == Direction.NORTH || direction == Direction.WEST;
        int size = Rules.BOARD_SIZE;

        int totalMerged = 0;
        int totalMergedValue = 0;
        Cell[][] newState = new Cell[size][size];
        
        if(direction == Direction.NORTH || direction == Direction.SOUTH){
            for(int column=0; column < size; column++){
                Cell[] filteredColumn = stripNull(slice(column));

                // If there's more than one cell, try merging
                if(filteredColumn.length > 1){
                    MoveResult partial = merge(filteredColumn, LTR);
                    totalMerged += partial.mergeCount;
                    totalMergedValue += partial.mergeValue;

                    filteredColumn = stripNull(filteredColumn);
                }

                // Copy in the new state
                if(LTR){
                    for(int row=0; row < size; row++){
                        newState[row][column] = row < filteredColumn.length ? filteredColumn[row] : null;
                    }
                }else{
                    for(int row = size - 1, i = filteredColumn.length - 1; row >= 0; row--, i--){
                        newState[row][column] = i >= 0 ? filteredColumn[i] : null;
                    }
                }
            }
        }else{
            for(int row = 0; row < size; row++){
                Cell[] filteredRow = stripNull(board[row]);

                // If there's more than one cell, try merging
                if(filteredRow.length > 1){
                    MoveResult partial = merge(filteredRow, LTR);
                    totalMerged += partial.mergeCount;
                    totalMergedValue += partial.mergeValue;

                    filteredRow = stripNull(filteredRow);
                }

                // Copy in the new state
                if(LTR){
                    for(int column = 0; column < size; column ++){
                        newState[row][column] = column < filteredRow.length ? filteredRow[column] : null;
                    }
                }else{
                    for(int column = size - 1, i = filteredRow.length - 1; column >= 0; column--, i--){
                        newState[row][column] = i >= 0 ? filteredRow[i] : null;
                    }
                }
            }
        }

        if(totalMerged == 0 && Arrays.deepEquals(board, newState)){
            doMoveComplete(MoveResult.invalid());
        }else{
            System.out.println("Switching to new state");
            setState(newState);
            boolean lost = placeRandom();
            doMoveComplete(new MoveResult(totalMerged, totalMergedValue));

            if(lost){
                //TODO: doGameLost
            }
        }
    }

    public MoveResult merge(Cell[] source, boolean LTR){
        int totalMerged = 0;
        int totalMergedValue = 0;

        int idx = LTR ? 0 : source.length-1;
        do{
            int next = idx + (LTR ? 1 : -1);

            Cell a = source[idx];
            Cell b = source[next];

            if(a.getCellValue() == b.getCellValue()){
                System.out.println("MERGE");
                source[next] = new Cell(a, b, a.getCellValue() * 2, b.getBoardX(), b.getBoardY());
                source[idx] = null;
                totalMerged++;
                totalMergedValue += b.getCellValue();
            }

            idx = next;
        }while(0 < idx && idx < source.length-1);

        return new MoveResult(totalMerged, totalMergedValue);
    }

    private void setState(Cell[][] state){
        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col = 0; col < Rules.BOARD_SIZE; col++){
                Cell c = state[row][col];

                board[row][col] = state[row][col];

                if(c != null && (c.getBoardX() != col || c.getBoardY() != row)){
                    c.move(col, row);
                }
            }
        }
    }

    public boolean placeRandom(){
        ArrayList<int[]> freeCells = getFreeCells();

        if(freeCells.isEmpty()){
            return false;
        }

        int initialValue = randomizer.nextDouble() >= Rules.FOUR_RATIO ? 4 : 2;

        int[] cell = freeCells.get(randomizer.nextInt(freeCells.size()));
        int row = cell[0];
        int col = cell[1];

        board[row][col] = new Cell(null, null, initialValue, col, row);
        return true;
    }

    public ArrayList<int[]> getFreeCells(){
        ArrayList<int[]> results = new ArrayList<>();

        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col = 0; col < Rules.BOARD_SIZE; col++){
                if(board[row][col] == null){
                    results.add(new int[]{row, col});
                }
            }
        }

        return  results;
    }

    public void undoMove(){
        //If there is an active game, undo the most recent move
    }

    public void onGameWon(SimpleListener listener){
        gameWonListeners.add(listener);
    }

    public void onMoveComplete(Consumer<MoveResult> listener){
        moveCompleteListeners.add(listener);
    }

    private void doMoveComplete(MoveResult move){
        moveCompleteListeners.stream().forEach((l) -> l.accept(move));
    }

    private void doGameWon(){
        gameWonListeners.stream().forEach((SimpleListener::listen));
    }

    public StatsManager getStatsManager(){
        return statsManager;
    }
}
