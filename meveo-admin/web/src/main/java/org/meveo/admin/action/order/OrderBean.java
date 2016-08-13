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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.billing.OrderApi;
import org.meveo.api.order.OrderProductCharacteristicEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
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
public class OrderBean extends BaseBean<Order> {

    private static final long serialVersionUID = 7399464661886086329L;

    /**
     * Injected @{link Order} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OrderService orderService;

    @Inject
    private OrderItemService orderItemService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private OrderApi orderApi;

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

        log.error("AKK editing {}", orderItemToEdit.getItemId());
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

        if (this.selectedOrderItem.getOrderItemDto() != null) {
            offersTree = getOrderedItemsAsTree(this.entity.getStatus() == OrderStatusEnum.IN_CREATION);
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
                orderItemDto.getProduct().setProductCharacteristic(mapToProductCharacteristics(((OfferItemInfo) offerNode.getData()).getCharacteristics()));

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
                            productCharacteristics.add(mapToProductCharacteristics(offerItemInfo.getCharacteristics()));

                        } else if (offerItemInfo.getTemplate() instanceof ServiceTemplate) {
                            serviceTemplates.add((ServiceTemplate) offerItemInfo.getTemplate());
                            serviceCharacteristics.add(mapToProductCharacteristics(offerItemInfo.getCharacteristics()));
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

                ProductCharacteristic characteristic = new ProductCharacteristic();
                characteristic.setName(OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY.getCharacteristicName());
                characteristic.setValue("5");

                orderItemDto.getProduct().getProductCharacteristic().add(characteristic);

            }
            selectedOrderItem.setOrderItemDto(orderItemDto);
            selectedOrderItem.setSource(org.tmf.dsmapi.catalog.resource.order.OrderItem.serializeOrderItem(orderItemDto));
            log.error("AKK orderitem source is {}", selectedOrderItem.getSource());
            if (selectedOrderItem.getUserAccount() == null && selectedOrderItem.getSubscription() != null) {
                selectedOrderItem.setUserAccount(selectedOrderItem.getSubscription().getUserAccount());
            }

            if (entity.getOrderItems() == null) {
                entity.setOrderItems(new ArrayList<OrderItem>());
            }
            if (!entity.getOrderItems().contains(selectedOrderItem)) {
                selectedOrderItem.setOrder(getEntity());
                entity.getOrderItems().add(selectedOrderItem);
            }

            selectedOrderItem = null;
            offerConfigurations = null;

            messages.info(new BundleKey("messages", "order.orderItemSaved"));

        } catch (Exception e) {
            log.error("Failed to save order item ", e);
            messages.error(new BundleKey("messages", "order.failedToSaveOrderItem"), e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void sendToProcess() throws BusinessException {
        entity.setStatus(OrderStatusEnum.ACKNOWLEDGED);
        saveOrUpdate(false);
    }

    /**
     * Construct a tree of what can/was be ordered for an offer and their properties
     * 
     * @param showAvailable Should checkboxes be shown for tree item selection
     * @return A tree
     */
    private TreeNode getOrderedItemsAsTree(boolean showAvailable) {

        log.error("AKK get getOrderedItemsAsTree");

        offerConfigurations = new ArrayList<>();

        org.tmf.dsmapi.catalog.resource.order.OrderItem orderItemDto = (org.tmf.dsmapi.catalog.resource.order.OrderItem) this.selectedOrderItem.getOrderItemDto();

        TreeNode root = new DefaultTreeNode("Offer details", null);
        root.setExpanded(true);

        ProductOffering mainOffering = this.selectedOrderItem.getProductOfferings().get(0);

        Map<String, Object> mainOfferCharacteristics = new HashMap<>();
        if (orderItemDto != null && orderItemDto.getProduct() != null) {
            mainOfferCharacteristics = productCharacteristicsToMap(orderItemDto.getProduct().getProductCharacteristic());
        }

        OfferItemInfo offerItemInfo = new OfferItemInfo(mainOffering, mainOfferCharacteristics, true, true);
        TreeNode mainOfferingNode = new DefaultTreeNode(mainOffering.getClass().getSimpleName(), offerItemInfo, root);
        mainOfferingNode.setExpanded(true);
        offerConfigurations.add(offerItemInfo);

        // For offer templates list services and products subscribed
        if (mainOffering instanceof OfferTemplate) {

            List<Product>[] productsAndServices = orderApi.getProductsAndServices(orderItemDto, this.selectedOrderItem);

            // Show services - all or only the ones ordered
            if (showAvailable || !productsAndServices[1].isEmpty()) {
                TreeNode servicesNode = new DefaultTreeNode("ServiceList", "Service", mainOfferingNode);
                servicesNode.setExpanded(true);

                for (OfferServiceTemplate offerServiceTemplate : ((OfferTemplate) mainOffering).getOfferServiceTemplates()) {

                    // Find a matching ordered service product by comparing product characteristic "serviceCode"
                    Product serviceProductMatched = null;
                    for (Product serviceProduct : productsAndServices[1]) {
                        String serviceCode = (String) orderApi.getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SERVICE_CODE.getCharacteristicName(),
                            String.class, null);
                        if (offerServiceTemplate.getServiceTemplate().getCode().equals(serviceCode)) {
                            serviceProductMatched = serviceProduct;
                            break;
                        }
                    }

                    if (showAvailable || serviceProductMatched != null) {

                        Map<String, Object> serviceCharacteristics = new HashMap<>();
                        if (serviceProductMatched != null) {
                            serviceCharacteristics = productCharacteristicsToMap(serviceProductMatched.getProductCharacteristic());
                        }

                        offerItemInfo = new OfferItemInfo(offerServiceTemplate.getServiceTemplate(), serviceCharacteristics, false, serviceProductMatched != null
                                || offerServiceTemplate.isMandatory());

                        new DefaultTreeNode(ServiceTemplate.class.getSimpleName(), offerItemInfo, servicesNode);
                        if (offerItemInfo.isSelected()) {
                            offerConfigurations.add(offerItemInfo);
                        }
                    }
                }
            }

            // Show products - all or only the ones ordered
            if ((showAvailable || this.selectedOrderItem.getProductOfferings().size() > 1) && !((OfferTemplate) mainOffering).getOfferProductTemplates().isEmpty()) {
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

                    if (showAvailable || productProductMatched != null) {

                        Map<String, Object> productCharacteristics = new HashMap<>();
                        if (productProductMatched != null) {
                            productCharacteristics = productCharacteristicsToMap(productProductMatched.getProductCharacteristic());
                        }

                        offerItemInfo = new OfferItemInfo(offerProductTemplate.getProductTemplate(), productCharacteristics, false, productProductMatched != null
                                || offerProductTemplate.isMandatory());
                        new DefaultTreeNode(ProductTemplate.class.getSimpleName(), offerItemInfo, productsNode);

                        if (offerItemInfo.isSelected()) {
                            offerConfigurations.add(offerItemInfo);
                        }

                    }
                }
            }
        }
        return root;
    }

    /**
     * New product offering is selected - need to reset orderItem values and the offer tree
     * 
     * @param event
     */
    public void setMainProductOffering(SelectEvent event) {

        if (selectedOrderItem.getProductOfferings().isEmpty() || !selectedOrderItem.getProductOfferings().get(0).equals(event.getObject())) {
            selectedOrderItem.resetMainOffering((ProductOffering) event.getObject());

            offersTree = getOrderedItemsAsTree(this.entity.getStatus() == OrderStatusEnum.IN_CREATION);
        }
    }

    private Map<String, Object> productCharacteristicsToMap(List<ProductCharacteristic> characteristics) {
        Map<String, Object> values = new HashMap<>();

        for (ProductCharacteristic productCharacteristic : characteristics) {

            OrderProductCharacteristicEnum characteristicEnum = OrderProductCharacteristicEnum.getByCharacteristicName(productCharacteristic.getName());
            // No matching characteristic found
            if (characteristicEnum == null) {
                continue;
            }
            Class valueClazz = characteristicEnum.getClazz();
            if (valueClazz == String.class) {
                values.put(productCharacteristic.getName(), productCharacteristic.getValue());
            } else if (valueClazz == Integer.class) {
                values.put(productCharacteristic.getName(), Integer.parseInt(productCharacteristic.getValue()));
            } else if (valueClazz == Date.class) {
                values.put(productCharacteristic.getName(),
                    DateUtils.parseDateWithPattern(productCharacteristic.getValue(), paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy")));
            }
        }
        return values;
    }

    private List<ProductCharacteristic> mapToProductCharacteristics(Map<String, Object> values) {

        List<ProductCharacteristic> characteristics = new ArrayList<>();

        for (Entry<String, Object> valueInfo : values.entrySet()) {
            if (valueInfo.getValue() != null) {
                ProductCharacteristic productCharacteristic = new ProductCharacteristic();
                productCharacteristic.setName(valueInfo.getKey());
                characteristics.add(productCharacteristic);

                Class valueClazz = OrderProductCharacteristicEnum.getByCharacteristicName(productCharacteristic.getName()).getClazz();
                if (valueClazz == String.class || valueClazz == Integer.class) {
                    productCharacteristic.setValue(valueInfo.getValue().toString());
                } else if (valueClazz == Date.class) {
                    productCharacteristic.setValue(DateUtils.formatDateWithPattern((Date) valueInfo.getValue(), paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy")));
                }
            }
        }

        return characteristics;
    }

    public void onTreeNodeSelection() {

        log.error("AKK onTreeNodeSelection");
        offerConfigurations = new ArrayList<>();

        if (selectedOrderItem.getMainOffering() instanceof OfferTemplate) {

            // Add offer configuration
            TreeNode offerNode = offersTree.getChildren().get(0);
            offerConfigurations.add((OfferItemInfo) offerNode.getData());

            for (TreeNode groupingNode : offerNode.getChildren()) { // service or product grouping node
                for (TreeNode serviceOrProduct : groupingNode.getChildren()) {

                    if (serviceOrProduct.getData() instanceof OfferItemInfo && ((OfferItemInfo) serviceOrProduct.getData()).isSelected()) {
                        log.error("AKK add serviceAndProductConfigurations {} {}", ((OfferItemInfo) serviceOrProduct.getData()).getTemplate().getCode(),
                            ((OfferItemInfo) serviceOrProduct.getData()).isMain());
                        offerConfigurations.add((OfferItemInfo) serviceOrProduct.getData());
                    }
                }
            }
        }

        log.error("AKK onTreeNodeSelection {}", offerConfigurations.size());
    }

    public List<OfferItemInfo> getOfferConfigurations() {
        return offerConfigurations;
    }
}