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

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.model.IdentifiableEnum;
import org.meveo.model.transformer.AliasToEntityOrderedMapResultTransformer;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.primefaces.model.SortOrder;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Query builder class for building JPA queries.
 * 
 * <p>
 * Usage example:
 * <p>
 * new QueryBuilder(AClass.class, "a").addCriterionWildcard("a.commercialStatus", commercialStatus, true).addCriterionEnum( "a.billingStatus",
 * billingStatus).addCriterionEnum("a.networkStatus", networkStatus).addCriterionEntity("a.terminalInstance", terminalInstance) .addPaginationConfiguration(configuration);
 * 
 * @author Richard Hallier
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
public class QueryBuilder {

    protected StringBuffer q;

    protected String alias;

    protected Map<String, Object> params;

    private boolean hasOneOrMoreCriteria;

    private boolean inOrClause;

    private int nbCriteriaInOrClause;

    protected PaginationConfiguration paginationConfiguration;

    private String paginationSortAlias;

    private Class<?> clazz;
    
    static final String FROM = "from ";

    public Class<?> getEntityClass() {
        return clazz;
    }

    public enum QueryLikeStyleEnum {
        /**
         * Field match value as provided (value already contains wildcard characters
         */
        MATCH_EQUAL,

        /**
         * Add a wildcard to the end of the value to match field values that start with a value provided
         */
        MATCH_BEGINNING,

        /**
         * Add a wildcard to the start and end of the value to match field values that start or end with a value provided
         */
        MATCH_ANYWHERE
    }

    public QueryBuilder() {

    }

    /**
     * Constructor.
     * 
     * @param sql Sql.
     */
    public QueryBuilder(String sql) {
        this(sql, null);
    }

    /**
     * Constructor.
     * 
     * @param sql Sql
     * @param alias Alias of a main table
     */
    public QueryBuilder(String sql, String alias) {
        q = new StringBuffer(sql);
        this.alias = alias;
        params = new HashMap<String, Object>();
        hasOneOrMoreCriteria = false;
        inOrClause = false;
        nbCriteriaInOrClause = 0;
    }

    /**
     * Constructor.
     * 
     * @param qb Query builder.
     */
    public QueryBuilder(QueryBuilder qb) {
        this.q = new StringBuffer(qb.q);
        this.alias = qb.alias;
        this.params = new HashMap<String, Object>(qb.params);
        this.hasOneOrMoreCriteria = qb.hasOneOrMoreCriteria;
        this.inOrClause = qb.inOrClause;
        this.nbCriteriaInOrClause = qb.nbCriteriaInOrClause;
    }

    /**
     * Constructor.
     * 
     * @param clazz Class for which query is created.
     * @param alias Alias of a main table.
     * @param fetchFields Additional (list/map type) fields to fetch
     */
    public QueryBuilder(Class<?> clazz, String alias, List<String> fetchFields) {
        this(getInitQuery(clazz, alias, fetchFields), alias);
        this.clazz = clazz;
    }

    /**
     * Constructor.
     * 
     * @param clazz Class for which query is created.
     * @param alias Alias of a main table.
     * @param fetchFields Additional (list/map type) fields to fetch
     * @param joinFields Field on which joins should be made
     */
    public QueryBuilder(Class<?> clazz, String alias, List<String> fetchFields, List<String> joinFields) {
        this(getInitJoinQuery(clazz, alias, fetchFields, joinFields), alias);
        this.clazz = clazz;
    }

    /**
     * @param clazz name of class
     * @param alias alias for entity
     * @param fetchFields list of field need to be fetched.
     * @param joinFields list of field need to joined
     * @return SQL query.
     */
    private static String getInitJoinQuery(Class<?> clazz, String alias, List<String> fetchFields, List<String> joinFields) {
        StringBuilder query = new StringBuilder("from " + clazz.getName() + " " + alias);
        if (fetchFields != null && !fetchFields.isEmpty()) {
            for (String fetchField : fetchFields) {
                query.append(" left join fetch " + alias + "." + fetchField);
            }
        }

        if (joinFields != null && !joinFields.isEmpty()) {
            for (String joinField : joinFields) {
                query.append(" inner join " + alias + "." + joinField + " " + joinField);
            }
        }

        return query.toString();
    }

    /**
     * @param clazz name of class
     * @param alias alias for entity
     * @param fetchFields list of field need to be fetched.
     * @return SQL query.
     */
    private static String getInitQuery(Class<?> clazz, String alias, List<String> fetchFields) {
        StringBuilder query = new StringBuilder("from " + clazz.getName() + " " + alias);
        if (fetchFields != null && !fetchFields.isEmpty()) {
            for (String fetchField : fetchFields) {
                query.append(" left join fetch " + alias + "." + fetchField);
            }
        }

        return query.toString();
    }

    /**
     * @return string buffer for SQL
     */
    public StringBuffer getSqlStringBuffer() {
        return q;
    }

    /**
     * @param paginationConfiguration pagination configuration
     * @return instance of QueryBuilder
     */
    public QueryBuilder addPaginationConfiguration(PaginationConfiguration paginationConfiguration) {
        return addPaginationConfiguration(paginationConfiguration, null);
    }

    /**
     * @param paginationConfiguration pagination configuration
     * @param sortAlias alias for sort.
     * @return instance of QueryBuilder
     */
    public QueryBuilder addPaginationConfiguration(PaginationConfiguration paginationConfiguration, String sortAlias) {
        this.paginationSortAlias = sortAlias;
        this.paginationConfiguration = paginationConfiguration;
        return this;
    }

