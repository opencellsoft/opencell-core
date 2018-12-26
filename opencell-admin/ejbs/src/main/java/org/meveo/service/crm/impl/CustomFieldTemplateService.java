package org.meveo.service.crm.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.CalendarBanking;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.service.base.BusinessService;
import org.meveo.service.custom.CfValueAccumulator;
import org.meveo.service.index.ElasticClient;
import org.meveo.util.PersistenceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 * 
 */
@Stateless
public class CustomFieldTemplateService extends BusinessService<CustomFieldTemplate> {

    @Inject
    private CustomFieldsCacheContainerProvider customFieldsCache;

    @Inject
    private ElasticClient elasticClient;

    @EJB
    private CfValueAccumulator cfValueAccumulator;

    @Inject
    private ClusterEventPublisher clusterEventPublisher;

    static boolean useCFTCache = true;

    @PostConstruct
    private void init() {
        useCFTCache = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("cache.cacheCFT", "true"));
    }

    /**
     * Find a list of custom field templates corresponding to a given entity
     * 
     * @param entity Entity that custom field templates apply to
     * @return A list of custom field templates mapped by a template key. Will return an empty map if no fields were found
     */
    public Map<String, CustomFieldTemplate> findByAppliesTo(ICustomFieldEntity entity) {
        try {
            return findByAppliesTo(CustomFieldTemplateService.calculateAppliesToValue(entity));

        } catch (CustomFieldException e) {
            // Its ok, handles cases when value that is part of CFT.AppliesTo calculation is not set yet on entity
            return new HashMap<String, CustomFieldTemplate>();
        }
    }

    /**
     * Find a list of custom field templates corresponding to a given entity. Custom field templates are looked up in cache or retrieved from DB.
     * 
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @return A list of custom field templates mapped by a template key
     */
    public Map<String, CustomFieldTemplate> findByAppliesTo(String appliesTo) {

        if (useCFTCache) {

            Map<String, CustomFieldTemplate> cfts = customFieldsCache.getCustomFieldTemplates(appliesTo);

            // Populate cache if record is not found in cache
            if (cfts == null) {
                cfts = findByAppliesToNoCache(appliesTo);
                if (cfts.isEmpty()) {
                    customFieldsCache.markNoCustomFieldTemplates(appliesTo);
                } else {
                    cfts.forEach((code, cft) -> customFieldsCache.addUpdateCustomFieldTemplate(cft));
                }
            }

            return cfts;

        } else {
            return findByAppliesToNoCache(appliesTo);
        }
    }

    /**
     * Find a list of custom field templates corresponding to a given entity - always do a lookup in DB
     * 
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @return A list of custom field templates mapped by a template key
     */
    public Map<String, CustomFieldTemplate> findByAppliesToNoCache(String appliesTo) {

        List<CustomFieldTemplate> values = getEntityManager().createNamedQuery("CustomFieldTemplate.getCFTByAppliesTo", CustomFieldTemplate.class)
            .setParameter("appliesTo", appliesTo).getResultList();

        Map<String, CustomFieldTemplate> cftMap = values.stream().collect(Collectors.toMap(cft -> cft.getCode(), cft -> cft));

        return cftMap;
    }

    /**
     * Find a specific custom field template by a code
     * 
     * @param code Custom field template code
     * @param entity Entity that custom field templates apply to
     * @return Custom field template or NULL if not found
     */
    public CustomFieldTemplate findByCodeAndAppliesTo(String code, ICustomFieldEntity entity) {
        try {
            return findByCodeAndAppliesTo(code, CustomFieldTemplateService.calculateAppliesToValue(entity));

        } catch (CustomFieldException e) {
            log.error("Can not determine applicable CFT type for entity of {} class.", entity.getClass().getSimpleName());
        }
        return null;
    }

    /**
     * Find a specific custom field template by a code. Custom field template will be looked up from cache or retrieved from DB.
     * 
     * @param code Custom field template code
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @return Custom field template or NULL if not found
     */
    public CustomFieldTemplate findByCodeAndAppliesTo(String code, String appliesTo) {

        if (useCFTCache) {

            CustomFieldTemplate cft = customFieldsCache.getCustomFieldTemplate(code, appliesTo);

            // Populate cache if record is not found in cache
            if (cft == null) {
                cft = findByCodeAndAppliesToNoCache(code, appliesTo);
                if (cft != null) {
                    customFieldsCache.addUpdateCustomFieldTemplate(cft);
                }
            }
            return cft;

        } else {
            return findByCodeAndAppliesToNoCache(code, appliesTo);
        }
    }

    /**
     * Find a specific custom field template by a code bypassing cache - always do a lookup in DB
     * 
     * @param code Custom field template code
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @return Custom field template or NULL if not found
     */
    public CustomFieldTemplate findByCodeAndAppliesToNoCache(String code, String appliesTo) {

        try {
            return getEntityManager().createNamedQuery("CustomFieldTemplate.getCFTByCodeAndAppliesTo", CustomFieldTemplate.class).setParameter("code", code)
                .setParameter("appliesTo", appliesTo).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void create(CustomFieldTemplate cft) throws BusinessException {

        if ("INVOICE_SEQUENCE".equals(cft.getCode()) && (cft.getFieldType() != CustomFieldTypeEnum.LONG || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE
                || !cft.isVersionable() || cft.getCalendar() == null)) {
            throw new ValidationException("invoice_sequence CF must be versionnable, Long, Single value and must have a Calendar");
        }
        if ("INVOICE_ADJUSTMENT_SEQUENCE".equals(cft.getCode()) && (cft.getFieldType() != CustomFieldTypeEnum.LONG || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE
                || !cft.isVersionable() || cft.getCalendar() == null)) {
            throw new ValidationException("invoice_adjustement_sequence CF must be versionnable, Long, Single value and must have a Calendar");
        }
        super.create(cft);

        customFieldsCache.addUpdateCustomFieldTemplate(cft);
        elasticClient.updateCFMapping(cft);
        boolean reaccumulateCFValues = cfValueAccumulator.refreshCfAccumulationRules(cft);
        if (reaccumulateCFValues) {

            clusterEventPublisher.publishEvent(cft, CrudActionEnum.create);
            cfValueAccumulator.cftCreated(cft);
        }
    }

    @Override
    public CustomFieldTemplate update(CustomFieldTemplate cft) throws BusinessException {

        if ("INVOICE_SEQUENCE".equals(cft.getCode()) && (cft.getFieldType() != CustomFieldTypeEnum.LONG || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE
                || !cft.isVersionable() || cft.getCalendar() == null)) {
            throw new ValidationException("invoice_sequence CF must be versionnable, Long, Single value and must have a Calendar");
        }
        if ("INVOICE_ADJUSTMENT_SEQUENCE".equals(cft.getCode()) && (cft.getFieldType() != CustomFieldTypeEnum.LONG || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE
                || !cft.isVersionable() || cft.getCalendar() == null)) {
            throw new ValidationException("invoice_adjustement_sequence CF must be versionnable, Long, Single value and must have a Calendar");
        }
        CustomFieldTemplate cftUpdated = super.update(cft);

        customFieldsCache.addUpdateCustomFieldTemplate(cftUpdated);
        elasticClient.updateCFMapping(cftUpdated);

        return cftUpdated;
    }

    @Override
    public void remove(CustomFieldTemplate cft) throws BusinessException {
        customFieldsCache.removeCustomFieldTemplate(cft);
        super.remove(cft);
        cfValueAccumulator.refreshCfAccumulationRules(cft);
    }

    @Override
    public CustomFieldTemplate enable(CustomFieldTemplate cft) throws BusinessException {
        cft = super.enable(cft);
        customFieldsCache.addUpdateCustomFieldTemplate(cft);
        return cft;
    }

    @Override
    public CustomFieldTemplate disable(CustomFieldTemplate cft) throws BusinessException {
        cft = super.disable(cft);
        customFieldsCache.removeCustomFieldTemplate(cft);
        return cft;
    }

    /**
     * Get a list of custom field templates for cache
     * 
     * @return A list of custom field templates
     */
    public List<CustomFieldTemplate> getCFTForCache() {
        List<CustomFieldTemplate> cfts = getEntityManager().createNamedQuery("CustomFieldTemplate.getCFTForCache", CustomFieldTemplate.class).getResultList();
        return cfts;
    }

    /**
     * Get a list of custom field templates for index
     * 
     * @return A list of custom field templates
     */
    public List<CustomFieldTemplate> getCFTForIndex() {
        List<CustomFieldTemplate> cfts = getEntityManager().createNamedQuery("CustomFieldTemplate.getCFTForIndex", CustomFieldTemplate.class).getResultList();
        return cfts;
    }

    /**
     * Calculate custom field template AppliesTo value for a given entity. AppliesTo consist of a prefix and optionally one or more entity fields. e.g. JOB_jobTemplate
     * 
     * @param entity Entity
     * @return A appliesTo value
     * @throws CustomFieldException An exception when AppliesTo value can not be calculated. Occurs when value that is part of CFT.AppliesTo calculation is not set yet on entity
     */
    public static String calculateAppliesToValue(ICustomFieldEntity entity) throws CustomFieldException {
        CustomFieldEntity cfeAnnotation = entity.getClass().getAnnotation(CustomFieldEntity.class);
        String appliesTo = null;
        if (cfeAnnotation != null){
            appliesTo = cfeAnnotation.cftCodePrefix();
            if (cfeAnnotation.cftCodeFields().length > 0) {
                for (String fieldName : cfeAnnotation.cftCodeFields()) {
                    try {
                        Object fieldValue = FieldUtils.getField(entity.getClass(), fieldName, true).get(entity);
                        if (fieldValue == null) {
                            throw new CustomFieldException("Can not calculate AppliesTo value");
                        }
                        appliesTo = appliesTo + "_" + fieldValue;
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        Logger log = LoggerFactory.getLogger(CustomFieldTemplateService.class);
                        log.error("Unable to access field {}.{}", entity.getClass().getSimpleName(), fieldName);
                        throw new RuntimeException("Unable to access field " + entity.getClass().getSimpleName() + "." + fieldName);
                    }
                }
            }
        }
        return appliesTo;
    }

    /**
     * Check and create missing templates given a list of templates.
     * 
     * @param entity Entity that custom field templates apply to
     * @param templates A list of templates to check
     * @return A complete list of templates for a given entity. Mapped by a custom field template key.
     * @throws BusinessException business exception.
     */
    public Map<String, CustomFieldTemplate> createMissingTemplates(ICustomFieldEntity entity, Collection<CustomFieldTemplate> templates) throws BusinessException {
        try {
            return createMissingTemplates(calculateAppliesToValue(entity), templates, false, false);

        } catch (CustomFieldException e) {
            // Its OK, handles cases when value that is part of CFT.AppliesTo calculation is not set yet on entity
            return new HashMap<String, CustomFieldTemplate>();
        }
    }

    /**
     * Check and create missing templates given a list of templates.
     * 
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @param templates A list of templates to check
     * @return A complete list of templates for a given entity. Mapped by a custom field template key.
     * @throws BusinessException business exception.
     */
    public Map<String, CustomFieldTemplate> createMissingTemplates(String appliesTo, Collection<CustomFieldTemplate> templates) throws BusinessException {
        return createMissingTemplates(appliesTo, templates, false, false);
    }

    /**
     * Check and create missing templates given a list of templates.
     * 
     * @param entity Entity that custom field templates apply to
     * @param templates A list of templates to check
     * @param removeOrphans When set to true, this will remove custom field templates that are not included in the templates collection.
     * @param updateExisting true if updating existing templates
     * @return A complete list of templates for a given entity. Mapped by a custom field template key.
     * @throws BusinessException business exception.
     */
    public Map<String, CustomFieldTemplate> createMissingTemplates(ICustomFieldEntity entity, Collection<CustomFieldTemplate> templates, boolean updateExisting,
            boolean removeOrphans) throws BusinessException {
        try {
            return createMissingTemplates(calculateAppliesToValue(entity), templates, updateExisting, removeOrphans);
        } catch (CustomFieldException e) {
            // Its OK, handles cases when value that is part of CFT.AppliesTo calculation is not set yet on entity
            return new HashMap<String, CustomFieldTemplate>();
        }
    }

    /**
     * Check and create missing templates given a list of templates.
     * 
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @param templates A list of templates to check
     * @param removeOrphans When set to true, this will remove custom field templates that are not included in the templates collection.
     * @param updateExisting true when updating missing template.
     * @return A complete list of templates for a given entity. Mapped by a custom field template key.
     * @throws BusinessException business exception.
     */
    public Map<String, CustomFieldTemplate> createMissingTemplates(String appliesTo, Collection<CustomFieldTemplate> templates, boolean updateExisting, boolean removeOrphans)
            throws BusinessException {

        // Get templates corresponding to an entity type
        Map<String, CustomFieldTemplate> allTemplates = findByAppliesToNoCache(appliesTo);

        if (templates != null) {
            CustomFieldTemplate existingCustomField = null;
            for (CustomFieldTemplate cft : templates) {
                if (!allTemplates.containsKey(cft.getCode())) {
                    log.debug("Create a missing CFT {} for {} entity", cft.getCode(), appliesTo);
                    create(cft);
                    allTemplates.put(cft.getCode(), cft);
                } else if (updateExisting) {
                    existingCustomField = allTemplates.get(cft.getCode());
                    existingCustomField.setDescription(cft.getDescription());
                    existingCustomField.setStorageType(cft.getStorageType());
                    existingCustomField.setAllowEdit(cft.isAllowEdit());
                    existingCustomField.setDefaultValue(cft.getDefaultValue());
                    existingCustomField.setFieldType(cft.getFieldType());
                    existingCustomField.setEntityClazz(cft.getEntityClazz());
                    existingCustomField.setListValues(cft.getListValues());
                    existingCustomField.setGuiPosition(cft.getGuiPosition());
                    log.debug("Update existing CFT {} for {} entity", cft.getCode(), appliesTo);
                    update(existingCustomField);
                }
            }
            if (removeOrphans) {
                CustomFieldTemplate customFieldTemplate = null;
                List<CustomFieldTemplate> forRemoval = new ArrayList<>();
                for (Map.Entry<String, CustomFieldTemplate> customFieldTemplateEntry : allTemplates.entrySet()) {
                    customFieldTemplate = customFieldTemplateEntry.getValue();
                    if (!templates.contains(customFieldTemplate)) {
                        // add to separate list to avoid ConcurrentModificationException
                        forRemoval.add(customFieldTemplate);
                    }
                }
                for (CustomFieldTemplate fieldTemplate : forRemoval) {
                    remove(fieldTemplate);
                }
            }
        }
        return allTemplates;
    }

    /**
     * Copy and associate a given custom field template to a given target entity type.
     * 
     * @param cft Custom field template to copy
     * @param targetAppliesTo Target CFT.appliesTo value associate custom field template with
     * @return custom field template
     * @throws BusinessException business exception.
     */
    public CustomFieldTemplate copyCustomFieldTemplate(CustomFieldTemplate cft, String targetAppliesTo) throws BusinessException {

        if (findByCodeAndAppliesTo(cft.getCode(), targetAppliesTo) != null) {
            throw new ValidationException("Custom field template " + cft.getCode() + " already exists in targe entity " + targetAppliesTo,
                "customFieldTemplate.copyCFT.alreadyExists");
        }

        // Load calendar for lazy loading
        if (cft.getCalendar() != null) {
            cft.setCalendar(PersistenceUtils.initializeAndUnproxy(cft.getCalendar()));
            if (cft.getCalendar() instanceof CalendarDaily) {
                ((CalendarDaily) cft.getCalendar()).setHours(PersistenceUtils.initializeAndUnproxy(((CalendarDaily) cft.getCalendar()).getHours()));
                ((CalendarDaily) cft.getCalendar()).nextCalendarDate(new Date());
            } else if (cft.getCalendar() instanceof CalendarYearly) {
                ((CalendarYearly) cft.getCalendar()).setDays(PersistenceUtils.initializeAndUnproxy(((CalendarYearly) cft.getCalendar()).getDays()));
                ((CalendarYearly) cft.getCalendar()).nextCalendarDate(new Date());
            } else if (cft.getCalendar() instanceof CalendarInterval) {
                ((CalendarInterval) cft.getCalendar()).setIntervals(PersistenceUtils.initializeAndUnproxy(((CalendarInterval) cft.getCalendar()).getIntervals()));
                ((CalendarInterval) cft.getCalendar()).nextCalendarDate(new Date());
            } else if (cft.getCalendar() instanceof CalendarBanking) {
                ((CalendarBanking)  cft.getCalendar()).setHolidays((PersistenceUtils.initializeAndUnproxy(((CalendarBanking) cft.getCalendar()).getHolidays())));
                ((CalendarBanking)  cft.getCalendar()).nextCalendarDate(new Date());
            }
        }
        if (cft.getListValues() != null) {
            cft.getListValues().values().toArray(new String[] {});
        }

        if (cft.getMatrixColumns() != null) {
            cft.getMatrixColumns().toArray(new CustomFieldMatrixColumn[] {});
        }

        detach(cft);

        CustomFieldTemplate cftCopy = SerializationUtils.clone(cft);
        cftCopy.setId(null);
        cftCopy.setVersion(null);
        cftCopy.setAppliesTo(targetAppliesTo);

        if (cft.getListValues() != null) {
            cftCopy.setListValues(new HashMap<>());
            cftCopy.getListValues().putAll(cft.getListValues());
        }

        if (cft.getMatrixColumns() != null) {
            cftCopy.setMatrixColumns(new ArrayList<>());
            cftCopy.getMatrixColumns().addAll(cft.getMatrixColumns());
        }

        create(cftCopy);

        return cftCopy;
    }

    /**
     * Get a list of custom fields to construct Custom field value accumulation rule. Only those fields that repeat over different entity classes are considered for acumulation.
     * 
     * @param appliesToValues AppliesTo values to filter only those entity classes that have custom field inheritance
     * @return A list of custom field templates
     */
    public List<CustomFieldTemplate> getCustomFieldsForAcumulation(Set<String> appliesToValues) {
        return getEntityManager().createNamedQuery("CustomFieldTemplate.getCFTsForAccumulation", CustomFieldTemplate.class).setParameter("appliesTo", appliesToValues)
            .getResultList();
    }
    
    
    /**
     * Get the file reader
     * 
     * @param cft the custom field
     * @return the file reader
     */
    private ObjectReader getReader(CustomFieldTemplate cft) {
        CsvSchema.Builder builder = CsvSchema.builder();

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
            builder.addColumn(CustomFieldValue.MAP_VALUE).build();
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            builder.addColumn(CustomFieldValue.MAP_KEY).addColumn(CustomFieldValue.MAP_VALUE).build();
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            for (CustomFieldMatrixColumn column : cft.getMatrixColumns()) {
                builder.addColumn(column.getCode());
            }
            if (cft.getFieldType() != CustomFieldTypeEnum.MULTI_VALUE) {
                builder.addColumn(CustomFieldValue.MAP_VALUE);
            }
        }

        CsvSchema schema = builder.build();
        CsvMapper mapper = new CsvMapper();
        return mapper.readerFor(Map.class).with(schema);
    }

    /**
     * Serialize map, list and entity reference values that were adapted for csv file data entry.
     * 
     * @param cft the custom field template
     * @param cfDto the custom field Dto.
     *
     * @return the custom field Dto values
     */
    public Object serializeFromFile(CustomFieldTemplate cft, CustomFieldDto cfDto) {

        Object values = null;
        if (cfDto != null && !StringUtils.isBlank(cfDto.getFileValue())) {
            byte[] bytes = Base64.decodeBase64(cfDto.getFileValue());
            
            // read from file
            ObjectReader oReader = getReader(cft);
            try (Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes))) {
                MappingIterator<Map<String, Object>> mappingIterator = oReader.readValues(reader);
                Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
                List<Object> listValue = new ArrayList<Object>();
                List<String> keyColumns = cft.getMatrixKeyColumnCodes();
                while (mappingIterator.hasNext()) {
        
                    Map<String, Object> csvLine = mappingIterator.next();
                    Object value = null;
        
                    // Populate customFieldValue.listValue
                    if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                        if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                            listValue.add(new EntityReferenceWrapper((BusinessEntity) csvLine.get(CustomFieldValue.MAP_VALUE)));
        
                        } else {
                            listValue.add(csvLine.get(CustomFieldValue.MAP_VALUE));
                        }
        
                        // Populate customFieldValue.mapValue from csv line
                    } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                        if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                            mapValue.put((String) csvLine.get(CustomFieldValue.MAP_KEY), new EntityReferenceWrapper((BusinessEntity) csvLine.get(CustomFieldValue.MAP_VALUE)));
        
                        } else {
                            mapValue.put((String) csvLine.get(CustomFieldValue.MAP_KEY), csvLine.get(CustomFieldValue.MAP_VALUE));
                        }
                        // Populate customFieldValue.mapValue from csv line
                    } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
        
                        // Multi-value values need to be concatenated and stored as string
                        if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
        
                            value = cft.serializeMultiValue(csvLine);
                            if (value == null) {
                                continue;
                            }
        
                        } else {
                            value = csvLine.get(CustomFieldValue.MAP_VALUE);
                            if (StringUtils.isBlank(value)) {
                                continue;
                            }
        
                            if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                                value = new EntityReferenceWrapper((BusinessEntity) value);
                            }
                        }
        
                        StringBuilder keyBuilder = new StringBuilder();
                        for (String column : keyColumns) {
                            keyBuilder.append(keyColumns.indexOf(column) == 0 ? "" : CustomFieldValue.MATRIX_KEY_SEPARATOR);
                            keyBuilder.append(csvLine.get(column));
                        }
                        mapValue.put(keyBuilder.toString(), value);
                    }
                }
                if (!listValue.isEmpty()) {
                    values = listValue;
                }
                if (!mapValue.isEmpty()) {
                    values = mapValue;
                }
            
            } catch (RuntimeJsonMappingException e) {
                log.error("invalid format", e.getMessage());
            } catch (IOException e) {
                log.error("read fail", e.getMessage());
            }
        }
        return values;
    }
    
}