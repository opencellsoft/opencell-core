package org.meveo.admin.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.ScrollableResults;

/**
 * Provides a one at a time access to iterator.getNext() function. Groups items into a list of items by some grouping criteria.
 * 
 * @author Andrius Karpavicius
 *
 * @param <T> Element class
 */
public abstract class SynchronizedIteratorGrouped<T> implements Iterator<List<T>> {

    /**
     * A number of total items
     */
    private int size;

    /**
     * Data iterator
     */
    private Iterator<T> iterator;

    private ScrollableResults scrollableResults;

    private T lastUnprocessedItem;

    public SynchronizedIteratorGrouped() {
    }

    /**
     * Constructor
     * 
     * @param dataList Data to iterate over
     */
    public SynchronizedIteratorGrouped(Collection<T> dataList) {
        iterator = dataList.iterator();
        size = dataList.size();
    }

    /**
     * Constructor
     * 
     * @param scrollableResults Scrollable results
     * @param size A total number of records
     */
    public SynchronizedIteratorGrouped(ScrollableResults scrollableResults, int size) {
        this.scrollableResults = scrollableResults;
        this.size = size;
    }

    /**
     * A synchronized implementation of Iterator.next(). Will return null if no more values are available
     * 
     * @return Returns the next element, or null if no more elements are found
     */
    @Override
    public synchronized List<T> next() {

        List<T> items = new ArrayList<T>();
        Object groupBy = null;

        T item = lastUnprocessedItem != null ? lastUnprocessedItem : nextSingle();
        if (item == null) {
            return null;
        }
        items.add(item);
        groupBy = getGroupByValue(item);

        while (true) {
            // no more items, so quit
            lastUnprocessedItem = nextSingle();
            if (lastUnprocessedItem == null) {
                return items;
            }
            // "Group by" value match, so continue
            Object groupByNext = getGroupByValue(lastUnprocessedItem);
            if ((groupBy == null && groupByNext == null) || (groupBy != null && groupBy.equals(groupByNext))) {
                items.add(lastUnprocessedItem);
                lastUnprocessedItem = null;
            } else {
                return items;
            }
        }
    }

    private T nextSingle() {

        if (iterator != null && iterator.hasNext()) {
            return iterator.next();

        } else if (scrollableResults != null) {
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

    /**
     * A function to return a value to group items by
     * 
     * @param item Item to group
     * @return A value to group by
     */
    public abstract Object getGroupByValue(T item);
}