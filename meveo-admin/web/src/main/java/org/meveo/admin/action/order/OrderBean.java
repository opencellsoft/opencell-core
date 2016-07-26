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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.billing.OrderApi;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.order.OrderItemService;
import org.meveo.service.order.OrderService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;

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

    private OrderItem selectedOrderItem;

    private TreeNode offersTree;

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

        // log.error("AKK setting selected order item {}", selectedOrderItem == null);
        this.selectedOrderItem = orderItemService.refreshOrRetrieve(selectedOrderItem);

        try {
            this.selectedOrderItem.setOrderItemDto(org.tmf.dsmapi.catalog.resource.order.OrderItem.deserializeOrderItem(selectedOrderItem.getSource()));
        } catch (BusinessException e) {
            log.error("Failed to deserialize order item DTO from a source");
        }

        offersTree = getSelectedOrderItemOffersAsTree(this.entity.getStatus() == OrderStatusEnum.IN_CREATION);
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

    public void newOrderItem() {
        selectedOrderItem = new OrderItem();
    }

    public void saveOrderItem() {
        selectedOrderItem = null;
    }

    public void sendToProcess() throws BusinessException {
        entity.setStatus(OrderStatusEnum.ACKNOWLEDGED);
        saveOrUpdate(false);
    }

    private TreeNode getSelectedOrderItemOffersAsTree(boolean selectable) {

        org.tmf.dsmapi.catalog.resource.order.OrderItem orderItemDto = (org.tmf.dsmapi.catalog.resource.order.OrderItem) this.selectedOrderItem.getOrderItemDto();

        TreeNode root = new DefaultTreeNode(new OfferItemInfo("Offer details", "Offer details", null), null);
        root.setExpanded(true);

        ProductOffering mainOffering = this.selectedOrderItem.getProductOfferings().get(0);

        TreeNode mainOfferingNode = null;
        if (selectable) {
            mainOfferingNode = new CheckboxTreeNode(mainOffering.getClass().getSimpleName(), new OfferItemInfo(mainOffering.getCode(), mainOffering.getDescription(), orderItemDto
                .getProduct().getProductCharacteristic()), root);
        } else {
            mainOfferingNode = new DefaultTreeNode(mainOffering.getClass().getSimpleName(), new OfferItemInfo(mainOffering.getCode(), mainOffering.getDescription(), orderItemDto
                .getProduct().getProductCharacteristic()), root);
        }
        mainOfferingNode.setExpanded(true);

        // For offer templates list services subscribed
        if (mainOffering instanceof OfferTemplate) {

            List<Product>[] productsAndServices = orderApi.getProductsAndServices(orderItemDto, this.selectedOrderItem);

            // Add services to the tree
            if (!productsAndServices[1].isEmpty()) {
                TreeNode servicesNode = new DefaultTreeNode("ServiceList", "Service", mainOfferingNode);
                servicesNode.setExpanded(true);

                for (Product serviceProduct : productsAndServices[1]) {

                    String serviceCode = (String) orderApi.getProductCharacteristic(serviceProduct, OrderApi.CHARACTERISTIC_SERVICE_CODE, String.class, null);
                    ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceCode, this.selectedOrderItem.getProvider());
                    if (selectable) {
                        new CheckboxTreeNode(ServiceTemplate.class.getSimpleName(), new OfferItemInfo(serviceCode, serviceTemplate.getDescription(),
                            serviceProduct.getProductCharacteristic()), servicesNode);
                    } else {
                        new DefaultTreeNode(ServiceTemplate.class.getSimpleName(), new OfferItemInfo(serviceCode, serviceTemplate.getDescription(),
                            serviceProduct.getProductCharacteristic()), servicesNode);
                    }
                }
            }

            // Add products to the tree
            if (this.selectedOrderItem.getProductOfferings().size() > 1) {
                TreeNode productsNode = new DefaultTreeNode("ProductList", "Product", mainOfferingNode);
                productsNode.setExpanded(true);

                int index = 0;
                for (ProductOffering offering : this.selectedOrderItem.getProductOfferings().subList(1, this.selectedOrderItem.getProductOfferings().size())) {

                    if (selectable) {
                        new CheckboxTreeNode(ProductTemplate.class.getSimpleName(), new OfferItemInfo(offering.getCode(), offering.getDescription(), productsAndServices[0].get(
                            index).getProductCharacteristic()), productsNode);
                    } else {
                        new DefaultTreeNode(ProductTemplate.class.getSimpleName(), new OfferItemInfo(offering.getCode(), offering.getDescription(), productsAndServices[0].get(
                            index).getProductCharacteristic()), productsNode);
                    }
                    index++;

                }
            }
        }
        return root;
    }
}