    /**
     * @param sql SQL command
     * @return instance of QueryBuilder
     */
    public QueryBuilder addSql(String sql) {
        return addSqlCriterion(sql, null, null);
    }

    public void addLikeCriteriasFilters(String tableNameAlias, String[] fields, Object value){
        startOrClause();
        if (value instanceof String) {
            String filterString = (String) value;
            Stream.of(fields)
                    .forEach(f -> addCriterionWildcard(tableNameAlias + "." + f, filterString, true));
        }
        endOrClause();
    }

    public void addSearchWildcardOrFilters(String tableNameAlias, String[] fields, Object value){
        startOrClause();
        Stream.of(fields)
                .forEach(field -> addSql(tableNameAlias + "." + field + " like '%" + value + "%'"));
        endOrClause();
    }

    public void addSearchWildcardOrIgnoreCasFilters(String tableNameAlias, String[] fields, Object value){
        startOrClause();
        Stream.of(fields)
                .forEach(field -> addSql("lower(" + tableNameAlias + "." + field + ") like '%" + String.valueOf(value).toLowerCase() + "%'"));
        endOrClause();
    }

    public void addSearchSqlFilters(Object value) {
        if (value.getClass().isArray()) {
            String additionalSql = (String) ((Object[]) value)[0];
            Object[] additionalParameters = Arrays.copyOfRange(((Object[]) value), 1, ((Object[]) value).length);
            addSqlCriterionMultiple(additionalSql, additionalParameters);
        } else {
            addSql((String) value);
        }
    }

    /**
     * @param sql SQL command
     * @param param param to pass for query
     * @param value value of param
     * @return instance of QueryBuilder
     */
    public QueryBuilder addSqlCriterion(String sql, String param, Object value) {
        if (param != null && StringUtils.isBlank(value)) {
            return this;
        }

        if (hasOneOrMoreCriteria) {
            if (inOrClause && nbCriteriaInOrClause != 0) {
                q.append(" or ");
            } else {
                q.append(" and ");
            }
        } else {
            q.append(" where ");
        }
        if (inOrClause && nbCriteriaInOrClause == 0) {
            q.append("(");
        }

        q.append(sql);

        if (param != null) {
            params.put(param, value);
        }

        hasOneOrMoreCriteria = true;
        if (inOrClause) {
            nbCriteriaInOrClause++;
        }

        return this;
    }

    /**
     * @param sql SQL command
     * @param multiParams multi params
     * @return instance of QueryBuilder
     */
    public QueryBuilder addSqlCriterionMultiple(String sql, Object... multiParams) {
        if (multiParams.length == 0) {
            return this;
        }

        if (hasOneOrMoreCriteria) {
            if (inOrClause && nbCriteriaInOrClause != 0) {
                q.append(" or ");
            } else {
                q.append(" and ");
            }
        } else {
            q.append(" where ");
        }
        if (inOrClause && nbCriteriaInOrClause == 0) {
            q.append("(");
        }

        q.append(sql);

        for (int i = 0; i < multiParams.length - 1; i = i + 2) {
            params.put((String) multiParams[i], multiParams[i + 1]);
        }

        hasOneOrMoreCriteria = true;
        if (inOrClause) {
            nbCriteriaInOrClause++;
        }

        return this;
    }

    /**
     * @param field field name
     * @param value true/false
     * @return instance of QueryBuilder
     */
    public QueryBuilder addBooleanCriterion(String field, Boolean value) {
        if (StringUtils.isBlank(value)) {
            return this;
        }

        addSql(field + (value.booleanValue() ? " is true " : " is false "));
        return this;
    }

    /**
     * Add a criteria to check field value equivalence to a value passed
     * 
     * @param field Name of field for entity
     * @param operator SQL operator
     * @param value Value to compare to
     * @param caseInsensitive If true, both value and field value will be converted to a lower case for comparison
     * @return instance QueryBuilder
     */
    public QueryBuilder addCriterion(String field, String operator, Object value, boolean caseInsensitive) {
        return addCriterion(field, operator, value, caseInsensitive, false);
    }

    /**
     * Add a criteria to check field value equivalence to a value passed
     * 
     * @param field Name of field for entity
     * @param operator SQL operator
     * @param value Value to compare to
     * @param caseInsensitive If true, both value and field value will be converted to a lower case for comparison
     * @param isFieldValueOptional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @return instance QueryBuilder
     */
    public QueryBuilder addCriterion(String field, String operator, Object value, boolean caseInsensitive, boolean isFieldValueOptional) {
        if (StringUtils.isBlank(value)) {
            return this;
        }

        StringBuffer sql = new StringBuffer();
        String param = convertFieldToParam(field);
        Object nvalue = value;

        if (caseInsensitive && (value instanceof String)) {
            sql.append("lower(" + field + ")");
        } else {
            sql.append(field);
        }
        sql.append(operator + ":" + param);

        if (caseInsensitive && (value instanceof String)) {
            nvalue = ((String) value).toLowerCase();
        }

        if (isFieldValueOptional) {
            return addSqlCriterion("(" + field + " IS NULL or (" + sql.toString() + "))", param, nvalue);
        } else {
            return addSqlCriterion(sql.toString(), param, nvalue);
        }
    }

    /**
     * @param field field name
     * @param entity entity for given name.
     * @return instance of QueryBuilder
     */
    public QueryBuilder addCriterionEntityInList(String field, Object entity) {
        if (entity == null) {
            return this;
        }

        String param = convertFieldToParam(field);

        return addSqlCriterion(" :" + param + " member of " + field, field, entity);
    }

