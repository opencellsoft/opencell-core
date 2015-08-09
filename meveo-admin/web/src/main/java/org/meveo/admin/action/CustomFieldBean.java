package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.wrapper.BaseWrapper;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomEntitySearchService;
import org.meveo.service.crm.impl.CustomFieldJob;
import org.meveo.util.serializable.SerializableUtil;

/**
 * support custom field instances
 *
 * @param <T>
 */
public abstract class CustomFieldBean<T extends IEntity> extends BaseBean<T> {

	private static final long serialVersionUID = 1L;
	
	private CustomFieldTemplate customFieldSelectedTemplate;
    /**
     * New custom field period
     */
    private CustomFieldPeriod customFieldNewPeriod;

    private boolean customFieldPeriodMatched;
    
    @Inject
    private CustomFieldJob customFieldJob;

    /**
     * Custom field templates
     */
    protected List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();
    
    @Inject
    private CustomEntitySearchService cfSearchService;
    
    private BaseWrapper baseWrapper;
    

	public CustomFieldBean() {
	}

	public CustomFieldBean(Class<T> clazz) {
		super(clazz);
	}
	protected abstract IPersistenceService<T> getPersistenceService();
	
	@Override
	public T initEntity() {
		T result=super.initEntity();
		initCustomFields();
		return result;
	}

	@Override
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {
		updateCustomFieldsInEntity();
		return super.saveOrUpdate(killConversation);
	}
	/**
     * Load available custom fields (templates) and their values
     */
	protected void initCustomFields() {

        if (!this.getClass().isAnnotationPresent(CustomFieldEnabledBean.class)) {
            return;
        }

        customFieldTemplates = getApplicateCustomFieldTemplates();

        if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
            for (CustomFieldTemplate cf : customFieldTemplates) {
                CustomFieldInstance cfi = ((ICustomFieldEntity) entity).getCustomFields().get(cf.getCode());
                if (cfi == null) {
                    cf.setInstance(CustomFieldInstance.fromTemplate(cf));
                } else{
                	cfi=SerializableUtil.initCustomField(cf, cfi, cfSearchService);
                	cf.setInstance(cfi);
                }
            }
        }
    }

    private void updateCustomFieldsInEntity(){

        if (!this.getClass().isAnnotationPresent(CustomFieldEnabledBean.class) || customFieldTemplates == null || customFieldTemplates.isEmpty()) {
            return;
        }

        for (CustomFieldTemplate cf : customFieldTemplates) {
            CustomFieldInstance cfi = SerializableUtil.updateCustomField(cf);
       	     // Not saving empty values
            if (cfi.isValueEmpty()) {
                if (!cfi.isTransient()) {
                    ((ICustomFieldEntity) entity).getCustomFields().remove(cfi.getCode());
                    log.debug("remove cfi {}",cfi.getCode());
                }
                // Existing value update
            } else{
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
            		}else if (entity instanceof Provider) {
            			cfi.setProvider((Provider)entity);
            		}
            	}
            	((ICustomFieldEntity) entity).getCustomFields().put(cfi.getCode(), cfi);
            }
       }
    }
    public List<CustomFieldTemplate> getCustomFieldTemplates() {
    	if(customFieldTemplates==null||customFieldTemplates.size()==0){
    		if(entity!=null){
    			initCustomFields();
    		}else{
    			initEntity();
    		}
    	}
        return customFieldTemplates;
    }

    public void setCustomFieldTemplates(List<CustomFieldTemplate> customFieldTemplates) {
        this.customFieldTemplates = customFieldTemplates;
    }

    public CustomFieldPeriod getCustomFieldNewPeriod() {
        return customFieldNewPeriod;
    }

    public void setCustomFieldNewPeriod(CustomFieldPeriod customFieldNewPeriod) {
        this.customFieldNewPeriod = customFieldNewPeriod;
    }

    public void setCustomFieldSelectedTemplate(CustomFieldTemplate customFieldSelectedTemplate) {
        this.customFieldSelectedTemplate = customFieldSelectedTemplate;
        this.customFieldPeriodMatched = false;
        // Set a default value for new period data entry
        this.customFieldNewPeriod = new CustomFieldPeriod();
        this.customFieldNewPeriod.setDefaultValue(customFieldSelectedTemplate.getDefaultValueConverted(), customFieldSelectedTemplate.getFieldType(),customFieldSelectedTemplate.getStorageType());
    }

    public CustomFieldTemplate getCustomFieldSelectedTemplate() {
        return customFieldSelectedTemplate;
    }
    public boolean isCustomFieldPeriodMatched() {
        return customFieldPeriodMatched;
    }

    /**
     * Add a new customField period with a previous validation that matching period does not exists
     */
    public void addNewCustomFieldPeriod() {

        // Check that two dates are one after another
        if (customFieldNewPeriod.getPeriodStartDate() != null && customFieldNewPeriod.getPeriodEndDate() != null
                && customFieldNewPeriod.getPeriodStartDate().compareTo(customFieldNewPeriod.getPeriodEndDate()) >= 0) {
            messages.error(new BundleKey("messages", "customFieldTemplate.periodIntervalIncorrect"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        CustomFieldPeriod period = null;
        // First check if any period matches the dates
        if (!customFieldPeriodMatched) {
            if (customFieldSelectedTemplate.getInstance().getCalendar() != null) {
                period = customFieldSelectedTemplate.getInstance().getValuePeriod(customFieldNewPeriod.getPeriodStartDate(), false);
            } else {
                period = customFieldSelectedTemplate.getInstance().getValuePeriod(customFieldNewPeriod.getPeriodStartDate(), customFieldNewPeriod.getPeriodEndDate(), false, false);
            }

            if (period != null) {
                customFieldPeriodMatched = true;
                ParamBean paramBean = ParamBean.getInstance();
                String datePattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");

                if (customFieldSelectedTemplate.getInstance().getCalendar() != null) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound.noNew"),
                        DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern), DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
                } else {
                    messages.warn(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound"), DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern),
                        DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
                }
                FacesContext.getCurrentInstance().validationFailed();
                customFieldPeriodMatched = true;
                return;
            }
        }

        // Create period if passed period check or if user decided to create it anyway
        if (customFieldSelectedTemplate.getInstance().getCalendar() != null) {
            period = customFieldSelectedTemplate.getInstance().addValuePeriod(customFieldNewPeriod.getPeriodStartDate(), customFieldNewPeriod.getValue(),customFieldNewPeriod.getLabel(),
                customFieldSelectedTemplate.getFieldType(),customFieldSelectedTemplate.getStorageType());
            
        } else {
            period = customFieldSelectedTemplate.getInstance().addValuePeriod(customFieldNewPeriod.getPeriodStartDate(), customFieldNewPeriod.getPeriodEndDate(),
                customFieldNewPeriod.getValue(),customFieldNewPeriod.getLabel(), customFieldSelectedTemplate.getFieldType(),customFieldSelectedTemplate.getStorageType());
        }
        
        if (customFieldSelectedTemplate.isVersionable()
				&& customFieldSelectedTemplate.getCalendar() != null
				&& customFieldSelectedTemplate.isTriggerEndPeriodEvent()) {
			// create a timer
			customFieldJob.triggerEndPeriodEvent(customFieldSelectedTemplate.getInstance(),
					customFieldNewPeriod.getPeriodEndDate());
		}
        
        customFieldNewPeriod = null;
        customFieldPeriodMatched = false;
    }

	public BaseWrapper getBaseWrapper() {
		return baseWrapper;
	}

	public void setBaseWrapper(BaseWrapper baseWrapper) {
		this.baseWrapper = baseWrapper;
	}

}