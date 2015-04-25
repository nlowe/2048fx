package eecs1510.Game;

import java.util.LinkedList;
import java.util.RandomAccess;

/**
 * Created by nathan on 4/25/15
 */
public class RingBuffer<T> implements RandomAccess{

    private final int size;
    private final LinkedList<T> buffer;

    public RingBuffer(int size){
        this.size = size;
        buffer = new LinkedList<>();
    }

    public void push(T element){
        buffer.addFirst(element);
        if(buffer.size() > size){
            buffer.removeLast();
        }
    }

    public T pop(){
        return buffer.removeFirst();
    }

    public T peek(){
        return buffer.getFirst();
    }

    public int getSize(){
        return size;
    }

    public int count(){
        return buffer.size();
    }

    public boolean hasNext(){
        return buffer.size() > 0;
    }

    public T getElement(int index){
        return buffer.get(index);
    }

    public void clear(){
        buffer.clear();
    }
}
