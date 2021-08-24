package org.meveo.model.accounting;

public enum SubAccountingPeriodTypeEnum {
	MONTHLY(12), QUARTERLY(4), HALFYEARLY(2);

	private int numberOfPeriodsPerYear;

	/**
	 * @param numberOfPeriodsPerYear
	 */
	private SubAccountingPeriodTypeEnum(int numberOfPeriodsPerYear) {
		this.numberOfPeriodsPerYear = numberOfPeriodsPerYear;
	}

	/**
	 * @return the numberOfPeriodsPerYear
	 */
	public int getNumberOfPeriodsPerYear() {
		return numberOfPeriodsPerYear;
	}

}
