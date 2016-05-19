package org.meveo.api.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.ServiceToActivateDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.util.EntityCustomizationUtils;
import org.slf4j.Logger;
import org.tmf.dsmapi.catalog.resource.order.OrderItem;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;
import org.tmf.dsmapi.catalog.resource.order.ProductRelationship;

@Stateless
public class OrderApi extends BaseApi {

    @Inject
    private Logger log;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private SubscriptionApi subscriptionApi;

    private static String CHARACTERISTIC_SERVICE_QUANTITY = "quantity";
    private static String CHARACTERISTIC_SUBSCRIPTION_DATE = "subscriptionDate";
    private static String CHARACTERISTIC_TERMINATION_DATE = "terminationDate";
    private static String CHARACTERISTIC_TERMINATION_REASON = "terminationReason";

    public ProductOrder createProductOrder(ProductOrder productOrder, User currentUser) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException,
            BusinessException, EntityDoesNotExistsException, Exception {

        if (productOrder.getOrderItem() == null || productOrder.getOrderItem().isEmpty()) {
            missingParameters.add("orderItem");
        }
        if (productOrder.getOrderDate() == null) {
            missingParameters.add("orderDate");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        for (OrderItem orderItem : productOrder.getOrderItem()) {

            // Validate billing account
            if (orderItem.getBillingAccount() == null || orderItem.getBillingAccount().isEmpty()) {
                throw new MissingParameterException("billingAccount for order item " + orderItem.getId());
            }
            String billingAccountId = orderItem.getBillingAccount().get(0).getId();
            if (StringUtils.isEmpty(billingAccountId)) {
                throw new MissingParameterException("billingAccount for order item " + orderItem.getId());
            }

            UserAccount userAccount = userAccountService.findByCode(billingAccountId, provider);
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, billingAccountId);
            }

            OfferTemplate offerTemplate = offerTemplateService.findByCode(orderItem.getProductOffering().getId(), provider);
            if (offerTemplate == null) {
                throw new EntityDoesNotExistsException(OfferTemplate.class, orderItem.getProductOffering().getId());
            }

            log.debug("find offerTemplate by {}", orderItem.getProductOffering().getId());

            List<Product> products = new ArrayList<>();
            if (orderItem.getProduct().getProductRelationship() == null || orderItem.getProduct().getProductRelationship().isEmpty()) {
                products.add(orderItem.getProduct());

            } else {
                for (ProductRelationship productRelationship : orderItem.getProduct().getProductRelationship()) {
                    products.add(productRelationship.getProduct());
                }
            }

            Subscription subscription = subscriptionService.findByCode(orderItem.getId(), provider);
            log.debug("find subscription {}", subscription);

            // Create subscription
            if (subscription == null) {
                subscription = new Subscription();
                subscription.setCode(orderItem.getId());
                subscription.setDescription(orderItem.getAppointment());
                subscription.setUserAccount(userAccount);
                subscription.setOffer(offerTemplate);
                subscription.setSubscriptionDate((Date) getProductCharacteristic(orderItem.getProduct(), CHARACTERISTIC_SUBSCRIPTION_DATE, Date.class,
                    DateUtils.setTimeToZero(productOrder.getOrderDate())));

                subscriptionService.create(subscription, currentUser);

                // Validate and populate customFields
                CustomFieldsDto customFields = extractCustomFields(orderItem.getProduct(), Subscription.class, provider);

                try {
                    populateCustomFields(customFields, subscription, true, currentUser, true);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error("Failed to associate custom field instance to an entity", e);
                    throw new MeveoApiException("Failed to associate custom field instance to an entity");
                }

                // Verify that subscription properties match
            } else {

                if (!subscription.getUserAccount().getCode().equalsIgnoreCase(orderItem.getBillingAccount().get(0).getId())) {
                    throw new MeveoApiException("Sub's userAccount doesn't match with orderitem's billingAccount");
                }
                if (!subscription.getOffer().getCode().equalsIgnoreCase(orderItem.getProductOffering().getId())) {
                    throw new MeveoApiException("Sub's offer doesn't match with orderitem's productOffer");
                }
            }

            // instantiate, activate and terminate services
            processServices(subscription, products, currentUser);
        }

