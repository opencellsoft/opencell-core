package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.CustomFieldValue;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldJob;

/**
 * Backing bean for support custom field instances value data entry
 * 
 * @param <T>
 */
public abstract class CustomFieldBean<T extends IEntity> extends BaseBean<T> {

    private static final long serialVersionUID = 1L;

    private CustomFieldTemplate customFieldSelectedTemplate;

    private CustomFieldPeriod customFieldSelectedPeriod;

    private String customFieldSelectedPeriodId;

    private boolean customFieldPeriodMatched;

    private Map<String, Object> customFieldNewValue = new HashMap<String, Object>();

    /**
     * Custom field templates
     */
    protected List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();

    @Inject
    private CustomFieldJob customFieldJob;

    public CustomFieldBean() {
    }

    public CustomFieldBean(Class<T> clazz) {
        super(clazz);
    }

    protected abstract IPersistenceService<T> getPersistenceService();

    @Override
    public T initEntity() {
        T result = super.initEntity();
        initCustomFields();
        return result;
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        updateCustomFieldsInEntity();
        
		if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
			for (CustomFieldTemplate cft : customFieldTemplates) {
				CustomFieldInstance cfi = ((ICustomFieldEntity) entity).getCustomFields().get(cft.getCode());
				if (cfi != null && cft.isVersionable() && cft.getCalendar() != null && cft.isTriggerEndPeriodEvent()) {
					// Create a timer if was requested
					for (CustomFieldPeriod cfp : cfi.getValuePeriods()) {
						if (cfp.getPeriodEndDate() != null) {
							customFieldJob.triggerEndPeriodEvent(cft.getInstance(), cfp.getPeriodEndDate());
						}
					}
				}
			}
		}

