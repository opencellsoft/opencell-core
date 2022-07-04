package org.meveo.service.order;

import static java.util.Optional.ofNullable;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.meveo.commons.utils.ListUtils;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.ordering.OpenOrderStatusEnum;
import org.meveo.service.base.BusinessService;

@Stateless
public class OpenOrderService extends BusinessService<OpenOrder> {

	/**
	 * Find Open orders compatible with InvoiceLine in parameter.
	 * 
	 * @param il : InvoiceLine
	 * @return
	 */
	public OpenOrder findOpenOrderCompatibleForIL(InvoiceLine il) {
		
    	TypedQuery<OpenOrder> query = getEntityManager().createNamedQuery("OpenOrder.getOpenOrderCompatibleForIL", OpenOrder.class);

    	query.setParameter("billingAccountId", il.getBillingAccount().getId());
    	query.setParameter("ilAmountWithTax", il.getAmountWithTax());
    	query.setParameter("status", OpenOrderStatusEnum.CANCELED);
    	query.setParameter("ilValueDate", il.getValueDate());
    	query.setParameter("productId", ofNullable(il.getProductVersion()).map(ilp -> ilp.getProduct().getId()).orElse(null));
    	query.setParameter("articleId", ofNullable(il.getAccountingArticle()).map(ila -> ila.getId()).orElse(null));
    	
    	List<OpenOrder> result = query.getResultList();
    	
    	if(!ListUtils.isEmtyCollection(result)) {
			return result.get(0);
    	}
    	
    	return null;
    }
}
