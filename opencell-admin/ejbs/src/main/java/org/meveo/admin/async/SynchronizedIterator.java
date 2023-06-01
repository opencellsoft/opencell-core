package org.meveo.admin.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
     * A number of columns that resultset returns. Used to construct an array of values as next() result.
     */
    private int numberOfColumns;

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
        this(scrollableResults, size, 1);
    }

    /**
     * Constructor
     * 
     * @param scrollableResults Scrollable results
     * @param size A total number of records
     * @param numberOfColumns Number of columns that resultset returns
     */
    public SynchronizedIterator(ScrollableResults scrollableResults, int size, int numberOfColumns) {
        this.scrollableResults = scrollableResults;
        this.size = size;
        this.numberOfColumns = numberOfColumns;
    }

    /**
     * A synchronized implementation of Iterator.next(). Will return null if no more values are available
     * 
     * @return Returns the next element, or null if no more elements are found
     */
    @SuppressWarnings("unchecked")
    @Override
    public T next() {

        synchronized (this) {
            if (iterator != null && iterator.hasNext()) {
                position++;
                return iterator.next();

            } else if (scrollableResults != null) {
                try {
                    if (scrollableResults.next()) {
                        position++;
                        if (numberOfColumns == 1) {
                            return (T) scrollableResults.get(0);

                        } else {
                            return (T) scrollableResults.get();
                        }

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
    }

    /**
     * A synchronized implementation of Iterator.next(). Will return null if no more values are available
     * 
     * @param nrItems Number of items to return
     * @return Returns a list of the next X elements, or null if no more elements are found
     */
    @SuppressWarnings("unchecked")
    public List<T> next(int nrItems) {

        synchronized (this) {

            List<T> items = new ArrayList<T>(nrItems);

            for (int i = 0; i < nrItems; i++) {
                T item = null;
                if (iterator != null && iterator.hasNext()) {
                    position++;
                    item = iterator.next();

                } else if (scrollableResults != null) {
                    try {
                        if (scrollableResults.next()) {
                            position++;
                            if (numberOfColumns == 1) {
                                item = (T) scrollableResults.get(0);
                            } else {
                                item = (T) scrollableResults.get();
                            }
                        } else {
                            break;
                        }
                    } catch (GenericJDBCException e) {
                        Logger log = LoggerFactory.getLogger(getClass());
                        log.error("Failed to scroll to the next record: {}", e.getMessage());
                        break;
                    }

                } else {
                    break;
                }
                items.add(item);
            }
            if (items.isEmpty()) {
                return null;
            } else {
                return items;
            }
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

                T item = numberOfColumns == 1 ? (T) scrollableResults.get(0) : (T) scrollableResults.get();

                NextItem<T> nextItem = new NextItem<T>(position, item);
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