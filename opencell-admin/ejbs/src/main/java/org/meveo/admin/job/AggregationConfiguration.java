package org.meveo.admin.job;

import static java.util.Arrays.stream;

import org.meveo.admin.exception.BusinessException;

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

	public enum DateAggregationOption {
		NO_DATE_AGGREGATION("no.date.aggregation"), MONTH_OF_USAGE_DATE("month.of.usage.date"),
		WEEK_OF_USAGE_DATE("week.of.usage.date"), DAY_OF_USAGE_DATE("day.of.usage.date");
		private String label;

		/**
		 * 
		 */
		private DateAggregationOption(String label) {
			setLabel(label);
		}

		public static DateAggregationOption fromValue(String value) {
			return stream(DateAggregationOption.values()).filter(option -> option.name().equalsIgnoreCase(value))
					.findFirst().orElseThrow(() -> new BusinessException());
		}

		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}
	}

}
