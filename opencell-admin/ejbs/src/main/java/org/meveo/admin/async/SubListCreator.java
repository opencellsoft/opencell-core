/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;

/**
 * List iterator specified either by a number of iterations or by a number items in each iteration
 * 
 * @author anasseh
 * @author Andrius Karpavicius
 */
public class SubListCreator<E> {

    /**
     * Number of iterations
     */
    private int nbIterations = 1;

    /**
     * List to iterate over
     */
    private List<E> theBigList;

    /**
     * Has more iterations?
     */
    private boolean hasNext = true;

    /** From index . */
    private int from;

    /** To index . */
    private int to;

    /** Block to run . */

    private int blocToRun;

    /** Modulo. */
    private int modulo;

    /** Size of list. */
    private int listSize;

    /**
     * Create list iterator. Specifies a number of items per iteration.
     * 
     * @param itemsPerSplit Items per split
     * @param listToSplit List to split. Null safe.
     */
    public SubListCreator(int itemsPerSplit, List<E> listToSplit) {

        this(listToSplit, listToSplit != null ? (listToSplit.size() / itemsPerSplit) + 1 : 0);
    }

    /**
     * Create list iterator. Specifies a number of total iterations.
     * 
     * @param listToSplit List to split. Null safe.
     * @param nbSplits Number of splits. Defaults to 1 for Zero or a negative value.
     */
    public SubListCreator(List<E> listToSplit, int nbSplits) {
        if (nbSplits < 1) {
            nbSplits = 1;
        }

        if (listToSplit == null) {
            hasNext = false;
            return;
        }

        this.theBigList = listToSplit;
        this.nbIterations = nbSplits;

        listSize = theBigList.size();
        if (nbIterations > listSize && listSize > 0) {
            nbIterations = listSize;
        }

        blocToRun = listSize / nbIterations;
        modulo = listSize % nbIterations;
        from = 0;
        to = blocToRun;
        if (from == listSize) {
            this.hasNext = false;
        }
    }

    /**
     * @return list of next work set
     */
    public List<E> getNextWorkSet() {
        List<E> toRuns = theBigList.subList(from, to);
        from = to;
        to = from + blocToRun;
        if (listSize - modulo == to) {
            to += modulo;
        }
        if (from == listSize) {
            hasNext = false;
        }
        return toRuns;
    }

    /**
     * @return the hasNext
     */
    public boolean isHasNext() {
        return hasNext;
    }

    /**
     * @return the blocToRun
     */
    public int getBlocToRun() {
        return blocToRun;
    }

    /**
     * @return the listSize
     */
    public int getListSize() {
        return listSize;
    }

}
