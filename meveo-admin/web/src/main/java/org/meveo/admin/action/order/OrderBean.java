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

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.order.OrderService;
import org.omnifaces.cdi.ViewScoped;

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

    private OrderItem selectedOrderItem;

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
}