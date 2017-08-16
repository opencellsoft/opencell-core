package org.meveo.service.crm.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.context.Conversation;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.event.CFEndPeriodEvent;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.BaseService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.PersistenceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Stateless
public class CustomFieldInstanceService extends BaseService {

    @Inject
    private CustomFieldTemplateService cfTemplateService;

    @Inject
    private Event<CFEndPeriodEvent> cFEndPeriodEvent;

    @Resource
    private TimerService timerService;

    @Inject
    private ProviderService providerService;

    @Inject
    @MeveoJpa
    private EntityManager em;

    @Inject
    @MeveoJpaForJobs
    private EntityManager emfForJobs;

    @Inject
    private Conversation conversation;

    private ParamBean paramBean = ParamBean.getInstance();

    // Previous comments
    // /**
    // * Convert BusinessEntityWrapper to an entity by doing a lookup in DB
    // *
    // * @param businessEntityWrapper Business entity information
    // * @return A BusinessEntity object
    // */
    // @SuppressWarnings("unchecked")
    // public BusinessEntity convertToBusinessEntityFromCfV(EntityReferenceWrapper businessEntityWrapper) {
    // if (businessEntityWrapper == null) {
    // return null;
    // }
    // Query query = getEntityManager().createQuery("select e from " + businessEntityWrapper.getClassname() + " e where e.code=:code ");
    // query.setParameter("code", businessEntityWrapper.getCode());
    // query;
    // List<BusinessEntity> entities = query.getResultList();
    // if (entities.size() > 0) {
    // return entities.get(0);
    // } else {
    // return null;
    // }
    // }

