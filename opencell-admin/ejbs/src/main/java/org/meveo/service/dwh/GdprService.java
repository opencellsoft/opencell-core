/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.dwh;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BaseService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.CustomerService;

/**
 * General Data Protection Regulation (GDPR) service provides a feature that
 * anonymized the stored data.
 * 
 * @author Edward P. Legaspi
 * @author Mounir Boukayoua
 * @lastModifiedVersion 5.2<
 */
@Stateless
public class GdprService extends BaseService {

	private static final Date DATE_1900_01_01 = DateUtils.parseDateWithPattern("01/01/1900", "dd/MM/yyyy");

	@Inject
	private CustomerService customerService;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;
	
	@Inject
	private SubscriptionService subscriptionService;

	/**
	 * Anonymize a Customer and its children entities (CA, BA and UA)
	 * 
	 * @param customer Customer to anonymize
	 * @throws BusinessException Business Exception
	 */
	public void anonymize(Customer customer) throws BusinessException {
		String randomCode = UUID.randomUUID().toString();
		customerService.anonymizeGdpr(customer, randomCode);
		
		//anonymize cfValues of the customer
		// and those of its CAs, BAs, UAs and SUBs
		anonymizeCustomFields(customer);
		for (CustomerAccount ca : customer.getCustomerAccounts()) {
			anonymizeCustomFields(ca);
			for (BillingAccount ba : ca.getBillingAccounts()) {
				anonymizeCustomFields(ba);
				for (UserAccount ua : ba.getUsersAccounts()) {
					anonymizeCustomFields(ua);
					for (Subscription sub : ua.getSubscriptions()) {
						anonymizeCustomFields(sub);
						subscriptionService.update(sub);
					}
				}
			}
		}
	}
	
	/**
	 * GDPR anonymize CF Values of an entity
	 * 
	 * @param entity a Business CF Entity
	 */
	public void anonymizeCustomFields(BusinessCFEntity entity) {
		String randomCode = UUID.randomUUID().toString();
		Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(entity);
		Map<String, CustomFieldTemplate> anonymizableCftCodes = cfts.entrySet().stream()
				.filter(entry -> isAnonymizeGdpr(entry.getValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		Map<String, List<CustomFieldValue>> newCfValues = new HashMap<>();
		Map<String, List<CustomFieldValue>> cfValuesByCode = entity.getCfValuesNullSafe().getValuesByCode();

		cfValuesByCode.forEach((cftCode, previousCfValues) -> {
			if (anonymizableCftCodes.containsKey(cftCode)) {
				for (CustomFieldValue previousCfValue : previousCfValues) {
					CustomFieldTemplate cfTemplate = anonymizableCftCodes.get(cftCode);
					anonymizeCFValue(previousCfValue, cfTemplate, randomCode);
					newCfValues.computeIfAbsent(cftCode, k -> new ArrayList<>()).add(previousCfValue);
				}
			} else if (previousCfValues != null && !previousCfValues.isEmpty()) {
				newCfValues.put(cftCode, previousCfValues);
			}
		});
		entity.getCfValuesNullSafe().setValues(newCfValues);
	}

	/**
	 * if CustomField Template can be anonymizable
	 * 
	 * @param cft CustomField Template
	 * @return is CustomField anonymizable
	 */
	private boolean isAnonymizeGdpr(CustomFieldTemplate cft) {
		return cft.getFieldType() != CustomFieldTypeEnum.ENTITY
				&& cft.getFieldType() != CustomFieldTypeEnum.CHILD_ENTITY
				&& cft.isAnonymizeGdpr();
	}

	/**
	 * anonymize a cfValue based on its template
	 * 
	 * @param cfValue cfValue
	 * @param cfTemplate cfTemplate
	 * @param randomCode randomCode
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void anonymizeCFValue(CustomFieldValue cfValue, CustomFieldTemplate cfTemplate, String randomCode) {
		if (cfValue.isValueEmpty()) {
			return;
		}
		if (cfTemplate.getStorageType() == CustomFieldStorageTypeEnum.MATRIX
				|| cfTemplate.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
			Map mapValues = cfValue.getMapValue();
			mapValues.replaceAll((k, v) -> anonymizeMapValue(v, randomCode));
		} else if (cfTemplate.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
			List listValues = cfValue.getListValue();
			listValues.replaceAll(v -> v != null ? anonymizeValue(((Object)v).getClass(), randomCode) : null);
		} else if (cfTemplate.getStorageType() == CustomFieldStorageTypeEnum.SINGLE
				&& cfValue.getValue() != null) {
			cfValue.setValue(anonymizeValue(cfValue.getValue().getClass(), randomCode));
		}
	}
	
	/**
	 * anonymize a map value depending on that it's composed (containing |)
	 * or single value
	 * 
	 * @param mapValue
	 * @param randomCode
	 * @return
	 */
	private Object anonymizeMapValue(Object mapValue, String randomCode) {
		if (mapValue != null) {
			if (mapValue instanceof String) {
				String[] splitedVals = ((String)mapValue).split("\\|");
				String anonymizedValue = "";
				for (int i = 0; i < splitedVals.length; i++) {
					anonymizedValue += anonymizeValue(getObjectType(splitedVals[i]), randomCode) + "|";
				}
				return anonymizedValue.substring(0, anonymizedValue.length() - 1);
			} else {
				return anonymizeValue(((Object)mapValue).getClass(), randomCode);
			}
		}
		return null;
	}

	/**
	 * check the object type of a string value
	 * 
	 * @param value
	 * @return
	 */
	private Class<?> getObjectType(String value) {
		if (isMatch(value, "-?\\d+(\\.\\d+)")) {
			return Double.class;
		} else if (isMatch(value, "-?\\d+")) {
			return Long.class;
		} else {
			return String.class;
		}		
	}
	
	private boolean isMatch(String value, String regEx) {
        Pattern r = Pattern.compile(regEx);
        Matcher m = r.matcher(value);
        return m.matches();
	}

	/**
	 * anonymize a lateral value depending on its class Type
	 * 
	 * @param valueClass value class
	 * @param randomCode used to anonymise String values
	 * @return anonymized value
	 */
	private Object anonymizeValue(Class<?> valueClass, String randomCode) {
		if (valueClass == String.class) {
			return randomCode;
		} else if (valueClass == Date.class) {
			return DATE_1900_01_01;
		} else if (valueClass == Long.class) {
			return 0L;
		} else if (valueClass == Double.class) {
			return 0D;
		}
		return null;
	}
}
