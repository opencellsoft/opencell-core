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
package org.meveo.admin.action.order;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.billing.OrderApi;
import org.meveo.api.order.OrderProductCharacteristicEnum;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditableFieldNameEnum;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.order.OrderItemProductOffering;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.WirePaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.order.OrderService;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.tmf.dsmapi.catalog.resource.order.BillingAccount;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;
import org.tmf.dsmapi.catalog.resource.order.ProductRelationship;
import org.tmf.dsmapi.catalog.resource.product.BundledProductReference;

/**
 * Standard backing bean for {@link Order} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Mounir Bahije
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Named
@ViewScoped
public class OrderBean extends CustomFieldBean<Order> {

    private static final long serialVersionUID = 7399464661886086329L;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private SellerService sellerService;

    /**
     * Injected @{link Order} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OrderService orderService;

    @Inject
    private OrderApi orderApi;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ProductOfferingService productOfferingService;

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    @Inject
    private UserService userService;

    @Inject
    private AuditableFieldService auditableFieldService;
    
    @Inject
    private EntityToDtoConverter entityToDtoConverter;

    private OrderItem selectedOrderItem;

    private TreeNode offersTree;

    private List<OfferItemInfo> offerConfigurations;

    private PaymentMethodEnum paymentMethodType;

    private PaymentMethod paymentMethod;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public OrderBean() {
        super(Order.class);
    }

    @Override
    public Order initEntity() {
        super.initEntity();

        if (entity.getOrderItems() != null) {
            for (OrderItem orderItem : entity.getOrderItems()) {
                PersistenceUtils.initializeAndUnproxy(orderItem.getOrderItemProductOfferings());
                PersistenceUtils.initializeAndUnproxy(orderItem.getProductInstances());
            }
        }

        if (entity.getPaymentMethod() != null) {
            paymentMethodType = entity.getPaymentMethod().getPaymentType();
            paymentMethod = PersistenceUtils.initializeAndUnproxy(entity.getPaymentMethod());
        } else {
            // setting default value to CHECK ,in order to avoid PropertyNotFoundException and NPE during orderDetail rendering
            // and also to remain with the displayed the selectOneMenu which is set to CHECK by default
            paymentMethodType = PaymentMethodEnum.CHECK;
            entity.setPaymentMethod(new CheckPaymentMethod());
            this.entity.getPaymentMethod().updateAudit(currentUser);
        }
        return entity;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Order> getPersistenceService() {
        return orderService;
    }

    public void setSelectedOrderItem(OrderItem selectedOrderItem) {
        this.selectedOrderItem = selectedOrderItem;
    }

    public OrderItem getSelectedOrderItem() {
        return selectedOrderItem;
    }

    public TreeNode getOffersTree() {
        return offersTree;
    }

    public void setOffersTree(TreeNode offersTree) {
        this.offersTree = offersTree;
    }

    public void editOrderItem(OrderItem orderItemToEdit) {

        try {
            this.selectedOrderItem = orderItemToEdit;

            if (!orderItemToEdit.isTransient()) {
                try {
                    this.selectedOrderItem.setOrderItemDto(org.tmf.dsmapi.catalog.resource.order.ProductOrderItem.deserializeOrderItem(selectedOrderItem.getSource()));
                } catch (BusinessException e) {
                    log.error("Failed to deserialize order item DTO from a source");
                }
            }

            this.selectedOrderItem = cloneOrderItem(this.selectedOrderItem);

            if (this.selectedOrderItem.getOrderItemDto() != null) {
                offersTree = constructOfferItemsTreeAndConfiguration(
                    this.entity.getStatus() == OrderStatusEnum.IN_CREATION && selectedOrderItem.getAction() != OrderItemActionEnum.DELETE,
                    this.entity.getStatus() == OrderStatusEnum.IN_CREATION && selectedOrderItem.getAction() != OrderItemActionEnum.DELETE, null, null);
            }

        } catch (Exception e) {
            log.error("Failed to load order item for edit", e);
            messages.error(new BundleKey("messages", "order.orderItemEdit.ko"), e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            facesContext.validationFailed();
        }
    }

    public void newOrderItem() {
        selectedOrderItem = new OrderItem();
        offerConfigurations = null;

        if (entity.getOrderItems() == null) {
            selectedOrderItem.setItemId("1");
        } else {
            selectedOrderItem.setItemId(Integer.toString(entity.getOrderItems().size() + 1));
        }
    }

    /**
     * Cancel editing/creation of order item
     */
    public void cancelOrderItemEdit() {
        selectedOrderItem = null;
        offerConfigurations = null;
        offersTree = null;
    }

    /**
     * Save or update order item to order
     */
    @ActionMethod
    public void saveOrderItem() {

        try {
            // Reconstruct product offerings - add main offering. Related product offerings are added later bellow
            selectedOrderItem.getOrderItemProductOfferings().clear();
            selectedOrderItem.getOrderItemProductOfferings().add(new OrderItemProductOffering(selectedOrderItem, selectedOrderItem.getMainOffering(), 0));

            org.tmf.dsmapi.catalog.resource.order.ProductOrderItem orderItemDto = new org.tmf.dsmapi.catalog.resource.order.ProductOrderItem();
            orderItemDto.setAction(selectedOrderItem.getAction().name().toLowerCase());

            List<BillingAccount> billingAccountDtos = new ArrayList<>();
            BillingAccount billingAccountDto = new BillingAccount();
            if (selectedOrderItem.getAction() != OrderItemActionEnum.ADD) {
                Subscription subscription = selectedOrderItem.getSubscription();
                subscription = subscriptionService.refreshOrRetrieve(subscription);
                billingAccountDto.setId(subscription.getUserAccount().getCode());
            } else if (selectedOrderItem.getUserAccount() != null) {
                billingAccountDto.setId(selectedOrderItem.getUserAccount().getCode());
            }
            billingAccountDtos.add(billingAccountDto);
            orderItemDto.setBillingAccount(billingAccountDtos);
            orderItemDto.setProductOffering(new org.tmf.dsmapi.catalog.resource.product.ProductOffering());
            orderItemDto.setProduct(new Product());
            customFieldDataEntryBean.saveCustomFieldsToEntity(selectedOrderItem, true);
            orderItemDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(selectedOrderItem));

            // Save products and services when main offer is an offer
            if (selectedOrderItem.getMainOffering() instanceof OfferTemplate) {

                TreeNode offerNode = offersTree.getChildren().get(0);

                // Save main offer as offering and product
                orderItemDto.getProductOffering().setId(selectedOrderItem.getMainOffering().getCode());

                // For now only modify or delete of subscription is enabled
                if (selectedOrderItem.getAction() != OrderItemActionEnum.ADD) {
                    orderItemDto.getProduct().setId(selectedOrderItem.getSubscription().getCode());
                }
                orderItemDto.getProduct().setProductCharacteristic(mapToProductCharacteristics(((OfferItemInfo) offerNode.getData()).getCharacteristics()));
                orderItemDto.getProduct().getProductCharacteristic().addAll(customFieldsAsCharacteristics(((OfferItemInfo) offerNode.getData()).getEntityForCFValues()));

                List<ProductTemplate> productTemplates = new ArrayList<>();
                List<ServiceTemplate> serviceTemplates = new ArrayList<>();
                List<List<ProductCharacteristic>> productCharacteristics = new ArrayList<>();
                List<List<ProductCharacteristic>> serviceCharacteristics = new ArrayList<>();

                for (TreeNode groupingNode : offerNode.getChildren()) { // service or product grouping node
                    for (TreeNode serviceOrProduct : groupingNode.getChildren()) {

                        if (!(serviceOrProduct.getData() instanceof OfferItemInfo) || !((OfferItemInfo) serviceOrProduct.getData()).isSelected()) {
                            continue;
                        }

                        OfferItemInfo offerItemInfo = ((OfferItemInfo) serviceOrProduct.getData());

                        if (offerItemInfo.getTemplate() instanceof ProductTemplate) {
                            productTemplates.add((ProductTemplate) offerItemInfo.getTemplate());

                            List<ProductCharacteristic> productTemplateCharacteristics = mapToProductCharacteristics(offerItemInfo.getCharacteristics());
                            productTemplateCharacteristics.addAll(customFieldsAsCharacteristics(((OfferItemInfo) serviceOrProduct.getData()).getEntityForCFValues()));

                            productCharacteristics.add(productTemplateCharacteristics);

                        } else if (offerItemInfo.getTemplate() instanceof ServiceTemplate) {
                            serviceTemplates.add((ServiceTemplate) offerItemInfo.getTemplate());

                            List<ProductCharacteristic> serviceTemplateCharacteristics = mapToProductCharacteristics(offerItemInfo.getCharacteristics());
                            serviceTemplateCharacteristics.addAll(customFieldsAsCharacteristics(((OfferItemInfo) serviceOrProduct.getData()).getEntityForCFValues()));

                            serviceCharacteristics.add(serviceTemplateCharacteristics);
                        }
                    }
                }

                orderItemDto.getProductOffering().setBundledProductOffering(new ArrayList<BundledProductReference>());
                orderItemDto.getProduct().setProductRelationship(new ArrayList<ProductRelationship>());

                // Save product templates as bundled offerings and bundled products
                if (!productTemplates.isEmpty()) {

                    int index = 0;
                    for (ProductTemplate productTemplate : productTemplates) {

                        selectedOrderItem.getOrderItemProductOfferings()
                            .add(new OrderItemProductOffering(selectedOrderItem, productTemplate, selectedOrderItem.getOrderItemProductOfferings().size()));

                        BundledProductReference productOffering = new BundledProductReference();
                        productOffering.setReferencedId(productTemplate.getCode());
                        orderItemDto.getProductOffering().getBundledProductOffering().add(productOffering);

                        ProductRelationship relatedProduct = new ProductRelationship();
                        relatedProduct.setType("bundled");
                        Product productDto = new Product();

                        productDto.setProductCharacteristic(productCharacteristics.get(index));
                        relatedProduct.setProduct(productDto);
                        orderItemDto.getProduct().getProductRelationship().add(relatedProduct);

                        index++;
                    }
                }

                // Save service templates as bundled
                if (!serviceTemplates.isEmpty()) {

                    int index = 0;
                    for (ServiceTemplate serviceTemplate : serviceTemplates) {
                        ProductRelationship relatedProduct = new ProductRelationship();
                        relatedProduct.setType("bundled");
                        Product productDto = new Product();
                        productDto.setProductCharacteristic(serviceCharacteristics.get(index));
                        productDto.getProductCharacteristic()
                            .add(new ProductCharacteristic(OrderProductCharacteristicEnum.SERVICE_CODE.getCharacteristicName(), serviceTemplate.getCode()));
                        relatedProduct.setProduct(productDto);
                        orderItemDto.getProduct().getProductRelationship().add(relatedProduct);

                        index++;
                    }
                }

                // Save product properties when main offer is product
            } else {

                orderItemDto.getProductOffering().setId(selectedOrderItem.getMainOffering().getCode());
                orderItemDto.getProduct().setProductCharacteristic(mapToProductCharacteristics(offerConfigurations.get(0).getCharacteristics()));
                orderItemDto.getProduct().getProductCharacteristic().addAll(customFieldsAsCharacteristics(offerConfigurations.get(0).getEntityForCFValues()));
            }

            selectedOrderItem.setOrderItemDto(orderItemDto);
            selectedOrderItem.setSource(org.tmf.dsmapi.catalog.resource.order.ProductOrderItem.serializeOrderItem(orderItemDto));

            if (selectedOrderItem.getUserAccount() == null && selectedOrderItem.getSubscription() != null) {
                selectedOrderItem.setUserAccount(userAccountService.retrieveIfNotManaged(selectedOrderItem.getSubscription().getUserAccount()));
            }

            if (entity.getOrderItems() == null) {
                entity.setOrderItems(new ArrayList<OrderItem>());
            }
            if (!entity.getOrderItems().contains(selectedOrderItem)) {
                selectedOrderItem.setOrder(getEntity());
                entity.getOrderItems().add(selectedOrderItem);
            } else {
                entity.getOrderItems().set(entity.getOrderItems().indexOf(selectedOrderItem), selectedOrderItem);
            }

            selectedOrderItem = null;
            offerConfigurations = null;

            messages.info(new BundleKey("messages", "order.orderItemSaved.ok"));

        } catch (Exception e) {
            log.error("Failed to save order item ", e);
            messages.error(new BundleKey("messages", "order.orderItemSaved.ko"), e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            facesContext.validationFailed();
        }
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        if (entity.getOrderItems() == null || entity.getOrderItems().isEmpty()) {
            throw new ValidationException("At least one order item is required", "order.itemsRequired");
        }

        String result = super.saveOrUpdate(killConversation);

        // Execute workflow with every update
        if (entity.getStatus() != OrderStatusEnum.IN_CREATION) {
            entity = orderApi.initiateWorkflow(entity);
        }

        if (OrderStatusEnum.IN_CREATION.equals(entity.getStatus())) {
            // Status audit (to trace the passage from before "" to creation "IN_CREATION") need for lifecycle
            auditableFieldService.createFieldHistory(entity, AuditableFieldNameEnum.STATUS.getFieldName(), AuditChangeTypeEnum.STATUS, "",
                    String.valueOf(entity.getStatus()));
        }
        return result;
    }

    /**
     * Initiate processing of order
     * 
     * @return output view
     * 
     */
    @ActionMethod
    public String sendToProcess() {

        try {
            entity = orderService.refreshOrRetrieve(entity);
            entity = orderApi.initiateWorkflow(entity);
            messages.info(new BundleKey("messages", "order.sendToProcess.ok"));
            return "orderDetail";

        } catch (BusinessException e) {
            log.error("Failed to send order for processing ", e);
            messages.error(new BundleKey("messages", "order.sendToProcess.ko"), e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            facesContext.validationFailed();
        }
        return null;
    }

    /**
     * Construct a tree of what can/was be ordered for an offer and their configuration properties/characteristics
     * 
     * @param showAvailable Should checkboxes be shown for tree item selection
     * @return A tree
     */
    private TreeNode constructOfferItemsTreeAndConfiguration(boolean showAvailableServices, boolean showAvailableProducts,
            Map<String, Map<OrderProductCharacteristicEnum, Object>> subscriptionConfiguration, Map<String, BusinessCFEntity> existingOfferEntities) {

        offerConfigurations = new ArrayList<>();

        org.tmf.dsmapi.catalog.resource.order.ProductOrderItem orderItemDto = (org.tmf.dsmapi.catalog.resource.order.ProductOrderItem) this.selectedOrderItem.getOrderItemDto();

        TreeNode root = new DefaultTreeNode("Offer details", null);
        root.setExpanded(true);

        ProductOffering mainOffering = productOfferingService.retrieveIfNotManaged(this.selectedOrderItem.getMainOffering());

        // Take offer characteristics either from DTO (priority) or from current subscription configuration (will be used only for the first time when entering order item to modify
        // or delete and subscription is selected)
        Map<OrderProductCharacteristicEnum, Object> mainOfferCharacteristics = new HashMap<>();

        BusinessCFEntity mainEntityForCFValues = null;

        if (orderItemDto != null && orderItemDto.getProduct() != null) {
            mainOfferCharacteristics = productCharacteristicsToMap(orderItemDto.getProduct().getProductCharacteristic());

        } else if (subscriptionConfiguration != null && subscriptionConfiguration.containsKey(mainOffering.getCode())) {
            mainOfferCharacteristics = subscriptionConfiguration.get(mainOffering.getCode());
            if (existingOfferEntities != null) {
                Subscription subscriptionEntity = (Subscription) existingOfferEntities.get(mainOffering.getCode());
                mainEntityForCFValues = subscriptionEntity;
            }
        }

        // Default subscription date field to order date
        if (!mainOfferCharacteristics.containsKey(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE)) {
            mainOfferCharacteristics.put(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE, entity.getOrderDate());
        }
        Date mainOfferSubscriptionDate = (Date) mainOfferCharacteristics.get(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE);

        // Default quantity to 1 for product and bundle templates
        // Default instance.code to template.code for product and bundle templates
        if (!(mainOffering instanceof OfferTemplate)) {
            if (!mainOfferCharacteristics.containsKey(OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY)) {
                mainOfferCharacteristics.put(OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY, 1);
            }
            if (!mainOfferCharacteristics.containsKey(OrderProductCharacteristicEnum.PRODUCT_INSTANCE_CODE)) {
                mainOfferCharacteristics.put(OrderProductCharacteristicEnum.PRODUCT_INSTANCE_CODE, mainOffering.getCode());
            }
        }
        OfferItemInfo offerItemInfo;
        if(orderItemDto != null && orderItemDto.getProduct() != null){
            offerItemInfo = new OfferItemInfo(mainOffering, mainOfferCharacteristics, true, true, true, mainEntityForCFValues,
                    toCustomFieldsValues(extractCustomFieldsValues(orderItemDto.getProduct().getProductCharacteristic(), new Subscription())));
        }else{
            offerItemInfo = new OfferItemInfo(mainOffering, mainOfferCharacteristics, true, true, true, mainEntityForCFValues);
        }

        TreeNode mainOfferingNode = new DefaultTreeNode(mainOffering.getClass().getSimpleName(), offerItemInfo, root);
        mainOfferingNode.setExpanded(true);
        offerConfigurations.add(offerItemInfo);

        // Extract and update custom fields in GUI
        if (orderItemDto != null && orderItemDto.getProduct() != null) {
            extractAndMakeAvailableInGUICustomFields(orderItemDto.getProduct().getProductCharacteristic(), offerItemInfo.getEntityForCFValues());
        }

        // For offer templates list services and products subscribed
        if (mainOffering instanceof OfferTemplate) {

            List<Product>[] productsAndServices = orderApi.getProductsAndServices(orderItemDto, this.selectedOrderItem);

            // Show services - all or only the ones ordered
            if (showAvailableServices || !productsAndServices[1].isEmpty()) {
                TreeNode servicesNode = new DefaultTreeNode("ServiceList", "Service", mainOfferingNode);
                servicesNode.setExpanded(true);

                for (OfferServiceTemplate offerServiceTemplate : ((OfferTemplate) mainOffering).getOfferServiceTemplates()) {

                    // Find a matching ordered service product from DTO by comparing product characteristic "serviceCode"
                    Product serviceProductMatched = null;
                    for (Product serviceProduct : productsAndServices[1]) {
                        String serviceCode = (String) orderApi.getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SERVICE_CODE.getCharacteristicName(),
                            String.class, null);
                        if (offerServiceTemplate.getServiceTemplate().getCode().equals(serviceCode)) {
                            serviceProductMatched = serviceProduct;
                            break;
                        }
                    }

                    if (showAvailableServices || serviceProductMatched != null
                            || (subscriptionConfiguration != null && subscriptionConfiguration.containsKey(offerServiceTemplate.getServiceTemplate().getCode()))) {

                        // Take service characteristics either from DTO (priority) or from current subscription configuration (will be used only for the first time when entering
                        // order item to modify or delete and subscription is selected
                        Map<OrderProductCharacteristicEnum, Object> serviceCharacteristics = new HashMap<>();
                        ServiceInstance serviceInstanceEntity = null;
                        if (serviceProductMatched != null) {
                            serviceCharacteristics = productCharacteristicsToMap(serviceProductMatched.getProductCharacteristic());
                        } else if (subscriptionConfiguration != null && subscriptionConfiguration.containsKey(offerServiceTemplate.getServiceTemplate().getCode())) {
                            serviceCharacteristics = subscriptionConfiguration.get(offerServiceTemplate.getServiceTemplate().getCode());
                            if (existingOfferEntities != null) {
                                serviceInstanceEntity = (ServiceInstance) existingOfferEntities.get(offerServiceTemplate.getServiceTemplate().getCode());
                            }
                        }

                        // Default service subscription date field to subscription's subscription date and quantity to 1
                        if (!serviceCharacteristics.containsKey(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE)) {
                            serviceCharacteristics.put(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE, mainOfferSubscriptionDate);
                        }
                        if (!serviceCharacteristics.containsKey(OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY)) {
                            serviceCharacteristics.put(OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY, 1);
                        }

                        boolean isMandatory = offerServiceTemplate.isMandatory()
                                || (subscriptionConfiguration != null && subscriptionConfiguration.containsKey(offerServiceTemplate.getServiceTemplate().getCode()));
                        boolean isSelected = serviceProductMatched != null || isMandatory;

                        if(serviceProductMatched != null && serviceProductMatched.getProductCharacteristic() != null){
                            offerItemInfo = new OfferItemInfo(offerServiceTemplate.getServiceTemplate(), serviceCharacteristics, false, isSelected, isMandatory, serviceInstanceEntity,
                                    toCustomFieldsValues(extractCustomFieldsValues(serviceProductMatched.getProductCharacteristic(), new ServiceInstance())));
                        }else{
                            offerItemInfo = new OfferItemInfo(offerServiceTemplate.getServiceTemplate(), serviceCharacteristics, false, isSelected, isMandatory, serviceInstanceEntity);
                        }

                        new DefaultTreeNode(ServiceTemplate.class.getSimpleName(), offerItemInfo, servicesNode);
                        if (offerItemInfo.isSelected()) {
                            offerConfigurations.add(offerItemInfo);

                            // Extract and update custom fields in GUI
                            if (serviceProductMatched != null) {
                                extractAndMakeAvailableInGUICustomFields(serviceProductMatched.getProductCharacteristic(), offerItemInfo.getEntityForCFValues());
                            }
                        }
                    }
                }
            }

            // Show products - all or only the ones ordered
            if ((showAvailableProducts || this.selectedOrderItem.getOrderItemProductOfferings().size() > 1)
                    && !((OfferTemplate) mainOffering).getOfferProductTemplates().isEmpty()) {
                TreeNode productsNode = null;
                productsNode = new DefaultTreeNode("ProductList", "Product", mainOfferingNode);
                productsNode.setSelectable(false);
                productsNode.setExpanded(true);

                for (OfferProductTemplate offerProductTemplate : ((OfferTemplate) mainOffering).getOfferProductTemplates()) {

                    // Find a matching ordered product offering
                    Product productProductMatched = null;
                    int index = 0;
                    for (OrderItemProductOffering orderItemOffering : this.selectedOrderItem.getOrderItemProductOfferings().subList(1,
                        this.selectedOrderItem.getOrderItemProductOfferings().size())) {
                        ProductOffering offering = orderItemOffering.getProductOffering();

                        if (offerProductTemplate.getProductTemplate().equals(offering)) {
                            productProductMatched = productsAndServices[0].get(index);
                            break;
                        }
                        index++;
                    }

                    if (showAvailableProducts || productProductMatched != null) {

                        // Take product characteristics either from DTO (priority) or from current product configuration (will be used only for the first time when entering
                        // order item to modify or delete and subscription/product is selected
                        Map<OrderProductCharacteristicEnum, Object> productCharacteristics = new HashMap<>();
                        ProductInstance productInstanceEntity = null;
                        if (productProductMatched != null) {
                            productCharacteristics = productCharacteristicsToMap(productProductMatched.getProductCharacteristic());
                        } else if (subscriptionConfiguration != null && subscriptionConfiguration.containsKey(offerProductTemplate.getProductTemplate().getCode())) {
                            productCharacteristics = subscriptionConfiguration.get(offerProductTemplate.getProductTemplate());
                            if (existingOfferEntities != null) {
                                productInstanceEntity = (ProductInstance) existingOfferEntities.get(offerProductTemplate.getProductTemplate());
                            }
                        }

                        // Default service subscription date field to subscription's subscription date or quote date if product is not part of offer template and quantity to 1
                        if (!productCharacteristics.containsKey(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE)) {
                            productCharacteristics.put(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE,
                                mainOfferSubscriptionDate != null ? mainOfferSubscriptionDate : entity.getOrderDate());
                        }
                        if (!productCharacteristics.containsKey(OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY)) {
                            productCharacteristics.put(OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY, 1);
                        }
                        if (!productCharacteristics.containsKey(OrderProductCharacteristicEnum.PRODUCT_INSTANCE_CODE)) {
                            productCharacteristics.put(OrderProductCharacteristicEnum.PRODUCT_INSTANCE_CODE, offerProductTemplate.getProductTemplate().getCode());
                        }

                        if(productProductMatched != null && productProductMatched.getProductCharacteristic() != null){
                            offerItemInfo = new OfferItemInfo(offerProductTemplate.getProductTemplate(), productCharacteristics, false,
                                productProductMatched != null || offerProductTemplate.isMandatory(), offerProductTemplate.isMandatory(), productInstanceEntity,
                                    toCustomFieldsValues(extractCustomFieldsValues(productProductMatched.getProductCharacteristic(), new ProductInstance())));
                        }else{
                            offerItemInfo = new OfferItemInfo(offerProductTemplate.getProductTemplate(), productCharacteristics, false,
                                    productProductMatched != null || offerProductTemplate.isMandatory(), offerProductTemplate.isMandatory(), productInstanceEntity);
                        }
                        new DefaultTreeNode(ProductTemplate.class.getSimpleName(), offerItemInfo, productsNode);

                        if (offerItemInfo.isSelected()) {
                            offerConfigurations.add(offerItemInfo);

                            // Extract and update custom fields in GUI
                            if (productProductMatched != null) {
                                extractAndMakeAvailableInGUICustomFields(productProductMatched.getProductCharacteristic(), offerItemInfo.getEntityForCFValues());
                            }
                        }

                    }
                }
            }
        }
        return root;
    }

    private CustomFieldValues toCustomFieldsValues(Map<CustomFieldTemplate, Object> extractCustomFieldsValues) {
        CustomFieldValues customFieldValues = new CustomFieldValues();
        extractCustomFieldsValues.keySet().forEach(customFieldTemplate -> {
            CustomFieldValue customFieldValue = new CustomFieldValue();
            customFieldValue.setValue(extractCustomFieldsValues.get(customFieldTemplate));
            if(customFieldTemplate.isVersionable()){
                customFieldValues.setValue(customFieldTemplate.getCode(), new DatePeriod(), 0, extractCustomFieldsValues.get(customFieldTemplate));
            }else{
                customFieldValues.setValue(customFieldTemplate.getCode(), extractCustomFieldsValues.get(customFieldTemplate));
            }
        });
        return customFieldValues;
    }

    /**
     * Subscription selected. Update offer information if necessary.
     * 
     * @param event faces select event
     */
    public void onSubscriptionSet(SelectEvent event) {

        if (selectedOrderItem.getSubscription() != null && selectedOrderItem.getSubscription().equals(event.getObject())) {
            return;
        }
        selectedOrderItem.setSubscription((Subscription) event.getObject());
        selectedOrderItem.resetMainOffering(((Subscription) event.getObject()).getOffer());
        offerConfigurations = null;

        Map<String, Map<OrderProductCharacteristicEnum, Object>> subscriptionConfiguration = null;
        Map<String, BusinessCFEntity> subscriptionEntities = null;

        // Gather information about instantiated/active services
        if (selectedOrderItem.getAction() == OrderItemActionEnum.MODIFY || selectedOrderItem.getAction() == OrderItemActionEnum.DELETE) {

            subscriptionConfiguration = new HashMap<>();
            subscriptionEntities = new HashMap<>();

            Map<OrderProductCharacteristicEnum, Object> offerConfiguration = new HashMap<>();

            Subscription subscription = selectedOrderItem.getSubscription();
            offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE, subscription.getSubscriptionDate());
            offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_END_DATE, subscription.getEndAgreementDate());

            if (selectedOrderItem.getAction() == OrderItemActionEnum.MODIFY) {

                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR, subscription.getSubscriptionRenewal().getInitialyActiveFor());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR_UNIT, subscription.getSubscriptionRenewal().getInitialyActiveForUnit());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_AUTO_RENEW, subscription.getSubscriptionRenewal().isAutoRenew());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_DAYS_NOTIFY_RENEWAL, subscription.getSubscriptionRenewal().getDaysNotifyRenewal());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_END_OF_TERM_ACTION, subscription.getSubscriptionRenewal().getEndOfTermAction());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_EXTEND_AGREEMENT_PERIOD,
                    subscription.getSubscriptionRenewal().isExtendAgreementPeriodToSubscribedTillDate());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR, subscription.getSubscriptionRenewal().getRenewFor());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR_UNIT, subscription.getSubscriptionRenewal().getRenewForUnit());
                if (subscription.getSubscriptionRenewal().getTerminationReason() != null) {
                    offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_TERMINATION_REASON,
                        subscription.getSubscriptionRenewal().getTerminationReason().getCode());
                }
            }

            subscriptionConfiguration.put(subscription.getOffer().getCode(), offerConfiguration);
            subscriptionEntities.put(subscription.getOffer().getCode(), subscription);

            if (selectedOrderItem.getAction() == OrderItemActionEnum.MODIFY) {
                for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {

                    offerConfiguration = new HashMap<>();
                    offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_DATE, serviceInstance.getSubscriptionDate());
                    offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_END_DATE, serviceInstance.getEndAgreementDate());
                    offerConfiguration.put(OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY, serviceInstance.getQuantity());
                    subscriptionConfiguration.put(serviceInstance.getServiceTemplate().getCode(), offerConfiguration);
                    subscriptionEntities.put(serviceInstance.getServiceTemplate().getCode(), serviceInstance);
                }
            }
        }

        offersTree = constructOfferItemsTreeAndConfiguration(selectedOrderItem.getAction() == OrderItemActionEnum.MODIFY, true, subscriptionConfiguration, subscriptionEntities);

    }

    /**
     * New product offering is selected - need to reset orderItem values and the offer tree
     * 
     * @param event faces select event
     */
    public void onMainProductOfferingSet(SelectEvent event) {

        if (selectedOrderItem.getMainOffering() == null || !selectedOrderItem.getMainOffering().equals(event.getObject())) {
            selectedOrderItem.resetMainOffering((ProductOffering) event.getObject());
            offerConfigurations = null;

            Map<String, Map<OrderProductCharacteristicEnum, Object>> subscriptionConfiguration = null;

            if (selectedOrderItem.getMainOffering() instanceof OfferTemplate) {

                OfferTemplate selectedOffer = (OfferTemplate) selectedOrderItem.getMainOffering();
                Map<OrderProductCharacteristicEnum, Object> offerConfiguration = new HashMap<>();
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR, selectedOffer.getSubscriptionRenewal().getInitialyActiveFor());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_INITIALLY_ACTIVE_FOR_UNIT, selectedOffer.getSubscriptionRenewal().getInitialyActiveForUnit());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_AUTO_RENEW, selectedOffer.getSubscriptionRenewal().isAutoRenew());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_DAYS_NOTIFY_RENEWAL, selectedOffer.getSubscriptionRenewal().getDaysNotifyRenewal());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_END_OF_TERM_ACTION, selectedOffer.getSubscriptionRenewal().getEndOfTermAction());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_EXTEND_AGREEMENT_PERIOD,
                    selectedOffer.getSubscriptionRenewal().isExtendAgreementPeriodToSubscribedTillDate());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR, selectedOffer.getSubscriptionRenewal().getRenewFor());
                offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_FOR_UNIT, selectedOffer.getSubscriptionRenewal().getRenewForUnit());
                if (selectedOffer.getSubscriptionRenewal().getTerminationReason() != null) {
                    offerConfiguration.put(OrderProductCharacteristicEnum.SUBSCRIPTION_RENEW_TERMINATION_REASON,
                        selectedOffer.getSubscriptionRenewal().getTerminationReason().getCode());
                }
                subscriptionConfiguration = new HashMap<>();
                subscriptionConfiguration.put(selectedOffer.getCode(), offerConfiguration);
            }

            offersTree = constructOfferItemsTreeAndConfiguration(true, true, subscriptionConfiguration, null);
        }
    }

    /**
     * Propagate main offer item properties to services and products where it was not set yet
     * 
     * @param event faces select event
     */
    public void onMainCharacteristicsSet(SelectEvent event) {
        if (!(boolean) event.getComponent().getAttributes().get("isMain")) {
            return;
        }

        OrderProductCharacteristicEnum characteristicEnum = OrderProductCharacteristicEnum
            .getByCharacteristicName((String) event.getComponent().getAttributes().get("characteristic"));
        for (OfferItemInfo offerItemInfo : offerConfigurations) {
            if (offerItemInfo.getCharacteristics().get(characteristicEnum) == null) {
                offerItemInfo.getCharacteristics().put(characteristicEnum, event.getObject());
            }
        }
    }

    /**
     * Convert product characteristics to a map of values extracting only those values that match OrderProductCharacteristicEnum values
     * 
     * @param characteristics Product characteristics to check
     * @return A map of values
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map<OrderProductCharacteristicEnum, Object> productCharacteristicsToMap(List<ProductCharacteristic> characteristics) {
        Map<OrderProductCharacteristicEnum, Object> values = new HashMap<>();

        for (ProductCharacteristic productCharacteristic : characteristics) {
            if (productCharacteristic.getValue() == null) {
                continue;
            }

            OrderProductCharacteristicEnum characteristicEnum = OrderProductCharacteristicEnum.getByCharacteristicName(productCharacteristic.getName());
            // No matching characteristic found
            if (characteristicEnum == null) {
                continue;
            }
            Class valueClazz = characteristicEnum.getClazz();

            if (valueClazz == String.class) {
                values.put(characteristicEnum, productCharacteristic.getValue());
            } else if (valueClazz == BigDecimal.class) {
                values.put(characteristicEnum, new BigDecimal(productCharacteristic.getValue()));
            } else if (valueClazz == Date.class) {
                values.put(characteristicEnum, DateUtils.parseDateWithPattern(productCharacteristic.getValue(), DateUtils.DATE_PATTERN));
            } else if (valueClazz == Integer.class) {
                values.put(characteristicEnum, new Integer(productCharacteristic.getValue()));
            } else if (valueClazz == Boolean.class) {
                values.put(characteristicEnum, new Boolean(productCharacteristic.getValue()));
            } else if (valueClazz.isEnum()) {
                values.put(characteristicEnum, Enum.valueOf(valueClazz, productCharacteristic.getValue()));
            } else if (BusinessEntity.class.isAssignableFrom(valueClazz)) {
                values.put(characteristicEnum, productCharacteristic.getValue()); // Right now a code is shown as value element in GUI.
            }
        }
        return values;
    }

    /**
     * Convert a map of values to a list of product characteristic entities
     * 
     * @param values Map of values
     * @return List of product characteristic entities
     */
    @SuppressWarnings("rawtypes")
    private List<ProductCharacteristic> mapToProductCharacteristics(Map<OrderProductCharacteristicEnum, Object> values) {

        List<ProductCharacteristic> characteristics = new ArrayList<>();

        for (Entry<OrderProductCharacteristicEnum, Object> valueInfo : values.entrySet()) {

            if (valueInfo.getValue() != null) {
                ProductCharacteristic productCharacteristic = new ProductCharacteristic();
                productCharacteristic.setName(valueInfo.getKey().getCharacteristicName());
                characteristics.add(productCharacteristic);

                Class valueClazz = valueInfo.getKey().getClazz();

                if (valueClazz == String.class || valueClazz == BigDecimal.class || valueClazz == Integer.class || valueClazz == Boolean.class) {
                    productCharacteristic.setValue(valueInfo.getValue().toString());
                } else if (valueClazz == Date.class) {
                    productCharacteristic.setValue(DateUtils.formatDateWithPattern((Date) valueInfo.getValue(), DateUtils.DATE_PATTERN));
                } else if (valueClazz.isEnum()) {
                    productCharacteristic.setValue(((Enum) valueInfo.getValue()).name());
                } else if (BusinessEntity.class.isAssignableFrom(valueClazz)) {
                    productCharacteristic.setValue(valueInfo.getValue().toString());// Right now a code is shown as value element in GUI.
                }
            }
        }

        return characteristics;
    }

    /**
     * Tree node is selected or unselected via checkbox, show appropriate service and product configuration
     */
    public void onTreeNodeSelection() {

        offerConfigurations = new ArrayList<>();

        if (selectedOrderItem.getMainOffering() instanceof OfferTemplate) {

            // Add offer configuration
            TreeNode offerNode = offersTree.getChildren().get(0);
            offerConfigurations.add((OfferItemInfo) offerNode.getData());

            for (TreeNode groupingNode : offerNode.getChildren()) { // service or product grouping node
                for (TreeNode serviceOrProduct : groupingNode.getChildren()) {

                    if (serviceOrProduct.getData() instanceof OfferItemInfo && ((OfferItemInfo) serviceOrProduct.getData()).isSelected()) {
                        offerConfigurations.add((OfferItemInfo) serviceOrProduct.getData());
                    }
                }
            }
        }
    }

    /**
     * Action type changed - clear the rest of information
     */
    public void onActionTypeChange() {

        selectedOrderItem.resetMainOffering(null);
        selectedOrderItem.setSubscription(null);
        offerConfigurations = null;

    }

    public List<OfferItemInfo> getOfferConfigurations() {
        return offerConfigurations;
    }

    /**
     * Extract custom fields from product characteristics and make then available in GUI. Only non-versioned custom fields are supported.
     * 
     * @param characteristics Product characteristics
     * @param cfEntity Custom field entity values will be applied to
     */
    private void extractAndMakeAvailableInGUICustomFields(List<ProductCharacteristic> characteristics, BusinessCFEntity cfEntity) {

        Map<CustomFieldTemplate, Object> cfValues = new HashMap<>();

        if (characteristics == null || characteristics.isEmpty()) {
            return;
        }
        customFieldDataEntryBean.setCustomFieldValues(extractCustomFieldsValues(characteristics, cfEntity), cfEntity);
    }

    private Map<CustomFieldTemplate, Object> extractCustomFieldsValues(List<ProductCharacteristic> characteristics, BusinessCFEntity cfEntity) {
        Map<CustomFieldTemplate, Object> cfValues = new HashMap<>();

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cfEntity);

        for (ProductCharacteristic characteristic : characteristics) {
            if (characteristic.getName() != null && cfts.containsKey(characteristic.getName())) {
                CustomFieldTemplate cft = cfts.get(characteristic.getName());
                cfValues.put(cft, CustomFieldValue.parseValueFromString(cft, characteristic.getValue()));
            }
        }
        return cfValues;
    }

    /**
     * Convert custom fields to product characteristics. Only non-versioned custom fields are supported.
     * 
     * @param cfEntity Custom field entity values will be applied to
     * @return
     * @throws BusinessException General business exception
     */
    private List<ProductCharacteristic> customFieldsAsCharacteristics(BusinessCFEntity cfEntity) throws BusinessException {

        List<ProductCharacteristic> characteristics = new ArrayList<>();

        Map<CustomFieldTemplate, Object> cfValues = customFieldDataEntryBean.getFieldValuesLatestValue(cfEntity);
        for (Entry<CustomFieldTemplate, Object> cfValue : cfValues.entrySet()) {
            characteristics.add(new ProductCharacteristic(cfValue.getKey().getCode(), CustomFieldValue.convertValueToString(cfValue.getKey(), cfValue.getValue())));
        }

        return characteristics;
    }

    private OrderItem cloneOrderItem(OrderItem itemToClone) throws BusinessException {

        try {
            return (OrderItem) BeanUtilsBean.getInstance().cloneBean(itemToClone);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to clone orderItem for edit", e);
            throw new BusinessException(e);
        }
    }

    /**
     * Order is editable only in non-final states and when order is routed to a user group - user must belong to that or a higher group
     * 
     * @return Is order editable
     */
    public boolean isOrderEditable() {
        getEntity();// This will initialize entity if not done so yet
        boolean editable = entity.getStatus() != OrderStatusEnum.CANCELLED && entity.getStatus() != OrderStatusEnum.COMPLETED && entity.getStatus() != OrderStatusEnum.REJECTED;

        if (editable && entity.getRoutedToUserGroup() != null) {
            UserHierarchyLevel userGroup = userHierarchyLevelService.retrieveIfNotManaged(entity.getRoutedToUserGroup());
            User user = userService.findByUsername(currentUser.getUserName());
            editable = userGroup.isUserBelongsHereOrHigher(user);
        }

        return editable;
    }

    /**
     * Update entity used for CF field association with entered code. Applies to subscriptions and product instances
     * 
     * @param itemInfo Configuration item info (tree item)
     * @param characteristicName Characteristic's name corresponding to code value
     */
    public void updateCFEntityCode(OfferItemInfo itemInfo, OrderProductCharacteristicEnum characteristicName) {
        itemInfo.getEntityForCFValues().setCode((String) itemInfo.getCharacteristics().get(characteristicName));
    }

    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.paymentMethod.updateAudit(currentUser);
    }

    public void changePaymentMethodType() {

        if (paymentMethodType == PaymentMethodEnum.CARD) {
            entity.setPaymentMethod(new CardPaymentMethod());
        } else if (paymentMethodType == PaymentMethodEnum.CHECK) {
            entity.setPaymentMethod(new CheckPaymentMethod());
        } else if (paymentMethodType == PaymentMethodEnum.WIRETRANSFER) {
            entity.setPaymentMethod(new WirePaymentMethod());
        } else if (paymentMethodType == PaymentMethodEnum.DIRECTDEBIT) {
            entity.setPaymentMethod(new DDPaymentMethod());
        } else if (paymentMethodType == null) {
            entity.setPaymentMethod(null);
        }
        setPaymentMethod(entity.getPaymentMethod());
    }
    
    @ActionMethod
    public void updateStatus(OrderStatusEnum status) throws BusinessException {
        entity.setStatus(status);
        saveOrUpdate(entity);
    }

    /**
     * Get Seller's list
     * @return list of sellers
     */
    public List<Seller> listSellers() {
        if (sellerService.list() != null) {
            return sellerService.list();
        } else {
            return new ArrayList<Seller>();
        }
    }

    /**
     * Get configured payment method's list
     * @return list of payment methods
     */
    public List<PaymentMethod> listPaymentMethod() {
        if (Objects.nonNull(selectedOrderItem) && Objects.nonNull(selectedOrderItem.getUserAccount()) ) {
            UserAccount userAccount = userAccountService.findById(selectedOrderItem.getUserAccount().getId());
            return userAccount.getBillingAccount().getCustomerAccount().getPaymentMethods();
        }
        return Collections.emptyList();
    }

    public PaymentMethod getPaymentMethodById(String id) {
        return id.isBlank() ? null : listPaymentMethod().stream().filter(pm -> pm.getId().equals(Long.valueOf(id))).findFirst().get();
    }
}
