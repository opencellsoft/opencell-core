package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

import static org.meveo.service.base.PersistenceService.FROM_JSON_FUNCTION;
import static org.meveo.service.base.PersistenceService.SEARCH_ATTR_TYPE_CLASS;

public class ExpressionFactory extends NativeExpressionFactory {

    public ExpressionFactory(QueryBuilder queryBuilder, String tableNameAlias) {
        super(queryBuilder, tableNameAlias);
    }

    @Override
    protected void checkOnCondition(String key, Object value, Expression exp){

        if (SEARCH_ATTR_TYPE_CLASS.equals(exp.getFieldName())) {
            addSearchTypeClassFilters(value, exp.getCondition());
        } else
            super.checkOnCondition(key, value, exp);

    }

    private void addSearchTypeClassFilters(Object value, String condition) {
        if (value instanceof Collection && !((Collection) value).isEmpty()) {
            List classes = new ArrayList<Class>();
            for (Object classNameOrClass : (Collection) value) {
                if (classNameOrClass instanceof Class) {
                    classes.add(classNameOrClass);
                } else {
                    try {
                        classes.add(Class.forName((String) classNameOrClass));
                    } catch (ClassNotFoundException e) {
                        log.error("Search by a type will be ignored - unknown class {}", (String) classNameOrClass);
                    }
                }
            }

            if (condition == null || "eq".equalsIgnoreCase(condition)) {
                queryBuilder.addSqlCriterion("type(a) in (:typeClass)", "typeClass", classes);
            } else if ("ne".equalsIgnoreCase(condition)) {
                queryBuilder.addSqlCriterion("type(a) not in (:typeClass)", "typeClass", classes);
            }

        } else if (value instanceof Class) {
            if (condition == null || "eq".equalsIgnoreCase(condition)) {
                queryBuilder.addSqlCriterion("type(a) = :typeClass", "typeClass", value);
            } else if ("ne".equalsIgnoreCase(condition)) {
                queryBuilder.addSqlCriterion("type(a) != :typeClass", "typeClass", value);
            }

        } else if (value instanceof String) {
            try {
                if (condition == null || "eq".equalsIgnoreCase(condition)) {
                    queryBuilder.addSqlCriterion("type(a) = :typeClass", "typeClass", Class.forName((String) value));
                } else if ("ne".equalsIgnoreCase(condition)) {
                    queryBuilder.addSqlCriterion("type(a) != :typeClass", "typeClass", Class.forName((String) value));
                }
            } catch (ClassNotFoundException e) {
                log.error("Search by a type will be ignored - unknown class {}", value);
            }
        }
    }

    @Override
    protected String extractFieldWithAlias(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return fieldName;
        }
        return fieldName.contains(FROM_JSON_FUNCTION) ? fieldName : super.extractFieldWithAlias(fieldName);
    }

    @Override
    protected void addFiltersToEntity(Object value, String condition, String fieldName) {
        this.queryBuilder.addCriterionEntity(extractFieldWithAlias(fieldName), value, condition.startsWith("ne") ? " != " : " = ", condition.endsWith("Optional"));
    }

    @Override
    public void addNullFilters(String fieldName, boolean isNot){
        if (isFieldCollection(fieldName)) {
            queryBuilder.addSql(extractFieldWithAlias(fieldName) + " is" +(isNot ? " not" : "") + " empty ");
        } else {
            super.addNullFilters(fieldName, isNot);
        }
    }

    @Override
    protected void addAuditableFilters(Object value){
        ((Map) value).forEach((k, mapValue) -> queryBuilder.addCriterionDateTruncatedToDay("a.auditable." + k, (Date) mapValue));
    }

    @Override
    protected void addListFilter(Object value, String fieldName, boolean notIn){
        // Searching for a list inside a list field requires to join it first as collection member e.g. "IN (a.sellers) seller"
        if (isFieldCollection(fieldName)) {

            String paramName = queryBuilder.convertFieldToParam(fieldName);
            String collectionItem = queryBuilder.convertFieldToCollectionMemberItem(fieldName);

            // this worked at first, but now complains about distinct clause, so switched to EXISTS clause instead.
            // queryBuilder.addCollectionMember(fieldName);
            // queryBuilder.addSqlCriterion(collectionItem + " IN (:" + paramName + ")", paramName, filterValue);

            String inListAlias = collectionItem + "Alias";
            queryBuilder.addSqlCriterion(" exists (select " + inListAlias + " from " + queryBuilder.getEntityClass().getName() + " " + inListAlias + ",IN (" + inListAlias + "." + fieldName + ") as " + collectionItem
                            + " where " + inListAlias + "=a and " + collectionItem + (notIn ? " NOT " : "") + " IN (:" + paramName + "))",
                    paramName, value);

        } else {

            queryBuilder.addFieldInAListOfValues(extractFieldWithAlias(fieldName), value, notIn, false);
        }
    }

    /**
     * @param fieldName
     * @return
     */
    private boolean isFieldCollection(String fieldName) {
        if (fieldName.contains(FROM_JSON_FUNCTION)) {
            return false;
        }
        final Class<?> entityClass = queryBuilder.getEntityClass();
        Field field = ReflectionUtils.getField(entityClass, fieldName);
        Class<?> fieldClassType = field.getType();
        return Collection.class.isAssignableFrom(fieldClassType);
    }
}
