package org.meveo.service.script;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

public class GenericImportScript extends Script {

	private CustomFieldTemplateService customFieldTemplateService =(CustomFieldTemplateService) getServiceInterface("CustomFieldTemplateService");

	public Object parseStringCf(String cftCode, String stringCF) {
		if (StringUtils.isEmpty(stringCF)) {
            return stringCF;
        }
		CustomFieldTemplate cft=customFieldTemplateService.findByCodeAndAppliesTo(cftCode, "Subscription");
		if (cft == null) {
            throw new BusinessException("No Custom Field exist on Subscription with code "+cftCode);
        }
		CustomFieldStorageTypeEnum storageType = cft.getStorageType();
        

		switch (storageType) {
		case SINGLE:
			return parseSingleValue(cft, stringCF);
		case MATRIX:
			Map<String, Object> matrix = new HashMap<>();
			final List<CustomFieldMatrixColumn> matrixKeys = cft.getMatrixKeyColumns();
			final List<CustomFieldMatrixColumn> matrixValues = cft.getMatrixValueColumns();
			if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
				List<String> stringCFLines = stringCF.contains("\n") ? Arrays.asList(stringCF.split("\n")) : Arrays.asList(stringCF);
				for (String stringCFLine : stringCFLines) {
					List<String> list = Arrays.asList(stringCFLine.split("\\|"));
					
					final int keySize = matrixKeys.size();
					if (list == null || list.size() != (keySize + matrixValues.size())) {
						throw new ValidationException("Not valid String representation of MATRIX Custom Field : " + cft.getCode() + "/" + stringCF);
					}
					String key = "";
					String value = "";
					for (String s : list.subList(0, keySize)) {
						key = key + "|" + s;
					}
					for (String s : list.subList(keySize, list.size())) {
						value = value + "|" + s;
					}
					matrix.put(key, value);
				}
			} else {
				List<String> stringCFLines = stringCF.contains("\n") ? Arrays.asList(stringCF.split("\n")) : Arrays.asList(stringCF);
				for (String stringCFLine : stringCFLines) {
					List<String> list = Arrays.asList(stringCFLine.split("\\|"));
					final int keySize = matrixKeys.size();
					if (list == null || list.size() != (keySize + 1)) {
						throw new ValidationException("Not valid String representation of MATRIX Custom Field : " + cft.getCode() + "/" + stringCF);
					}
					String key = "";
					for (String s : list.subList(0, keySize)) {
						key = key + "|" + s;
					}
					matrix.put(key, parseSingleValue(cft, list.get(list.size() - 1)));
				}
			}
			return matrix;
		case MAP:
			Map<String, Object> map = new HashMap<>();
			if(stringCF.isEmpty()) {
				return map;
			}
			List<String> stringCFLines = stringCF.contains("\n") ? Arrays.asList(stringCF.split("\n")) : Arrays.asList(stringCF);
			
			for (String stringCFLine : stringCFLines) {
				List<String> list = Arrays.asList(stringCFLine.split("\\|"));
				if (list == null || list.size() != 2) {
					throw new ValidationException("Not valid String representation of MAP Custom Field : " + cft.getCode() + "/" + stringCF);
				}
				String key = list.get(0);
				map.put(key, parseSingleValue(cft, list.get(1)));
			}
			return map;
		case LIST:
			// TODO
			return stringCF;
		default:
			return stringCF;
		}
	}

	private static Object parseSingleValue(CustomFieldTemplate cft, String stringCF) {
		if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
		    return Double.parseDouble(stringCF);
		} else if (cft.getFieldType() == CustomFieldTypeEnum.BOOLEAN) {
		    return Boolean.parseBoolean(stringCF);
		} else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
		    return Long.parseLong(stringCF);
		} else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST || cft.getFieldType() == CustomFieldTypeEnum.CHECKBOX_LIST
		        || cft.getFieldType() == CustomFieldTypeEnum.TEXT_AREA) {
		    return stringCF;
		} else if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
			Date date = DateUtils.validateParseDate(stringCF);
			if(!StringUtils.isBlank(stringCF) && date ==null) {
				throw new ValidationException("Invalid date format : "+stringCF);
			}
			return date;
		} else {
			throw new ValidationException("NOT YET IMPLEMENTED");
		}
	}
}