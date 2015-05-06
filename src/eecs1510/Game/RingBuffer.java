package eecs1510.Game;

import java.util.LinkedList;

/**
 * Created by nathan on 4/25/15
 *
 * A limited size, generic FIFO buffer
 */
public class RingBuffer<T>
{

    /** The maximum number of allowed elements */
    private final int size;
    /** The internal buffer, trimmed after adding elements*/
    private final LinkedList<T> buffer;

    public RingBuffer(int size)
    {
        this.size = size;
        buffer = new LinkedList<>();
    }

    /**
     * Add a new element to the buffer. If this causes the buffer
     * to go over the maximum allowed element count, the last element
     * is removed from the buffer
     *
     * @param element the element to add
     */
    public void push(T element)
    {
        buffer.addFirst(element);
        if(buffer.size() > size){
            buffer.removeLast();
        }
    }

    /**
     * Removes and returns the first element in the buffer
     * @return the first element in the buffer
     */
    public T pop()
    {
        return buffer.removeFirst();
    }

    /**
     * @return the first element in the buffer
     */
    public T peek()
    {
        return buffer.getFirst();
    }

    /**
     * @return the maximum number of allowed elements in the buffer
     */
    public int getMaxSize()
    {
        return size;
    }

    /**
     * @return the total number of items in the buffer
     */
    public int count()
    {
        return buffer.size();
    }

    /**
     * @return iff there is at least one element in the buffer
     */
    public boolean hasNext()
    {
        return buffer.size() > 0;
    }

    /**
     * Gets the element at the specified offset from the first element (where 0 is the first element)
     * @param index
     * @return
     */
    public T getElement(int index)
    {
        return buffer.get(index);
    }

    /**
     * Removes all elements from the buffer
     */
    public void clear()
    {
        buffer.clear();
    }
}
