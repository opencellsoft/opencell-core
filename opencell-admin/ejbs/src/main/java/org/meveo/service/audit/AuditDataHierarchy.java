package org.meveo.service.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.meveo.model.audit.AuditCrudActionEnum;
import org.meveo.model.audit.AuditDataLog;
import org.meveo.model.audit.AuditDataLogRecord;
import org.meveo.model.persistence.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import liquibase.repackaged.org.apache.commons.collections4.map.HashedMap;

public class AuditDataHierarchy implements Serializable {

    private static final long serialVersionUID = -258622566538575345L;

    /**
     * @OneToMany or @ManyToMany relationship field identifying a referenced entity
     */
    public static final String LIST_ENTITY_FIELD_ID = "id";
    /**
     * @OneToMany or @ManyToMany relationship field identifying a parent entity
     */
    public static final String LIST_ENTITY_FIELD_PARENTID = "parentId";

    /**
     * Stores a list of raw record identifiers
     */
    public static final String RAW_RECORD_POS = "rawRecordIds";

    /**
     * Field name in a parent entity
     */
    String fieldName;

    /**
     * Entity class
     */
    @SuppressWarnings("rawtypes")
    Class entityClass;

    /**
     * Table name
     */
    String tableName;

    /**
     * Actions supported - comma separated {@link AuditCrudActionEnum}
     */
    String actions;

    /**
     * Mapping of db column to field names
     */
    Map<String, String> dbColumnToFieldMap = new HashMap<String, String>();

    /**
     * Auditable related entities/fields
     */
    List<AuditDataHierarchy> relatedEntities = new ArrayList<AuditDataHierarchy>();

    /**
     * A field name corresponding to a FK to a parent in case of a related entity
     */
    String parentIdField;

    /**
     * A DB column name corresponding to a FK to a parent in case of a related entity
     */
    String parentIdDbColumn;

    /**
     * Constructor
     */
    public AuditDataHierarchy() {

    }

    /**
     * Constructor
     * 
     * @param entityClass Entity class
     * @param tableName Table name
     */
    @SuppressWarnings("rawtypes")
    public AuditDataHierarchy(Class entityClass, String tableName) {
        this.entityClass = entityClass;
        this.tableName = tableName;
    }

    /**
     * Constructor
     * 
     * @param fieldName Field name
     * @param entityClass Entity class
     * @param tableName Table name
     * @param actions Actions supported
     */
    @SuppressWarnings("rawtypes")
    public AuditDataHierarchy(String fieldName, Class entityClass, String tableName, String actions) {
        this.fieldName = fieldName;
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.actions = actions;
    }

    /**
     * @return Field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName Field name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return Entity class
     */
    @SuppressWarnings("rawtypes")
    public Class getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass Entity class
     */
    @SuppressWarnings("rawtypes")
    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return Table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName Table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return Actions supported
     */
    public String getActions() {
        return actions;
    }

    /**
     * @param actions Actions supported
     */
    public void setActions(String actions) {
        this.actions = actions;
    }

    /**
     * @return Mapping of db column to field names
     */
    public Map<String, String> getDbColumnToFieldMap() {
        return dbColumnToFieldMap;
    }

    /**
     * @param dbColumnToFieldMap Mapping of db column to field names
     */
    public void setDbColumnToFieldMap(Map<String, String> dbColumnToFieldMap) {
        this.dbColumnToFieldMap = dbColumnToFieldMap;
    }

    /**
     * @return Auditable related entities/fields
     */
    public List<AuditDataHierarchy> getRelatedEntities() {
        return relatedEntities;
    }

    /**
     * @param relatedEntities Auditable related entities/fields
     */
    public void setRelatedEntities(List<AuditDataHierarchy> relatedEntities) {
        this.relatedEntities = relatedEntities;
    }

    /**
     * @return A field name corresponding to a FK to a parent entity in case of a related entity
     */
    public String getParentIdField() {
        return parentIdField;
    }

    /**
     * @param parentIdField A field name corresponding to a FK to a parent in case of a related entity
     */
    public void setParentIdField(String parentIdField) {
        this.parentIdField = parentIdField;
    }

    /**
     * 
     * @return A DB column name corresponding to a FK to a parent in case of a related entity
     */
    public String getParentIdDbColumn() {
        return parentIdDbColumn;
    }

    /**
     * @param parentIdDbColumn A DB column name corresponding to a FK to a parent in case of a related entity
     */
    public void setParentIdDbColumn(String parentIdDbColumn) {
        this.parentIdDbColumn = parentIdDbColumn;
    }

    /**
     * Is crud action enabled
     * 
     * @param crudAction Crud action
     * @return True if action is null or given action is matched
     */
    public boolean isActionEnabled(AuditCrudActionEnum crudAction) {
        return actions == null || actions.contains(crudAction.name());
    }

    @Override
    public String toString() {
        return "AuditDataHierarchy [fieldName=" + fieldName + ", entityClass=" + entityClass + ", tableName=" + tableName + ", actions=" + actions + "]";
    }

