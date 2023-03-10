package org.meveo.service.script;

import java.math.BigDecimal;
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
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;

public class ServiceImportScript extends Script {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String RECORD_VARIABLE_NAME = "record";

    private ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface("ServiceInstanceService");
    CustomFieldTemplateService customFieldTemplateService = (CustomFieldTemplateService) getServiceInterface("CustomFieldTemplateService");
    private ServiceTemplateService serviceTemplateService = (ServiceTemplateService) getServiceInterface("ServiceTemplateService");
    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
    private SubscriptionTerminationReasonService reasonService = (SubscriptionTerminationReasonService) getServiceInterface("SubscriptionTerminationReasonService");

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        try {
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
            if (recordMap != null && !recordMap.isEmpty()) {
                DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
                String OC_ENTITY = (String) recordMap.get("OC_ENTITY");
                if (!"ServiceInstance".equals(OC_ENTITY)) {
                    throw new ValidationException("value of OC_ENTITY is not correct: " + OC_ENTITY);
                }
                String OC_ACTION = (String) recordMap.get("OC_ACTION");
                if (!Stream.of(ServiceInstanceActionEnum.values()).anyMatch(e -> e.toString().equals(OC_ACTION))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
                }
                ServiceInstanceActionEnum action = ServiceInstanceActionEnum.valueOf(OC_ACTION);

                String OC_Subscription_code = (String) recordMap.get("OC_Subscription_code");
                String OC_ServiceInstance_code = (String) recordMap.get("OC_ServiceInstance_code");
                List<ServiceInstance> serviceInstances = serviceInstanceService.findByCodeAndCodeSubscription(OC_ServiceInstance_code, OC_Subscription_code);

                ServiceInstance serviceInstance = null;
                if (action == ServiceInstanceActionEnum.INSTANTIATE) {
                    serviceInstance = new ServiceInstance();
                    mapServiceInstanceFields(recordMap, serviceInstance);
                    serviceInstance.setCode(OC_ServiceInstance_code);
                    Subscription subscription = subscriptionService.findByCode(OC_Subscription_code);
                    if (subscription == null) {
                        throw new ValidationException("no Subscription found with subscriptionCode: '" + OC_Subscription_code + "'");
                    }
                    serviceInstance.setSubscription(subscription);
                    ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(OC_ServiceInstance_code);
                    if (serviceTemplate == null) {
                        throw new ValidationException("no serviceTemplate found with code: '" + OC_ServiceInstance_code + "'");
                    }
                    serviceInstance.setServiceTemplate(serviceTemplate);
                    serviceInstanceService.create(serviceInstance);
                } else {
                    if (serviceInstances == null || serviceInstances.isEmpty()) {
                        throw new ValidationException("no ServiceInstanceFound for subscriptionCode/serviceInstanceCode: '" + OC_Subscription_code + "'/'" + OC_ServiceInstance_code + "'");
                    }
                    serviceInstance = serviceInstances.get(0);
                }

                switch (action) {
                case INSTANTIATE:
                    serviceInstanceService.serviceInstanciation(serviceInstance);
                    break;
                case ACTIVATE:
                    serviceInstanceService.serviceActivation(serviceInstance);
                    break;
                case RESUME:
                    serviceInstanceService.serviceReactivation(serviceInstance, new Date(), true, false);
                    break;
                case SUSPEND:
                    serviceInstanceService.serviceSuspension(serviceInstance, new Date());
                    break;
                case TERMINATE:
                    String terminationDate = (String) recordMap.get("OC_terminationDate");
                    Date OC_terminationDate = StringUtils.isEmpty(terminationDate) ? new Date() : dateFormat.parse(terminationDate);
                    String OC_terminationReason = (String) recordMap.get("OC_terminationReason");
                    SubscriptionTerminationReason reason = reasonService.findByCodeReason(OC_terminationReason);
                    if (reason == null) {
                        throw new ValidationException("no TerminationReason found with code: '" + OC_terminationReason + "'");
                    }
                    serviceInstanceService.terminateService(serviceInstance, OC_terminationDate, reason, serviceInstance.getOrderNumber());
                    break;
                case UPDATE:
                    updateService(recordMap, serviceInstance);
                    break;
                default:
                    break;
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private void updateService(Map<String, Object> recordMap, ServiceInstance serviceInstance) throws Exception {
        mapServiceInstanceFields(recordMap, serviceInstance);
        serviceInstanceService.update(serviceInstance);
    }

    private void mapServiceInstanceFields(Map<String, Object> recordMap, ServiceInstance serviceInstance) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        String OC_ServiceInstance_subscriptionDate = (String) recordMap.get("OC_ServiceInstance_subscriptionDate");
        String OC_ServiceInstance_rateUntilDate = (String) recordMap.get("OC_ServiceInstance_rateUntilDate");
        String OC_ServiceInstance_endAgreementDate = (String) recordMap.get("OC_ServiceInstance_endAgreementDate");

        Date subscriptionDate = StringUtils.isEmpty(OC_ServiceInstance_subscriptionDate) ? null : dateFormat.parse(OC_ServiceInstance_subscriptionDate);
        Date rateUntilDate = StringUtils.isEmpty(OC_ServiceInstance_rateUntilDate) ? null : dateFormat.parse(OC_ServiceInstance_rateUntilDate);
        Date endAgreementDate = StringUtils.isEmpty(OC_ServiceInstance_endAgreementDate) ? null : dateFormat.parse(OC_ServiceInstance_endAgreementDate);
        String OC_ServiceInstance_description = (String) recordMap.get("OC_ServiceInstance_description");
        String OC_ServiceInstance_quantity = (String) recordMap.get("OC_ServiceInstance_quantity");

        serviceInstance.setSubscriptionDate(subscriptionDate);
        serviceInstance.setRateUntilDate(rateUntilDate);
        serviceInstance.setEndAgreementDate(endAgreementDate);
        serviceInstance.setQuantity(new BigDecimal(OC_ServiceInstance_quantity));
        serviceInstance.setDescription(OC_ServiceInstance_description);
        recordMap.keySet().stream().filter(key -> key.startsWith("CF_")).forEach(key -> serviceInstance.setCfValue(key.substring(3), parseStringCf(key.substring(3), (String) recordMap.get(key))));
    }

    public enum ServiceInstanceActionEnum {
        INSTANTIATE, ACTIVATE, RESUME, SUSPEND, TERMINATE, UPDATE
    }

    public Object parseStringCf(String cftCode, String stringCF) {

        if (StringUtils.isEmpty(stringCF)) {
            return stringCF;
        }
        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(cftCode, "ServiceInstance");
        if (cft == null) {
            throw new BusinessException("No Custom Field exist on ServiceInstance with code " + cftCode);
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