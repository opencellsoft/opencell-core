package org.meveocrm.admin.action.reporting;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.services.dwh.MeasurableQuantityService;

@Named
@ConversationScoped
public class MeasurableQuantityBean extends BaseBean<MeasurableQuantity> {

	private static final long serialVersionUID = -1644247310944456827L;

	@Inject
	MeasurableQuantityService measurableQuantityService;

	public MeasurableQuantityBean() {
		super(MeasurableQuantity.class);
	}

	@Override
	protected IPersistenceService<MeasurableQuantity> getPersistenceService() {
		return measurableQuantityService;
	}

	protected String getDefaultViewName() {
		return "measurableQuantities";
	}

	@Override
	protected String getListViewName() {
		return "measurableQuantities";
	}

}
