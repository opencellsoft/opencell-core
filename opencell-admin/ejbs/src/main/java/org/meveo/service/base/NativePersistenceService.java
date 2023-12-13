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
package org.meveo.service.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.generics.GenericHelper;
import org.meveo.api.generics.GenericRequestMapper;
import org.meveo.api.generics.PersistenceServiceHelper;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.ImmutableGenericPagingAndFiltering;
import org.meveo.apiv2.generic.ImmutableGenericPagingAndFiltering.Builder;
import org.meveo.commons.utils.ListUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.CustomTableEvent;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.transformer.AliasToEntityOrderedMapResultTransformer;
import org.meveo.service.base.expressions.NativeExpressionFactory;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.notification.GenericNotificationService;
import org.meveo.util.MeveoParamBean;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.ListUtils.partition;
import static org.meveo.commons.utils.ParamBean.getInstance;

/**
 * Generic implementation that provides the default implementation for persistence methods working directly with native DB tables
 *
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 9.3.1
 */
@Named
public class NativePersistenceService extends BaseService {

    /**
     * ID field name
     */
    public static final String FIELD_ID = "id";

    /**
     * Valid from field name
     */
    public static final String FIELD_VALID_FROM = "valid_from";

    /**
     * Validity priority field name
     */
    public static final String FIELD_VALID_PRIORITY = "valid_priority";

    /**
     * Disabled field name
     */
    public static final String FIELD_DISABLED = "disabled";

    /**
     *
     */
    public static final int SHORT_MAX_VALUE = 32767;

    /**
     * Field name to field data type mapping to be used when native query caching is enabled. Format <db tablename:<field name,data type>>
     */
    private static Map<String, Map<String, CustomFieldTypeEnum>> fieldDataTypeMappings;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    @MeveoParamBean
    protected ParamBean paramBean;

    @Inject
    private DeletionService deletionService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    protected Event<CustomTableEvent> entityChangeEventProducer;

    @Inject
    private GenericNotificationService genericNotificationService;

    /**
     * Find record by its identifier
     *
     * @param tableName Table name
     * @param id Identifier
     * @return A map of values with field name as a map key and field value as a map value. Or null if no record was found with such identifier.
     */
    public Map<String, Object> findById(String tableName, Long id) {
        return findByIdWithouCheckCodeAndDescription(tableName, id, true);
    }

