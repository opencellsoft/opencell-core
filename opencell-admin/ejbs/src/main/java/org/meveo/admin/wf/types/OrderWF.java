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

package org.meveo.admin.wf.types;

import java.util.ArrayList;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.order.OrderStatusEnum;

@WorkflowTypeClass
public class OrderWF extends WorkflowType<Order> {

    public OrderWF() {
        super();
    }

    public OrderWF(Order e) {
        super(e);
    }

    @Override
    public List<String> getStatusList() {
        List<String> values = new ArrayList<String>();
        for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
            values.add(orderStatusEnum.name());
        }
        return values;
    }

    @Override
    public void changeStatus(String newStatus) throws BusinessException {
        entity.setStatus(OrderStatusEnum.valueOf(newStatus));
        for (OrderItem orderItem : entity.getOrderItems()) {
            orderItem.setStatus(entity.getStatus());
        }
    }

    @Override
    public String getActualStatus() {
        return entity.getStatus().name();
    }

}