package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;

import static java.util.Arrays.stream;

import java.util.Arrays;
import java.util.List;

public class AggregationConfiguration {

    /**
     * Is application running in B2B or B2C mode.
     */
    private boolean enterprise;

    /**
     * InvoiceLine Aggregation types
     */
    private List<AggregationOption> aggregationOptions = Arrays.asList(AggregationOption.NO_AGGREGATION);
    
    private DateAggregationOption dateAggregationOption = DateAggregationOption.MONTH_OF_USAGE_DATE;
    
    public AggregationConfiguration(boolean enterprise) {
        this.enterprise = enterprise;
    }

    public AggregationConfiguration(boolean enterprise, List<AggregationOption> aggregationOptions, DateAggregationOption dateAggregationOption) {
        this.enterprise = enterprise;
        this.aggregationOptions = aggregationOptions;
        this.dateAggregationOption=dateAggregationOption;
    }

    public boolean isEnterprise() {
        return enterprise;
    }

    public void setEnterprise(boolean enterprise) {
        this.enterprise = enterprise;
    }

    public List<AggregationOption> getAggregationOption() {
        return aggregationOptions;
    }

    public void setAggregationOption(List<AggregationOption> aggregationOptions) {
        this.aggregationOptions = aggregationOptions;
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

	public enum AggregationOption {
		NO_AGGREGATION("no.aggregation"), ARTICLE_LABEL("article.label"), UNIT_AMOUNT("unit.amount");
		private String label;

		/**
		 * 
		 */
		private AggregationOption(String label) {
			setLabel(label);
		}

		public static AggregationOption fromValue(String value) {
			return stream(AggregationOption.values()).filter(option -> option.name().equalsIgnoreCase(value))
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
	
	public enum DateAggregationOption {
		NO_DATE_AGGREGATION("no.date.aggregation"), MONTH_OF_USAGE_DATE("month.of.usage.date"), WEEK_OF_USAGE_DATE("week.of.usage.date"), DAY_OF_USAGE_DATE("day.of.usage.date");
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
