package org.meveo.admin.action.filter;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.FilterCustomFieldSearchBean;
import org.primefaces.component.datatable.DataTable;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class FilterListBean extends FilterBean {

    private static final long serialVersionUID = -1079492194327180982L;
    
    @Inject
	private FilterCustomFieldSearchBean filterCustomFieldSearchBean;
	
	@Override
	public DataTable search() {
		filterCustomFieldSearchBean.buildFilterParameters(filters);
		return super.search();
	}
}