    /**
     * Get all tables referenced in the entity hierarchy
     * 
     * @return A list of table names
     */
    public List<String> getAllTables() {
        List<String> allTables = new ArrayList<String>();

        allTables.add(tableName);
        for (AuditDataHierarchy field : relatedEntities) {
            allTables.addAll(field.getAllTables());
        }

        return allTables;
    }

    /**
     * Get a hierarchy path for logging audit data
     * 
     * @param auditDataLogRaw Audit data log record for which to find the path
     * @return A sequential list of audit data hierarchies
     */
    public LinkedList<AuditDataHierarchy> getPath(AuditDataLogRecord auditDataLogRaw) {
        LinkedList<AuditDataHierarchy> path = null;

        for (AuditDataHierarchy relatedEntityDefinition : this.getRelatedEntities()) {
            if (relatedEntityDefinition.getTableName().equalsIgnoreCase(auditDataLogRaw.getReferenceTable())) {
                path = new LinkedList<AuditDataHierarchy>();
                path.add(relatedEntityDefinition);
                break;

            } else {
                path = relatedEntityDefinition.getPath(auditDataLogRaw);
                if (path != null) {
//                    path.addFirst(relatedEntityDefinition);
                    break;
                }
            }
        }

        if (path != null) {
            path.addFirst(this);
        }

        return path;
    }

