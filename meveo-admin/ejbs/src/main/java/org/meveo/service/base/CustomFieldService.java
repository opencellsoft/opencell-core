package org.meveo.service.base;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.Auditable;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

@Stateless
public class CustomFieldService<P extends IEntity> extends
		PersistenceService<P> {

	@Inject
	CustomFieldTemplateService cfTemplateService;

	private ParamBean paramBean = ParamBean.getInstance();

	public Object getCustomFieldOrProperty(String code,
			String defaultParamBeanValue, ICustomFieldEntity entity,
			boolean saveInCFIfNotExist, AccountLevelEnum accountLevelEnum,
			User user) {
		Object result = entity.getCustomFields().containsKey(code) ? entity
				.getCustomFields().get(code) : null;

		if (result == null) {

			CustomFieldTemplate cfTemplate = cfTemplateService
					.findByCodeAndAccountLevel(code, accountLevelEnum,
							user.getProvider());

			if (cfTemplate == null) {
				cfTemplate = new CustomFieldTemplate();
				cfTemplate.setCode(code);
				cfTemplate.setAccountLevel(accountLevelEnum);
				cfTemplate.setActive(true);
				cfTemplate.setDescription(code);
				cfTemplate.setFieldType(CustomFieldTypeEnum.STRING);
				cfTemplate.setDefaultValue(defaultParamBeanValue);
				cfTemplate.setValueRequired(false);
				Auditable a = new Auditable(user);
				cfTemplate.setAuditable(a);
				cfTemplateService.create(cfTemplate, user, user.getProvider());
			}

			CustomFieldInstance cfInstance = new CustomFieldInstance();
			cfInstance.setCode(code);
			cfInstance.setStringValue(paramBean.getProperty(code,
					defaultParamBeanValue));
			Auditable a = new Auditable(user);
			cfInstance.setAuditable(a);

			if (saveInCFIfNotExist) {
				entity.getCustomFields().put(code, cfInstance);
				update((P) entity, user);
			}
			return cfInstance;
		}

		return result;
	}
}
