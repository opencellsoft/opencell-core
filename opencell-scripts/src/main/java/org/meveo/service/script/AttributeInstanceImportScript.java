package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.billing.AttributeInstanceDto;
import org.meveo.api.dto.billing.ServiceToUpdateDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.cpq.AttributeService;

public class AttributeInstanceImportScript extends GenericMassImportScript {
    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "ATTRIBUTE_INSTANCE";
    private static final String ENTITY_NAME = "AttributeInstance";

    public enum AttributeInstanceActionEnum {
        CREATE, UPDATE, DELETE
    }

    private final AttributeService attributeService = (AttributeService) getServiceInterface("AttributeService");
    private final SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
    private final ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface("ServiceInstanceService");

    private final SubscriptionApi subscriptionApi = (SubscriptionApi) getServiceInterface(SubscriptionApi.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
            if (recordMap != null && !recordMap.isEmpty()) {
                // VALIDATE ENTITY
                String OC_ENTITY = (String) recordMap.get("OC_ENTITY");
                if (!ENTITY.equals(OC_ENTITY)) {
                    throw new ValidationException("value of OC_ENTITY is not correct: " + OC_ENTITY);
                }
                // VALIDATE ACTION
                String OC_ACTION = (String) recordMap.get("OC_ACTION");
                if (Stream.of(AttributeInstanceActionEnum.values()).noneMatch(e -> e.toString().equals(OC_ACTION))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
                }

                AttributeInstanceActionEnum action = AttributeInstanceActionEnum.valueOf(OC_ACTION);

                AttributeInstanceDto attributeInstanceDto = validateAndGetAttributeInstance(action, recordMap);

                AttributeInstance attributeInstance = new AttributeInstance();

                if (AttributeInstanceActionEnum.CREATE.equals(action)) {
                    setAttributeInstanceValues(recordMap, attributeInstance);
                    this.setCFValues(recordMap, attributeInstance, ENTITY_NAME);
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private AttributeInstanceDto validateAndGetAttributeInstance(AttributeInstanceActionEnum action, Map<String, Object> recordMap) {
        AttributeInstanceDto attributeInstanceDto = null;

        String OC_subscription_code = (String) recordMap.get("OC_SUBSCRIPTION_CODE");
        if (OC_subscription_code.isEmpty()) {
            throw new ValidationException("subscription_code is required");
        }

        String OC_si_code = (String) recordMap.get("OC_SERVICE_INSTANCE_CODE");
        if (OC_si_code.isEmpty()) {
            throw new ValidationException("serviceinstance_code is required");
        }

        attributeInstanceDto = new AttributeInstanceDto();
        Subscription subscription = subscriptionService.findByCode(OC_subscription_code);
        if (subscription == null) {
            throw new ValidationException("no subscription found for code: '" + OC_subscription_code + "'");
        }

        ServiceInstance serviceInstance = serviceInstanceService.findByCode(OC_si_code);
        if (serviceInstance == null) {
            throw new ValidationException("no service instance found for code: '" + OC_si_code + "'");
        }

        return attributeInstanceDto;
    }

    private void setAttributeInstanceValues(Map<String, Object> recordMap, AttributeInstance attributeInstance) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        dateFormat.setLenient(false);

        Attribute attribute = attributeService.findByCode((String) recordMap.get("OC_SI_ATTRIB_CODE"));
        AttributeTypeEnum type = attribute.getAttributeType();

        attributeInstance.setAttribute(attribute);

        Subscription subscription = subscriptionService.findByCode((String) recordMap.get("OC_SUBSCRIPTION_CODE"));
        ServiceInstance serviceInstance = serviceInstanceService.findByCodeAndCodeSubscriptionId((String) recordMap.get("OC_SERVICE_INSTANCE_CODE"), subscription);

        List<AttributeInstanceDto> attributeInstances = new ArrayList<>();
        for (AttributeInstance ai : serviceInstance.getAttributeInstances()) {
            attributeInstances.add(new AttributeInstanceDto(ai, null));
        }

        switch (type) {
        case LIST_TEXT:
        case TEXT:
        case EMAIL:
        case INFO:
        case PHONE:
            attributeInstances.stream().filter(ai -> Objects.equals(ai.getAttributeCode(), attribute.getCode())).findFirst().get().setStringValue((String) recordMap.get("OC_SI_ATTRIB_VALUE"));
            attributeInstance.setStringValue((String) recordMap.get("OC_SI_ATTRIB_VALUE"));
            break;
        case TOTAL:
        case COUNT:
        case NUMERIC:
        case INTEGER:
        case LIST_NUMERIC:
            attributeInstances.stream().filter(ai -> Objects.equals(ai.getAttributeCode(), attribute.getCode())).findFirst().get().setDoubleValue(Double.valueOf((String) recordMap.get("OC_SI_ATTRIB_VALUE")));
            attributeInstance.setDoubleValue(Double.valueOf((String) recordMap.get("OC_SI_ATTRIB_VALUE")));
            break;
        case DATE:
            try {
                attributeInstances.stream().filter(ai -> Objects.equals(ai.getAttributeCode(), attribute.getCode())).findFirst().get()
                    .setDateValue(StringUtils.isEmpty((String) recordMap.get("OC_SI_ATTRIB_VALUE")) ? null : dateFormat.parse((String) recordMap.get("OC_SI_ATTRIB_VALUE")));
                attributeInstance.setDateValue(StringUtils.isEmpty((String) recordMap.get("OC_SI_ATTRIB_VALUE")) ? null : dateFormat.parse((String) recordMap.get("OC_SI_ATTRIB_VALUE")));
            } catch (ParseException e) {
                throw new ValidationException("Incorrect date format for " + attribute.getCode() + ". Please use dd/MM/yyyy");
            }

            break;
        case BOOLEAN:
            attributeInstances.stream().filter(ai -> Objects.equals(ai.getAttributeCode(), attribute.getCode())).findFirst().get()
                .setStringValue(Objects.equals(recordMap.get("OC_SI_ATTRIB_VALUE"), "X") ? "true" : "false");
            attributeInstance.setStringValue(Objects.equals(recordMap.get("OC_SI_ATTRIB_VALUE"), "X") ? "true" : "false");
            break;
        }

        // ATTRIBUTE INSTANTIIATION
        UpdateServicesRequestDto requestDto = new UpdateServicesRequestDto();

        requestDto.setSubscriptionCode((String) recordMap.get("OC_SUBSCRIPTION_CODE"));
        ServiceToUpdateDto serviceDto = new ServiceToUpdateDto();
        serviceDto.setId(serviceInstance.getId());
        serviceDto.setCode(serviceInstance.getCode());

        requestDto.addService(serviceDto);
        requestDto.setAttributeInstances(attributeInstances);

        subscriptionApi.updateServiceInstance(requestDto);
    }
}