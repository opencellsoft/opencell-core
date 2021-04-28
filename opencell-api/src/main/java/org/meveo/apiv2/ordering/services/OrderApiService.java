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

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.payments.impl.PaymentMethodService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderApiService implements ApiService<Order> {

    private List<String> fetchFields;
    @Inject
    private org.meveo.service.order.OrderService orderService;
    @Inject
    private BillingCycleService billingCycleService;
    @Inject
    private PaymentMethodService paymentMethodService;
    @Inject
    private OrderItemApiService orderItemService;

    @PostConstruct
    public void initService(){
        fetchFields = Arrays.asList("paymentMethod", "billingCycle", "orderItems");
    }

    @Override
    public List<Order> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, fetchFields, null, null);
        return orderService.list(paginationConfiguration);
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, fetchFields, null, null);
        return orderService.count(paginationConfiguration);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderService.findById(id, fetchFields));
    }

    @Override
    public Order create(Order order) {
        try {
            if (order.getOrderDate() == null || StringUtils.isBlank(order.getStatus())) {
                throw new BadRequestException("order date and order status fields are mandatory");
            }
            populateOrderFields(order);
            orderService.create(order);
        }catch (Exception e){
            throw new BadRequestException(e);
        }
        return order;
    }

    private void populateOrderFields(Order order){
        if(order.getBillingCycle() != null){
            order.setBillingCycle(billingCycleService.findById(order.getBillingCycle().getId()));
        }

        if(order.getPaymentMethod() != null){
            order.setPaymentMethod(paymentMethodService.findById(order.getPaymentMethod().getId()));
        }

        if(order.getOrderItems() != null){
            order.setOrderItems(order.getOrderItems().stream()
                    .map(orderItem -> {
                        if(orderItem.getId() != null){
                            OrderItem existingOrderItem = orderItemService.findById(orderItem.getId()).get();
                            existingOrderItem.setOrder(order);
                            return existingOrderItem;
                        }
                        orderItem.setOrder(order);
                        try {
                            orderItemService.populateOrderItemFields(orderItem);
                        } catch (BusinessException e) {
                            throw new BadRequestException(e);
                        }
                        return orderItem;

                    })
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public Optional<Order> update(Long id, Order order) {
        Optional<Order> orderOptional = findById(id);
        if(orderOptional.isPresent()){
            if (order.getOrderDate() == null || StringUtils.isBlank(order.getStatus())) {
                throw new BadRequestException("order date and order status fields are mandatory");
            }
            try {
                populateOrderFields(order);
                Order orderToUpdate = orderOptional.get();
                orderToUpdate.setDescription(order.getDescription());
                orderToUpdate.setPriority(order.getPriority());
                orderToUpdate.setCategory(order.getCategory());
                orderToUpdate.setStatus(order.getStatus());
                orderToUpdate.setStatusMessage(order.getStatusMessage());
                orderToUpdate.setOrderDate(order.getOrderDate());
                orderToUpdate.setExpectedCompletionDate(order.getExpectedCompletionDate());
                orderToUpdate.setRequestedCompletionDate(order.getRequestedCompletionDate());
                orderToUpdate.setStartDate(order.getStartDate());
                orderToUpdate.setDeliveryInstructions(order.getDeliveryInstructions());

                orderToUpdate.setBillingCycle(order.getBillingCycle());
                orderToUpdate.setPaymentMethod(order.getPaymentMethod());
                orderToUpdate.setOrderItems(order.getOrderItems().stream()
                        .peek(orderItem -> orderItem.setOrder(orderToUpdate))
                        .collect(Collectors.toList()));
                orderService.update(orderToUpdate);
            } catch (Exception e) {
                throw new BadRequestException(e);
            }
        }
        return orderOptional;
    }

    @Override
    public Optional<Order> patch(Long id, Order order) {
        Optional<Order> orderOptional = findById(id);
        if(orderOptional.isPresent()){
            Order orderToUpdate = orderOptional.get();
            try {
                populateOrderFields(order);
                if(order.getDescription()!=null){
                    orderToUpdate.setDescription(order.getDescription());
                }
                if(order.getPriority()!=null){
                    orderToUpdate.setPriority(order.getPriority());
                }
                if(order.getCategory()!=null){
                    orderToUpdate.setCategory(order.getCategory());
                }
                if(order.getStatus()!=null){
                    orderToUpdate.setStatus(order.getStatus());
                }
                if(order.getStatusMessage()!=null){
                    orderToUpdate.setStatusMessage(order.getStatusMessage());
                }
                if(order.getOrderDate()!=null){
                    orderToUpdate.setOrderDate(order.getOrderDate());
                }
                if(order.getOrderDate()!=null){
                    orderToUpdate.setExpectedCompletionDate(order.getExpectedCompletionDate());
                }
                if(order.getRequestedCompletionDate()!=null){
                    orderToUpdate.setRequestedCompletionDate(order.getRequestedCompletionDate());
                }
                if(order.getStartDate()!=null){
                    orderToUpdate.setStartDate(order.getStartDate());
                }
                if(order.getDeliveryInstructions()!=null){
                    orderToUpdate.setDeliveryInstructions(order.getDeliveryInstructions());
                }
                if(order.getBillingCycle() != null){
                    orderToUpdate.setBillingCycle(order.getBillingCycle());
                }
                if(order.getPaymentMethod() != null){
                    orderToUpdate.setPaymentMethod(order.getPaymentMethod());
                }
                if(order.getOrderItems() != null){
                    orderToUpdate.setOrderItems(order.getOrderItems().stream()
                            .peek(orderItem -> orderItem.setOrder(orderToUpdate))
                            .collect(Collectors.toList()));
                }

                orderService.update(orderToUpdate);
            } catch (Exception e) {
                throw new BadRequestException(e);
            }
        }
        return orderOptional;
    }

    @Override
    public Optional<Order> delete(Long id) {
        Optional<Order> order = findById(id);
        if(order.isPresent()){
            try {
                orderService.remove(id);
            } catch (Exception e) {
                throw new BadRequestException(e);
            }
        }
        return order;
    }
}
