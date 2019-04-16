package org.meveo.apiv2.services;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.order.OrderService;
import org.tmf.dsmapi.catalog.resource.order.ProductOrderItem;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderItemApiService implements ApiService<OrderItem> {
    private List<String> fetchFields;

    @Inject
    private org.meveo.service.order.OrderItemService orderItemService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private ProductInstanceService productInstanceService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OrderService orderService;

    @Inject
    private SellerService sellerService;

    @PostConstruct
    public void initService(){
        fetchFields = Arrays.asList("productInstances", "orderItemProductOfferings");
    }

    @Override
    public List<OrderItem> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, fetchFields, null, null);
        List<OrderItem> list = orderItemService.list(paginationConfiguration);

        return list.stream()
                .map(this::refreshOrRetrieveOrderItemProductInstanceProduct)
                .collect(Collectors.toList());
    }

    private OrderItem refreshOrRetrieveOrderItemProductInstanceProduct(OrderItem orderItem) {
        if(orderItem != null){
            orderItem.setProductInstances(
                    orderItem.getProductInstances().stream()
                            .peek(productInstance -> productInstance.setProductTemplate(productTemplateService.retrieveIfNotManaged(productInstance.getProductTemplate())))
                            .collect(Collectors.toList()));
        }
        return orderItem;
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, fetchFields, null, null);
        return orderItemService.count(paginationConfiguration);
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        return Optional.ofNullable(refreshOrRetrieveOrderItemProductInstanceProduct(orderItemService.findById(id, fetchFields)));
    }

    @Override
    public OrderItem create(OrderItem orderItem) {
        try {
            populateOrderItemFields(orderItem);
            orderItemService.create(orderItem);
        }catch (Exception e){
            throw new BadRequestException(e);
        }
        return orderItem;
    }

    void populateOrderItemFields(OrderItem orderItem) throws BusinessException {
        if(orderItem.getOrder() != null && orderItem.getOrder().getId() != null) {
            Order orderById = orderService.findById(orderItem.getOrder().getId());
            orderItem.setOrder(orderById);
        }

        if(orderItem.getUserAccount() != null) {
            orderItem.setUserAccount(userAccountService.findById(orderItem.getUserAccount().getId()));
        }

        if(orderItem.getProductInstances() != null) {
            orderItem.setProductInstances(orderItem.getProductInstances().stream()
                    .map(this::createOrFetchProductInstance)
                    .collect(Collectors.toList()));
        }

        if(orderItem.getSubscription() != null) {
            Subscription subscription = subscriptionService.findById(orderItem.getSubscription().getId());
            orderItem.setSubscription(subscription);
        }

        ProductOrderItem productOrderItem = new ProductOrderItem();
        productOrderItem.setId(String.valueOf(orderItem.getId()));
        if(orderItem.getAction() != null){
            productOrderItem.setAction(orderItem.getAction().getLabel());
        }
        if(orderItem.getStatus() != null){
            productOrderItem.setState(orderItem.getStatus().getApiState());
        }
        org.tmf.dsmapi.catalog.resource.order.BillingAccount billingAccountTMF = new org.tmf.dsmapi.catalog.resource.order.BillingAccount();
        if(orderItem.getUserAccount() != null){
            billingAccountTMF.setId(String.valueOf(orderItem.getUserAccount().getBillingAccount().getId()));
        }
        productOrderItem.setBillingAccount(Collections.singletonList(billingAccountTMF));

        orderItem.setSource(ProductOrderItem.serializeOrderItem(productOrderItem));
    }

    private ProductInstance createOrFetchProductInstance(ProductInstance productInstance) {
        if(productInstance.getId()!=null){
            return productInstanceService.findById(productInstance.getId());
        }

        if(productInstance.getSeller() !=null){
            productInstance.setSeller(sellerService.findById(productInstance.getSeller().getId()));
        }
        if(productInstance.getProductTemplate() !=null){
            productInstance.setProductTemplate(productTemplateService.findById(productInstance.getProductTemplate().getId()));
        }
        try {
            productInstanceService.create(productInstance);
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
        return productInstance;
    }

    @Override
    public Optional<OrderItem> update(Long id, OrderItem orderItem) {
        Optional<OrderItem> OrderItemOptional = findById(id);
        if(OrderItemOptional.isPresent()){
            try {
                OrderItem oldOrderItem = OrderItemOptional.get();
                populateOrderItemFields(orderItem);
                oldOrderItem.setItemId(orderItem.getItemId());
                oldOrderItem.setStatus(orderItem.getStatus());
                oldOrderItem.setAction(orderItem.getAction());

                oldOrderItem.setUserAccount(orderItem.getUserAccount());
                oldOrderItem.setProductInstances(orderItem.getProductInstances());
                oldOrderItem.setSubscription(orderItem.getSubscription());

                orderItemService.update(oldOrderItem);
                refreshOrRetrieveOrderItemProductInstanceProduct(oldOrderItem);
            } catch (Exception e) {
                throw new BadRequestException(e);
            }
        }
        return OrderItemOptional;
    }

    @Override
    public Optional<OrderItem> patch(Long id, OrderItem orderItem) {
        Optional<OrderItem> OrderItemOptional = findById(id);
        if(OrderItemOptional.isPresent()){
            try {
                OrderItem oldOrderItem = OrderItemOptional.get();
                populateOrderItemFields(orderItem);
                if(orderItem.getItemId() != null){
                    oldOrderItem.setItemId(orderItem.getItemId());
                }
                if(orderItem.getStatus() != null){
                    oldOrderItem.setStatus(orderItem.getStatus());
                }
                if(orderItem.getAction() != null){
                    oldOrderItem.setAction(orderItem.getAction());
                }
                populateOrderItemFields(orderItem);
                if(orderItem.getUserAccount() != null){
                    oldOrderItem.setUserAccount(orderItem.getUserAccount());
                }
                if(orderItem.getProductInstances() != null){
                    oldOrderItem.setProductInstances(orderItem.getProductInstances());
                }
                if(orderItem.getSubscription() != null){
                    oldOrderItem.setSubscription(orderItem.getSubscription());
                }
                orderItemService.update(oldOrderItem);
                refreshOrRetrieveOrderItemProductInstanceProduct(oldOrderItem);
            } catch (Exception e) {
                throw new BadRequestException(e);
            }
        }
        return OrderItemOptional;
    }

    @Override
    public Optional<OrderItem> delete(Long id) {
        Optional<OrderItem> OrderItemOptional = findById(id);
        if(OrderItemOptional.isPresent()){
            try {
                orderItemService.remove(id);
            } catch (Exception e) {
                throw new BadRequestException(e);
            }
        }
        return OrderItemOptional;
    }
}
