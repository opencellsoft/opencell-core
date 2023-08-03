package org.meveo.service.audit.logging;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ListUtils;
import org.meveo.model.AuditableField;
import org.meveo.model.audit.AuditCrudActionEnum;
import org.meveo.model.audit.AuditDataLog;
import org.meveo.model.audit.AuditDataLogRecord;
import org.meveo.service.audit.AuditDataConfigurationService;
import org.meveo.service.audit.AuditDataHierarchy;
import org.meveo.service.audit.AuditFieldInfo;
import org.meveo.service.audit.AuditableFieldConfiguration;
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.base.PersistenceService;

/**
 * A CRUD service class for AuditDataLog entity
 * 
 * @author explo
 *
 */
@Stateless
public class AuditDataLogService extends PersistenceService<AuditDataLog> {

    /**
     * A name of a search criteria to search for a presence of a field in audit log "valuesChanged" field.
     */
    public static final String SEARCH_CRITERIA_FIELD = "field";

    /**
     * A name of a search criteria to search by entity class.
     */
    public static final String SEARCH_CRITERIA_ENTITY_CLASS = "entityClass";

    /**
     * A name of a search criteria to search by entity id.
     */
    public static final String SEARCH_CRITERIA_ENTITY_ID = "entityId";

    @Inject
    private AuditDataConfigurationService auditDataConfigurationService;

    @Inject
    private AuditableFieldService auditableFieldService;

    @Inject
    private Event<AuditableField> auditFieldEventProducer;

    @Override
    public List<AuditDataLog> list(PaginationConfiguration config) {

        validateAndConvertSearchCriteria(config);

        return super.list(config);
    }

    @Override
    public long count(PaginationConfiguration config) {

        validateAndConvertSearchCriteria(config);

        return super.count(config);
    }

    /**
     * Validate and convert "entityClass" and "field" fields. "EntityClass" - a valid, full class name. "Field" - add a wildcardOr prefix if missing.
     * 
     * @param config
     */
    private void validateAndConvertSearchCriteria(PaginationConfiguration config) {

        Map<String, Object> filters = config.getFilters();

        // Validate that entity class is a valid class and convert a simple name into a full class name
        if (filters.containsKey(SEARCH_CRITERIA_ENTITY_CLASS)) {
            String className = (String) filters.get(SEARCH_CRITERIA_ENTITY_CLASS);

            try {
                @SuppressWarnings("rawtypes")
                Class clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    clazz = AuditDataConfigurationService.getClassFromShortName(className);
                }

                filters.put(SEARCH_CRITERIA_ENTITY_CLASS, clazz.getName());

            } catch (ClassNotFoundException e) {
                throw new BusinessException(e);
            }
        }

