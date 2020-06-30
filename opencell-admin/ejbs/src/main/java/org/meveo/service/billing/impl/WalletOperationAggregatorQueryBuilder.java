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

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationActionEnum;
import org.meveo.model.billing.WalletOperationAggregationLine;
import org.meveo.model.billing.WalletOperationAggregationMatrix;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.util.List;

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
	private String select = "";

	public WalletOperationAggregatorQueryBuilder(RatedTransactionsJobAggregationSetting aggregationSettings) {
		this.aggregationSettings = aggregationSettings;

		prepareQuery();
	}

	private void prepareQuery() {
		WalletOperationAggregationMatrix aggregationMatrix = aggregationSettings.getWalletOperationAggregationMatrix();
		List<WalletOperationAggregationLine> aggregationLines = aggregationMatrix.getAggregationLines();
		for (WalletOperationAggregationLine aggregationLine : aggregationLines) {
			select += getSelect(aggregationLine);
			groupBy += getQueryGroupBy(aggregationLine);
		}
		if (!StringUtils.isBlank(select)) {
			select = select.substring(0, select.length() - 2);
		}
		if (!StringUtils.isBlank(groupBy)) {
			groupBy = groupBy.substring(0, groupBy.length() - 2);
		}
	}

	private String getQueryGroupBy(WalletOperationAggregationLine aggregationLine) {
		String field = aggregationLine.getField();
		if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.KEY)) {
			return "o." + field + ".id, ";
		}

		if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.TRUNCATE)) {
			if (aggregationLine.getValue().equalsIgnoreCase("day")) {
				return "YEAR(o." + field + "), MONTH(o." + field + "), DAY(o." + field + "), ";
			}
			if (aggregationLine.getValue().equalsIgnoreCase("month")) {
				return "YEAR(o." + field + "), MONTH(o." + field + "), ";
			}
			if (aggregationLine.getValue().equalsIgnoreCase("year")) {
				return "YEAR(o." + field + "), ";
			}
		}
		return "";
	}

	private String getSelect(WalletOperationAggregationLine aggregationLine) {
		String selectStr = "";
		String field = aggregationLine.getField();

		if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.EMPTY)) {
			selectStr = "";
		} else if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.KEY)) {
			selectStr = "o." + field + ".id as " + getFieldSuffix(field);
		} else if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.TRUNCATE)) {
			if (aggregationLine.getValue().equalsIgnoreCase("day")) {
				selectStr = "YEAR(o." + field + ") as year, MONTH(o." + field + ") as month, DAY(o." + field + ") as day ";
			}
			if (aggregationLine.getValue().equalsIgnoreCase("month")) {
				selectStr = "YEAR(o." + field + ") as year, MONTH(o." + field + ") as month, 1 as day ";
			}
			if (aggregationLine.getValue().equalsIgnoreCase("year")) {
				selectStr = "YEAR(o." + field + "), as year, 0 as month, 1 as day ";
			}
		} else if (aggregationLine.getAction().equals(WalletOperationAggregationActionEnum.VALUE)) {
			selectStr = field + "as " + field;
		} else {
			selectStr = aggregationLine.getAction() + "(o." + field + ") as " + field;
		}
		if (StringUtils.isBlank(selectStr)) {
			return "";
		} else {
			return selectStr + ", ";
		}
	}

	private String getFieldSuffix(String field) {
		if (field.contains(".")) {
			String[] parts = field.split(".");
			return parts[parts.length - 1];
		} else {
			return field;
		}
	}

	public String getParameter1Field() {
		return aggregationSettings.isAggregateByParam1() ? "o.parameter_1" : "'NULL'";
	}

	public String getParameter2Field() {
		return aggregationSettings.isAggregateByParam2() ? "o.parameter_2" : "'NULL'";
	}

	public String getParameter3Field() {
		return aggregationSettings.isAggregateByParam3() ? "o.parameter_3" : "'NULL'";
	}

	public String getParameterExtraField() {
		return aggregationSettings.isAggregateByExtraParam() ? "o.parameter_extra" : "'NULL'";
	}

	public String getOrderNumberField() {
		return aggregationSettings.isAggregateByOrder() ? "o.orderNumber" : "'NULL'";
	}
	
	public String getUnitAmountField() {
		return aggregationSettings.isAggregateByUnitAmount() ? "o.unit_amount_withoutTax" : "AVG(o.unit_amount_without_tax)";
	}

	public String getGroupQuery() {
		return "SELECT " //
				+ "STRING_AGG(cast(o.id as string), ','), " //
				+ select //
				+ " FROM WalletOperationPeriod o " //
				+ " WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate) AND o.status='OPEN' " //
				+ " GROUP BY " + groupBy;

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
