package org.meveo.admin.job;

import org.meveo.model.billing.DateAggregationOption;

public class AggregationConfiguration {

	/**
	 * Is application running in B2B or B2C mode.
	 */
	private boolean enterprise;

	private DateAggregationOption dateAggregationOption = DateAggregationOption.MONTH_OF_USAGE_DATE;

	private boolean aggregationPerUnitAmount;

	public AggregationConfiguration(boolean enterprise) {
		this.enterprise = enterprise;
	}

	public AggregationConfiguration(boolean enterprise, boolean AggregationPerUnitAmount, DateAggregationOption dateAggregationOption) {
		this.enterprise = enterprise;
		this.aggregationPerUnitAmount = AggregationPerUnitAmount;
		this.dateAggregationOption = dateAggregationOption;
	}

	public boolean isEnterprise() {
		return enterprise;
	}

	public void setEnterprise(boolean enterprise) {
		this.enterprise = enterprise;
	}

	/**
	 * @return the dateAggregationOptions
	 */
	public DateAggregationOption getDateAggregationOption() {
		return dateAggregationOption;
	}

	/**
	 * @param dateAggregationOptions the dateAggregationOptions to set
	 */
	public void setDateAggregationOption(DateAggregationOption dateAggregationOption) {
		this.dateAggregationOption = dateAggregationOption;
	}

	/**
	 * @return the AggregationPerUnitAmount
	 */
	public boolean isAggregationPerUnitAmount() {
		return aggregationPerUnitAmount;
	}

	/**
	 * @param AggregationPerUnitAmount the AggregationPerUnitAmount to set
	 */
	public void setAggregationPerUnitAmount(boolean AggregationPerUnitAmount) {
		this.aggregationPerUnitAmount = AggregationPerUnitAmount;
	}
}