    /**
     * Add a criteria to check field value is equal to the entity passed
     * 
     * @param field field name
     * @param entity entity for given name.
     * @return instance of QueryBuilder
     */
    public QueryBuilder addCriterionEntity(String field, Object entity) {
        return addCriterionEntity(field, entity, " = ", false);
    }

    /**
     * Add a criteria to check field value is equal to the entity passed
     * 
     * @param field name of field
     * @param entity entity of given field to add criterion
     * @param condition Comparison type
     * @param isFieldValueOptional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @return instance of QueryBuilder
     */
    public QueryBuilder addCriterionEntity(String field, Object entity, String condition, boolean isFieldValueOptional) {
        if (entity == null) {
            return this;
        }

        String param = convertFieldToParam(field);

        if (isFieldValueOptional) {
            return addSqlCriterion("(" + field + " IS NULL or (" + field + condition + ":" + param + "))", param, entity);
        } else {
            return addSqlCriterion(field + condition + ":" + param, param, entity);
        }
    }

    /**
     * @param field field name
     * @param enumValue value of field
     * @return instance of QueryBuilder.
     */
    @SuppressWarnings("rawtypes")
    public QueryBuilder addCriterionEnum(String field, Enum enumValue) {
        return addCriterionEnum(field, enumValue, "=", false);
    }

    /**
     * Add a criteria to check field value is equal to the enum passed
     * 
     * @param field Field name
     * @param enumValue Enum value to compare to
     * @param condition Comparison type
     * @param isFieldValueOptional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @return instance of QueryBuilder.
     */
    @SuppressWarnings("rawtypes")
    public QueryBuilder addCriterionEnum(String field, Enum enumValue, String condition, boolean isFieldValueOptional) {
        if (enumValue == null) {
            return this;
        }

        String param = convertFieldToParam(field);

        if (isFieldValueOptional) {
            return addSqlCriterion("(" + field + " IS NULL or (" + field + " " + condition + ":" + param + "))", param, enumValue);
        } else {
            return addSqlCriterion(field + " " + condition + ":" + param, param, enumValue);
        }
    }

    /**
     * Add a criteria to check field value is in a list passed
     * 
     * @param field Field name
     * @param listValue List value to compare to
     * @param condition Comparison type
     * @return instance of QueryBuilder.
     */
    @SuppressWarnings("rawtypes")
    public QueryBuilder addCriterionInList(String field, List listValue, String condition) {
        return addCriterionInList(field, listValue, condition);
    }

    /**
     * Add a criteria to check field value is in a list passed
     * 
     * @param field Field name
     * @param listValue List value to compare to
     * @param condition Comparison type
     * @param isFieldValueOptional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @return instance of QueryBuilder.
     */
    @SuppressWarnings("rawtypes")
    public QueryBuilder addCriterionInList(String field, List listValue, String condition, boolean isFieldValueOptional) {
        if (listValue == null) {
            return this;
        }

        String param = convertFieldToParam(field);

        if (isFieldValueOptional) {
            return addSqlCriterion("(" + field + " IS NULL or (" + field + " " + condition + ":" + param + "))", param, listValue);
        } else {
            return addSqlCriterion(field + " " + condition + ":" + param, param, listValue);
        }
    }

    /**
     * Add a criteria to check field value is like a value passed
     * 
     * @param field Field name
     * @param value Value to compare to
     * @param matchingStyle : Matching style
     * @param caseInsensitive true/false.
     * @return instance QueryBuiler.
     */
    public QueryBuilder like(String field, String value, QueryLikeStyleEnum matchingStyle, boolean caseInsensitive) {
        return like(field, value, matchingStyle, caseInsensitive, false, false);
    }

    /**
     * Add a criteria to check field value is like a value passed
     * 
     * @param field Field name
     * @param value Value to compare to
     * @param matchingStyle : Matching style
     * @param caseInsensitive true/false
     * @param addNot Should NOT be added to comparison
     * @param isFieldValueOptional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @return instance QueryBuilder
     */
    public QueryBuilder like(String field, String value, QueryLikeStyleEnum matchingStyle, boolean caseInsensitive, boolean addNot, boolean isFieldValueOptional) {
        if (StringUtils.isBlank(value)) {
            return this;
        }

        String v = caseInsensitive ? value.toLowerCase() : value;

        if (matchingStyle == QueryLikeStyleEnum.MATCH_BEGINNING || matchingStyle == QueryLikeStyleEnum.MATCH_ANYWHERE) {
            v = v + "%";
        }
        if (matchingStyle == QueryLikeStyleEnum.MATCH_ANYWHERE) {
            v = "%" + v;
        }

        StringBuffer sql = new StringBuffer();

        if (caseInsensitive && (value instanceof String)) {
            sql.append("lower(" + field + ")");
        } else {
            sql.append(field);
        }
        sql.append(addNot ? " not like " : " like ");
        sql.append("'" + v + "'");

        if (isFieldValueOptional) {
            return addSqlCriterion("(" + field + " IS NULL or (" + sql.toString() + "))", null, null);
        } else {
            return addSqlCriterion(sql.toString(), null, null);
        }
    }

