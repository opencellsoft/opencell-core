package org.meveo.service.cpq.order;

import java.util.Calendar;

import javax.ejb.Stateless;

import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author Tarik FA.
 * @version 11.0
 * @dateCreation 31-12-2020
 *
 */
@Stateless
public class CommercialOrderService extends PersistenceService<CommercialOrder>{

	
	public CommercialOrder duplicate(CommercialOrder entity) {
		final CommercialOrder duplicate = new CommercialOrder(entity);
		detach(entity);
		duplicate.setStatus(CommercialOrderEnum.DRAFT.toString());
		duplicate.setStatusDate(Calendar.getInstance().getTime());
		create(duplicate);
		return duplicate;
	}
}
