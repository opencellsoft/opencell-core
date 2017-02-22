package org.meveo.admin.action.finance;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.finance.RevenueSchedule;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.finance.RevenueScheduleService;

@Named
@ViewScoped
public class RevenueScheduleBean extends BaseBean<RevenueSchedule> {

	private static final long serialVersionUID = 1L;

	@Inject
	RevenueScheduleService revenueScheduleService;

	public RevenueScheduleBean() {
		super(RevenueSchedule.class);
	}

	@Override
	protected IPersistenceService<RevenueSchedule> getPersistenceService() {
		// TODO Auto-generated method stub
		return revenueScheduleService;
	}

	@Override
	protected String getDefaultSort() {
		return "chargeInstance.code";
	}
}