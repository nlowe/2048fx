package eecs1510.Game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by nathan on 4/11/15
 */
public class Cell {

    private final IntegerProperty lastBoardX;
    private final ReadOnlyIntegerProperty boardX;
    private final IntegerProperty lastBoardY;
    private final ReadOnlyIntegerProperty boardY;
    private final ReadOnlyIntegerProperty cellValue;
    private final Cell father;
    private final Cell mother;

    private RingBuffer<Vec2i> positionHistory;

    private int age = 0;

    public Cell(Cell father, Cell mother, int value) {
        this(father, mother, value, mother.getBoardX(), mother.getBoardY());
    }

    public Cell(Cell father, Cell mother, int value, int x, int y){
        positionHistory = new RingBuffer<>(10);
        positionHistory.push(new Vec2i(x, y));

        this.father = father;
        this.mother = mother;

        lastBoardX = new SimpleIntegerProperty(x);
        boardX = new ReadOnlyIntegerWrapper(){
            @Override
            public int get() {
                return positionHistory.peek().x;
            }
        };

        lastBoardY = new SimpleIntegerProperty(y);
        boardY = new ReadOnlyIntegerWrapper(){
            @Override
            public int get() {
                return positionHistory.peek().y;
            }
        };

        cellValue = new SimpleIntegerProperty(value);
    }

    public void move(int x, int y){
        if(positionHistory.count() > 0){
            lastBoardX.set(boardX.get());
            lastBoardY.set(boardY.get());
        }
        positionHistory.push(new Vec2i(x, y));
    }

    public boolean rollBack(){
        if(positionHistory.count() >= 2){
            positionHistory.pop();
            Vec2i previousPosition =  positionHistory.pop();
            move(previousPosition.x, previousPosition.y);
            return false;
        }else{
            //Newly created or merged cell
            return true;
        }
    }

    public Cell getFather() {
        return father;
    }

    public Cell getMother() {
        return mother;
    }

    public int getCellValue() {
        return cellValue.get();
    }

    public ReadOnlyIntegerProperty cellValueProperty() {
        return cellValue;
    }

    public int getBoardX() {
        return boardX.get();
    }

    public ReadOnlyIntegerProperty boardXProperty() {
        return boardX;
    }

    public int getBoardY() {
        return boardY.get();
    }

    public ReadOnlyIntegerProperty boardYProperty() {
        return boardY;
    }

    @Override
    public String toString(){
        return "{x: " + getBoardX() + ", y: " + getBoardY() + ", value: " + getCellValue() + "}";
    }

    public int getAge(){
        return age;
    }

    public void survive(){
        age++;
    }

    public boolean isOriginCell(){
        return father == null && mother == null;
    }

    public int getLastBoardX() {
        return lastBoardX.get();
    }

    public ReadOnlyIntegerProperty lastBoardXProperty() {
        return lastBoardX;
    }

    public int getLastBoardY() {
        return lastBoardY.get();
    }

    public ReadOnlyIntegerProperty lastBoardYProperty() {
        return lastBoardY;
    }
}
