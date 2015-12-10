package org.meveo.service.crm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ProviderNotAllowedException;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.ParamBean;
import org.meveo.event.CFEndPeriodEvent;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IProvider;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.CustomFieldValue;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.util.PersistenceUtils;

@Stateless
public class CustomFieldInstanceService extends PersistenceService<CustomFieldInstance> {

    @Inject
    private CustomFieldTemplateService cfTemplateService;

    @Inject
    private CustomFieldsCacheContainerProvider customFieldsCacheContainerProvider;

    @Inject
    private Event<CFEndPeriodEvent> cFEndPeriodEvent;

    @Resource
    private TimerService timerService;

    private ParamBean paramBean = ParamBean.getInstance();

    @Override
    public void create(CustomFieldInstance cfi) throws BusinessException {
        throw new RuntimeException(
            "CustomFieldInstanceService.create(CustomFieldInstance cfi) method not supported. Should use CustomFieldInstanceService.create(CustomFieldInstance cfi, ICustomFieldEntity entity) method instead");
    }

    public void create(CustomFieldInstance cfi, ICustomFieldEntity entity, User creator, Provider provider) throws BusinessException {
        super.create(cfi, creator, provider);
        customFieldsCacheContainerProvider.addUpdateCustomFieldInCache(entity, cfi);

        triggerEndPeriodEvent(cfi);
    }

    @Override
    public CustomFieldInstance update(CustomFieldInstance e) {
        throw new RuntimeException(
            "CustomFieldInstanceService.update(CustomFieldInstance cfi) method not supported. Should use CustomFieldInstanceService.update(CustomFieldInstance cfi, ICustomFieldEntity entity) method instead");
    }

    public CustomFieldInstance update(CustomFieldInstance cfi, ICustomFieldEntity entity, User updater) {
        cfi = super.update(cfi, updater);
        customFieldsCacheContainerProvider.addUpdateCustomFieldInCache(entity, cfi);

        triggerEndPeriodEvent(cfi);

        return cfi;
    }

    // @Override
    // public void remove(CustomFieldInstance e) {
    // throw new RuntimeException(
    // "CustomFieldInstanceService.remove(CustomFieldInstance cfi) method not supported. Should use CustomFieldInstanceService.remove(CustomFieldInstance cfi, ICustomFieldEntity entity) method instead");
    // }

    public void remove(CustomFieldInstance cfi, ICustomFieldEntity entity) {
        customFieldsCacheContainerProvider.removeCustomFieldFromCache(entity, cfi);
        super.remove(cfi.getId());
    }

    /**
     * Get a list of custom field instances to populate a cache
     * 
     * @return A list of custom field instances
     */
    public List<CustomFieldInstance> getCFIForCache() {

        TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiForCache", CustomFieldInstance.class);
        return query.getResultList();
    }

    // /**
    // * Convert BusinessEntityWrapper to an entity by doing a lookup in DB
    // *
    // * @param businessEntityWrapper Business entity information
    // * @return A BusinessEntity object
    // */
    // @SuppressWarnings("unchecked")
    // public BusinessEntity convertToBusinessEntityFromCfV(EntityReferenceWrapper businessEntityWrapper, Provider provider) {
    // if (businessEntityWrapper == null) {
    // return null;
    // }
    // Query query = getEntityManager().createQuery("select e from " + businessEntityWrapper.getClassname() + " e where e.code=:code and e.provider=:provider");
    // query.setParameter("code", businessEntityWrapper.getCode());
    // query.setParameter("provider", provider);
    // List<BusinessEntity> entities = query.getResultList();
    // if (entities.size() > 0) {
    // return entities.get(0);
    // } else {
    // return null;
    // }
    // }

    @SuppressWarnings("unchecked")
    public List<BusinessEntity> findBusinessEntityForCFVByCode(String className, String wildcode, Provider provider) {
        Query query = getEntityManager().createQuery("select e from " + className + " e where lower(e.code) like :code and e.provider=:provider");
        query.setParameter("code", "%" + wildcode.toLowerCase() + "%");
        query.setParameter("provider", provider);
        List<BusinessEntity> entities = query.getResultList();
        return entities;
    }

