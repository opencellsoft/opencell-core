package org.meveo.admin.action.billing;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.DiscountPlanInstanceService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
@Named
@ViewScoped
public class DiscountPlanInstanceBean extends CustomFieldBean<DiscountPlanInstance> {

	private static final long serialVersionUID = -4454680184449485082L;

	@Inject
	private DiscountPlanInstanceService discountPlanInstanceService;

	public DiscountPlanInstanceBean() {
		super(DiscountPlanInstance.class);
	}

	@Override
	protected IPersistenceService<DiscountPlanInstance> getPersistenceService() {
		return discountPlanInstanceService;
	}

}