    /**
     * Find record by its identifier
     *
     * @param tableName Table name
     * @param id Identifier
     * @param canCheck When True it will get id, and the code of the customer table
     * @return A map of values with field name as a map key and field value as a map value. Or null if no record was found with such identifier.
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    public Map<String, Object> findByIdWithouCheckCodeAndDescription(String tableName, Long id, boolean canCheck) {
        tableName = addCurrentSchema(tableName);
        try {
            Session session = getEntityManager().unwrap(Session.class);
            StringBuilder selectQuery = new StringBuilder("select * from ").append(tableName).append(" e where id=:id");
            SQLQuery query = session.createSQLQuery(selectQuery.toString());
            query.setParameter("id", id);
            if (canCheck) {
                query.addScalar("id", new LongType());
                query.setFlushMode(FlushMode.COMMIT);
            }

            query.setResultTransformer(AliasToEntityOrderedMapResultTransformer.INSTANCE);

            Map<String, Object> values = (Map<String, Object>) query.uniqueResult();
            if (values != null) {
                for (String key : values.keySet()) {
                    if (values.get(key) instanceof java.sql.Timestamp) {
                        java.sql.Timestamp date = (java.sql.Timestamp) values.get(key);
                        values.put(key, new Date(date.getTime()));
                    }
                }

                return values;
            } else {
                return null;
                // throw new BusinessException("Failed to retrieve values from table " + tableName + " by id " + id);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve values from table by id {}/{} sql {}", tableName, id, e);
            throw e;
        }
    }

    /**
     * Insert values into table
     *
     * @param tableName Table name to insert values to
     * @param values Values to insert
     * @throws BusinessException General exception
     */
    public Long create(String tableName, Map<String, Object> values) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        return create(tableName, values, true, true);
    }

    /**
     * Insert multiple values into table. Uses a prepared statement.
     * <p>
     * NOTE: The sql statement is determined by the fields passed in the first value, so its important that either all values have the same fields (order does not matter), or first
     * value has the maximum number of fields
     *
     * @param tableName Table name to insert values to
     * @param customEntityTemplateCode Custom entity template, corresponding to a custom table, code
     * @param values A list of values to insert
     * @throws BusinessException General exception
     */
    public void create(String tableName, String customEntityTemplateCode, List<Map<String, Object>> values) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        if (values == null || values.isEmpty()) {
            return;
        }

        StringBuffer sql = new StringBuffer();
        Map<String, Object> firstValue = values.get(0);

        sql.append("insert into ").append(tableName);
        StringBuffer fields = new StringBuffer();
        StringBuffer fieldValues = new StringBuffer();
        List<String> fieldNames = new LinkedList<>();
        Map<String, Object> customTableFields = getFields(customEntityTemplateCode);
        boolean first = true;
        for (String fieldName : customTableFields.keySet()) {

            if (!first) {
                fields.append(",");
                fieldValues.append(",");
            }
            fieldNames.add(fieldName);
            fields.append(fieldName);
            fieldValues.append("?");
            first = false;
        }

        sql.append(" (").append(fields).append(") values (").append(fieldValues).append(")");

        Session hibernateSession = getEntityManager().unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

                    Object fieldValue = null;
                    int i = 1;
                    int itemsProcessed = 0;
                    for (Map<String, Object> value : values) {

                        i = 1;
                        for (String fieldName : fieldNames) {
                            fieldValue = value.get(fieldName);
                            Object defaultValue = customTableFields.get(fieldName);
                            if (fieldValue == null) {
                                fieldValue = defaultValue;
                            }
                            if (fieldValue instanceof String) {
                                preparedStatement.setString(i, (String) fieldValue);
                            } else if (fieldValue instanceof Long) {
                                preparedStatement.setLong(i, (Long) fieldValue);
                            } else if (fieldValue instanceof Double) {
                                preparedStatement.setDouble(i, (Double) fieldValue);
                            } else if (fieldValue instanceof BigInteger) {
                                preparedStatement.setInt(i, ((BigInteger) fieldValue).intValue());
                            } else if (fieldValue instanceof Integer) {
                                preparedStatement.setInt(i, ((Integer) fieldValue).intValue());
                            } else if (fieldValue instanceof BigDecimal) {
                                preparedStatement.setBigDecimal(i, (BigDecimal) fieldValue);
                            } else if (fieldValue instanceof Date) {
                                preparedStatement.setTimestamp(i, new Timestamp(((Date) fieldValue).getTime()));
                            } else if (fieldValue instanceof Boolean) {
                                preparedStatement.setBoolean(i, (Boolean) fieldValue);
                            } else if (fieldValue == null) {
                                preparedStatement.setNull(i, Types.NULL);
                            }

                            i++;
                        }

                        preparedStatement.addBatch();

                        // Batch size: 20
                        if (itemsProcessed > 1 && itemsProcessed % 500 == 0) {
                            preparedStatement.executeBatch();
                        }
                        itemsProcessed++;
                    }
                    preparedStatement.executeBatch();

                } catch (SQLException e) {
                    log.error("Failed to bulk insert with sql {}", sql, e);
                    throw e;
                }
            }
        });
    }

    /**
     * List all fields with their default values of tableName
     *
     * @param tableName the table name
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<String, Object> getFields(String tableName) {
        tableName = addCurrentSchema(tableName);
        Map<String, Object> fields = new HashedMap();
        Map<String, CustomFieldTemplate> customFieldTemplateMap = customFieldTemplateService.findByAppliesTo(CustomEntityTemplate.CFT_PREFIX + "_" + tableName);
        for (String key : customFieldTemplateMap.keySet()) {
            CustomFieldTemplate cft = customFieldTemplateMap.get(key);
            Class clazz = cft.getFieldType().getDataClass();
            String defaultValueString = cft.getDefaultValue();
            if (StringUtils.isBlank(defaultValueString)) {
                fields.put(cft.getDbFieldname(), defaultValueString);
                continue;
            }
            Object defaultValue = defaultValueString;

            if (Long.class.equals(clazz)) {
                defaultValue = Long.valueOf(defaultValueString);
            } else if (Double.class.equals(clazz)) {
                defaultValue = Double.valueOf(defaultValueString);
            } else if (BigInteger.class.equals(clazz)) {
                defaultValue = new BigInteger(defaultValueString);
            } else if (Integer.class.equals(clazz)) {
                defaultValue = Integer.valueOf(defaultValueString);
            } else if (BigDecimal.class.equals(clazz)) {
                defaultValue = new BigDecimal(defaultValueString);
            } else if (Date.class.equals(clazz)) {
                defaultValue = DateUtils.parseDateWithPattern(defaultValueString, DateUtils.DATE_TIME_PATTERN);
            } else if (Boolean.class.equals(clazz)) {
                defaultValue = Boolean.valueOf(defaultValueString);
            }
            fields.put(cft.getDbFieldname(), defaultValue);
        }
        return fields;
    }

    /**
     * Insert a new record into a table. If returnId=True values parameter will be updated with 'id' field value.
     *
     * @param tableName         Table name to update
     * @param values            Values
     * @param returnId          Should identifier be returned - does a lookup in DB by matching same values. If True values will be updated with 'id' field value.
     * @param fireNotifications Should notifications be fired upon record creation
     * @throws BusinessException General exception
     */
    protected Long create(String tableName, Map<String, Object> values, boolean returnId, boolean fireNotifications) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        StringBuffer sql = new StringBuffer();
        try {

            // Change ID field data type to long
            Object id = values.get(FIELD_ID);
            if (id != null) {
                if (id instanceof String) {
                    id = Long.parseLong((String) id);
                } else if (id instanceof BigInteger) {
                    id = ((BigInteger) id).longValue();
                }
                values.put(FIELD_ID, id);
            } else {
                id = getNextValueFromSequence(tableName);
                values.put(FIELD_ID, id);
            }

            sql.append("insert into ").append(tableName);
            StringBuffer fields = new StringBuffer();
            StringBuffer fieldValues = new StringBuffer();
            StringBuffer findIdFields = new StringBuffer();

            boolean first = true;
            if (values.isEmpty()) {
                sql.append(" DEFAULT VALUES");
            } else {
                for (String fieldName : values.keySet()) {
                    // Ignore a null ID field
                    if (fieldName.equals(FIELD_ID) && values.get(fieldName) == null) {
                        continue;
                    }

                    if (!first) {
                        fields.append(",");
                        fieldValues.append(",");
                        findIdFields.append(" and ");
                    }
                    fields.append(fieldName);
                    if (values.get(fieldName) == null) {
                        fieldValues.append("NULL");
                        findIdFields.append(fieldName).append(" IS NULL");
                    } else {
                        fieldValues.append(":").append(fieldName);
                        findIdFields.append(fieldName).append("=:").append(fieldName);
                    }
                    first = false;
                }

                sql.append(" (").append(fields).append(") values (").append(fieldValues).append(")");
            }
            Query query = getEntityManager().createNativeQuery(sql.toString());
            for (String fieldName : values.keySet()) {
                if (values.get(fieldName) == null) {
                    continue;
                }
                query.setParameter(fieldName, values.get(fieldName));
            }
            query.executeUpdate();

            Long result = null;
            // Find the identifier of the last inserted record
            if (fireNotifications) {
                returnId = true;
            }
            if (returnId) {
                if (id != null) {
                    if (id instanceof Number) {
                        result = ((Number) id).longValue();
                    }

                } else {
                    StringBuffer requestConstruction = buildSqlInsertionRequest(tableName, findIdFields);

                    query = getEntityManager().createNativeQuery(requestConstruction.toString()).setMaxResults(1);
                    for (String fieldName : values.keySet()) {
                        if (values.get(fieldName) == null) {
                            continue;
                        }
                        query.setParameter(fieldName, values.get(fieldName));
                    }
                    id = query.getSingleResult();
                    if (id instanceof Number) {
                        result = ((Number) id).longValue();
                    }
                }
            }
            if (fireNotifications) {
                entityChangeEventProducer.fire(new CustomTableEvent(tableName, result, values, NotificationEventTypeEnum.CREATED));
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to insert values into OR find ID of table {} {} sql {}", tableName, values, sql, e);
            throw e;
        }
    }

    StringBuffer buildSqlInsertionRequest(String tableName, StringBuffer findIdFields) {
        tableName = addCurrentSchema(tableName);
        StringBuffer requestConstruction = new StringBuffer("select id from " + tableName);
        if (StringUtils.isNotEmpty(findIdFields)) {
            requestConstruction.append(" where " + findIdFields);
        }
        requestConstruction.append(" order by id desc");
        return requestConstruction;
    }

    /**
     * Update a record in a table. Record is identified by an "id" field value.
     *
     * @param tableName         Table name to update
     * @param value             Values. Values must contain an "id" (FIELD_ID) field.
     * @param fireNotifications Should notifications be fired upon record update
     * @throws BusinessException General exception
     */
    public void update(String tableName, Map<String, Object> value, boolean fireNotifications) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        Number id = ((Number) value.get(FIELD_ID));
        if (id == null) {
            throw new BusinessException("'id' field value not provided to update values in native table");
        }

        StringBuffer sql = new StringBuffer();
        try {
            sql.append("update ").append(tableName).append(" set ");
            boolean first = true;
            for (String fieldName : value.keySet()) {
                if (fieldName.equals(FIELD_ID)) {
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
            if (fireNotifications) {
                entityChangeEventProducer.fire(new CustomTableEvent(tableName, id.longValue(), value, NotificationEventTypeEnum.UPDATED));
            }

        } catch (Exception e) {
            log.error("Failed to insert values into table {} {} sql {}", tableName, value, sql, e);
            throw e;
        }
    }

    /**
     * Update field value in a table
     *
     * @param tableName Table name to update
     * @param id        Record identifier
     * @param fieldName Field to update
     * @param value     New value
     * @throws BusinessException General exception
     */
    public void updateValue(String tableName, Long id, String fieldName, Object value) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        StringBuilder updateQuery = new StringBuilder("update ").append(tableName).append(" set ").append(fieldName).append(value == null ? "= null" : "= :" + fieldName).append(" where id= :id");
        try {
            if (value == null) {
                getEntityManager().createNativeQuery(updateQuery.toString()).setParameter("id", id).executeUpdate();
            } else {
                getEntityManager().createNativeQuery(updateQuery.toString()).setParameter(fieldName, value).setParameter("id", id).executeUpdate();
            }

        } catch (Exception e) {
            log.error("Failed to update value in table {}/{}/{}", tableName, fieldName, id);
            throw e;
        }
    }

    /**
     * Disable a record. Note: There is no check done that record exists.
     *
     * @param tableName Table name to update
     * @param id        Record identifier
     * @throws BusinessException General exception
     */
    public void disable(String tableName, Long id) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        getEntityManager().createNativeQuery("update " + tableName + " set disabled=1 where id=" + id).executeUpdate();
        entityChangeEventProducer.fire(new CustomTableEvent(tableName, id, null, NotificationEventTypeEnum.DISABLED));
    }

    /**
     * Disable multiple records. Note: There is no check done that records exists.
     *
     * @param tableName Table name to update
     * @param ids       A list of record identifiers
     * @throws BusinessException General exception
     */
    public void disable(String tableName, Set<Long> ids) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        getEntityManager().createNativeQuery("update " + tableName + " set disabled=1 where id in :ids").setParameter("ids", ids).executeUpdate();
    }

    /**
     * Enable a record. Note: There is no check done that record exists.
     *
     * @param tableName Table name to update
     * @param id        Record identifier
     * @throws BusinessException General exception
     */
    public void enable(String tableName, Long id) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        getEntityManager().createNativeQuery("update " + tableName + " set disabled=0 where id=" + id).executeUpdate();
        entityChangeEventProducer.fire(new CustomTableEvent(tableName, id, null, NotificationEventTypeEnum.ENABLED));
    }

    /**
     * Enable multiple records. Note: There is no check done that records exists.
     *
     * @param tableName Table name to update
     * @param ids       A list of record identifiers
     * @throws BusinessException General exception
     */
    public void enable(String tableName, Set<Long> ids) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        StringBuilder updateQuery = new StringBuilder("update ").append(tableName).append(" set ").append(FIELD_DISABLED).append("=0 where id in :ids");
        getEntityManager().createNativeQuery(updateQuery.toString()).setParameter("ids", ids).executeUpdate();
    }

    /**
     * Delete a record. Note: There is no check done that record exists.
     *
     * @param tableName Table name to update
     * @param id        Record identifier
     * @return Number of records deleted
     * @throws BusinessException General exception
     */
    public int remove(String tableName, Long id) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        this.deletionService.checkTableNotreferenced(tableName, id);
        Map<String, Object> values = findByIdWithouCheckCodeAndDescription(tableName, id, false);
        if (values == null) {
            return 0;
        }
        int nrDeleted = getEntityManager().createNativeQuery("delete from " + tableName + " where id=" + id).executeUpdate();
        if (nrDeleted > 0) {
            entityChangeEventProducer.fire(new CustomTableEvent(tableName, id, values, NotificationEventTypeEnum.REMOVED));
        }
        return nrDeleted;
    }

    /**
     * Delete multiple records. Note: There is no check done that records exists.
     * Will call find by id and check if it has a code or description field other will throw 
     * excpetion
     *
     * @param tableName Table name to delete from
     * @param ids       A set of record identifiers
     * @return Number of records deleted
     * @throws BusinessException General exception
     */
    public int remove(String tableName, Set<Long> ids) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        int nrDeleted = 0;
        for (Long id : ids) {
            nrDeleted = nrDeleted + remove(tableName, id);
        }
        return nrDeleted;

        // TODO. Here could be a check that if no notification exist, delete it in batch mode
