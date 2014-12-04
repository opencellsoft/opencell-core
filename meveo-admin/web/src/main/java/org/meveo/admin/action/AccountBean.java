package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.meveo.model.AccountEntity;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

public abstract class AccountBean<T extends AccountEntity> extends BaseBean<T> {

	private static final long serialVersionUID = 3407699633028715707L;

	@Inject
	protected CustomFieldTemplateService customFieldTemplateService;

	@Inject
	protected CustomFieldInstanceService customFieldInstanceService;

	protected List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();

	public AccountBean() {

	}

	public AccountBean(Class<T> clazz) {
		super(clazz);
	}

	protected void initCustomFields(AccountLevelEnum accountLevel) {
		customFieldTemplates = customFieldTemplateService
				.findByAccountLevel(accountLevel);
		if (customFieldTemplates != null && customFieldTemplates.size() > 0
				&& !getEntity().isTransient()) {
			for (CustomFieldTemplate cf : customFieldTemplates) {
				CustomFieldInstance cfi = customFieldInstanceService
						.findByCodeAndAccount(cf.getCode(), getEntity());
				if (cfi != null) {
					if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
						cf.setDateValue(cfi.getDateValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
						cf.setDoubleValue(cfi.getDoubleValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
						cf.setLongValue(cfi.getLongValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.STRING) {
						cf.setStringValue(cfi.getStringValue());
					}
				}
			}
		}
	}

	protected void saveCustomFields() {
		if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
			for (CustomFieldTemplate cf : customFieldTemplates) {
				CustomFieldInstance cfi = customFieldInstanceService
						.findByCodeAndAccount(cf.getCode(), getEntity());
				if (cfi != null) {
					if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
						cfi.setDateValue(cf.getDateValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
						cfi.setDoubleValue(cf.getDoubleValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
						cfi.setLongValue(cf.getLongValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.STRING) {
						cfi.setStringValue(cf.getStringValue());
					}
				} else {
					// create
					cfi = new CustomFieldInstance();
					cfi.setCode(cf.getCode());
					cfi.setAccount(getEntity());

					if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
						cfi.setDateValue(cf.getDateValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
						cfi.setDoubleValue(cf.getDoubleValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
						cfi.setLongValue(cf.getLongValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.STRING) {
						cfi.setStringValue(cf.getStringValue());
					}

					customFieldInstanceService.create(cfi, getCurrentUser(),
							getCurrentProvider());
				}
			}
		}
	}

	public List<CustomFieldTemplate> getCustomFieldTemplates() {
		return customFieldTemplates;
	}

	public void setCustomFieldTemplates(
			List<CustomFieldTemplate> customFieldTemplates) {
		this.customFieldTemplates = customFieldTemplates;
	}

}