    /**
     * Add a criteria to check field value is like a value passed
     * 
     * @param field field name
     * @param value value.
     * @param caseInsensitive true/false
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionWildcard(String field, String value, boolean caseInsensitive) {
        return addCriterionWildcard(field, value, caseInsensitive, false, false);
    }

    /**
     * Add a criteria to check field value is like a value passed
     * 
     * @param field name of field
     * @param value value of field
     * @param caseInsensitive true/false
     * @param addNot Should NOT be added to comparison
     * @param isFieldValueOptional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @return query instance.
     */
    public QueryBuilder addCriterionWildcard(String field, String value, boolean caseInsensitive, boolean addNot, boolean isFieldValueOptional) {

        if (StringUtils.isBlank(value)) {
            return this;
        }
        boolean wildcard = (value.indexOf("*") != -1);

        if (wildcard) {
            return like(field, value.replace("*", "%"), QueryLikeStyleEnum.MATCH_EQUAL, caseInsensitive, addNot, isFieldValueOptional);
        } else {
            return addCriterion(field, addNot ? " != " : " = ", value, caseInsensitive, isFieldValueOptional);
        }
    }

    /**
     * Add a criteria to check field value is equal to the date passed considering the time
     * 
     * @param field Name of entity's field
     * @param value Date value to compare to
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionDate(String field, Date value) {
        if (StringUtils.isBlank(value)) {
            return this;
        }
        return addCriterion(field, "=", value, false, false);

    }

    /**
     * Add a criteria to check field value is equal to the date passed ignoring the time
     * 
     * @param field Name of entity's field
     * @param value Date value to compare to
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionDateTruncatedToDay(String field, Date value) {
        return addCriterionDateTruncatedToDay(field, value, false, false, null);
    }

    /**
     * Add a criteria to check field value is equal or not to the date passed ignoring the time
     * 
     * @param field Name of entity's field
     * @param value Date value to compare to
     * @param isNot Should NOT be applied
     * @param isFieldValueOptional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @param parameterNamePrefix A prefix to apply to a parameter name. Used in case of inList function whenOR clause is used to join multiple date comparisons for the same field
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionDateTruncatedToDay(String field, Date value, boolean isNot, boolean isFieldValueOptional, String parameterNamePrefix) {

        if (StringUtils.isBlank(value)) {
            return this;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(value);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date start = c.getTime();

        c.add(Calendar.DATE, 1);
        Date end = c.getTime();

        String startDateParameterName = "start" + field.replaceAll("[^a-zA-Z0-9_]", "") + (parameterNamePrefix != null ? parameterNamePrefix : "");
        String endDateParameterName = "end" + field.replaceAll("[^a-zA-Z0-9_]", "") + (parameterNamePrefix != null ? parameterNamePrefix : "");

        String sql = field + ">=:" + startDateParameterName + " and " + field + "<:" + endDateParameterName;

        if (isFieldValueOptional) {
            return addSqlCriterionMultiple((isNot ? " not(" : "") + "(" + field + " IS NULL or (" + sql + "))" + (isNot ? ")" : ""), startDateParameterName, start, endDateParameterName, end);
        } else {
            return addSqlCriterionMultiple((isNot ? " not(" : "(") + sql + ")", startDateParameterName, start, endDateParameterName, end);
        }

    }

    /**
     * Add a criteria to check that field value is equal or after the date passed
     * 
     * @param field Name of entity's field
     * @param valueFrom Date value to compare to
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionDateRangeFromTruncatedToDay(String field, Date valueFrom) {
        return addCriterionDateRangeFromTruncatedToDay(field, valueFrom, false);
    }

    /**
     * Add a criteria to check that field value is equal or after the date passed
     * 
     * @param field Name of entity's field
     * @param valueFrom Date value to compare to
     * @param isFieldValueOptional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionDateRangeFromTruncatedToDay(String field, Date valueFrom, boolean isFieldValueOptional) {
        if (StringUtils.isBlank(valueFrom)) {
            return this;
        }
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(valueFrom);
        calFrom.set(Calendar.HOUR_OF_DAY, 0);
        calFrom.set(Calendar.MINUTE, 0);
        calFrom.set(Calendar.SECOND, 0);
        calFrom.set(Calendar.MILLISECOND, 0);

        Date start = calFrom.getTime();

        String startDateParameterName = "start" + field.replace(".", "");

        if (isFieldValueOptional) {
            return addSqlCriterion("(" + field + " IS NULL or " + field + ">=:" + startDateParameterName + ")", startDateParameterName, start);
        } else {
            return addSqlCriterion(field + ">=:" + startDateParameterName, startDateParameterName, start);
        }
    }

    /**
     * Add a criteria to check that field value is before the date passed. Date value is truncated to a day.
     *
     * @param field the field
     * @param valueTo the value to. Will be truncated to day.
     * @param inclusive If True, the field will be considered as inclusive - will apply &lt= instead of &lt; comparison.
     * @param optional Is field value optional - a "(field is NULL or ...)" will be added to the criteria
     * @return the query builder
     */
    public QueryBuilder addCriterionDateRangeToTruncatedToDay(String field, Date valueTo, boolean inclusive, boolean optional) {
        if (StringUtils.isBlank(valueTo)) {
            return this;
        }
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(valueTo);
        if (inclusive) {
            calTo.add(Calendar.DATE, 1);
        }
        calTo.set(Calendar.HOUR_OF_DAY, 0);
        calTo.set(Calendar.MINUTE, 0);
        calTo.set(Calendar.SECOND, 0);
        calTo.set(Calendar.MILLISECOND, 0);

        Date end = calTo.getTime();

        String endDateParameterName = "end" + field.replace(".", "");
        if (optional) {
            return addSqlCriterion("(" + field + " IS NULL or " + field + "<:" + endDateParameterName + ")", endDateParameterName, end);
        } else {
            return addSqlCriterion(field + "<:" + endDateParameterName, endDateParameterName, end);
        }
    }

