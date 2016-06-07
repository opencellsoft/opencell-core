package org.meveo.admin.action.payments;

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
public class CreditCategoryListBean extends CreditCategoryBean {

	private static final long serialVersionUID = 8109076594426859889L;
	
	@Inject
	private FilterCustomFieldSearchBean filterCustomFieldSearchBean;
	
    @Override
    public DataTable search() {
    	filterCustomFieldSearchBean.buildFilterParameters(filters);
    	return super.search();
    }
	
	@Override
	public String getEditViewName() {
		return "creditCategory";
	}

}
