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
 * Provides a one at a time access to iterator.getNext() function. Collects items into a list of items by some decision making criteria. Will return at least one item, irrelevant was decision was re
 * 
 * @author Andrius Karpavicius
 *
 * @param <T> Element class
 */
public abstract class SynchronizedMultiItemIterator<T> implements Iterator<List<T>> {

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
     * Data iterator
     */
    private Iterator<T> iterator;

    private ScrollableResults scrollableResults;

    private T lastUnprocessedItem;

    public SynchronizedMultiItemIterator() {
    }

    /**
     * Constructor
     * 
     * @param dataList Data to iterate over
     */
    public SynchronizedMultiItemIterator(Collection<T> dataList) {
        iterator = dataList.iterator();
        size = dataList.size();
    }

    /**
     * Constructor
     * 
     * @param scrollableResults Scrollable results
     * @param size A total number of records
     */
    public SynchronizedMultiItemIterator(ScrollableResults scrollableResults, int size) {
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
    public SynchronizedMultiItemIterator(ScrollableResults scrollableResults, int size, boolean isMultipleColumns, List<String> fieldNames) {
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
    @Override
    public synchronized List<T> next() {

        List<T> items = new ArrayList<T>();

        T item = lastUnprocessedItem != null ? lastUnprocessedItem : nextSingle();
        if (item == null) {
            return null;
        }
        items.add(item);
        initializeDecisionMaking(item);

        boolean includeNextItem = true;

        while (includeNextItem) {
            // no more items, so quit
            lastUnprocessedItem = nextSingle();
            if (lastUnprocessedItem == null) {
                return items;
            }
            // "Group by" value match, so continue
            includeNextItem = isIncludeItem(lastUnprocessedItem);
            if (includeNextItem) {
                items.add(lastUnprocessedItem);
                lastUnprocessedItem = null;
            } else {
                break;
            }
        }

        return items;
    }

    private T nextSingle() {

        if (iterator != null && iterator.hasNext()) {
            return iterator.next();

        } else if (scrollableResults != null) {
            try {
                if (scrollableResults.next()) {
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

    /**
     * A method to initialize any decision making variables at the start of iterator.Next() method
     * 
     * @param item First item to be included
     */
    public abstract void initializeDecisionMaking(T item);

    /**
     * A function to determine if item should be included.
     * 
     * @param item Item to include
     * @return True if item should be included in a current collection
     */
    public abstract boolean isIncludeItem(T item);
}