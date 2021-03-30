package org.meveo.admin.async;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a one at a time access to iterator.getNext() function
 * 
 * @author Andrius Karpavicius
 *
 * @param <T> Element class
 */
public class SynchronizedIterator<T> implements Iterator<T> {

    /**
     * A number of total items
     */
    private int size;

    /**
     * Data iterator
     */
    private Iterator<T> iterator;

    public SynchronizedIterator() {
    }

    public SynchronizedIterator(Collection<T> dataList) {
        iterator = dataList.iterator();
        size = dataList.size();
    }

    @Override
    /**
     * A synchronized implementation of Iterator.next(). Will return null if no more values are available
     * 
     * @return Returns the next element, or null if no more elements are found
     */
    public synchronized T next() {

        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }

    /**
     * Do not use this method - use next() instead. This method will always return false.
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * @return Total number of records
     */
    public int getSize() {
        return size;
    }
}