    public Object getOrCreateCFValueFromParamValue(String code, String defaultParamBeanValue, ICustomFieldEntity entity, boolean saveInCFIfNotExist, User currentUser)
            throws BusinessException {

        Object value = getCFValue(entity, code, currentUser);
        if (value != null) {
            return value;
        }

        // If value is not found, create a new Custom field with a value taken from configuration parameters
        value = ParamBean.getInstance().getProperty(code, defaultParamBeanValue);
        if (value == null) {
            return null;
        }
        try {
            // If no template found - create it first
            CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(code, entity, currentUser.getProvider());
            if (cft == null) {
                cft = new CustomFieldTemplate();
                cft.setCode(code);
                cft.setAppliesTo(CustomFieldTemplateService.calculateAppliesToValue(entity));
                cft.setActive(true);
                cft.setDescription(code);
                cft.setFieldType(CustomFieldTypeEnum.STRING);
                cft.setDefaultValue(value.toString());
                cft.setValueRequired(false);
                cfTemplateService.create(cft, currentUser, currentUser.getProvider());
            }

            CustomFieldInstance cfi = CustomFieldInstance.fromTemplate(cft, entity);

            if (saveInCFIfNotExist) {
                create(cfi, entity, currentUser, currentUser.getProvider());
            }
        } catch (CustomFieldException e) {
            log.error("Can not determine applicable CFT type for entity of {} class. Value from propeties file will NOT be saved as customfield", entity.getClass().getSimpleName());
        }
        return value;
    }

    /**
     * Get a custom field value for a given entity
     * 
     * @param entity Entity
     * @param code Custom field code
     * @return Custom field value
     */
    public Object getCFValue(ICustomFieldEntity entity, String code, User currentUser) {

        boolean useCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCFI", "true"));

        CustomFieldTemplate cft = customFieldsCacheContainerProvider.getCustomFieldTemplate(code, entity);
        if (cft == null) {
            log.trace("No CFT found {}/{}", entity, code);
            return null;
        }

        if (cft.isVersionable()) {
            log.warn("Trying to access a versionable custom field {}/{} value with no provided date. Null will be returned", entity.getClass().getSimpleName(), code);
            return null;
        }

        Object value = null;

        // Try cache if applicable
        if (cft.isCacheValue() && useCache) {
            value = customFieldsCacheContainerProvider.getValue(entity, code);

            // Or retrieve directly from DB
        } else {
            TypedQuery<CustomFieldValue> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiValueByCode", CustomFieldValue.class);
            query.setParameter("appliesToEntity", entity.getUuid());
            query.setParameter("code", code);
            query.setParameter("provider", getProvider(entity));

            List<CustomFieldValue> cfvs = query.getResultList();
            if (!cfvs.isEmpty()) {
                CustomFieldValue cfv = cfvs.get(0);

                cfv.deserializeValue();
                value = cfv.getValue();
            }
        }

        // Create such CF with default value if one is specified on CFT
        if (value == null && cft.getDefaultValue() != null && currentUser != null) {
            value = cft.getDefaultValueConverted();
            try {
                setCFValue(entity, code, value, currentUser);
            } catch (BusinessException e) {
                log.error("Failed to set a default Custom field value {}/{}", entity.getClass().getSimpleName(), code, e);
            }
        }

        return value;
    }

