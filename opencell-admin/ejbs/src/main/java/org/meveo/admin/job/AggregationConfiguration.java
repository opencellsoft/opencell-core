package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;

import static java.util.Arrays.stream;

public class AggregationConfiguration {

    /**
     * Is application running in B2B or B2C mode.
     */
    private boolean enterprise;

    /**
     * InvoiceLine Aggregation types
     */
    private AggregationOption aggregationOption;

    public AggregationConfiguration(boolean enterprise, AggregationOption aggregationOption) {
        this.enterprise = enterprise;
        this.aggregationOption = aggregationOption;
    }

    public boolean isEnterprise() {
        return enterprise;
    }

    public void setEnterprise(boolean enterprise) {
        this.enterprise = enterprise;
    }

    public AggregationOption getAggregationOption() {
        return aggregationOption;
    }

    public void setAggregationOption(AggregationOption aggregationOption) {
        this.aggregationOption = aggregationOption;
    }

    enum AggregationOption {
        NO_AGGREGATION, ARTICLE_LABEL, UNIT_AMOUNT, DATE;

        public static AggregationOption fromValue(String value) {
            return stream(AggregationOption.values())
                        .filter(option -> option.name().equalsIgnoreCase(value))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException());
        }
    }

}
