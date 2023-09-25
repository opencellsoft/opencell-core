package org.meveo.admin.job;

import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.DateAggregationOption;

public class AggregationConfiguration {

	/**
	 * Is application running in B2B or B2C mode.
	 */
	private boolean enterprise;

	private DateAggregationOption dateAggregationOption = DateAggregationOption.MONTH_OF_USAGE_DATE;

	private boolean aggregationPerUnitAmount;
	
    private boolean useAccountingArticleLabel = false;
    
    private boolean ignoreSubscriptions = true;
    
    private boolean ignoreOrders = true;
    
    private boolean ignoreUserAccounts = true;

	private BillingEntityTypeEnum type = BillingEntityTypeEnum.BILLINGACCOUNT;

	public boolean isUseAccountingArticleLabel() {
		return useAccountingArticleLabel;
	}

	public void setUseAccountingArticleLabel(boolean useAccountingArticleLabel) {
		this.useAccountingArticleLabel = useAccountingArticleLabel;
	}

	public boolean isIgnoreSubscriptions() {
		return ignoreSubscriptions;
	}

	public void setIgnoreSubscriptions(boolean ignoreSubscriptions) {
		this.ignoreSubscriptions = ignoreSubscriptions;
	}

	public boolean isIgnoreOrders() {
		return ignoreOrders;
	}

	public void setIgnoreOrders(boolean ignoreOrders) {
		this.ignoreOrders = ignoreOrders;
	}

	public boolean isIgnoreUserAccounts() {
		return ignoreUserAccounts;
	}

	public void setIgnoreUserAccounts(boolean ignoreUserAccounts) {
		this.ignoreUserAccounts = ignoreUserAccounts;
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
		this.dateAggregationOption = billingCycle.getDateAggregation()!=null? billingCycle.getDateAggregation() : DateAggregationOption.NO_DATE_AGGREGATION;
		this.aggregationPerUnitAmount= billingCycle.isAggregateUnitAmounts();
		this.useAccountingArticleLabel = billingCycle.isUseAccountingArticleLabel() ;
		this.ignoreSubscriptions = billingCycle.isIgnoreSubscriptions();
		this.ignoreOrders = billingCycle.isIgnoreOrders();
		this.ignoreUserAccounts = billingCycle.isIgnoreUserAccounts();
		this.type=billingCycle.getType();
	}
	
	public AggregationConfiguration(BillingRun billingRun) {
		this.dateAggregationOption = billingRun.getDateAggregation()!=null? billingRun.getDateAggregation() : DateAggregationOption.NO_DATE_AGGREGATION;
		this.aggregationPerUnitAmount= billingRun.isAggregateUnitAmounts();
		this.useAccountingArticleLabel = billingRun.isUseAccountingArticleLabel() ;
		this.ignoreSubscriptions = billingRun.isIgnoreSubscriptions();
		this.ignoreOrders = billingRun.isIgnoreOrders();
		this.ignoreUserAccounts = billingRun.isIgnoreUserAccounts();
		this.type=billingRun.getBillingCycle().getType();
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

	public BillingEntityTypeEnum getType() {
		// TODO Auto-generated method stub
		return type ;
	}

	@Override
	public String toString() {
		return "AggregationConfiguration [enterprise : " + enterprise + ", dateAggregationOption : " + dateAggregationOption
				+ ", aggregationPerUnitAmount : " + aggregationPerUnitAmount + ", useAccountingArticleLabel : " + useAccountingArticleLabel
				+ ", ignoreSubscriptions : " + ignoreSubscriptions + ", ignoreOrders : " + ignoreOrders  + ", BillingEntityTypeEnum : " + type + "]";
	}
}
