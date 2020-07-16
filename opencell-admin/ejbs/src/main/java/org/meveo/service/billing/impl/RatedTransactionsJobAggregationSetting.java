/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.billing.impl;

import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.filter.Filter;

import java.io.Serializable;

/**
 * The aggregation configuration of wallet operations during rating.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class RatedTransactionsJobAggregationSetting implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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
	 * Whether to aggregate by unit amount.
	 */
	private boolean aggregateByUnitAmount = false;

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
	/**
	 * The aggregation matrix to aggregate multiple WO in one RT
	 */
	private WalletOperationAggregationSettings walletOperationAggregationSettings;
	/**
	 * Filter WO
	 */
	private Filter filter;
	/**
	 * EL expression to get other aggregation setting
	 */
	private String aggregationKeyEl;
	/**
	 * Aggregat by continious periods
	 */
	private boolean periodAggregation;

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
			AggregationLevelEnum aggregationLevel, boolean aggregateByOrder, boolean aggregateByUnitAmount, boolean aggregateByParam1,
			boolean aggregateByParam2, boolean aggregateByParam3, boolean aggregateByExtraParam) {
		this.enable = enable;
		this.aggregateGlobally = aggregateGlobally;
		this.aggregateByDay = aggregateByDay;
		this.aggregationLevel = aggregationLevel;
		this.aggregateByOrder = aggregateByOrder;
		this.aggregateByParam1 = aggregateByParam1;
		this.aggregateByParam2 = aggregateByParam2;
		this.aggregateByParam3 = aggregateByParam3;
		this.aggregateByUnitAmount=aggregateByUnitAmount;
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
	 * @param aggregateByExtraParam
	 *            by extra param
	 */
	public void setAggregateByExtraParam(boolean aggregateByExtraParam) {
		this.aggregateByExtraParam = aggregateByExtraParam;
	}

	@Override
	public String toString() {
		return "RatedTransactionsJobAggregationSetting [enable=" + enable + ", aggregateGlobally=" + aggregateGlobally
				+ ", aggregateByDay=" + aggregateByDay + ", aggregationLevel=" + aggregationLevel
				+ ", aggregateByOrder=" + aggregateByOrder + ", aggregateByUnitAmount=" + aggregateByUnitAmount
				+ ", aggregateByParam1=" + aggregateByParam1 + ", aggregateByParam2=" + aggregateByParam2
				+ ", aggregateByParam3=" + aggregateByParam3 + ", aggregateByExtraParam=" + aggregateByExtraParam + "]";
	}

	/**
	 * @return the aggregateByUnitAmount
	 */
	public boolean isAggregateByUnitAmount() {
		return aggregateByUnitAmount;
	}

	/**
	 * @param aggregateByUnitAmount the aggregateByUnitAmount to set
	 */
	public void setAggregateByUnitAmount(boolean aggregateByUnitAmount) {
		this.aggregateByUnitAmount = aggregateByUnitAmount;
	}

	public WalletOperationAggregationSettings getWalletOperationAggregationSettings() {
		return walletOperationAggregationSettings;
	}

	public void setWalletOperationAggregationSettings(WalletOperationAggregationSettings walletOperationAggregationSettings) {
		this.walletOperationAggregationSettings = walletOperationAggregationSettings;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public String getAggregationKeyEl() {
		return aggregationKeyEl;
	}

	public void setAggregationKeyEl(String aggregationKeyEl) {
		this.aggregationKeyEl = aggregationKeyEl;
	}

	public boolean isPeriodAggregation() {
		return periodAggregation;
	}

	public void setPeriodAggregation(boolean periodAggregation) {
		this.periodAggregation = periodAggregation;
	}
}