    /**
     * @param orderColumn name of column which is used for orderBy
     * @param ascending true/false
     */
    public void addOrderCriterion(String orderColumn, boolean ascending) {

        q.append(q.indexOf("ORDER BY") > 0 ? ", " : " ORDER BY ");

        if (clazz != null) {
            Field field = ReflectionUtils.getField(clazz, orderColumn.substring(orderColumn.indexOf(".") + 1));
            if (field != null && field.getType().isAssignableFrom(String.class)) {
                q.append(" LOWER(CAST(" + orderColumn + " AS string))");
            } else {
                q.append(orderColumn);
            }
        } else {
            q.append(orderColumn);
        }

        if (ascending) {
            q.append(" ASC ");
        } else {
            q.append(" DESC ");
        }
    }

    /**
     * @param orderColumn name of column which is used for orderBy
     * @param ascending true/false
     */
    public void addOrderCriterionAsIs(String orderColumn, boolean ascending) {
        q.append(" ORDER BY ").append(orderColumn).append(ascending ? " ASC " : " DESC ");
    }

    /**
     * @param groupColumn the name of groupBy column
     */
    public void addGroupCriterion(String groupColumn) {
        q.append(" GROUP BY " + groupColumn);

    }

    /**
     * Append an ORDER BY clause for multiple fields
     * 
     * @param orderRules An array of column name and order direction combinations. Order direction is expressed as a boolean with True for ascending order. E.g. "NAME,
     *        false,ID,true" will sort by NAME field descending and then by "ID" field ascending.
     * @return instance of QueryBuilder
     */
    public QueryBuilder addOrderMultiCriterion(Object... orderRules) {

        q.append(" ORDER BY ");

        for (int i = 0; i < orderRules.length; i = i + 2) {
            if (i > 0) {
                q.append(", ");
            }
            q.append(orderRules[i]);
            if (Boolean.TRUE.equals(orderRules[i + 1])) {
                q.append(" ASC ");
            } else {
                q.append(" DESC ");
            }
        }
        return this;

    }

    /**
     * Add a criteria to check value is in between the values of two fields. e.g. field1Value&lt;=value&lt;field2Value or field1Value&lt;=value&lt;=field2Value
     * 
     * @param startField starting field
     * @param endField ending field
     * @param value value to compare to. In case of date, value is truncated to the start of the date
     * @param inclusive If True, end range field will be considered as inclusive
     * @param optional If true, consider that either one of the field values can be null
     * @return instance of Query builder.
     */
    public QueryBuilder addValueInBetweenTwoFields(String startField, String endField, Object value, boolean inclusive, boolean optional) {
        if (StringUtils.isBlank(value)) {
            return this;
        }

        if (value instanceof Double) {
            value = BigDecimal.valueOf((Double) value);

        } else if (value instanceof Date) {
            Calendar c = Calendar.getInstance();
            c.setTime((Date) value);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int date = c.get(Calendar.DATE);
            c.set(year, month, date, 0, 0, 0);
            value = c.getTime();
        }

        if (optional) {
            String paramName = convertFieldToParam(startField);

            String sql = "((" + startField + " IS NULL and " + endField + " IS NULL) or (" + startField + "<=:" + paramName + " and :" + paramName + (inclusive ? "<=" : "<") + endField + ") or (" + startField + "<=:"
                    + paramName + " and " + endField + " IS NULL) or (" + startField + " IS NULL and :" + paramName + (inclusive ? "<=" : "<") + endField + "))";
            addSqlCriterionMultiple(sql, paramName, value);

        } else {
            addCriterion(startField, "<=", value, false);
            addCriterion(endField, inclusive ? " >= " : " > ", value, false);
        }
        return this;
    }

    /**
     * Add a criteria to check value is greater than a field value. e.g. fieldValue&lt;=value
     * 
     * @param field field
     * @param value value to compare to. In case of date, value is truncated to the start of the date
     * @param optional If true, consider that the field values can be null
     * @return instance of Query builder.
     */
    public QueryBuilder addValueIsGreaterThanField(String field, Object value, boolean optional) {

        if (value instanceof Double) {
            addCriterion(field, " >= ", BigDecimal.valueOf((Double) value), false, optional);
        } else if (value instanceof Number) {
            addCriterion(field, " >= ", value, false, optional);
        } else if (value instanceof Date) {
            addCriterionDateRangeFromTruncatedToDay(field, (Date) value, optional);
        }
        return this;
    }

    /**
     * Add a criteria to check value is less than a field value. e.g. value&lt;fieldValue or value&lt;=fieldValue
     * 
     * @param field field
     * @param value value to compare to. In case of date, value is truncated to the start of the date
     * @param inclusive If True, end range field will be considered as inclusive
     * @param isFieldValueOptional If true, consider that the field values can be null
     * @return instance of Query builder.
     */
    public QueryBuilder addValueIsLessThanField(String field, Object value, boolean inclusive, boolean isFieldValueOptional) {

        if (value instanceof Double) {
            addCriterion(field, inclusive ? " <= " : " < ", BigDecimal.valueOf((Double) value), true, isFieldValueOptional);
        } else if (value instanceof Number) {
            addCriterion(field, inclusive ? " <= " : " < ", value, true, isFieldValueOptional);
        } else if (value instanceof Date) {
            addCriterionDateRangeToTruncatedToDay(field, (Date) value, inclusive, isFieldValueOptional);
        }
        return this;
    }