    /**
     * Find a list of entities of a given class and matching given code. In case classname points to CustomEntityTemplate, find CustomEntityInstances of a CustomEntityTemplate code
     * 
     * @param classNameAndCode Classname to match. In case of CustomEntityTemplate, classname consist of "CustomEntityTemplate - <CustomEntityTemplate code>"
     * @param wildcode Filter by entity code
     * @return A list of entities
     */
    @SuppressWarnings("unchecked") // TODO review location
    public List<BusinessEntity> findBusinessEntityForCFVByCode(String classNameAndCode, String wildcode) {
        Query query = null;
        if (classNameAndCode.startsWith(CustomEntityTemplate.class.getName())) {
            String cetCode = CustomFieldTemplate.retrieveCetCode(classNameAndCode);
            query = getEntityManager().createQuery("select e from CustomEntityInstance e where cetCode=:cetCode and lower(e.code) like :code");
            query.setParameter("cetCode", cetCode);

        } else {
            query = getEntityManager().createQuery("select e from " + classNameAndCode + " e where lower(e.code) like :code");
        }

        query.setParameter("code", "%" + wildcode.toLowerCase() + "%");
        List<BusinessEntity> entities = query.getResultList();
        return entities;
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
     * @throws BusinessException
     */
    public Object getOrCreateCFValueFromParamValue(String cfCode, String defaultParamBeanValue, ICustomFieldEntity entity, boolean saveInCFIfNotExist) throws BusinessException {

        Object value = getCFValue(entity, cfCode, true);
        if (value != null) {
            return value;
        }

        // If value is not found, create a new Custom field with a value taken from configuration parameters
        value = paramBean.getProperty(cfCode, defaultParamBeanValue);
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

        if (cft.isVersionable()) {
            log.warn("Trying to access a versionable custom field {}/{} value with no provided date. Current date will be used", entity.getClass().getSimpleName(), cfCode);
            return getCFValue(entity, cfCode, new Date(), instantiateDefaultValue);
        }

        Object value = null;
        if (entity.getCfValues() != null) {
            value = entity.getCfValues().getValue(cfCode);
        }

        // Create such CF with default value if one is specified on CFT and other conditions match
        if (value == null && instantiateDefaultValue) {
            value = instantiateCFWithDefaultValue(cft, entity);
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
     * Get a custom field value for a given entity and a date
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
        if (!cft.isVersionable()) {
            return getCFValue(entity, cfCode, instantiateDefaultValue);
        }

        Object value = null;
        if (entity.getCfValues() != null) {
            value = entity.getCfValues().getValue(cfCode, date);
        }

        // Create such CF with default value if one is specified on CFT and other conditions match
        if (value == null && instantiateDefaultValue) {
            value = instantiateCFWithDefaultValue(cft, entity, date);
        }

        return value;
    }

    public String getCFValuesAsJson(ICustomFieldEntity entity) {
        return getCFValuesAsJson(entity, false);
    }

    /**
     * Get custom field values of an entity as JSON string
     * 
     * @param entity Entity
     * @param includeParent include parentCFEntities or not
     * @return JSON format string
     */
    public String getCFValuesAsJson(ICustomFieldEntity entity, boolean includeParent) {

        String result = "";
        String sep = "";
        Map<String, CustomFieldTemplate> cfts = null;

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

        return result;
    }

    public Element getCFValuesAsDomElement(ICustomFieldEntity entity, Document doc) {
        return getCFValuesAsDomElement(entity, doc, false);
    }

    public Element getCFValuesAsDomElement(ICustomFieldEntity entity, Document doc, boolean includeParent) {
        Element customFieldsTag = doc.createElement("customFields");

        if (entity.getCfValues() == null) {
            return customFieldsTag;
        }

        Map<String, CustomFieldTemplate> cfts = cfTemplateService.findByAppliesTo(entity);

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

        return customFieldsTag;
    }

    /**
     * Set a Custom field value on an entity
     * 
     * @param entity Entity
     * @param cfCode Custom field value code
     * @param value Value to set
     * 
     * @throws BusinessException
     */
    public CustomFieldValue setCFValue(ICustomFieldEntity entity, String cfCode, Object value) throws BusinessException {

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

        CustomFieldValue cfValue = null;
        if (entity.getCfValues() != null) {
            cfValue = entity.getCfValues().getCfValue(cfCode);
        }
        // No existing CF value. Create CF value with new value. Assign(persist) NULL value only if cft.defaultValue is present
        if (cfValue == null) {
            if (value == null && cft.getDefaultValue() == null) {
                return null;
            }
            cfValue = entity.getCfValuesNullSafe().setValue(cfCode, value);

            // Existing CFI found. Update with new value or NULL value only if cft.defaultValue is present
        } else if (value != null || (value == null && cft.getDefaultValue() != null)) {
            cfValue.setValue(value);

            // Existing CF value found, but new value is null, so remove CF value all together
        } else {
            entity.getCfValues().removeValue(cfCode);
            return null;
        }
        return cfValue;
    }

    public CustomFieldValue setCFValue(ICustomFieldEntity entity, String cfCode, Object value, Date valueDate) throws BusinessException {

        log.debug("Setting CF value. Code: {}, entity {} value {} valueDate {}", cfCode, entity, value, valueDate);

        // If field is not versionable - set the value without the date
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            throw new BusinessException("Custom field template with code " + cfCode + " not found found for entity " + entity);
        }

        if (!cft.isVersionable()) {
            setCFValue(entity, cfCode, value);

            // Calendar is needed to be able to set a value with a single date
        } else if (cft.getCalendar() == null) {
            log.error("Can not determine a period for Custom Field {}/{} value if no calendar is provided", entity.getClass().getSimpleName(), cfCode);
            throw new RuntimeException("Can not determine a period for Custom Field " + entity.getClass().getSimpleName() + "/" + cfCode + " value if no calendar is provided");
        }

        // Should not match more then one record as periods are calendar based
        CustomFieldValue cfValue = null;
        if (entity.getCfValues() != null) {
            cfValue = entity.getCfValues().getCfValue(cfCode, valueDate);
        }
        // No existing CF value. Create CF value with new value. Persist NULL value only if cft.defaultValue is present
        if (cfValue == null) {
            if (value == null && cft.getDefaultValue() == null) {
                return null;
            }
            entity.getCfValuesNullSafe().setValue(cfCode, cft.getDatePeriod(valueDate), null, value);

            // Existing CFI found. Update with new value or NULL value only if cft.defaultValue is present
        } else if (value != null || (value == null && cft.getDefaultValue() != null)) {
            cfValue.setValue(value);

            // Existing CFI found, but new value is null, so remove CFI
        } else {
            entity.getCfValues().removeValue(cfCode, valueDate);
            return null;
        }

        return cfValue;
    }

    public CustomFieldValue setCFValue(ICustomFieldEntity entity, String cfCode, Object value, Date valueDateFrom, Date valueDateTo, Integer valuePriority)
            throws BusinessException {

        log.debug("Setting CF value. Code: {}, entity {} value {} valueDateFrom {} valueDateTo {}", cfCode, entity, value, valueDateFrom, valueDateTo);

        // If field is not versionable - set the value without the date
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            throw new BusinessException("Custom field template with code " + cfCode + " not found found for entity " + entity);
        }

        if (!cft.isVersionable()) {
            setCFValue(entity, cfCode, value);

            // If calendar is provided - use calendar by the valueDateFrom date
        } else if (cft.getCalendar() != null) {
            log.warn(
                "Calendar is provided in Custom Field template {}/{} while trying to assign value period start and end dates with two values. Only start date will be considered",
                entity.getClass().getSimpleName(), cfCode);
            setCFValue(entity, cfCode, value, valueDateFrom);
        }

        // Should not match more then one record, as match is strict
        CustomFieldValue cfValue = null;
        if (entity.getCfValues() != null) {
            cfValue = entity.getCfValues().getCfValue(cfCode, valueDateFrom, valueDateTo);
        }
        // No existing CF value. Create CF value with new value. Persist NULL value only if cft.defaultValue is present
        if (cfValue == null) {
            if (value == null && cft.getDefaultValue() == null) {
                return null;
            }
            entity.getCfValuesNullSafe().setValue(cfCode, new DatePeriod(valueDateFrom, valueDateTo), valuePriority, value);

            // Existing CF value found. Update with new value or NULL value only if cft.defaultValue is present
        } else if (value != null || (value == null && cft.getDefaultValue() != null)) {
            cfValue.setValue(value);

            // Existing CF value found, but new value is null, so remove CF value
        } else {
            entity.getCfValues().removeValue(cfCode, valueDateFrom, valueDateTo);
            return null;
        }

        return cfValue;
    }

