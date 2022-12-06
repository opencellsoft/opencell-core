/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

    private boolean strictNbRun;

    /**
     * Create list iterator. Specifies a number of items per iteration.
     * 
     * @param itemsPerSplit Items per split
     * @param listToSplit List to split. Null safe.
     */
    public SubListCreator(int itemsPerSplit, List<E> listToSplit) {

        if (itemsPerSplit < 1) {
            itemsPerSplit = 1;
        }

        if (listToSplit == null || listToSplit.isEmpty()) {
            hasNext = false;
            return;
        }

        this.theBigList = listToSplit;

        this.listSize = theBigList.size();
        this.blocToRun = itemsPerSplit;
        this.modulo = listToSplit.size() % itemsPerSplit;

        this.from = 0;
        this.to = this.listSize < this.blocToRun ? this.listSize : this.blocToRun;

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

        if (listToSplit == null || listToSplit.isEmpty()) {
            hasNext = false;
            return;
        }

        this.strictNbRun = true;
        this.theBigList = listToSplit;

        listSize = theBigList.size();
        if (nbSplits > listSize && listSize > 0) {
            nbSplits = listSize;
        }

        blocToRun = listSize / nbSplits;
        modulo = listSize % nbSplits;
       if( modulo > 0) {
           this.blocToRun = this.blocToRun+1;
       }
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
        if (strictNbRun && listSize - modulo == to) {
            to += modulo;
        } else if (to > listSize) {
            to = listSize;
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