    public void addListFilters(String tableNameAlias, String fieldName, Object value){
        String paramName = convertFieldToParam(fieldName);
        addSqlCriterion(":" + paramName + " in elements(" + tableNameAlias + '.' + fieldName + ")", paramName, value);
    }

    /**
     * Add a criteria to check that values overlap the values of two fields.
     * 
     * @param startField starting field
     * @param endField ending field
     * @param fromValue range of values to compare to - from value. In case of date, value is truncated to the start of the date
     * @param toValue range of values to compare to - to value. In case of date, value is truncated to the start of the date
     * @param inclusive If True, end range field will be considered as inclusive
     * @return instance of Query builder.
     */
    public QueryBuilder addValueRangeOverlapTwoFieldRange(String startField, String endField, Object fromValue, Object toValue, boolean inclusive) {

        String paramNameFrom = convertFieldToParam(startField);
        String paramNameTo = convertFieldToParam(endField);

        // older query before adding inclusive check
        // String sql = "(( " + startField + " IS NULL and " + endField + " IS NULL) or ( " + startField + " IS NULL and " + endField + ">:" + paramNameFrom + ") or (" + endField +
        // " IS NULL and " + startField + "<:"
        // + paramNameTo + ") or (" + startField + " IS NOT NULL and " + endField + " IS NOT NULL and ((" + startField + "<=:" + paramNameFrom + " and :" + paramNameFrom + "<" +
        // endField + ") or (:" + paramNameFrom
        // + "<=" + startField + " and " + startField + "<:" + paramNameTo + "))))";

        String sql = "(( " + startField + " IS NULL and " + endField + " IS NULL) or  ( " + startField + " IS NULL and :" + paramNameFrom + (inclusive ? "<=" : "<") + endField + ") or (" + endField + " IS NULL and "
                + startField + (inclusive ? "<=:" : "<:") + paramNameTo + ") or (" + startField + " IS NOT NULL and " + endField + " IS NOT NULL and ((" + startField + "<=:" + paramNameFrom + " and :" + paramNameFrom
                + (inclusive ? "<=" : "<") + endField + ") or (:" + paramNameFrom + "<=" + startField + " and " + startField + (inclusive ? "<=:" : "<:") + paramNameTo + "))))";

        addSqlCriterionMultiple(sql, paramNameFrom, fromValue, paramNameTo, toValue);

        return this;
    }

    /**
     * Add a criteria to check field value is/not in the list of values e.g. fieldValue inList(value)
     * 
     * @param field field
     * @param value value to compare to. In case of date, field value and value are compared ignoring the time.
     * @param isNot Should NOT be applied
     * @param isFieldValueOptional If true, consider that the field values can be null
     * @return instance of Query builder.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public QueryBuilder addFieldInAListOfValues(String field, Object value, boolean isNot, boolean isFieldValueOptional) {

        String paramName = convertFieldToParam(field);

        if (value instanceof String) {
            addSql("lower(" + field + ")" + (isNot ? " NOT " : "") + " IN (" + value + ")");

        } else if (value instanceof Collection) {

            // Convert to lowercase and do case insensitive search for String based search
            Object firstValue = ((Collection) value).iterator().next();
            if (firstValue instanceof String) {
                value = ((Collection<String>) value).stream().map(val -> val != null ? val.toLowerCase() : val).collect(Collectors.toList());
                addSqlCriterion("lower(" + field + ")" + (isNot ? " NOT " : "") + " IN (:" + paramName + ")", paramName, value);

                // Date must treat each value as a from/to value truncated to day start and end respectively
            } else if (firstValue instanceof Date) {

                if (!isNot) {
                    startOrClause();
                }

                int i = 0;
                for (Date val : (Collection<Date>) value) {
                    addCriterionDateTruncatedToDay(field, val, isNot, isFieldValueOptional, "" + i++);
                }

                if (!isNot) {
                    endOrClause();
                }

            } else {
                addSqlCriterion(field + (isNot ? " NOT " : "") + " IN (:" + paramName + ")", paramName, value);
            }
        }
        return this;
    }

    /**
     * Add a criteria to check field value is/not equal to a value e.g. fieldValue=value
     * 
     * @param field field
     * @param value value to compare to. In case of date, field value and value are compared ignoring the time.
     * @param isNot Should NOT be applied
     * @param isFieldValueOptional If true, consider that the field values can be null
     * @return instance of Query builder.
     */
    @SuppressWarnings({ "rawtypes" })
    public QueryBuilder addValueIsEqualToField(String field, Object value, boolean isNot, boolean isFieldValueOptional) {

        // Search by equals/not equals to a string value
        if (value instanceof String) {

            // if contains dot, that means join is needed
            String filterString = (String) value;

            addCriterionWildcard(field, filterString, true, isNot, isFieldValueOptional);

            // Search by equals to truncated date value
        } else if (value instanceof Date) {
            addCriterionDateTruncatedToDay(field, (Date) value, isNot, isFieldValueOptional, null);

            // Search by equals/not equals to a number value
        } else if (value instanceof Number) {
            addCriterion(field, isNot ? " != " : " = ", value, true, isFieldValueOptional);

            // Search by equals/not equals to a boolean value
        } else if (value instanceof Boolean) {
            boolean bValue = (boolean) value;
            addBooleanCriterion(field, isNot ? !bValue : bValue);

            // Search by equals/not equals to an enum value
        } else if (value instanceof Enum) {
            if (value instanceof IdentifiableEnum) {
                String enumIdKey = field + "Id";
                addCriterion(enumIdKey, isNot ? " != " : " = ", ((IdentifiableEnum) value).getId(), true, isFieldValueOptional);
            } else {
                addCriterionEnum(field, (Enum) value, isNot ? " != " : " = ", isFieldValueOptional);
            }

        } else if (value instanceof List) {
            addCriterionInList(field, (List) value, isNot ? " not in " : " in ", isFieldValueOptional);
        }
        return this;
    }

