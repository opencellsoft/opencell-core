package org.meveo.admin.async;

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.ScrollableResults;

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

    private ScrollableResults scrollableResults;
    
    public SynchronizedIterator() {
    }

    public SynchronizedIterator(Collection<T> dataList) {
        iterator = dataList.iterator();
        size = dataList.size();
    }
    
    public SynchronizedIterator(ScrollableResults scrollableResults, int size) {
        this.scrollableResults = scrollableResults;
        this.size = size;
    }

    /**
     * A synchronized implementation of Iterator.next(). Will return null if no more values are available
     * 
     * @return Returns the next element, or null if no more elements are found
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized T next() {

        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        
        } else if (scrollableResults!=null) {
            if (scrollableResults.next()) {
                return (T) scrollableResults.get(0);
            } else {
                return null;
            }
            
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