        return productOrder;
    }

    @SuppressWarnings("rawtypes")
    private CustomFieldsDto extractCustomFields(Product product, Class appliesToClass, Provider provider) {

        if (product.getProductCharacteristic() == null || product.getProductCharacteristic().isEmpty()) {
            return null;
        }

        CustomFieldsDto customFieldsDto = new CustomFieldsDto();

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(EntityCustomizationUtils.getAppliesTo(appliesToClass, null), provider);

        for (ProductCharacteristic characteristic : product.getProductCharacteristic()) {
            if (characteristic.getName() != null && cfts.containsKey(characteristic.getName())) {

                CustomFieldTemplate cft = cfts.get(characteristic.getName());
                CustomFieldDto cftDto = entityToDtoConverter.customFieldToDTO(characteristic.getName(), cft.parseValue(characteristic.getValue()),
                    cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY, provider);
                customFieldsDto.getCustomField().add(cftDto);
            }
        }

        return customFieldsDto;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getProductCharacteristic(Product product, String code, Class valueClass, Object defaultValue) {

        if (product.getProductCharacteristic() == null || product.getProductCharacteristic().isEmpty()) {
            return defaultValue;
        }

        Object value = null;
        for (ProductCharacteristic productCharacteristic : product.getProductCharacteristic()) {
            if (productCharacteristic.getName().equals(code)) {
                value = productCharacteristic.getValue();
                break;
            }
        }

        if (value != null) {

            // Need to perform conversion
            if (!valueClass.isAssignableFrom(value.getClass())) {

                if (valueClass == BigDecimal.class) {
                    value = new BigDecimal((String) value);

                }
                if (valueClass == Date.class) {
                    value = DateUtils.parseDateWithPattern((String) value, "yyyy-MM-dd");
                }
            }

        } else {
            value = defaultValue;
        }

        return value;
    }

    private void processServices(Subscription subscription, List<Product> services, User currentUser) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException,
            BusinessException, MeveoApiException {

        ActivateServicesRequestDto activateServicesRequestDto = new ActivateServicesRequestDto();
        activateServicesRequestDto.setSubscription(subscription.getCode());

        List<TerminateSubscriptionServicesRequestDto> servicesToTerminate = new ArrayList<>();

        for (Product serviceProduct : services) {

            // ID is a service code. If missing - list of services is provided as product characteristic with name "service" - OLD implementation.
            if (StringUtils.isBlank(serviceProduct.getId()) && serviceProduct.getProductCharacteristic() != null) {

                // Services will be instantiated and activated
                if (getProductCharacteristic(serviceProduct, CHARACTERISTIC_TERMINATION_DATE, Date.class, null) == null) {

                    for (ProductCharacteristic c : serviceProduct.getProductCharacteristic()) {
                        if ("service".equalsIgnoreCase(c.getName()) && !StringUtils.isBlank(c.getValue())) {

                            ServiceToActivateDto service = new ServiceToActivateDto();
                            service.setCode(c.getValue());
                            service.setQuantity(new BigDecimal(1));
                            service.setSubscriptionDate(DateUtils.setTimeToZero(new Date()));

                            activateServicesRequestDto.getServicesToActivateDto().addService(service);
                        }
                    }

                    // Services will be terminated
                } else {

                    for (ProductCharacteristic c : serviceProduct.getProductCharacteristic()) {
                        if ("service".equalsIgnoreCase(c.getName()) && !StringUtils.isBlank(c.getValue())) {

                            TerminateSubscriptionServicesRequestDto terminationDto = new TerminateSubscriptionServicesRequestDto();
                            terminationDto.setSubscriptionCode(subscription.getCode());
                            terminationDto.setTerminationDate((Date) getProductCharacteristic(serviceProduct, CHARACTERISTIC_TERMINATION_DATE, Date.class, null));
                            terminationDto.setTerminationReason((String) getProductCharacteristic(serviceProduct, CHARACTERISTIC_TERMINATION_REASON, String.class, null));
                            terminationDto.getServices().add(c.getValue());
                            servicesToTerminate.add(terminationDto);
                        }
                    }

                }

                // If ID is represent - product represents a service and product characteristics define custom fields and other service attributes
            } else if (!StringUtils.isBlank(serviceProduct.getId())) {

                // Service will activated
                if (getProductCharacteristic(serviceProduct, CHARACTERISTIC_TERMINATION_DATE, Date.class, null) == null) {

                    ServiceToActivateDto service = new ServiceToActivateDto();
                    service.setCode(serviceProduct.getId());
                    service.setQuantity((BigDecimal) getProductCharacteristic(serviceProduct, CHARACTERISTIC_SERVICE_QUANTITY, BigDecimal.class, new BigDecimal(1)));
                    service.setSubscriptionDate((Date) getProductCharacteristic(serviceProduct, CHARACTERISTIC_SUBSCRIPTION_DATE, Date.class, DateUtils.setTimeToZero(new Date())));
                    service.setCustomFields(extractCustomFields(serviceProduct, ServiceInstance.class, currentUser.getProvider()));

                    activateServicesRequestDto.getServicesToActivateDto().addService(service);

                    // Service will be terminated
                } else {

                    TerminateSubscriptionServicesRequestDto terminationDto = new TerminateSubscriptionServicesRequestDto();
                    terminationDto.setSubscriptionCode(subscription.getCode());
                    terminationDto.setTerminationDate((Date) getProductCharacteristic(serviceProduct, CHARACTERISTIC_TERMINATION_DATE, Date.class, null));
                    terminationDto.setTerminationReason((String) getProductCharacteristic(serviceProduct, CHARACTERISTIC_TERMINATION_REASON, String.class, null));
                    terminationDto.getServices().add(serviceProduct.getId());
                    servicesToTerminate.add(terminationDto);
                }
            }
        }

        // Activate services
        if (!activateServicesRequestDto.getServicesToActivateDto().getService().isEmpty()) {
            subscriptionApi.activateServices(activateServicesRequestDto, currentUser, true);
        }
        if (!servicesToTerminate.isEmpty()) {
            for (TerminateSubscriptionServicesRequestDto terminationDto : servicesToTerminate) {
                subscriptionApi.terminateServices(terminationDto, currentUser);
            }
        }
    }

    public ProductOrder getProductOrder(String orderId, User currentUser) throws EntityDoesNotExistsException {

        throw new EntityDoesNotExistsException(ProductOrder.class, orderId);
    }

    public List<ProductOrder> findProductOrders(Map<String, List<String>> filterCriteria, User currentUser) {
        // Need to implement
        return null;
    }

    public ProductOrder updatePartiallyProductOrder(String orderId, ProductOrder productOrder, User currentUser) throws EntityDoesNotExistsException {
        // Need to implement
        throw new EntityDoesNotExistsException(ProductOrder.class, orderId);
    }

    public void deleteProductOrder(String orderId, User currentUser) throws EntityDoesNotExistsException, ActionForbiddenException {
        // Need to implement

    }
}