    /**
     * @return instance QueryBuilder.
     */
    public QueryBuilder startOrClause() {
        inOrClause = true;
        nbCriteriaInOrClause = 0;
        return this;
    }

    /**
     * @return instance of QueryBuilder.
     */
    public QueryBuilder endOrClause() {
        if (nbCriteriaInOrClause != 0) {
            q.append(")");
        }

        inOrClause = false;
        nbCriteriaInOrClause = 0;
        return this;
    }

    /**
     * Get a JPA query object
     * 
     * @param em entity manager
     * @return instance of Query.
     */
    public Query getQuery(EntityManager em) {
        applyOrdering(paginationSortAlias);

        Query result = em.createQuery(q.toString());
        applyPagination(result);

        for (Map.Entry<String, Object> e : params.entrySet()) {
            result.setParameter(e.getKey(), e.getValue());
        }
        return result;
    }

    /**
     * Get Hibernate native query object
     * 
     * @param em entity manager
     * @param convertToMap If False, query will return a list of Object[] values. If True, query will return a list of map of values.
     * @return instance of Query.
     */
    public SQLQuery getNativeQuery(EntityManager em, boolean convertToMap) {
        applyOrdering(paginationSortAlias);

        Session session = em.unwrap(Session.class);
        SQLQuery result = session.createSQLQuery(q.toString());
        applyPagination(result);

        if (convertToMap) {
            result.setResultTransformer(AliasToEntityOrderedMapResultTransformer.INSTANCE);
        }
        for (Map.Entry<String, Object> e : params.entrySet()) {
            Object value = e.getValue();
            if (value.getClass().isArray()) {
                result.setParameterList(e.getKey(), (Object[]) value);
            } else if (value instanceof Collection) {
                result.setParameterList(e.getKey(), (Collection) value);
            } else {
                result.setParameter(e.getKey(), value);
            }
        }

        return result;
    }

    /**
     * Return a query to retrive ids.
     * 
     * @param em entity Manager
     * @return typed query instance
     */
    public TypedQuery<Long> getIdQuery(EntityManager em) {
        applyOrdering(paginationSortAlias);
        StringBuilder s = new StringBuilder("select ").append(alias != null ? alias + "." : "").append("id ").append(q.toString().substring(q.indexOf(FROM)));

        TypedQuery<Long> result = em.createQuery(s.toString(), Long.class);
        applyPagination(result);

        for (Map.Entry<String, Object> e : params.entrySet()) {
            result.setParameter(e.getKey(), e.getValue());
        }
        return result;
    }
    
	public String addCurrentSchema(String query) {
		CurrentUserProvider currentUserProvider = (CurrentUserProvider) EjbUtils.getServiceInterface("CurrentUserProvider");
		String currentproviderCode = currentUserProvider.getCurrentUserProviderCode();
		if (currentproviderCode != null) {
			EntityManagerProvider entityManagerProvider = (EntityManagerProvider) EjbUtils.getServiceInterface("EntityManagerProvider");
			String schema = entityManagerProvider.convertToSchemaName(currentproviderCode) + ".";
			if (!query.startsWith(FROM + schema)) {
				return query.replace(FROM, FROM+schema);
			}
		}
		return query;
	}
	
    /**
     * Convert to a query to count number of entities matched: "select .. from" is changed to "select count(*) from"
     * 
     * @param em entity Manager
     * @return instance of Query.
     */
    public Query getCountQuery(EntityManager em) {
    	String countSql = "select count(*) " + q.toString().substring(q.indexOf(FROM));

        // Uncomment if plan to use addCollectionMember()
        // String sql = q.toString().toLowerCase();
        // if (sql.contains(" distinct")) {
        //
        // String regex = "from[ \\t]+[\\w\\.]+[ \\t]+(\\w+)";
        // Pattern pattern = Pattern.compile(regex);
        // Matcher matcher = pattern.matcher(sql);
        // if (!matcher.find()) {
        // throw new RuntimeException("Can not determine alias name");
        // }
        // String aliasName = matcher.group(1);
        //
        // countSql = "select count(distinct " + aliasName + ") " + q.toString().substring(q.indexOf(from));
        // }

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.trace("Count query is {}", countSql);

        Query result = em.createQuery(countSql);
        
        for (Map.Entry<String, Object> e : params.entrySet()) {
            result.setParameter(e.getKey(), e.getValue());
        }
        return result;
    }

    /**
     * Convert to a query to count number of records matched: "select .. from" is changed to "select count(*) from". To be used with NATIVE query in conjunction with
     * getNativeQuery()
     * 
     * @param em entity Manager
     * @return instance of Query.
     */
    public Query getNativeCountQuery(EntityManager em) {

        String countSql = "select count(*) " + addCurrentSchema(q.toString().substring(q.indexOf(FROM)));
        // Logger log = LoggerFactory.getLogger(getClass());
        // log.trace("Count query is {}", countSql);

        Query result = em.createNativeQuery(countSql);
        for (Map.Entry<String, Object> e : params.entrySet()) {
            result.setParameter(e.getKey(), e.getValue());
        }
        return result;
    }

