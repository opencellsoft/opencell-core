package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.mediation.Access;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.medina.impl.AccessService;

public class AccessImportScript extends Script {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String RECORD_VARIABLE_NAME = "record";

    private AccessService accessService = (AccessService) getServiceInterface("AccessService");
    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
    CustomFieldTemplateService customFieldTemplateService = (CustomFieldTemplateService) getServiceInterface("CustomFieldTemplateService");

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
                            throw new BusinessException("Duplicate subscription/access point: '" + access.getSubscription().getCode() + "'/'" + access.getAccessUserId() + "'");
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
        String OC_subscription_code = (String) recordMap.get("OC_Subscription_code");
        String OC_Access_code = (String) recordMap.get("OC_accessPoint_code");
        List<Access> accessList = accessService.getActiveAccessByCodeAndCodeSubscription(OC_Access_code, OC_subscription_code);
        if (!AccessActionEnum.CREATE.equals(action)) {
            if (accessList == null || accessList.isEmpty()) {
                throw new ValidationException("no Access found for subscriptionCode/AccessCode: '" + OC_subscription_code + "'/'" + OC_Access_code + "'");
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

        recordMap.keySet().stream().filter(key -> key.startsWith("CF_")).forEach(key -> access.setCfValue(key.substring(3), parseStringCf(key.substring(3), (String) recordMap.get(key))));
    }

    public enum AccessActionEnum {
        CREATE, UPDATE, DELETE
    }

    public Object parseStringCf(String cftCode, String stringCF) {

        if (StringUtils.isEmpty(stringCF)) {
            return stringCF;
        }
        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(cftCode, "Access");
        if (cft == null) {
            throw new BusinessException("No Custom Field exist on Access with code " + cftCode);
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
                        key = key != "" ? key + "|" + s : s;
                    }
                    for (String s : list.subList(keySize, list.size())) {
                        value = value != "" ? value + "|" + s : s;
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
                        key = key != "" ? key + "|" + s : s;
                    }
                    matrix.put(key, parseSingleValue(cft, list.get(list.size() - 1)));
                }
            }
            return matrix;
        case MAP:
            Map<String, Object> map = new HashMap<>();
            if (stringCF.isEmpty()) {
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
            return DateUtils.parseDate(stringCF);
        } else {
            throw new ValidationException("NOT YET IMPLEMENTED");
        }
    }
}