    /**
     * Get a custom field value for a given entity and a date
     * 
     * @param entity Entity
     * @param code Custom field code
     * @param date Date
     * @return Custom field value
     */
    public Object getCFValue(ICustomFieldEntity entity, String code, Date date, User currentUser) {

        boolean useCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCFI", "true"));

        // If field is not versionable - get the value without the date
		CustomFieldTemplate cft = customFieldsCacheContainerProvider.getCustomFieldTemplate(code, entity);
		if (cft == null) {
			log.trace("No CFT found {}/{}", entity, code);
			return null;
		}
        if (!cft.isVersionable()) {
            return getCFValue(entity, code, currentUser);
        }

        Object value = null;

        // Check cache first TODO need to check if date falls within cacheable period date timeframe
        if (cft.isCacheValue() && useCache) {
            value = customFieldsCacheContainerProvider.getValue(entity, code, date);

        } else {
            TypedQuery<CustomFieldValue> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiValueByCodeAndDate", CustomFieldValue.class);
            query.setParameter("appliesToEntity", entity.getUuid());
            query.setParameter("code", code);
            query.setParameter("provider", getProvider(entity));
            query.setParameter("date", date);

            List<CustomFieldValue> cfvs = query.getResultList();
            if (!cfvs.isEmpty()) {
                CustomFieldValue cfv = cfvs.get(0);
                cfv.deserializeValue();
                value = cfv.getValue();
            }
        }

        // Create such CF with default value if one is specified on CFT and field is versioned by a calendar
        if (value == null && cft.getDefaultValue() != null && cft.getCalendar() != null && currentUser != null) {
            value = cft.getDefaultValueConverted();
            try {
                setCFValue(entity, code, value, date, currentUser);
            } catch (BusinessException e) {
                log.error("Failed to set a default Custom field value {}/{}", entity.getClass().getSimpleName(), code, e);
            }
        }

        return value;
    }

    /**
     * Get custom field values of an entity as JSON string
     * 
     * @param entity Entity
     * @return JSON format string
     */
    public String getCFValuesAsJson(ICustomFieldEntity entity) {

        String result = "";
        String sep = "";

        Map<String, List<CustomFieldInstance>> customFieldsMap = getCustomFieldInstances(entity);

        for (List<CustomFieldInstance> customFields : customFieldsMap.values()) {
            for (CustomFieldInstance cf : customFields) {
                result += sep + cf.toJson();
                sep = ";";
            }
        }

        return result;
    }

    /**
     * Set a Custom field value on an entity
     * 
     * @param entity Entity
     * @param code Custom field value code
     * @param value
     * @throws BusinessException
     */
    public CustomFieldInstance setCFValue(ICustomFieldEntity entity, String code, Object value, User currentUser) throws BusinessException {

        log.debug("Setting CF value. Code: {}, entity {} value {}", code, entity, value);

        // Can not set the value if field is versionable without a date
        CustomFieldTemplate cft = customFieldsCacheContainerProvider.getCustomFieldTemplate(code, entity);
        if (cft == null) {
            throw new BusinessException("Custom field template with code " + code + " not found found for entity " + entity);
        }

        if (cft.isVersionable()) {
            throw new RuntimeException("Can not determine a period for Custom Field " + entity.getClass().getSimpleName() + "/" + code
                    + " value if no date or date range is provided");
        }

        List<CustomFieldInstance> cfis = getCustomFieldInstances(entity, code);
        CustomFieldInstance cfi = null;
        if (cfis.isEmpty()) {
            if (value == null) {
                return null;
            }
            cfi = CustomFieldInstance.fromTemplate(cft, entity);
            cfi.setValue(value);
            create(cfi, entity, currentUser, currentUser.getProvider());

        } else {
            cfi = cfis.get(0);
            cfi.setValue(value);
            cfi = update(cfi, entity, currentUser);
        }
        customFieldsCacheContainerProvider.addUpdateCustomFieldInCache(entity, cfi);
        return cfi;
    }

    public CustomFieldInstance setCFValue(ICustomFieldEntity entity, String code, Object value, Date valueDate, User currentUser) throws BusinessException {

        log.debug("Setting CF value. Code: {}, entity {} value {} valueDate {}", code, entity, value, valueDate);

        // If field is not versionable - set the value without the date
        CustomFieldTemplate cft = customFieldsCacheContainerProvider.getCustomFieldTemplate(code, entity);
        if (cft == null) {
            throw new BusinessException("Custom field template with code " + code + " not found found for entity " + entity);
        }
        
        if (!cft.isVersionable()) {
            setCFValue(entity, code, value, currentUser);

            // Calendar is needed to be able to set a value with a single date
        } else if (cft.getCalendar() == null) {
            log.error("Can not determine a period for Custom Field {}/{} value if no calendar is provided", entity.getClass().getSimpleName(), code);
            throw new RuntimeException("Can not determine a period for Custom Field " + entity.getClass().getSimpleName() + "/" + code + " value if no calendar is provided");
        }

        // Should not match more then one record as periods are calendar based
        List<CustomFieldInstance> cfis = getCustomFieldInstances(entity, code, valueDate);
        CustomFieldInstance cfi = null;
        if (cfis.isEmpty()) {
            // Nothing found and nothing to save
            if (value == null) {
                return null;
            }
            cfi = CustomFieldInstance.fromTemplate(cft, entity, valueDate);
            cfi.setValue(value);
            create(cfi, entity, currentUser, currentUser.getProvider());

        } else {
            cfi = cfis.get(0);
            cfi.setValue(value);
            cfi = update(cfi, entity, currentUser);
        }
        customFieldsCacheContainerProvider.addUpdateCustomFieldInCache(entity, cfi);
        return cfi;
    }

