package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.service.base.PersistenceService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.meveo.service.base.PersistenceService.SEARCH_WILDCARD_OR;
import static org.meveo.service.base.PersistenceService.SEARCH_WILDCARD_OR_IGNORE_CAS;

public class ExpressionFactory {

    private QueryBuilder queryBuilder;
    private String tableNameAlias;

    public ExpressionFactory(QueryBuilder queryBuilder, String tableNameAlias) {

        this.queryBuilder = queryBuilder;
        this.tableNameAlias = tableNameAlias;
    }

    public Expression from(String key, Object value) {

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
                return new FromRangeExpression(tableNameAlias, fieldName, value);
            case "fromOptionalRange":
                return new FromOptionalRangeExpression(tableNameAlias, fieldName, value);
            case "toRange":
                return new ToRangeExpression(tableNameAlias, fieldName, value);
            case "toRangeInclusive":
                return new ToRangeInclusiveExpression(tableNameAlias, fieldName, value);
            case "toOptionalRange":
                return new ToOptionalRangeExpression(tableNameAlias, fieldName, value);
            case "toOptionalRangeInclusive":
                return new ToOptionalRangeInclusiveExpression(tableNameAlias, fieldName, value);
            case "list":
                return new ListExpression(tableNameAlias, fieldName, value);
            case "inList":
                return new InListExpression(tableNameAlias, fieldName, value);
            case "not-inList":
                return new NotInListExpression(tableNameAlias, fieldName, value);
            case "minmaxRange":
                return new MinMaxRangeExpression(tableNameAlias, fieldName, fieldName2, value);
            case "minmaxRangeInclusive":
                return new MinMaxRangeInclusiveExpression(tableNameAlias, fieldName, fieldName2, value);
            case "minmaxOptionalRange":
                return new MinMaxOptionalRangeExpression(tableNameAlias, fieldName, fieldName2, value);
            case "minmaxOptionalRangeInclusive":
                return new MinMaxOptionalRangeInclusiveExpression(tableNameAlias, fieldName, fieldName2, value);
            case "overlapOptionalRange":
                return new OverlapOptionalRangeExpression(tableNameAlias, fieldName, fieldName2, value);
            case "overlapOptionalRangeInclusive":
                return new OverlapOptionalRangeInclusiveExpression(tableNameAlias, fieldName, fieldName2, value);
            case "likeCriterias":
                return new LikeCriteriasExpression(tableNameAlias, Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length), value);
            case SEARCH_WILDCARD_OR:
                return new SearchWildcardOrExpression(tableNameAlias, Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length), value);
            case SEARCH_WILDCARD_OR_IGNORE_CAS:
                return new SearchWildcardOrIgnoreCasExpression(tableNameAlias, Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length), value);
            default: {
                if(key.startsWith(PersistenceService.SEARCH_SQL))
                    return new SearchSqlExpression(value);
                if(value instanceof String && PersistenceService.SEARCH_IS_NULL.equals(value))
                    return new SearchIsNullExpression(tableNameAlias, fieldName);
                if(value instanceof String && PersistenceService.SEARCH_IS_NOT_NULL.equals(value))
                    return new SearchIsNotNullExpression(tableNameAlias, fieldName);
                if(value instanceof String || value instanceof Date || value instanceof Number || value instanceof Boolean || value instanceof Enum || value instanceof List)
                    return new PrimitiveFieldExpression(tableNameAlias, fieldName, value, condition.startsWith("ne"), condition.endsWith("Optional"));
                return new DummyExpression();
            }
        }

    }
}