    /**
     * Remove Custom field instance
     * 
     * @param cfCode Custom field code to remove
     */
    public void removeCFValue(ICustomFieldEntity entity, String cfCode) throws BusinessException {
        if (entity.getCfValues() != null) {
            entity.getCfValues().removeValue(cfCode);
        }
    }

    /**
     * Remove all custom field values for a given entity
     * 
     * @param entity
     */
    public void removeCFValues(ICustomFieldEntity entity) throws BusinessException {
        entity.clearCfValues();
    }
    //
    // /**
    // * Get All custom field instances for a given entity and his parentCFEntities.
    // *
    // * @param entity Entity
    // * @param includeParent flag to include parentCFEntities or not
    // * @return A map of Custom field instances with CF code as a key
    // */
    // public Map<String, List<CustomFieldInstance>> getCustomFieldInstances(ICustomFieldEntity entity, boolean includeParent) {
    // Map<String, List<CustomFieldInstance>> cfisAsMap = new HashMap<String, List<CustomFieldInstance>>();
    // if (includeParent && entity.getParentCFEntities() != null) {
    // for (ICustomFieldEntity entityParent : entity.getParentCFEntities()) {
    // cfisAsMap.putAll(getCustomFieldInstances(entityParent));
    // }
    // }
    // cfisAsMap.putAll(getCustomFieldInstances(entity));
    // return cfisAsMap;
    // }
    //
    // /**
    // * Get All custom field instances for a given entity.
    // *
    // * @param entity Entity
    // * @return A map of Custom field instances with CF code as a key
    // */
    // public Map<String, List<CustomFieldInstance>> getCustomFieldInstances(ICustomFieldEntity entity) {
    // // No longer checking for isTransient as for offer new version creation, CFs are duplicated, but entity is not persisted, ofering to review it in GUI before saving it.
    // // if (((IEntity) entity).isTransient()) {
    // // return new HashMap<String, List<CustomFieldInstance>>();
    // // }
    //
    // TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByEntity", CustomFieldInstance.class);
    // query.setParameter("appliesToEntity", entity.getUuid());
    //
    // List<CustomFieldInstance> cfis = query.getResultList();
    //
    // // // Make sure that embedded CF value property is not null
    // // if (cfi != null && cfi.getCfValue() == null) {
    // // cfi.setCfValue(new CustomFieldValue());
    // // }
    //
    // Map<String, List<CustomFieldInstance>> cfisAsMap = new HashMap<String, List<CustomFieldInstance>>();
    //
    // for (CustomFieldInstance cfi : cfis) {
    // cfisAsMap.putIfAbsent(cfi.getCode(), new ArrayList<CustomFieldInstance>());
    // cfisAsMap.get(cfi.getCode()).add(cfi);
    // }
    //
    // return cfisAsMap;
    // }
    //
    // /**
    // * Get custom field instances for a given entity. Should be only a single record when custom field is not versioned
    // *
    // * @param entity Entity
    // * @param code Custom field code
    // * @return Custom field instance
    // */
    // public List<CustomFieldInstance> getCustomFieldInstances(ICustomFieldEntity entity, String code) {
    //
    // TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByCode", CustomFieldInstance.class);
    // query.setParameter("appliesToEntity", entity.getUuid());
    // query.setParameter("code", code);
    //
    // List<CustomFieldInstance> cfis = query.getResultList();
    //
    // // // Make sure that embedded CF value property is not null
    // // if (cfi != null && cfi.getCfValue() == null) {
    // // cfi.setCfValue(new CustomFieldValue());
    // // }
    //
    // return cfis;
    // }
    //
    // /**
    // * Get custom field instances for a given entity and a given date.
    // *
    // * @param entity Entity
    // * @param code Custom field code
    // * @return Custom field instance
    // */
    // private List<CustomFieldInstance> getCustomFieldInstances(ICustomFieldEntity entity, String code, Date date) {
    //
    // TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByCodeAndDate", CustomFieldInstance.class);
    // query.setParameter("appliesToEntity", entity.getUuid());
    // query.setParameter("code", code);
    // query.setParameter("date", date);
    //
    // List<CustomFieldInstance> cfis = query.getResultList();
    //
    // // // Make sure that embedded CF value property is not null
    // // if (cfi != null && cfi.getCfValue() == null) {
    // // cfi.setCfValue(new CustomFieldValue());
    // // }
    //
    // return cfis;
    // }
    //
    // /**
    // * Get custom field instances for a given entity and a given date.
    // *
    // * @param entity Entity
    // * @param code Custom field code
    // * @param valueDateFrom Value period data range - from
    // * @param valueDateTo Value period data range - to
    // * @return
    // */
    // private List<CustomFieldInstance> getCustomFieldInstances(ICustomFieldEntity entity, String code, Date valueDateFrom, Date valueDateTo) {
    //
    // TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByCodeAndDateRange", CustomFieldInstance.class);
    // query.setParameter("appliesToEntity", entity.getUuid());
    // query.setParameter("code", code);
    // query.setParameter("dateFrom", valueDateFrom);
    // query.setParameter("dateTo", valueDateTo);
    //
    // List<CustomFieldInstance> cfis = query.getResultList();
    //
    // // // Make sure that embedded CF value property is not null
    // // if (cfi != null && cfi.getCfValue() == null) {
    // // cfi.setCfValue(new CustomFieldValue());
    // // }
    //
    // return cfis;
    // }

