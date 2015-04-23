package eecs1510.Game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by nathan on 4/11/15
 */
public class Cell {

    private IntegerProperty boardX;
    private IntegerProperty boardY;
    private ReadOnlyIntegerProperty cellValue;
    private final Cell father;
    private final Cell mother;

    private int age = 0;

    public Cell(Cell father, Cell mother, int value, int x, int y){
        this.father = father;
        this.mother = mother;

        boardX = new SimpleIntegerProperty(x);
        boardY = new SimpleIntegerProperty(y);

        cellValue = new SimpleIntegerProperty(value);
    }

    public void move(int x, int y){
        boardX.setValue(x);
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
}
