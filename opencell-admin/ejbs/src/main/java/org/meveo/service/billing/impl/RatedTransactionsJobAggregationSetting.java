package org.meveo.service.billing.impl;

/**
 * The aggregation configuration of wallet operations during rating.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class RatedTransactionsJobAggregationSetting {

	/**
	 * Whether aggregation is enabled or not.
	 */
	private boolean enable = false;

	/**
	 * Global aggregation rather than by job run.
	 */
	private boolean aggregateGlobally = false;

	/**
	 * Aggregate by day or by month.
	 */
	private boolean aggregateByDay = true;

	/**
	 * Level of aggregation.
	 */
	private AggregationLevelEnum aggregationLevel = AggregationLevelEnum.BA;

	/**
	 * Whether to aggregate by order number.
	 */
	private boolean aggregateByOrder = false;

	/**
	 * Whether to aggregate by param 1.
	 */
	private boolean aggregateByParam1 = false;

	/**
	 * Whether to aggregate by param 2.
	 */
	private boolean aggregateByParam2 = false;

	/**
	 * Whether to aggregate by param 3.
	 */
	private boolean aggregateByParam3 = false;

	/**
	 * Whether to aggregate by extra param.
	 */
	private boolean aggregateByExtraParam = false;

	/*
	 * Aggregation level
	 * 
	 * <pre>
	 * BA - BillingAccount
	 * UA - UserAccount
	 * SUB - Subscription
	 * SI - ServiceInstance
	 * CI - ChargeInstance
	 * DESC - WalletOperation.description
	 * </pre>
	 */
	public enum AggregationLevelEnum {
		BA, UA, SUB, SI, CI, DESC
	}
	
	public RatedTransactionsJobAggregationSetting() {
		
	}

	public RatedTransactionsJobAggregationSetting(boolean enable, boolean aggregateGlobally, boolean aggregateByDay,
			AggregationLevelEnum aggregationLevel, boolean aggregateByOrder, boolean aggregateByParam1,
			boolean aggregateByParam2, boolean aggregateByParam3, boolean aggregateByExtraParam) {
		this.enable = enable;
		this.aggregateGlobally = aggregateGlobally;
		this.aggregateByDay = aggregateByDay;
		this.aggregationLevel = aggregationLevel;
		this.aggregateByOrder = aggregateByOrder;
		this.aggregateByParam1 = aggregateByParam1;
		this.aggregateByParam2 = aggregateByParam2;
		this.aggregateByParam3 = aggregateByParam3;
		this.aggregateByExtraParam = aggregateByExtraParam;
	}

	/**
	 * @return is aggregation enabled
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * @param enable
	 *            is aggregation enabled
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * @return is aggregation globally
	 */
	public boolean isAggregateGlobally() {
		return aggregateGlobally;
	}

	/**
	 * @param aggregateGlobally
	 *            is aggregation globally
	 */
	public void setAggregateGlobally(boolean aggregateGlobally) {
		this.aggregateGlobally = aggregateGlobally;
	}

	/**
	 * @return is aggregation by day
	 */
	public boolean isAggregateByDay() {
		return aggregateByDay;
	}

	/**
	 * @param aggregateByDay
	 *            is aggregation by day
	 */
	public void setAggregateByDay(boolean aggregateByDay) {
		this.aggregateByDay = aggregateByDay;
	}

	/**
	 * @return level of aggregation
	 */
	public AggregationLevelEnum getAggregationLevel() {
		return aggregationLevel;
	}

	/**
	 * @param aggregationLevel
	 *            level of aggregation
	 */
	public void setAggregationLevel(AggregationLevelEnum aggregationLevel) {
		this.aggregationLevel = aggregationLevel;
	}

	/**
	 * @return aggregate by order
	 */
	public boolean isAggregateByOrder() {
		return aggregateByOrder;
	}

	/**
	 * @param aggregateByOrder
	 *            aggregate by order
	 */
	public void setAggregateByOrder(boolean aggregateByOrder) {
		this.aggregateByOrder = aggregateByOrder;
	}

	/**
	 * @return aggregate by param1
	 */
	public boolean isAggregateByParam1() {
		return aggregateByParam1;
	}

	/**
	 * @param aggregateByParam1
	 *            aggregate by param1
	 */
	public void setAggregateByParam1(boolean aggregateByParam1) {
		this.aggregateByParam1 = aggregateByParam1;
	}

	/**
	 * @return aggregate by param2
	 */
	public boolean isAggregateByParam2() {
		return aggregateByParam2;
	}

	/**
	 * @param aggregateByParam2
	 *            aggregate by param2
	 */
	public void setAggregateByParam2(boolean aggregateByParam2) {
		this.aggregateByParam2 = aggregateByParam2;
	}

	/**
	 * @return aggregate by param3
	 */
	public boolean isAggregateByParam3() {
		return aggregateByParam3;
	}

	/**
	 * @param aggregateByParam3
	 *            aggregate by param3
	 */
	public void setAggregateByParam3(boolean aggregateByParam3) {
		this.aggregateByParam3 = aggregateByParam3;
	}

	/**
	 * @return aggregate by extra param
	 */
	public boolean isAggregateByExtraParam() {
		return aggregateByExtraParam;
	}

	/**
	 * @param aggregate
	 *            by extra param
	 */
	public void setAggregateByExtraParam(boolean aggregateByExtraParam) {
		this.aggregateByExtraParam = aggregateByExtraParam;
	}

	@Override
	public String toString() {
		return "RatedTransactionsJobAggregationSetting [enable=" + enable + ", aggregateGlobally=" + aggregateGlobally
				+ ", aggregateByDay=" + aggregateByDay + ", aggregationLevel=" + aggregationLevel
				+ ", aggregateByOrder=" + aggregateByOrder + ", aggregateByParam1=" + aggregateByParam1
				+ ", aggregateByParam2=" + aggregateByParam2 + ", aggregateByParam3=" + aggregateByParam3
				+ ", aggregateByExtraParam=" + aggregateByExtraParam + "]";
	}
}
