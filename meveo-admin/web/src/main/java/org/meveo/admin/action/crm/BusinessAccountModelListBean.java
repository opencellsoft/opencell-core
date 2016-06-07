package org.meveo.admin.action.crm;

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
public class BusinessAccountModelListBean extends BusinessAccountModelBean {

	private static final long serialVersionUID = 3574716066981246932L;
	
	@Inject
	private FilterCustomFieldSearchBean filterCustomFieldSearchBean;
	
	@Override
	public DataTable search() {
		filterCustomFieldSearchBean.buildFilterParameters(filters);
		return super.search();
	}
	
	@Override
	public String getEditViewName() {
		return "businessAccountModelDetail";
	}

}
