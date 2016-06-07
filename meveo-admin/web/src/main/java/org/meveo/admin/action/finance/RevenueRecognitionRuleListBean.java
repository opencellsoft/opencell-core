package org.meveo.admin.action.finance;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.FilterCustomFieldSearchBean;
import org.primefaces.component.datatable.DataTable;

@Named
@ConversationScoped
public class RevenueRecognitionRuleListBean extends RevenueRecognitionRuleBean {

	private static final long serialVersionUID = 2821493035026941662L;
	
	@Inject
	private FilterCustomFieldSearchBean filterCustomFieldSearchBean;
	
	@Override
	public DataTable search() {
		filterCustomFieldSearchBean.buildFilterParameters(filters);
		return super.search();
	}
	
}