        String outcome = super.saveOrUpdate(killConversation);
        
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            return null; //getEditViewName();
        } else {
            return outcome;
        }
    }

    /**
     * Load available custom fields (templates) and their values
     */
    protected void initCustomFields() {

        customFieldTemplates = getApplicateCustomFieldTemplates();

        if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
            for (CustomFieldTemplate cft : customFieldTemplates) {
                CustomFieldInstance cfi = ((ICustomFieldEntity) entity).getCustomFields().get(cft.getCode());

                if (cfi == null) {
                    cft.setInstance(CustomFieldInstance.fromTemplate(cft));
                } else {
                    if (cfi.getCfValue() == null) {
                        cfi.setCfValue(new CustomFieldValue());
                    }
                    deserializeForGUI(cft, cfi);
                    cft.setInstance(cfi);
                }
            }
        }
    }

    private void updateCustomFieldsInEntity() {

        for (CustomFieldTemplate cft : customFieldTemplates) {
            CustomFieldInstance cfi = cft.getInstance();
            // Not saving empty values
            if (cfi.isValueEmptyForGui()) {
                if (!cfi.isTransient()) {
                    ((ICustomFieldEntity) entity).getCustomFields().remove(cfi.getCode());
                    log.debug("Remove empty cfi value {}", cfi.getCode());
                }
                // Existing value update
            } else {
                serializeForGUI(cft, cfi);
                if (!cfi.isTransient()) {

                    cfi.updateAudit(getCurrentUser());
                    // Create a new instance from a template value
                } else {
                    cfi.updateAudit(getCurrentUser());
                    IEntity entity = getEntity();
                    if (entity instanceof AccountEntity) {
                        cfi.setAccount((AccountEntity) getEntity());
                    } else if (entity instanceof Subscription) {
                        cfi.setSubscription((Subscription) entity);
                    } else if (entity instanceof Access) {
                        cfi.setAccess((Access) entity);
                    } else if (entity instanceof ChargeTemplate) {
                        cfi.setChargeTemplate((ChargeTemplate) entity);
                    } else if (entity instanceof ServiceTemplate) {
                        cfi.setServiceTemplate((ServiceTemplate) entity);
                    } else if (entity instanceof OfferTemplate) {
                        cfi.setOfferTemplate((OfferTemplate) entity);
                    } else if (entity instanceof JobInstance) {
                        cfi.setJobInstance((JobInstance) entity);
                    } else if (entity instanceof Provider) {
                        cfi.setProvider((Provider) entity);
                    }
                }
                ((ICustomFieldEntity) entity).getCustomFields().put(cfi.getCode(), cfi);
            }
        }
    }

    private void deserializeForGUI(CustomFieldTemplate cft, CustomFieldInstance cfi) {
        if (cft.isVersionable()) {
            for (CustomFieldPeriod period : cfi.getValuePeriods()) {
                deserializeForGUI(cft, period.getCfValue());
            }
        } else {
            deserializeForGUI(cft, cfi.getCfValue());
        }
    }

    private void deserializeForGUI(CustomFieldTemplate cft, CustomFieldValue cfv) {

        // Convert just Entity type field to a JPA object
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                cfv.setEntityReferenceValueForGUI(deserializeEntityReferenceForGUI(cfv.getEntityReferenceValue()));
            }

            // Populate mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {

            List<Map<String, Object>> listOfMapValues = new ArrayList<Map<String, Object>>();
            if (cfv.getListValue() != null) {
                for (Object listItem : cfv.getListValue()) {
                    Map<String, Object> listEntry = new HashMap<String, Object>();
                    if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        listEntry.put(CustomFieldValue.MAP_VALUE, deserializeEntityReferenceForGUI((EntityReferenceWrapper) listItem));
                    } else {
                        listEntry.put(CustomFieldValue.MAP_VALUE, listItem);
                    }
                    listOfMapValues.add(listEntry);
                }
            }
            cfv.setMapValuesForGUI(listOfMapValues);

            // Populate mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {

            List<Map<String, Object>> listOfMapValues = new ArrayList<Map<String, Object>>();

            if (cfv.getMapValue() != null) {
                for (Entry<String, Object> mapInfo : cfv.getMapValue().entrySet()) {
                    Map<String, Object> listEntry = new HashMap<String, Object>();
                    listEntry.put(CustomFieldValue.MAP_KEY, mapInfo.getKey());
                    if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        listEntry.put(CustomFieldValue.MAP_VALUE, deserializeEntityReferenceForGUI((EntityReferenceWrapper) mapInfo.getValue()));
                    } else {
                        listEntry.put(CustomFieldValue.MAP_VALUE, mapInfo.getValue());
                    }
                    listOfMapValues.add(listEntry);
                }
            }
            cfv.setMapValuesForGUI(listOfMapValues);
        }
    }

    /**
     * Covert entity reference to a Business entity JPA object.
     * 
     * @param entityReferenceValue Entity reference value
     * @return Business entity JPA object
     */
    private BusinessEntity deserializeEntityReferenceForGUI(EntityReferenceWrapper entityReferenceValue) {

        // NOTE: For PF autocomplete seems that fake BusinessEntity object with code value filled is sufficient - it does not have to be a full loaded JPA object

        // BusinessEntity convertedEntity = customFieldInstanceService.convertToBusinessEntityFromCfV(entityReferenceValue, this.currentProvider);
        // if (convertedEntity == null) {
        // convertedEntity = (BusinessEntity) ReflectionUtils.createObject(entityReferenceValue.getClassname());
        // if (convertedEntity != null) {
        // convertedEntity.setCode("NOT FOUND: " + entityReferenceValue.getCode());
        // }
        // } else {
        BusinessEntity convertedEntity = (BusinessEntity) ReflectionUtils.createObject(entityReferenceValue.getClassname());
        if (convertedEntity != null) {
            convertedEntity.setCode(entityReferenceValue.getCode());
        }
        // }
        return convertedEntity;
    }

    private void serializeForGUI(CustomFieldTemplate cft, CustomFieldInstance cfi) {

        if (cft.isVersionable()) {
            for (CustomFieldPeriod period : cfi.getValuePeriods()) {
                serializeForGUI(cft, period.getCfValue());
            }
        } else {
            serializeForGUI(cft, cfi.getCfValue());
        }
    }

    private void serializeForGUI(CustomFieldTemplate cft, CustomFieldValue cfv) {

        // Convert just Entity type field to a JPA object
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                if (cfv.getEntityReferenceValueForGUI() == null) {
                    cfv.setEntityReferenceValue(null);
                } else {
                    cfv.setEntityReferenceValue(new EntityReferenceWrapper(cfv.getEntityReferenceValueForGUI()));
                }
            }

            // Populate mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {

            List<Object> listValue = new ArrayList<Object>();
            for (Map<String, Object> listItem : cfv.getMapValuesForGUI()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    listValue.add(new EntityReferenceWrapper((BusinessEntity) listItem.get(CustomFieldValue.MAP_VALUE)));
                } else {
                    listValue.add(listItem.get(CustomFieldValue.MAP_VALUE));
                }
            }
            cfv.setListValue(listValue);

            // Populate mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {

            Map<String, Object> mapValue = new HashMap<String, Object>();

            for (Map<String, Object> listItem : cfv.getMapValuesForGUI()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    mapValue.put((String) listItem.get(CustomFieldValue.MAP_KEY), new EntityReferenceWrapper((BusinessEntity) listItem.get(CustomFieldValue.MAP_VALUE)));
                } else {
                    mapValue.put((String) listItem.get(CustomFieldValue.MAP_KEY), listItem.get(CustomFieldValue.MAP_VALUE));
                }
            }
            cfv.setMapValue(mapValue);
        }
    }

    public List<CustomFieldTemplate> getCustomFieldTemplates() {
        if (customFieldTemplates == null || customFieldTemplates.size() == 0) {
            if (entity != null) {
                initCustomFields();
            } else {
                initEntity();
            }
        }
        return customFieldTemplates;
    }

    public void setCustomFieldTemplates(List<CustomFieldTemplate> customFieldTemplates) {
        this.customFieldTemplates = customFieldTemplates;
    }

    public CustomFieldPeriod getCustomFieldSelectedPeriod() {
        return customFieldSelectedPeriod;
    }

    public void setCustomFieldSelectedPeriod(CustomFieldPeriod customFieldSelectedPeriod) {
        this.customFieldSelectedPeriod = customFieldSelectedPeriod;
    }

    public void setCustomFieldSelectedTemplate(CustomFieldTemplate customFieldSelectedTemplate) {
        this.customFieldSelectedTemplate = customFieldSelectedTemplate;
    }

    public CustomFieldTemplate getCustomFieldSelectedTemplate() {
        return customFieldSelectedTemplate;
    }

    public String getCustomFieldSelectedPeriodId() {
        return customFieldSelectedPeriodId;
    }

    public void setCustomFieldSelectedPeriodId(String customFieldSelectedPeriodId) {
        this.customFieldSelectedPeriodId = customFieldSelectedPeriodId;
    }

    public boolean isCustomFieldPeriodMatched() {
        return customFieldPeriodMatched;
    }

    /**
     * Add a new customField period with a previous validation that matching period does not exists
     */
    public void addNewCustomFieldPeriod(CustomFieldTemplate cft) {

        Date periodStartDate = (Date) customFieldNewValue.get(cft.getCode() + "_periodStartDate");
        Date periodEndDate = (Date) customFieldNewValue.get(cft.getCode() + "_periodEndDate");
        String key = (String) customFieldNewValue.get(cft.getCode() + "_key");
        Object value = customFieldNewValue.get(cft.getCode() + "_value");

        // Check that two dates are one after another
        if (periodStartDate != null && periodEndDate != null && periodStartDate.compareTo(periodEndDate) >= 0) {
            messages.error(new BundleKey("messages", "customFieldTemplate.periodIntervalIncorrect"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        CustomFieldPeriod period = null;
        // First check if any period matches the dates
        if (!customFieldPeriodMatched) {
            boolean strictMatch = false;
            if (cft.getInstance().getCalendar() != null) {
                period = cft.getInstance().getValuePeriod(periodStartDate, false);
                strictMatch = true;
            } else {
                period = cft.getInstance().getValuePeriod(periodStartDate, periodEndDate, false, false);
                if (period != null) {
                    strictMatch = period.isCorrespondsToPeriod(periodStartDate, periodEndDate, true);
                }
            }

            if (period != null) {
                customFieldPeriodMatched = true;
                ParamBean paramBean = ParamBean.getInstance();
                String datePattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");

                // For a strict match need to edit an existing period
                if (strictMatch) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound.noNew"),
                        DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern), DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
                    customFieldPeriodMatched = false;

                    // For a non-strict match user has an option to create a period with a higher priority
                } else {
                    messages.warn(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound"), DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern),
                        DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
                    customFieldPeriodMatched = true;
                }
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        // Create period if passed a period check or if user decided to create it anyway
        if (cft.getInstance().getCalendar() != null) {
            period = cft.getInstance().addValuePeriod(periodStartDate);

        } else {
            period = cft.getInstance().addValuePeriod(periodStartDate, periodEndDate);
        }

        // Set value
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            period.getCfValue().setSingleValue(value, cft.getFieldType());
        } else {
            Map<String, Object> newValue = new HashMap<String, Object>();
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                newValue.put("key", key);
            }
            newValue.put("value", value);
            period.getCfValue().getMapValuesForGUI().add(newValue);
        }   

        customFieldNewValue.clear();
        customFieldPeriodMatched = false;
    }

    /**
     * Add value to a map of values, setting a default value if applicable
     * 
     * @param cft Custom field template corresponding to an instance
     */
    public void addValueToMap(CustomFieldValue cfv, CustomFieldTemplate cft) {

        String newKey = (String) customFieldNewValue.get(cft.getCode() + "_key");
        Object newValue = customFieldNewValue.get(cft.getCode() + "_value");

        Map<String, Object> value = new HashMap<String, Object>();
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            value.put(CustomFieldValue.MAP_KEY, newKey);
        }
        value.put(CustomFieldValue.MAP_VALUE, newValue);

        // Validate that key or value is not duplicate

        for (Map<String, Object> mapItem : cfv.getMapValuesForGUI()) {
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP && mapItem.get(CustomFieldValue.MAP_KEY).equals(newKey)) {
                messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyExists"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST && mapItem.get(CustomFieldValue.MAP_VALUE).equals(newValue)) {
                messages.error(new BundleKey("messages", "customFieldTemplate.listValueExists"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        cfv.getMapValuesForGUI().add(value);

        customFieldNewValue.clear();
    }

    public Map<String, Object> getCustomFieldNewValue() {
        return customFieldNewValue;
    }

    public void setCustomFieldNewValue(Map<String, Object> customFieldNewValue) {
        this.customFieldNewValue = customFieldNewValue;
    }

    public List<BusinessEntity> autocompleteCustomEntityForCFV(String wildcode) {
        String classname = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("classname");
        return customFieldInstanceService.findBusinessEntityForCFVByCode(classname, wildcode, this.currentProvider);
    }
    
    /**
     * Get a list of custom field templates applicable to an entity.
     * 
     * @return A list of custom field templates
     */
    protected List<CustomFieldTemplate> getApplicateCustomFieldTemplates() {
        AccountLevelEnum accountLevel = this.getClazz().getAnnotation(CustomFieldEntity.class).accountLevel();
        List<CustomFieldTemplate> result= customFieldTemplateService.findByAccountLevel(accountLevel);
        log.debug("Found {} custom field templates by fieldType={} for {}",result.size(),accountLevel,this.getClass());
        return result;
    }
}