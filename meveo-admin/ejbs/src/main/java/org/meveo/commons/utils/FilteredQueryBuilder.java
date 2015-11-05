package org.meveo.commons.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.LongValidator;
import org.meveo.admin.exception.FilterException;
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

	private ParamBean paramBean = ParamBean.getInstance();

	public FilteredQueryBuilder() {

	}

	public FilteredQueryBuilder(Filter filter) throws FilterException {
		this(filter, false, true);
	}

	public FilteredQueryBuilder(Filter filter, boolean export, boolean applyOrder) {
		super(ReflectionUtils.createObject(filter.getPrimarySelector().getTargetEntity()).getClass(), filter
				.getPrimarySelector().getAlias());

		if (filter.getFilterCondition() != null) {
			processFilterCondition(filter.getFilterCondition(), filter.getPrimarySelector().getAlias());
		}

		// order condition
		if (applyOrder && filter.getOrderCondition() != null) {
			processOrderCondition(filter.getOrderCondition(), filter.getPrimarySelector().getAlias());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void processFilterCondition(FilterCondition filterCondition, String alias) {
		if (filterCondition instanceof OrCompositeFilterCondition) {
			startOrClause();
			OrCompositeFilterCondition tempFilter = (OrCompositeFilterCondition) filterCondition;
			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition fc : tempFilter.getFilterConditions()) {
					processFilterCondition(fc, alias);
				}
			}
			endOrClause();
		} else if (filterCondition instanceof AndCompositeFilterCondition) {
			AndCompositeFilterCondition tempFilter = (AndCompositeFilterCondition) filterCondition;
			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition fc : tempFilter.getFilterConditions()) {
					processFilterCondition(fc, alias);
				}
			}
		} else if (filterCondition instanceof PrimitiveFilterCondition) {
			PrimitiveFilterCondition tempFilter = (PrimitiveFilterCondition) filterCondition;
			if (tempFilter.getOperand().indexOf("date:") != -1) {
				try {
					String strDateValue = tempFilter.getOperand().substring(5);
					Date dateValue = null;

					SimpleDateFormat sdf = new SimpleDateFormat(paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy"));
					try {
						dateValue = sdf.parse(strDateValue);
					} catch (ParseException e) {
						try {
							sdf = new SimpleDateFormat(
									paramBean.getProperty("meveo.dateTimeFormat", "dd/MM/yyyy HH:mm"));
							dateValue = sdf.parse(strDateValue);
						} catch (ParseException e1) {
							throw new FilterException(e1.getMessage());
						}
					}

					if (tempFilter.getOperator().equals("=")) {
						addCriterionDate(tempFilter.getFieldName(), dateValue);
					} else if (tempFilter.getOperator().equals(">=")) {
						addCriterionDateRangeFromTruncatedToDay(tempFilter.getFieldName(), dateValue);
					} else if (tempFilter.getOperator().equals("<=")) {
						addCriterionDateRangeToTruncatedToDay(tempFilter.getFieldName(), dateValue);
					}
				} catch (Exception e) {
					throw new FilterException(e.getMessage());
				}
			} else if (tempFilter.getOperand().indexOf("enum:") != -1) {
				String enumClassName = (tempFilter.getOperand().substring(5, tempFilter.getOperand().lastIndexOf(".")));
				String enumValue = tempFilter.getOperand().substring(tempFilter.getOperand().lastIndexOf(".") + 1,
						tempFilter.getOperand().length());
				Class<? extends Enum> enumClass = null;
				try {
					enumClass = (Class<? extends Enum>) Class.forName(enumClassName);
					if (tempFilter.getFieldName().indexOf(".") == -1) {
						addCriterionEntity(alias + "." + tempFilter.getFieldName(),
								ReflectionUtils.getEnumFromString(enumClass, enumValue));
					} else {
						addCriterionEntity(tempFilter.getFieldName(),
								ReflectionUtils.getEnumFromString(enumClass, enumValue));
					}
				} catch (ClassNotFoundException e) {
					throw new FilterException(e.getMessage());
				}
			} else if (tempFilter.getOperand().indexOf("bool:") != -1) {
				addBooleanCriterion(tempFilter.getFieldName(),
						BooleanUtils.toBooleanObject(tempFilter.getOperand().substring(5)));
			} else if (tempFilter.getOperator().equalsIgnoreCase("LIKE")) {
				if (tempFilter.getFieldName().indexOf(".") == -1) {
					like(alias + "." + tempFilter.getFieldName(), tempFilter.getOperand(),
							QueryLikeStyleEnum.MATCH_BEGINNING, true);
				} else {
					like(tempFilter.getFieldName(), tempFilter.getOperand(), QueryLikeStyleEnum.MATCH_BEGINNING, true);
				}
			} else {
				if (NumberUtils.isNumber(tempFilter.getOperand())) {
					Long lv = LongValidator.getInstance().validate(tempFilter.getOperand());
					if (lv != null) {
						if (tempFilter.getFieldName().indexOf(".") == -1) {
							addCriterion(alias + "." + tempFilter.getFieldName(), tempFilter.getOperator(), lv, true);
						} else {
							addCriterion(tempFilter.getFieldName(), tempFilter.getOperator(), lv, true);
						}
					} else {
						BigDecimal bdv = BigDecimalValidator.getInstance().validate(tempFilter.getOperand());
						if (bdv != null) {
							if (tempFilter.getFieldName().indexOf(".") == -1) {
								addCriterion(alias + "." + tempFilter.getFieldName(), tempFilter.getOperator(), bdv,
										true);
							} else {
								addCriterion(tempFilter.getFieldName(), tempFilter.getOperator(), bdv, true);
							}
						} else {
							Integer iv = IntegerValidator.getInstance().validate(tempFilter.getOperand());
							if (iv != null) {
								if (tempFilter.getFieldName().indexOf(".") == -1) {
									addCriterion(alias + "." + tempFilter.getFieldName(), tempFilter.getOperator(), iv,
											true);
								} else {
									addCriterion(tempFilter.getFieldName(), tempFilter.getOperator(), iv, true);
								}
							}
						}
					}
				} else {
					if (tempFilter.getFieldName().indexOf(".") == -1) {
						addCriterion(alias + "." + tempFilter.getFieldName(), tempFilter.getOperator(),
								tempFilter.getOperand(), true);
					} else {
						addCriterion(tempFilter.getFieldName(), tempFilter.getOperator(), tempFilter.getOperand(), true);
					}
				}
			}
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

}
