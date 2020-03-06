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

package org.meveo.service.order;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.order.OrderHistory;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 * @version 12 Mar 2018
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class OrderHistoryService extends PersistenceService<OrderHistory> {

    @Inject
    private OrderItemService orderItemService;

    public void create(String orderNumber, Long orderItemId, ServiceInstance serviceInstance, OrderItemActionEnum orderItemAction) throws BusinessException {
        // check if already exists, possible in case of service instantiation and activate
        if (find(orderItemId, serviceInstance, orderItemAction) != null) {
            return;
        }

        if (StringUtils.isBlank(orderNumber) || orderItemId == null) {
            return;
        }

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrderNumber(orderNumber);
        orderHistory.setOrderItem(orderItemService.findById(orderItemId));
        orderHistory.setServiceInstance(serviceInstance);
        orderHistory.setAction(orderItemAction);
        orderHistory.setEventDate(new Date());

        create(orderHistory);
    }

    public OrderHistory find(Long orderItemId, ServiceInstance serviceInstance, OrderItemActionEnum orderItemAction) {
        QueryBuilder qb = new QueryBuilder(OrderHistory.class, "o");
        qb.addCriterion("orderItem.id", "=", orderItemId, true);
        qb.addCriterionEntity("serviceInstance", serviceInstance);
        qb.addCriterionEnum("action", orderItemAction);

        try {
            return (OrderHistory) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
