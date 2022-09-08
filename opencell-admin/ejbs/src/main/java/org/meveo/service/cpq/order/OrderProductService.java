package org.meveo.service.cpq.order;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.service.base.PersistenceService;

/**
 * @author Tarik FAKHOURI
 * @version 11.0
 * @dateCreation 19/01/2021
 *
 */
@Stateless
public class OrderProductService extends PersistenceService<OrderProduct> {

	@SuppressWarnings("unchecked")
	public List<OrderProduct> findOrderProductsByOrder(Long orderId) {
		return getEntityManager().createNamedQuery("OrderProduct.findOrderProductByOrder").setParameter("commercialOrderId", orderId).getResultList();
	}

}
