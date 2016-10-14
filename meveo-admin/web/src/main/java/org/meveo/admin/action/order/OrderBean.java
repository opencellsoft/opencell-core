/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.order;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.action.admin.custom.CustomFieldDataEntryBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.billing.OrderApi;
import org.meveo.api.order.OrderProductCharacteristicEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.order.OrderItemService;
import org.meveo.service.order.OrderService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;
import org.tmf.dsmapi.catalog.resource.order.ProductRelationship;
import org.tmf.dsmapi.catalog.resource.product.BundledProductReference;

/**
 * Standard backing bean for {@link Order} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class OrderBean extends CustomFieldBean<Order> {

    private static final long serialVersionUID = 7399464661886086329L;

    /**
     * Injected @{link Order} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OrderService orderService;

    @Inject
    private OrderItemService orderItemService;

    @Inject
    private OrderApi orderApi;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ProductOfferingService productOfferingService;

    @Inject
    private CustomFieldDataEntryBean customFieldDataEntryBean;

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    private ParamBean paramBean = ParamBean.getInstance();

    private OrderItem selectedOrderItem;

    private TreeNode offersTree;

    private List<OfferItemInfo> offerConfigurations;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public OrderBean() {
        super(Order.class);
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
            if (orderItemToEdit.isTransient()) {
                this.selectedOrderItem = orderItemToEdit;

            } else {

                this.selectedOrderItem = orderItemService.refreshOrRetrieve(orderItemToEdit);

                try {
                    this.selectedOrderItem.setOrderItemDto(org.tmf.dsmapi.catalog.resource.order.OrderItem.deserializeOrderItem(selectedOrderItem.getSource()));
                } catch (BusinessException e) {
                    log.error("Failed to deserialize order item DTO from a source");
                }
            }

            this.selectedOrderItem = cloneOrderItem(this.selectedOrderItem);

            if (this.selectedOrderItem.getOrderItemDto() != null) {
                offersTree = constructOfferItemsTreeAndConfiguration(this.entity.getStatus() == OrderStatusEnum.IN_CREATION
                        && selectedOrderItem.getAction() != OrderItemActionEnum.DELETE, this.entity.getStatus() == OrderStatusEnum.IN_CREATION
                        && selectedOrderItem.getAction() != OrderItemActionEnum.DELETE, null, null);
            }

        } catch (Exception e) {
            messages.error(new BundleKey("messages", "order.orderItemEdit.ko"), e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            FacesContext.getCurrentInstance().validationFailed();
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
            selectedOrderItem.getProductOfferings().clear();
            selectedOrderItem.getProductOfferings().add(selectedOrderItem.getMainOffering());

            org.tmf.dsmapi.catalog.resource.order.OrderItem orderItemDto = new org.tmf.dsmapi.catalog.resource.order.OrderItem();
            orderItemDto.setProductOffering(new org.tmf.dsmapi.catalog.resource.product.ProductOffering());
            orderItemDto.setProduct(new Product());

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

                        selectedOrderItem.getProductOfferings().add(productTemplate);

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
                        productDto.getProductCharacteristic().add(
                            new ProductCharacteristic(OrderProductCharacteristicEnum.SERVICE_CODE.getCharacteristicName(), serviceTemplate.getCode()));
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
            selectedOrderItem.setSource(org.tmf.dsmapi.catalog.resource.order.OrderItem.serializeOrderItem(orderItemDto));

            if (selectedOrderItem.getUserAccount() == null && selectedOrderItem.getSubscription() != null) {
                selectedOrderItem.setUserAccount(userAccountService.refreshOrRetrieve(selectedOrderItem.getSubscription().getUserAccount()));
            }

            if (entity.getOrderItems() == null) {
                entity.setOrderItems(new ArrayList<OrderItem>());
            }
            if (!entity.getOrderItems().contains(selectedOrderItem)) {
                selectedOrderItem.setOrder(getEntity());
                selectedOrderItem.setProvider(getCurrentProvider());
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
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        String result = super.saveOrUpdate(killConversation);
        
        // Execute workflow with every update
        if (entity.getStatus() != OrderStatusEnum.IN_CREATION) {
            entity = orderApi.initiateWorkflow(entity, getCurrentUser());
        }
        return result;
    }

    /**
     * Initiate processing of order
     * 
     * @throws BusinessException
     */
    public void sendToProcess() {

        try {
            entity = orderApi.initiateWorkflow(entity, getCurrentUser());
            messages.info(new BundleKey("messages", "order.sendToProcess.ok"));

        } catch (BusinessException e) {
            log.error("Failed to send order for processing ", e);
            messages.error(new BundleKey("messages", "order.sendToProcess.ko"), e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            FacesContext.getCurrentInstance().validationFailed();
        }
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

        org.tmf.dsmapi.catalog.resource.order.OrderItem orderItemDto = (org.tmf.dsmapi.catalog.resource.order.OrderItem) this.selectedOrderItem.getOrderItemDto();

        TreeNode root = new DefaultTreeNode("Offer details", null);
        root.setExpanded(true);

        ProductOffering mainOffering = productOfferingService.refreshOrRetrieve(this.selectedOrderItem.getMainOffering());

        // Take offer characteristics either from DTO (priority) or from current subscription configuration (will be used only for the first time when entering order item to modify
        // or delete and subscription is selected)
        Map<OrderProductCharacteristicEnum, Object> mainOfferCharacteristics = new HashMap<>();
        Subscription subscriptionEntity = null;
        if (orderItemDto != null && orderItemDto.getProduct() != null) {
            mainOfferCharacteristics = productCharacteristicsToMap(orderItemDto.getProduct().getProductCharacteristic());

        } else if (subscriptionConfiguration != null && subscriptionConfiguration.containsKey(mainOffering.getCode())) {
            mainOfferCharacteristics = subscriptionConfiguration.get(mainOffering.getCode());
            if (existingOfferEntities != null) {
                subscriptionEntity = (Subscription) existingOfferEntities.get(mainOffering.getCode());
            }
        }

        OfferItemInfo offerItemInfo = new OfferItemInfo(mainOffering, mainOfferCharacteristics, true, true, true, subscriptionEntity);
        TreeNode mainOfferingNode = new DefaultTreeNode(mainOffering.getClass().getSimpleName(), offerItemInfo, root);
        mainOfferingNode.setExpanded(true);
        offerConfigurations.add(offerItemInfo);

        // Extract and update custom fields in GUI
        if (orderItemDto != null && orderItemDto.getProduct() != null) {
            extractAndMakeAvailableInGUICustomFields(orderItemDto.getProduct().getProductCharacteristic(), offerItemInfo.getEntityForCFValues(), getCurrentProvider());
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
                        boolean isMandatory = offerServiceTemplate.isMandatory()
                                || (subscriptionConfiguration != null && subscriptionConfiguration.containsKey(offerServiceTemplate.getServiceTemplate().getCode()));
                        boolean isSelected = serviceProductMatched != null || isMandatory;

                        offerItemInfo = new OfferItemInfo(offerServiceTemplate.getServiceTemplate(), serviceCharacteristics, false, isSelected, isMandatory, serviceInstanceEntity);

                        new DefaultTreeNode(ServiceTemplate.class.getSimpleName(), offerItemInfo, servicesNode);
                        if (offerItemInfo.isSelected()) {
                            offerConfigurations.add(offerItemInfo);

                            // Extract and update custom fields in GUI
                            if (serviceProductMatched != null) {
                                extractAndMakeAvailableInGUICustomFields(serviceProductMatched.getProductCharacteristic(), offerItemInfo.getEntityForCFValues(),
                                    getCurrentProvider());
                            }
                        }
                    }
                }
            }

            // Show products - all or only the ones ordered
            if ((showAvailableProducts || this.selectedOrderItem.getProductOfferings().size() > 1) && !((OfferTemplate) mainOffering).getOfferProductTemplates().isEmpty()) {
                TreeNode productsNode = null;
                productsNode = new DefaultTreeNode("ProductList", "Product", mainOfferingNode);
                productsNode.setSelectable(false);
                productsNode.setExpanded(true);

                for (OfferProductTemplate offerProductTemplate : ((OfferTemplate) mainOffering).getOfferProductTemplates()) {

                    // Find a matching ordered product offering
                    Product productProductMatched = null;
                    int index = 0;
                    for (ProductOffering offering : this.selectedOrderItem.getProductOfferings().subList(1, this.selectedOrderItem.getProductOfferings().size())) {
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

                        offerItemInfo = new OfferItemInfo(offerProductTemplate.getProductTemplate(), productCharacteristics, false, productProductMatched != null
                                || offerProductTemplate.isMandatory(), offerProductTemplate.isMandatory(), productInstanceEntity);
                        new DefaultTreeNode(ProductTemplate.class.getSimpleName(), offerItemInfo, productsNode);

                        if (offerItemInfo.isSelected()) {
                            offerConfigurations.add(offerItemInfo);

                            // Extract and update custom fields in GUI
                            if (productProductMatched != null) {
                                extractAndMakeAvailableInGUICustomFields(productProductMatched.getProductCharacteristic(), offerItemInfo.getEntityForCFValues(),
                                    getCurrentProvider());
                            }
                        }

                    }
                }
            }
        }
        return root;
    }

    /**
     * Subscription selected. Update offer information if necessary.
     * 
     * @param event
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

        offersTree = constructOfferItemsTreeAndConfiguration(selectedOrderItem.getAction() == OrderItemActionEnum.MODIFY, false, subscriptionConfiguration, subscriptionEntities);

    }

    /**
     * New product offering is selected - need to reset orderItem values and the offer tree
     * 
     * @param event
     */
    public void onMainProductOfferingSet(SelectEvent event) {

        if (selectedOrderItem.getMainOffering() == null || !selectedOrderItem.getMainOffering().equals(event.getObject())) {
            selectedOrderItem.resetMainOffering((ProductOffering) event.getObject());
            offerConfigurations = null;

            offersTree = constructOfferItemsTreeAndConfiguration(true, true, null, null);
        }
    }

    /**
     * Propagate main offer item properties to services and products where it was not set yet
     * 
     * @param event
     */
    public void onMainCharacteristicsSet(SelectEvent event) {
        if (!(boolean) event.getComponent().getAttributes().get("isMain")) {
            return;
        }

        OrderProductCharacteristicEnum characteristicEnum = OrderProductCharacteristicEnum.getByCharacteristicName((String) event.getComponent().getAttributes()
            .get("characteristic"));
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
    private Map<OrderProductCharacteristicEnum, Object> productCharacteristicsToMap(List<ProductCharacteristic> characteristics) {
        Map<OrderProductCharacteristicEnum, Object> values = new HashMap<>();

        for (ProductCharacteristic productCharacteristic : characteristics) {

            OrderProductCharacteristicEnum characteristicEnum = OrderProductCharacteristicEnum.getByCharacteristicName(productCharacteristic.getName());
            // No matching characteristic found
            if (characteristicEnum == null) {
                continue;
            }
            Class<?> valueClazz = characteristicEnum.getClazz();
            if (valueClazz == String.class) {
                values.put(characteristicEnum, productCharacteristic.getValue());
            } else if (valueClazz == BigDecimal.class) {
                values.put(characteristicEnum, new BigDecimal(productCharacteristic.getValue()));
            } else if (valueClazz == Date.class) {
                values.put(characteristicEnum, DateUtils.parseDateWithPattern(productCharacteristic.getValue(), paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy")));
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
                if (valueClazz == String.class || valueClazz == BigDecimal.class) {
                    productCharacteristic.setValue(valueInfo.getValue().toString());
                } else if (valueClazz == Date.class) {
                    productCharacteristic.setValue(DateUtils.formatDateWithPattern((Date) valueInfo.getValue(), paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy")));
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
     * @param provider Provider
     * @return
     */
    private void extractAndMakeAvailableInGUICustomFields(List<ProductCharacteristic> characteristics, BusinessCFEntity cfEntity, Provider provider) {

        Map<CustomFieldTemplate, Object> cfValues = new HashMap<>();

        if (characteristics == null || characteristics.isEmpty()) {
            return;
        }

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cfEntity, provider);

        for (ProductCharacteristic characteristic : characteristics) {
            if (characteristic.getName() != null && cfts.containsKey(characteristic.getName())) {
                CustomFieldTemplate cft = cfts.get(characteristic.getName());
                cfValues.put(cft, CustomFieldValue.parseValueFromString(cft, characteristic.getValue()));
            }
        }
        customFieldDataEntryBean.setCustomFieldValues(cfValues, cfEntity);
    }

    /**
     * Convert custom fields to product characteristics. Only non-versioned custom fields are supported.
     * 
     * @param cfEntity Custom field entity values will be applied to
     * @return
     * @throws BusinessException
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
            UserHierarchyLevel userGroup = userHierarchyLevelService.refreshOrRetrieve(entity.getRoutedToUserGroup());
            editable = userGroup.isUserBelongsHereOrHigher(getCurrentUser());
        }

        return editable;
    }
}