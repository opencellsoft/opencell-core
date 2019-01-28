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
package org.meveo.service.base;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.IdentifiableEnum;

/**
 * Generic implementation that provides the default implementation for persistence methods working directly with native DB tables
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 7.0
 * 
 */
public class NativePersistenceService extends BaseService {

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    public Map<String, Object> findById(String tableName, Long id) {

        return null;
    }

    /**
     * Insert a new record into a table
     * 
     * @param tableName Table name to update
     * @param value Values
     * @throws BusinessException General exception
     */
    public void create(String tableName, Map<String, Object> value) throws BusinessException {

        StringBuffer sql = new StringBuffer();
        try {
            sql.append("insert into ").append(tableName);
            StringBuffer fields = new StringBuffer();
            StringBuffer fieldValues = new StringBuffer();

            boolean first = true;
            for (String fieldName : value.keySet()) {
                if (fieldName.equals("id")) {
                    continue;
                }

                if (!first) {
                    fields.append(",");
                    fieldValues.append(",");
                }
                fields.append(fieldName);
                if (value.get(fieldName) == null) {
                    fieldValues.append("NULL");
                } else {
                    fieldValues.append(":").append(fieldName);
                }
                first = false;
            }

            sql.append(" (").append(fields).append(") values (").append(fieldValues).append(")");

            Query query = getEntityManager().createNativeQuery(sql.toString());
            for (String fieldName : value.keySet()) {
                if (fieldName.equals("id") || value.get(fieldName) == null) {
                    continue;
                }
                query.setParameter(fieldName, value.get(fieldName));
            }
            query.executeUpdate();

        } catch (Exception e) {
            log.error("Failed to insert values into table {} {} sql {}", tableName, value, sql, e);
            throw e;
        }
    }

    /**
     * Update a record in a table
     * 
     * @param tableName Table name to update
     * @param value Values
     * @throws BusinessException General exception
     */
    public void update(String tableName, Map<String, Object> value) throws BusinessException {

        StringBuffer sql = new StringBuffer();
        try {
            sql.append("update ").append(tableName).append(" set ");
            boolean first = true;
            for (String fieldName : value.keySet()) {
                if (fieldName.equals("id")) {
                    continue;
                }

                if (!first) {
                    sql.append(",");
                }
                if (value.get(fieldName) == null) {
                    sql.append(fieldName).append("=NULL");
                } else {
                    sql.append(fieldName).append("=:").append(fieldName);
                }
                first = false;
            }

            sql.append(" where id=:id");

            Query query = getEntityManager().createNativeQuery(sql.toString());
            for (String fieldName : value.keySet()) {
                if (value.get(fieldName) != null) {
                    query.setParameter(fieldName, value.get(fieldName));
                }
            }
            query.executeUpdate();

        } catch (Exception e) {
            log.error("Failed to insert values into table {} {} sql {}", tableName, value, sql, e);
            throw e;
        }
    }

    /**
     * Update a record in a table
     * 
     * @param tableName Table name to update
     * @param id Record identifier
     * @param fieldName Field to update
     * @param value New value
     * @throws BusinessException General exception
     */
    public void updateValue(String tableName, Long id, String fieldName, Object value) throws BusinessException {

        try {
            if (value == null) {
                getEntityManager().createNativeQuery("update " + tableName + " set " + fieldName + "= null where id=" + id).executeUpdate();
            } else {
                getEntityManager().createNativeQuery("update " + tableName + " set " + fieldName + "= :" + fieldName + " where id=" + id).setParameter(fieldName, value)
                    .executeUpdate();
            }

        } catch (Exception e) {
            log.error("Failed to update value in table {}/{}/{}", tableName, fieldName, id);
            throw e;
        }
    }

    public void disable(String tableName, Long id) throws BusinessException {

        getEntityManager().createNativeQuery("update " + tableName + " set disabled=1 where id=" + id).executeUpdate();
    }

    public void enable(String tableName, Long id) throws BusinessException {

        getEntityManager().createNativeQuery("update " + tableName + " set disabled=0 where id=" + id).executeUpdate();
    }

    public void remove(String tableName, Long id) throws BusinessException {

        getEntityManager().createNativeQuery("delete from" + tableName + " where id=" + id).executeUpdate();
    }

    public void remove(String tableName, Set<Long> ids) throws BusinessException {
        getEntityManager().createNativeQuery("delete from" + tableName + " where id in:ids").setParameter("ids", ids).executeUpdate();

    }

