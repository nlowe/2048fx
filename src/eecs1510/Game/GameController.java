package eecs1510.Game;

import eecs1510.Game.Gui.MainWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by nathan on 4/7/15
 *
 * The main controller for a game. Controls game statistics and the board
 */
public class GameController {

    /** The identifier used to indicate that no previous game was saved */
    public static final String NO_PREVIOUS_GAME = "!NO_PREVIOUS_GAME!";
    /** The default location for the high score file */
    public static final File HIGH_SCORE_FILE = new File(System.getenv("user.home"), "HighScore.dat");

    /** The internal representation of the game board*/
    private Cell[][] board;
    /** The random number generator used in placing new cells */
    private Random randomizer;
    /** The stats controller, reset when a new game is started or loaded */
    private final StatsManager statsManager;

    // --- Listener Stores----
    /** All listeners listening to the 'GameLost' signal or event */
    private final ArrayList<SimpleListener> gameLostListeners = new ArrayList<>();
    /** All listeners listening to the 'GameWon' signal or event */
    private final ArrayList<SimpleListener> gameWonListeners = new ArrayList<>();
    /** All listeners listening to the 'onMoveComplete' signal or event */
    private final ArrayList<Consumer<MoveResult>> moveCompleteListeners = new ArrayList<>();
    // -----------------------

    /** The number of undo's left */
    private int undoCounter = 0;
    /** True if the won condition has already been met */
    private boolean notifiedWon = false;

    /** The high score cached from reading the high score file*/
    private int lastHighScore = 0;
    /** The path to the most recent game played (and saved) */
    private String lastGamePath = NO_PREVIOUS_GAME;

