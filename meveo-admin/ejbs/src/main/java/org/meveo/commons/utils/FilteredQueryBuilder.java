package org.meveo.commons.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

	public FilteredQueryBuilder() {

	}

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

	public Map<String, Object> getFilterConditions(FilterCondition filterCondition) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		getFilterConditions(filterCondition, result);

		return result;
	}

	/**
	 * Process the FilterCondition and return a map of key, value pair to be use
	 * in BaseBean. We only use primitive filter.
	 * 
	 * @param filterCondition
	 * @param result
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getFilterConditions(FilterCondition filterCondition, Map<String, Object> result) throws Exception {
		if (filterCondition instanceof OrCompositeFilterCondition) {
			OrCompositeFilterCondition tempFilter = (OrCompositeFilterCondition) filterCondition;
			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition fc : tempFilter.getFilterConditions()) {
					getFilterConditions(fc, result);
				}
			}
		} else if (filterCondition instanceof AndCompositeFilterCondition) {
			AndCompositeFilterCondition tempFilter = (AndCompositeFilterCondition) filterCondition;
			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition fc : tempFilter.getFilterConditions()) {
					getFilterConditions(fc, result);
				}
			}
		} else if (filterCondition instanceof PrimitiveFilterCondition) {
			PrimitiveFilterCondition tempFilter = (PrimitiveFilterCondition) filterCondition;
			if (tempFilter.getOperand().indexOf(".") != 0) {
				String enumClassName = (tempFilter.getOperand().substring(0, tempFilter.getOperand().lastIndexOf(".")));
				String enumValue = tempFilter.getOperand().substring(tempFilter.getOperand().lastIndexOf(".") + 1,
						tempFilter.getOperand().length());
				Class<? extends Enum> enumClass = (Class<? extends Enum>) Class.forName(enumClassName);
				result.put(tempFilter.getFieldName(), ReflectionUtils.getEnumFromString(enumClass, enumValue));
			} else {
				result.put(tempFilter.getFieldName(), tempFilter.getOperand());
			}
		} else if (filterCondition instanceof NativeFilterCondition) {
			// don't process native filter 
		}
	}

	private void processOrderCondition(OrderCondition orderCondition) {
		addOrderCriterion(StringUtils.join(orderCondition.getFieldNames(), ","), orderCondition.isAscending());
	}

}
