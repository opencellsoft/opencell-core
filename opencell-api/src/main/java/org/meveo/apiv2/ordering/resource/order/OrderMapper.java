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

package org.meveo.apiv2.ordering.resource.order;

import com.google.common.annotations.VisibleForTesting;
import org.meveo.apiv2.generic.NotYetImplementedResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.ordering.resource.orderItem.ImmutableOrderItem;
import org.meveo.apiv2.ordering.resource.orderitem.OrderItemMapper;
import org.meveo.model.billing.BillingCycle;

import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.WirePaymentMethod;
import org.meveo.model.shared.DateUtils;

import java.util.stream.Collectors;

public class OrderMapper extends ResourceMapper<Order, org.meveo.model.order.Order> {
    @Override
    @VisibleForTesting
    public  Order toResource(org.meveo.model.order.Order entity) {
        return ImmutableOrder.builder()
                .code(entity.getCode())
                .id(entity.getId())
                .description(entity.getDescription())
                .priority(Long.valueOf(entity.getPriority()))
                .category(entity.getCategory())
                .status(entity.getStatus())
                .statusMessage(entity.getStatusMessage())
                .orderDate(DateUtils.formatDateWithPattern(entity.getOrderDate(), DateUtils.DATE_PATTERN))
                .expectedCompletionDate(DateUtils.formatDateWithPattern(entity.getExpectedCompletionDate(), DateUtils.DATE_PATTERN))
                .requestedCompletionDate(DateUtils.formatDateWithPattern(entity.getRequestedCompletionDate(), DateUtils.DATE_PATTERN))
                .requestedProcessingStartDate(DateUtils.formatDateWithPattern(entity.getStartDate(), DateUtils.DATE_PATTERN))
                .billingCycle(buildImmutableResource(NotYetImplementedResource.class, entity.getBillingCycle()))
                .deliveryInstructions(entity.getDeliveryInstructions())
                .paymentMethod(entity.getPaymentMethod() == null? null : ImmutablePaymentMethod.builder()
                        .id(entity.getPaymentMethod().getId())
                        .type(entity.getPaymentMethod().getPaymentType())
                        .build()
                ).orderItems((entity.getOrderItems() == null) ?
                        null :
                        entity.getOrderItems().stream()
                                .map(orderItem -> ImmutableOrderItem.builder().
                                        id(orderItem.getId())
                                        .itemId(orderItem.getItemId())
                                        .status(orderItem.getStatus())
                                        .build())
                                .collect(Collectors.toList())
                ).build();
    }

    @Override
    @VisibleForTesting
    public org.meveo.model.order.Order toEntity(Order resource) {
        OrderItemMapper orderItemMapper = new OrderItemMapper();
        org.meveo.model.order.Order order = new org.meveo.model.order.Order();
        order.setCode(resource.getCode());
        order.setId(resource.getId());
        order.setDescription(resource.getDescription());
        if(resource.getPriority() != null){
            order.setPriority(resource.getPriority().intValue());
        }
        order.setCategory(resource.getCategory());
        order.setStatus(resource.getStatus());
        order.setStatusMessage(resource.getStatusMessage());
        order.setOrderDate(DateUtils.parseDateWithPattern(resource.getOrderDate(), DateUtils.DATE_PATTERN));
        order.setExpectedCompletionDate(DateUtils.parseDateWithPattern(resource.getExpectedCompletionDate(), DateUtils.DATE_PATTERN));
        order.setRequestedCompletionDate(DateUtils.parseDateWithPattern(resource.getRequestedCompletionDate(), DateUtils.DATE_PATTERN));
        order.setStartDate(DateUtils.parseDateWithPattern(resource.getRequestedProcessingStartDate(), DateUtils.DATE_PATTERN));
        order.setDeliveryInstructions(resource.getDeliveryInstructions());

        if(resource.getBillingCycle() != null && resource.getBillingCycle().getId()!= null){
            BillingCycle billingCycle = new BillingCycle();
            billingCycle.setId(resource.getBillingCycle().getId());
            order.setBillingCycle(billingCycle);
        }

        if(resource.getPaymentMethod() != null && resource.getPaymentMethod().getId()!= null){
            order.setPaymentMethod(toPaymentMethodEntity(resource.getPaymentMethod()));
        }

        if(resource.getOrderItems() != null){
            order.setOrderItems(resource.getOrderItems().stream()
                    .map(orderItemResource -> orderItemMapper.toEntity(orderItemResource))
                    .collect(Collectors.toList()));
        }

        return order;
    }

    private PaymentMethod toPaymentMethodEntity(org.meveo.apiv2.ordering.resource.order.PaymentMethod paymentMethodResource) {
        PaymentMethod paymentMethod = null;
        switch (paymentMethodResource.getType()){
        case CARD:
            paymentMethod = new CardPaymentMethod();
            break;
        case CHECK:
            paymentMethod = new CheckPaymentMethod();
            break;
        case DIRECTDEBIT:
            paymentMethod = new DDPaymentMethod();
            break;
        case WIRETRANSFER:
            paymentMethod = new WirePaymentMethod();
            break;
        default:
            return paymentMethod;
        }
        paymentMethod.setId(paymentMethodResource.getId());
        return paymentMethod;
    }
}
