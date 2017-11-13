package org.meveo.api.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.meveo.api.dto.billing.ApplicableDueDateDelayDto;
import org.meveo.api.dto.billing.DueDateDelayLevelEnum;
import org.meveo.api.dto.billing.DueDateDelayReferenceDateEnum;
import org.meveo.api.dto.billing.ServiceToActivateDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValueException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.order.OrderProductCharacteristicEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.order.OrderItemProductOffering;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.quote.Quote;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.order.OrderItemService;
import org.meveo.service.order.OrderService;
import org.meveo.service.wf.WorkflowService;
import org.meveo.util.EntityCustomizationUtils;
import org.slf4j.Logger;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;
import org.tmf.dsmapi.catalog.resource.order.ProductOrderItem;
import org.tmf.dsmapi.catalog.resource.order.ProductRelationship;
import org.tmf.dsmapi.catalog.resource.product.BundledProductReference;

@Stateless
public class OrderApi extends BaseApi {

    @Inject
    private Logger log;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ProductOfferingService productOfferingService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ProductInstanceService productInstanceService;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private SubscriptionApi subscriptionApi;

    @Inject
    private OrderService orderService;

    @Inject
    private OrderItemService orderItemService;

    @Inject
    private WorkflowService workflowService;

    @Inject
    private TerminationReasonService terminationReasonService;

    /**
     * Register an order from TMForumApi
     * 
     * @param productOrder Product order
     * 
     * @return Product order DTO updated
     * @throws BusinessException
     * @throws MeveoApiException
     */
    public ProductOrder createProductOrder(ProductOrder productOrder, Long quoteId) throws BusinessException, MeveoApiException {

        if (productOrder.getOrderItem() == null || productOrder.getOrderItem().isEmpty()) {
            missingParameters.add("orderItem");
        }
        if (productOrder.getOrderDate() == null) {
            missingParameters.add("orderDate");
        }

        handleMissingParameters();

        Order order = new Order();
        if (quoteId != null) {
            order.setQuote(orderService.getEntityManager().getReference(Quote.class, quoteId));
        }
        order.setCode(UUID.randomUUID().toString());
        order.setCategory(productOrder.getCategory());
        // order.setDeliveryInstructions("");
        order.setDescription(productOrder.getDescription());
        order.setExternalId(productOrder.getExternalId());
        order.setReceivedFromApp("API");
        order.setDueDateDelayEL(productOrder.getDueDateDelayEL());

        if (productOrder.getPaymentMethods() != null && !productOrder.getPaymentMethods().isEmpty()) {
            PaymentMethod paymentMethod = productOrder.getPaymentMethods().get(0).fromDto(null);
            paymentMethod.updateAudit(currentUser);
            order.setPaymentMethod(paymentMethod);
        }

        order.setOrderDate(productOrder.getOrderDate() != null ? productOrder.getOrderDate() : new Date());
        if (!StringUtils.isBlank(productOrder.getPriority())) {
            order.setPriority(Integer.parseInt(productOrder.getPriority()));
        }
        order.setRequestedCompletionDate(productOrder.getRequestedCompletionDate());
        order.setRequestedStartDate(productOrder.getRequestedStartDate());
        if (productOrder.getState() != null) {
            order.setStatus(OrderStatusEnum.valueByApiState(productOrder.getState()));
        } else {
            order.setStatus(OrderStatusEnum.ACKNOWLEDGED);
        }

        for (ProductOrderItem productOrderItem : productOrder.getOrderItem()) {

            if (org.meveo.commons.utils.StringUtils.isBlank(productOrderItem.getAction())) {
                missingParameters.add("orderItem.action");
                handleMissingParameters();
            }

            // Validate billing account
            List<org.tmf.dsmapi.catalog.resource.order.BillingAccount> billingAccount = productOrderItem.getBillingAccount();
            if (billingAccount == null || billingAccount.isEmpty()) {
                throw new MissingParameterException("billingAccount for order item " + productOrderItem.getId());
            }

            String billingAccountId = billingAccount.get(0).getId();
            if (StringUtils.isEmpty(billingAccountId)) {
                throw new MissingParameterException("billingAccount for order item " + productOrderItem.getId());
            }

            UserAccount userAccount = (UserAccount) userAccountService.getEntityManager().createNamedQuery("UserAccount.findByCode").setParameter("code", billingAccountId)
                .getSingleResult();

            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, billingAccountId);
            }

            org.meveo.model.order.OrderItem orderItem = new org.meveo.model.order.OrderItem();
            List<OrderItemProductOffering> productOfferings = new ArrayList<>();
            ProductOffering mainProductOffering = null;

