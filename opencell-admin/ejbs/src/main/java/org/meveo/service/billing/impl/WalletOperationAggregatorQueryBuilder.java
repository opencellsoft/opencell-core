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

/**
 * Generates the query use to group wallet operations with the given
 * {@link RatedTransactionsJobAggregationSetting}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class WalletOperationAggregatorQueryBuilder {

	private RatedTransactionsJobAggregationSetting aggregationSettings;
	private String groupBy = "";
	private String id = "";
	private String dateAggregateSelect = "";

	public WalletOperationAggregatorQueryBuilder(RatedTransactionsJobAggregationSetting aggregationSettings) {
		this.aggregationSettings = aggregationSettings;

		prepareQuery();
	}

	private void prepareQuery() {
		if (aggregationSettings.isAggregateByDay()) {
			// truncate by day
			dateAggregateSelect += ", YEAR(operation_date), MONTH(operation_date), DAY(operation_date)";
			groupBy += "YEAR(operation_date), MONTH(operation_date), DAY(operation_date)";

		} else {
			// truncate by month
			dateAggregateSelect += ", YEAR(operation_date), MONTH(operation_date), 0";
			groupBy += "YEAR(operation_date), MONTH(operation_date)";
		}

		switch (aggregationSettings.getAggregationLevel()) {
		case BA:
			groupBy += ", o.subscription.userAccount.billingAccount";
			id = "o.subscription.userAccount.billingAccount.id";
			break;

		case UA:
			groupBy += ", o.subscription.userAccount";
			id = "o.subscription.userAccount.id";
			break;

		case SUB:
			groupBy += ", o.subscription";
			id = "o.subscription.id";
			break;

		case SI:
			groupBy += ", o.serviceInstance";
			id = "o.serviceInstance.id";
			break;

		case CI:
			groupBy += ", o.chargeInstance";
			id = "o.chargeInstance.id";
			break;

		case DESC:
			groupBy += ", CONCAT(o.chargeInstance.id, '|', o.description)";
			id = "CONCAT(o.chargeInstance.id, '|', o.description)";
			break;

		default:
			groupBy += ", o.subscription.userAccount.billingAccount";
			id = "o.subscription.userAccount.billingAccount.id";
		}

		// additional criteria
		if (aggregationSettings.isAggregateByOrder()) {
			groupBy += ", o.orderNumber";
		}
		if (aggregationSettings.isAggregateByUnitAmount()) {
			groupBy += ", o.unitAmountWithoutTax";
		}
		if (aggregationSettings.isAggregateByParam1()) {
			groupBy += ", o.parameter1";
		}
		if (aggregationSettings.isAggregateByParam2()) {
			groupBy += ", o.parameter2";
		}
		if (aggregationSettings.isAggregateByParam3()) {
			groupBy += ", o.parameter3";
		}
		if (aggregationSettings.isAggregateByExtraParam()) {
			groupBy += ", o.parameterExtra";
		}
		
	}

	public String getParameter1Field() {
		return aggregationSettings.isAggregateByParam1() ? "o.parameter1" : "'NULL'";
	}

	public String getParameter2Field() {
		return aggregationSettings.isAggregateByParam2() ? "o.parameter2" : "'NULL'";
	}

	public String getParameter3Field() {
		return aggregationSettings.isAggregateByParam3() ? "o.parameter3" : "'NULL'";
	}

	public String getParameterExtraField() {
		return aggregationSettings.isAggregateByExtraParam() ? "o.parameterExtra" : "'NULL'";
	}

	public String getOrderNumberField() {
		return aggregationSettings.isAggregateByOrder() ? "o.orderNumber" : "'NULL'";
	}
	
	public String getUnitAmountField() {
		return aggregationSettings.isAggregateByUnitAmount() ? "o.unitAmountWithoutTax" : "(CAST(AVG(o.unitAmountWithoutTax) as big_decimal))";
	}

	public String getGroupQuery() {
		return "SELECT new org.meveo.service.billing.impl.AggregatedWalletOperation(" //
				+ "STRING_AGG(cast(o.id as string), ','), "
				+ "o.seller.id" //
				+ dateAggregateSelect //
				+ ", o.tax" //
				+ ", o.invoiceSubCategory" //
				+ ", " + id //
				+ ", SUM(o.amountWithTax), SUM(o.amountWithoutTax), SUM(o.amountTax)" //
				+ ", o.taxClass" //
				+ ", SUM(o.quantity)" //
				+ ", " + getUnitAmountField()//
				+ ", " + getOrderNumberField() //
				+ ", " + getParameter1Field() //
				+ ", " + getParameter2Field() //
				+ ", " + getParameter3Field() //
				+ ", " + getParameterExtraField() //
				+ ")" + " FROM WalletOperation o " //
				+ " WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate) AND o.status='OPEN' " //
				+ " GROUP BY o.seller.id, o.tax, o.taxClass, o.invoiceSubCategory, " + groupBy;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDateAggregateSelect() {
		return dateAggregateSelect;
	}

	public void setDateAggregateSelect(String dateAggregateSelect) {
		this.dateAggregateSelect = dateAggregateSelect;
	}

}
