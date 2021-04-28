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

package org.meveo.apiv2.ordering.resource.mappers;

import org.junit.Test;
import org.meveo.apiv2.ordering.resource.orderitem.OrderItemMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.shared.DateUtils;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class OrderItemMapperTest implements ResourceMapperTest {
    @Test
    @Override
    public void mapDtoToEntityAndEntityToDtoTest() {
        OrderItemMapper orderItemMapper = new OrderItemMapper();
        OrderItem orderItem = (OrderItem) generateEntity();

        org.meveo.apiv2.ordering.resource.orderItem.OrderItem orderItemResource = orderItemMapper.toResource(orderItem);
        OrderItem orderItemFromJson = orderItemMapper.toEntity(orderItemResource);

        assertSame(orderItemFromJson.getId(), orderItem.getId());
        assertSame(orderItemFromJson.getItemId(), orderItem.getItemId());
        assertSame(orderItemFromJson.getStatus(), orderItem.getStatus());
        assertSame(orderItemFromJson.getAction(), orderItem.getAction());

        assertSame(orderItemFromJson.getUserAccount().getId(), orderItem.getUserAccount().getId());

        assertSame(orderItemFromJson.getOrder().getId(), orderItem.getOrder().getId());

        assertEquals(orderItemFromJson.getSubscription().getId(), orderItem.getSubscription().getId());
        assertEquals(orderItemFromJson.getSubscription().getCode(), orderItem.getSubscription().getCode());
        assertEquals(orderItemFromJson.getSubscription().getSubscriptionDate(), orderItem.getSubscription().getSubscriptionDate());
        assertEquals(orderItemFromJson.getSubscription().getEndAgreementDate(), orderItem.getSubscription().getEndAgreementDate());
        assertEquals(orderItemFromJson.getSubscription().getOffer().getId(), orderItem.getSubscription().getOffer().getId());
        assertEquals(orderItemFromJson.getSubscription().getUserAccount().getId(), orderItem.getSubscription().getUserAccount().getId());
        assertEquals(orderItemFromJson.getSubscription().getSeller().getId(), orderItem.getSubscription().getSeller().getId());
        assertEquals(orderItemFromJson.getSubscription().getServiceInstances().get(0).getServiceTemplate().getId(), orderItem.getSubscription().getServiceInstances().get(0).getServiceTemplate().getId());

        ProductInstance productInstanceFromJson = orderItemFromJson.getProductInstances().get(0);
        ProductInstance productInstance = orderItem.getProductInstances().get(0);
        assertEquals(productInstanceFromJson.getQuantity(), productInstance.getQuantity());
        assertEquals(productInstanceFromJson.getCode(), productInstance.getCode());
        assertEquals(productInstanceFromJson.getSeller().getId(), productInstance.getSeller().getId());
        assertEquals(productInstanceFromJson.getProductTemplate().getId(), productInstance.getProductTemplate().getId());
    }

    @Override
    public BaseEntity generateEntity() {
        OrderItem orderItem=new OrderItem();
        orderItem.setId(123L);
        orderItem.setItemId("item-id-123");
        orderItem.setStatus(OrderStatusEnum.valueByApiState("Acknowledged"));

        orderItem.setAction(OrderItemActionEnum.valueOf("ADD"));

        Order order = new Order();
        order.setId(234L);
        orderItem.setOrder(order);

        orderItem.setSubscription(generateSubscription());

        UserAccount userAccount = new UserAccount();
        userAccount.setId(345L);
        orderItem.setUserAccount(userAccount);

        orderItem.setProductInstances(Collections.singletonList(generateProductInstance()));
        return orderItem;
    }

    private ProductInstance generateProductInstance() {
        org.meveo.model.billing.ProductInstance productInstance = new org.meveo.model.billing.ProductInstance();
        productInstance.setQuantity(new BigDecimal(1));
        productInstance.setCode("CODE");
        Seller seller = new Seller();
        seller.setId(678L);
        productInstance.setSeller(seller);
        ProductTemplate productTemplate = new ProductTemplate();
        productTemplate.setId(789L);
        productInstance.setProductTemplate(productTemplate);
        return productInstance;
    }

    private Subscription generateSubscription() {
        Subscription subscription = new Subscription();

        subscription.setId(123L);
        subscription.setCode("CODE");

        subscription.setSubscriptionDate(DateUtils.parseDateWithPattern("2019-01-01", DateUtils.DATE_PATTERN));
        subscription.setEndAgreementDate(DateUtils.parseDateWithPattern("2019-01-01", DateUtils.DATE_PATTERN));

        OfferTemplate offer = new OfferTemplate();
        offer.setId(234L);
        subscription.setOffer(offer);

        UserAccount userAccount = new UserAccount();
        userAccount.setId(345L);
        subscription.setUserAccount(userAccount);

        Seller seller = new Seller();
        seller.setId(456L);
        subscription.setSeller(seller);

        ServiceInstance serviceInstance = new ServiceInstance();
        ServiceTemplate serviceTemplate = new ServiceTemplate();
        serviceTemplate.setId(567L);
        serviceInstance.setServiceTemplate(serviceTemplate);

        subscription.setServiceInstances(Collections.singletonList(serviceInstance));
        return subscription;
    }
}
