package org.meveo.service.order;

import javax.ejb.Stateless;

import org.meveo.model.order.OrderItem;
import org.meveo.service.base.PersistenceService;

/**
 * @author phung 
 *
 */
@Stateless
public class OrderItemService extends PersistenceService<OrderItem> {

}