    /**
     * Convert json string of key=db column name into a map of values with key being entity field name. Null values convert to "null".
     * 
     * @param data Json string
     * @return A map of values
     */
    public Map<String, Object> convertChangedValues(String data) {
        if (data == null) {
            return new HashedMap<String, Object>();
        }
        Map<String, Object> changedValues = JacksonUtil.fromString(data, new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> changedValuesConverted = new HashedMap<String, Object>();
        for (Entry<String, Object> entrySet : changedValues.entrySet()) {
            String fieldName = this.dbColumnToFieldMap.get(entrySet.getKey());
            if (fieldName != null) {
                changedValuesConverted.put(fieldName, entrySet.getValue() == null ? "null" : entrySet.getValue());
            } else {
                Logger log = LoggerFactory.getLogger(getClass());
                log.warn("No field definition matched for db field {} in table {}", entrySet.getKey(), tableName);
            }
        }

        return changedValuesConverted;
    }

    /**
     * Recursively inspect a map to match key. Go deeper if map value is of a map type. A special adaptation of ListUtils.matchMapKeyRecursively that handles AuditDataLog.changedValues field.
     * 
     * @param mapToInspect Map to inspect
     * @param keyToMatch A key to match
     * @param fullMatch Shall a full or partial (starts with) key match should be performed. True for a full match.
     * @param isValuesChangedField In case of introspection of a value of AuditDataLog type should valuesChanged (true) of valuesOld (false) field be consulted.
     * @return A matched value
     */
    public static Object matchMapKeyRecursively(Map<String, ?> mapToInspect, String keyToMatch, boolean fullMatch, boolean isValuesChangedField) {

        if (fullMatch && mapToInspect.containsKey(keyToMatch)) {
            return mapToInspect.get(keyToMatch);

        } else {
            for (Entry<String, ?> entry : mapToInspect.entrySet()) {
                if (!fullMatch && entry.getKey().startsWith(keyToMatch)) {
                    return entry.getValue();
                } else if (entry.getValue() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Object matched = matchMapKeyRecursively((Map<String, ?>) entry.getValue(), keyToMatch, fullMatch, isValuesChangedField);
                    if (matched != null) {
                        return matched;
                    }
                } else if (entry.getValue() instanceof AuditDataLog && isValuesChangedField) {
                    Object matched = matchMapKeyRecursively(((AuditDataLog) entry.getValue()).getValuesChanged(), keyToMatch, fullMatch, isValuesChangedField);
                    if (matched != null) {
                        return matched;
                    }
                } else if (entry.getValue() instanceof AuditDataLog && !isValuesChangedField) {
                    Object matched = matchMapKeyRecursively(((AuditDataLog) entry.getValue()).getValuesOld(), keyToMatch, fullMatch, isValuesChangedField);
                    if (matched != null) {
                        return matched;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Recursively inspect a map to match key. Go deeper if map value is of a map type. A special adaptation of ListUtils.matchMapKeyRecursively that handles AuditDataLog.changedValues field.
     * 
     * @param mapToInspect Map to inspect
     * @param keyToMatch A key to match
     * @param fullMatch Shall a full or partial (starts with) key match should be performed. True for a full match.
     * @param isValuesChangedField In case of introspection of a value of AuditDataLog type should valuesChanged (true) of valuesOld (false) field be consulted.
     * @return A matched value
     */
    public static List<Map.Entry<String, ?>> matchMapKeyRecursivelyMultipleTimes(Map<String, ?> mapToInspect, String keyToMatch, boolean fullMatch, boolean isValuesChangedField) {

        List<Map.Entry<String, ?>> matches = new ArrayList<>();

        for (Entry<String, ?> entry : mapToInspect.entrySet()) {
            if ((fullMatch && entry.getKey().equals(keyToMatch)) || (!fullMatch && entry.getKey().startsWith(keyToMatch))) {
                matches.add(entry);

            } else if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                List<Map.Entry<String, ?>> matched = matchMapKeyRecursivelyMultipleTimes((Map<String, ?>) entry.getValue(), keyToMatch, fullMatch, isValuesChangedField);
                if (matched != null) {
                    matches.addAll(matched);
                }
            } else if (entry.getValue() instanceof AuditDataLog && isValuesChangedField) {
                List<Map.Entry<String, ?>> matched = matchMapKeyRecursivelyMultipleTimes(((AuditDataLog) entry.getValue()).getValuesChanged(), keyToMatch, fullMatch, isValuesChangedField);
                if (matched != null) {
                    matches.addAll(matched);
                }
            } else if (entry.getValue() instanceof AuditDataLog && !isValuesChangedField) {
                List<Map.Entry<String, ?>> matched = matchMapKeyRecursivelyMultipleTimes(((AuditDataLog) entry.getValue()).getValuesOld(), keyToMatch, fullMatch, isValuesChangedField);
                if (matched != null) {
                    matches.addAll(matched);
                }
            }
        }
        return matches;
    }

    /**
     * Recursively inspect a map to match key. Go deeper if map value is of a map type. A special adaptation of ListUtils.matchMapKeyRecursively that handles AuditDataLog.changedValues field.
     * 
     * @param mapToInspect Map to inspect
     * @param keyToMatch A key to match
     * @param fullMatch Shall a full or partial (starts with) key match should be performed. True for a full match.
     * @return A removed value or null if nothing was removed
     */
    public static Object removeMapKeyRecursively(Map<String, ?> mapToInspect, String keyToMatch, boolean fullMatch) {

        if (fullMatch && mapToInspect.containsKey(keyToMatch)) {
            return mapToInspect.remove(keyToMatch);

        } else {
            for (Entry<String, ?> entry : mapToInspect.entrySet()) {
                if (!fullMatch && entry.getKey().startsWith(keyToMatch)) {
                    return mapToInspect.remove(entry.getKey());
                } else if (entry.getValue() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Object matched = removeMapKeyRecursively((Map<String, Object>) entry.getValue(), keyToMatch, fullMatch);
                    if (matched != null) {
                        return matched;
                    }
                } else if (entry.getValue() instanceof AuditDataLog) {
                    Object matched = removeMapKeyRecursively(((AuditDataLog) entry.getValue()).getValuesChanged(), keyToMatch, fullMatch);
                    if (matched != null) {
                        return matched;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Recursively inspect a map to match key as many times as needed. Go deeper if map value is of a map type. A special adaptation of ListUtils.matchMapKeyRecursively that handles AuditDataLog.changedValues field.
     * 
     * @param mapToInspect Map to inspect
     * @param keyToMatch A key to match
     * @param fullMatch Shall a full or partial (starts with) key match should be performed. True for a full match.
     * @return A removed value or null if nothing was removed
     */
    public static List<Object> removeMapKeyRecursivelyMultipleTimes(Map<String, Object> mapToInspect, String keyToMatch, boolean fullMatch) {

        List<Object> matches = new ArrayList<Object>();

        Set<String> keys = new HashSet<String>(mapToInspect.keySet());

        for (String key : keys) {
            Object value = mapToInspect.get(key);
            if ((fullMatch && key.equals(keyToMatch)) || (!fullMatch && key.startsWith(keyToMatch))) {
                matches.add(mapToInspect.remove(key));

            } else if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                List<Object> matched = removeMapKeyRecursivelyMultipleTimes((Map<String, Object>) value, keyToMatch, fullMatch);
                if (!matched.isEmpty()) {
                    matches.addAll(matched);
                }

            } else if (value instanceof AuditDataLog) {
                List<Object> matched = removeMapKeyRecursivelyMultipleTimes(((AuditDataLog) value).getValuesChanged(), keyToMatch, fullMatch);
                if (!matched.isEmpty()) {
                    matches.addAll(matched);
                }
            }
        }
        return matches;
    }

    /**
     * Get a corresponding db column name for a given field name
     * 
     * @param fieldName Entity field name
     * @return A db column name
     */
    public String getDbColumByFieldname(String fieldName) {

        for (Entry<String, String> fieldInfo : dbColumnToFieldMap.entrySet()) {
            if (fieldInfo.getValue().equals(fieldName)) {
                return fieldInfo.getKey();
            }
        }
        return null;
    }

    /**
     * Parse ID value from the Changed values hierarchy map in audit data log
     * 
     * @param key A Changed values hierarchy map key in audit data log. Possible format - EntityClass_ID_action or EntityClass_ID
     * @return
     */
    public static Long parseParentEntityId(String key) {

        String value = key.substring(key.lastIndexOf('_') + 1);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            value = key.substring(0, key.lastIndexOf('_'));
            value = value.substring(value.lastIndexOf('_') + 1);
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e2) {
                // Dont care - was not able to determine ID value
                return null;
            }
        }
    }
}