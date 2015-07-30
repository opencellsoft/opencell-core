package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomEntitySearchService;
import org.meveo.service.crm.impl.CustomFieldJob;
import org.meveo.util.serializable.SerializableUtil;

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
	@SuppressWarnings("unchecked")
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
                } else {
                    if(CustomFieldStorageTypeEnum.SINGLE.equals(cf.getStorageType())){
               	 		if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
               	 			cfi.setBusinessEntity(SerializableUtil.decodeSingle(cfSearchService,cf.getEntityClazz(),cfi.getEntityValue()));
               	 		}
               	 	}else{
               	 		if(cf.isVersionable()){
               	 			for(CustomFieldPeriod cfp: cfi.getValuePeriods()){
               	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
               	 					cfp.setBusinessEntity(SerializableUtil.decodeSingle(cfSearchService,cf.getEntityClazz(),cfp.getEntityValue()));
               	 				}
               	 			}
               	 		}else{
               	 			if(CustomFieldStorageTypeEnum.LIST.equals(cf.getStorageType())){
               	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
               	 					cfi.setEntityList(SerializableUtil.decodeList(cfSearchService,cf.getEntityClazz(),cfi.getEntityValue()));
               	 				}else if(CustomFieldTypeEnum.STRING.equals(cf.getFieldType())){
               	 					cfi.setStringList((Set<String>)SerializableUtil.decode(cfi.getEntityValue()));
               	 				}else if(CustomFieldTypeEnum.LONG.equals(cf.getFieldType())){
               	 					cfi.setLongList((Set<Long>)SerializableUtil.decode(cfi.getEntityValue()));
               	 				}else if(CustomFieldTypeEnum.DOUBLE.equals(cf.getFieldType())){
               	 					cfi.setDoubleList((Set<Double>)SerializableUtil.decode(cfi.getEntityValue()));
               	 				}else if(CustomFieldTypeEnum.DATE.equals(cf.getFieldType())){
               	 					cfi.setDateList((Set<Date>)SerializableUtil.decode(cfi.getEntityValue()));
               	 				}
               	 			}else if(CustomFieldStorageTypeEnum.MAP.equals(cf.getStorageType())){
               	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
               	 					cfi.setEntityMap((Map<String,BusinessEntity>)SerializableUtil.decodeMap(cfSearchService,cf.getEntityClazz(),cfi.getEntityValue()));
               	 				}else if(CustomFieldTypeEnum.STRING.equals(cf.getFieldType())){
               	 					cfi.setStringMap((Map<String,String>)SerializableUtil.decode(cfi.getEntityValue()));
               	 				}else if(CustomFieldTypeEnum.LONG.equals(cf.getFieldType())){
               	 					cfi.setLongMap((Map<String,Long>)SerializableUtil.decode(cfi.getEntityValue()));
               	 				}else if(CustomFieldTypeEnum.DOUBLE.equals(cf.getFieldType())){
               	 					cfi.setDoubleMap((Map<String,Double>)SerializableUtil.decode(cfi.getEntityValue()));
               	 				}else if(CustomFieldTypeEnum.DATE.equals(cf.getFieldType())){
               	 					cfi.setDateMap((Map<String,Date>)SerializableUtil.decode(cfi.getEntityValue()));
               	 				}
               	 			}
               	 		}
               	 	}
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
            CustomFieldInstance cfi = cf.getInstance();
       	 	if(CustomFieldStorageTypeEnum.SINGLE.equals(cf.getStorageType())){
       	 		if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
       	 			BusinessEntity temp=cfi.getBusinessEntity();
       	 			BusinessEntity result=new BusinessEntity();
       	 			result.setId(temp.getId());
       	 			cfi.setEntityValue(SerializableUtil.encode(result));
       	 		}
       	 	}else {
       	 		if(cf.isVersionable()){
       	 			for(CustomFieldPeriod cfp: cfi.getValuePeriods()){
       	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
       	 					BusinessEntity temp=cfp.getBusinessEntity();
       	 					if(temp!=null){
       	 						BusinessEntity result=new BusinessEntity();
       	 						result.setId(temp.getId());
       	 						cfp.setEntityValue(SerializableUtil.encode(result));
       	 					}
       	 				}
       	 			}
       	 		}else if(CustomFieldStorageTypeEnum.LIST.equals(cf.getStorageType())){
	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
	 					Set<BusinessEntity> result=new HashSet<BusinessEntity>();
	 					BusinessEntity temp=null;
	 					for(BusinessEntity list:cfi.getEntityList()){
	 						temp=new BusinessEntity();
	 						temp.setId(list.getId());
	 						result.add(temp);
	 					}
	 					cfi.setEntityValue(cfi.getEntityList().size()==0?null:SerializableUtil.encode(result));
	 				}else if(CustomFieldTypeEnum.STRING.equals(cf.getFieldType())){
	 					cfi.setEntityValue(cfi.getStringList().size()==0?null:SerializableUtil.encode(cfi.getStringList()));
	 					cfi.setStringValue(null);
	 				}else if(CustomFieldTypeEnum.LONG.equals(cf.getFieldType())){
	 					cfi.setEntityValue(cfi.getLongList().size()==0?null:SerializableUtil.encode(cfi.getLongList()));
	 					cfi.setLongValue(null);
	 				}else if(CustomFieldTypeEnum.DOUBLE.equals(cf.getFieldType())){
	 					cfi.setEntityValue(cfi.getDoubleList().size()==0?null:SerializableUtil.encode(cfi.getDoubleList()));
	 					cfi.setDoubleValue(null);
	 				}else if(CustomFieldTypeEnum.DATE.equals(cf.getFieldType())){
	 					cfi.setEntityValue(cfi.getDateList().size()==0?null:SerializableUtil.encode(cfi.getDateList()));
	 					cfi.setDateValue(null);
	 				}
	 			}else if(CustomFieldStorageTypeEnum.MAP.equals(cf.getStorageType())){
	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
	 					Map<String,BusinessEntity> result=new HashMap<String,BusinessEntity>();
	 					BusinessEntity temp=null;
	 					for(Map.Entry<String, BusinessEntity> entry:cfi.getEntityMap().entrySet()){
	 						temp=new BusinessEntity();
	 						temp.setId(entry.getValue().getId());
	 						result.put(entry.getKey(),temp);
	 					}
	 					cfi.setEntityValue(cfi.getEntityMap().size()==0?null:SerializableUtil.encode(result));
	 				}else if(CustomFieldTypeEnum.STRING.equals(cf.getFieldType())){
	 					cfi.setEntityValue(cfi.getStringMap().size()==0?null:SerializableUtil.encode(cfi.getStringMap()));
	 					cfi.setStringValue(null);
	 				}else if(CustomFieldTypeEnum.LONG.equals(cf.getFieldType())){
	 					cfi.setEntityValue(cfi.getLongMap().size()==0?null:SerializableUtil.encode(cfi.getLongMap()));
	 					cfi.setLongValue(null);
	 				}else if(CustomFieldTypeEnum.DOUBLE.equals(cf.getFieldType())){
	 					cfi.setEntityValue(cfi.getDoubleMap().size()==0?null:SerializableUtil.encode(cfi.getDoubleMap()));
	 					cfi.setDoubleValue(null);
	 				}else if(CustomFieldTypeEnum.DATE.equals(cf.getFieldType())){
	 					cfi.setEntityValue(cfi.getDateMap().size()==0?null:SerializableUtil.encode(cfi.getDateMap()));
	 					cfi.setDateValue(null);
	 				}
	 			}
       	 	}
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
        this.customFieldNewPeriod.setValue(customFieldSelectedTemplate.getDefaultValueConverted(), customFieldSelectedTemplate.getFieldType());
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
            period = customFieldSelectedTemplate.getInstance().addValuePeriod(customFieldNewPeriod.getPeriodStartDate(), customFieldNewPeriod.getValue(),
                customFieldSelectedTemplate.getFieldType(),customFieldNewPeriod.getLabel(),customFieldSelectedTemplate.getStorageType());
        } else {
            period = customFieldSelectedTemplate.getInstance().addValuePeriod(customFieldNewPeriod.getPeriodStartDate(), customFieldNewPeriod.getPeriodEndDate(),
                customFieldNewPeriod.getValue(), customFieldSelectedTemplate.getFieldType(),customFieldNewPeriod.getLabel(),customFieldSelectedTemplate.getStorageType());
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

}