            // For modify and delete actions, product offering might not be specified
            if (productOrderItem.getProductOffering() != null) {

                Date subscriptionDate = ((Date) getProductCharacteristic(productOrderItem.getProduct(), OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(),
                    Date.class, DateUtils.setTimeToZero(order.getOrderDate())));

                mainProductOffering = productOfferingService.findByCode(productOrderItem.getProductOffering().getId(), subscriptionDate);
                if (mainProductOffering == null) {
                    throw new EntityDoesNotExistsException(ProductOffering.class,
                        productOrderItem.getProductOffering().getId() + " / " + DateUtils.formatDateWithPattern(subscriptionDate, ParamBean.getInstance().getDateTimeFormat()));
                }
                productOfferings.add(new OrderItemProductOffering(orderItem, mainProductOffering, 0));

                if (productOrderItem.getProductOffering().getBundledProductOffering() != null) {
                    for (BundledProductReference bundledProductOffering : productOrderItem.getProductOffering().getBundledProductOffering()) {
                        ProductOffering productOfferingInDB = productOfferingService.findByCode(bundledProductOffering.getReferencedId(), subscriptionDate);
                        if (productOfferingInDB == null) {
                            throw new EntityDoesNotExistsException(ProductOffering.class,
                                bundledProductOffering.getReferencedId() + " / " + DateUtils.formatDateWithPattern(subscriptionDate, ParamBean.getInstance().getDateTimeFormat()));
                        }
                        productOfferings.add(new OrderItemProductOffering(orderItem, productOfferingInDB, productOfferings.size()));
                    }
                }
            } else {
                // We need productOffering so we know if product is subscription or
                // productInstance - NEED TO FIX IT
                throw new MissingParameterException("productOffering");
            }

            // Validate or supplement if not provided subscription renewal fields
            if (mainProductOffering instanceof OfferTemplate) {
                validateOrSupplementSubscriptionRenewalFields(productOrderItem.getProduct(), (OfferTemplate) mainProductOffering);
            }

