package org.meveo.services.job;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.FilterCustomFieldSearchBean;
import org.primefaces.component.datatable.DataTable;

@Named
@ConversationScoped
public class TimerEntityListBean extends TimerEntityBean {

    /**
     * 
     */
    private static final long serialVersionUID = 291083155570451308L;
    
    @Inject
	private FilterCustomFieldSearchBean filterCustomFieldSearchBean;
	
	@Override
	public DataTable search() {
		filterCustomFieldSearchBean.buildFilterParameters(filters);
		return super.search();
	}

}
