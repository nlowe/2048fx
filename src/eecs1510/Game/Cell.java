package eecs1510.Game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by nathan on 4/11/15
 *
 * A model for a single Cell. Each cell keeps track of it's value and location,
 * including where a cell has been (for up to 11 turns) and what cells combined
 * (if any) to produce it.
 */
public class Cell
{

    /** The cell's previous x coordinate (or column index) */
    private final IntegerProperty lastBoardX;
    /** The cell's current x coordinate (or column index) */
    private final ReadOnlyIntegerProperty boardX;
    /** The cell's previous y coordinate (or row index) */
    private final IntegerProperty lastBoardY;
    /** The cell's current y coordinate (or row index) */
    private final ReadOnlyIntegerProperty boardY;
    /** The value of the cell, a power of 2 */
    private final ReadOnlyIntegerProperty cellValue;
    /** The first cell combined to form this cell (null if this cell was randomly spawned) */
    private final Cell father;
    /** The second cell combined to form this cell (null if this cell was randomly spawned) */
    private final Cell mother;

    /** A buffer of previous locations that this cell has been in*/
    private RingBuffer<Vec2i> positionHistory;

    /**
     * The age of this cell, 0 for newly spawned cells (except the first two cells each game)
     * and at least one for all others
     */
    private int age = 0;

    public Cell(Cell father, Cell mother, int value)
    {
        this(father, mother, value, mother.getBoardX(), mother.getBoardY());
    }

    public Cell(Cell father, Cell mother, int value, int x, int y)
    {
        positionHistory = new RingBuffer<>(11);
        positionHistory.push(new Vec2i(x, y));

        this.father = father;
        this.mother = mother;

        lastBoardX = new SimpleIntegerProperty(x);
        boardX = new ReadOnlyIntegerWrapper(){
            @Override
            public int get()
            {
                // The current x coordinate is always given by the x coordinate
                // of the vector at the top of the position stack
                return positionHistory.peek().x;
            }
        };

        lastBoardY = new SimpleIntegerProperty(y);
        boardY = new ReadOnlyIntegerWrapper(){
            @Override
            public int get()
            {
                // The current y coordinate is always given by the y coordinate
                // of the vector at the top of the position stack
                return positionHistory.peek().y;
            }
        };

        cellValue = new SimpleIntegerProperty(value);
    }

    /**
     * Pushes a new position onto the position history stack for this cell,
     * updating the previous location as needed
     *
     * @param x
     * @param y
     */
    public void move(int x, int y)
    {
        if(positionHistory.count() > 0)
        {
            setMoveFrom(boardX.get(), boardY.get());
        }
        positionHistory.push(new Vec2i(x, y));
    }

    /**
     * Rolls back the cell to the previous state
     *
     * @return true if the cell should be decomposed or removed
     */
    public boolean rollBack()
    {
        if(--age >= 0)
        {
            if(age == 0 && !isOriginCell()) return true; // Newly Merged Cell

            // Move to the previous location 'from' the current location
            Vec2i current = positionHistory.pop();
            setMoveFrom(current.x, current.y);
            return false;
        } else {
            //Newly created cell
            return true;
        }
    }

    /**
     * Forces the last location properties to the specified values, used for animations
     *
     * @param x
     * @param y
     */
    public void setMoveFrom(int x, int y)
    {
        lastBoardX.set(x);
        lastBoardY.set(y);
    }

    /**
     * Reads a Cell object from the specified data stream, recursively loading parent cells.
     *
     * See <code>FORMAT.md</code> for a description of the on-disk file format
     *
     * @param in the stream to read from
     * @return
     * @throws IOException
     */
    public static Cell readCell(DataInputStream in) throws IOException
    {
        int age = in.readInt();
        int value = in.readInt();

        RingBuffer<Vec2i> positionHistory = new RingBuffer<>(11);

        int depth = in.readInt();
        for(int i=0; i<depth; i++)
        {
            int x = in.readInt();
            int y = in.readInt();

            positionHistory.push(new Vec2i(x, y));
        }

        boolean origin = in.readBoolean();

        Cell father = null;
        Cell mother = null;

        if(!origin)
        {
            father = readCell(in);
            mother = readCell(in);
        }

        Cell c = new Cell(father, mother, value, positionHistory.peek().x, positionHistory.peek().y);
        c.age = age;
        c.positionHistory = positionHistory;

        System.out.println("Read " + c);
        return c;
    }

    /**
     * Writes the cell to the specified stream, recursively writing parent cells
     *
     * See <code>FORMAT.md</code> for a description of the on-disk file format
     *
     * @param out the stream to read from
     * @throws IOException
     */
    public void storeCell(DataOutputStream out) throws IOException
    {
        out.writeInt(getAge());
        out.writeInt(getCellValue());

        int depth = positionHistory.count();

        out.writeInt(depth);
        for(int i=depth-1; i >= 0; i--)
        {
            Vec2i pos = positionHistory.getElement(i);
            out.writeInt(pos.x);
            out.writeInt(pos.y);
        }

        out.writeBoolean(isOriginCell());
        if(!isOriginCell())
        {
            father.storeCell(out);
            mother.storeCell(out);
        }

        System.out.println("Wrote " + this);
    }

    public Cell getFather()
    {
        return father;
    }

    public Cell getMother()
    {
        return mother;
    }

    public int getCellValue()
    {
        return cellValue.get();
    }

    public ReadOnlyIntegerProperty cellValueProperty()
    {
        return cellValue;
    }

    public int getBoardX()
    {
        return boardX.get();
    }

    public ReadOnlyIntegerProperty boardXProperty()
    {
        return boardX;
    }

    public int getBoardY()
    {
        return boardY.get();
    }

    public ReadOnlyIntegerProperty boardYProperty()
    {
        return boardY;
    }

    @Override
    public String toString()
    {
        return "{spawned: " + isOriginCell() + ", x: " + getBoardX() + ", y: " + getBoardY() + ", vx: " + (getBoardX() - getLastBoardX()) + ", vy:" + (getBoardY() - getLastBoardY()) + ", value: " + getCellValue() + ", age: " + age + ", positionStackSize: " + positionHistory.count() + "}";
    }

    public int getAge()
    {
        return age;
    }

    public void survive()
    {
        age++;
    }

    /**
     * Whether or not this cell is an 'origin' cell
     *
     * Origin cells are those without parents (in other words, randomly placed at the end of a turn)
     *
     * @return true iff this cell has no parents
     */
    public boolean isOriginCell()
    {
        return father == null && mother == null;
    }

    public int getLastBoardX()
    {
        return lastBoardX.get();
    }

    public ReadOnlyIntegerProperty lastBoardXProperty()
    {
        return lastBoardX;
    }

    public int getLastBoardY()
    {
        return lastBoardY.get();
    }

    public ReadOnlyIntegerProperty lastBoardYProperty()
    {
        return lastBoardY;
    }
}
