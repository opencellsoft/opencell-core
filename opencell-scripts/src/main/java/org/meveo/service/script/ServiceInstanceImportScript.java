package org.meveo.service.script;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.billing.AttributeInstanceDto;
import org.meveo.api.dto.billing.ServiceToUpdateDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
import org.meveo.api.dto.cpq.ProductToInstantiateDto;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;

public class ServiceInstanceImportScript extends GenericMassImportScript {

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "SERVICE_INSTANCE";
    private static final String ENTITY_NAME = "ServiceInstance";

    public enum ServiceInstanceActionEnum {
        CREATE, UPDATE, DELETE
    }

    private final ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface("ServiceInstanceService");
    private final SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
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
                if (Stream.of(ServiceInstanceActionEnum.values()).noneMatch(e -> e.toString().equals(OC_ACTION))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
                }

                ServiceInstanceActionEnum action = ServiceInstanceActionEnum.valueOf(OC_ACTION);

                ProductToInstantiateDto productToInstantiateDto = validateAndGetServiceInstance(action, recordMap);

                if (ServiceInstanceActionEnum.CREATE.equals(action)) {
                    setServiceInstanceValues(recordMap, productToInstantiateDto);

                    Subscription subscription = subscriptionService.findByCode((String) recordMap.get("OC_SUBSCRIPTION_CODE"));
                    ServiceInstance si = serviceInstanceService.findByCodeAndCodeSubscriptionId((String) recordMap.get("OC_PRODUCT_CODE"), subscription);
                    this.setCFValues(recordMap, si, ENTITY_NAME);
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private ProductToInstantiateDto validateAndGetServiceInstance(ServiceInstanceActionEnum action, Map<String, Object> recordMap) {
        ProductToInstantiateDto productToInstantiateDto = null;

        String OC_subscription_code = (String) recordMap.get("OC_SUBSCRIPTION_CODE");
        if (OC_subscription_code.isEmpty()) {
            throw new ValidationException("subscription_code is required");
        }

        String OC_si_code = (String) recordMap.get("OC_SI_CODE");
        if (OC_si_code.isEmpty()) {
            throw new ValidationException("serviceinstance_code is required");
        }

        productToInstantiateDto = new ProductToInstantiateDto();
        Subscription subscription = subscriptionService.findByCode(OC_subscription_code);
        if (subscription == null) {
            throw new ValidationException("no subscription found for code: '" + OC_subscription_code + "'");
        }

        return productToInstantiateDto;
    }

    private void setServiceInstanceValues(Map<String, Object> recordMap, ProductToInstantiateDto productToInstantiateDto) throws ParseException {
        String OC_subscription_code = (String) recordMap.get("OC_SUBSCRIPTION_CODE");
        Subscription subscription = subscriptionService.findByCode(OC_subscription_code);

        // Instantiate products
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        dateFormat.setLenient(false);

        String OC_product_deliv_dt = (String) recordMap.get("OC_PRODUCT_DELIV_DT");
        productToInstantiateDto.setProductCode((String) recordMap.get("OC_PRODUCT_CODE"));
        productToInstantiateDto.setQuantity(new BigDecimal((String) recordMap.get("OC_PRODUCT_QTY")));
        try {
            Date deliveryDate = StringUtils.isEmpty(OC_product_deliv_dt) ? null : dateFormat.parse(OC_product_deliv_dt);
            productToInstantiateDto.setDeliveryDate(deliveryDate);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect date format for delivery date. Please use 'dd/MM/yyyy'");
        }

        List<ProductToInstantiateDto> products = new ArrayList<>();
        products.add(productToInstantiateDto);

        subscriptionApi.instanciateProduct(OC_subscription_code, products);
        serviceInstanceService.getEntityManager().flush();

        // Save SI description
        UpdateServicesRequestDto requestDto = new UpdateServicesRequestDto();
        ServiceInstance serviceInstance = serviceInstanceService.findByCodeAndCodeSubscriptionId((String) recordMap.get("OC_PRODUCT_CODE"), subscription);

        List<AttributeInstance> attributeInstances = serviceInstance.getAttributeInstances();
        List<AttributeInstanceDto> aiDto = attributeInstances.stream().map(attribute -> new AttributeInstanceDto(attribute, null)).collect(Collectors.toList());

        requestDto.setSubscriptionCode((String) recordMap.get("OC_SUBSCRIPTION_CODE"));
        ServiceToUpdateDto serviceDto = new ServiceToUpdateDto();
        serviceDto.setId(serviceInstance.getId());
        serviceDto.setCode(serviceInstance.getCode());
        serviceDto.setDescription((String) recordMap.get("OC_SI_DESC"));

        requestDto.addService(serviceDto);
        requestDto.setAttributeInstances(aiDto);

        subscriptionApi.updateServiceInstance(requestDto);
    }
}