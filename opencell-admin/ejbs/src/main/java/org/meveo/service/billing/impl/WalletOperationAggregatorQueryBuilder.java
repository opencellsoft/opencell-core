package org.meveo.service.billing.impl;

import org.meveo.model.billing.WalletOperation;

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

	/**
	 * Returns the chargeInstance.id. Special case since chargeInstance.id is
	 * concatenated with walletOperation's description.
	 * 
	 * @return the charge instance id
	 */
	private String getComputedId() {
		return id.contains("|") ? "o.chargeInstance.id" : id;
	}

	public String listWoQuery(Long idValue) {
		return "SELECT o FROM WalletOperation o" //
				+ " WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate) AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN" //
				+ " AND " + getComputedId() + "=" + idValue;
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

	public String getGroupQuery() {
		return "SELECT new org.meveo.service.billing.impl.AggregatedWalletOperation(" //
				+ "o.seller.id" //
				+ dateAggregateSelect //
				+ ", o.tax" //
				+ ", o.invoiceSubCategory" //
				+ ", " + id //
				+ ", SUM(o.amountWithTax), SUM(o.amountWithoutTax), SUM(o.amountTax)" //
				+ ", SUM(o.unitAmountWithTax), SUM(o.unitAmountWithoutTax), SUM(o.unitAmountTax), SUM(o.quantity)" //
				+ ", " + getOrderNumberField() //
				+ ", " + getParameter1Field() //
				+ ", " + getParameter2Field() //
				+ ", " + getParameter3Field() //
				+ ", " + getParameterExtraField() //
				+ ")" + " FROM " + WalletOperation.class.getSimpleName() + " o" //
				+ " WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate) AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN" //
				+ " GROUP BY o.seller.id, o.tax, o.invoiceSubCategory, " + groupBy;
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
