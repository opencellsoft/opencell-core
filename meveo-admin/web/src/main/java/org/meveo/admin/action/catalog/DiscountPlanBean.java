package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.StatelessBaseBean;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class DiscountPlanBean extends StatelessBaseBean<DiscountPlan> {

	private static final long serialVersionUID = -2345373648137067066L;

	@Inject
	private DiscountPlanService discountPlanService;

	public DiscountPlanBean() {
		super(DiscountPlan.class);
	}

	@Override
	protected IPersistenceService<DiscountPlan> getPersistenceService() {
		return discountPlanService;
	}

}
