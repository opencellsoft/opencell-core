package org.meveocrm.admin.action.reporting;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.services.dwh.ChartService;


@Named
@ConversationScoped
public class ChartBean extends
BaseBean<Chart> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2585685452044860823L;
	
	@Inject
	ChartService chartService;
	
	public ChartBean(){
		super(Chart.class);
	}
	
	protected IPersistenceService<Chart> getPersistenceService() {
		return chartService;
	}
	

	protected String getDefaultViewName() {
		return "charts";
	}
	

	@Override
	protected String getListViewName() {
		return "charts";
	}


}
