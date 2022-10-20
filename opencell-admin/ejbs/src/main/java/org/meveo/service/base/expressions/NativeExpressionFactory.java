package org.meveo.service.base.expressions;

import static org.meveo.service.base.PersistenceService.SEARCH_WILDCARD_OR;
import static org.meveo.service.base.PersistenceService.SEARCH_WILDCARD_OR_IGNORE_CAS;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.criteria.JoinType;

import org.meveo.admin.util.pagination.FilterOperatorEnum;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.UniqueEntity;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeExpressionFactory {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected QueryBuilder queryBuilder;
    private String tableNameAlias;

    public NativeExpressionFactory(QueryBuilder queryBuilder, String tableNameAlias) {

        this.queryBuilder = queryBuilder;
        this.tableNameAlias = tableNameAlias;
    }

    public void addFilters(String key, Object value) {
    	if(key.endsWith(".id")) {
        	Object ids = (value instanceof Collection)? ((Collection)value).stream().map(x->Long.parseLong(x.toString())).collect(Collectors.toList()):Long.parseLong(value.toString());
            checkOnCondition(key, ids, new ExpressionParser(key.split(" ")));
    	} else {
    		checkOnCondition(key, value, new ExpressionParser(key.split(" ")));
    	}
    }

    protected void checkOnCondition(String key, Object value, ExpressionParser exp) {

        switch (exp.getCondition()) {
            case "fromRange":
                queryBuilder.addValueIsGreaterThanOrEqualField(extractFieldWithAlias(exp.getFieldName()), value, false);
                break;
            case "fromRangeExclusive":
                queryBuilder.addValueIsGreaterThanField(extractFieldWithAlias(exp.getFieldName()), value, false);
                break;
            case "fromOptionalRange":
                queryBuilder.addValueIsGreaterThanField(extractFieldWithAlias(exp.getFieldName()), value, true);
                break;
            case "toRange":
                queryBuilder.addValueIsLessThanField(extractFieldWithAlias(exp.getFieldName()), value, false, false);
                break;
            case "toRangeInclusive":
                queryBuilder.addValueIsLessThanField(extractFieldWithAlias(exp.getFieldName()), value, true, false);
                break;
            case "toOptionalRange":
                queryBuilder.addValueIsLessThanField(extractFieldWithAlias(exp.getFieldName()), value, false, true);
                break;
            case "toOptionalRangeInclusive":
                queryBuilder.addValueIsLessThanField(extractFieldWithAlias(exp.getFieldName()), value, true, true);
                break;
            case PersistenceService.SEARCH_LIST:
                queryBuilder.addListFilters(exp.getFieldName(), value);
                break;
            case "listInList":
                queryBuilder.addListInList(exp.getFieldName(), value);
            case "inList":
                addInListFilter(value, exp.getFieldName(), false);
                break;
            case "not-inList":
                addInListFilter(value, exp.getFieldName(), true);
                break;
            case "minmaxRange":
                queryBuilder.addValueInBetweenTwoFields(extractFieldWithAlias(exp.getFieldName()), extractFieldWithAlias(exp.getFieldName2()), value, false, false);
                break;
            case "minmaxRangeInclusive":
                queryBuilder.addValueInBetweenTwoFields(extractFieldWithAlias(exp.getFieldName()), extractFieldWithAlias(exp.getFieldName2()), value, true, false);
                break;
            case "minmaxOptionalRange":
                queryBuilder.addValueInBetweenTwoFields(extractFieldWithAlias(exp.getFieldName()), extractFieldWithAlias(exp.getFieldName2()), value, false, true);
                break;
            case "minmaxOptionalRangeInclusive":
                queryBuilder.addValueInBetweenTwoFields(extractFieldWithAlias(exp.getFieldName()), extractFieldWithAlias(exp.getFieldName2()), value, true, true);
                break;
            case "overlapOptionalRange":
                queryBuilder.addValueRangeOverlapTwoFieldRange(extractFieldWithAlias(exp.getFieldName()), extractFieldWithAlias(exp.getFieldName2()), fromValue(value), toValue(value), false);
                break;
            case "overlapOptionalRangeInclusive":
                queryBuilder.addValueRangeOverlapTwoFieldRange(extractFieldWithAlias(exp.getFieldName()), extractFieldWithAlias(exp.getFieldName2()), fromValue(value), toValue(value), true);
                break;
            case "likeCriterias":
                queryBuilder.addLikeCriteriasFilters(tableNameAlias, exp.getAllFields(), value);
                break;
            case SEARCH_WILDCARD_OR:
                queryBuilder.addSearchWildcardOrFilters(tableNameAlias, exp.getAllFields(), value);
                break;
            case "or":
            case SEARCH_WILDCARD_OR_IGNORE_CAS:
                queryBuilder.addSearchWildcardOrIgnoreCasFilters(tableNameAlias, exp.getAllFields(), value);
                break;
            default: {
            	if (key.matches("^\\$filter[0-9]+$")) {
            		Map<String, Object> nestedFilterItems = (Map<String, Object>) value;
            		FilterOperatorEnum operator = (FilterOperatorEnum) nestedFilterItems.getOrDefault("$operator", FilterOperatorEnum.AND);
            		queryBuilder.startNestedFilter(operator);
                    nestedFilterItems.keySet().stream().filter(orItemKey -> nestedFilterItems.get(orItemKey) != null && !"$OPERATOR".equalsIgnoreCase(orItemKey)).forEach(orItemKey -> addFilters(orItemKey, nestedFilterItems.get(orItemKey)));
            		queryBuilder.endNestedFilter();
                } else if (key.startsWith(PersistenceService.SEARCH_SQL)) {
                    queryBuilder.addSearchSqlFilters(value);
                } else if (key.startsWith(PersistenceService.SEARCH_OR)) {
                    queryBuilder.startOrClause();

                    Map<String, Object> orClauseItems = (Map<String, Object>) value;
                    orClauseItems.keySet().stream().filter(orItemKey -> orClauseItems.get(orItemKey) != null).forEach(orItemKey -> addFilters(orItemKey, orClauseItems.get(orItemKey)));

                    queryBuilder.endOrClause();

                } else if (key.startsWith(PersistenceService.ANY_MATCH)) {
                	queryBuilder.startOrClause();
                	queryBuilder.setJoinType(JoinType.LEFT);
                	Map<String, Object> orClauseItems = Stream.of(key.split(" ")).skip(1).collect(Collectors.toMap(s -> s, s -> value));
                	orClauseItems.keySet().stream().filter(orItemKey -> orClauseItems.get(orItemKey) != null).forEach(orItemKey -> addFilters(orItemKey, orClauseItems.get(orItemKey)));
                	queryBuilder.endOrClause();

                } else if (value instanceof String && PersistenceService.SEARCH_IS_NULL.equals(value)) {
                    addNullFilters(exp.getFieldName(), false);
                } else if (value instanceof String && PersistenceService.SEARCH_IS_NOT_NULL.equals(value)) {
                    addNullFilters(exp.getFieldName(), true);
                } else if (BaseEntity.class.isAssignableFrom(value.getClass()) || value instanceof UniqueEntity || value instanceof IEntity) {
                    addFiltersToEntity(value, exp.getCondition(), exp.getFieldName());
                } else if ("auditable".equalsIgnoreCase(exp.getFieldName()) && value instanceof Map) {
                    addAuditableFilters(value);
                } else if (value instanceof String || value instanceof Date || value instanceof Number || value instanceof Boolean || value instanceof Enum || value instanceof List) {
                    queryBuilder.addValueIsEqualToField(extractFieldWithAlias(exp.getFieldName()), value, exp.getCondition().startsWith("ne"), exp.getCondition().endsWith("Optional"));
                }
            }
        }
    }

    protected void addInListFilter(Object value, String fieldName, boolean notIn) {
        queryBuilder.addFieldInAListOfValues(extractFieldWithAlias(fieldName), value, notIn, false);
    }

    protected void addFiltersToEntity(Object value, String condition, String fieldName) {
    }

    protected void addNullFilters(String fieldName, boolean isNot){
        queryBuilder.addSql(extractFieldWithAlias(fieldName) + " is" + (isNot ? " not" : "") + " null ");
    }

    protected void addAuditableFilters(Object value){
    }

    protected String extractFieldWithAlias(String fieldName) {
        return tableNameAlias + '.' + fieldName;
    }

    private Object fromValue(Object value) {
        if (value.getClass().isArray()) {
            return ((Object[]) value)[0];

        } else if (value instanceof List) {
            return ((List) value).get(0);
        }
        return null;
    }

    private Object toValue(Object value) {
        if (value.getClass().isArray()) {
            return ((Object[]) value)[1];

        } else if (value instanceof List) {
            return ((List) value).get(1);
        }
        return null;
    }
}
