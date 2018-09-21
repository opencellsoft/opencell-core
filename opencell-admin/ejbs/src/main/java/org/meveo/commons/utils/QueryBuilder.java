/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.commons.utils;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.util.pagination.PaginationConfiguration;

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
 * @author akadid abdelmounaim
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
public class QueryBuilder {

    protected StringBuffer q;

    protected String alias;

    private Map<String, Object> params;

    private boolean hasOneOrMoreCriteria;

    private boolean inOrClause;

    private int nbCriteriaInOrClause;

    protected PaginationConfiguration paginationConfiguration;

    private String paginationSortAlias;

    private Class<?> clazz;

    public enum QueryLikeStyleEnum {
        MATCH_EQUAL, MATCH_BEGINNING, MATCH_ANYWHERE
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
     * @param field name of field for entity
     * @param operator SQL operator
     * @param value value for field
     * @param caseInsensitive true/false.
     * @return instance QueryBuilder
     */
    public QueryBuilder addCriterion(String field, String operator, Object value, boolean caseInsensitive) {
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

        return addSqlCriterion(sql.toString(), param, nvalue);
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
     * @param field field name
     * @param entity entity for given name.
     * @return instance of QueryBuilder
     */
    public QueryBuilder addCriterionEntity(String field, Object entity) {
        return addCriterionEntity(field, entity, " = ");
    }

    /**
     * @param field name of field
     * @param entity entity of given field to add criterion
     * @param condition Comparison type
     * @return instance of QueryBuilder
     */
    public QueryBuilder addCriterionEntity(String field, Object entity, String condition) {
        if (entity == null) {
            return this;
        }

        String param = convertFieldToParam(field);

        return addSqlCriterion(field + condition + ":" + param, param, entity);
    }

    /**
     * @param field field name
     * @param enumValue value of field
     * @return instance of QueryBuilder.
     */
    @SuppressWarnings("rawtypes")
    public QueryBuilder addCriterionEnum(String field, Enum enumValue) {
        return addCriterionEnum(field, enumValue, "=");
    }

    /**
     * @param field field name
     * @param enumValue value.
     * @param condition Comparison type
     * @return instance of QueryBuilder.
     */
    @SuppressWarnings("rawtypes")
    public QueryBuilder addCriterionEnum(String field, Enum enumValue, String condition) {
        if (enumValue == null) {
            return this;
        }

        String param = convertFieldToParam(field);

        return addSqlCriterion(field + " " + condition + ":" + param, param, enumValue);
    }

    /**
     * Ajouter un critere like.
     * 
     * @param field field name
     * @param value value of field
     * @param style : 0=aucun travail sur la valeur rechercher, 1=Recherche sur dbut du mot, 2=Recherche partout dans le mot
     * @param caseInsensitive true/false.
     * @return instance QueryBuiler.
     */
    public QueryBuilder like(String field, String value, QueryLikeStyleEnum style, boolean caseInsensitive) {
        return like(field, value, style, caseInsensitive, false);
    }

    /**
     * Ajouter un critere like.
     * 
     * @param field field name
     * @param value value
     * @param style : 0=aucun travail sur la valeur rechercher, 1=Recherche sur dbut du mot, 2=Recherche partout dans le mot
     * @param caseInsensitive true/false
     * @param addNot Should NOT be added to comparison
     * @return instance QueryBuilder
     */
    public QueryBuilder like(String field, String value, QueryLikeStyleEnum style, boolean caseInsensitive, boolean addNot) {
        if (StringUtils.isBlank(value)) {
            return this;
        }

        String v = value;

        if (style == QueryLikeStyleEnum.MATCH_BEGINNING || style == QueryLikeStyleEnum.MATCH_ANYWHERE) {
            v = v + "%";
        }
        if (style == QueryLikeStyleEnum.MATCH_ANYWHERE) {
            v = "%" + v;
        }

        return addCriterion(field, addNot ? "not like " : " like ", v, caseInsensitive);
    }

    /**
     * @param field field name
     * @param value value.
     * @param caseInsensitive true/false
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionWildcard(String field, String value, boolean caseInsensitive) {
        return addCriterionWildcard(field, value, caseInsensitive, false);
    }

    /**
     * @param field name of field
     * @param value value of field
     * @param caseInsensitive true/false
     * @param addNot Should NOT be added to comparison
     * @return query instance.
     */
    public QueryBuilder addCriterionWildcard(String field, String value, boolean caseInsensitive, boolean addNot) {

        if (StringUtils.isBlank(value)) {
            return this;
        }
        boolean wildcard = (value.indexOf("*") != -1);

        if (wildcard) {
            return like(field, value.replace("*", "%"), QueryLikeStyleEnum.MATCH_EQUAL, caseInsensitive, addNot);
        } else {
            return addCriterion(field, addNot ? " != " : " = ", value, caseInsensitive);
        }
    }

    /**
     * add the date field searching support.
     * 
     * @param field entity's field
     * @param value value of date.
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionDate(String field, Date value) {
        if (StringUtils.isBlank(value)) {
            return this;
        }
        return addCriterion(field, "=", value, false);

    }

    /**
     * @param field name of entity's field
     * @param value date value
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionDateTruncatedToDay(String field, Date value) {
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

        String startDateParameterName = "start" + field.replace(".", "");
        String endDateParameterName = "end" + field.replace(".", "");
        return addSqlCriterion(field + ">=:" + startDateParameterName, startDateParameterName, start).addSqlCriterion(field + "<:" + endDateParameterName, endDateParameterName,
            end);
    }

    /**
     * @param field name of column
     * @param valueFrom date value
     * @return instance of QueryBuilder.
     */
    public QueryBuilder addCriterionDateRangeFromTruncatedToDay(String field, Date valueFrom) {
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
        return addSqlCriterion(field + ">=:" + startDateParameterName, startDateParameterName, start);
    }

    /**
     * @param field name of field to add
     * @param valueTo date value.
     * @return instance of QueryBuilder
     */
    public QueryBuilder addCriterionDateRangeToTruncatedToDay(String field, Date valueTo) {
        return addCriterionDateRangeToTruncatedToDay(field, valueTo, true);
    }

    /**
     * Adds the criterion date range to truncated to day.
     *
     * @param field the field
     * @param valueTo the value to
     * @param includeEndDate the include end date : if true then the entities having the valueTo date are included
     * @return the query builder
     */
    public QueryBuilder addCriterionDateRangeToTruncatedToDay(String field, Date valueTo, boolean includeEndDate) {
        if (StringUtils.isBlank(valueTo)) {
            return this;
        }
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(valueTo);
        if (includeEndDate) {
            calTo.add(Calendar.DATE, 1);
        }
        calTo.set(Calendar.HOUR_OF_DAY, 0);
        calTo.set(Calendar.MINUTE, 0);
        calTo.set(Calendar.SECOND, 0);
        calTo.set(Calendar.MILLISECOND, 0);

        Date end = calTo.getTime();

        String endDateParameterName = "end" + field.replace(".", "");
        return addSqlCriterion(field + "<:" + endDateParameterName, endDateParameterName, end);
    }

    /**
     * v5.0: Fix for date format problem
     * 
     * @param startField starting field
     * @param endField ending field
     * @param value date value
     * @return instance of Query builder.
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public QueryBuilder addCriterionDateInRange(String startField, String endField, Date value) {
        if (StringUtils.isBlank(value))
            return this;
        Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);
        cal.set(year, month, date, 0, 0, 0);
        Date start = cal.getTime();
        cal.set(year, month, date, 23, 59, 59);
        Date end = cal.getTime();

        String startDateParameterName = "start" + startField.replace(".", "");
        String endDateParameterName = "end" + endField.replace(".", "");

        return addSqlCriterion("(" + startField + ">=:" + startDateParameterName + " OR " + startField + " IS NULL )", startDateParameterName, start)
            .addSqlCriterion("(" + endField + "<=:" + endDateParameterName + " OR " + endField + " IS NULL )", endDateParameterName, end);
    }

    /**
     * @param orderColumn name of column which is used for orderBy
     * @param ascending true/false
     */
    public void addOrderCriterion(String orderColumn, boolean ascending) {
        if (clazz != null) {
            Field field = ReflectionUtils.getField(clazz, orderColumn.substring(orderColumn.indexOf(".") + 1));
            if (field != null && field.getType().isAssignableFrom(String.class)) {
                q.append(" ORDER BY UPPER(CAST(" + orderColumn + " AS string))");
            } else {
                q.append(" ORDER BY " + orderColumn);
            }
        } else {
            q.append(" ORDER BY " + orderColumn);
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
     * @param em entity manager
     * @return instance of Query.
     */
    public Query getQuery(EntityManager em) {
        applyPagination(paginationSortAlias);

        Query result = em.createQuery(q.toString());
        applyPagination(result);

        for (Map.Entry<String, Object> e : params.entrySet()) {
            result.setParameter(e.getKey(), e.getValue());
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
        applyPagination(paginationSortAlias);

        String from = "from ";
        String s = "select " + (alias != null ? alias + "." : "") + "id " + q.toString().substring(q.indexOf(from));

        TypedQuery<Long> result = em.createQuery(s, Long.class);
        applyPagination(result);

        for (Map.Entry<String, Object> e : params.entrySet()) {
            result.setParameter(e.getKey(), e.getValue());
        }
        return result;
    }

    /**
     * @param em entity Manager
     * @return instance of Query.
     */
    public Query getCountQuery(EntityManager em) {
        String from = "from ";

        String countSql = "select count(*) " + q.toString().substring(q.indexOf(from));

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
     * @param em entity Manager
     * @return number of query.
     */
    public Long count(EntityManager em) {
        Query query = getCountQuery(em);
        return (Long) query.getSingleResult();
    }

    /**
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
        fieldname = fieldname.replace(".", "_").replace("(", "_").replace(")", "_");
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
     * @param alias alias of column?
     */
    private void applyPagination(String alias) {
        if (paginationConfiguration == null) {
            return;
        }

        if (paginationConfiguration.isSorted() && q.indexOf("ORDER BY") == -1) {
            addOrderCriterion(((alias != null) ? (alias + ".") : "") + paginationConfiguration.getSortField(), paginationConfiguration.isAscendingSorting());
        }
    }

    /**
     * @param query query using for pagination.
     */
    private void applyPagination(Query query) {
        if (paginationConfiguration == null) {
            return;
        }

        applyPagination(query, paginationConfiguration.getFirstRow(), paginationConfiguration.getNumberOfRows());
    }

    /**
     * @param query query instance
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

    /* DEBUG */
    public void debug() {
        System.out.println("Requete : " + q.toString());
        for (Map.Entry<String, Object> e : params.entrySet()) {
            System.out.println("Param name:" + e.getKey() + " value:" + e.getValue().toString());
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
