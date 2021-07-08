package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.medina.impl.AccessService;

public class AccessImportScript extends Script {

	private static final String DATE_FORMAT_PATTERN = "dd/MM/yy";
	private static final String RECORD_VARIABLE_NAME = "record";

	private AccessService accessService = (AccessService) getServiceInterface("AccessService");
	private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");

	@Override
	public void execute(Map<String, Object> context) throws BusinessException {
		try {
			Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
			if (recordMap != null && !recordMap.isEmpty()) {
				String OC_ENTITY = (String) recordMap.get("OC_ENTITY");
				if (!"AccessPoint".equals(OC_ENTITY)) {
					throw new ValidationException("value of OC_ENTITY is not correct: " + OC_ENTITY);
				}
				String OC_ACTION = (String) recordMap.get("OC_ACTION");
				if (!Stream.of(AccessActionEnum.values()).anyMatch(e -> e.toString().equals(OC_ACTION))) {
					throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
				}
				AccessActionEnum action = AccessActionEnum.valueOf(OC_ACTION);

				Access access = validateAndGetAccess(action, recordMap);

				if (AccessActionEnum.DELETE.equals(action)) {
					accessService.remove(access);
				} else {
					setAccessValues(recordMap, access);
					if (AccessActionEnum.CREATE.equals(action)) {
				        if (accessService.isDuplicateAndOverlaps(access)) {
				            throw new BusinessException( "Duplicate subscription/access point: '"+access.getSubscription().getCode()+"'/'"+access.getAccessUserId()+"'");
				        }
						accessService.create(access);
					} else if (AccessActionEnum.UPDATE.equals(action)) {
						accessService.update(access);
					}

				}
			}
		} catch (Exception exception) {
			throw new BusinessException(exception);
		}
	}

	private Access validateAndGetAccess(AccessActionEnum action, Map<String, Object> recordMap) {
		Access access = null;
		String OC_subscription_code = (String) recordMap.get("OC_subscription_code");
		String OC_Access_code = (String) recordMap.get("OC_accessPoint_code");
		List<Access> accessList = accessService.getActiveAccessByCodeAndCodeSubscription(OC_Access_code, OC_subscription_code);
		if (!AccessActionEnum.CREATE.equals(action)) {
			if (accessList == null || accessList.isEmpty()) {
				throw new ValidationException("no Access found for subscriptionCode/AccessCode: '"
						+ OC_subscription_code + "'/'" + OC_Access_code + "'");
			}
			access = accessList.get(0);
		} else {
			access = new Access();
			Subscription subscription = subscriptionService.findByCode(OC_subscription_code);
			if (subscription == null) {
				throw new ValidationException("no subscription found for code: '" + OC_subscription_code + "'");
			}
			access.setSubscription(subscription);
			access.setAccessUserId(OC_Access_code);
		}
		return access;
	}

	private void setAccessValues(Map<String, Object> recordMap, Access access) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		String OC_accessPoint_startDate = (String) recordMap.get("OC_accessPoint_startDate");
		String OC_accessPoint_endDate = (String) recordMap.get("OC_accessPoint_endDate");
		Date startDate = StringUtils.isEmpty(OC_accessPoint_startDate) ? null : dateFormat.parse(OC_accessPoint_startDate);
		Date endDate = StringUtils.isEmpty(OC_accessPoint_endDate) ? null : dateFormat.parse(OC_accessPoint_endDate);
		access.setEndDate(endDate);
		access.setStartDate(startDate);

		recordMap.keySet().stream().filter(key -> key.startsWith("CF_"))
				.forEach(key -> access.setCfValue(key.substring(3), recordMap.get(key)));
	}

	public enum AccessActionEnum {
		CREATE, UPDATE, DELETE
	}
}