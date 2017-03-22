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