package org.meveo.service.script;

import org.meveo.admin.exception.ValidationException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

import java.util.ArrayList;
import java.util.Map;

public class GenericMassImportScript extends GenericImportScript {

	private final transient CustomFieldTemplateService customFieldTemplateService = (CustomFieldTemplateService) getServiceInterface(
			"CustomFieldTemplateService");
	private final transient CustomFieldInstanceService customFieldInstanceService = (CustomFieldInstanceService) getServiceInterface(
			"CustomFieldInstanceService");

	protected void setCFValues(Map<String, Object> recordMap, ICustomFieldEntity entity, String entityName) {
		@SuppressWarnings("unchecked")
		ArrayList<String> cfs = (ArrayList<String>) recordMap.get("OC_CFS");

		cfs.forEach(cf -> {
			if (!cf.contains(":")) return;
			String[] cfSplit = cf.split(":", 2);
			String cfCode = cfSplit[0];
			String cfValue = cfSplit[1];
			CustomFieldTemplate cfTemplate = customFieldTemplateService.findByCode(cfCode);
			if (cfTemplate == null) {
				throw new ValidationException("no custom field template found for code: '" + cfCode + "'");
			}
			Object cfObjectValue = parseStringCf(cfCode, cfValue, entityName);
			customFieldInstanceService.setCFValue(entity, cfCode, cfObjectValue, null, null, 1);
		});
	}
}
