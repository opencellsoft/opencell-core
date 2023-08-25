package org.meveo.admin.job;

import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.DiscountAggregationModeEnum;

public class AggregationConfiguration {

    /**
     * Is application running in B2B or B2C mode.
     */
    private boolean enterprise;

    /**
     * Aggregate by date option
     */
    private DateAggregationOption dateAggregationOption = DateAggregationOption.MONTH_OF_USAGE_DATE;

    /**
     * Aggregate per unit amount
     */
    private boolean aggregationPerUnitAmount;

    /**
     * Aggregate based on accounting article label instead of RT description
     */
    private boolean useAccountingArticleLabel = false;

    /**
     * If TRUE, aggregation will ignore subscription field (multiple subscriptions will be aggregated together)
     */
    private boolean ignoreSubscriptions = true;

    /**
     * If TRUE, aggregation will ignore order field (multiple orders will be aggregated together)
     */
    private boolean ignoreOrders = true;

    /**
     * Aggregation mode of Discount type Rated Transactions
     */
    private DiscountAggregationModeEnum discountAggregation = DiscountAggregationModeEnum.FULL_AGGREGATION;

    private BillingEntityTypeEnum type = BillingEntityTypeEnum.BILLINGACCOUNT;

    /**
     * @return Aggregate based on accounting article label instead of RT description
     */
    public boolean isUseAccountingArticleLabel() {
        return useAccountingArticleLabel;
    }

    /**
     * @param useAccountingArticleLabel Aggregate based on accounting article label instead of RT description
     */
    public void setUseAccountingArticleLabel(boolean useAccountingArticleLabel) {
        this.useAccountingArticleLabel = useAccountingArticleLabel;
    }

    /**
     * @return If TRUE, aggregation will ignore subscription field (multiple subscriptions will be aggregated together)
     */
    public boolean isIgnoreSubscriptions() {
        return ignoreSubscriptions;
    }

    /**
     * @param ignoreSubscriptions If TRUE, aggregation will ignore subscription field (multiple subscriptions will be aggregated together)
     */
    public void setIgnoreSubscriptions(boolean ignoreSubscriptions) {
        this.ignoreSubscriptions = ignoreSubscriptions;
    }

    /**
     * @return If TRUE, aggregation will ignore order field (multiple orders will be aggregated together)
     */
    public boolean isIgnoreOrders() {
        return ignoreOrders;
    }

    /**
     * @param ignoreOrders If TRUE, aggregation will ignore order field (multiple orders will be aggregated together)
     */
    public void setIgnoreOrders(boolean ignoreOrders) {
        this.ignoreOrders = ignoreOrders;
    }

    /**
     * @return Aggregation mode of Discount type Rated Transactions
     */
    public DiscountAggregationModeEnum getDiscountAggregation() {
        return discountAggregation;
    }

    public AggregationConfiguration(boolean enterprise) {
        this.enterprise = enterprise;
    }

    public AggregationConfiguration(boolean enterprise, boolean AggregationPerUnitAmount, DateAggregationOption dateAggregationOption) {
        this.enterprise = enterprise;
        this.aggregationPerUnitAmount = AggregationPerUnitAmount;
        this.dateAggregationOption = dateAggregationOption;
    }

    public AggregationConfiguration(BillingCycle billingCycle) {
        this.dateAggregationOption = billingCycle.getDateAggregation() != null ? billingCycle.getDateAggregation() : DateAggregationOption.NO_DATE_AGGREGATION;
        this.aggregationPerUnitAmount = billingCycle.isAggregateUnitAmounts();
        this.useAccountingArticleLabel = billingCycle.isUseAccountingArticleLabel();
        this.ignoreSubscriptions = billingCycle.isIgnoreSubscriptions();
        this.ignoreOrders = billingCycle.isIgnoreOrders();
        this.discountAggregation = billingCycle.getDiscountAggregation();
        this.type = billingCycle.getType();
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
     * @return Aggregate per unit amount
     */
    public boolean isAggregationPerUnitAmount() {
        return aggregationPerUnitAmount;
    }

    /**
     * @param AggregationPerUnitAmount Aggregate per unit amount
     */
    public void setAggregationPerUnitAmount(boolean AggregationPerUnitAmount) {
        this.aggregationPerUnitAmount = AggregationPerUnitAmount;
    }

    public BillingEntityTypeEnum getType() {
        return type;
    }

    @Override
    public String toString() {
        return "AggregationConfiguration [enterprise : " + enterprise + ", dateAggregationOption : " + dateAggregationOption + ", aggregationPerUnitAmount : " + aggregationPerUnitAmount + ", useAccountingArticleLabel : "
                + useAccountingArticleLabel + ", ignoreSubscriptions : " + ignoreSubscriptions + ", ignoreOrders : " + ignoreOrders + ", BillingEntityTypeEnum : " + type + "]";
    }
}