    public CustomFieldInstance setCFValue(ICustomFieldEntity entity, String code, Object value, Date valueDateFrom, Date valueDateTo, Integer valuePriority, User currentUser)
            throws BusinessException {

        log.debug("Setting CF value. Code: {}, entity {} value {} valueDateFrom {} valueDateTo {}", code, entity, value, valueDateFrom, valueDateTo);

        // If field is not versionable - set the value without the date
        CustomFieldTemplate cft = customFieldsCacheContainerProvider.getCustomFieldTemplate(code, entity);
        if (cft == null) {
            throw new BusinessException("Custom field template with code " + code + " not found found for entity " + entity);
        }
        
        if (!cft.isVersionable()) {
            setCFValue(entity, code, value, currentUser);

            // If calendar is provided - use calendar by the valueDateFrom date
        } else if (cft.getCalendar() != null) {
            log.warn(
                "Calendar is provided in Custom Field template {}/{} while trying to assign value period start and end dates with two values. Only start date will be considered",
                entity.getClass().getSimpleName(), code);
            setCFValue(entity, code, value, valueDateFrom, currentUser);
        }

        // Should not match more then one record
        List<CustomFieldInstance> cfis = getCustomFieldInstances(entity, code, valueDateFrom, valueDateTo);
        CustomFieldInstance cfi = null;
        if (cfis.isEmpty()) {
            if (value == null) {
                return null;
            }
            cfi = CustomFieldInstance.fromTemplate(cft, entity, valueDateFrom, valueDateTo, valuePriority);
            cfi.setValue(value);
            create(cfi, entity, currentUser, currentUser.getProvider());

        } else {
            cfi = cfis.get(0);
            cfi.setValue(value);
            cfi = update(cfi, entity, currentUser);
        }
        customFieldsCacheContainerProvider.addUpdateCustomFieldInCache(entity, cfi);

        return cfi;
    }

    /**
     * Remove Custom field instance
     * 
     * @param code Custom field code to remove
     */
    public void removeCFValue(ICustomFieldEntity entity, String code) {
        List<CustomFieldInstance> cfis = getCustomFieldInstances(entity, code);
        for (CustomFieldInstance cfi : cfis) {
            super.remove(cfi.getId());
        }

        customFieldsCacheContainerProvider.removeCustomFieldFromCache(entity, code);
    }

    /**
     * Remove all custom field values for a given entity
     * 
     * @param entity
     */
    public void removeCFValues(ICustomFieldEntity entity) {

        Map<String, List<CustomFieldInstance>> cfisByCode = getCustomFieldInstances(entity);
        for (Entry<String, List<CustomFieldInstance>> cfisInfo : cfisByCode.entrySet()) {
            for (CustomFieldInstance cfi : cfisInfo.getValue()) {
                super.remove(cfi.getId());
            }

            customFieldsCacheContainerProvider.removeCustomFieldFromCache(entity, cfisInfo.getKey());
        }
    }