//        ids.stream().forEach(id -> deletionService.checkTableNotreferenced(tableName, id));
//        return getEntityManager().createNativeQuery("delete from " + tableName + " where id in :ids").setParameter("ids", ids).executeUpdate();
    }
    
    /**
     * Delete multiple records. Note: There is no check done that records exists.
     * Will not check Code or description field, it will find the table by id and then delete
     *
     * @param tableName Table name to delete from
     * @param ids       A set of record identifiers
     * @return Number of records deleted
     * @throws BusinessException General exception
     */
    public int removeById(String tableName, Set<Long> ids) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        int nrDeleted = 0;
        for (Long id : ids) {
            nrDeleted = nrDeleted + remove(tableName, id);
        }
        return nrDeleted;

    }

    /**
     * Delete all records
     *
     * @param tableName Table name to update
     * @return Number of records deleted
     * @throws BusinessException General exception
     */
    public int remove(String tableName) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        StringBuilder deleteQuery = new StringBuilder("delete from ").append(tableName);
        return getEntityManager().createNativeQuery(deleteQuery.toString()).executeUpdate();
    }

    /**
     * Retrieve values from a table
     *
     * @param tableName Table name to query
     * @return A list of map of values with field name as map's key and field value as map's value
     */
    public List<Map<String, Object>> list(String tableName) {
        tableName = addCurrentSchema(tableName);
        return list(tableName, null);
    }

    /**
     * Retrieve ONLY enabled values from a table
     *
     * @param tableName Table name to query
     * @return A list of map of values with field name as map's key and field value as map's value
     */
    public List<Map<String, Object>> listActive(String tableName) {
        tableName = addCurrentSchema(tableName);
        Map<String, Object> filters = new HashMap<>();
        filters.put(FIELD_DISABLED, 0);
        return list(tableName, new PaginationConfiguration(filters));
    }

    /**
     * Creates NATIVE query to filter entities according data provided in pagination configuration.
     * <p>
     * Search filters (key = Filter key, value = search pattern or value).
     * <p>
     * Filter key can be:
     * <ul>
     * <li>SQL. Additional sql to apply. Value is either a sql query or an array consisting of sql query and one or more parameters to apply</li>
     * <li>&lt;condition&gt; &lt;fieldname1&gt; &lt;fieldname2&gt; ... &lt;fieldnameN&gt;. Value is a value to apply in condition</li>
     * </ul>
     * <p>
     * A union between different filter items is AND.
     * <p>
     * <p>
     * Condition is optional. Number of fieldnames depend on condition used. If no condition is specified an "equals ignoring case" operation is considered.
     * <p>
     * <p>
     * Following conditions are supported:
     * <ul>
     * <li><b>fromRange</b>. Ranged search - field value in between from - to values. Specifies "from" part value: e.g value&lt;=fieldValue. Applies to date and number type fields.
     * Date value is truncated to start of the day</li>
     * <li><b>toRange</b>. Ranged search - field value in between from - to values. Specifies "to" part value: e.g fieldValue&lt;value. Value is exclusive. Applies to date and
     * number type fields. Date value is truncated to the start of the day</li>
     * <li><b>toRangeInclusive</b>. Ranged search - field value in between from - to values. Specifies "to" part value: e.g fieldValue&lt;=value. Value is inclusive. Applies to
     * date and number type fields. Date value is truncated to the end of the day</li>
     * <li><b>fromOptionalRange</b>. Ranged search - field value in between from - to values. Field value is optional. Specifies "from" part value: e.g value&lt;=field.value.
     * Applies to date and number type fields. Date value is truncated to start of the day</li>
     * <li><b>toOptionalRange</b>. Ranged search - field value in between from - to values. Field value is optional. Specifies "to" part value: e.g fieldValue&lt;value. Value is
     * inclusive. Applies to date and number type fields. Date value is truncated to the start of the day</li>
     * <li><b>toOptionalRangeInclusive</b>. Ranged search - field value in between from - to values. Field value is optional. Specifies "to" part value: e.g fieldValue&lt;=value.
     * Value is inclusive. Applies to date and number type fields. Date value is truncated to the end of the day</li>
     * <li><b>list</b>. Value is in field's list value. Applies to date and number type fields.</li>
     * <li><b>inList</b>/<b>not-inList</b>. Field value is [not] in value (list). A comma separated string will be parsed into a list if values. A single value will be considered
     * as a list value of one item</li>
     * <li><b>minmaxRange</b>. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields. The TO field value is exclusive.
     * Date value is truncated to the start of the day. E.f. field1Value&lt;value&ltfield2Value</li>
     * <li><b>minmaxRangeInclusive</b>. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields. The TO field value is
     * inclusive. Date value is truncated to the start of the day. E.g. field1Value&lt;=value&ltfield2Value</li>
     * <li><b>minmaxOptionalRange</b>. Similar to minmaxRange. The value is in between two field values with either them being optional. TWO fieldnames must be specified. The TO
     * field value is exclusive. Date value is truncated to the start of the day.</li>
     * <li><b>minmaxOptionalRangeInclusive</b>. Similar to minmaxRangeOptional. The value is in between two field values with either them being optional. TWO fieldnames must be
     * specified. The TO field value is inclusive. Date value is truncated to the start of the day.</li>
     * <li><b>overlapOptionalRange</b>. The value range is overlapping two field values with either them being optional. TWO fieldnames must be specified. Value must be an array or
     * a list of two values. End fields and to values are exclusive.</li>
     * <li><b>overlapOptionalRangeInclusive</b>. The value range is overlapping two field values with either them being optional. TWO fieldnames must be specified. Value must be an
     * array or a list of two values. End fields and to values are inclusive.</li>
     * <li><b>likeCriterias</b>. Multiple fieldnames can be specified. Any of the multiple field values match the value (OR criteria). In case value contains *, a like criteria
     * match will be used. In either case case insensative matching is used. Applies to String type fields.</li>
     * <li><b>wildcardOr</b>. Similar to likeCriterias. A wildcard match will always used. A * will be appended to start and end of the value automatically if not present. Applies
     * to
     * <li><b>wildcardOrIgnoreCase</b>. Similar to wildcardOr but ignoring case String type fields.</li>
     * <li><b>eq</b>. Equals. Supports wildcards in case of string value. NOTE: This is a default behavior when condition is not specified
     * <li><b>eqOptional</b>. Equals. Supports wildcards in case of string value. Field value is optional.
     * <li><b>ne</b>. Not equal.
     * <li><b>neOptional</b>. Not equal. Field value is optional
     * </ul>
     * <p>
     * <p>
     * "eq" is a default condition when no condition is not specified
     * <p>
     * Following special meaning values are supported:
     * <ul>
     * <li>IS_NULL. Field value is null</li>
     * <li>IS_NOT_NULL. Field value is not null</li>
     * </ul>
     * <p>
     * <p>
     * <p>
     * To filter by a related entity's field you can either filter by related entity's field or by related entity itself specifying code as value. These two example will do the
     * same in case when quering a customer account: customer.code=aaa OR customer=aaa
     * <p>
     * To filter a list of related entities by a list of entity codes use "inList" on related entity field. e.g. for quering offer template by sellers: inList sellers=code1,code2
     *
     *
     * <b>Note:</b> Quering by related entity field directly will result in exception when entity with a specified code does not exists
     * <p>
     * <p>
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
     * @param tableName A name of a table to query
     * @param config    Data filtering, sorting and pagination criteria
     * @param id Id field value to explicitly extract data by ID 
     * @return Query builder to filter entities according to pagination configuration data.
     */
    public QueryBuilder getQuery(String tableName, PaginationConfiguration config, Long id) {
        tableName = addCurrentSchema(tableName);
        Predicate<String> predicate = field -> this.checkAggFunctions(field.toUpperCase().trim());
        String aggFields = (config != null && config.getFetchFields() != null) ? aggregationFields(config.getFetchFields(), predicate) : "";
        if (!aggFields.isEmpty()) {
            config.getFetchFields().remove("id");
        }

        List<String> fetch = (config != null && config.getFetchFields() != null) ? config.getFetchFields().stream().filter(s -> s.contains(".")).map(s -> s.substring(0, s.lastIndexOf("."))).distinct().collect(Collectors.toList()) : Collections.emptyList();
        Map<String, String> fetchAlias = fetch.stream().collect(Collectors.toMap(Function.identity(), s -> QueryBuilder.getJoinAlias("a", s)));

        String fieldsToRetrieve = (config != null && config.getFetchFields() != null) ? retrieveFields(config.getFetchFields(), predicate.negate()) : "";
        if (fieldsToRetrieve.isEmpty() && aggFields.isEmpty()) {
            fieldsToRetrieve = "*";
        }
        StringBuilder fetchSql = new StringBuilder();
        if (!ListUtils.isEmtyCollection(fetch)) {
            for (String fetchField : fetch) {
                String joinAlias = fetchField.contains(QueryBuilder.JOIN_AS) ? "" : QueryBuilder.JOIN_AS + fetchAlias.get(fetchField);
                fetchSql.append(" left join " + "a" + "." + fetchField + joinAlias);
            }
        }
        StringBuilder sql = new StringBuilder("select " + buildFields(fieldsToRetrieve, aggFields) + " from " + tableName + " a ");
        sql.append(fetchSql);
        sql.append(" ");
        QueryBuilder queryBuilder = new QueryBuilder(sql.toString(), "a");

        if (id != null) {
            queryBuilder.addSql(" a.id ='" + id + "'");
        }

        if (config == null) {
            return queryBuilder;
        }
        Map<String, Object> filters = config.getFilters();

        if (filters != null && !filters.isEmpty()) {
            NativeExpressionFactory nativeExpressionFactory = new NativeExpressionFactory(queryBuilder, "a");
            filters.keySet().stream()
            		.sorted((k1, k2) -> org.apache.commons.lang3.StringUtils.countMatches(k2, ".") - org.apache.commons.lang3.StringUtils.countMatches(k1, "."))
                    .filter(key -> filters.get(key) != null)
                .forEach(key -> nativeExpressionFactory.addFilters(key, filters.get(key)));

        }

        if (aggFields.isEmpty()) {
            queryBuilder.addPaginationConfiguration(config, "a");
        }
        if (!aggFields.isEmpty() && !fieldsToRetrieve.isEmpty()) {
            queryBuilder.addGroupCriterion(fieldsToRetrieve);
        }

        // log.trace("Filters is {}", filters);
        // log.trace("Query is {}", queryBuilder.getSqlString());
        // log.trace("Query params are {}", queryBuilder.getParams());
        return queryBuilder;

    }

    /**
     * Get Query without adding dependencies left join
     * 
     * @param tableName Table name
     * @param config {@link PaginationConfiguration}
     * @param id Id
     * @return {@link QueryBuilder}
     */
    public QueryBuilder getQueryWithoutDependencies(String tableName, PaginationConfiguration config, Long id) {
        tableName = addCurrentSchema(tableName);
        Predicate<String> predicate = field -> this.checkAggFunctions(field.toUpperCase().trim());
        String aggFields = (config != null && config.getFetchFields() != null) ? aggregationFields(config.getFetchFields(), predicate) : "";
        if (!aggFields.isEmpty()) {
            config.getFetchFields().remove("id");
        }

        String fieldsToRetrieve = (config != null && config.getFetchFields() != null) ? retrieveFields(config.getFetchFields(), predicate.negate()) : "";
        if (fieldsToRetrieve.isEmpty() && aggFields.isEmpty()) {
            fieldsToRetrieve = "*";
        }

        StringBuilder sql = new StringBuilder("select " + buildFields(fieldsToRetrieve, aggFields) + " from " + tableName + " a ");
        sql.append(" ");
        QueryBuilder queryBuilder = new QueryBuilder(sql.toString(), "a");

        if (id != null) {
            queryBuilder.addSql(" a.id ='" + id + "'");
        }

        if (config == null) {
            return queryBuilder;
        }
        Map<String, Object> filters = config.getFilters();

        if (filters != null && !filters.isEmpty()) {
            NativeExpressionFactory nativeExpressionFactory = new NativeExpressionFactory(queryBuilder, "a");
            filters.keySet().stream()
                    .filter(key -> filters.get(key) != null)
                    .forEach(key -> nativeExpressionFactory.addFilters(key, filters.get(key)));

        }

        if (aggFields.isEmpty()) {
            queryBuilder.addPaginationConfiguration(config, "a");
        }
        if (!aggFields.isEmpty() && !fieldsToRetrieve.isEmpty()) {
            queryBuilder.addGroupCriterion(fieldsToRetrieve);
        }

        return queryBuilder;

    }

    /**
     * Specific getQuery
     * @param tableName entity name
     * @param defaultLeftJoinWithAlias if needed, add left join alias when using SQL criteria with OR criteria
     * @param config PaginationConfoguration
     * @param id id
     * @return QueryBuilder
     */
    public QueryBuilder getQuery(String tableName, String defaultLeftJoinWithAlias, PaginationConfiguration config, Long id) {
        Predicate<String> predicate = field -> this.checkAggFunctions(field.toUpperCase().trim());
        String aggFields = (config != null && config.getFetchFields() != null) ? aggregationFields(config.getFetchFields(), predicate) : "";
        if (!aggFields.isEmpty()) {
            config.getFetchFields().remove("id");
        }
        String fieldsToRetrieve = (config != null && config.getFetchFields() != null) ? retrieveFields(config.getFetchFields(), predicate.negate()) : "";
        if (fieldsToRetrieve.isEmpty() && aggFields.isEmpty()) {
            fieldsToRetrieve = "*";
        }

        StringBuilder queryInit = new StringBuilder();
        queryInit.append("SELECT ").append(buildFields(fieldsToRetrieve, aggFields)).append(" FROM ").append(addCurrentSchema(tableName)).append(" a ");
        if (StringUtils.isNotBlank(defaultLeftJoinWithAlias)) {
            queryInit.append(" LEFT JOIN ").append(defaultLeftJoinWithAlias);
        }

        QueryBuilder queryBuilder = new QueryBuilder(queryInit.toString(), "a");
        if (id != null) {
            queryBuilder.addSql(" a.id ='" + id + "'");
        }

        if (config == null) {
            return queryBuilder;
        }
        Map<String, Object> filters = config.getFilters();

        if (filters != null && !filters.isEmpty()) {
            NativeExpressionFactory nativeExpressionFactory = new NativeExpressionFactory(queryBuilder, "a");
            filters.keySet().stream()
            		.sorted((k1, k2) -> org.apache.commons.lang3.StringUtils.countMatches(k2, ".") - org.apache.commons.lang3.StringUtils.countMatches(k1, "."))
                    .filter(key -> filters.get(key) != null)
                .forEach(key -> nativeExpressionFactory.addFilters(key, filters.get(key)));

        }

        if (aggFields.isEmpty()) {
            queryBuilder.addPaginationConfiguration(config, "a");
        }
        if (!aggFields.isEmpty() && !fieldsToRetrieve.isEmpty()) {
            queryBuilder.addGroupCriterion(fieldsToRetrieve);
        }

        return queryBuilder;

    }

    public QueryBuilder getAggregateQuery(String tableName, PaginationConfiguration config, Long id) {
        return getAggregateQuery(tableName, config, id, null, null);
    }

    public QueryBuilder getAggregateQuery(String tableName, PaginationConfiguration config, Long id, String extraCondition,
                                          String leftJoinClause) {
        tableName = addCurrentSchema(tableName);

        String fieldsToRetrieve = (config != null && config.getFetchFields() != null) ? retrieveFields(config.getFetchFields(), null) : "";
        if (!fieldsToRetrieve.isEmpty()) {
            config.getFetchFields().remove("id");
        }

        QueryBuilder queryBuilder;
        if (leftJoinClause != null) {
            queryBuilder = new QueryBuilder("select " + buildFields(fieldsToRetrieve, "") + " from "
                    + tableName + " a " + leftJoinClause, "a");
        }
        else {
            queryBuilder = new QueryBuilder("select " + buildFields(fieldsToRetrieve, "") + " from " + tableName + " a ", "a");
        }

        if (id != null) {
            queryBuilder.addSql(" a.id ='" + id + "'");
        }

        if (extraCondition != null) {
            queryBuilder.addSql(extraCondition);
        }

        if (config == null) {
            return queryBuilder;
        }
        Map<String, Object> filters = config.getFilters();

        if (filters != null && !filters.isEmpty()) {
            NativeExpressionFactory nativeExpressionFactory = new NativeExpressionFactory(queryBuilder, "a");
            filters.keySet().stream()
            		.sorted((k1, k2) -> org.apache.commons.lang3.StringUtils.countMatches(k2, ".") - org.apache.commons.lang3.StringUtils.countMatches(k1, "."))
                    .filter(key -> filters.get(key) != null)
                .forEach(key -> nativeExpressionFactory.addFilters(key, filters.get(key)));
        }

        if (config.getOrderings() != null && config.getOrderings().length == 2) {
            if (config.getOrderings()[0].equals("id")
                    && config.getOrderings()[1].equals(PagingAndFiltering.SortOrder.ASCENDING)) {
                config.setOrderings(new Object[]{});
            }
        }

        queryBuilder.addPaginationConfiguration(config, "a");
        String fieldsToGroupBy = config.getGroupBy() != null ? retrieveFields(new ArrayList<>(config.getGroupBy()), null) : "";

        if (!fieldsToGroupBy.isEmpty()) {
            queryBuilder.addGroupCriterion(fieldsToGroupBy);
        }

        String fieldsFilteredByHaving = config.getHaving() != null ? retrieveFields(new ArrayList<>(config.getHaving()), null) : "";

        if (!fieldsFilteredByHaving.isEmpty()) {
            queryBuilder.addHavingCriterion(fieldsFilteredByHaving);
        }

        // log.trace("Filters is {}", filters);
        // log.trace("Query is {}", queryBuilder.getSqlString());
        // log.trace("Query params are {}", queryBuilder.getParams());
        return queryBuilder;

    }

    private String buildFields(String fieldsToRetrieve, String aggFields) {
        if (!fieldsToRetrieve.isEmpty() && !aggFields.isEmpty()) {
            return String.join(",", fieldsToRetrieve, aggFields);
        } else if (!fieldsToRetrieve.isEmpty()) {
            return fieldsToRetrieve;
        } else {
            return aggFields;
        }
    }

    private String retrieveFields(List<String> fields, Predicate<String> predicate) {
        if (predicate == null) {
            return fields
                    .stream()
                    .map(x -> {
                if (x.toLowerCase().trim().contains("->>string"))
                    return "varcharFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>double"))
                    return "numericFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>long"))
                    return "bigIntFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>date"))
                    return "timestampFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>boolean"))
                    return "booleanFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>entity"))
                    return "entityFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (checkAggFunctions(x.toUpperCase().trim()))
                    return x;
                else
                    return "a." + x;
                    })
                    .collect(joining(","));
        }
        else {
            return fields
                    .stream()
                    .filter(predicate)
                    .map(x -> {
                if (x.toLowerCase().trim().contains("->>string"))
                    return "varcharFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>double"))
                    return "numericFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>long"))
                    return "bigIntFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>date"))
                    return "timestampFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>boolean"))
                    return "booleanFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else if (x.toLowerCase().trim().contains("->>entity"))
                    return "entityFromJson(a.cfValues," + x.split("->>")[0] + "," + x.split("->>")[1] + ")";
                else
                    return "a." + x;
                    })
                    .collect(joining(","));
        }
    }

    private String aggregationFields(List<String> fields, Predicate<String> predicate) {
        return fields.stream()
                    .filter(predicate)
                    .collect(joining(","));
    }

    private boolean checkAggFunctions(String field) {
        if (field.startsWith("SUM(") || field.startsWith("COUNT(") || field.startsWith("AVG(")
                || field.startsWith("MAX(") || field.startsWith("MIN(") || field.startsWith("COALESCE(SUM(")
                || field.startsWith("STRING_AGG_LONG") || field.startsWith("TO_CHAR(") || field.startsWith("CAST(")) {
            return true;
        } else {
            return false;
        }
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
        
        tableName = tableName.toLowerCase();
        
        tableName = addCurrentSchema(tableName);
        QueryBuilder queryBuilder = getQuery(tableName, config, null);
        SQLQuery query = queryBuilder.getNativeQuery(getEntityManager(), true);

        if (config.isCacheable()) {

            Map<String, CustomFieldTypeEnum> tableFieldTypes = fieldDataTypeMappings.get(tableName);

            if (tableFieldTypes != null) {
                query.setCacheable(true);

                // Define a data type for all or only requested fields to fetch
                for (String fieldName : (config.getFetchFields() != null && !config.getFetchFields().isEmpty()) ? config.getFetchFields() : tableFieldTypes.keySet()) {

                    CustomFieldTypeEnum fieldType = tableFieldTypes.get(fieldName);
                    if (fieldType != null) {
                        query.addScalar(fieldName, fieldType.getHibernateType());
                    } else {
                        query.addScalar(fieldName, new StringType());
                    }
                }
            }
        }
        return query.setFlushMode(FlushMode.COMMIT).list();
    }

    /**
     * Load and return the list of the records IN A Object[] format from database according to sorting and paging information in {@link PaginationConfiguration} object. <br/>
     * In case a list of fields is provided in search and paging configuration, only that list of fields will be retrieved. Otherwise all fields will be retrieved.
     *
     * @param tableName A name of a table to query
     * @param config Data filtering, sorting and pagination criteria
     * @return A list of Object[] values for each record. A full list of fields or only the ones specified in a list of fields in search and paging configuration
     */
    @SuppressWarnings({"deprecation", "rawtypes"})
    public List listAsObjects(String tableName, PaginationConfiguration config) {
        tableName = addCurrentSchema(tableName);
        QueryBuilder queryBuilder = getQuery(tableName, config, null);
        SQLQuery query = queryBuilder.getNativeQuery(getEntityManager(), false);

        if (config.isCacheable()) {

            Map<String, CustomFieldTypeEnum> tableFieldTypes = fieldDataTypeMappings.get(tableName);

            if (tableFieldTypes != null) {
                query.setCacheable(true);

                // Define a data type for all or only requested fields to fetch
                for (String fieldName : (config.getFetchFields() != null && !config.getFetchFields().isEmpty()) ? config.getFetchFields() : tableFieldTypes.keySet()) {

                    CustomFieldTypeEnum fieldType = tableFieldTypes.get(fieldName);
                    if (fieldType != null) {
                        query.addScalar(fieldName, fieldType.getHibernateType());
                    } else {
                        query.addScalar(fieldName, new StringType());
                    }
                }
            }
        }
        return query.setFlushMode(FlushMode.COMMIT).list();
    }

    /**
     * Execute a search query with optional parameters
     *
     * @param sql A query to execute
     * @param maxResults Number of records to retrieve. Optional.
     * @param parameters Query parameters
     * @return A list of Object[] values for each record
     */
    @SuppressWarnings({ "rawtypes" })
    public List listAsObjects(String sql, Integer maxResults, Map<String, Object> parameters) {

        Query query = getEntityManager().createNativeQuery(sql);
        if (parameters != null) {
            for (Entry<String, Object> param : parameters.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
        }

        query.setFlushMode(FlushModeType.COMMIT);
        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    /**
     * Count number of records in a database table
     *
     * @param tableName A name of a table to query
     * @param config    Data filtering, sorting and pagination criteria
     * @return Number of entities.
     */
    public long count(String tableName, PaginationConfiguration config) {
        tableName = addCurrentSchema(tableName);
        QueryBuilder queryBuilder = getQuery(tableName, config, null);
        Query query = queryBuilder.getNativeCountQuery(getEntityManager());
        Object count = query.setFlushMode(FlushModeType.COMMIT).getSingleResult();
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
     * @param values    Values to save
     * @throws BusinessException General exception
     */
    public void createOrUpdate(String tableName, List<Map<String, Object>> values) throws BusinessException {
        tableName = addCurrentSchema(tableName);
        for (Map<String, Object> value : values) {

            // New record
            if (value.get(FIELD_ID) == null) {
                create(tableName, value, false, true);

                // Existing record
            } else {
                update(tableName, value, true);
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
        
        // Original comment: RE #5414 SQL request to create a new CT on second tenant issue
        //return entityManagerProvider.getEntityManager().getEntityManager();
    }

    /**
     * Convert value of unknown data type to a target data type. A value of type list is considered as already converted value, as would come only from WS.
     *
     * @param value        Value to convert
     * @param targetClass  Target data type class to convert to
     * @param expectedList Is return value expected to be a list. If value is not a list and is a string a value will be parsed as comma separated string and each value will be
     *                     converted accordingly. If a single value is passed, it will be added to a list.
     * @param datePatterns Optional. Date patterns to apply to a date type field. Conversion is attempted in that order until a valid date is matched.If no values are provided, a
     *                     standard date and time and then date only patterns will be applied.
     * @param cft
     * @param regExp
     * @return A converted data type
     * @throws ValidationException Value can not be cast to a target class
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Object castValue(Object value, Class targetClass, boolean expectedList, String[] datePatterns, CustomFieldTemplate cft) throws ValidationException {

        // log.debug("Casting {} of class {} target class {} expected list {} is array {}", value, value != null ? value.getClass() : null, targetClass, expectedList,
        // value != null ? value.getClass().isArray() : null);

        // Nothing to cast - same data type
        if (targetClass.isAssignableFrom(value.getClass()) && !expectedList) {
            return extractString(value, targetClass, cft);
            // A list is expected as value. If value is not a list, parse value as comma separated string and convert each value separately
        } else if (expectedList) {
            if (value instanceof List || value instanceof Set || value.getClass().isArray()) {
                return value;

                // Parse comma separated string
            } else if (value instanceof String) {
                List valuesConverted = new ArrayList<>();
                String[] valueItems = ((String) value).split(",");
                for (String valueItem : valueItems) {
                    Object valueConverted = castValue(valueItem, targetClass, false, datePatterns, cft);
                    if (valueConverted != null) {
                        valuesConverted.add(valueConverted);
                    } else {
                        throw new ValidationException("Filter value " + value + " does not match " + targetClass.getSimpleName());
                    }
                }
                return valuesConverted;

                // A single value list
            } else {
                Object valueConverted = castValue(value, targetClass, false, datePatterns, cft);
                if (valueConverted != null) {
                    return Arrays.asList(valueConverted);
                } else {
                    throw new ValidationException("Filter value " + value + " does not match " + targetClass.getSimpleName());
                }
            }
        }

        Number numberVal = null;
        BigDecimal bdVal = null;
        String stringVal = null;
        Boolean booleanVal = null;
        Date dateVal = null;
        List listVal = null;

        if (value instanceof Number) {
            numberVal = (Number) value;
        } else if (value instanceof BigDecimal) {
            bdVal = (BigDecimal) value;
        } else if (value instanceof Boolean) {
            booleanVal = (Boolean) value;
        } else if (value instanceof java.sql.Timestamp) {
            dateVal = new Date(((java.sql.Timestamp) value).getTime());
        } else if (value instanceof Date) {
            dateVal = (Date) value;
        } else if (value instanceof String) {
            stringVal = (String) value;
        } else if (value instanceof List) {
            listVal = (List) value;
        } else {
            throw new ValidationException("Unrecognized data type for value " + value + " type " + value.getClass());
        }

        try {
            if (targetClass == String.class) {
                return extractString(value, targetClass, cft);
            } else if (targetClass == EntityReferenceWrapper.class) {
                long id = Long.parseLong(value.toString());
                boolean exist = validateRecordExistance(cft, id);
                if (!exist) {
                    throw new ElementNotFoundException(id, cft.getEntityClazz());
                }
                return id;

            } else if (targetClass == Boolean.class || (targetClass.isPrimitive() && targetClass.getName().equals("boolean"))) {
                if (booleanVal != null) {
                    return value;
                }
            } else if (targetClass == Date.class) {
                if (dateVal != null) {
                    return dateVal;
                } else if (listVal != null) {
                    return listVal;
                } else if (numberVal != null) {
                    return new Date(numberVal.longValue());
                } else if (stringVal != null && !stringVal.isEmpty()) {

                    // Use provided date patterns or try default patterns if they were not provided
                    if (datePatterns != null) {
                        for (String datePattern : datePatterns) {
                            Date date = DateUtils.parseDateWithPattern(stringVal, datePattern);
                            if (date != null) {
                                return date;
                            }
                        }
                    } else {

                        // first try with date and time and then only with date format
                        Date date = DateUtils.parseDateWithPattern(stringVal, DateUtils.DATE_TIME_PATTERN);
                        if (date == null) {
                            date = DateUtils.parseDateWithPattern(stringVal, paramBean.getDateTimeFormat());
                        }
                        if (date == null) {
                            date = DateUtils.parseDateWithPattern(stringVal, DateUtils.DATE_PATTERN);
                        }
                        if (date == null) {
                            date = DateUtils.parseDateWithPattern(stringVal, paramBean.getDateFormat());
                        }
                    }
                }

            } else if (targetClass.isEnum()) {
                if (listVal != null || targetClass.isAssignableFrom(value.getClass())) {
                    return value;
                } else if (stringVal != null) {
                    Enum enumVal = ReflectionUtils.getEnumFromString((Class<? extends Enum>) targetClass, stringVal);
                    if (enumVal != null) {
                        return enumVal;
                    }
                }

            } else if (targetClass == Integer.class || (targetClass.isPrimitive() && targetClass.getName().equals("int"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Integer.parseInt(stringVal);
                }

            } else if (targetClass == Long.class || (targetClass.isPrimitive() && targetClass.getName().equals("long"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Long.parseLong(stringVal);
                }

            } else if (targetClass == Byte.class || (targetClass.isPrimitive() && targetClass.getName().equals("byte"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Byte.parseByte(stringVal);
                }

            } else if (targetClass == Short.class || (targetClass.isPrimitive() && targetClass.getName().equals("short"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Short.parseShort(stringVal);
                }

            } else if (targetClass == Double.class || (targetClass.isPrimitive() && targetClass.getName().equals("double"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Double.parseDouble(stringVal);
                }

            } else if (targetClass == Float.class || (targetClass.isPrimitive() && targetClass.getName().equals("float"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Float.parseFloat(stringVal);
                }

            } else if (targetClass == BigDecimal.class) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return new BigDecimal(stringVal);
                }

            }

        } catch (NumberFormatException e) {
            throw new ValidationException("wrong value format for filter, cannot cast '" + value + "' to " + targetClass, e);
        }
        throw new ValidationException("Failed to cast value [" + value + "] to class: " + targetClass.getSimpleName());
    }

    private Object extractString(Object value, Class targetClass, CustomFieldTemplate cft) {
        if (targetClass == String.class) {
            if (cft.getRegExp() != null) {
                final Pattern pattern = Pattern.compile(cft.getRegExp());
                if (!pattern.matcher((String) value).matches()) {
                    throw new ValidationException("value of String " + value + " not accepted for regexp" + pattern.toString());
                }
            }
            if (CustomFieldTypeEnum.LIST.equals(cft.getFieldType())) {
                Map<String, String> listValues = cft.getListValuesSorted();
                if (!listValues.containsKey(value)) {
                    throw new ValidationException("value " + value + " is not accepted as value for enum " + cft.getCode());
                }
            }
        } else if (value instanceof java.sql.Timestamp) {
            value = new Date(((java.sql.Timestamp) value).getTime());
        }
        return value;
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Object> findByClassAndId(String className, Long id) {
        try {
            Class clazz = Class.forName(className);
            String tableName = getTableNameForClass(clazz);
            return findById(tableName, id);
        } catch (ClassNotFoundException e) {
            throw new BusinessException("Exception when trying to get class with name: " + className);
        }
    }

    public String getTableNameForClass(Class entityClass) {
        SessionFactory sessionFactory = ((Session) getEntityManager().getDelegate()).getSessionFactory();
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityClass);
        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
        AbstractEntityPersister entityPersister = (AbstractEntityPersister) sessionFactoryImpl.getEntityPersister(classMetadata.getEntityName());
        return entityPersister.getTableName();
    }

    public boolean validateRecordExistance(CustomFieldTemplate field, Long id) {
        String tableName = null;

        CustomEntityTemplate relatedEntity = customEntityTemplateService.findByCode(field.tableName());
        try {
            if (relatedEntity != null) {
                if (relatedEntity.isStoreAsTable()) {
                    tableName = relatedEntity.getDbTablename();
                } else {
                    tableName = getTableNameForClass(CustomEntityInstance.class);
                }
            } else {
                tableName = getTableNameForClass(Class.forName(field.getEntityClazz()));
            }
        } catch (ClassNotFoundException e) {
            throw new BusinessException("Exception when trying to get class with name: " + field.getEntityClazz());
        }

        return validateRecordExistanceByTableName(tableName, id);
    }

    public boolean validateRecordExistanceByTableName(String tableName, Long id) {
        tableName = addCurrentSchema(tableName);
        Session session = getEntityManager().unwrap(Session.class);
        StringBuilder selectQuery = new StringBuilder("select ").append(FIELD_ID).append(" from ").append(tableName).append(" e where ").append(FIELD_ID).append("=:id");
        SQLQuery query = session.createSQLQuery(selectQuery.toString());
        query.setParameter("id", id);
        return query.setFlushMode(FlushMode.COMMIT).uniqueResult() != null;
    }

    @SuppressWarnings("unchecked")
    public List<BigInteger> filterExistingRecordsOnTable(String tableName, List<Long> ids) {
        tableName = addCurrentSchema(tableName);
        Session session = getEntityManager().unwrap(Session.class);
        StringBuilder selectQuery = new StringBuilder("select ").append(FIELD_ID).append(" from ").append(tableName).append(" e where ").append(FIELD_ID).append(" in (:ids)");
        SQLQuery query = session.createSQLQuery(selectQuery.toString());
        query.setParameterList("ids", ids);
        return (List<BigInteger>) query.setFlushMode(FlushMode.COMMIT).list();
    }

    /**
     * Add a DB schema name to the table if not using a main provider
     * 
     * @param tableName DB table name
     * @return DB Table name prefixed with a schema name
     */
    public String addCurrentSchema(String tableName) {
        String currentproviderCode = currentUser.getProviderCode();
        return addCurrentSchema(tableName, currentproviderCode);
    }

    /**
     * Add a DB schema name to the table if not using a main provider
     * 
     * @param tableName DB table name
     * @param providerCode Provider code
     * @return DB Table name prefixed with a schema name
     */
    public static String addCurrentSchema(String tableName, String providerCode) {

        if (providerCode != null && tableName != null) {
            String schema = EntityManagerProvider.convertToSchemaName(providerCode) + ".";
            if (!tableName.contains(schema)) {
                return schema + tableName;
            }
        }
        return tableName;
    }

    /**
     * get next value from sequence using all_sequences_view
     *
     * @param customTableName the table name
     * @return a long
     */
    private Long getNextValueFromSequence(String customTableName) {
        try {
            customTableName = addCurrentSchema(customTableName);
            final String sqlString = "select seq_val from all_sequences_view where lower(SEQ_CODE) =:code";
            Object nextVal = getEntityManager().createNativeQuery(sqlString).setParameter("code", customTableName + "_seq").getSingleResult();
            return Long.parseLong((String) nextVal);
        } catch (Exception e) {
            throw new BusinessException("cannot get next value from sequence of table : " + customTableName + "_seq", e);
        }
    }

    public QueryBuilder generatedAdvancedQuery(ReportQuery reportQuery) {

        Class<?> entityClass = GenericHelper.getEntityClass(reportQuery.getTargetEntity());
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
        GenericPagingAndFiltering genericPagingAndFilter = buildGenericPagingAndFiltering(reportQuery);
        PaginationConfiguration searchConfig = genericRequestMapper.mapTo(genericPagingAndFilter);
        QueryBuilder qb;
        List<String> genericFields = (List<String>) reportQuery.getAdvancedQuery().getOrDefault("fields", new ArrayList<>());
        if (isAggregationQueries(genericPagingAndFilter.getGenericFields())) {
            searchConfig.setFetchFields(genericFields);
            qb = this.getAggregateQuery(entityClass.getCanonicalName(), searchConfig, null);
        } else if (isCustomFieldQuery(genericPagingAndFilter.getGenericFields())) {
            searchConfig.setFetchFields(genericFields);
            qb = this.getQuery(entityClass.getCanonicalName(), searchConfig, null);
        } else {
            qb = PersistenceServiceHelper.getPersistenceService(entityClass, searchConfig).listQueryBuilder(searchConfig);
            String fieldsToRetrieve = !genericFields.isEmpty() ? retrieveFields(genericFields, null) : "";
            if (!fieldsToRetrieve.isBlank()) {
                qb.setQ(new StringBuilder("select " + buildFields(fieldsToRetrieve, "") + " ").append(qb.getSqlStringBuffer()));
            }
        }

        return qb;
    }

    private GenericPagingAndFiltering buildGenericPagingAndFiltering(ReportQuery reportQuery) {
        Builder builder = ImmutableGenericPagingAndFiltering.builder()
                                                            .filters((Map<String, Object>) reportQuery.getAdvancedQuery().getOrDefault("filters", new HashMap<>()))
                                                            .groupBy((List<String>) reportQuery.getAdvancedQuery().getOrDefault("groupBy", new ArrayList<>()))
                                                            .nestedEntities((List<String>) reportQuery.getAdvancedQuery().getOrDefault("nestedEntities", new ArrayList<>()))
                                                            .genericFields((List<String>) reportQuery.getAdvancedQuery().getOrDefault("fields", new ArrayList<>()))
                                                            .having((List<String>) reportQuery.getAdvancedQuery().getOrDefault("having", new ArrayList<>()));
        String sortBy = (String) reportQuery.getAdvancedQuery().get("sortBy");
        if (org.meveo.commons.utils.StringUtils.isNotBlank(sortBy)) {
            builder.sortBy(sortBy);
        }
        String sortOrder = (String) reportQuery.getAdvancedQuery().get("sortOrder");
        if (org.meveo.commons.utils.StringUtils.isNotBlank(sortOrder)) {
            builder.sortOrder(sortOrder);
        }

        return builder.build();

    }

    private boolean isAggregationField(String field) {
        return field.startsWith("SUM(") || field.startsWith("COUNT(") || field.startsWith("AVG(")
                || field.startsWith("MAX(") || field.startsWith("MIN(") || field.startsWith("COALESCE(SUM(");
    }

    private boolean isCustomField(String field) {
        return field.contains("->>");
    }

    public boolean isCustomFieldQuery(Set<String> genericFields) {
        return genericFields.stream()
                .filter(genericField -> isCustomField(genericField))
                .findFirst()
                .isPresent();
    }

    private boolean isAggregationQueries(Set<String> genericFields) {
        return genericFields.stream()
                .filter(genericField -> isAggregationField(genericField))
                .findFirst()
                .isPresent();
    }

    /**
     * Refresh native table field to data type mapping. Used in NativePersistenceService to determine a field data type when caching is enabled.
     * 
     * @param tableName Table name to refresh mapping for. Optional. If not provided, mapping for all tables will be refreshed.
     */
    public void refreshTableFieldMapping(String tableName) {

        Map<String, Map<String, CustomFieldTypeEnum>> fieldMappings = new HashMap<String, Map<String, CustomFieldTypeEnum>>();

        if (tableName == null) {
            List<CustomEntityTemplate> cets = customEntityTemplateService.listNoCache();

            for (CustomEntityTemplate cet : cets) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesToNoCache(cet.getAppliesTo());

                if (cfts != null) {

                    Map<String, CustomFieldTypeEnum> fieldTypeMap = new HashMap<String, CustomFieldTypeEnum>();
                    fieldMappings.put(cet.getDbTablename(), fieldTypeMap);

                    fieldTypeMap.put("id", CustomFieldTypeEnum.LONG);

                    for (CustomFieldTemplate cft : cfts.values()) {
                        fieldTypeMap.put(cft.getDbFieldname(), cft.getFieldType());
                    }
                }
            }
        } else {
            Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesToNoCache(CustomEntityTemplate.getAppliesTo(tableName));

            Map<String, CustomFieldTypeEnum> fieldTypeMap = new HashMap<String, CustomFieldTypeEnum>();
            fieldMappings.put(tableName, fieldTypeMap);

            fieldTypeMap.put("id", CustomFieldTypeEnum.LONG);

            for (CustomFieldTemplate cft : cfts.values()) {
                fieldTypeMap.put(cft.getDbFieldname(), cft.getFieldType());
            }
        }

        fieldDataTypeMappings = fieldMappings;
    }

    /**
     * Check if event of a given type is enabled for a given table
     * 
     * @param tableName Table name to check
     * @param eventType Event type
     * @return True if events of such event type exist for a given table
     */
    protected boolean areEventsEnabled(String tableName, NotificationEventTypeEnum eventType) {
        List<Notification> notifications = genericNotificationService.getApplicableNotifications(NotificationEventTypeEnum.CREATED, new CustomTableEvent(tableName, null, null, eventType));
        return notifications != null && !notifications.isEmpty();
    }

    /**
     * Build the update query with the provided filters
     *
     * @param updateQueryBuilder the update query builder
     * @param entityClassName    the entity class name
     * @param filters            the filters
     */
    public String getUpdateQuery(QueryBuilder updateQueryBuilder, String entityClassName, Map<String, Object> filters) {
        String updateQuery = null;
        if (updateQueryBuilder != null) {
            updateQuery = updateQueryBuilder.getQueryAsString();
            if (filters != null && !filters.isEmpty()) {
                PaginationConfiguration searchConfig = new PaginationConfiguration(filters);
                searchConfig.setFetchFields(Arrays.asList("id"));
                String subQuery = getQuery(entityClassName, searchConfig, null).getQueryAsString();
                if (subQuery.indexOf("join") > -1) {
                    updateQuery = updateQuery + " WHERE id in (" + subQuery + ")";
                } else {
                    updateQuery = updateQuery + subQuery.substring(subQuery.indexOf(" where "));
                    updateQuery = updateQuery.replaceAll(" a\\.", " ");
                    updateQuery = updateQuery.replaceAll("\\(a\\.", "(");
                }
            }
        }
        return updateQuery;
    }

    /**
     * Execute the provided query builder with the provided filters
     *
     * @param updateQueryBuilder the update query builder
     * @param entityClassName    the entity class name
     * @param filters            the filters
     */
    public void update(QueryBuilder updateQueryBuilder, String entityClassName, Map<String, Object> filters) {
        String updateQuery = getUpdateQuery(updateQueryBuilder, entityClassName, filters);
        if (StringUtils.isNotBlank(updateQuery)) {
            getEntityManager().createQuery(updateQuery).executeUpdate();
        }
    }
    /**
     * Execute the provided update query with the provided ids
     *
     * @param updateQuery the update query to be executed
     * @param ids         the ids of records to be updated
     * @return the number of updated records
     */
    public int update(StringBuilder updateQuery, List<Long> ids) {
        AtomicInteger updated = new AtomicInteger(0);
        if (updateQuery != null && updateQuery.length() > 0 && ids != null && ids.size() > 0) {
            final int maxValue = getInstance().getPropertyAsInteger("database.number.of.inlist.limit", SHORT_MAX_VALUE);
            List<List<Long>> listOfSubListIds = partition(ids, maxValue);
            listOfSubListIds.forEach(sublist -> {
                if (sublist != null && !sublist.isEmpty()) {
                    updateQuery.append(" WHERE id in (")
                            .append(sublist.stream().map(String::valueOf).collect(joining(",")))
                            .append(")");
                    updated.addAndGet(getEntityManager().createQuery(updateQuery.toString()).executeUpdate());
                    getEntityManager().flush();
                    getEntityManager().clear();
                }
            });
        }
        return updated.intValue();
    }
}