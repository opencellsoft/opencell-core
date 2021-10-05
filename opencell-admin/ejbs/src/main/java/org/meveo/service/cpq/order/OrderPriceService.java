package org.meveo.service.cpq.order;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderPrice;
import org.meveo.service.base.BusinessService;

/**
 * @author Tarik FAKHOURI
 * @version 11.0
 * @dateCreation 20/01/2021
 *
 */
@Stateless
public class OrderPriceService extends BusinessService<OrderPrice> {

    public List<OrderPrice> findByOrder(CommercialOrder commercialOrder) {
        return getEntityManager().createNamedQuery("OrderPrice.findByOrder", OrderPrice.class)
                .setParameter("commercialOrder", commercialOrder)
                .getResultList();
    }
    
    public List<OrderPrice> findByQuote(CpqQuote quote) {
        return getEntityManager().createNamedQuery("OrderPrice.findByQuote", OrderPrice.class)
                .setParameter("quote", quote)
                .getResultList();
    }
}