    /**
     * Get a custom field value for a given entity's parent's. If custom field is versionable, a current date will be used to access the value.
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public Object getInheritedOnlyCFValue(ICustomFieldEntity entity, String cfCode) {
        ICustomFieldEntity[] parentCFEntities = entity.getParentCFEntities();
        if (parentCFEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCFEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
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
     * Get a a list of custom field CFvalues for a given entity's parent's. NOTE: retrieves only the first parent up - does not climb up the hierarchy as getInheritedOnlyCFValue
     * does.
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @return A list of Custom field CFvalues
     */
    public List<CustomFieldValue> getInheritedVersionableOnlyCFValue(ICustomFieldEntity entity, String code) {
        ICustomFieldEntity[] parentCFEntities = entity.getParentCFEntities();
        if (parentCFEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCFEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                if (parentCfEntity.getCfValues() != null) {
                    List<CustomFieldValue> value = parentCfEntity.getCfValues().getValuesByCode().get(code);
                    if (value != null) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check if give entity's parent has any custom field value defined (in any period for versionable fields)
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @return True if any of entity's CF parents have value for a given custom field (in any period for versionable fields)
     */
    public boolean hasInheritedOnlyCFValue(ICustomFieldEntity entity, String cfCode) {
        ICustomFieldEntity[] parentCFEntities = entity.getParentCFEntities();
        if (parentCFEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCFEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
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
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                hasValue = hasInheritedCFValue(parentCfEntity, cfCode);
                if (hasValue) {
                    return true;
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

        if (entity.getCfValues() == null) {
            return false;
        }
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null) {
            // log.trace("No CFT found {}/{}", entity, code);
            return false;
        }

        return entity.getCfValues().hasCfValue(cfCode);

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
            // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
            if (parentCfEntity instanceof Provider) {
                parentCfEntity = providerService.findById(appProvider.getId());
            } else {
                parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
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
     * Get a cumulative and unique custom field value for a given entity's all parent chain. Applies to Map (matrix) values only. The closest parent entity's CF value will be
     * preserved. If custom field is versionable, a current date will be used to access the value.
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

        List<Object> cfValues = new ArrayList<>();

        ICustomFieldEntity[] parentCfEntities = getHierarchyParentCFEntities(entity);
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                if (parentCfEntity.getCfValues() != null) {
                    Object value = parentCfEntity.getCfValues().getValue(cfCode);
                    if (value != null) {
                        cfValues.add(value);
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
     * Get a custom field value for a given entity's parent's and a date
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @return Custom field value
     */
    public Object getInheritedOnlyCFValue(ICustomFieldEntity entity, String cfCode, Date date) {

        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
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

        // Get value without instantiating a default value if value not found
        if (entity.getCfValues() != null) {
            Object value = entity.getCfValues().getValue(cfCode);
            if (value != null) {
                return value;
            }
        }

        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                Object cfeValue = getInheritedCFValue(parentCfEntity, cfCode);
                if (cfeValue != null) {
                    return cfeValue;
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

        // Get value without instantiating a default value if value not found
        if (entity.getCfValues() != null) {
            Object value = entity.getCfValues().getValue(cfCode, date);
            if (value != null) {
                return value;
            }
        }

        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                Object cfeValue = getInheritedCFValue(parentCfEntity, cfCode, date);
                if (cfeValue != null) {
                    return cfeValue;
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

        Object value = getCFValueByClosestMatch(entity, cfCode, keyToMatch);
        if (value != null) {
            return value;
        }
        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                Object cfeValue = getInheritedCFValueByClosestMatch(parentCfEntity, cfCode, keyToMatch);
                if (cfeValue != null) {
                    return cfeValue;
                }
            }
        }
        return null;
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

        Object value = getCFValueByClosestMatch(entity, code, date, keyToMatch);
        if (value != null) {
            return value;
        }
        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                Object cfeValue = getInheritedCFValueByClosestMatch(parentCfEntity, code, date, keyToMatch);
                if (cfeValue != null) {
                    return cfeValue;
                }
            }
        }
        return null;
    }

    /**
     * Match for a given entity's or its parent's custom field (non-versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * <matrix first key>|<matrix second key>|<matrix xx key>|<range of numbers for the third key></li>
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    public Object getInheritedCFValueByKey(ICustomFieldEntity entity, String cfCode, Object... keys) {

        Object value = getCFValueByKey(entity, cfCode, keys);
        if (value != null) {
            return value;
        }
        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                Object cfeValue = getInheritedCFValueByKey(parentCfEntity, cfCode, keys);
                if (cfeValue != null) {
                    return cfeValue;
                }
            }
        }
        return null;
    }

    /**
     * Match for a given date (versionable values) for a given entity's or its parent's custom field (versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * <matrix first key>|<matrix second key>|<matrix xx key>|<range of numbers for the third key></li>
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    public Object getInheritedCFValueByKey(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {

        Object value = getCFValueByKey(entity, cfCode, date, keys);
        if (value != null) {
            return value;
        }
        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                Object cfeValue = getInheritedCFValueByKey(parentCfEntity, cfCode, date, keys);
                if (cfeValue != null) {
                    return cfeValue;
                }
            }
        }
        return null;
    }

    /**
     * Match for a given entity's or its parent's custom field (non-versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: <number from>&gt;<number to>
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    public Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Object numberToMatch) {

        Object value = getCFValueByRangeOfNumbers(entity, cfCode, numberToMatch);
        if (value != null) {
            return value;
        }
        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                Object cfeValue = getInheritedCFValueByRangeOfNumbers(parentCfEntity, cfCode, numberToMatch);
                if (cfeValue != null) {
                    return cfeValue;
                }
            }
        }
        return null;
    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field (versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: <number from>&gt;<number to>
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    public Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Date date, Object numberToMatch) {

        Object value = getCFValueByRangeOfNumbers(entity, cfCode, date, numberToMatch);
        if (value != null) {
            return value;
        }
        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                // If Parent entity is Provider, use appProvider instead as entity passed will be a fake one.
                if (parentCfEntity instanceof Provider) {
                    parentCfEntity = providerService.findById(appProvider.getId());
                } else {
                    parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                }
                Object cfeValue = getInheritedCFValueByRangeOfNumbers(parentCfEntity, cfCode, date, numberToMatch);
                if (cfeValue != null) {
                    return cfeValue;
                }
            }
        }
        return null;
    }
    //
    // /**
    // * Duplicate custom field values from one entity to another
    // *
    // * @param sourceAppliesToEntity Source AppliesToEntity (UUID) value
    // * @param entity New entity to copy custom field values to
    // * @throws BusinessException
    // */
    // public void duplicateCfValues(String sourceAppliesToEntity, ICustomFieldEntity entity) throws BusinessException {
    // log.debug("Duplicating CF values from entity with UUID {} to {}", sourceAppliesToEntity, entity.getUuid());
    //
    // TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByEntity", CustomFieldInstance.class);
    //
    // query.setParameter("appliesToEntity", sourceAppliesToEntity);
    //
    // List<CustomFieldInstance> cfis = query.getResultList();
    //
    // for (CustomFieldInstance cfi : cfis) {
    //
    // CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfi.getCode(), entity);
    // if (cft == null) {
    // continue;
    // }
    //
    // getEntityManager().detach(cfi);
    // cfi.setId(null);
    // cfi.setVersion(0);
    // cfi.setAppliesToEntity(entity.getUuid());
    // cfi.setAuditable(null);
    // create(cfi, cft, entity);
    // }
    // log.trace("Finished duplicating CF values from entity with UUID {} to {}", sourceAppliesToEntity, entity.getUuid());
    // }

    /**
     * A trigger when a future custom field end period event expired
     * 
     * @param timer Timer information
     */
    @Timeout
    private void triggerEndPeriodEventExpired(Timer timer) {
        log.debug("triggerEndPeriodEventExpired={}", timer);
        try {
            CFEndPeriodEvent event = (CFEndPeriodEvent) timer.getInfo();
            cFEndPeriodEvent.fire(event);
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
            CFEndPeriodEvent event = new CFEndPeriodEvent(entity, cfCode, period);
            cFEndPeriodEvent.fire(event);
            log.debug("AKK Firing timer for triggerEndPeriodEvent for Custom field value {} with expiration={}", event, period.getTo());

        } else if (period != null && period.getTo() != null) {
            CFEndPeriodEvent event = new CFEndPeriodEvent(entity, cfCode, period);

            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setInfo(event);

            // used for testing
            // expiration = new Date();
            // expiration = DateUtils.addMinutes(expiration, 1);

            log.debug("Creating timer for triggerEndPeriodEvent for Custom field value {} with expiration={}", event, period.getTo());

            timerService.createSingleActionTimer(period.getTo(), timerConfig);
        }
    }

    private IEntity refreshOrRetrieveAny(IEntity entity) {

        if (entity.isTransient()) {
            return entity;
        }

        if (getEntityManager().contains(entity)) {
            getEntityManager().refresh(entity);
            return entity;

        } else {
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

        if (entity.getCfValues() == null) {
            return null;
        }

        Object value = entity.getCfValues().getValue(cfCode);
        Object valueMatched = CustomFieldInstanceService.matchClosestValue(value, keyToMatch);

        log.trace("Found closest match value {} for keyToMatch={}", valueMatched, keyToMatch);
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

        if (entity.getCfValues() == null) {
            return null;
        }

        Object value = entity.getCfValues().getValue(cfCode, date);

        Object valueMatched = CustomFieldInstanceService.matchClosestValue(value, keyToMatch);
        log.trace("Found closest match value {} for period {} and keyToMatch={}", valueMatched, date, keyToMatch);
        return valueMatched;

    }

    /**
     * Match for a given entity's custom field (non-versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * <matrix first key>|<matrix second key>|<matrix xx key>|<range of numbers for the third key></li>
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    @SuppressWarnings("unchecked")
    public Object getCFValueByKey(ICustomFieldEntity entity, String cfCode, Object... keys) {

        if (entity.getCfValues() == null) {
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

        Map<String, Object> value = (Map<String, Object>) entity.getCfValues().getValue(cfCode);
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
        return valueMatched;

    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field (versionable values) map's key (map or matrix) and return a map value.
     * 
     * For matrix, map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * <matrix first key>|<matrix second key>|<matrix xx key>|<range of numbers for the third key></li>
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param keys Keys to match. For matrix, the order must correspond to the order of the keys during data entry
     * @return Map value that matches the map key (map key or matrix formated map key)
     */
    @SuppressWarnings("unchecked")
    public Object getCFValueByKey(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {

        if (entity.getCfValues() == null) {
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

        Map<String, Object> value = (Map<String, Object>) entity.getCfValues().getValue(cfCode, date);
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
        return valueMatched;

    }

    /**
     * Match for a given entity's custom field (non-versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: <number from>&gt;<number to>
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    @SuppressWarnings("unchecked")
    public Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Object numberToMatch) {

        if (entity.getCfValues() == null) {
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

        Map<String, Object> value = (Map<String, Object>) entity.getCfValues().getValue(cfCode);
        Object valueMatched = CustomFieldInstanceService.matchRangeOfNumbersValue(value, numberToMatch);

        log.trace("Found map value match {} for numberToMatch={}", valueMatched, numberToMatch);
        return valueMatched;

    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field (versionable values) map's key as a range of numbers value and return a map value.
     * 
     * Number ranges is assumed to be the following format: <number from>&gt;<number to>
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date to match
     * @param numberToMatch Number (long, integer, double, bigdecimal) value to match
     * @return Map value that matches the range of numbers in a map key
     */
    public Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String cfCode, Date date, Object numberToMatch) {

        if (entity.getCfValues() == null) {
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

        Object value = entity.getCfValues().getValue(cfCode, date);
        Object valueMatched = CustomFieldInstanceService.matchRangeOfNumbersValue(value, numberToMatch);

        log.trace("Found matrix value match {} for period {} and numberToMatch={}", valueMatched, date, numberToMatch);
        return valueMatched;

    }

    /**
     * Match as close as possible map's key to the key provided and return a map value. Match is performed by matching a full string and then reducing one by one symbol untill a
     * match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param value Value to inspect
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    @SuppressWarnings("unchecked")
    public static Object matchClosestValue(Object value, String keyToMatch) {
        if (value == null || !(value instanceof Map) || StringUtils.isEmpty(keyToMatch)) {
            return null;
        }
        Logger log = LoggerFactory.getLogger(CustomFieldInstanceService.class);
        Object valueFound = null;
        Map<String, Object> mapValue = (Map<String, Object>) value;
        log.trace("matchClosestValue keyToMatch: {} in {}", keyToMatch, mapValue);
        for (int i = keyToMatch.length(); i > 0; i--) {
            valueFound = mapValue.get(keyToMatch.substring(0, i));
            if (valueFound != null) {
                log.trace("matchClosestValue found value: {} for key: {}", valueFound, keyToMatch.substring(0, i));
                return valueFound;
            }
        }

        return null;
    }

    /**
     * Match for a given value map's key as the matrix value and return a map value.
     * 
     * Map key is assumed to be the following format. Note that MATRIX_STRING and MATRIX_RON keys can be mixed
     * 
     * <matrix first key>|<matrix second key>|<range of numbers for the third key></li>
     * 
     * @param cft Custom field template
     * @param value Value to inspect
     * @param keys Keys to match. The order must correspond to the order of the keys during data entry
     * @return A value matched
     */
    @SuppressWarnings("unchecked")
    public static Object matchMatrixValue(CustomFieldTemplate cft, Object value, Object... keys) {
        if (value == null || !(value instanceof Map) || keys == null || keys.length == 0) {
            return null;
        }

        Object valueMatched = null;

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
     * <matrix first key>|<matrix second key>|<range of numbers for the third key></li>
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
     * Number ranges is assumed to be the following format: <number from>&lt;<number to>
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
     * Check if a match map's key as a range of numbers value is present
     * 
     * Number ranges is assumed to be the following format: <number from>&lt;<number to>
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
     * Determine if a number value is inside the number range expressed as <number from>&lt;<number to>
     * 
     * @param numberRange Number range value
     * @param numberToMatchObj A double number o
     * @return True if number have matched
     */
    private static boolean isNumberRangeMatch(String numberRange, Object numberToMatchObj) {
        if (numberToMatchObj == null) {
            return false;
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
    //
    // /**
    // * Get a list of custom fields instances corresponding to entities, identified by uuid
    // *
    // * @param uuids A list of uuid values
    // * @return A list of custom fields instances
    // */
    // public List<CustomFieldInstance> getCustomFieldInstances(List<String> uuids) {
    // return getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByEntityListForIndex", CustomFieldInstance.class).setParameter("appliesToEntityList", uuids)
    // .getResultList();
    // }

    /**
     * Instantiate a custom field value with default value for a given entity. If custom field is versionable, a current date will be used to access the value.
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public Object instantiateCFWithDefaultValue(ICustomFieldEntity entity, String cfCode) {

        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null || cft.getDefaultValue() == null || !isCFTApplicableToEntity(cft, entity)) {
            // log.trace("No CFT found or no default value specified {}/{}", entity, code);
            return null;
        }

        if (cft.isVersionable()) {
            log.warn("Trying to instantiate CF value from default value on a versionable custom field {}/{} value with no provided date. Current date will be used",
                entity.getClass().getSimpleName(), cfCode);
            return instantiateCFWithDefaultValue(entity, cfCode, new Date());
        }

        // Create such CF value with default value if one is specified on CFT
        Object value = cft.getDefaultValueConverted();
        entity.getCfValuesNullSafe().setValue(cfCode, value);

        return value;
    }

    /**
     * Instantiate a custom field value with default or inherited value for a given entity. If custom field is versionable, a current date will be used to access the value.
     * 
     * @param entity Entity
     * @param cft Custom field definition
     * @return Custom field value
     */
    public Object instantiateCFWithInheritedOrDefaultValue(ICustomFieldEntity entity, CustomFieldTemplate cft) {

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
     * Instantiate a custom field value with default value for a given entity and a date
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @return Custom field value
     */
    private Object instantiateCFWithDefaultValue(ICustomFieldEntity entity, String cfCode, Date date) {

        // If field is not versionable - get the value without the date
        CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(cfCode, entity);
        if (cft == null || cft.getDefaultValue() == null || cft.getCalendar() == null || !isCFTApplicableToEntity(cft, entity)) {
            // log.trace("No CFT found or no default value or calendar specified {}/{}", entity, code);
            return null;
        }

        if (!cft.isVersionable()) {
            return instantiateCFWithDefaultValue(entity, cfCode);
        }

        Object value = cft.getDefaultValueConverted();
        entity.getCfValuesNullSafe().setValue(cfCode, cft.getDatePeriod(date), null, value);

        return value;
    }

    /**
     * Instantiate a custom field value with default value for a given entity. If custom field is versionable, a current date will be used to access the value. Can be instantiated
     * only if cft.applicableOnEl condition pass
     * 
     * @param cft Custom field template
     * @param entity Entity
     * @return Custom field value
     */
    private Object instantiateCFWithDefaultValue(CustomFieldTemplate cft, ICustomFieldEntity entity) {

        if (cft.getDefaultValue() == null || !isCFTApplicableToEntity(cft, entity)) {
            // log.trace("No CFT found or no default value specified {}/{}", entity, cft.getCode());
            return null;
        }

        if (cft.isVersionable()) {
            log.warn("Trying to instantiate CF value from default value on a versionable custom field {}/{} value with no provided date. Current date will be used",
                entity.getClass().getSimpleName(), cft.getCode());
            return instantiateCFWithDefaultValue(cft, entity, new Date());
        }

        // Create such CF with default value if one is specified on CFT
        Object value = cft.getDefaultValueConverted();
        entity.getCfValuesNullSafe().setValue(cft.getCode(), value);

        return value;
    }

    /**
     * Instantiate a custom field value with default value for a given entity and a date. Can be instantiated only if values are versioned by a calendar and cft.applicableOnEl
     * condition pass
     * 
     * @param cft Custom field template
     * @param entity Entity
     * @param date Date
     * @return Custom field value
     */
    private Object instantiateCFWithDefaultValue(CustomFieldTemplate cft, ICustomFieldEntity entity, Date date) {

        if (cft.getDefaultValue() == null || cft.getCalendar() == null || !isCFTApplicableToEntity(cft, entity)) {
            // log.trace("No CFT found or no default value or calendar specified {}/{}", entity, code);
            return null;
        }

        // If field is not versionable - instantiate the value without the date
        if (!cft.isVersionable()) {
            return instantiateCFWithDefaultValue(cft, entity);
        }

        Object value = cft.getDefaultValueConverted();
        entity.getCfValuesNullSafe().setValue(cft.getCode(), cft.getDatePeriod(date), null, value);

        return value;
    }

    /**
     * Check if a given entity has a CF value of type Map or Matrix with a given key
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key
     */
    @SuppressWarnings("unchecked")
    public boolean isCFValueHasKey(ICustomFieldEntity entity, String cfCode, Object... keys) {

        if (entity.getCfValues() == null) {
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

        Map<String, Object> value = (Map<String, Object>) entity.getCfValues().getValue(cfCode);
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
     * Check if a given entity at a given period date has a CF value of type Map or Matrix with a given key
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @param date Date
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key at a given period date
     */
    @SuppressWarnings("unchecked")
    public boolean isCFValueHasKey(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {

        if (entity.getCfValues() == null) {
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

        Map<String, Object> value = (Map<String, Object>) entity.getCfValues().getValue(cfCode, date);
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
     * Check if a given entity or its parents have a CF value of type Map or Matrix with a given key
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key
     */
    public boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String cfCode, Object... keys) {

        boolean hasKey = isCFValueHasKey(entity, cfCode, keys);
        if (hasKey) {
            return true;
        }

        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                hasKey = isInheritedCFValueHasKey(parentCfEntity, cfCode, keys);
                if (hasKey) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if a given entity or its parents at a given perio date have a CF value of type Map or Matrix with a given key
     * 
     * @param entity Entity
     * @param cfCode Custom field code
     * @param data Date
     * @param keys Key or keys (in case of matrix) to match
     * @return True if CF value has a given key at a given perio date
     */
    public boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {

        boolean hasKey = isCFValueHasKey(entity, cfCode, date, keys);
        if (hasKey) {
            return true;
        }

        ICustomFieldEntity[] parentCfEntities = entity.getParentCFEntities();
        if (parentCfEntities != null) {
            for (ICustomFieldEntity parentCfEntity : parentCfEntities) {
                if (parentCfEntity == null) {
                    continue;
                }
                parentCfEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) parentCfEntity);
                hasKey = isInheritedCFValueHasKey(parentCfEntity, cfCode, date, keys);
                if (hasKey) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Deprecated. See getCFValueByKey function
     */
    @Deprecated
    public Object getCFValueByMatrix(ICustomFieldEntity entity, String cfCode, Object... keys) {
        return getCFValueByKey(entity, cfCode, keys);
    }

    /**
     * Deprecated. See getCFValueByKey function
     */
    @Deprecated
    public Object getCFValueByMatrix(ICustomFieldEntity entity, String cfCode, Date date, Object... keys) {
        return getCFValueByKey(entity, cfCode, date, keys);
    }

    /**
     * Deprecated. See getInheritedCFValueByKey function
     */
    @Deprecated
    public Object getInheritedCFValueByMetrix(ICustomFieldEntity entity, String cfCode, Object... keys) {
        return getInheritedCFValueByKey(entity, cfCode, keys);
    }

    /**
     * Deprecated. See getInheritedCFValueByKey function
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

    public EntityManager getEntityManager() {
        EntityManager result = emfForJobs;
        if (conversation != null) {
            try {
                conversation.isTransient();
                result = em;
            } catch (Exception e) {
            }
        }

        // log.debug("em.txKey={}, em.hashCode={}", txReg.getTransactionKey(),
        // em.hashCode());
        return result;
    }
}