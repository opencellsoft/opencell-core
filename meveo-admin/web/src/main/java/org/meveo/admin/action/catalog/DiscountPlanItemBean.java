package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.StatelessBaseBean;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class DiscountPlanItemBean extends StatelessBaseBean<DiscountPlanItem> {

	private static final long serialVersionUID = -2345373648137067066L;

	@Inject
	private DiscountPlanItemService discountPlanItemService;

	@Override
	protected IPersistenceService<DiscountPlanItem> getPersistenceService() {
		return discountPlanItemService;
	}

}
