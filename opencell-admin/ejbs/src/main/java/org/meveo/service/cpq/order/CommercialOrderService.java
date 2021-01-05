package org.meveo.service.cpq.order;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.ServiceSingleton;

/**
 * @author Tarik FA.
 * @version 11.0
 * @dateCreation 31/12/2020
 *
 */
@Stateless
public class CommercialOrderService extends PersistenceService<CommercialOrder>{


    @Inject
    private ServiceSingleton serviceSingleton;
    
	public CommercialOrder duplicate(CommercialOrder entity) {
		final CommercialOrder duplicate = new CommercialOrder(entity);
		detach(entity);
		duplicate.setStatus(CommercialOrderEnum.DRAFT.toString());
		duplicate.setStatusDate(Calendar.getInstance().getTime());
		create(duplicate);
		return duplicate;
	}
	
	public CommercialOrder validateOrder(CommercialOrder commercialOrder) {
		commercialOrder = serviceSingleton.assignCommercialOrderNumber(commercialOrder);
		
		commercialOrder.setStatus(CommercialOrderEnum.VALIDATED.toString());
		commercialOrder.setStatusDate(Calendar.getInstance().getTime());
		
        update(commercialOrder);
		return commercialOrder;
	}
}