        if (filters.containsKey(SEARCH_CRITERIA_FIELD) && !((String) filters.get(SEARCH_CRITERIA_FIELD)).contains(PersistenceService.SEARCH_WILDCARD_OR)) {
            filters.put(PersistenceService.SEARCH_WILDCARD_OR + " valuesChanged ", filters.get(SEARCH_CRITERIA_FIELD));
            filters.remove(SEARCH_CRITERIA_FIELD);
        }
    }

    /**
     * Aggregate multiple table base audit data log records into an entity based log entry and persist them. Aggregated log records will be deleted.
     * 
     * @param auditDataLogRecords A list of audit data log records
     */
    public void aggregateAuditLogs(List<AuditDataLogRecord> auditDataLogRecords) {

        List<Long> idsToRemove = new ArrayList<Long>();

        Map<String, List<AuditFieldInfo>> auditableFieldsInfo = AuditableFieldConfiguration.getAuditableEntities();

        List<AuditDataHierarchy> dataHierarchies = auditDataConfigurationService.getAuditDataHierarchies();
        for (AuditDataHierarchy dataHierarchy : dataHierarchies) {

            // Stop trying different data hierarchies when all records are processed
            if (auditDataLogRecords.isEmpty()) {
                break;
            }
            List<AuditDataLog> auditDataLogs = aggregateAuditLogs(dataHierarchy, auditDataLogRecords);
            for (AuditDataLog auditDataLog : auditDataLogs) {
                idsToRemove.addAll(auditDataLog.getAuditDataLogRecords());

                // Store audit data log
                // Don't store audit log if it does not contain any changed values.
                // A scenario is when List update happens and same entities are marked as deleted and then inserted, so both actions cancel each other.

                if (!auditDataLog.getValuesChanged().isEmpty()) {
                    create(auditDataLog);
                }
            }
            // Process auditable fields
            createAndNotifyAuditableFieldLogs(auditDataLogs, auditableFieldsInfo);
        }

        // Delete records that were already aggregated
        if (!idsToRemove.isEmpty()) {
            getEntityManager().createNamedQuery("AuditDataLogRecord.deleteAuditDataLogRecords").setParameter("ids", idsToRemove).executeUpdate();
        }
    }

    /**
     * Create Audit Field logs and fire events if required
     * 
     * @param auditDataLogs Audit data logs
     * @param auditableFieldsInfo Information about field to audit
     */
    private void createAndNotifyAuditableFieldLogs(List<AuditDataLog> auditDataLogs, Map<String, List<AuditFieldInfo>> auditableFieldsInfo) {

        for (AuditDataLog auditDataLog : auditDataLogs) {

            List<AuditFieldInfo> auditableFieldsForClass = auditableFieldsInfo.get(auditDataLog.getEntityClass());

            // Process auditable fields
            if (auditDataLog.getAction() == AuditCrudActionEnum.DELETE || auditableFieldsForClass == null || auditDataLog.getValuesChanged().isEmpty()) {
                continue;
            }

            for (AuditFieldInfo fieldInfo : auditableFieldsForClass) {

                List<Entry<String, ?>> matchesNew = AuditDataHierarchy.matchMapKeyRecursivelyMultipleTimes(auditDataLog.getValuesChanged(), fieldInfo.getFieldName(), !fieldInfo.isCompositeField(), true);

                // No changes found for a given field
                if (matchesNew.isEmpty()) {
                    continue;

                }

                String valueTo = null;
                String valueFrom = null;

                // A single value field
                if (matchesNew.size() == 1) {

                    if (matchesNew.get(0).getValue() != null) {
                        valueTo = matchesNew.get(0).getValue().toString();
                    }
                    if (auditDataLog.getValuesOld() != null) {
                        Object valueOld = ListUtils.matchMapKeyRecursively(auditDataLog.getValuesOld(), matchesNew.get(0).getKey(), true);
                        if (valueOld != null) {
                            valueFrom = valueOld.toString();
                        }
                    }
                    // A composite value/embedded type field
                } else if (matchesNew.size() > 1) {
                    for (Entry<String, ?> matchedNewEntry : matchesNew) {
                        valueTo = (valueTo == null ? "{" : valueTo + ",") + "\"" + matchedNewEntry.getKey() + "\":" + (matchedNewEntry.getValue() == null ? "null" : "\"" + matchedNewEntry.getValue() + "\"");
                        if (auditDataLog.getValuesOld() != null) {
                            Object valueOld = ListUtils.matchMapKeyRecursively(auditDataLog.getValuesOld(), matchedNewEntry.getKey(), true);
                            valueFrom = (valueFrom == null ? "{" : valueFrom + ",") + "\"" + matchedNewEntry.getKey() + "\":" + (valueOld == null ? "null" : "\"" + valueOld + "\"");
                        }
                    }
                    if (valueTo != null) {
                        valueTo = valueTo + "}";
                    }
                    if (valueFrom != null) {
                        valueFrom = valueFrom + "}";
                    }
                }

                AuditableField auditableField = new AuditableField();
                auditableField.setActor(auditDataLog.getUserName());
                auditableField.setCreated(auditDataLog.getCreated());
                auditableField.setCurrentState(valueTo);
                auditableField.setPreviousState(valueFrom);
                auditableField.setEntityClass(auditDataLog.getEntityClass());
                auditableField.setEntityId(auditDataLog.getEntityId());
                auditableField.setChangeOrigin(auditDataLog.getOrigin());
                auditableField.setOriginName(auditDataLog.getOriginName());
                auditableField.setName(fieldInfo.getFieldName());
                auditableField.setChangeType(fieldInfo.getChangeType());

                if (fieldInfo.isPreserveHistory()) {
                    auditableFieldService.create(auditableField);
                }
                // Fire a notification about field change
                if (fieldInfo.isNotify()) {
                    auditFieldEventProducer.fire(auditableField);
                }

            }
        }
    }

    /**
     * Aggregate multiple raw audit log records into a hierarchical structure based on same transaction ID and same entity
     * 
     * @param dataHierarchy Data hierarchy to use in aggregation
     * @param auditDataLogRecords Raw audit log records. NOTE: List is modified - processed items are removed.
     * @return A list of aggregated multiple raw audit log records into a hierarchical structure based on same transaction ID
     */
    @SuppressWarnings("unchecked")
    public List<AuditDataLog> aggregateAuditLogs(AuditDataHierarchy dataHierarchy, List<AuditDataLogRecord> auditDataLogRecords) {

        Map<Long, Map<String, AuditDataLog>> auditDataLogsByTx = new LinkedHashMap<Long, Map<String, AuditDataLog>>();

        for (int i = 0; i < auditDataLogRecords.size(); i++) {

            AuditDataLogRecord auditDataLogRaw = auditDataLogRecords.get(i);

            Map<String, AuditDataLog> txAuditLogs = auditDataLogsByTx.get(auditDataLogRaw.getTxId());
            if (txAuditLogs == null) {
                txAuditLogs = new LinkedHashMap<String, AuditDataLog>();

                auditDataLogsByTx.put(auditDataLogRaw.getTxId(), txAuditLogs);
            }

            // A primary entity table
            if (dataHierarchy.getTableName().equalsIgnoreCase(auditDataLogRaw.getReferenceTable())) {

                AuditDataLog auditDataLog = new AuditDataLog();
                auditDataLog.setValuesChanged(new LinkedHashMap<String, Object>());

                auditDataLog.setAction(auditDataLogRaw.getAction());
                auditDataLog.setTxId(auditDataLogRaw.getTxId());
                auditDataLog.setCreated(auditDataLogRaw.getCreated());
                auditDataLog.setOrigin(auditDataLogRaw.getOrigin());
                auditDataLog.setOriginName(auditDataLogRaw.getOriginName());
                auditDataLog.setUserName(auditDataLogRaw.getUserName());
                auditDataLog.setEntityClassAsClass(dataHierarchy.getEntityClass());
                auditDataLog.setEntityId(auditDataLogRaw.getReferenceId());

                if (auditDataLogRaw.getDataChanges() != null) {
                    auditDataLog.getValuesChanged().putAll(dataHierarchy.convertChangedValues(auditDataLogRaw.getDataChanges()));
                }

                if (auditDataLogRaw.getDataOld() != null && !auditDataLogRaw.getDataOld().isEmpty()) {
                    auditDataLog.getValuesOldNullSafe().putAll(dataHierarchy.convertChangedValues(auditDataLogRaw.getDataOld()));
                }

                String rootEntityKey = auditDataLog.getEntityClass() + "_" + auditDataLog.getEntityId();

                // Handle a case when related tables were processed before the main table
                if (txAuditLogs.containsKey(rootEntityKey)) {
                    auditDataLog.getValuesChanged().putAll(((AuditDataLog) txAuditLogs.get(rootEntityKey)).getValuesChanged());
                    if (((AuditDataLog) txAuditLogs.get(rootEntityKey)).getValuesOld() != null) {
                        auditDataLog.getValuesOldNullSafe().putAll(((AuditDataLog) txAuditLogs.get(rootEntityKey)).getValuesOld());
                    }
                }

                txAuditLogs.put(rootEntityKey, auditDataLog);

                // Track what was processed
                List<Integer> recordsProcessed = (List<Integer>) auditDataLog.getValuesChanged().get(AuditDataHierarchy.RAW_RECORD_POS);
                if (recordsProcessed == null) {
                    recordsProcessed = new ArrayList<Integer>();
                    auditDataLog.getValuesChanged().put(AuditDataHierarchy.RAW_RECORD_POS, recordsProcessed);
                }
                recordsProcessed.add(i);

                // A related entity table
            } else {
                LinkedList<AuditDataHierarchy> hierarchyPath = dataHierarchy.getPath(auditDataLogRaw);
                if (hierarchyPath == null) {
                    continue;
                }

                AuditDataHierarchy lastHierarchyStep = hierarchyPath.getLast();
                Map<String, Object> changedValues = lastHierarchyStep.convertChangedValues(auditDataLogRaw.getDataChanges());

                AuditDataHierarchy precedingHierarchy = hierarchyPath.get(hierarchyPath.size() - 2);
                String precedingFieldPath = (precedingHierarchy.getFieldName() != null ? precedingHierarchy.getFieldName() : precedingHierarchy.getEntityClass().getName()) + "_"
                        + changedValues.get(lastHierarchyStep.getParentIdField());

                String fieldPathBase = lastHierarchyStep.getFieldName() + (auditDataLogRaw.getReferenceId() != null ? "_" + auditDataLogRaw.getReferenceId() : "");
                String fieldPath = fieldPathBase + "_" + auditDataLogRaw.getAction().name();
                String fieldPathDelete = fieldPathBase + "_" + AuditCrudActionEnum.DELETE;

                // Try to find a parent entity to add changes to
                Object match = AuditDataHierarchy.matchMapKeyRecursively(txAuditLogs, precedingFieldPath, false, true);
                Map<String, Object> valuesChangedToAddTo = null;
                if (match != null) {
                    // A changed values map was matched to
                    if (match instanceof Map) {
                        valuesChangedToAddTo = (Map<String, Object>) match;

                        // A parent entity audit data log was matched to
                    } else {
                        valuesChangedToAddTo = ((AuditDataLog) match).getValuesChanged();
                    }

                    // No audit data log record for a parent entity was processed yet, so add it as a AuditDataLog with changed values
                } else {
                    valuesChangedToAddTo = new LinkedHashMap<String, Object>();
                    AuditDataLog precedingAuditDataLog = new AuditDataLog();
                    precedingAuditDataLog.setValuesChanged(valuesChangedToAddTo);
                    txAuditLogs.put(precedingFieldPath, precedingAuditDataLog);

                    // This is applicable only for a hierarchy of at least three levels
                    if (hierarchyPath.size() > 2) {
                        precedingAuditDataLog.setPrecedingDataHierarchy(hierarchyPath.subList(0, hierarchyPath.size() - 1));
                    }
                }

                // Track what was processed
                List<Integer> recordsProcessed = (List<Integer>) valuesChangedToAddTo.get(AuditDataHierarchy.RAW_RECORD_POS);
                if (recordsProcessed == null) {
                    recordsProcessed = new ArrayList<Integer>();
                    valuesChangedToAddTo.put(AuditDataHierarchy.RAW_RECORD_POS, recordsProcessed);
                }
                recordsProcessed.add(i);

                AuditDataLog branchDeleteMatch = null;

                // When a reference table ID is present, then it is treated as a pointer to another entity (e.g. OfferTemplate.offerServiceTemplates)
                // @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
                // @OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
                // @OrderBy("id")
                if (auditDataLogRaw.getReferenceId() != null) {

                    // Handle Changed values

                    if (auditDataLogRaw.getAction() == AuditCrudActionEnum.DELETE) {
                        fieldPath = fieldPathDelete;
                    }
                    valuesChangedToAddTo.put(fieldPath, changedValues);

                    // Handle a case when child entity was removed first and then parent entity is removed - e.g. @OneToMany and @ManyToMany relationship - list entities are removed before the main entity (e.g.
                    // OfferServiceTemplate.incompatibleServices)
                    // Need to move all values from a branch of list to a parent entity branch
                    if (auditDataLogRaw.getAction() == AuditCrudActionEnum.DELETE) {
                        branchDeleteMatch = (AuditDataLog) AuditDataHierarchy.removeMapKeyRecursively(txAuditLogs, fieldPathBase, true);
                        if (branchDeleteMatch != null) {
                            ((Map<String, Object>) valuesChangedToAddTo.get(fieldPath)).putAll(branchDeleteMatch.getValuesChanged());
                        }
                    }

                    // Reference table ID is missing in cases of @JoinTable, when simply a link between two entities is stored (e.g. OfferServiceTemplate.incompatibleServices)
                    // @ManyToMany(fetch = FetchType.LAZY)
                    // @JoinTable(name = "cat_offer_serv_incomp", joinColumns = @JoinColumn(name = "offer_service_template_id"), inverseJoinColumns = @JoinColumn(name = "service_template_id"))
                    // private List<ServiceTemplate> incompatibleServices = new ArrayList<>();
                    // Only two possible actions are here - DELETE and INSERT, and actions are recorded in audit log in this order.
                } else {

                    Object referencedEntityId = changedValues.get(AuditDataHierarchy.LIST_ENTITY_FIELD_ID);

                    // Handle Changed values

                    List<Object> valueChangedList = (List<Object>) valuesChangedToAddTo.get(fieldPath);
                    if (valueChangedList == null) {
                        valueChangedList = new LinkedList<Object>();
                        valuesChangedToAddTo.put(fieldPath, valueChangedList);
                    }

                    // In case action is INSERT, and DELETE already exists, means that really there was no change - remove that item from DELETE and don't add to INSERT
                    if (auditDataLogRaw.getAction() == AuditCrudActionEnum.INSERT) {
                        List<Object> deleteItems = (List<Object>) valuesChangedToAddTo.get(fieldPathDelete);
                        if (deleteItems != null) {
                            boolean itemWasDeleted = deleteItems.remove(referencedEntityId);
                            if (itemWasDeleted) {
                                if (deleteItems.isEmpty()) {
                                    valuesChangedToAddTo.remove(fieldPathDelete);
                                }
                                if (valueChangedList.isEmpty()) {
                                    valuesChangedToAddTo.remove(fieldPath);
                                }
                                continue;
                            }
                        }
                    }

                    valueChangedList.add(referencedEntityId);

                }

                // ----- Handle old values

                Map<String, Object> oldValues = lastHierarchyStep.convertChangedValues(auditDataLogRaw.getDataOld());
                if (oldValues != null && !oldValues.isEmpty()) {

                    // Try to find a parent entity to add old values to
                    match = AuditDataHierarchy.matchMapKeyRecursively(txAuditLogs, precedingFieldPath, false, false);
                    Map<String, Object> valuesOldToAddTo = null;
                    if (match != null) {
                        // A changed values map was matched to
                        if (match instanceof Map) {
                            valuesOldToAddTo = (Map<String, Object>) match;

                            // A parent entity audit data log was matched to
                        } else {
                            valuesOldToAddTo = ((AuditDataLog) match).getValuesOldNullSafe();
                        }

                        // No audit data log record for a parent entity was processed yet, so add it as AuditDataLog with old values
                    } else {
                        valuesOldToAddTo = new LinkedHashMap<String, Object>();
                        AuditDataLog precedingAuditDataLog = new AuditDataLog();
                        precedingAuditDataLog.setValuesOld(valuesOldToAddTo);
                        txAuditLogs.put(precedingFieldPath, precedingAuditDataLog);
                        // This is applicable only for a hierarchy of at least three levels
                        if (hierarchyPath.size() > 2) {
                            precedingAuditDataLog.setPrecedingDataHierarchy(hierarchyPath.subList(0, hierarchyPath.size() - 1));
                        }
                    }

                    // When a reference table ID is present, then it is treated as a pointer to another entity (e.g. OfferTemplate.offerServiceTemplates)
                    // @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
                    // @OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
                    // @OrderBy("id")
                    if (auditDataLogRaw.getReferenceId() != null) {

                        valuesOldToAddTo.put(fieldPathBase, oldValues);

                        if (branchDeleteMatch != null) {
                            ((Map<String, Object>) valuesOldToAddTo.get(fieldPathBase)).putAll(branchDeleteMatch.getValuesOld());
                        }

                        // Reference table ID is missing in cases of @JoinTable, when simply a link between two entities is stored (e.g. OfferServiceTemplate.incompatibleServices)
                        // @ManyToMany(fetch = FetchType.LAZY)
                        // @JoinTable(name = "cat_offer_serv_incomp", joinColumns = @JoinColumn(name = "offer_service_template_id"), inverseJoinColumns = @JoinColumn(name = "service_template_id"))
                        // private List<ServiceTemplate> incompatibleServices = new ArrayList<>();
                        // Only two possible actions are here - DELETE and INSERT, and actions are recorded in audit log in this order.
                    } else {

                        Object oldReferencedEntityId = oldValues.get(AuditDataHierarchy.LIST_ENTITY_FIELD_ID);
                        if (oldReferencedEntityId != null) {

                            List<Object> valueOldList = (List<Object>) valuesOldToAddTo.get(fieldPathBase);
                            if (valueOldList == null) {
                                valueOldList = new LinkedList<Object>();
                                valuesOldToAddTo.put(fieldPathBase, valueOldList);
                            }

                            valueOldList.add(oldReferencedEntityId);
                        }
                    }
                }
            }
        }

        List<AuditDataLog> aggregatedLogs = new ArrayList<AuditDataLog>();

        EntityManager em = getEntityManager();

        // Move the changed values that were added to the root, as parent entity was not processed yet, to a parent entity changed value set
        for (Entry<Long, Map<String, AuditDataLog>> auditDatLogsTxInfo : auditDataLogsByTx.entrySet()) {

            Long txId = auditDatLogsTxInfo.getKey();
            Map<String, AuditDataLog> auditDataLogs = auditDatLogsTxInfo.getValue();

            List<Entry<String, AuditDataLog>> unmappedEntriesThatAreInRoot = new ArrayList<Entry<String, AuditDataLog>>();

            for (Entry<String, AuditDataLog> auditDataLogInfo : auditDataLogs.entrySet()) {
                // Fake, unmapped entry
                if (auditDataLogInfo.getValue().getAction() == null) {
                    unmappedEntriesThatAreInRoot.add(auditDataLogInfo);
                } else {
                    aggregatedLogs.add(auditDataLogInfo.getValue());
                }
            }

            // Move the values
            for (Entry<String, AuditDataLog> entryInRoot : unmappedEntriesThatAreInRoot) {
                auditDataLogs.remove(entryInRoot.getKey());

                AuditDataLog unmappedAuditDataLog = entryInRoot.getValue();

                boolean movedChanged = false;
                boolean movedOld = false;

                // --- Move old values
                if (unmappedAuditDataLog.getValuesOld() != null) {

                    // Find a corresponding parent entity and move to there
                    Object match = AuditDataHierarchy.matchMapKeyRecursively(auditDataLogs, entryInRoot.getKey(), false, false);

                    boolean addIntermediate = true;
                    // If match was not found, lookup in DB parent entity id and look it up again with a parent key
                    if (match == null && unmappedAuditDataLog.getPrecedingDataHierarchy() != null) {
                        Long parsedId = AuditDataHierarchy.parseParentEntityId(entryInRoot.getKey());
                        if (parsedId != null) {

                            List<AuditDataHierarchy> hierarchyPath = (List<AuditDataHierarchy>) unmappedAuditDataLog.getPrecedingDataHierarchy();
                            AuditDataHierarchy precedingHierarchy = hierarchyPath.get(hierarchyPath.size() - 1);
                            AuditDataHierarchy prePrecedingHierarchy = hierarchyPath.get(hierarchyPath.size() - 2);

                            String sql = "select " + precedingHierarchy.getParentIdField() + ".id from " + precedingHierarchy.getEntityClass().getSimpleName() + " where id=:id";
                            try {
                                Long parentId = em.createQuery(sql, Long.class).setParameter("id", parsedId).getSingleResult();
                                String parentKey = prePrecedingHierarchy.getEntityClass().getName() + "_" + parentId;
                                match = AuditDataHierarchy.matchMapKeyRecursively(auditDataLogs, parentKey, false, false);
                                addIntermediate = true;

                            } catch (NoResultException e) {
                                // Data not found, error is recored later
                            }
                        }
                    }

                    Map<String, Object> valuesToAddTo = null;
                    if (match != null) {
                        // A changed values map was matched to
                        if (match instanceof Map) {
                            valuesToAddTo = (Map<String, Object>) match;

                            // A parent entity audit data log was matched to
                        } else {
                            valuesToAddTo = ((AuditDataLog) match).getValuesOld();
                        }
                        if (addIntermediate) {
                            valuesToAddTo.put(entryInRoot.getKey(), unmappedAuditDataLog.getValuesOld());
                        } else {
                            valuesToAddTo.putAll(unmappedAuditDataLog.getValuesOld());
                        }
                        movedOld = true;
                    }
                } else {
                    movedOld = true;
                }

                // --- Move changed values
                if (unmappedAuditDataLog.getValuesChanged() != null) {

                    // Find a corresponding parent entity and move to there
                    Object match = AuditDataHierarchy.matchMapKeyRecursively(auditDataLogs, entryInRoot.getKey(), false, true);

                    boolean addIntermediate = true;
                    // If match was not found, lookup in DB parent entity id and look it up again with a parent key
                    if (match == null && unmappedAuditDataLog.getPrecedingDataHierarchy() != null) {
                        Long parsedId = AuditDataHierarchy.parseParentEntityId(entryInRoot.getKey());
                        if (parsedId != null) {

                            List<AuditDataHierarchy> hierarchyPath = (List<AuditDataHierarchy>) unmappedAuditDataLog.getPrecedingDataHierarchy();
                            AuditDataHierarchy precedingHierarchy = hierarchyPath.get(hierarchyPath.size() - 1);
                            AuditDataHierarchy prePrecedingHierarchy = hierarchyPath.get(hierarchyPath.size() - 2);

                            String sql = "select " + precedingHierarchy.getParentIdField() + ".id from " + precedingHierarchy.getEntityClass().getSimpleName() + " where id=:id";
                            try {
                                Long parentId = em.createQuery(sql, Long.class).setParameter("id", parsedId).getSingleResult();
                                String parentKey = prePrecedingHierarchy.getEntityClass().getName() + "_" + parentId;
                                match = AuditDataHierarchy.matchMapKeyRecursively(auditDataLogs, parentKey, false, true);
                                addIntermediate = true;

                            } catch (NoResultException e) {
                                // Data not found, error is recored later
                            }
                        }
                    }

                    Map<String, Object> valuesToAddTo = null;
                    if (match != null) {
                        // A changed values map was matched to
                        if (match instanceof Map) {
                            valuesToAddTo = (Map<String, Object>) match;

                            // A parent entity audit data log was matched to
                        } else {
                            valuesToAddTo = ((AuditDataLog) match).getValuesChanged();
                        }
                        if (addIntermediate) {
                            valuesToAddTo.put(entryInRoot.getKey(), unmappedAuditDataLog.getValuesChanged());
                        } else {
                            valuesToAddTo.putAll(unmappedAuditDataLog.getValuesChanged());
                        }
                        movedChanged = true;
                    }
                } else {
                    movedChanged = true;
                }

                if (!movedChanged || !movedOld) {
                    // Failed to find where to aggregate log entry to, so store it as a new auditDataLog with no relation to an entity

                    AuditDataLog auditDataLog = new AuditDataLog();

                    auditDataLog.setValuesChanged(new LinkedHashMap<String, Object>());

                    auditDataLog.setAction(AuditCrudActionEnum.UPDATE);
                    auditDataLog.setTxId(txId);

                    auditDataLog.setEntityClassAsClass(dataHierarchy.getEntityClass());
                    auditDataLog.setEntityId(0L);
                    if (!movedChanged) {
                        auditDataLog.getValuesChanged().put(entryInRoot.getKey(), unmappedAuditDataLog.getValuesChanged());
                    }
                    if (!movedOld) {
                        auditDataLog.getValuesOldNullSafe().put(entryInRoot.getKey(), unmappedAuditDataLog.getValuesOld());
                    }

                    List<Object> rawRecordPositions = (List<Object>) unmappedAuditDataLog.getValuesChanged().get(AuditDataHierarchy.RAW_RECORD_POS);
                    if (rawRecordPositions != null && !rawRecordPositions.isEmpty()) {
                        AuditDataLogRecord auditDataLogRaw = auditDataLogRecords.get(0);
                        auditDataLog.setCreated(auditDataLogRaw.getCreated());
                        auditDataLog.setOrigin(auditDataLogRaw.getOrigin());
                        auditDataLog.setOriginName(auditDataLogRaw.getOriginName());
                        auditDataLog.setUserName(auditDataLogRaw.getUserName());

                    }

                    aggregatedLogs.add(auditDataLog);
                }
            }
        }

        // Link original audit data log records to an aggregated audit data log
        List<Integer> recordsProcessed = new ArrayList<Integer>();
        for (int i = aggregatedLogs.size() - 1; i >= 0; i--) {

            AuditDataLog auditDataLog = aggregatedLogs.get(i);
            List<Object> rawRecordPositions = AuditDataHierarchy.removeMapKeyRecursivelyMultipleTimes(auditDataLog.getValuesChanged(), AuditDataHierarchy.RAW_RECORD_POS, true);
            for (Object rawRecordPositionList : rawRecordPositions) {
                for (Integer pos : (List<Integer>) rawRecordPositionList) {
                    AuditDataLogRecord auditDataLogRaw = auditDataLogRecords.get(pos);
                    auditDataLog.addAuditDataLogRecord(auditDataLogRaw.getId());
                    recordsProcessed.add(pos);
                }
            }

            // Remove any empty map values
            ListUtils.removeEmptyMapValuesRecursively(auditDataLog.getValuesChanged());
            if (auditDataLog.getValuesOld() != null) {
                ListUtils.removeEmptyMapValuesRecursively(auditDataLog.getValuesOld());
            }
        }

        // Remove records from an original audit data record list that were processed already
        recordsProcessed.sort(Comparator.naturalOrder());
        for (int i = recordsProcessed.size() - 1; i >= 0; i--) {
            auditDataLogRecords.remove(recordsProcessed.get(i).intValue());
        }

        return aggregatedLogs;
    }
}