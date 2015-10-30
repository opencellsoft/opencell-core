package org.meveo.service.base;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.Auditable;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

@Stateless
public class CustomFieldService<P extends IEntity> extends PersistenceService<P> {

	@Inject
	CustomFieldTemplateService cfTemplateService;

	private ParamBean paramBean = ParamBean.getInstance();
	
	/**
	 * 
	 * @param code
	 * @param defaultParamBeanValue
	 * @param entity
	 * @param saveInCFIfNotExist
	 * @param accountLevelEnum
	 * @param user
	 * @return
	 */
	public Object getCustomFieldOrProperty(String code, String defaultParamBeanValue, ICustomFieldEntity entity, boolean saveInCFIfNotExist, AccountLevelEnum accountLevelEnum, User user) {
		return getCustomFieldOrProperty(CustomFieldStorageTypeEnum.SINGLE, null, code, defaultParamBeanValue, entity, saveInCFIfNotExist, accountLevelEnum, user);
	}

	/**
	 * 
	 * @param group
	 * @param code
	 * @param defaultParamBeanValue
	 * @param entity
	 * @param saveInCFIfNotExist
	 * @param accountLevelEnum
	 * @param user
	 * @return
	 */
	public Object getCustomFieldOrProperty(String group, String code, String defaultParamBeanValue, ICustomFieldEntity entity, boolean saveInCFIfNotExist, AccountLevelEnum accountLevelEnum, User user) {
		return getCustomFieldOrProperty(CustomFieldStorageTypeEnum.MAP, group, code, defaultParamBeanValue, entity, saveInCFIfNotExist, accountLevelEnum, user);
	}

	/**
	 * 
	 * @param storageType
	 * @param group
	 * @param code
	 * @param defaultParamBeanValue
	 * @param entity
	 * @param saveInCFIfNotExist
	 * @param accountLevelEnum
	 * @param user
	 * @return
	 */
	private Object getCustomFieldOrProperty(CustomFieldStorageTypeEnum storageType, String group, String code, String defaultParamBeanValue, ICustomFieldEntity entity, boolean saveInCFIfNotExist, AccountLevelEnum accountLevelEnum, User user) {
		Object result = null;
		log.debug("getCustomFieldOrProperty code '{}' defaultParamBeanValue '{}'", code, defaultParamBeanValue);
		if (entity.getCustomFields() != null) {
			result = entity.getCustomFields().containsKey(code) ? entity.getCustomFields().get(code) : null;
		}
		if (result == null) {
			log.debug("no CustomFieldInstances for '{}' on entity '{}'", code, entity.getClass().getName());
			CustomFieldTemplate cfTemplate = cfTemplateService.findByCodeAndAccountLevel(code, accountLevelEnum, user.getProvider());
			if (cfTemplate == null) {
				log.debug("no CustomFieldTemplates for '{}' on entity '{}'", code, entity.getClass().getName());
				cfTemplate = new CustomFieldTemplate();
				cfTemplate.setCode(code);
				cfTemplate.setAccountLevel(accountLevelEnum);
				cfTemplate.setActive(true);
				cfTemplate.setDescription(code);
				cfTemplate.setFieldType(CustomFieldTypeEnum.STRING);
				cfTemplate.setStorageType(storageType);
				if (CustomFieldStorageTypeEnum.SINGLE == storageType) {
					cfTemplate.setDefaultValue(defaultParamBeanValue);
				} else {
					cfTemplate.getListValues().put(group, defaultParamBeanValue);
				}
				cfTemplate.setValueRequired(false);
				Auditable a = new Auditable(user);
				cfTemplate.setAuditable(a);
				cfTemplateService.create(cfTemplate, user, user.getProvider());
				log.debug("create CFT done");
			} else {
				log.debug("CustomFieldTemplates for '{}'  is '{}'", code, cfTemplate);
				if (CustomFieldStorageTypeEnum.MAP == storageType) {
					if (cfTemplate.getListValues().containsKey(code)) {
						return cfTemplate.getListValues().get(code);
					} else {
						cfTemplate.getListValues().put(code, defaultParamBeanValue);
						cfTemplateService.update(cfTemplate);
						return cfTemplate;
					}
				}
			}
			log.debug("create CFI with value '{}' ...", paramBean.getProperty(code, defaultParamBeanValue));
			CustomFieldInstance cfInstance = new CustomFieldInstance();
			cfInstance.setCode(code);
			cfInstance.setStringValue(paramBean.getProperty(code, defaultParamBeanValue));
			Auditable a = new Auditable(user);
			cfInstance.setAuditable(a);
			log.debug("create CFI without db persist");
			if (saveInCFIfNotExist) {
				if (entity instanceof AccountEntity) {
					cfInstance.setAccount((AccountEntity) entity);
				} else if (entity instanceof Subscription) {
					cfInstance.setSubscription((Subscription) entity);
				} else if (entity instanceof Access) {
					cfInstance.setAccess((Access) entity);
				} else if (entity instanceof ChargeTemplate) {
					cfInstance.setChargeTemplate((ChargeTemplate) entity);
				} else if (entity instanceof ServiceTemplate) {
					cfInstance.setServiceTemplate((ServiceTemplate) entity);
				} else if (entity instanceof OfferTemplate) {
					cfInstance.setOfferTemplate((OfferTemplate) entity);
				} else if (entity instanceof JobInstance) {
					cfInstance.setJobInstance((JobInstance) entity);
				} else if (entity instanceof Provider) {
					cfInstance.setProvider((Provider) entity);
				} else if (entity instanceof Seller) {
					cfInstance.setSeller((Seller) entity);
				}
				entity.getCustomFields().put(code, cfInstance);
				update((P) entity, user);
				log.debug("create CFI and db persist");
			}
			result = cfInstance;
		} else {
			log.debug("CustomFieldInstance found for '{}' is '{}'", code, result);
		}
		return result;
	}
}