    /**
     * Get All custom field instances for a given entity.
     * 
     * @param entity Entity
     * @return A map of Custom field instances with CF code as a key
     */
    public Map<String, List<CustomFieldInstance>> getCustomFieldInstances(ICustomFieldEntity entity) {
        TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByEntity", CustomFieldInstance.class);
        query.setParameter("appliesToEntity", entity.getUuid());
        query.setParameter("provider", getProvider(entity));

        List<CustomFieldInstance> cfis = query.getResultList();

        // // Make sure that embedded CF value property is not null
        // if (cfi != null && cfi.getCfValue() == null) {
        // cfi.setCfValue(new CustomFieldValue());
        // }

        Map<String, List<CustomFieldInstance>> cfisAsMap = new HashMap<String, List<CustomFieldInstance>>();

        for (CustomFieldInstance cfi : cfis) {
            if (!cfisAsMap.containsKey(cfi.getCode())) {
                cfisAsMap.put(cfi.getCode(), new ArrayList<CustomFieldInstance>());
            }
            cfisAsMap.get(cfi.getCode()).add(cfi);
        }

        return cfisAsMap;
    }

    /**
     * Get custom field instances for a given entity. Should be only a single record when custom field is not versioned
     * 
     * @param entity Entity
     * @param code Custom field code
     * @return Custom field instance
     */
    public List<CustomFieldInstance> getCustomFieldInstances(ICustomFieldEntity entity, String code) {

        TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByCode", CustomFieldInstance.class);
        query.setParameter("appliesToEntity", entity.getUuid());
        query.setParameter("code", code);
        query.setParameter("provider", getProvider(entity));

        List<CustomFieldInstance> cfis = query.getResultList();

        // // Make sure that embedded CF value property is not null
        // if (cfi != null && cfi.getCfValue() == null) {
        // cfi.setCfValue(new CustomFieldValue());
        // }

        return cfis;
    }

    /**
     * Get custom field instances for a given entity and a given date.
     * 
     * @param entity Entity
     * @param code Custom field code
     * @return Custom field instance
     */
    private List<CustomFieldInstance> getCustomFieldInstances(ICustomFieldEntity entity, String code, Date date) {

        TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByCodeAndDate", CustomFieldInstance.class);
        query.setParameter("appliesToEntity", entity.getUuid());
        query.setParameter("code", code);
        query.setParameter("provider", getProvider(entity));
        query.setParameter("date", date);

        List<CustomFieldInstance> cfis = query.getResultList();

        // // Make sure that embedded CF value property is not null
        // if (cfi != null && cfi.getCfValue() == null) {
        // cfi.setCfValue(new CustomFieldValue());
        // }

        return cfis;
    }

    /**
     * Get custom field instances for a given entity and a given date.
     * 
     * @param entity Entity
     * @param code Custom field code
     * @param valueDateFrom Value period data range - from
     * @param valueDateTo Value period data range - to
     * @return
     */
    private List<CustomFieldInstance> getCustomFieldInstances(ICustomFieldEntity entity, String code, Date valueDateFrom, Date valueDateTo) {

        TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByCodeAndDateRange", CustomFieldInstance.class);
        query.setParameter("appliesToEntity", entity.getUuid());
        query.setParameter("code", code);
        query.setParameter("provider", getProvider(entity));
        query.setParameter("dateFrom", valueDateFrom);
        query.setParameter("dateTo", valueDateTo);

        List<CustomFieldInstance> cfis = query.getResultList();

        // // Make sure that embedded CF value property is not null
        // if (cfi != null && cfi.getCfValue() == null) {
        // cfi.setCfValue(new CustomFieldValue());
        // }

        return cfis;
    }

    /**
     * Get provider of and entity. Handles cases when entity itself is a provider
     * 
     * @param entity Entity
     * @return Provider
     */
    private Provider getProvider(ICustomFieldEntity entity) {

        if (entity instanceof Provider) {
            return (Provider) entity;

        } else {
            return ((IProvider) entity).getProvider();
        }
    }

