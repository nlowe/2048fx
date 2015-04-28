package eecs1510.Game;

import eecs1510.Game.Gui.MainWindow;
import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by nathan on 4/7/15
 */
public class GameController {

    public static final String NO_PREVIOUS_GAME = "!NO_PREVIOUS_GAME!";
    public static final File HIGH_SCORE_FILE = new File(System.getenv("user.home"), "HighScore.dat");

    private Cell[][] board;
    private Random randomizer;
    private final StatsManager statsManager;

    // --- Listener Stores----
    private final ArrayList<SimpleListener> gameWonListeners = new ArrayList<>();
    private final ArrayList<Consumer<MoveResult>> moveCompleteListeners = new ArrayList<>();
    // -----------------------

    private int undoCounter = 0;

    private int lastHighScore = 0;
    private String lastGamePath = NO_PREVIOUS_GAME;

    public GameController(MainWindow w){

        try(DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(HIGH_SCORE_FILE)))){
            System.out.println("Reading stats from "  + HIGH_SCORE_FILE.getAbsolutePath());
            lastHighScore = in.readInt();
            lastGamePath = in.readUTF();
        }catch(IOException e){
            e.printStackTrace();
        }

        statsManager = new StatsManager(w, lastHighScore);
        onMoveComplete((move) -> {
            Platform.runLater(() -> statsManager.applyMove(move));
        });

        startNewGame();
    }

    public void startNewGame(){
        undoCounter = 0;
        statsManager.reset(lastHighScore);
        //Start a game with the specified rules
        board = new Cell[Rules.BOARD_SIZE][Rules.BOARD_SIZE];

        randomizer = new Random();

        placeRandom();
        placeRandom();

        Arrays.stream(board).flatMap(Arrays::stream).filter((c) -> c != null).forEach(Cell::survive);
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
        //If there is an active game, execute a move in the direction specified

        boolean LTR = direction == Direction.NORTH || direction == Direction.WEST;
        int size = Rules.BOARD_SIZE;

        int totalMerged = 0;
        int totalMergedValue = 0;
        Cell[][] newState = new Cell[size][size];
        
        if(direction == Direction.NORTH || direction == Direction.SOUTH){
            for(int column=0; column < size; column++){
                Cell[] filteredColumn = stripNull(slice(column));
                if(filteredColumn.length == 0) continue;

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
                if(filteredRow.length == 0) continue;

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
            setState(newState);

            Arrays.stream(board).flatMap(Arrays::stream).filter((c) -> c != null).forEach(Cell::survive);

            if(undoCounter < 10) undoCounter++;

            boolean lost = placeRandom();
            doMoveComplete(new MoveResult(totalMerged, totalMergedValue));

            if(lost){
                //TODO: doGameLost
            }
        }
    }

    //TODO: This needs refactored / optimized
    public MoveResult merge(Cell[] source, boolean LTR){
        int totalMerged = 0;
        int totalMergedValue = 0;

        if(LTR){
            for(int i=0; i < source.length - 1; i++){
                if(source[i].getCellValue() == source[i+1].getCellValue()){
                    source[i+1] = new Cell(source[i], source[i+1], source[i].getCellValue()*2);
                    source[i] = null;
                    totalMerged++;
                    totalMergedValue += source[++i].getCellValue();
                }
            }
        }else{
            for(int i = source.length - 1; i >= 1; i--){
                if(source[i].getCellValue() == source[i-1].getCellValue()){
                    source[i-1] = new Cell(source[i], source[i-1], source[i].getCellValue()*2);
                    source[i] = null;
                    totalMerged++;
                    totalMergedValue += source[--i].getCellValue();
                }
            }
        }

        return new MoveResult(totalMerged, totalMergedValue);
    }

    private void setState(Cell[][] state){
        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col = 0; col < Rules.BOARD_SIZE; col++){
                Cell c = state[row][col];

                board[row][col] = state[row][col];

                if(c != null){
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

    public boolean undoMove(){
        if(undoCounter < 1) return false;

        //If there is an active game, undo the most recent move
        Cell[][] state = new Cell[Rules.BOARD_SIZE][Rules.BOARD_SIZE];
        Arrays.stream(board).flatMap(Arrays::stream).filter((cell) -> cell != null).forEach((cell) -> {
            boolean decompose = cell.rollBack();
            if (!decompose) {
                System.out.println(cell + " is at least two generations old, just moving");
                state[cell.getBoardY()][cell.getBoardX()] = cell;
            } else {
                if (!cell.isOriginCell()) {

                    // Return Parents to game board
                    Cell father = cell.getFather();
                    father.setMoveFrom(cell.getBoardX(), cell.getBoardY());

                    Cell mother = cell.getMother();
                    mother.setMoveFrom(cell.getBoardX(), cell.getBoardY());

                    System.out.println("Decomposing " + cell + " into " + father + " and " + mother);

                    state[father.getBoardY()][father.getBoardX()] = father;
                    state[mother.getBoardY()][mother.getBoardX()] = mother;
                }else{
                    System.out.println(cell + " was just added, removing");
                }
            }
        });

        statsManager.rollBack();
        board = state; //Skip call to setState(...) because cells have already 'moved'
        doMoveComplete(null);
        undoCounter--;

        return true;
    }

    public boolean saveGame(String path){
        try(DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))){

            return true;
        }catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void loadGame(String path){

    }

    public void saveHighScore(){
        System.out.println("Trying to save high score to " + HIGH_SCORE_FILE.getAbsolutePath());
        try(DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(HIGH_SCORE_FILE)))){
            out.writeInt(getStatsManager().getHighScore());
            out.writeUTF("!NO_PREVIOUS_GAME!");

            out.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
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