    /**
     * Count a number of entities matching search criteria
     * 
     * @param em entity Manager
     * @return number of query.
     */
    public Long count(EntityManager em) {
        Query query = getCountQuery(em);
        return (Long) query.getSingleResult();
    }

    /**
     * Perform a search and return a list of entities matching search criteria
     * 
     * @param em entity manager
     * @return list of result
     */
    @SuppressWarnings("rawtypes")
    public List find(EntityManager em) {
        Query query = getQuery(em);
        return query.getResultList();
    }

    /**
     * @param fieldname field name
     * @return convert para.
     */
    public String convertFieldToParam(String fieldname) {
        fieldname = fieldname.replace(".", "_").replace("(", "_").replace(")", "_").replace(",", "_").replace(" ", "");
        StringBuilder newField = new StringBuilder(fieldname);
        while (params.containsKey(newField.toString())) {
            newField = new StringBuilder(fieldname).append("_" + String.valueOf(new Random().nextInt(100)));
        }
        return newField.toString();
    }

    /**
     * Convert fieldname to a collection member item name.
     * 
     * @param fieldname Fieldname
     * @return Fieldname converted to parameter name with suffix "Item". e.g. for "sellers" it will return sellersItem
     */
    public String convertFieldToCollectionMemberItem(String fieldname) {
        return convertFieldToParam(fieldname) + "Item";
    }

    /**
     * Apply ordering to the query
     * 
     * @param alias alias of column?
     */
    protected void applyOrdering(String alias) {
        if (paginationConfiguration == null) {
            return;
        }

        if (paginationConfiguration.isSorted() && q.indexOf("ORDER BY") == -1) {
            Object[] orderings = paginationConfiguration.getOrderings();
            for (int i = 0; i < orderings.length; i = i + 2) {
                addOrderCriterion(((alias != null) ? (alias + ".") : "") + orderings[i], orderings[i + 1] == SortOrder.ASCENDING);
            }
        }
    }

    /**
     * Apply pagination criteria to a JPA query
     * 
     * @param query JPA query to apply pagination to
     */
    protected void applyPagination(Query query) {
        if (paginationConfiguration == null) {
            return;
        }

        applyPagination(query, paginationConfiguration.getFirstRow(), paginationConfiguration.getNumberOfRows());
    }

    /**
     * Apply pagination criteria to a JPA query
     * 
     * @param query JPA query to apply pagination to
     * @param firstRow the index of first row
     * @param numberOfRows number of rows shoud return.
     */
    public void applyPagination(Query query, Integer firstRow, Integer numberOfRows) {
        if (firstRow != null) {
            query.setFirstResult(firstRow);
        }
        if (numberOfRows != null) {
            query.setMaxResults(numberOfRows);
        }
    }

    /**
     * Apply pagination criteria to a Hibernate query
     * 
     * @param query Hibernate query to apply pagination to
     */
    protected void applyPagination(SQLQuery query) {
        if (paginationConfiguration == null) {
            return;
        }
        applyPagination(query, paginationConfiguration.getFirstRow(), paginationConfiguration.getNumberOfRows());
    }

    /**
     * Apply pagination criteria to a Hibernate query
     * 
     * @param query Hibernate query to apply pagination to
     * @param firstRow the index of first row
     * @param numberOfRows number of rows shoud return.
     */
    public void applyPagination(SQLQuery query, Integer firstRow, Integer numberOfRows) {
        if (firstRow != null) {
            query.setFirstResult(firstRow);
        }
        if (numberOfRows != null) {
            query.setMaxResults(numberOfRows);
        }
    }

    public String getSqlString() {
        return q.toString();
    }

    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public QueryBuilder(Class<?> clazz, String alias) {
        this("from " + clazz.getName() + " " + alias, alias);
        this.clazz = clazz;
    }

    public String toString() {
        String result = q.toString();
        for (Map.Entry<String, Object> e : params.entrySet()) {
            result = result + " Param name:" + e.getKey() + " value:" + e.getValue().toString();
        }
        return result;
    }

    // Was causing issues with distinct clause. Switched to EXISTS clause instead when using inList criteria for list type field
    // /**
    // * Add a collection member join e.g " IN (a.sellers) s " right after a from clause
    // *
    // * @param fieldName
    // */
    // public void addCollectionMember(String fieldName) {
    //
    // String sql = q.toString().toLowerCase();
    //
    // String regex = "(from[ \\t]+[\\w\\.]+[ \\t]+(\\w+))";
    // Pattern pattern = Pattern.compile(regex);
    // Matcher matcher = pattern.matcher(sql);
    // if (!matcher.find()) {
    // throw new RuntimeException("Can not determine where to add collection member clause");
    // }
    // String fromClause = matcher.group(1);
    // String aliasName = matcher.group(2);
    //
    // q.insert(sql.indexOf(fromClause) + fromClause.length(),
    // ", IN (" + (aliasName != null ? aliasName + "." : "") + fieldName + ") as " + convertFieldToCollectionMemberItem(fieldName));
    //
    // // Append select clause to select only a main entity
    // if (!sql.startsWith("select") && aliasName != null) {
    // q.insert(0, "select distinct " + aliasName + " ");
    // }
    // }
}