    public List<Map<String, Object>> list(String tableName) {

        return list(tableName, null);
    }

    public List<Map<String, Object>> listActive(String tableName) {

        Map<String, Object> filters = new HashMap<>();
        filters.put("disabled", 0);
        return list(tableName, new PaginationConfiguration(filters));
    }

    /**
     * Creates NATIVE query to filter entities according data provided in pagination configuration.
     * 
     * Search filters (key = Filter key, value = search pattern or value).
     * 
     * Filter key can be:
     * <ul>
     * <li>SQL. Additional sql to apply. Value is either a sql query or an array consisting of sql query and one or more parameters to apply</li>
     * <li>&lt;condition&gt; &lt;fieldname1&gt; &lt;fieldname2&gt; ... &lt;fieldnameN&gt;. Value is a value to apply in condition</li>
     * </ul>
     * 
     * A union between different filter items is AND.
     * 
     * 
     * Condition is optional. Number of fieldnames depend on condition used. If no condition is specified an "equals ignoring case" operation is considered.
     * 
     * 
     * Following conditions are supported:
     * <ul>
     * <li>fromRange. Ranged search - field value in between from - to values. Specifies "from" part value: e.g value&lt;=fiel.value. Applies to date and number type fields.</li>
     * <li>toRange. Ranged search - field value in between from - to values. Specifies "to" part value: e.g field.value&lt;=value</li>
     * <li>list. Value is in field's list value. Applies to date and number type fields.</li>
     * <li>inList/not-inList. Field value is [not] in value (list). A comma separated string will be parsed into a list if values. A single value will be considered as a list value
     * of one item</li>
     * <li>minmaxRange. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields.</li>
     * <li>minmaxOptionalRange. Similar to minmaxRange. The value is in between two field values with either them being optional. TWO fieldnames must be specified.</li>
     * <li>overlapOptionalRange. The value range is overlapping two field values with either them being optional. TWO fieldnames must be specified. Value must be an array of two
     * values.</li>
     * <li>likeCriterias. Multiple fieldnames can be specified. Any of the multiple field values match the value (OR criteria). In case value contains *, a like criteria match will
     * be used. In either case case insensative matching is used. Applies to String type fields.</li>
     * <li>wildcardOr. Similar to likeCriterias. A wildcard match will always used. A * will be appended to start and end of the value automatically if not present. Applies to
     * <li>wildcardOrIgnoreCase. Similar to wildcardOr but ignoring case String type fields.</li>
     * <li>ne. Not equal.
     * </ul>
     * 
     * Following special meaning values are supported:
     * <ul>
     * <li>IS_NULL. Field value is null</li>
     * <li>IS_NOT_NULL. Field value is not null</li>
     * </ul>
     * 
     * 
     * 
     * To filter by a related entity's field you can either filter by related entity's field or by related entity itself specifying code as value. These two example will do the
     * same in case when quering a customer account: customer.code=aaa OR customer=aaa
     * 
     * To filter a list of related entities by a list of entity codes use "inList" on related entity field. e.g. for quering offer template by sellers: inList sellers=code1,code2
     * 
     * 
     * <b>Note:</b> Quering by related entity field directly will result in exception when entity with a specified code does not exists
     * 
     * 
     * Examples:
     * <ul>
     * <li>invoice number equals "1578AU": Filter key: invoiceNumber. Filter value: 1578AU</li>
     * <li>invoice number is not "1578AU": Filter key: ne invoiceNumber. Filter value: 1578AU</li>
     * <li>invoice number is null: Filter key: invoiceNumber. Filter value: IS_NULL</li>
     * <li>invoice number is not empty: Filter key: invoiceNumber. Filter value: IS_NOT_NULL</li>
     * <li>Invoice date is between 2017-05-01 and 2017-06-01: Filter key: fromRange invoiceDate. Filter value: 2017-05-01 Filter key: toRange invoiceDate. Filter value:
     * 2017-06-01</li>
     * <li>Date is between creation and update dates: Filter key: minmaxRange audit.created audit.updated. Filter value: 2017-05-25</li>
     * <li>invoice number is any of 158AU, 159KU or 189LL: Filter key: inList invoiceNumber. Filter value: 158AU,159KU,189LL</li>
     * <li>any of param1, param2 or param3 fields contains "energy": Filter key: wildcardOr param1 param2 param3. Filter value: energy</li>
     * <li>any of param1, param2 or param3 fields start with "energy": Filter key: likeCriterias param1 param2 param3. Filter value: *energy</li>
     * <li>any of param1, param2 or param3 fields is "energy": Filter key: likeCriterias param1 param2 param3. Filter value: energy</li>
     * </ul>
     * 
     * 
     * @param tableName A name of a table to query
     * @param config Data filtering, sorting and pagination criteria
     * @return Query builder to filter entities according to pagination configuration data.
     */
    @SuppressWarnings({ "rawtypes" })
    public QueryBuilder getQuery(String tableName, PaginationConfiguration config) {

        QueryBuilder queryBuilder = new QueryBuilder("select * from " + tableName + " a ", "a");

        if (config == null) {
            return queryBuilder;
        }
        Map<String, Object> filters = config.getFilters();

        if (filters != null && !filters.isEmpty()) {

            for (String key : filters.keySet()) {

                Object filterValue = filters.get(key);
                if (filterValue == null) {
                    continue;
                }

                // Key format is: condition field1 field2 or condition-field1-field2-fieldN
                // example: "ne code", condition=code, fieldName=code, fieldName2=null
                String[] fieldInfo = key.split(" ");
                String condition = fieldInfo.length == 1 ? null : fieldInfo[0];
                String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1];
                String fieldName2 = fieldInfo.length == 3 ? fieldInfo[2] : null;

                String[] fields = null;
                if (condition != null) {
                    fields = Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length);
                }

                // if ranged search - field value in between from - to values. Specifies "from" value: e.g value<=field.value
                if ("fromRange".equals(condition)) {
                    if (filterValue instanceof Double) {
                        BigDecimal rationalNumber = new BigDecimal((Double) filterValue);
                        queryBuilder.addCriterion("a." + fieldName, " >= ", rationalNumber, true);
                    } else if (filterValue instanceof Number) {
                        queryBuilder.addCriterion("a." + fieldName, " >= ", filterValue, true);
                    } else if (filterValue instanceof Date) {
                        queryBuilder.addCriterionDateRangeFromTruncatedToDay("a." + fieldName, (Date) filterValue);
                    }

                    // if ranged search - field value in between from - to values. Specifies "to" value: e.g field.value<=value
                } else if ("toRange".equals(condition)) {
                    if (filterValue instanceof Double) {
                        BigDecimal rationalNumber = new BigDecimal((Double) filterValue);
                        queryBuilder.addCriterion("a." + fieldName, " <= ", rationalNumber, true);
                    } else if (filterValue instanceof Number) {
                        queryBuilder.addCriterion("a." + fieldName, " <= ", filterValue, true);
                    } else if (filterValue instanceof Date) {
                        queryBuilder.addCriterionDateRangeToTruncatedToDay("a." + fieldName, (Date) filterValue);
                    }

                    // Value is in field value (list)
                } else if ("list".equals(condition)) {
                    String paramName = queryBuilder.convertFieldToParam(fieldName);
                    queryBuilder.addSqlCriterion(":" + paramName + " in elements(a." + fieldName + ")", paramName, filterValue);

                    // Field value is in value (list)
                } else if ("inList".equals(condition) || "not-inList".equals(condition)) {

                    boolean isNot = "not-inList".equals(condition);

                    if (filterValue instanceof String) {
                        queryBuilder.addSql("a." + fieldName + (isNot ? " NOT " : "") + " IN (" + filterValue + ")");
                    } else if (filterValue instanceof Collection) {
                        String paramName = queryBuilder.convertFieldToParam(fieldName);
                        queryBuilder.addSqlCriterion("a." + fieldName + (isNot ? " NOT " : "") + " IN (:" + paramName + ")", paramName, filterValue);
                    }

                    // The value is in between two field values
                } else if ("minmaxRange".equals(condition)) {
                    if (filterValue instanceof Double) {
                        BigDecimal rationalNumber = new BigDecimal((Double) filterValue);
                        queryBuilder.addCriterion("a." + fieldName, " <= ", rationalNumber, false);
                        queryBuilder.addCriterion("a." + fieldName2, " >= ", rationalNumber, false);
                    } else if (filterValue instanceof Number) {
                        queryBuilder.addCriterion("a." + fieldName, " <= ", filterValue, false);
                        queryBuilder.addCriterion("a." + fieldName2, " >= ", filterValue, false);
                    }
                    if (filterValue instanceof Date) {
                        Date value = (Date) filterValue;
                        Calendar c = Calendar.getInstance();
                        c.setTime(value);
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int date = c.get(Calendar.DATE);
                        c.set(year, month, date, 0, 0, 0);
                        value = c.getTime();
                        queryBuilder.addCriterion("a." + fieldName, "<=", value, false);
                        queryBuilder.addCriterion("a." + fieldName2, ">=", value, false);
                    }

                    // The value is in between two field values with either them being optional
                } else if ("minmaxOptionalRange".equals(condition)) {

                    String paramName = queryBuilder.convertFieldToParam(fieldName);

                    String sql = "((a." + fieldName + " IS NULL and a." + fieldName2 + " IS NULL) or (a." + fieldName + "<=:" + paramName + " and :" + paramName + "<a."
                            + fieldName2 + ") or (a." + fieldName + "<=:" + paramName + " and a." + fieldName2 + " IS NULL) or (a." + fieldName + " IS NULL and :" + paramName
                            + "<a." + fieldName2 + "))";
                    queryBuilder.addSqlCriterionMultiple(sql, paramName, filterValue);

                    // The value range is overlapping two field values with either them being optional
                } else if ("overlapOptionalRange".equals(condition)) {

                    String paramNameFrom = queryBuilder.convertFieldToParam(fieldName);
                    String paramNameTo = queryBuilder.convertFieldToParam(fieldName2);

                    String sql = "(( a." + fieldName + " IS NULL and a." + fieldName2 + " IS NULL) or  ( a." + fieldName + " IS NULL and a." + fieldName2 + ">:" + paramNameFrom
                            + ") or (a." + fieldName2 + " IS NULL and a." + fieldName + "<:" + paramNameTo + ") or (a." + fieldName + " IS NOT NULL and a." + fieldName2
                            + " IS NOT NULL and ((a." + fieldName + "<=:" + paramNameFrom + " and :" + paramNameFrom + "<a." + fieldName2 + ") or (:" + paramNameFrom + "<=a."
                            + fieldName + " and a." + fieldName + "<:" + paramNameTo + "))))";

                    if (filterValue.getClass().isArray()) {
                        queryBuilder.addSqlCriterionMultiple(sql, paramNameFrom, ((Object[]) filterValue)[0], paramNameTo, ((Object[]) filterValue)[1]);
                    } else if (filterValue instanceof List) {
                        queryBuilder.addSqlCriterionMultiple(sql, paramNameFrom, ((List) filterValue).get(0), paramNameTo, ((List) filterValue).get(1));
                    }

                    // Any of the multiple field values wildcard or not wildcard match the value (OR criteria)
                } else if ("likeCriterias".equals(condition)) {

                    queryBuilder.startOrClause();
                    if (filterValue instanceof String) {
                        String filterString = (String) filterValue;
                        for (String field : fields) {
                            queryBuilder.addCriterionWildcard("a." + field, filterString, true);
                        }
                    }
                    queryBuilder.endOrClause();

                    // Any of the multiple field values wildcard match the value (OR criteria) - a diference from "likeCriterias" is that wildcard will be appended to the value
                    // automatically
                } else if (PersistenceService.SEARCH_WILDCARD_OR.equals(condition)) {
                    queryBuilder.startOrClause();
                    for (String field : fields) {
                        queryBuilder.addSql("a." + field + " like '%" + filterValue + "%'");
                    }
                    queryBuilder.endOrClause();

                    // Just like wildcardOr but ignoring case :
                } else if (PersistenceService.SEARCH_WILDCARD_OR_IGNORE_CAS.equals(condition)) {
                    queryBuilder.startOrClause();
                    for (String field : fields) { // since SEARCH_WILDCARD_OR_IGNORE_CAS , then filterValue is necessary a String
                        queryBuilder.addSql("lower(a." + field + ") like '%" + String.valueOf(filterValue).toLowerCase() + "%'");
                    }
                    queryBuilder.endOrClause();

                    // Search by additional Sql clause with specified parameters
                } else if (PersistenceService.SEARCH_SQL.equals(key)) {
                    if (filterValue.getClass().isArray()) {
                        String additionalSql = (String) ((Object[]) filterValue)[0];
                        Object[] additionalParameters = Arrays.copyOfRange(((Object[]) filterValue), 1, ((Object[]) filterValue).length);
                        queryBuilder.addSqlCriterionMultiple(additionalSql, additionalParameters);
                    } else {
                        queryBuilder.addSql((String) filterValue);
                    }

                } else {
                    if (filterValue instanceof String && PersistenceService.SEARCH_IS_NULL.equals(filterValue)) {
                        queryBuilder.addSql("a." + fieldName + " is null ");

                    } else if (filterValue instanceof String && PersistenceService.SEARCH_IS_NOT_NULL.equals(filterValue)) {
                        queryBuilder.addSql("a." + fieldName + " is not null ");

                    } else if (filterValue instanceof String) {

                        // if contains dot, that means join is needed
                        String filterString = (String) filterValue;
                        boolean wildcard = (filterString.indexOf("*") != -1);
                        if (wildcard) {
                            queryBuilder.addCriterionWildcard("a." + fieldName, filterString, true, "ne".equals(condition));
                        } else {
                            queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " != " : " = ", filterString, true);
                        }

                    } else if (filterValue instanceof Date) {
                        queryBuilder.addCriterionDateTruncatedToDay("a." + fieldName, (Date) filterValue);

                    } else if (filterValue instanceof Number) {
                        queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " != " : " = ", filterValue, true);

                    } else if (filterValue instanceof Boolean) {
                        queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " not is" : " is ", filterValue, true);

                    } else if (filterValue instanceof Enum) {
                        if (filterValue instanceof IdentifiableEnum) {
                            String enumIdKey = new StringBuilder(fieldName).append("Id").toString();
                            queryBuilder.addCriterion("a." + enumIdKey, "ne".equals(condition) ? " != " : " = ", ((IdentifiableEnum) filterValue).getId(), true);
                        } else {
                            queryBuilder.addCriterionEnum("a." + fieldName, (Enum) filterValue, "ne".equals(condition) ? " != " : " = ");
                        }

                    } else if (filterValue instanceof List) {
                        queryBuilder.addSqlCriterion("a." + fieldName + ("ne".equals(condition) ? " not in  " : " in ") + ":" + fieldName, fieldName, filterValue);
                    }
                }
            }
        }

        queryBuilder.addPaginationConfiguration(config, "a");

        // log.trace("Filters is {}", filters);
        // log.trace("Query is {}", queryBuilder.getSqlString());
        // log.trace("Query params are {}", queryBuilder.getParams());
        return queryBuilder;
    }

    /**
     * Load and return the list of the records IN A MAP format from database according to sorting and paging information in {@link PaginationConfiguration} object.
     * 
     * @param tableName A name of a table to query
     * @param config Data filtering, sorting and pagination criteria
     * @return A list of map of values for each record
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> list(String tableName, PaginationConfiguration config) {

        QueryBuilder queryBuilder = getQuery(tableName, config);
        SQLQuery query = queryBuilder.getNativeQuery(getEntityManager(), true);
        return query.list();
    }

    /**
     * Load and return the list of the records IN A Object[] format from database according to sorting and paging information in {@link PaginationConfiguration} object.
     * 
     * @param tableName A name of a table to query
     * @param config Data filtering, sorting and pagination criteria
     * @return A list of Object[] values for each record
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> listAsObjets(String tableName, PaginationConfiguration config) {

        QueryBuilder queryBuilder = getQuery(tableName, config);
        SQLQuery query = queryBuilder.getNativeQuery(getEntityManager(), false);
        return query.list();
    }

    /**
     * Count number of records in a database table
     * 
     * @param tableName A name of a table to query
     * @param config Data filtering, sorting and pagination criteria
     * @return Number of entities.
     */
    public long count(String tableName, PaginationConfiguration config) {
        QueryBuilder queryBuilder = getQuery(tableName, config);
        Query query = queryBuilder.getNativeCountQuery(getEntityManager());
        Object count = query.getSingleResult();
        if (count instanceof Long) {
            return (Long) count;
        } else if (count instanceof BigDecimal) {
            return ((BigDecimal) count).longValue();
        } else if (count instanceof Integer) {
            return ((Integer) count).longValue();
        } else {
            return Long.valueOf(count.toString());
        }
    }

    /**
     * Create new or update existing custom table record value
     * 
     * @param tableName A name of a table to query
     * @param values Values to save
     * @throws BusinessException General exception
     */
    public void createOrUpdate(String tableName, List<Map<String, Object>> values) throws BusinessException {

        for (Map<String, Object> value : values) {

            // New record
            if (value.get("id") == null) {
                create(tableName, value);

                // Existing record
            } else {
                update(tableName, value);
            }
        }
    }

    /**
     * Return an entity manager for a current provider
     * 
     * @return Entity manager
     */
    public EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }
}