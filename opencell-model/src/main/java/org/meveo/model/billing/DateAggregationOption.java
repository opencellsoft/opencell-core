package org.meveo.model.billing;

import static java.util.Arrays.stream;

public enum DateAggregationOption {
    NO_DATE_AGGREGATION,
    DAY_OF_USAGE_DATE,
    WEEK_OF_USAGE_DATE,
    MONTH_OF_USAGE_DATE;



    public static DateAggregationOption fromValue(String value) {
        return stream(DateAggregationOption.values())
                .filter(option -> option.name().equalsIgnoreCase(value))
                .findFirst().orElseThrow(() -> new IllegalArgumentException());
    }
    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}