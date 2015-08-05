package org.meveo.commons.utils;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.LongValidator;
import org.meveo.model.filter.AndCompositeFilterCondition;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.NativeFilterCondition;
import org.meveo.model.filter.OrCompositeFilterCondition;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.PrimitiveFilterCondition;

/**
 * @author Edward P. Legaspi
 **/
public class FilteredQueryBuilder extends QueryBuilder {

	public FilteredQueryBuilder(Filter filter) {
		this(filter, false);
	}

	public FilteredQueryBuilder(Filter filter, boolean export) {
		super(ReflectionUtils.createObject(filter.getPrimarySelector().getTargetEntity()).getClass(), filter
				.getPrimarySelector().getAlias());

		if (filter.getFilterCondition() != null) {
			processFilterCondition(filter.getFilterCondition());
		}

		// order condition
		if (filter.getOrderCondition() != null) {
			processOrderCondition(filter.getOrderCondition());
		}
	}

	private void processFilterCondition(FilterCondition filterCondition) {
		if (filterCondition instanceof OrCompositeFilterCondition) {
			startOrClause();
			OrCompositeFilterCondition tempFilter = (OrCompositeFilterCondition) filterCondition;
			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition fc : tempFilter.getFilterConditions()) {
					processFilterCondition(fc);
				}
			}
			endOrClause();
		} else if (filterCondition instanceof AndCompositeFilterCondition) {
			AndCompositeFilterCondition tempFilter = (AndCompositeFilterCondition) filterCondition;
			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition fc : tempFilter.getFilterConditions()) {
					processFilterCondition(fc);
				}
			}
		} else if (filterCondition instanceof PrimitiveFilterCondition) {
			PrimitiveFilterCondition tempFilter = (PrimitiveFilterCondition) filterCondition;
			if (tempFilter.getOperator().equalsIgnoreCase("LIKE")) {
				like(tempFilter.getFieldName(), tempFilter.getOperand(), QueryLikeStyleEnum.MATCH_BEGINNING, true);
			} else {
				if (NumberUtils.isNumber(tempFilter.getOperand())) {
					Long lv = LongValidator.getInstance().validate(tempFilter.getOperand());
					if (lv != null) {
						addCriterion(tempFilter.getFieldName(), tempFilter.getOperator(), lv, true);
					} else {
						BigDecimal bdv = BigDecimalValidator.getInstance().validate(tempFilter.getOperand());
						if (bdv != null) {
							addCriterion(tempFilter.getFieldName(), tempFilter.getOperator(), bdv, true);
						} else {
							Integer iv = IntegerValidator.getInstance().validate(tempFilter.getOperand());
							if (iv != null) {
								addCriterion(tempFilter.getFieldName(), tempFilter.getOperator(), iv, true);
							}
						}
					}
				} else {
					addCriterion(tempFilter.getFieldName(), tempFilter.getOperator(), tempFilter.getOperand(), true);
				}
			}
		} else if (filterCondition instanceof NativeFilterCondition) {
			NativeFilterCondition tempFilter = (NativeFilterCondition) filterCondition;
			addSql(tempFilter.getJpql());
		}
	}

	private void processOrderCondition(OrderCondition orderCondition) {
		addOrderCriterion(StringUtils.join(orderCondition.getFieldNames(), ","), orderCondition.isAscending());
	}

}
