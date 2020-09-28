package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.service.base.PersistenceService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.meveo.service.base.PersistenceService.SEARCH_WILDCARD_OR;
import static org.meveo.service.base.PersistenceService.SEARCH_WILDCARD_OR_IGNORE_CAS;

public class ExpressionFactory {

    private QueryBuilder queryBuilder;
    private String tableNameAlias;

    public ExpressionFactory(QueryBuilder queryBuilder, String tableNameAlias) {

        this.queryBuilder = queryBuilder;
        this.tableNameAlias = tableNameAlias;
    }

    public void addFilters(Map<String, Object> filters, String key) {

        Object value = filters.get(key);
        if (value == null) {
            return;
        }

        String[] fieldInfo = key.split(" ");

        String condition = "eq";
        String fieldName = fieldInfo[0];
        String fieldName2 = null;

        if (fieldInfo.length == 2) {
            condition = fieldInfo[0];
            fieldName = fieldInfo[1];
        } else if (fieldInfo.length > 2) {
            condition = fieldInfo[0];
            fieldName = fieldInfo[1];
            fieldName2 = fieldInfo[2];
        }

        switch (condition) {
            case "fromRange":
                queryBuilder.addValueIsGreaterThanField(tableNameAlias + '.' + fieldName, value, false);
                break;
            case "fromOptionalRange":
                queryBuilder.addValueIsGreaterThanField(tableNameAlias + '.' + fieldName, value, true);
                break;
            case "toRange":
                queryBuilder.addValueIsLessThanField(tableNameAlias + '.' + fieldName, value, false, false);
                break;
            case "toRangeInclusive":
                queryBuilder.addValueIsLessThanField(tableNameAlias + '.' + fieldName, value, true, false);
                break;
            case "toOptionalRange":
                queryBuilder.addValueIsLessThanField(tableNameAlias + '.' + fieldName, value, false, true);
                break;
            case "toOptionalRangeInclusive":
                queryBuilder.addValueIsLessThanField(tableNameAlias + '.' + fieldName, value, true, true);
                break;
            case "list":
                queryBuilder.addListFilters(tableNameAlias, fieldName, value);
                break;
            case "inList":
                queryBuilder.addFieldInAListOfValues(tableNameAlias + '.' + fieldName, value, false, false);
                break;
            case "not-inList":
                queryBuilder.addFieldInAListOfValues(tableNameAlias + '.' + fieldName, value,  true, false);
                break;
            case "minmaxRange":
                queryBuilder.addValueInBetweenTwoFields(tableNameAlias + '.' + fieldName, tableNameAlias + '.' + fieldName2, value, false, false);
                break;
            case "minmaxRangeInclusive":
                queryBuilder.addValueInBetweenTwoFields(tableNameAlias + '.' + fieldName, tableNameAlias + '.' + fieldName2, value, true, false);
                break;
            case "minmaxOptionalRange":
                queryBuilder.addValueInBetweenTwoFields(tableNameAlias + '.' + fieldName, tableNameAlias + '.' + fieldName2, value, false, true);
                break;
            case "minmaxOptionalRangeInclusive":
                queryBuilder.addValueInBetweenTwoFields(tableNameAlias + '.' + fieldName, tableNameAlias + '.' + fieldName2, value, true, true);
                break;
            case "overlapOptionalRange":
                queryBuilder.addValueRangeOverlapTwoFieldRange(tableNameAlias + '.' + fieldName, tableNameAlias + '.' + fieldName2, fromValue(value), toValue(value), false);
                break;
            case "overlapOptionalRangeInclusive":
                queryBuilder.addValueRangeOverlapTwoFieldRange(tableNameAlias + '.' + fieldName, tableNameAlias + '.' + fieldName2, fromValue(value), toValue(value), true);
                break;
            case "likeCriterias":
                queryBuilder.addLikeCriteriasFilters(tableNameAlias, Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length), value);
                break;
            case SEARCH_WILDCARD_OR:
                queryBuilder.addSearchWildcardOrFilters(tableNameAlias, Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length), value);
                break;
            case SEARCH_WILDCARD_OR_IGNORE_CAS:
                queryBuilder.addSearchWildcardOrIgnoreCasFilters(tableNameAlias, Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length), value);
                break;
            default: {
                if (key.startsWith(PersistenceService.SEARCH_SQL))
                    queryBuilder.addSearchSqlFilters(value);
                else if (value instanceof String && PersistenceService.SEARCH_IS_NULL.equals(value))
                    queryBuilder.addSql(tableNameAlias + "." + fieldName + " is null ");
                else if (value instanceof String && PersistenceService.SEARCH_IS_NOT_NULL.equals(value))
                    queryBuilder.addSql(tableNameAlias + "." + fieldName + " is not null ");
                else if (value instanceof String || value instanceof Date || value instanceof Number || value instanceof Boolean || value instanceof Enum || value instanceof List)
                    queryBuilder.addValueIsEqualToField(tableNameAlias + "." + fieldName, value, condition.startsWith("ne"), condition.endsWith("Optional"));
            }
        }

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
