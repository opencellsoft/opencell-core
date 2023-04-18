package org.meveo.admin.async;

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.ScrollableResults;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Keeps track of a current position in iterator or scrollable results data source implementation
     */
    private int position;

    /**
     * Data iterator
     */
    private Iterator<T> iterator;

    private ScrollableResults scrollableResults;

    public SynchronizedIterator() {
    }

    /**
     * Constructor
     * 
     * @param dataList Data to iterate over
     */
    public SynchronizedIterator(Collection<T> dataList) {
        iterator = dataList.iterator();
        size = dataList.size();
    }

    /**
     * Constructor
     * 
     * @param scrollableResults Scrollable results
     * @param size A total number of records
     */
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
            position++;
            return iterator.next();

        } else if (scrollableResults != null) {
            try {
                if (scrollableResults.next()) {
                    position++;
                    return (T) scrollableResults.get(0);
                } else {
                    return null;
                }
            } catch (GenericJDBCException e) {
                Logger log = LoggerFactory.getLogger(getClass());
                log.error("Failed to scroll to the next record: {}", e.getMessage());
                return null;
            }

        } else {
            return null;
        }
    }

    /**
     * A synchronized implementation of Iterator.next(). Will return null if no more values are available
     * 
     * @return Returns the next element and a position in a list, or null if no more elements are found
     */
    @SuppressWarnings("unchecked")
    public synchronized NextItem<T> nextWPosition() {

        if (iterator != null && iterator.hasNext()) {
            NextItem<T> nextItem = new NextItem<T>(position, iterator.next());
            position++;

            return nextItem;

        } else if (scrollableResults != null) {
            if (scrollableResults.next()) {

                NextItem<T> nextItem = new NextItem<T>(position, (T) scrollableResults.get(0));
                position++;

                return nextItem;
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

    public class NextItem<T> {

        private int position;

        private T value;

        public NextItem(int position, T value) {
            this.position = position;
            this.value = value;
        }

        public int getPosition() {
            return position;
        }

        public T getValue() {
            return value;
        }
    }
}