    public Object getInheritedOnlyCFValue(ICustomFieldEntity entity, String code, User currentUser) {
        if (entity.getParentCFEntity() != null) {
            ICustomFieldEntity parentCFEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) entity.getParentCFEntity());
            return getInheritedCFValue(parentCFEntity, code, currentUser);
        }
        return null;
    }

    public Object getInheritedOnlyCFValue(ICustomFieldEntity entity, String code, Date date, User currentUser) {

        if (entity.getParentCFEntity() != null) {
            ICustomFieldEntity parentCFEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) entity.getParentCFEntity());
            return getInheritedCFValue(parentCFEntity, code, date, currentUser);
        }
        return null;
    }

    public Object getInheritedCFValue(ICustomFieldEntity entity, String code, User currentUser) {
        Object value = getCFValue(entity, code, currentUser);
        if (value == null && entity.getParentCFEntity() != null) {
            ICustomFieldEntity parentCFEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) entity.getParentCFEntity());
            return getInheritedCFValue(parentCFEntity, code, currentUser);
        }
        return value;
    }

    public Object getInheritedCFValue(ICustomFieldEntity entity, String code, Date date, User currentUser) {

        Object value = getCFValue(entity, code, date, currentUser);
        if (value == null && entity.getParentCFEntity() != null) {
            ICustomFieldEntity parentCFEntity = (ICustomFieldEntity) refreshOrRetrieveAny((IEntity) entity.getParentCFEntity());
            return getInheritedCFValue(parentCFEntity, code, date, currentUser);
        }
        return value;
    }

    /**
     * Duplicate custom field values from one entity to another
     * 
     * @param sourceAppliesToEntity Source AppliesToEntity (UUID) value
     * @param entity New entity to copy custom field values to
     * @param currentUser User
     * @throws BusinessException
     */
    public void duplicateCfValues(String sourceAppliesToEntity, ICustomFieldEntity entity, User currentUser) throws BusinessException {
        TypedQuery<CustomFieldInstance> query = getEntityManager().createNamedQuery("CustomFieldInstance.getCfiByEntity", CustomFieldInstance.class);
        query.setParameter("appliesToEntity", sourceAppliesToEntity);
        query.setParameter("provider", getProvider(entity));

        List<CustomFieldInstance> cfis = query.getResultList();

        for (CustomFieldInstance cfi : cfis) {
            cfi.setId(null);
            cfi.setVersion(0);
            cfi.setAppliesToEntity(entity.getUuid());
            cfi.setAuditable(null);
            create(cfi, entity, currentUser, currentUser.getProvider());
        }
    }

    /**
     * A trigger when a future custom field end period event expired
     * 
     * @param timer Timer information
     */
    @Timeout
    private void triggerEndPeriodEventExpired(Timer timer) {
        log.debug("triggerEndPeriodEventExpired={}", timer);
        try {
            CustomFieldInstance cfi = (CustomFieldInstance) timer.getInfo();
            CFEndPeriodEvent event = new CFEndPeriodEvent();
            event.setCustomFieldInstance(cfi);
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
    private void triggerEndPeriodEvent(CustomFieldInstance cfi) {

        if (cfi.getPeriodEndDate() != null && cfi.getPeriodEndDate().before(new Date())) {
            CFEndPeriodEvent event = new CFEndPeriodEvent();
            event.setCustomFieldInstance(cfi);
            cFEndPeriodEvent.fire(event);

        } else if (cfi.getPeriodEndDate() != null) {

            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setInfo(cfi);

            // used for testing
            // expiration = new Date();
            // expiration = DateUtils.addMinutes(expiration, 1);

            log.debug("Creating timer for triggerEndPeriodEvent for Custom field value {} with expiration={}", cfi, cfi.getPeriodEndDate());

            timerService.createSingleActionTimer(cfi.getPeriodEndDate(), timerConfig);
        }
    }

    private IEntity refreshOrRetrieveAny(IEntity entity) {

        if (getEntityManager().contains(entity)) {
            getEntityManager().refresh(entity);
            return entity;

        } else {
            entity = getEntityManager().find(PersistenceUtils.getClassForHibernateObject(entity), entity.getId());
            if (entity != null && isConversationScoped() && getCurrentProvider() != null) {
                if (entity instanceof BaseEntity) {
                    boolean notSameProvider = !((BaseEntity) entity).doesProviderMatch(getCurrentProvider());
                    if (notSameProvider) {
                        log.debug("CheckProvider in refreshOrRetrieveAny getCurrentProvider() id={}, entityProvider id={}", new Object[] { getCurrentProvider().getId(),
                                ((BaseEntity) entity).getProvider().getId() });
                        throw new ProviderNotAllowedException();
                    }
                }
            }
            return entity;
        }
    }
}