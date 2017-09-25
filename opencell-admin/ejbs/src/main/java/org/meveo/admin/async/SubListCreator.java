/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;

/**
 * @author anasseh
 *
 */

public class SubListCreator {

	/** number of threads. */
	private int nbThreads = 1;

	/** list to split. */
	private List<?> theBigList;

	/** has next value to proceed . */
	private boolean hasNext = true;

	/** from index . */
	private int from;

	/** to index . */
	private int to;

	/** block to run . */

	private int blocToRun;

	/** modulo. */
	private int modulo;

	/** size of list. */
	private int listSize;

	/**
	 * @param theList
	 *            list to split
	 * @param nbRuns
	 *            number of run
	 * @throws Exception
	 *             exception
	 */
	public SubListCreator(List<?> theList, int nbRuns) throws Exception {
		if (nbRuns < 1) {
			throw new Exception("nbRuns should not be < 1 ");
		}

		if (theList == null) {
			throw new Exception("The list should not be null");
		}
		
		this.theBigList = theList;
		this.nbThreads = nbRuns;

		listSize = theBigList.size();
		if (nbThreads > listSize && listSize > 0) {
			nbThreads = listSize;
		}
		
		blocToRun = listSize / nbThreads;
		modulo = listSize % nbThreads;
		from = 0;
		to = blocToRun;
		if (from == listSize) {
			this.hasNext = false;
		}
	}

	/**
	 * @return list of next work set
	 */
	public List<?> getNextWorkSet() {
		List<?> toRuns = theBigList.subList(from, to);
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
