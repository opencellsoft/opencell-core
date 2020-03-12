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

package org.meveo.commons.utils;

import java.util.Map;

import org.meveo.admin.exception.FilterException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.AndCompositeFilterCondition;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.NativeFilterCondition;
import org.meveo.model.filter.OrCompositeFilterCondition;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.PrimitiveFilterCondition;
import org.meveo.service.filter.processor.PrimitiveFilterProcessor;
import org.meveo.service.filter.processor.PrimitiveFilterProcessorFactory;

/**
 * @author Edward P. Legaspi
 **/
public class FilteredQueryBuilder extends QueryBuilder {

	private Filter filter;
	private  Map<CustomFieldTemplate, Object> parameterMap;

	public FilteredQueryBuilder() {

	}

	public FilteredQueryBuilder(Filter filter) throws FilterException {
		this(filter, null, false, true);
	}

	public FilteredQueryBuilder(Filter filter, Map<CustomFieldTemplate, Object> parameterMap, boolean export, boolean applyOrder) {
		super(ReflectionUtils.createObject(filter.getPrimarySelector().getTargetEntity()).getClass(), filter.getPrimarySelector().getAlias(),null);

		this.filter = filter;
		this.parameterMap = parameterMap;

		if (filter.getFilterCondition() != null) {
			processFilterCondition(filter.getFilterCondition(), filter.getPrimarySelector().getAlias());
		}

		// order condition
		if (applyOrder && filter.getOrderCondition() != null) {
			processOrderCondition(filter.getOrderCondition(), filter.getPrimarySelector().getAlias());
		}
	}
	
	private void processFilterCondition(FilterCondition filterCondition, String alias) {
		if (filterCondition instanceof OrCompositeFilterCondition) {
			startOrClause();
			OrCompositeFilterCondition tempFilter = (OrCompositeFilterCondition) filterCondition;
			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition condition : tempFilter.getFilterConditions()) {
					processFilterCondition(condition, alias);
				}
			}
			endOrClause();
		} else if (filterCondition instanceof AndCompositeFilterCondition) {
			AndCompositeFilterCondition tempFilter = (AndCompositeFilterCondition) filterCondition;
			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition condition : tempFilter.getFilterConditions()) {
					processFilterCondition(condition, alias);
				}
			}
		} else if (filterCondition instanceof PrimitiveFilterCondition) {
			PrimitiveFilterCondition condition = (PrimitiveFilterCondition) filterCondition;
			PrimitiveFilterProcessor processor = PrimitiveFilterProcessorFactory.getInstance().getProcessor(condition);
			processor.process(this, alias, condition);
		} else if (filterCondition instanceof NativeFilterCondition) {
			NativeFilterCondition tempFilter = (NativeFilterCondition) filterCondition;
			addSql(tempFilter.getJpql());
		}
	}

	public void processOrderCondition(OrderCondition orderCondition, String alias) {
		if (orderCondition == null)
			return;

		StringBuffer sb = new StringBuffer();
		for (String field : orderCondition.getFieldNames()) {
			if (field.indexOf(".") == -1) {
				sb.append(alias + "." + field + ",");
			} else {
				sb.append(field + ",");
			}
		}
		sb.deleteCharAt(sb.length() - 1);

		addOrderCriterion(sb.toString(), orderCondition.isAscending());
	}

	public Filter getFilter() {
		return filter;
	}

	public Map<CustomFieldTemplate, Object> getParameterMap() {
		return parameterMap;
	}

}