            orderItem.setItemId(productOrderItem.getId());
            try {
                orderItem.setAction(OrderItemActionEnum.valueOf(productOrderItem.getAction().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException(OrderItemActionEnum.class.getSimpleName(), productOrderItem.getAction());
            }
            orderItem.setOrder(order);
            orderItem.setUserAccount(userAccount);
            orderItem.setSource(ProductOrderItem.serializeOrderItem(productOrderItem));
            orderItem.setOrderItemProductOfferings(productOfferings);

            if (productOrderItem.getState() != null) {
                orderItem.setStatus(OrderStatusEnum.valueByApiState(productOrderItem.getState()));
            } else {
                orderItem.setStatus(OrderStatusEnum.ACKNOWLEDGED);
            }

            if (productOrderItem.getProduct() != null && productOrderItem.getProduct().getPlace() != null && productOrderItem.getProduct().getPlace().getAddress() != null) {
                Address shippingAddress = new Address();
                shippingAddress.setAddress1(productOrderItem.getProduct().getPlace().getAddress().getAddress1());
                shippingAddress.setAddress2(productOrderItem.getProduct().getPlace().getAddress().getAddress2());
                shippingAddress.setAddress3(productOrderItem.getProduct().getPlace().getAddress().getAddress3());
                shippingAddress.setCity(productOrderItem.getProduct().getPlace().getAddress().getCity());
                shippingAddress.setCountry(productOrderItem.getProduct().getPlace().getAddress().getCountry());
                shippingAddress.setZipCode(productOrderItem.getProduct().getPlace().getAddress().getZipCode());
                shippingAddress.setState(productOrderItem.getProduct().getPlace().getAddress().getState());
                orderItem.setShippingAddress(shippingAddress);
            }

            // Extract products that are not services. For each product offering there must
            // be a product. Products that exceed the number of product offerings are
            // treated as
            // services.
            //
            // Sample of ordering a single product:
            // productOffering
            // product with product characteristics
            //
            // Sample of ordering two products bundled under an offer template:
            // productOffering bundle (offer template)
            // ...productOffering (product1)
            // ...productOffering (product2)
            // product with subscription characteristics
            // ...product with product1 characteristics
            // ...product with product2 characteristics
            // ...product for service with service1 characteristics - not considered as
            // product/does not required ID for modify/delete opperation
            // ...product for service with service2 characteristics - not considered as
            // product/does not required ID for modify/delete opperation

            List<Product> products = new ArrayList<>();
            products.add(productOrderItem.getProduct());
            if (productOfferings.size() > 1 && productOrderItem.getProduct().getProductRelationship() != null
                    && !productOrderItem.getProduct().getProductRelationship().isEmpty()) {
                for (ProductRelationship productRelationship : productOrderItem.getProduct().getProductRelationship()) {
                    products.add(productRelationship.getProduct());
                    if (productOfferings.size() >= products.size()) {
                        break;
                    }
                }
            }

            for (Product product : products) {
                // Validate that product ID was provided when modifying or deleting a product
                // ordered
                if (product.getId() == null && (orderItem.getAction() == OrderItemActionEnum.MODIFY || orderItem.getAction() == OrderItemActionEnum.DELETE)) {
                    throw new MissingParameterException("product.id");
                }
            }

            order.addOrderItem(orderItem);
        }

        // populate customFields
        try {
            populateCustomFields(productOrder.getCustomFields(), order, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        orderService.create(order);

        // Commit before initiating workflow/order processing
        orderService.commit();

        order = initiateWorkflow(order);

        return orderToDto(order);
    }

    /**
     * Initiate workflow on order. If workflow is enabled on Order class, then execute workflow. If workflow is not enabled - then process the order right away.
     * 
     * @param order
     * 
     * @return
     * @throws BusinessException
     * @throws MeveoApiException
     */
    public Order initiateWorkflow(Order order) throws BusinessException {

        if (order.getStatus() == OrderStatusEnum.IN_CREATION) {
            order.setStatus(OrderStatusEnum.ACKNOWLEDGED);
        }

        if (workflowService.isWorkflowSetup(Order.class)) {
            order = (Order) workflowService.executeMatchingWorkflows(order);

        } else {
            try {
                order = processOrder(order);
            } catch (MeveoApiException e) {
                throw new BusinessException(e);
            }
        }

        return order;

    }

    /**
     * Process the order
     * 
     * @param order
     * 
     * @throws BusinessException
     * @throws MeveoApiException
     */
    public Order processOrder(Order order) throws BusinessException, MeveoApiException {

        // Nothing to process in final state
        if (order.getStatus() == OrderStatusEnum.COMPLETED) {
            return order;
        }

        log.info("Processing order {}", order.getCode());

        order = orderService.refreshOrRetrieve(order);

        order.setStartDate(new Date());

        for (org.meveo.model.order.OrderItem orderItem : order.getOrderItems()) {
            processOrderItem(order, orderItem);
        }

        order.setCompletionDate(new Date());
        order.setStatus(OrderStatusEnum.COMPLETED);
        for (org.meveo.model.order.OrderItem orderItem : order.getOrderItems()) {
            orderItem.setStatus(OrderStatusEnum.COMPLETED);
        }

        order = orderService.update(order);

        log.trace("Finished processing order {}", order.getCode());

        return order;
    }

    private void processOrderItem(Order order, org.meveo.model.order.OrderItem orderItem) throws BusinessException, MeveoApiException {

        log.info("Processing order item {} {}", order.getCode(), orderItem.getItemId());

        String orderNumber = order.getOrderNumber();
        orderItem.setStatus(OrderStatusEnum.IN_PROGRESS);

        ProductOrderItem productOrderItem = ProductOrderItem.deserializeOrderItem(orderItem.getSource());

        // Ordering a new product
        if (orderItem.getAction() == OrderItemActionEnum.ADD) {
            ProductOffering primaryOffering = orderItem.getMainOffering();

            // Just a simple case of ordering a single product
            if (primaryOffering instanceof ProductTemplate) {

                ProductInstance productInstance = instantiateProduct((ProductTemplate) primaryOffering, productOrderItem.getProduct(), orderItem, productOrderItem, null,
                    order.getOrderNumber());
                if (productInstance != null) {
                    orderItem.addProductInstance(productInstance);
                    productOrderItem.getProduct().setId(productInstance.getCode());
                }

                // A complex case of ordering from offer template with services and optional
                // products
            } else {

                // Distinguish bundled products which could be either services or products

                List<Product> products = new ArrayList<>();
                List<Product> services = new ArrayList<>();
                int index = 1;
                if (productOrderItem.getProduct().getProductRelationship() != null && !productOrderItem.getProduct().getProductRelationship().isEmpty()) {
                    for (ProductRelationship productRelationship : productOrderItem.getProduct().getProductRelationship()) {
                        if (index < orderItem.getOrderItemProductOfferings().size()) {
                            products.add(productRelationship.getProduct());
                        } else {
                            services.add(productRelationship.getProduct());
                        }
                        index++;
                    }
                }

                // Instantiate a service
                Subscription subscription = instantiateSubscription((OfferTemplate) primaryOffering, services, orderItem, productOrderItem, orderNumber);
                orderItem.setSubscription(subscription);
                // Instantiate products - find a matching product offering. The order of
                // products must match the order of productOfferings
                index = 1;
                for (Product product : products) {
                    ProductTemplate productOffering = (ProductTemplate) orderItem.getOrderItemProductOfferings().get(index).getProductOffering();
                    productOffering = productTemplateService.refreshOrRetrieve(productOffering);
                    ProductInstance productInstance = instantiateProduct(productOffering, product, orderItem, productOrderItem, subscription, orderNumber);
                    if (productInstance != null) {
                        orderItem.addProductInstance(productInstance);
                        product.setId(productInstance.getCode());
                    }
                    index++;
                }
                productOrderItem.getProduct().setId(subscription.getCode());

            }

            // Serialize back the productOrderItem with updated product ids
            orderItem.setSource(ProductOrderItem.serializeOrderItem(productOrderItem));

        } else if (orderItem.getAction() == OrderItemActionEnum.MODIFY) {

            if (productOrderItem.getProduct().getId() == null) {
                throw new MissingParameterException("product.id");
            }

            // For modify and delete actions, product offering might not be specified
            ProductOffering primaryOffering = orderItem.getMainOffering();

            // We need productOffering so we know if product is subscription or
            // productInstance
            if (primaryOffering == null) {
                throw new MissingParameterException("productOffering");

                // Modifying an existing product
            } else if (primaryOffering instanceof ProductTemplate) {
                // TODO modify product

                ProductInstance productInstance = productInstanceService.findByCode(productOrderItem.getProduct().getId());
                if (productInstance == null) {
                    throw new EntityDoesNotExistsException(ProductInstance.class, productOrderItem.getProduct().getId());
                }
                log.debug("will modify product instance {}", productInstance);
                orderItem.addProductInstance(productInstance);

                // Modifying an existing subscription
            } else if (primaryOffering instanceof OfferTemplate) {

                Subscription subscription = subscriptionService.findByCode(productOrderItem.getProduct().getId());
                if (subscription == null) {
                    throw new EntityDoesNotExistsException(Subscription.class, productOrderItem.getProduct().getId());
                }
                log.debug("will modify subscription {}", subscription);

                // Verify that subscription properties match
                if (!subscription.getUserAccount().equals(orderItem.getUserAccount())) {
                    throw new MeveoApiException("Sub's userAccount doesn't match with orderitem's billingAccount");
                }

                // Update renewal rule if applicable
                subscription.setSubscriptionRenewal(subscriptionApi.subscriptionRenewalFromDto(subscription.getSubscriptionRenewal(),
                    extractSubscriptionRenewalDto(productOrderItem.getProduct()), subscription.isRenewed()));

                // Validate and populate customFields
                CustomFieldsDto customFields = extractCustomFields(productOrderItem.getProduct(), Subscription.class);

                try {
                    populateCustomFields(customFields, subscription, true, true);
                } catch (MissingParameterException e) {
                    log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("Failed to associate custom field instance to an entity", e);
                    throw e;
                }

                // Services are expressed as child products
                // instantiate, activate and terminate services
                List<Product> services = new ArrayList<>();
                if (productOrderItem.getProduct().getProductRelationship() != null && !productOrderItem.getProduct().getProductRelationship().isEmpty()) {
                    for (ProductRelationship productRelationship : productOrderItem.getProduct().getProductRelationship()) {
                        services.add(productRelationship.getProduct());
                    }
                }

                processServices(subscription, services, orderNumber);
                subscription = subscriptionService.update(subscription);

                orderItem.setSubscription(subscription);

            }
        } else if (orderItem.getAction() == OrderItemActionEnum.DELETE) {

            if (productOrderItem.getProduct().getId() == null) {
                throw new MissingParameterException("product.id");
            }

            // For modify and delete actions, product offering might not be specified
            ProductOffering primaryOffering = orderItem.getMainOffering();

            if (primaryOffering == null) {
                throw new MissingParameterException("productOffering");

                // Modifying an existing product
            } else if (primaryOffering instanceof ProductTemplate) {
                // modify product

                // Modifying an existing subscription
            } else if (primaryOffering instanceof OfferTemplate) {

                Subscription subscription = subscriptionService.findByCode(productOrderItem.getProduct().getId());
                log.debug("will modify subscription {}", subscription);
                if (subscription == null) {
                    throw new EntityDoesNotExistsException(Subscription.class, productOrderItem.getProduct().getId());
                }
            }

            TerminateSubscriptionRequestDto terminateSubscription = new TerminateSubscriptionRequestDto();
            terminateSubscription.setSubscriptionCode(productOrderItem.getProduct().getId());
            terminateSubscription.setTerminationDate((Date) getProductCharacteristic(productOrderItem.getProduct(),
                OrderProductCharacteristicEnum.TERMINATION_DATE.getCharacteristicName(), Date.class, DateUtils.setTimeToZero(orderItem.getOrder().getOrderDate())));
            terminateSubscription.setTerminationReason(
                (String) getProductCharacteristic(productOrderItem.getProduct(), OrderProductCharacteristicEnum.TERMINATION_REASON.getCharacteristicName(), String.class, null));

            subscriptionApi.terminateSubscription(terminateSubscription, orderNumber);

        }

        orderItem.setStatus(OrderStatusEnum.COMPLETED);
        orderItemService.update(orderItem);

        log.info("Finished processing order item {} {}", order.getCode(), orderItem.getItemId());
    }

    private Subscription instantiateSubscription(OfferTemplate offerTemplate, List<Product> services, org.meveo.model.order.OrderItem orderItem, ProductOrderItem productOrderItem,
            String orderNumber) throws BusinessException, MeveoApiException {

        log.debug("Instantiating subscription from offer template {} for order {} line {}", offerTemplate.getCode(), orderItem.getOrder().getCode(), orderItem.getItemId());

        Product product = productOrderItem.getProduct();
        String subscriptionCode = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_CODE.getCharacteristicName(), String.class,
            UUID.randomUUID().toString());

        if (subscriptionService.findByCode(subscriptionCode) != null) {
            throw new BusinessException("Subscription with code " + subscriptionCode + " already exists");
        }

        Subscription subscription = new Subscription();
        subscription.setCode(subscriptionCode);
        subscription.setUserAccount(orderItem.getUserAccount());
        subscription.setOffer(offerTemplate);

        subscription.setSubscriptionDate((Date) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(), Date.class,
            DateUtils.setTimeToZero(orderItem.getOrder().getOrderDate())));
        subscription.setEndAgreementDate((Date) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_END_DATE.getCharacteristicName(), Date.class, null));

