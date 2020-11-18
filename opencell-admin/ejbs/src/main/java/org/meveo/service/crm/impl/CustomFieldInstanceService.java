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

package org.meveo.service.crm.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.custom.CustomTableRecordDto;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.CFEndPeriodEvent;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.ReferenceIdentifierCode;
import org.meveo.model.ReferenceIdentifierQuery;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.BaseService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Wassim Drira
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class CustomFieldInstanceService extends BaseService {

    @Inject
    private CustomFieldTemplateService cfTemplateService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private Event<CFEndPeriodEvent> cFEndPeriodEventProducer;

    @Resource
    private TimerService timerService;

    @Inject
    private ProviderService providerService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
	private CustomTableService customTableService;

    static boolean accumulateCF = true;

    @PostConstruct
    private void init() {
        accumulateCF = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("accumulateCF", "false"));
    }


    /**
     * Find a list of entities of a given class and matching given code. In case classname points to CustomEntityTemplate, find CustomEntityInstances of a CustomEntityTemplate code
     * <p>
     * In case the ReferenceIdentifierCode annotation does not exists in the entity, It's possible to use ReferenceIdentifierQuery annotation with the entity to define the query
     * used to retrieve the list of entities
     * </p>
     * 
     * @param classNameAndCode Classname to match. In case of CustomEntityTemplate, classname consist of "CustomEntityTemplate - &lt;CustomEntityTemplate code&gt;:"
     * @param wildcode Filter by entity code
     * @param limit max number of entities to be returned. If null no limit is applied
     * @return A list of entities
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked") // TODO review location
    public List<BusinessEntity> findBusinessEntityForCFVByCode(String classNameAndCode, String wildcode, Integer limit)
            throws ClassNotFoundException {
        Query query = null;
        Class<?> clazz = trimTableNameAndGetClass(classNameAndCode);

        StringBuilder selectQuery = new StringBuilder("select e from ")
                .append(classNameAndCode)
                .append(" e where ");
        
        if (classNameAndCode.startsWith(CustomEntityTemplate.class.getName())) {
            String cetCode = CustomFieldTemplate.retrieveCetCode(classNameAndCode);
            CustomEntityTemplate cet = customEntityTemplateService.findByCode(cetCode);
            if(!cet.isStoreAsTable()) {
	            query = getEntityManager().createQuery("select e from CustomEntityInstance e where e.cetCode=:cetCode or e.cetCode=:lowerCode and lower(e.code) like :code");
	            query.setParameter("cetCode", cetCode);
	            query.setParameter("lowerCode", cetCode.toLowerCase());
            } else {
                List<CustomTableRecordDto> result = customTableService.selectAllRecordsOfATableAsRecord(cet.getDbTablename(),wildcode);
            	return result.stream().map(record-> initTempEntityInstance(record.getId(), record.display())).collect(Collectors.toList());
            }
        } else if (clazz.isInstance(BusinessEntity.class)) {
            query = getEntityManager().createQuery(selectQuery.append("lower(e.code) like :code").toString());

        } else {
            ReferenceIdentifierCode referenceIdentifier = clazz.getAnnotation(ReferenceIdentifierCode.class);
            if (referenceIdentifier != null) {
                String field = referenceIdentifier.value();
                query = getEntityManager().createQuery(selectQuery.append("lower(cast(e." + field + " as string) ) like :code").toString());
            }

            ReferenceIdentifierQuery referenceIdentifierQuery = clazz.getAnnotation(ReferenceIdentifierQuery.class);
            if (referenceIdentifierQuery != null) {
                query = getEntityManager().createNamedQuery(referenceIdentifierQuery.value());
            }
        }

        if (query == null) {
            return Collections.EMPTY_LIST;
        }
        if(limit!=null) {
        	query.setMaxResults(limit);
        }
        query.setParameter("code", "%" + wildcode.toLowerCase() + "%");
        List<BusinessEntity> entities = query.getResultList();
        return entities;
    }

	private CustomEntityInstance initTempEntityInstance(Long id, String details) {
		CustomEntityInstance cet = new CustomEntityInstance();
		cet.setReferenceCode("" + id);
		cet.setDescription(details);
		return cet;
	}


	Class<?> trimTableNameAndGetClass(String className) throws ClassNotFoundException {
        String classNameToConvert = Optional.ofNullable(className).filter(c -> c.contains(" - ")).map(c -> c.split(" - ")[0]).orElse(className);
        return Class.forName(classNameToConvert);
    }

    /**
     * Return a value from either a custom field value or a settings/configuration parameter if CF value was not set yet by optionally setting custom field value.
     *
     * @param cfCode Custom field and/or settings/configuration parameter code
     * @param defaultParamBeanValue A default value to set as custom field value in case settings/configuration parameter was not set
     * @param entity Entity holding custom field value
     * @param saveInCFIfNotExist Set CF value if it does not exist yet
     * 
     * @return A value, or a default value if none was found in neither custom field nor settings/configuration parameter
     * @throws BusinessException business exception.
     */
    public Object getOrCreateCFValueFromParamValue(String cfCode, String defaultParamBeanValue, ICustomFieldEntity entity, boolean saveInCFIfNotExist) throws BusinessException {

        Object value = getCFValue(entity, cfCode, true);
        if (value != null) {
            return value;
        }

        // If value is not found, create a new Custom field with a value taken from configuration parameters
        value = paramBeanFactory.getInstance().getProperty(cfCode, defaultParamBeanValue);
        if (value == null) {
            return null;
        }
        try {
            // If no template found - create it first

            CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
            if (cft == null) {
                cft = new CustomFieldTemplate();
                cft.setCode(cfCode);
                cft.setAppliesTo(CustomFieldTemplateService.calculateAppliesToValue(entity));
                cft.setActive(true);
                cft.setDescription(cfCode);
                cft.setFieldType(CustomFieldTypeEnum.STRING);
                cft.setDefaultValue(value.toString());
                cft.setValueRequired(false);
                cfTemplateService.create(cft);
            }

            if (saveInCFIfNotExist) {

                // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
                if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
                    entity = providerService.findById(appProvider.getId());
                }

                entity.getCfValuesNullSafe().setValue(cfCode, value.toString());
            }
        } catch (CustomFieldException e) {
            log.error("Can not determine applicable CFT type for entity of {} class. Value from propeties file will NOT be saved as customfield",
                entity.getClass().getSimpleName());
        }
        return value;
    }

    /**
     * Get a custom field value for a given entity. If custom field is versionable, a current date will be used to access the value. Will instantiate a default value if value was
     * not found.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public Object getCFValue(ICustomFieldEntity entity, String cfCode) {
        return getCFValue(entity, cfCode, true);
    }

    /**
     * Gets the run time CF value.
     *
     * @param entity the entity
     * @param cfCode the cf code
     * @return the run time CF value
     */
    private Object getRunTimeCFValue(ICustomFieldEntity entity, String cfCode) {
        if (entity instanceof JobInstance) {
            return ((JobInstance) entity).getParamValue(cfCode);
        }
        return null;
    }

    /**
     * Get a custom field value for a given entity. If custom field is versionable, a current date will be used to access the value.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param instantiateDefaultValue Should a default value be instantiated if value was not found
     * @return Custom field value
     */
    public Object getCFValue(ICustomFieldEntity entity, String cfCode, boolean instantiateDefaultValue) {

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            // log.trace("No CFT found {}/{}", entity, code);
            return null;
        }

        Object runTimeCFValue = this.getRunTimeCFValue(entity, cfCode);
        if (runTimeCFValue != null) {
            return runTimeCFValue;
        }

        if (cft.isVersionable()) {
            log.warn("Trying to access a versionable custom field {}/{} value with no provided date. Current date will be used", entity.getClass().getSimpleName(), cfCode);
            return getCFValue(entity, cfCode, new Date(), instantiateDefaultValue);
        }

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        Object value = entity.getCfValue(cfCode);

        // Create such CF with default value if one is specified on CFT and other conditions match
        if (value == null && instantiateDefaultValue) {
            value = instantiateCFWithDefaultValue(entity, cft);
        }

        return value;
    }

    /**
     * Get a custom field value for a given entity and a date. Will instantiate a default value if value not found.
     *
     * @param entity Entity
     * @param code Custom field code
     * @param date Date
     * @return Custom field value
     */
    public Object getCFValue(ICustomFieldEntity entity, String code, Date date) {
        return getCFValue(entity, code, date, true);
    }

    /**
     * Get a custom field value for a given entity and a date.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @param instantiateDefaultValue Should a default value be instantiated if value was not found
     * @return Custom field value
     */
    public Object getCFValue(ICustomFieldEntity entity, String cfCode, Date date, boolean instantiateDefaultValue) {

        // If field is not versionable - get the value without the date
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            // log.trace("No CFT found {}/{}", entity, code);
            return null;
        }

        Object runTimeCFValue = this.getRunTimeCFValue(entity, cfCode);
        if (runTimeCFValue != null) {
            return runTimeCFValue;
        }

        if (!cft.isVersionable()) {
            return getCFValue(entity, cfCode, instantiateDefaultValue);
        }

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        Object value = entity.getCfValue(cfCode, date);

        // Create such CF with default value if one is specified on CFT and other conditions match
        if (value == null && instantiateDefaultValue) {
            value = instantiateCFWithDefaultValue(entity, cft, date);
        }

        return value;
    }

    /**
     * Get custom field values of an entity As JSON string
     * 
     * @param entity Entity with custom field values
     * @return JSON format string
     */
    public String getCFValuesAsJson(ICustomFieldEntity entity) {
        return getCFValuesAsJson(entity, false);
    }

    /**
     * Get custom field values of an entity as JSON string
     *
     * @param entity Entity
     * @param includeParent Include custom field values of parent custom field entities in custom field inheritance hierarchy
     * @return JSON format string
     */
    public String getCFValuesAsJson(ICustomFieldEntity entity, boolean includeParent) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        String result = "";
        String sep = "";
        Map<String, CustomFieldTemplate> cfts = null;

        if (accumulateCF && includeParent) {
            if (entity.getCfAccumulatedValues() != null) {
                cfts = cfTemplateService.findByAppliesTo(entity);
                result = entity.getCfAccumulatedValues().asJson(cfts);
                sep = ",";
            }

        } else {
            if (entity.getCfValues() != null) {
                cfts = cfTemplateService.findByAppliesTo(entity);
                result = entity.getCfValues().asJson(cfts);
                sep = ",";
            }

            if (includeParent) {
                ICustomFieldEntity[] parentCFEntities = getHierarchyParentCFEntities(entity);
                if (parentCFEntities != null && parentCFEntities.length > 0) {
                    for (ICustomFieldEntity parentCF : parentCFEntities) {
                        if (parentCF.getCfValues() != null) {
                            cfts = cfTemplateService.findByAppliesTo(parentCF);
                            result += sep + parentCF.getCfValues().asJson(cfts);
                            sep = ",";
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Append custom field values of an entity to XML document, each as "customField" element
     * 
     * @param entity Entity with custom field values
     * @param doc Document to append custom field values to
     * @return Appended XML document element
     */
    public Element getCFValuesAsDomElement(ICustomFieldEntity entity, Document doc) {
        return getCFValuesAsDomElement(entity, doc, false);
    }

    /**
     * Append custom field values of an entity to XML document, each as "customField" element
     * 
     * @param entity Entity with custom field values
     * @param doc Document to append custom field values to
     * @param includeParent Include custom field values of parent custom field entities in custom field inheritance hierarchy
     * @return Appended XML document element
     */
    public Element getCFValuesAsDomElement(ICustomFieldEntity entity, Document doc, boolean includeParent) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        Element customFieldsTag = doc.createElement("customFields");

        Map<String, CustomFieldTemplate> cfts = cfTemplateService.findByAppliesTo(entity);

        if (accumulateCF && includeParent) {
            if (entity.getCfAccumulatedValues() != null) {
                entity.getCfAccumulatedValues().asDomElement(doc, customFieldsTag, cfts);
            }

        } else {
            if (entity.getCfValues() == null) {
                return customFieldsTag;
            }

            entity.getCfValues().asDomElement(doc, customFieldsTag, cfts);

            if (includeParent) {
                ICustomFieldEntity[] parentCFEntities = getHierarchyParentCFEntities(entity);
                if (parentCFEntities != null && parentCFEntities.length > 0) {
                    for (ICustomFieldEntity parentCF : parentCFEntities) {
                        if (parentCF.getCfValues() != null) {
                            cfts = cfTemplateService.findByAppliesTo(parentCF);
                            parentCF.getCfValues().asDomElement(doc, customFieldsTag, cfts);
                        }
                    }
                }
            }
        }
        return customFieldsTag;
    }

    /**
     * Set a Custom field value on an entity.
     *
     * @param entity Entity
     * @param cfCode Custom field value code
     * @param value Value to set
     * @throws BusinessException General business exception.
     */
    public void setCFValue(ICustomFieldEntity entity, String cfCode, Object value) throws BusinessException {

        log.debug("Setting CF value. Code: {}, entity {} value {}", cfCode, entity, value);

        // Can not set the value if field is versionable without a date
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            throw new BusinessException("Custom field template with code " + cfCode + " not found found for entity " + entity);
        }

        if (cft.isVersionable()) {
            throw new RuntimeException(
                "Can not determine a period for Custom Field " + entity.getClass().getSimpleName() + "/" + cfCode + " value if no date or date range is provided");
        }

        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
            entity = providerService.findById(appProvider.getId());
        }

        boolean hasCfValue = entity.hasCfValue(cfCode);

        // No existing CF value. Create CF value with new value. Assign(persist) NULL value only if cft.defaultValue is present
        if (!hasCfValue) {
            if (value == null && cft.getDefaultValue() == null) {
                return;
            }
            entity.setCfValue(cfCode, value);

            // Existing CFI found. Update with new value or NULL value only if cft.defaultValue is present
        } else if (value != null || (value == null && cft.getDefaultValue() != null)) {
            entity.setCfValue(cfCode, value);

            // Existing CF value found, but new value is null, so remove CF value all together
        } else {
            entity.removeCfValue(cfCode);
        }
    }

    /**
     * Set a Custom field value on an entity for a given date. Applies to calendar versioned custom fields.
     *
     * @param entity Entity
     * @param cfCode Custom field value code
     * @param value Value to set
     * @param valueDate Date value applies on
     * @throws BusinessException General business exception
     */
    public void setCFValue(ICustomFieldEntity entity, String cfCode, Object value, Date valueDate) throws BusinessException {

        log.debug("Setting CF value. Code: {}, entity {} value {} valueDate {}", cfCode, entity, value, valueDate);

        // If field is not versionable - set the value without the date
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            throw new BusinessException("Custom field template with code " + cfCode + " not found found for entity " + entity);
        }

        if (!cft.isVersionable()) {
            setCFValue(entity, cfCode, value);
            return;

            // Calendar is needed to be able to set a value with a single date
        } else if (cft.getCalendar() == null) {
            log.error("Can not determine a period for Custom Field {}/{} value if no calendar is provided", entity.getClass().getSimpleName(), cfCode);
            throw new RuntimeException("Can not determine a period for Custom Field " + entity.getClass().getSimpleName() + "/" + cfCode + " value if no calendar is provided");
        }

        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
            entity = providerService.findById(appProvider.getId());
        }

        // Should not match more then one record as periods are calendar based
        boolean hasCfValue = entity.hasCfValue(cfCode, valueDate);

        // No existing CF value. Create CF value with new value. Persist NULL value only if cft.defaultValue is present
        if (!hasCfValue) {
            if (value == null && cft.getDefaultValue() == null) {
                return;
            }
            entity.setCfValue(cfCode, cft.getDatePeriod(valueDate), null, value);

            // Existing CFI found. Update with new value or NULL value only if cft.defaultValue is present
        } else if (value != null || (value == null && cft.getDefaultValue() != null)) {
            entity.setCfValue(cfCode, cft.getDatePeriod(valueDate), null, value);

            // Existing CFI found, but new value is null, so remove CFI
        } else {
            entity.removeCfValue(cfCode, valueDate);
        }

    }

    /**
     * Set a Custom field value on an entity for a given date period. Applies to non-calendar versioned custom fields.
     *
     * @param entity Entity
     * @param cfCode Custom field value code
     * @param value Value to set
     * @param valueDateFrom Date period value applies on - period start date
     * @param valueDateTo Date period value applies on - period end date
     * @param valuePriority Value priority in case multiple date periods overlap
     * @throws BusinessException General business exception
     */
    public void setCFValue(ICustomFieldEntity entity, String cfCode, Object value, Date valueDateFrom, Date valueDateTo, Integer valuePriority) throws BusinessException {

        log.debug("Setting CF value. Code: {}, entity {} value {} valueDateFrom {} valueDateTo {}", cfCode, entity, value, valueDateFrom, valueDateTo);

        // If field is not versionable - set the value without the date
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            throw new BusinessException("Custom field template with code " + cfCode + " not found found for entity " + entity);
        }

        if (!cft.isVersionable()) {
            setCFValue(entity, cfCode, value);
            return;

            // If calendar is provided - use calendar by the valueDateFrom date
        } else if (cft.getCalendar() != null) {
            log.warn(
                "Calendar is provided in Custom Field template {}/{} while trying to assign value period start and end dates with two values. Only start date will be considered",
                entity.getClass().getSimpleName(), cfCode);
            setCFValue(entity, cfCode, value, valueDateFrom);
            return;
        }

        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
            entity = providerService.findById(appProvider.getId());
        }

        // Should not match more then one record, as match is strict
        boolean hasCFValue = entity.hasCfValue(cfCode, valueDateFrom, valueDateTo);

        // No existing CF value. Create CF value with new value. Persist NULL value only if cft.defaultValue is present
        if (!hasCFValue) {
            if (value == null && cft.getDefaultValue() == null) {
                return;
            }
            entity.setCfValue(cfCode, new DatePeriod(valueDateFrom, valueDateTo), valuePriority, value);

            // Existing CF value found. Update with new value or NULL value only if cft.defaultValue is present
        } else if (value != null || (value == null && cft.getDefaultValue() != null)) {
            entity.setCfValue(cfCode, new DatePeriod(valueDateFrom, valueDateTo), valuePriority, value);

            // Existing CF value found, but new value is null, so remove CF value
        } else {
            entity.getCfValues().removeValue(cfCode, valueDateFrom, valueDateTo);
        }
    }

    /**
     * Remove Custom field instance.
     *
     * @param entity custom field entity
     * @param cfCode Custom field code to remove
     * @throws BusinessException business exception.
     */
    public void removeCFValue(ICustomFieldEntity entity, String cfCode) throws BusinessException {

        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
            entity = providerService.findById(appProvider.getId());
        }

        if (entity.getCfValues() != null) {
            entity.getCfValues().removeValue(cfCode);
        }
    }

    /**
     * Remove all custom field values for a given entity.
     *
     * @param entity custom field entity
     * @throws BusinessException business exception.
     */
    public void removeCFValues(ICustomFieldEntity entity) throws BusinessException {

        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
            entity = providerService.findById(appProvider.getId());
        }

        entity.clearCfValues();
    }

    /**
     * Get a custom field value for a given entity's parent's. (DOES NOT include a given entity). If custom field is versionable, a current date will be used to access the value.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public Object getInheritedOnlyCFValue(ICustomFieldEntity entity, String cfCode) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        ICustomFieldEntity[] parentCFEntities = entity.getParentCFEntities();
        if (parentCFEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCFEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, lookup provider from appProvider
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = appProvider;
                } else {
                    parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                }
                Object value = getInheritedCFValue(parentCfEntity, cfCode);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Get a a list of custom field CFvalues for a given entity and its parent's CF entity hierarchy up.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return A list of Custom field CFvalues. From this and all the entities CF entity hierarchy up.
     */
    public List<CustomFieldValue> getInheritedAllCFValues(ICustomFieldEntity entity, String cfCode) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        List<CustomFieldValue> allValues = new ArrayList<>();

        if (accumulateCF) {
            if (entity.getCfAccumulatedValues() != null) {
                List<CustomFieldValue> entityValues = entity.getCfAccumulatedValues().getValuesByCode().get(cfCode);
                if (entityValues != null) {
                    allValues.addAll(entityValues);
                }
            }

        } else {

            if (entity.getCfValues() != null) {
                List<CustomFieldValue> entityValues = entity.getCfValues().getValuesByCode().get(cfCode);
                if (entityValues != null) {
                    allValues.addAll(entityValues);
                }
            }

            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    allValues.addAll(getInheritedAllCFValues(parentCfEntity, cfCode));
                }
            }
        }
        return allValues;
    }

    /**
     * Get a a list of custom field CFvalues for a given entity's parent's hierarchy up. (DOES NOT include a given entity)
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return A list of Custom field CFvalues. From all the entities CF entity hierarchy up.
     */
    public List<CustomFieldValue> getInheritedOnlyAllCFValues(ICustomFieldEntity entity, String cfCode) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        List<CustomFieldValue> allValues = new ArrayList<>();

        ICustomFieldEntity[] parentCFEntities = entity.getParentCFEntities();
        if (parentCFEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCFEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, lookup provider from appProvider
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = appProvider;
                } else {
                    parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                }
                allValues.addAll(getInheritedAllCFValues(parentCfEntity, cfCode));
            }
        }
        return allValues;
    }

    /**
     * Check if give entity's parent has any custom field value defined (in any period for versionable fields)
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return True if any of entity's CF parents have value for a given custom field (in any period for versionable fields)
     */
    public boolean hasInheritedOnlyCFValue(ICustomFieldEntity entity, String cfCode) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        ICustomFieldEntity[] parentCFEntities = entity.getParentCFEntities();
        if (parentCFEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCFEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, lookup provider from appProvider
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = appProvider;
                } else {
                    parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                }
                boolean hasValue = hasInheritedCFValue(parentCfEntity, cfCode);
                if (hasValue) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Check if given entity or any of its parent has any custom field value defined (in any period for versionable fields)
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return True if entity or any of entity's CF parents have value for a given custom field (in any period for versionable fields)
     */
    public boolean hasInheritedCFValue(ICustomFieldEntity entity, String cfCode) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            if (entity.getCfAccumulatedValues() != null) {
                return entity.getCfAccumulatedValues().hasCfValue(cfCode);
            }
        } else {

            boolean hasValue = hasCFValue(entity, cfCode);
            if (hasValue) {
                return true;
            }

            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    hasValue = hasInheritedCFValue(parentCfEntity, cfCode);
                    if (hasValue) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check if given entity has custom field value defined (in any period for versionable fields)
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return True if entity or any of entity's CF parents have value for a given custom field (in any period for versionable fields)
     */
    public boolean hasCFValue(ICustomFieldEntity entity, String cfCode) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        return entity.hasCfValue(cfCode);
    }

    /**
     * get hierarchy parents of cf entity
     *
     * @param entity
     * @return
     */
    private ICustomFieldEntity[] getHierarchyParentCFEntities(ICustomFieldEntity entity) {

        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities == null) {
            return null;
        }
        Set<ICustomFieldEntity> result = new HashSet<ICustomFieldEntity>();
        for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
            if (parentCfEntity == null) {
                continue;
            }
            // If Parent entity is Provider, lookup provider from appProvider
            if (parentCfEntity instanceof Provider) {
                parentCfEntity = appProvider;
            } else {
                parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
            }
            result.add(parentCfEntity);
            ICustomFieldEntity[] recurseCfes = getHierarchyParentCFEntities(parentCfEntity);
            if (recurseCfes != null && recurseCfes.length > 0) {
                result.addAll(Arrays.asList(recurseCfes));
            }
        }
        return result.toArray(new ICustomFieldEntity[0]);
    }

    /**
     * Get a cumulative and unique custom field value for a given entity's all parent chain. (DOES NOT include a given entity). Applies to Map (matrix) values only. The closest
     * parent entity's CF value will be preserved. If custom field is versionable, a current date will be used to access the value.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return Custom field value
     */
    @SuppressWarnings("unchecked")
    public Object getInheritedOnlyCFValueCumulative(ICustomFieldEntity entity, String cfCode) {

        if (entity == null) {
            return null;
        }

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        List<Object> cfValues = new ArrayList<>();

        if (accumulateCF) {
            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    Object value = parentCfEntity.getCfAccumulatedValue(cfCode);
                    if (value != null) {
                        cfValues.add(value);
                    }
                }
            }

        } else {
            ICustomFieldEntity[] parentCfEntities = getHierarchyParentCFEntities(entity);
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    if (parentCfEntity.getCfValues() != null) {
                        Object value = parentCfEntity.getCfValue(cfCode);
                        if (value != null) {
                            cfValues.add(value);
                        }
                    }
                }
            }
        }

        if (cfValues.isEmpty()) {
            return null;

        } else if (!(cfValues.get(0) instanceof Map) || cfValues.size() == 0) {
            return cfValues.get(0);

        } else {
            Map<String, Object> valueMap = new LinkedHashMap<>();
            valueMap.putAll((Map<String, Object>) cfValues.get(0));
            for (int i = 1; i < cfValues.size(); i++) {
                Map<String, Object> iterMap = (Map<String, Object>) cfValues.get(i);
                for (Entry<String, Object> mapItem : iterMap.entrySet()) {
                    if (!valueMap.containsKey(mapItem.getKey())) {
                        valueMap.put(mapItem.getKey(), mapItem.getValue());
                    }
                }
            }
            return valueMap;
        }
    }

    /**
     * Get a custom field value for a given entity's parent's and a date. (DOES NOT include a given entity)
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @return Custom field value
     */
    public Object getInheritedOnlyCFValue(ICustomFieldEntity entity, String cfCode, Date date) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, lookup provider from appProvider
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = appProvider;
                } else {
                    parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                }
                Object value = getInheritedCFValue(parentCfEntity, cfCode, date);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Get a custom field value for a given entity or its parent's. If custom field is versionable, a current date will be used to access the value.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public Object getInheritedCFValue(ICustomFieldEntity entity, String cfCode) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            if (entity.getCfAccumulatedValues() != null) {
                Object value = entity.getCfAccumulatedValues().getValue(cfCode);
                if (value != null) {
                    return value;
                }
            }

        } else {

            // Get value without instantiating a default value if value not found
            Object value = entity.getCfValue(cfCode);
            if (value != null) {
                return value;
            }

            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    Object cfeValue = getInheritedCFValue(parentCfEntity, cfCode);
                    if (cfeValue != null) {
                        return cfeValue;
                    }
                }
            }
        }

        // Instantiate default value if applicable
        return instantiateCFWithDefaultValue(entity, cfCode);

    }

    /**
     * Get a custom field value for a given entity or its parent's and a date
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @return Custom field value
     */
    public Object getInheritedCFValue(ICustomFieldEntity entity, String cfCode, Date date) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            Object value = entity.getCfAccumulatedValue(cfCode, date);
            if (value != null) {
                return value;
            }

        } else {

            // Get value without instantiating a default value if value not found
            Object value = entity.getCfValue(cfCode, date);
            if (value != null) {
                return value;
            }

            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    Object cfeValue = getInheritedCFValue(parentCfEntity, cfCode, date);
                    if (cfeValue != null) {
                        return cfeValue;
                    }
                }
            }
        }
        // Instantiate default value if applicable
        return instantiateCFWithDefaultValue(entity, cfCode, date);
    }

    /**
     * Match for a given entity's or its parent's custom field (non-versionable values) as close as possible map's key to the key provided and return a map value. Match is
     * performed by matching a full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public Object getInheritedCFValueByClosestMatch(ICustomFieldEntity entity, String cfCode, String keyToMatch) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            Object value = getCFValueByClosestMatch(entity, cfCode, true, keyToMatch);

            return value;

        } else {
            Object value = getCFValueByClosestMatch(entity, cfCode, false, keyToMatch);
            if (value != null) {
                return value;
            }
            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider as appProvider is not managed
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    Object cfeValue = getInheritedCFValueByClosestMatch(parentCfEntity, cfCode, keyToMatch);
                    if (cfeValue != null) {
                        return cfeValue;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Match for a given date (versionable values) for a given entity's or its parent's custom field as close as possible map's key to the key provided and return a map value.
     * Match is performed by matching a full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     *
     * @param entity Entity to match
     * @param code Custom field code
     * @param date Date
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public Object getInheritedCFValueByClosestMatch(ICustomFieldEntity entity, String code, Date date, String keyToMatch) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            Object value = getCFValueByClosestMatch(entity, code, date, true, keyToMatch);
            return value;

        } else {
            Object value = getCFValueByClosestMatch(entity, code, date, false, keyToMatch);
            if (value != null) {
                return value;
            }
            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {

                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    Object cfeValue = getInheritedCFValueByClosestMatch(parentCfEntity, code, date, keyToMatch);
                    if (cfeValue != null) {
                        return cfeValue;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Match for a given entity's or its parent's custom field (non-versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * &lt;matrix first key&gt;|&lt;matrix second key&gt;|&lt;matrix xx key&gt;|&lt;range of numbers for the third key&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    public Object getInheritedCFValueByKey(ICustomFieldEntity entity, String cfCode, Object... keys) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            Object value = getCFValueByKey(entity, cfCode, true, keys);
            return value;

        } else {
            Object value = getCFValueByKey(entity, cfCode, false, keys);
            if (value != null) {
                return value;
            }
            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    Object cfeValue = getInheritedCFValueByKey(parentCfEntity, cfCode, keys);
                    if (cfeValue != null) {
                        return cfeValue;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Match for a given date (versionable values) for a given entity's or its parent's custom field (versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * &lt;matrix first key&gt;|&lt;matrix second key&gt;|&lt;matrix xx key&gt;|&lt;range of numbers for the third key&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    public Object getInheritedCFValueByKey(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            Object value = getCFValueByKey(entity, cfCode, date, true, keys);
            return value;

        } else {
            Object value = getCFValueByKey(entity, cfCode, date, false, keys);
            if (value != null) {
                return value;
            }
            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    Object cfeValue = getInheritedCFValueByKey(parentCfEntity, cfCode, date, keys);
                    if (cfeValue != null) {
                        return cfeValue;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Match for a given entity's or its parent's custom field (non-versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: &lt;number from&gt;&gt;&lt;number to&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    public Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Object numberToMatch) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            Object value = getCFValueByRangeOfNumbers(entity, cfCode, true, numberToMatch);
            return value;
        } else {
            Object value = getCFValueByRangeOfNumbers(entity, cfCode, false, numberToMatch);
            if (value != null) {
                return value;
            }
            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    Object cfeValue = getInheritedCFValueByRangeOfNumbers(parentCfEntity, cfCode, numberToMatch);
                    if (cfeValue != null) {
                        return cfeValue;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field (versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: &lt;number from&gt;&gt;&lt;number to&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    public Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Date date, Object numberToMatch) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            Object value = getCFValueByRangeOfNumbers(entity, cfCode, date, true, numberToMatch);
            return value;

        } else {
            Object value = getCFValueByRangeOfNumbers(entity, cfCode, date, false, numberToMatch);
            if (value != null) {
                return value;
            }
            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }
                    // If Parent entity is Provider, lookup provider from appProvider
                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    Object cfeValue = getInheritedCFValueByRangeOfNumbers(parentCfEntity, cfCode, date, numberToMatch);
                    if (cfeValue != null) {
                        return cfeValue;
                    }
                }
            }
            return null;
        }
    }

    /**
     * A trigger when a future custom field end period event expired
     *
     * @param timer Timer information
     */
    @Timeout
    private void triggerEndPeriodEventExpired(Timer timer) {
        log.debug("Custom field value period has expired {}", timer);
        try {
            CFEndPeriodEvent event = (CFEndPeriodEvent) timer.getInfo();

            currentUserProvider.forceAuthentication(null, event.getProviderCode());
            cFEndPeriodEventProducer.fire(event);
        } catch (Exception e) {
            log.error("Failed executing end period event timer", e);
        }
    }

    /**
     * Initiate custom field end period event - either right away, or delay it for the future
     *
     * @param cfi Custom field instance
     */
    private void triggerEndPeriodEvent(ICustomFieldEntity entity, String cfCode, DatePeriod period) {

        if (period != null && period.getTo() != null && period.getTo().before(new Date())) {
            CFEndPeriodEvent event = new CFEndPeriodEvent(entity, cfCode, period, currentUser.getProviderCode());
            cFEndPeriodEventProducer.fire(event);

        } else if (period != null && period.getTo() != null) {
            CFEndPeriodEvent event = new CFEndPeriodEvent(entity, cfCode, period, currentUser.getProviderCode());

            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setInfo(event);

            // used for testing
            // expiration = new Date();
            // expiration = DateUtils.addMinutes(expiration, 1);

            log.debug("Creating timer for triggerEndPeriodEvent for Custom field value {} with expiration={}", event, period.getTo());

            timerService.createSingleActionTimer(period.getTo(), timerConfig);
        }
    }

    private IEntity retrieveIfNotManagedAny(IEntity entity) {

        if (entity.isTransient()) {
            return entity;
        }

        if (getEntityManager().contains(entity)) {
            // Entity is managed already, no need to retrieve
            return entity;

        } else {
            //log.trace("Find {}/{} by id", entity.getClass().getSimpleName(), entity.getId());
            entity = getEntityManager().find(PersistenceUtils.getClassForHibernateObject(entity), entity.getId());
            return entity;
        }
    }

    /**
     * Match for a given entity's custom field (non-versionable values) as close as possible map's key to the key provided and return a map value. Match is performed by matching a
     * full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public Object getCFValueByClosestMatch(ICustomFieldEntity entity, String cfCode, String keyToMatch) {
        return getCFValueByClosestMatch(entity, cfCode, false, keyToMatch);
    }

    /**
     * Match for a given entity's custom field (non-versionable values) as close as possible map's key to the key provided and return a map value. Match is performed by matching a
     * full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param accumulated True if accumulated value be checked instead
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    private Object getCFValueByClosestMatch(ICustomFieldEntity entity, String cfCode, boolean accumulated, String keyToMatch) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if ((accumulated && entity.getCfAccumulatedValues() == null) || (!accumulated && entity.getCfValues() == null)) {
            return null;
        }

        Object valueMatched = accumulated ? entity.getCFAccumulatedValueByClosestMatch(cfCode, keyToMatch) : entity.getCFValueByClosestMatch(cfCode, keyToMatch);
        log.trace("Found closest match value {} for keyToMatch={}", valueMatched, keyToMatch);

        // Need to check if it is a multi-value type value and convert it to a map
        if (valueMatched != null && valueMatched instanceof String) {
            CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
            if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
                return cft.deserializeMultiValue((String) valueMatched, null);
            }
        }

        return valueMatched;
    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field as close as possible map's key to the key provided and return a map value. Match is performed
     * by matching a full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public Object getCFValueByClosestMatch(ICustomFieldEntity entity, String cfCode, Date date, String keyToMatch) {
        return getCFValueByClosestMatch(entity, cfCode, date, false, keyToMatch);
    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field as close as possible map's key to the key provided and return a map value. Match is performed
     * by matching a full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date
     * @param accumulated True if accumulated value be checked instead
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    private Object getCFValueByClosestMatch(ICustomFieldEntity entity, String cfCode, Date date, boolean accumulated, String keyToMatch) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if ((accumulated && entity.getCfAccumulatedValues() == null) || (!accumulated && entity.getCfValues() == null)) {
            return null;
        }

        Object valueMatched = accumulated ? entity.getCFAccumulatedValueByClosestMatch(cfCode, date, keyToMatch) : entity.getCFValueByClosestMatch(cfCode, date, keyToMatch);
        log.trace("Found closest match value {} for period {} and keyToMatch={}", valueMatched, date, keyToMatch);

        // Need to check if it is a multi-value type value and convert it to a map
        if (valueMatched != null && valueMatched instanceof String) {
            CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
            if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
                return cft.deserializeMultiValue((String) valueMatched, null);
            }
        }

        return valueMatched;
    }

    /**
     * Match for a given entity's custom field (non-versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * &lt;matrix first key&gt;|&lt;matrix second key&gt;|&lt;matrix xx key&gt;|&lt;range of numbers for the third key&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    public Object getCFValueByKey(ICustomFieldEntity entity, String cfCode, Object... keys) {
        return getCFValueByKey(entity, cfCode, false, keys);
    }

    /**
     * Match for a given entity's custom field (non-versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * &lt;matrix first key&gt;|&lt;matrix second key&gt;|&lt;matrix xx key&gt;|&lt;range of numbers for the third key&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param accumulated True if accumulated value be checked instead
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    @SuppressWarnings("unchecked")
    private Object getCFValueByKey(ICustomFieldEntity entity, String cfCode, boolean accumulated, Object... keys) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if ((accumulated && entity.getCfAccumulatedValues() == null) || (!accumulated && entity.getCfValues() == null)) {
            return null;
        }

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            log.trace("No CFT found {}/{}", entity, cfCode);
            return null;
        }

        if (cft.getStorageType() != CustomFieldStorageTypeEnum.MAP && cft.getStorageType() != CustomFieldStorageTypeEnum.MATRIX) {
            log.trace("getCFValueByKey does not apply to storage type {}", cft.getStorageType());
            return null;
        }
        if (keys.length == 0) {
            log.trace("getCFValueByKey needs at least one key passed");
            return null;
        }

        Map<String, Object> value = accumulated ? (Map<String, Object>) entity.getCfAccumulatedValue(cfCode) : (Map<String, Object>) entity.getCfValue(cfCode);
        if (value == null) {
            return null;
        }
        Object valueMatched = null;
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            valueMatched = CustomFieldInstanceService.matchMatrixValue(cft, value, keys);

        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            if (keys[0] == null) {
                return null;
            }
            if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
                valueMatched = value.get(keys[0].toString());

            } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {
                if (keys[0] instanceof String) {
                    try {
                        keys[0] = Double.parseDouble((String) keys[0]);
                    } catch (NumberFormatException e) {
                        // Don't care about error nothing will be found later
                    }
                }
                valueMatched = CustomFieldInstanceService.matchRangeOfNumbersValue(value, keys[0]);
            }
        }

        log.trace("Found value match {} by keyToMatch={}", valueMatched, keys);

        // Need to check if it is a multi-value type value and convert it to a map
        if (valueMatched != null && valueMatched instanceof String && cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
            return cft.deserializeMultiValue((String) valueMatched, null);
        }

        return valueMatched;

    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field (versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * &lt;matrix first key&gt;|&lt;matrix second key&gt;|&lt;matrix xx key&gt;|&lt;range of numbers for the third key&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    public Object getCFValueByKey(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {
        return getCFValueByKey(entity, cfCode, date, false, keys);
    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field (versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * &lt;matrix first key&gt;|&lt;matrix second key&gt;|&lt;matrix xx key&gt;|&lt;range of numbers for the third key&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param accumulated True if accumulated value be checked instead
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    @SuppressWarnings("unchecked")
    public Object getCFValueByKey(ICustomFieldEntity entity, String cfCode, Date date, boolean accumulated, Object... keys) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if ((accumulated && entity.getCfAccumulatedValues() == null) || (!accumulated && entity.getCfValues() == null)) {
            return null;
        }

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            log.trace("No CFT found {}/{}", entity, cfCode);
            return null;
        }

        if (cft.getStorageType() != CustomFieldStorageTypeEnum.MAP && cft.getStorageType() != CustomFieldStorageTypeEnum.MATRIX) {
            log.trace("getCFValueByKey does not apply to storage type {}", cft.getStorageType());
            return null;
        }
        if (keys.length == 0) {
            log.trace("getCFValueByKey needs at least one key passed");
            return null;
        }

        Map<String, Object> value = accumulated ? (Map<String, Object>) entity.getCfAccumulatedValue(cfCode, date) : (Map<String, Object>) entity.getCfValue(cfCode, date);
        if (value == null) {
            return null;
        }
        Object valueMatched = null;
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            valueMatched = CustomFieldInstanceService.matchMatrixValue(cft, value, keys);

        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            if (keys[0] == null) {
                return null;
            }
            if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
                valueMatched = value.get(keys[0].toString());

            } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {
                if (keys[0] instanceof String) {
                    try {
                        keys[0] = Double.parseDouble((String) keys[0]);
                    } catch (NumberFormatException e) {
                        // Don't care about error nothing will be found later
                    }
                }
                valueMatched = CustomFieldInstanceService.matchRangeOfNumbersValue(value, keys[0]);
            }
        }

        log.trace("Found matrix value match {} for period {} and keyToMatch={}", valueMatched, date, keys);

        // Need to check if it is a multi-value type value and convert it to a map
        if (valueMatched != null && valueMatched instanceof String && cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
            return cft.deserializeMultiValue((String) valueMatched, null);
        }

        return valueMatched;
    }

    /**
     * Match for a given entity's custom field (non-versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: &lt;number from&gt;&gt;&lt;number to&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    public Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Object numberToMatch) {
        return getCFValueByRangeOfNumbers(entity, cfCode, false, numberToMatch);
    }

    /**
     * Match for a given entity's custom field (non-versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: &lt;number from&gt;&gt;&lt;number to&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param accumulated True if accumulated value be checked instead
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    @SuppressWarnings("unchecked")
    public Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, boolean accumulated, Object numberToMatch) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if ((accumulated && entity.getCfAccumulatedValues() == null) || (!accumulated && entity.getCfValues() == null)) {
            return null;
        }

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            log.trace("No CFT found {}/{}", entity, cfCode);
            return null;
        }

        if (!(cft.getStorageType() == CustomFieldStorageTypeEnum.MAP && cft.getMapKeyType() == CustomFieldMapKeyEnum.RON)) {
            log.trace("getCFValueByRangeOfNumbers does not apply to storage type {} and mapKeyType {}", cft.getStorageType(), cft.getMapKeyType());
            return null;
        }

        Map<String, Object> value = accumulated ? (Map<String, Object>) entity.getCfAccumulatedValue(cfCode) : (Map<String, Object>) entity.getCfValue(cfCode);
        Object valueMatched = CustomFieldInstanceService.matchRangeOfNumbersValue(value, numberToMatch);

        log.trace("Found map value match {} for numberToMatch={}", valueMatched, numberToMatch);
        return valueMatched;

    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field (versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: &lt;number from&gt;&gt;&lt;number to&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    public Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Date date, Object numberToMatch) {
        return getCFValueByRangeOfNumbers(entity, cfCode, date, false, numberToMatch);
    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field (versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: &lt;number from&gt;&gt;&lt;number to&gt;
     *
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param accumulated True if accumulated value be checked instead
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    private Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Date date, boolean accumulated, Object numberToMatch) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if ((accumulated && entity.getCfAccumulatedValues() == null) || (!accumulated && entity.getCfValues() == null)) {
            return null;
        }

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            log.trace("No CFT found {}/{}", entity, cfCode);
            return null;
        }

        if (!(cft.getStorageType() == CustomFieldStorageTypeEnum.MAP && cft.getMapKeyType() == CustomFieldMapKeyEnum.RON)) {
            log.trace("getCFValueByRangeOfNumbers does not apply to storage type {} and mapKeyType {}", cft.getStorageType(), cft.getMapKeyType());
            return null;
        }

        Object value = accumulated ? entity.getCfAccumulatedValue(cfCode, date) : entity.getCfValue(cfCode, date);
        Object valueMatched = CustomFieldInstanceService.matchRangeOfNumbersValue(value, numberToMatch);

        log.trace("Found matrix value match {} for period {} and numberToMatch={}", valueMatched, date, numberToMatch);
        return valueMatched;

    }

    /**
     * Match for a given value map's key as the matrix value and return a map value.
     * 
     * Map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * &lt;matrix first key&gt;|&lt;matrix second key&gt;|&lt;range of numbers for the third key&gt;
     *
     * @param cft Custom field template
     * @param value Value to inspect
     * @param filterKeys Keys to match. The order must correspond to the order of the keys during data entry
     * @return A value matched
     */
    @SuppressWarnings("unchecked")
    public static Object matchMatrixValue(CustomFieldTemplate cft, Object value, Object... filterKeys) {
        if (!(value instanceof Map) || filterKeys == null || filterKeys.length == 0) {
            return null;
        }

        Object valueMatched = null;

        for (Entry<String, Object> valueInfo : ((Map<String, Object>) value).entrySet()) {
            String[] matrixKeys = valueInfo.getKey().split("\\" + CustomFieldValue.MATRIX_KEY_SEPARATOR);
            if (matrixKeys.length != filterKeys.length) {
                continue;
            }

            boolean isMatchedAllKeys = true;
            for (int i = 0; i < matrixKeys.length; i++) {
                CustomFieldMatrixColumn matrixColumn = cft.getMatrixColumnByIndex(i);
                if (matrixColumn == null
                        || (matrixColumn.getKeyType() == CustomFieldMapKeyEnum.STRING && !matrixKeys[i].equals(filterKeys[i])
                                && !matrixKeys[i].equals(CustomFieldValue.WILDCARD_MATCH_ALL))
                        || (matrixColumn.getKeyType() == CustomFieldMapKeyEnum.RON && !isNumberRangeMatch(matrixKeys[i], filterKeys[i]))) {
                    isMatchedAllKeys = false;
                    break;
                }
            }

            if (isMatchedAllKeys) {
                valueMatched = valueInfo.getValue();
                break;
            }
        }

        return valueMatched;
    }

    /**
     * Check if a match for a given value map's key as the matrix value is present.
     * 
     * Map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * &lt;matrix first key&gt;|&lt;matrix second key&gt;|&lt;range of numbers for the third key&gt;
     *
     * @param cft Custom field template
     * @param value Value to inspect
     * @param keys Keys to match. The order must correspond to the order of the keys during data entry
     * @return True if a value was matched
     */
    @SuppressWarnings("unchecked")
    public static boolean isMatchMatrixValue(CustomFieldTemplate cft, Object value, Object... keys) {
        if (value == null || !(value instanceof Map) || keys == null || keys.length == 0) {
            return false;
        }

        for (Entry<String, Object> valueInfo : ((Map<String, Object>) value).entrySet()) {
            String[] keysParsed = valueInfo.getKey().split("\\" + CustomFieldValue.MATRIX_KEY_SEPARATOR);
            if (keysParsed.length != keys.length) {
                continue;
            }

            boolean allMatched = true;
            for (int i = 0; i < keysParsed.length; i++) {
                CustomFieldMatrixColumn matrixColumn = cft.getMatrixColumnByIndex(i);
                if (matrixColumn == null || (matrixColumn.getKeyType() == CustomFieldMapKeyEnum.STRING && !keysParsed[i].equals(keys[i]))
                        || (matrixColumn.getKeyType() == CustomFieldMapKeyEnum.RON && !isNumberRangeMatch(keysParsed[i], keys[i]))) {
                    allMatched = false;
                    break;
                }
            }

            if (allMatched) {
                return true;
            }
        }

        return false;
    }

    /**
     * Match map's key as a range of numbers value and return a matched value.
     * 
     * Number ranges is assumed to be the following format: &lt;number from&gt;&lt;&lt;number to&gt;
     *
     * @param value Value to inspect
     * @param numberToMatch Number to match
     * @return Map value that closely matches map key
     */
    @SuppressWarnings("unchecked")
    public static Object matchRangeOfNumbersValue(Object value, Object numberToMatch) {
        if (value == null || !(value instanceof Map) || numberToMatch == null
                || !(numberToMatch instanceof Long || numberToMatch instanceof Integer || numberToMatch instanceof Double || numberToMatch instanceof BigDecimal)) {
            return null;
        }

        for (Entry<String, Object> valueInfo : ((Map<String, Object>) value).entrySet()) {
            if (isNumberRangeMatch(valueInfo.getKey(), numberToMatch)) {
                return valueInfo.getValue();
            }
        }

        return null;
    }

    /**
     * Check if a match map's key as a range of numbers value is present.
     * 
     * Number ranges is assumed to be the following format: &lt;number from&gt;&lt;&lt;number to&gt;
     *
     * @param value Value to inspect
     * @param numberToMatch Number to match
     * @return True if map value matches map key
     */
    @SuppressWarnings("unchecked")
    public static boolean isMatchRangeOfNumbersValue(Object value, Object numberToMatch) {
        if (value == null || !(value instanceof Map) || numberToMatch == null
                || !(numberToMatch instanceof Long || numberToMatch instanceof Integer || numberToMatch instanceof Double || numberToMatch instanceof BigDecimal)) {
            return false;
        }

        for (Entry<String, Object> valueInfo : ((Map<String, Object>) value).entrySet()) {
            if (isNumberRangeMatch(valueInfo.getKey(), numberToMatch)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determine if a number value is inside the number range expressed as &lt;number from&gt;&lt;&lt;number to&gt;.
     *
     * @param numberRange Number range value
     * @param numberToMatchObj A double number o
     * @return True if number have matched
     */
    private static boolean isNumberRangeMatch(String numberRange, Object numberToMatchObj) {
        if (numberToMatchObj == null) {
            return false;
        }

        if (numberRange.equals(CustomFieldValue.WILDCARD_MATCH_ALL)) {
            return true;
        }

        String[] rangeInfo = numberRange.split(CustomFieldValue.RON_VALUE_SEPARATOR);
        Double fromNumber = null;
        try {
            fromNumber = Double.parseDouble(rangeInfo[0]);
        } catch (NumberFormatException e) { // Ignore the error as value might be empty
        }
        Double toNumber = null;
        if (rangeInfo.length == 2) {
            try {
                toNumber = Double.parseDouble(rangeInfo[1]);
            } catch (NumberFormatException e) { // Ignore the error as value might be empty
            }
        }

        // Convert matching number to Double for further comparison
        Double numberToMatchDbl = null;
        if (numberToMatchObj instanceof Double) {
            numberToMatchDbl = (Double) numberToMatchObj;

        } else if (numberToMatchObj instanceof Integer) {
            numberToMatchDbl = ((Integer) numberToMatchObj).doubleValue();

        } else if (numberToMatchObj instanceof Long) {
            numberToMatchDbl = ((Long) numberToMatchObj).doubleValue();

        } else if (numberToMatchObj instanceof BigDecimal) {
            numberToMatchDbl = ((BigDecimal) numberToMatchObj).doubleValue();

        } else if (numberToMatchObj instanceof String) {
            try {
                numberToMatchDbl = Double.parseDouble(((String) numberToMatchObj));

            } catch (NumberFormatException e) {
                Logger log = LoggerFactory.getLogger(CustomFieldInstanceService.class);
                log.error("Failed to match CF value for a range of numbers. Value passed is not a number {} {}", numberToMatchObj,
                    numberToMatchObj != null ? numberToMatchObj.getClass() : null);
                return false;
            }

        } else {
            Logger log = LoggerFactory.getLogger(CustomFieldInstanceService.class);
            log.error("Failed to match CF value for a range of numbers. Value passed is not a number {} {}", numberToMatchObj,
                numberToMatchObj != null ? numberToMatchObj.getClass() : null);
            return false;
        }

        if (fromNumber != null && toNumber != null) {
            if (fromNumber.compareTo(numberToMatchDbl) <= 0 && toNumber.compareTo(numberToMatchDbl) > 0) {
                return true;
            }
        } else if (fromNumber != null) {
            if (fromNumber.compareTo(numberToMatchDbl) <= 0) {
                return true;
            }
        } else if (toNumber != null) {
            if (toNumber.compareTo(numberToMatchDbl) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Instantiate a custom field value with default value for a given entity. If custom field is versionable, a current date will be used to access the value.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public Object instantiateCFWithDefaultValue(ICustomFieldEntity entity, String cfCode) {

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            // log.trace("No CFT found or no default value specified {}/{}", entity, code);
            return null;
        }

        return instantiateCFWithDefaultValue(entity, cft);
    }

    /**
     * Instantiate a custom field value with default or inherited value for a given entity. If custom field is versionable, a current date will be used to access the value.
     *
     * @param entity Entity
     * @param cft Custom field definition
     * @return Custom field value
     */
    public Object instantiateCFWithInheritedOrDefaultValue(ICustomFieldEntity entity, CustomFieldTemplate cft) {

        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
            entity = providerService.findById(appProvider.getId());
        }

        if (cft.isUseInheritedAsDefaultValue()) {
            Object value = getInheritedOnlyCFValue(entity, cft.getCode());
            if (value != null) {
                try {
                    if (cft.isVersionable()) {
                        setCFValue(entity, cft.getCode(), value, new Date());
                    } else {
                        setCFValue(entity, cft.getCode(), value);
                    }
                    return value;
                } catch (BusinessException e) {
                    log.error("Failed to instantiate field with inherited value as default value {}/{}", entity.getClass().getSimpleName(), cft.getCode(), e);
                }
            }
        }

        return instantiateCFWithDefaultValue(entity, cft.getCode());
    }

    /**
     * Instantiate a custom field value with default value for a given entity and a date.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @return Custom field value
     */
    private Object instantiateCFWithDefaultValue(ICustomFieldEntity entity, String cfCode, Date date) {

        // If field is not versionable - get the value without the date
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            // log.trace("No CFT found or no default value or calendar specified {}/{}", entity, code);
            return null;
        }

        return instantiateCFWithDefaultValue(entity, cft, date);
    }

    /**
     * Instantiate all custom fields value with default value for a given entity. If custom field is versionable, a current date will be used to access the value. Can be
     * instantiated only if cft.applicableOnEl condition pass
     *
     * @param entity Entity
     */
    public void instantiateCFWithDefaultValue(ICustomFieldEntity entity) {
        Map<String, CustomFieldTemplate> cfts = cfTemplateService.findByAppliesTo(entity);
        if (cfts != null && !cfts.isEmpty()) {
            for (CustomFieldTemplate cft : cfts.values()) {
                instantiateCFWithDefaultValue(entity, cft);
            }
        }
    }
    
    /**
     * Just like {@link #instantiateCFWithDefaultValue(ICustomFieldEntity)} but checking if CF value is null before instantiating default value. 
     * @param jobInstance
     */
    public void instantiateCFWithDefaultValueIfNull(ICustomFieldEntity entity) {
    	Map<String, CustomFieldTemplate> cfts = cfTemplateService.findByAppliesTo(entity);
        if (MapUtils.isNotEmpty(cfts)) {
            for (CustomFieldTemplate cft : cfts.values()) {
            	Object cfValue = entity.getCfValue(cft.getCode());
            	if (cfValue == null) {
            		instantiateCFWithDefaultValue(entity, cft);
            	}
            }
        }
	}

    /**
     * Instantiate a custom field value with default value for a given entity. If custom field is versionable, a current date will be used to access the value. Can be instantiated
     * only if cft.applicableOnEl condition pass
     *
     * @param entity Entity
     * @param cft Custom field template
     * @return Custom field value
     */
    private Object instantiateCFWithDefaultValue(ICustomFieldEntity entity, CustomFieldTemplate cft) {

        Object value = cft.getDefaultValueConverted();

        if (value == null || StringUtils.isEmpty(value.toString()) || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE) {
            // log.trace("No CFT found or no default value specified {}/{}", entity, cft.getCode());
            return null;
        }

        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
            entity = providerService.findById(appProvider.getId());
        }

        if (!isCFTApplicableToEntity(cft, entity)) {
            // log.trace("No CFT found or no default value specified {}/{}", entity, cft.getCode());
            return null;
        }

        if (cft.isVersionable()) {
            log.warn("Trying to instantiate CF value from default value on a versionable custom field {}/{} value with no provided date. Current date will be used",
                entity.getClass().getSimpleName(), cft.getCode());
            return instantiateCFWithDefaultValue(entity, cft, new Date());
        }

        // Create such CF with default value if one is specified on CFT
        entity.getCfValuesNullSafe().setValue(cft.getCode(), value);

        return value;
    }

    /**
     * Instantiate a custom field value with default value for a given entity and a date. Can be instantiated only if values are versioned by a calendar and cft.applicableOnEl
     * condition pass
     *
     * @param entity Entity
     * @param cft Custom field template
     * @param date Date
     * @return Custom field value
     */
    private Object instantiateCFWithDefaultValue(ICustomFieldEntity entity, CustomFieldTemplate cft, Date date) {

        Object value = cft.getDefaultValueConverted();

        if (value == null || StringUtils.isEmpty(value.toString()) || cft.getCalendar() == null || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE) {
            // log.trace("No CFT found or no default value or calendar specified {}/{}", entity, code);
            return null;
        }

        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
            entity = providerService.findById(appProvider.getId());
        }

        if (!isCFTApplicableToEntity(cft, entity)) {
            // log.trace("No CFT found or no default value or calendar specified {}/{}", entity, code);
            return null;
        }

        // If field is not versionable - instantiate the value without the date
        if (!cft.isVersionable()) {
            return instantiateCFWithDefaultValue(entity, cft);
        }

        entity.setCfValue(cft.getCode(), cft.getDatePeriod(date), null, value);

        return value;
    }

    /**
     * Check if a given entity has a CF value of type Map or Matrix with a given key.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key
     */
    public boolean isCFValueHasKey(ICustomFieldEntity entity, String cfCode, Object... keys) {
        return isCFValueHasKey(entity, cfCode, false, keys);
    }

    /**
     * Check if a given entity has a CF value of type Map or Matrix with a given key.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key
     */
    @SuppressWarnings("unchecked")
    private boolean isCFValueHasKey(ICustomFieldEntity entity, String cfCode, boolean accumulated, Object... keys) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if ((accumulated && entity.getCfAccumulatedValues() == null) || (!accumulated && entity.getCfValues() == null)) {
            return false;
        }

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            log.trace("No CFT found {}/{}", entity, cfCode);
            return false;
        }

        if (cft.getStorageType() != CustomFieldStorageTypeEnum.MAP && cft.getStorageType() != CustomFieldStorageTypeEnum.MATRIX) {
            log.trace("isCFValueHasKey does not apply to storage type {}", cft.getStorageType());
            return false;
        }
        if (keys.length == 0) {
            log.trace("isCFValueHasKey needs at least one key passed");
            return false;
        }

        Map<String, Object> value = accumulated ? (Map<String, Object>) entity.getCfAccumulatedValue(cfCode) : (Map<String, Object>) entity.getCfValue(cfCode);
        if (value == null) {
            return false;
        }
        boolean hasKey = false;
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            hasKey = CustomFieldInstanceService.isMatchMatrixValue(cft, value, keys);

        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            if (keys[0] == null) {
                return false;
            }
            if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
                hasKey = value.containsKey(keys[0].toString());

            } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {
                if (keys[0] instanceof String) {
                    try {
                        keys[0] = Double.parseDouble((String) keys[0]);
                    } catch (NumberFormatException e) {
                        // Don't care about error nothing will be found later
                    }
                }
                hasKey = CustomFieldInstanceService.isMatchRangeOfNumbersValue(value, keys[0]);
            }
        }
        log.trace("Value match {} for keyToMatch={}", hasKey, keys);
        return hasKey;
    }

    /**
     * Check if a given entity at a given period date has a CF value of type Map or Matrix with a given key.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key at a given period date
     */
    public boolean isCFValueHasKey(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {
        return isCFValueHasKey(entity, cfCode, date, false, keys);
    }

    /**
     * Check if a given entity at a given period date has a CF value of type Map or Matrix with a given key.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @param accumulated True if accumulated value be checked instead
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key at a given period date
     */
    @SuppressWarnings("unchecked")
    private boolean isCFValueHasKey(ICustomFieldEntity entity, String cfCode, Date date, boolean accumulated, Object... keys) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if ((accumulated && entity.getCfAccumulatedValues() == null) || (!accumulated && entity.getCfValues() == null)) {
            return false;
        }

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            log.trace("No CFT found {}/{}", entity, cfCode);
            return false;
        }

        if (cft.getStorageType() != CustomFieldStorageTypeEnum.MAP && cft.getStorageType() != CustomFieldStorageTypeEnum.MATRIX) {
            log.trace("isCFValueHasKey does not apply to storage type {}", cft.getStorageType());
            return false;
        }
        if (keys.length == 0) {
            log.trace("isCFValueHasKey needs at least one key passed");
            return false;
        }

        Map<String, Object> value = accumulated ? (Map<String, Object>) entity.getCfAccumulatedValue(cfCode, date) : (Map<String, Object>) entity.getCfValue(cfCode, date);
        if (value == null) {
            return false;
        }
        boolean hasKey = false;
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            hasKey = CustomFieldInstanceService.isMatchMatrixValue(cft, value, keys);

        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            if (keys[0] == null) {
                return false;
            }
            if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
                hasKey = value.containsKey(keys[0].toString());

            } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {
                if (keys[0] instanceof String) {
                    try {
                        keys[0] = Double.parseDouble((String) keys[0]);
                    } catch (NumberFormatException e) {
                        // Don't care about error nothing will be found later
                    }
                }
                hasKey = CustomFieldInstanceService.isMatchRangeOfNumbersValue(value, keys[0]);
            }

        }
        log.trace("Value match {} for date for keyToMatch={}", hasKey, date, keys);
        return hasKey;
    }

    /**
     * Check if a given entity or its parents have a CF value of type Map or Matrix with a given key.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key
     */
    public boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String cfCode, Object... keys) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            boolean hasKey = isCFValueHasKey(entity, cfCode, true, keys);
            return hasKey;

        } else {
            boolean hasKey = isCFValueHasKey(entity, cfCode, false, keys);
            if (hasKey) {
                return true;
            }

            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }

                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }

                    hasKey = isInheritedCFValueHasKey(parentCfEntity, cfCode, keys);
                    if (hasKey) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Check if a given entity or its parents at a given perio date have a CF value of type Map or Matrix with a given key.
     *
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key at a given perio date
     */
    public boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {

//        // Handle cases when appProvider was passed instead of a real Provider entity. The class in this case is org.meveo.model.crm.Provider$Proxy$_$$_WeldClientProxy
//        if (entity instanceof Provider && entity.getClass().getSimpleName().contains("Proxy")) {
//            entity = providerService.findById(appProvider.getId());
//        }

        if (accumulateCF) {
            boolean hasKey = isCFValueHasKey(entity, cfCode, date, true, keys);
            return hasKey;
        } else {
            boolean hasKey = isCFValueHasKey(entity, cfCode, date, false, keys);
            if (hasKey) {
                return true;
            }

            ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
            if (parentCfEntities != null) {
                for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                    if (parentCfEntity == null) {
                        continue;
                    }

                    if (parentCfEntity instanceof Provider) {
                        parentCfEntity = appProvider;
                    } else {
                        parentCfEntity = (ICustomFieldEntity) retrieveIfNotManagedAny((IEntity) parentCfEntity);
                    }
                    hasKey = isInheritedCFValueHasKey(parentCfEntity, cfCode, date, keys);
                    if (hasKey) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Deprecated. See getCFValueByKey function
     *
     * @param entity custom field entity
     * @param cfCode custom field code
     * @param keys list of key
     * @return custom field value.
     */
    @Deprecated
    public Object getCFValueByMatrix(ICustomFieldEntity entity, String cfCode, Object... keys) {
        return getCFValueByKey(entity, cfCode, keys);
    }

    /**
     * Deprecated. See getCFValueByKey function
     *
     * @param entity custom field entity
     * @param cfCode custom field code
     * @param date date to check
     * @param keys list of key
     * @return custom field value.
     */
    @Deprecated
    public Object getCFValueByMatrix(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {
        return getCFValueByKey(entity, cfCode, date, keys);
    }

    /**
     * Deprecated. See getInheritedCFValueByKey function
     *
     * @param entity custom field entity
     * @param cfCode custom field code
     * @param keys list of key
     * @return custom field value.
     */
    @Deprecated
    public Object getInheritedCFValueByMetrix(ICustomFieldEntity entity, String cfCode, Object... keys) {
        return getInheritedCFValueByKey(entity, cfCode, keys);
    }

    /**
     * Deprecated. See getInheritedCFValueByKey function
     *
     * @param entity custom field entity
     * @param cfCode custom field code
     * @param date date to check
     * @param keys list of key
     * @return custom field value.
     */
    @Deprecated
    public Object getInheritedCFValueByMatrix(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {
        return getInheritedCFValueByKey(entity, cfCode, date, keys);
    }

    /**
     * Check if Custom field template is applicable to a given entity - evaluate cft.applicableOnEl expression is set
     *
     * @param cft Custom field template
     * @param entity Entity to check
     * @return True if cft.applicableOnEl expression is null or evaluates to true
     */
    private boolean isCFTApplicableToEntity(CustomFieldTemplate cft, ICustomFieldEntity entity) {
        if (cft.getApplicableOnEl() != null) {
            return ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity);
        }
        return true;
    }

    /**
     * Schedule end period events for an entity if applicable.
     *
     * @param entity Entity
     */
    public void scheduleEndPeriodEvents(ICustomFieldEntity entity) {

        if (entity.getCfValues() == null) {
            return;
        }

        Map<String, List<DatePeriod>> newCfValuePeriods = entity.getCfValues().getNewVersionedCFValuePeriods();
        if (newCfValuePeriods == null || newCfValuePeriods.isEmpty()) {
            return;
        }

        for (Entry<String, List<DatePeriod>> periodInfo : newCfValuePeriods.entrySet()) {
            CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(periodInfo.getKey(), entity);
            if (cft != null && cft.isTriggerEndPeriodEvent()) {
                for (DatePeriod period : periodInfo.getValue()) {
                    triggerEndPeriodEvent(entity, periodInfo.getKey(), period);
                }
            }
        }
    }

    private EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }
}