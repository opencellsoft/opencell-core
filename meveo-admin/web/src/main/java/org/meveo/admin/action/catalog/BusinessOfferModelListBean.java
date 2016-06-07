package org.meveo.admin.action.catalog;

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
public class BusinessOfferModelListBean extends BusinessOfferModelBean {

	private static final long serialVersionUID = 2963552034235893444L;
	
	@Inject
	private FilterCustomFieldSearchBean filterCustomFieldSearchBean;
	
    @Override
    public DataTable search() {
    	filterCustomFieldSearchBean.buildFilterParameters(filters);
    	return super.search();
    }

	@Override
	public String getEditViewName() {
		return "businessOfferModelDetail";
	}

}
