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

package org.meveo.apiv2.ordering.services;

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmf.dsmapi.catalog.resource.order.ProductOrderItem;

public class OrderItemApiService implements ApiService<OrderItem> {
    private List<String> fetchFields;

    @Inject
    private org.meveo.service.order.OrderItemService orderItemService;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private ProductInstanceService productInstanceService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private BillingAccountService billingAccountService;
    
    private static final Logger log = LoggerFactory.getLogger(OrderItemApiService.class);

    @PostConstruct
    public void initService(){
        fetchFields = Arrays.asList("productInstances", "orderItemProductOfferings", "subscription");
    }

    @Override
    public List<OrderItem> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, fetchFields, null, null);
        List<OrderItem> list = orderItemService.list(paginationConfiguration);

        return list.stream()
                .map(this::refreshOrRetrieveOrderItemProductInstanceProduct)
                .map(this::refreshOrRetrieveOrderItemSubscriptionServiceInstances)
                .collect(Collectors.toList());
    }

    private OrderItem refreshOrRetrieveOrderItemSubscriptionServiceInstances(OrderItem orderItem) {
        if(orderItem != null && orderItem.getSubscription() != null){
            orderItem.setSubscription(subscriptionService.findById(orderItem.getSubscription().getId(), Collections.singletonList("serviceInstances")));
        }
        return orderItem;
    }

    private OrderItem refreshOrRetrieveOrderItemProductInstanceProduct(OrderItem orderItem) {
        if(orderItem != null){
            orderItem.setProductInstances(
                    orderItem.getProductInstances().stream()
                            .filter(productInstance -> productInstance.getProductTemplate() != null)
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
        return ofNullable(refreshOrRetrieveOrderItemProductInstanceProduct(orderItemService.findById(id, fetchFields)));
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
    
	/**
	 * @param order
	 * @param orderItem
	 */
	public void populateOrderItemFields(OrderItem orderItem) throws BusinessException {
		populateOrderItemFields(orderItem, null);
	}

    void populateOrderItemFields(OrderItem orderItem, Order order) throws BusinessException {
    	if(order!=null) {
    		orderItem.setOrder(order);
    	} else if(orderItem.getOrder() != null && orderItem.getOrder() != null) {
            Order orderById = (Order) orderItemService.tryToFindByCodeOrId(orderItem.getOrder());
            orderItem.setOrder(orderById);
        }

        if(orderItem.getUserAccount() != null) {
            orderItem.setUserAccount((UserAccount) orderItemService.tryToFindByCodeOrId(orderItem.getUserAccount()));
        }

        if(orderItem.getProductInstances() != null) {
            orderItem.setProductInstances(orderItem.getProductInstances().stream()
                    .map(this::createOrFetchProductInstance)
                    .collect(Collectors.toList()));
        }

        fetchOrCreateSubscription(orderItem);

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

     private Subscription fetchOrCreateSubscription(OrderItem orderItem) throws BusinessException {
        Subscription subscription = orderItem.getSubscription();
        if(subscription != null){
            if(subscription.getId() != null || subscription.getCode()!=null){
                subscription = (Subscription) orderItemService.tryToFindByCodeOrId(orderItem.getSubscription());
            }else {
                if(subscription.getSeller() != null){
                    subscription.setSeller((Seller) orderItemService.tryToFindByCodeOrId(subscription.getSeller()));
                }
                UserAccount userAccount = subscription.getUserAccount();
                if(userAccount != null ){
                    subscription.setUserAccount((UserAccount) orderItemService.tryToFindByCodeOrId(subscription.getUserAccount()));
                    subscription.getUserAccount().setBillingAccount(billingAccountService.retrieveIfNotManaged(subscription.getUserAccount().getBillingAccount()));
                }
                if(subscription.getOffer() != null){
                    subscription.setOffer((OfferTemplate) orderItemService.tryToFindByCodeOrId(subscription.getOffer(), Collections.singletonList("offerServiceTemplates")));
                }
                List<ServiceInstance> serviceInstances = subscription.getServiceInstances();

                subscription.setServiceInstances(null);
                subscriptionService.create(subscription);
                subscription.setServiceInstances(serviceInstances);

                serviceInstances = new ArrayList<>(subscription.getServiceInstances());

                if(!serviceInstances.isEmpty()){
                    Iterator<ServiceInstance> iterator = Collections.unmodifiableList(serviceInstances).iterator();
                    List<ServiceInstance> managedServiceInstances = new ArrayList<>();
                    while (iterator.hasNext()) {
                        ServiceInstance serviceInstance = iterator.next();
                        if (serviceInstance.getId() != null) {
                            managedServiceInstances.add(serviceInstanceService.findById(serviceInstance.getId()));
                        } else {
                            // this looks ugly I know, I used this to prevent MultipleBagFetchException, either this
                            // or I need to change all List fields collection type to set
                            ServiceTemplate serviceTemplateByIdWithServiceRecurringCharges = serviceTemplateService
                                    .findById(serviceInstance.getServiceTemplate().getId(), Collections.singletonList("serviceRecurringCharges"));

                            List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges = serviceTemplateService
                                    .findById(serviceInstance.getServiceTemplate().getId(), Collections.singletonList("serviceSubscriptionCharges")).getServiceSubscriptionCharges();
                            List<ServiceChargeTemplateTermination> serviceTerminationCharges = serviceTemplateService
                                    .findById(serviceInstance.getServiceTemplate().getId(), Collections.singletonList("serviceTerminationCharges")).getServiceTerminationCharges();
                            List<ServiceChargeTemplateUsage> serviceUsageCharges = serviceTemplateService
                                    .findById(serviceInstance.getServiceTemplate().getId(), Collections.singletonList("serviceUsageCharges")).getServiceUsageCharges();

                            serviceTemplateByIdWithServiceRecurringCharges.setServiceSubscriptionCharges(serviceSubscriptionCharges);
                            serviceTemplateByIdWithServiceRecurringCharges.setServiceTerminationCharges(serviceTerminationCharges);
                            serviceTemplateByIdWithServiceRecurringCharges.setServiceUsageCharges(serviceUsageCharges);
                            ServiceInstance serviceInstance1 = new ServiceInstance();
                            serviceInstance1.setServiceTemplate(serviceTemplateByIdWithServiceRecurringCharges);
                            serviceInstance1.setCode(serviceTemplateByIdWithServiceRecurringCharges.getCode());
                            serviceInstance1.setSubscription(subscription);
                            try {
                                serviceInstanceService.serviceInstanciation(serviceInstance1);
                                managedServiceInstances.add(serviceInstance1);
                            } catch (BusinessException e) {
                                log.error("error = {}", e);
                            }
                        }
                    }
                    subscription.setServiceInstances(managedServiceInstances);
                }
            }
        }
        return subscription;
    }

    private ProductInstance createOrFetchProductInstance(ProductInstance productInstance) {
        if(productInstance.getId()!=null){
            return productInstanceService.findById(productInstance.getId());
        }

        if(productInstance.getSeller() != null){
        	productInstance.setSeller((Seller) orderItemService.tryToFindByCodeOrId(productInstance.getSeller()));
        }
        if(productInstance.getProductTemplate() != null){
        	productInstance.setProductTemplate((ProductTemplate) orderItemService.tryToFindByCodeOrId(productInstance.getProductTemplate()));
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
                oldOrderItem.setCfValues(orderItem.getCfValues());

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
                if(orderItem.getCfValues() != null){
                    oldOrderItem.setCfValues(orderItem.getCfValues());
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

    @Override
    public Optional<OrderItem> findByCode(String code) {
        return ofNullable(orderItemService.findByCode(code, fetchFields));
    }

}