        subscription.setSubscriptionRenewal(subscriptionApi.subscriptionRenewalFromDto(null, extractSubscriptionRenewalDto(product), false));

        // Validate and populate customFields
        CustomFieldsDto customFields = extractCustomFields(product, Subscription.class);

        try {
            populateCustomFields(customFields, subscription, true, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new BusinessException("Failed to associate custom field instance to an entity", e);
        }

        subscriptionService.create(subscription);

        // instantiate and activate services
        processServices(subscription, services, orderNumber);

        return subscription;
    }

    private ProductInstance instantiateProduct(ProductTemplate productTemplate, Product product, org.meveo.model.order.OrderItem orderItem, ProductOrderItem productOrderItem,
            Subscription subscription, String orderNumber) throws BusinessException {

        log.debug("Instantiating product from product template {} for order {} line {}", productTemplate.getCode(), orderItem.getOrder().getCode(), orderItem.getItemId());

        BigDecimal quantity = ((BigDecimal) getProductCharacteristic(product, OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY.getCharacteristicName(), BigDecimal.class,
            new BigDecimal(1)));
        Date chargeDate = ((Date) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(), Date.class,
            DateUtils.setTimeToZero(orderItem.getOrder().getOrderDate())));

        String code = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.PRODUCT_INSTANCE_CODE.getCharacteristicName(), String.class,
            UUID.randomUUID().toString());
        String criteria1 = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.CRITERIA_1.getCharacteristicName(), String.class, null);
        String criteria2 = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.CRITERIA_2.getCharacteristicName(), String.class, null);
        String criteria3 = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.CRITERIA_3.getCharacteristicName(), String.class, null);
        ProductInstance productInstance = new ProductInstance(orderItem.getUserAccount(), subscription, productTemplate, quantity, chargeDate, code,
            productTemplate.getDescription(), orderNumber);

        try {
            CustomFieldsDto customFields = extractCustomFields(product, ProductInstance.class);
            populateCustomFields(customFields, productInstance, true, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new BusinessException("Failed to associate custom field instance to an entity", e);
        }

        productInstanceService.applyProductInstance(productInstance, criteria1, criteria2, criteria3, true);

        return productInstance;
    }

    @SuppressWarnings("rawtypes")
    private CustomFieldsDto extractCustomFields(Product product, Class appliesToClass) {

        if (product.getProductCharacteristic() == null || product.getProductCharacteristic().isEmpty()) {
            return null;
        }

        CustomFieldsDto customFieldsDto = new CustomFieldsDto();

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(EntityCustomizationUtils.getAppliesTo(appliesToClass, null));

        for (ProductCharacteristic characteristic : product.getProductCharacteristic()) {
            if (characteristic.getName() != null && cfts.containsKey(characteristic.getName())) {

                CustomFieldTemplate cft = cfts.get(characteristic.getName());
                CustomFieldDto cftDto = entityToDtoConverter.customFieldToDTO(characteristic.getName(), CustomFieldValue.parseValueFromString(cft, characteristic.getValue()),
                    cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY);
                customFieldsDto.getCustomField().add(cftDto);
            }
        }

        return customFieldsDto;
    }

    private void setProductCharacteristic(Product product, String code, Object value) {
        if (product.getProductCharacteristic() == null) {
            product.setProductCharacteristic(new ArrayList<>());
        }

        String valueTxt = null;
        value.toString();
        if (value instanceof Date) {
            valueTxt = DateUtils.formatDateWithPattern((Date) value, DateUtils.DATE_PATTERN);
        } else {
            valueTxt = value.toString();
        }

        for (ProductCharacteristic productCharacteristic : product.getProductCharacteristic()) {
            if (productCharacteristic.getName().equals(code)) {
                productCharacteristic.setValue(valueTxt);
                return;
            }
        }

        if (value != null) {
            product.getProductCharacteristic().add(new ProductCharacteristic(code, valueTxt));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getProductCharacteristic(Product product, String code, Class valueClass, Object defaultValue) {

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

                } else if (valueClass == Integer.class) {
                    value = new Integer((String) value);

                } else if (valueClass == Date.class) {
                    String originalValue = (String) value;
                    value = DateUtils.parseDateWithPattern(originalValue, DateUtils.DATE_TIME_PATTERN);
                    if (value == null) {
                        value = DateUtils.parseDateWithPattern(originalValue, DateUtils.DATE_PATTERN);
                    }

                } else if (valueClass == Boolean.class) {
                    value = new Boolean((String) value);

                } else if (valueClass.isEnum()) {
                    value = Enum.valueOf(valueClass, (String) value);
                }
            }

        } else {
            value = defaultValue;
        }

        return value;
    }

    private void processServices(Subscription subscription, List<Product> services, String orderNumber)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, MeveoApiException {

        ActivateServicesRequestDto activateServicesRequestDto = new ActivateServicesRequestDto();
        activateServicesRequestDto.setSubscription(subscription.getCode());

        List<TerminateSubscriptionServicesRequestDto> servicesToTerminate = new ArrayList<>();

        for (Product serviceProduct : services) {

            String serviceCode = (String) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SERVICE_CODE.getCharacteristicName(), String.class, null);

            if (StringUtils.isBlank(serviceCode)) {
                throw new MissingParameterException("serviceCode");
            }

            // Service will be activated
            if (getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.TERMINATION_DATE.getCharacteristicName(), Date.class, null) == null) {

                ServiceToActivateDto service = new ServiceToActivateDto();
                service.setCode(serviceCode);
                service.setQuantity((BigDecimal) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY.getCharacteristicName(),
                    BigDecimal.class, new BigDecimal(1)));
                service.setSubscriptionDate((Date) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(), Date.class,
                    DateUtils.setTimeToZero(new Date())));
                service.setRateUntilDate((Date) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.RATE_UNTIL_DATE.getCharacteristicName(), Date.class,
                    DateUtils.setTimeToZero(new Date())));

                // FIX - no way to set value via API
                // service.setEndAgreementDate((Date) getProductCharacteristic(serviceProduct,
                // OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(),
                // Date.class,
                // null));

                service.setCustomFields(extractCustomFields(serviceProduct, ServiceInstance.class));

                activateServicesRequestDto.getServicesToActivateDto().addService(service);

                // Service will be terminated
            } else {

                TerminateSubscriptionServicesRequestDto terminationDto = new TerminateSubscriptionServicesRequestDto();
                terminationDto.setSubscriptionCode(subscription.getCode());
                terminationDto
                    .setTerminationDate((Date) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.TERMINATION_DATE.getCharacteristicName(), Date.class, null));
                terminationDto.setTerminationReason(
                    (String) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.TERMINATION_REASON.getCharacteristicName(), String.class, null));
                terminationDto.getServices().add(serviceCode);
                servicesToTerminate.add(terminationDto);
            }
        }

        // Activate services
        if (!activateServicesRequestDto.getServicesToActivateDto().getService().isEmpty()) {
            subscriptionApi.activateServices(activateServicesRequestDto, orderNumber, true);
        }
        if (!servicesToTerminate.isEmpty()) {
            for (TerminateSubscriptionServicesRequestDto terminationDto : servicesToTerminate) {
                subscriptionApi.terminateServices(terminationDto, orderNumber);
            }
        }
    }

    public ProductOrder getProductOrder(String orderId) throws EntityDoesNotExistsException, BusinessException {

        Order order = orderService.findByCode(orderId);

        if (order == null) {
            throw new EntityDoesNotExistsException(ProductOrder.class, orderId);
        }

        return orderToDto(order);
    }

    public List<ProductOrder> findProductOrders(Map<String, List<String>> filterCriteria) throws BusinessException {

        List<Order> orders = orderService.list();

        List<ProductOrder> productOrders = new ArrayList<>();
        for (Order order : orders) {
            productOrders.add(orderToDto(order));
        }

        return productOrders;
    }

    public ProductOrder updatePartiallyProductOrder(String orderId, ProductOrder productOrder) throws BusinessException, MeveoApiException {

        Order order = orderService.findByCode(orderId);
        if (order == null) {
            throw new EntityDoesNotExistsException(ProductOrder.class, orderId);
        }

        // populate customFields
        try {
            populateCustomFields(productOrder.getCustomFields(), order, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        // TODO Need to initiate workflow if there is one

        order = orderService.refreshOrRetrieve(order);

        return orderToDto(order);

    }

    public void deleteProductOrder(String orderId) throws EntityDoesNotExistsException, ActionForbiddenException, BusinessException {

        Order order = orderService.findByCode(orderId);

        if (order.getStatus() == OrderStatusEnum.IN_CREATION || order.getStatus() == OrderStatusEnum.ACKNOWLEDGED) {
            orderService.remove(order);
        }
    }

    /**
     * Convert order stored in DB to order DTO expected by tmForum api.
     * 
     * @param order Order to convert
     * @return Order DTO object
     * @throws BusinessException
     */
    private ProductOrder orderToDto(Order order) throws BusinessException {

        ProductOrder productOrder = new ProductOrder();

        productOrder.setId(order.getCode().toString());
        productOrder.setCategory(order.getCategory());
        productOrder.setCompletionDate(order.getCompletionDate());
        productOrder.setDescription(order.getDescription());
        productOrder.setExpectedCompletionDate(order.getExpectedCompletionDate());
        productOrder.setExternalId(order.getExternalId());
        productOrder.setOrderDate(order.getOrderDate());
        if (order.getPriority() != null) {
            productOrder.setPriority(order.getPriority().toString());
        }
        productOrder.setRequestedCompletionDate(order.getRequestedCompletionDate());
        productOrder.setRequestedStartDate(order.getRequestedStartDate());
        productOrder.setState(order.getStatus().getApiState());
        productOrder.setDueDateDelayEL(order.getDueDateDelayEL());
        if (order.getPaymentMethod() != null) {
            productOrder.setPaymentMethods(new ArrayList<>());
            PaymentMethodDto pmDto = new PaymentMethodDto(order.getPaymentMethod());
            productOrder.getPaymentMethods().add(pmDto);

        }

        List<ProductOrderItem> productOrderItems = new ArrayList<>();
        productOrder.setOrderItem(productOrderItems);

        for (org.meveo.model.order.OrderItem orderItem : order.getOrderItems()) {
            productOrderItems.add(orderItemToDto(orderItem));
        }

        productOrder.setCustomFields(entityToDtoConverter.getCustomFieldsWithInheritedDTO(order, true));

        return productOrder;
    }

    /**
     * Convert order item stored in DB to orderItem dto expected by tmForum api. As actual dto was serialized earlier, all need to do is to deserialize it and update the status.
     * 
     * @param orderItem Order item to convert to dto
     * @return Order item Dto
     * @throws BusinessException
     */
    private ProductOrderItem orderItemToDto(org.meveo.model.order.OrderItem orderItem) throws BusinessException {

        ProductOrderItem productOrderItem = ProductOrderItem.deserializeOrderItem(orderItem.getSource());
        //
        productOrderItem.setState(orderItem.getOrder().getStatus().getApiState());

        return productOrderItem;
    }

    /**
     * Distinguish bundled products which could be either services or products
     * 
     * @param productOrderItem Product order item DTO
     * @param orderItem Order item entity
     * @return An array of List<Product> elements, first being list of products, and second - list of services
     */
    @SuppressWarnings("unchecked")
    public List<Product>[] getProductsAndServices(ProductOrderItem productOrderItem, org.meveo.model.order.OrderItem orderItem) {

        List<Product> products = new ArrayList<>();
        List<Product> services = new ArrayList<>();
        if (productOrderItem != null) {
            int index = 1;
            if (productOrderItem.getProduct().getProductRelationship() != null && !productOrderItem.getProduct().getProductRelationship().isEmpty()) {
                for (ProductRelationship productRelationship : productOrderItem.getProduct().getProductRelationship()) {
                    if (index < orderItem.getOrderItemProductOfferings().size()) {
                        products.add(productRelationship.getProduct());
                    } else {
                        services.add(productRelationship.getProduct());
                    }
                    index++;
                }
            }
        }
        return new List[] { products, services };
    }

    public void validateOrSupplementSubscriptionRenewalFields(Product product, OfferTemplate offerTemplate) throws InvalidParameterException, MissingParameterException {

        Integer initialyActiveFor = (Integer) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR.getCharacteristicName(),
            Integer.class, null);
        if (initialyActiveFor == null && (offerTemplate.getSubscriptionRenewal() == null || offerTemplate.getSubscriptionRenewal().getInitialyActiveFor() == null)) {
            return;

            // Default the values from an offer
        } else if (initialyActiveFor == null) {
            setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR.getCharacteristicName(),
                offerTemplate.getSubscriptionRenewal().getInitialyActiveFor());
            setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR_UNIT.getCharacteristicName(),
                offerTemplate.getSubscriptionRenewal().getInitialyActiveForUnit());
            setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_END_OF_TERM_ACTION.getCharacteristicName(),
                offerTemplate.getSubscriptionRenewal().getEndOfTermAction());
            setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_AUTO_RENEW.getCharacteristicName(), offerTemplate.getSubscriptionRenewal().isAutoRenew());
            setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR.getCharacteristicName(), offerTemplate.getSubscriptionRenewal().getRenewFor());
            setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR_UNIT.getCharacteristicName(),
                offerTemplate.getSubscriptionRenewal().getRenewForUnit());
            if (offerTemplate.getSubscriptionRenewal().getTerminationReason() != null) {
                setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_TERMINATION_REASON.getCharacteristicName(),
                    offerTemplate.getSubscriptionRenewal().getTerminationReason().getCode());
            }
            setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_DAYS_NOTIFY_RENEWAL.getCharacteristicName(),
                offerTemplate.getSubscriptionRenewal().getDaysNotifyRenewal());
            setProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_EXTEND_AGREEMENT_PERIOD.getCharacteristicName(),
                offerTemplate.getSubscriptionRenewal().isExtendAgreementPeriodToSubscribedTillDate());
        }

        List<String> missingFields = new ArrayList<>();

        RenewalPeriodUnitEnum initialyActiveForUnit = (RenewalPeriodUnitEnum) getProductCharacteristic(product,
            OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR_UNIT.getCharacteristicName(), RenewalPeriodUnitEnum.class, null);
        if (initialyActiveForUnit == null) {
            missingFields.add(OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR_UNIT.getCharacteristicName());
        }

        EndOfTermActionEnum endOfTermAction = (EndOfTermActionEnum) getProductCharacteristic(product,
            OrderProductCharacteristicEnum.SUBSCRIPTION_END_OF_TERM_ACTION.getCharacteristicName(), EndOfTermActionEnum.class, null);
        if (endOfTermAction == null) {
            missingFields.add(OrderProductCharacteristicEnum.SUBSCRIPTION_END_OF_TERM_ACTION.getCharacteristicName());
        }

        boolean autoRenew = (boolean) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_AUTO_RENEW.getCharacteristicName(), Boolean.class, false);
        if (autoRenew) {
            Integer renewFor = (Integer) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR.getCharacteristicName(), Integer.class, null);
            if (renewFor == null) {
                missingFields.add(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR.getCharacteristicName());
            }
            RenewalPeriodUnitEnum renewForUnit = (RenewalPeriodUnitEnum) getProductCharacteristic(product,
                OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR_UNIT.getCharacteristicName(), RenewalPeriodUnitEnum.class, null);
            if (renewForUnit == null) {
                missingFields.add(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR_UNIT.getCharacteristicName());
            }
        }
        String terminationReasonCode = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_TERMINATION_REASON.getCharacteristicName(),
            String.class, null);

        if (terminationReasonCode != null) {
            SubscriptionTerminationReason terminationReason = terminationReasonService.findByCode(terminationReasonCode);
            if (terminationReason == null) {
                throw new InvalidParameterException(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_TERMINATION_REASON.getCharacteristicName(), terminationReasonCode);
            }
        } else if (endOfTermAction == EndOfTermActionEnum.TERMINATE) {
            missingFields.add(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_TERMINATION_REASON.getCharacteristicName());
        }

        if (!missingFields.isEmpty()) {
            throw new MissingParameterException(missingFields);
        }
    }

    /**
     * Extract from product characteristics values related to subscription renewal rule
     * 
     * @param product Product information
     * @return SubscriptionRenewalDto object
     * @throws InvalidParameterException
     */
    public SubscriptionRenewalDto extractSubscriptionRenewalDto(Product product) throws InvalidParameterException {

        SubscriptionRenewalDto renewRuleDto = new SubscriptionRenewalDto();

        renewRuleDto
            .setAutoRenew((boolean) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_AUTO_RENEW.getCharacteristicName(), Boolean.class, false));
        renewRuleDto.setDaysNotifyRenewal(
            (Integer) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_DAYS_NOTIFY_RENEWAL.getCharacteristicName(), Integer.class, null));
        renewRuleDto.setEndOfTermAction((EndOfTermActionEnum) getProductCharacteristic(product,
            OrderProductCharacteristicEnum.SUBSCRIPTION_END_OF_TERM_ACTION.getCharacteristicName(), EndOfTermActionEnum.class, null));
        renewRuleDto.setExtendAgreementPeriodToSubscribedTillDate(
            (boolean) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_EXTEND_AGREEMENT_PERIOD.getCharacteristicName(), Boolean.class, false));
        renewRuleDto.setInitialyActiveFor(
            (Integer) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR.getCharacteristicName(), Integer.class, null));
        renewRuleDto.setInitialyActiveForUnit((RenewalPeriodUnitEnum) getProductCharacteristic(product,
            OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR_UNIT.getCharacteristicName(), RenewalPeriodUnitEnum.class, null));
        renewRuleDto.setRenewFor((Integer) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR.getCharacteristicName(), Integer.class, null));
        renewRuleDto.setRenewForUnit((RenewalPeriodUnitEnum) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR_UNIT.getCharacteristicName(),
            RenewalPeriodUnitEnum.class, null));

        String terminationReasonCode = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_TERMINATION_REASON.getCharacteristicName(),
            String.class, null);

        if (terminationReasonCode != null) {
            SubscriptionTerminationReason terminationReason = terminationReasonService.findByCode(terminationReasonCode);
            if (terminationReason != null) {
                renewRuleDto.setTerminationReasonCode(terminationReasonCode);

            } else {
                throw new InvalidParameterException(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_TERMINATION_REASON.getCharacteristicName(), terminationReasonCode);
            }
        }
        return renewRuleDto;
    }

    public ApplicableDueDateDelayDto applicableDueDateDelay(String orderId) throws MeveoApiException {
        if (org.meveo.commons.utils.StringUtils.isBlank(orderId)) {
            missingParameters.add("orderId");
        }

        Order order = orderService.findByCode(orderId);
        if (order == null) {
            throw new EntityDoesNotExistsException(ProductOrder.class, orderId);
        }

        ApplicableDueDateDelayDto result = new ApplicableDueDateDelayDto();
        result.setCustom(false);

        if (!org.meveo.commons.utils.StringUtils.isBlank(order.getDueDateDelayEL())) {
            result.setLevel(DueDateDelayLevelEnum.ORDER);
            result.setDueDateDelayEL(order.getDueDateDelayEL());
        } else {
            BillingAccount ba = null;
            if (order.getOrderItems().get(0).getSubscription() != null) {
                ba = order.getOrderItems().get(0).getSubscription().getUserAccount().getBillingAccount();
            }
            if (ba != null) {
                if (!org.meveo.commons.utils.StringUtils.isBlank(ba.getCustomerAccount().getDueDateDelayEL())) {
                    result.setLevel(DueDateDelayLevelEnum.CA);
                    result.setDueDateDelayEL(ba.getCustomerAccount().getDueDateDelayEL());
                } else if (!org.meveo.commons.utils.StringUtils.isBlank(ba.getBillingCycle().getDueDateDelayEL())) {
                    result.setLevel(DueDateDelayLevelEnum.BC);
                    result.setDueDateDelayEL(ba.getBillingCycle().getDueDateDelayEL());
                }
            }
        }

        if (org.meveo.commons.utils.StringUtils.isBlank(result.getDueDateDelayEL())) {
            throw new MeveoApiException("No dueDateDelayEL found on Order, CA or BA.");
        }

        result.setReferenceDate(DueDateDelayReferenceDateEnum.guestExpression(result.getDueDateDelayEL()));

        if (result.getReferenceDate() == null) {
            result.setCustom(true);
        } else {
            result.setNumberOfDays(DueDateDelayReferenceDateEnum.guestNumberOfDays(result.getReferenceDate(), result.getDueDateDelayEL()));
        }

        return result;
    }

    public void simpleDueDateDelay(String orderId, ApplicableDueDateDelayDto postData) throws EntityDoesNotExistsException, MissingParameterException {
        if (org.meveo.commons.utils.StringUtils.isBlank(orderId)) {
            missingParameters.add("orderId");
        }

        if (org.meveo.commons.utils.StringUtils.isBlank(postData.getReferenceDate())) {
            missingParameters.add("dueDateDelayReferenceDate");
        }

        if (org.meveo.commons.utils.StringUtils.isBlank(postData.getNumberOfDays())) {
            missingParameters.add("numberOfDays");
        }

        handleMissingParameters();

        Order order = orderService.findByCode(orderId);
        if (order == null) {
            throw new EntityDoesNotExistsException(ProductOrder.class, orderId);
        }

        String el = postData.getReferenceDate().evaluateNumberOfDays(postData.getNumberOfDays());

        order.setDueDateDelayEL(el);
    }
}