    public GameController(MainWindow w){

        // Try to read the high score file
        try(DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(HIGH_SCORE_FILE)))){
            System.out.println("Reading stats from "  + HIGH_SCORE_FILE.getAbsolutePath());
            lastHighScore = in.readInt();
            lastGamePath = in.readUTF();
        }catch(IOException e){
            e.printStackTrace();
        }

        statsManager = new StatsManager(w, lastHighScore);
        onMoveComplete(statsManager::applyMove);

        startNewGame();
    }

    public void startNewGame(){
        undoCounter = 0;
        statsManager.reset(lastHighScore);
//        board = new Cell[Rules.BOARD_SIZE][Rules.BOARD_SIZE];
        board = new Cell[][]{
                {new Cell(null, null, 1024, 0, 0), new Cell(null, null, 1024, 1, 0), null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
        };

        randomizer = new Random();

        // Place two random tiles as per game rules
        placeRandom();
        placeRandom();

        // The first two tiles have an age of 1 because of an edge case with undo animations
        Arrays.stream(board).flatMap(Arrays::stream).filter((c) -> c != null).forEach(Cell::survive);
    }

    /**
     * @return  the cell at the specified row and column
     */
    public Cell cellAt(int row, int col){
        return board[row][col];
    }

    /**
     * Extract a vertical slice from the board at a given column
     *
     * @param column The column to slice
     * @return a vertical slice of the board at the specified column
     */
    private Cell[] slice(int column){
        Cell[] result = new Cell[Rules.BOARD_SIZE];

        for(int i=0; i< Rules.BOARD_SIZE; i++){
            result[i] = board[i][column];
        }

        return result;
    }

    /**
     * @param slice
     * @return a filtered array of the source <code>arr</code> with all null elements removed
     */
    private Cell[] stripNull(Cell[] slice){
        return Arrays.stream(slice).filter((c) -> c != null).toArray(Cell[]::new);
    }

    /**
     * Executes a move in the specified direction
     *
     * Rows or columns are merged in the opposite direction that the move was taken in, and the
     * results are copied into the game board, aligned at the direction edge
     *
     * @param direction
     */
    public void takeMove(Direction direction){
        //TODO: This needs refactored / optimized

        // If we're moving towards 0 for a slice, we need to merge left-to-right
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
            // If we haven't merged anything and the new state is the same as the old one,
            // then by definition the move is invalid
            doMoveComplete(MoveResult.invalid());
        }else{
            // Otherwise, copy in the new state
            setState(newState);

            // Increment the age for each cell
            Arrays.stream(board).flatMap(Arrays::stream).filter((c) -> c != null).forEach(Cell::survive);

            // Increment the maximum amount of allowed undo's (up to 10)
            if(undoCounter < 10) undoCounter++;

            // Determine if the game is lost
            boolean lost = !placeRandom() || isLost();

            // Notify listeners
            doMoveComplete(new MoveResult(totalMerged, totalMergedValue));

            if(lost){
                doGameLost();
            }else if(!notifiedWon && isGameWon()){
                notifiedWon = true;
                doGameWon();
            }
        }
    }

    /**
     * Merges like items in an array along the specified direction
     *
     * @param source The row or column-slice to merge
     * @param LTR    Whether to merge from left to right or right to left
     * @return a <code>MoveResult</code> object containing the total number
     *         of merged cells as well as the score gained by those merged
     *         cells for the specified slice of the game board
     */
    public MoveResult merge(Cell[] source, boolean LTR){
        //TODO: This needs refactored / optimized
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

    /**
     * Copies in the new state for the game board, 'moving' cells to their locations if they exist
     * @param state the state to apply
     */
    private void setState(Cell[][] state){
        for(int row = 0; row < Rules.BOARD_SIZE; row++){
            for(int col = 0; col < Rules.BOARD_SIZE; col++){
                Cell c = state[row][col];

                board[row][col] = c;

                if(c != null){
                    c.move(col, row);
                }
            }
        }
    }

    /**
     * Places a random 2 or 4 on the game board at a free space.
     * If there are no more free spaces, this method returns false.
     *
     * @return true if a value was able to be placed
     */
    public boolean placeRandom(){
        ArrayList<int[]> freeCells = getFreeCells();

        if(freeCells.isEmpty()){
            return false;
        }

        int initialValue = randomizer.nextDouble() >= Rules.FOUR_THRESHOLD ? 4 : 2;

        int[] cell = freeCells.get(randomizer.nextInt(freeCells.size()));
        int row = cell[0];
        int col = cell[1];

        // Randomly placed cells don't have parents
        board[row][col] = new Cell(null, null, initialValue, col, row);
        return true;
    }

    /**
     * @return an ArrayList of integer arrays pointing to the location of free cells.
     */
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

    /**
     * Rolls back the previous move if there is at least one undo left
     *
     * @return true iff the rollback was executed
     */
    public boolean undoMove(){
        if(undoCounter < 1) return false;

        //If there is an active game, undo the most recent move
        Cell[][] state = new Cell[Rules.BOARD_SIZE][Rules.BOARD_SIZE];
        Arrays.stream(board).flatMap(Arrays::stream).filter((cell) -> cell != null).forEach((cell) -> {
            boolean decompose = cell.rollBack();
            if (!decompose) {
                // The cell is at least two generations old, just move it
                System.out.println(cell + " is at least two generations old, just moving");
                state[cell.getBoardY()][cell.getBoardX()] = cell;
            } else {
                if (!cell.isOriginCell()) {
                    // Return Parents to game board and don't add the current cell back
                    Cell father = cell.getFather();
                    father.setMoveFrom(cell.getBoardX(), cell.getBoardY());

                    Cell mother = cell.getMother();
                    mother.setMoveFrom(cell.getBoardX(), cell.getBoardY());

                    System.out.println("Decomposing " + cell + " into " + father + " and " + mother);

                    state[father.getBoardY()][father.getBoardX()] = father;
                    state[mother.getBoardY()][mother.getBoardX()] = mother;
                }else{
                    // The cell was just added. Remove it
                    System.out.println(cell + " was just added, removing");
                }
            }
        });

        // Roll back stats
        statsManager.rollBack();

        // Skip call to setState(...) because cells have already 'moved'
        board = state;

        // Update listeners and decrement the undo counter
        doMoveComplete(MoveResult.undo());
        undoCounter--;

        return true;
    }

    /**
     * @return true iff the game board contains a cell with a value of at least 2048
     */
    public boolean isGameWon(){
        return Arrays.stream(board).flatMap(Arrays::stream).filter(c -> c != null && c.getCellValue() >= 2048).count() > 0;
    }

    /**
     * @return true if there are no more moves left to be made
     */
    public boolean isLost(){

        // North / South
        for (int col = 0; col < Rules.BOARD_SIZE; col++){
            for (int row = 0; row < Rules.BOARD_SIZE - 1; row++){
                if (canMergeCell(board[row][col], board[row + 1][col]))
                    return false;
            }
        }

        // East / West
        for (int row = 0; row < Rules.BOARD_SIZE; row++){
            for (int col = 0; col < Rules.BOARD_SIZE - 1; col++){
                if(canMergeCell(board[row][col], board[row][col + 1]))
                    return false;
            }
        }

        return true;
    }

    /**
     * Determines whether or not two cells can be merged
     * @param left The first cell to check
     * @param right The second cell to check
     * @return true iff only one of the cells is null (the other can be moved) or the cells contain the same value
     */
    private boolean canMergeCell(Cell left, Cell right){
        return
                (left == null ^ right == null) ||
                (left != null && right != null &&  left.getCellValue() == right.getCellValue());
    }

    /**
     * Saves the game to the file at the specified path, creating it if it doesn't exist.
     *
     * See <code>FORMAT.md</code> for a description of the on-disk file format
     *
     * @param path the path to save to
     * @return true iff the game was able to be saved to the specified path
     */
    public boolean saveGame(String path){
        try(DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)))){
            statsManager.save(out);

            out.writeInt(undoCounter);

            Cell[] cells = Arrays.stream(board).flatMap(Arrays::stream).filter(c -> c != null).toArray(Cell[]::new);
            out.writeInt(cells.length);
            for (Cell cell : cells) {
                cell.storeCell(out);
            }


            lastGamePath = path;
            return true;
        }catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the game from the file at the specified path
     *
     * See <code>FORMAT.md</code> for a description of the on-disk file format
     *
     * @param path the path to load the game from
     * @return true iff the game was able to be loaded
     */
    public boolean startGameFromFile(String path){
        try(DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))){
            statsManager.loadFromFile(in, lastHighScore);

            int undoCounter = in.readInt();

            int count = in.readInt();
            Cell[] cells = new Cell[count];
            for(int i=0; i<count; i++){
                cells[i] = Cell.readCell(in);
            }

            Cell[][] b = new Cell[Rules.BOARD_SIZE][Rules.BOARD_SIZE];
            for(Cell c : cells){
                b[c.getBoardY()][c.getBoardX()] = c;
            }

            board = b;
            this.undoCounter = undoCounter;

            return true;
        }catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the high score information between games
     */
    public void saveHighScore(){
        System.out.println("Trying to save high score to " + HIGH_SCORE_FILE.getAbsolutePath());
        try(DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(HIGH_SCORE_FILE)))){
            out.writeInt(getStatsManager().getHighScore());
            out.writeUTF(lastGamePath);

            out.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getLastGamePath(){
        return lastGamePath;
    }

    public void setLastGamePath(String p){
        lastGamePath = p == null || p.isEmpty() ? NO_PREVIOUS_GAME : p;
    }

    public void onGameLost(SimpleListener listener){ gameLostListeners.add(listener); }

    /**
     * Add a listener to the 'gameWon' signal or event
     * @param listener
     */
    public void onGameWon(SimpleListener listener){
        gameWonListeners.add(listener);
    }

    /**
     * Add a listener to the 'moveComplete' signal or event
     * @param listener
     */
    public void onMoveComplete(Consumer<MoveResult> listener){
        moveCompleteListeners.add(listener);
    }

    /**
     * Notifies all listeners that a move has been completed
     * @param move the move result from the move. May be null.
     */
    private void doMoveComplete(MoveResult move){
        moveCompleteListeners.stream().forEach((l) -> l.accept(move));
    }

    /**
     * Notify all listeners that the game is won
     */
    private void doGameWon(){
        gameWonListeners.stream().forEach(SimpleListener::listen);
    }

    /**
     * Notify all listeners that the game is lost
     */
    private void doGameLost(){
        gameLostListeners.stream().forEach(SimpleListener::listen);
    }

    public StatsManager getStatsManager(){
        return statsManager;
    }
}
