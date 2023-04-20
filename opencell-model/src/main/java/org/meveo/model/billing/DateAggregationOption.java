package org.meveo.model.billing;

import static java.util.Arrays.stream;

public enum DateAggregationOption {
    NO_DATE_AGGREGATION("no.date.aggregation"),
    DAY_OF_USAGE_DATE("day.of.usage.date"),
    WEEK_OF_USAGE_DATE("week.of.usage.date"),
    MONTH_OF_USAGE_DATE("month.of.usage.date");

    private String label;

    /**
     * 
     */
    private DateAggregationOption(String label) {
        setLabel(label);
    }

    public static DateAggregationOption fromValue(String value) {
        return stream(DateAggregationOption.values())
                .filter(option -> option.name().equalsIgnoreCase(value))
                .findFirst().orElseThrow(() -> new IllegalArgumentException());
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