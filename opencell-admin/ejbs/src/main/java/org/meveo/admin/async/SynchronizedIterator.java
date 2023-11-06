package org.meveo.admin.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * Are multiple columns returned in a resultset. Used to construct an array of values as next() result.
     */
    private boolean isMultipleColumns;

    /**
     * List of field names corresponding to the order of resultset columns
     */
    List<String> fieldNames;

    /**
     * Keeps track of a current position in iterator or scrollable results data source implementation
     */
    private int position;

    /**
     * Keeps an "optimistic" track if there is any data more to retrieve
     */
    private boolean hasMore = true;

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
        this(scrollableResults, size, false, null);
    }

    /**
     * Constructor
     * 
     * @param scrollableResults Scrollable results
     * @param size A total number of records
     * @param isMultipleColumns Read multiple columns
     * @param fieldNames List of field names corresponding to the order of resultset columns
     */
    public SynchronizedIterator(ScrollableResults scrollableResults, int size, boolean isMultipleColumns, List<String> fieldNames) {
        this.scrollableResults = scrollableResults;
        this.size = size;
        this.isMultipleColumns = isMultipleColumns;
        this.fieldNames = fieldNames;
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
                        if (isMultipleColumns) {
                            // Return an array of data
                            if (fieldNames == null) {
                                return (T) scrollableResults.get();
                                // Or mapped by a fieldname
                            } else {

                                Object[] data = scrollableResults.get();

                                Map<String, Object> mappedData = new HashMap<>();
                                for (int i = 0; i < data.length; i++) {
                                    String dataShort = data[i] != null ? data[i].toString() : "null";
                                    dataShort = dataShort.substring(0, dataShort.length() > 20 ? 20 : dataShort.length());
                                    mappedData.put(fieldNames.get(i), data[i]);
                                }
                                return (T) mappedData;
                            }

                        } else {
                            return (T) scrollableResults.get(0);
                        }

                    } else {
                        hasMore = false;
                        return null;
                    }
                } catch (GenericJDBCException e) {
                    Logger log = LoggerFactory.getLogger(getClass());
                    log.error("Failed to scroll to the next record: {}", e.getMessage());
                    hasMore = false;
                    return null;
                }

            } else {
                hasMore = false;
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
                            if (isMultipleColumns) {

                                // Return an array of data
                                if (fieldNames == null) {
                                    item = (T) scrollableResults.get();
                                    // Or mapped by a fieldname
                                } else {

                                    Object[] data = scrollableResults.get();

                                    Map<String, Object> mappedData = new HashMap<>();
                                    for (int k = 0; k < data.length; k++) {
                                        mappedData.put(fieldNames.get(k), data[k]);
                                    }
                                    item = (T) mappedData;
                                }

                            } else {
                                item = (T) scrollableResults.get(0);
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
                hasMore = false;
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

                T item = null;

                if (isMultipleColumns) {

                    // Return an array of data
                    if (fieldNames == null) {
                        item = (T) scrollableResults.get();
                        // Or mapped by a fieldname
                    } else {

                        Object[] data = scrollableResults.get();

                        Map<String, Object> mappedData = new HashMap<>();
                        for (int k = 0; k < data.length; k++) {
                            mappedData.put(fieldNames.get(k), data[k]);
                        }
                        item = (T) mappedData;
                    }

                } else {
                    item = (T) scrollableResults.get(0);
                }

                NextItem<T> nextItem = new NextItem<T>(position, item);
                position++;

                return nextItem;
            } else {
                hasMore = false;
                return null;
            }

        } else {
            hasMore = false;
            return null;
        }
    }

    /**
     * An "optimistic" response if there is any data more to retrieve. Will return <b>False</n> ONLY if the last call to next() returned no data.
     */
    public boolean hasNext() {
        return hasMore;
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