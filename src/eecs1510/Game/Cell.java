package eecs1510.Game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by nathan on 4/11/15
 */
public class Cell {

    private final IntegerProperty lastBoardX;
    private final IntegerProperty boardX;
    private final IntegerProperty lastBoardY;
    private final IntegerProperty boardY;
    private final ReadOnlyIntegerProperty cellValue;
    private final Cell father;
    private final Cell mother;

    private int age = 0;

    public Cell(Cell father, Cell mother, int value) {
        this(father, mother, value, mother.getBoardX(), mother.getBoardY());
    }

    public Cell(Cell father, Cell mother, int value, int x, int y){
        this.father = father;
        this.mother = mother;

        lastBoardX = new SimpleIntegerProperty(x);
        boardX = new SimpleIntegerProperty(x);
        lastBoardY = new SimpleIntegerProperty(y);
        boardY = new SimpleIntegerProperty(y);

        cellValue = new SimpleIntegerProperty(value);
    }

    public void move(int x, int y){
        lastBoardX.setValue(getBoardX());
        boardX.setValue(x);
        lastBoardY.setValue(getBoardY());
        boardY.setValue(y);
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

    public IntegerProperty boardXProperty() {
        return boardX;
    }

    public int getBoardY() {
        return boardY.get();
    }

    public IntegerProperty boardYProperty() {
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

    public IntegerProperty lastBoardXProperty() {
        return lastBoardX;
    }

    public int getLastBoardY() {
        return lastBoardY.get();
    }

    public IntegerProperty lastBoardYProperty() {
        return lastBoardY;
    }
}
