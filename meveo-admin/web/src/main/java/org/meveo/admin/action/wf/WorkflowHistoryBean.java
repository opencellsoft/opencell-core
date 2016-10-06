/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.wf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.wf.WorkflowHistory;
import org.meveo.model.wf.WorkflowHistoryAction;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.wf.WorkflowHistoryService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link WorkflowHistory} (extends {@link BaseBean} that provides
 * almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF
 * components.
 */
@Named
@ViewScoped
public class WorkflowHistoryBean extends BaseBean<WorkflowHistory> {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private WorkflowHistoryActionListBean workflowHistoryActionListBean;
	
	/**
	 * 
	 * Injected @{link WorkflowHistory} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private WorkflowHistoryService workflowHistoryService;
	


	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public WorkflowHistoryBean() {
		super(WorkflowHistory.class);		
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public WorkflowHistory initEntity() {		
		WorkflowHistory workflowHistory = super.initEntity();
		return workflowHistory;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<WorkflowHistory> getPersistenceService() {
		return workflowHistoryService;
	}

	
	@Override
	protected String getListViewName() {
		return "workflowHistories";
	}

	/**
	 * Fetch customer field so no LazyInitialize exception is thrown when we
	 * access it from account edit view.
	 * 
	 * @see org.manaty.beans.base.BaseBean#getFormFieldsToFetch()
	 */
	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected String getDefaultSort() {
		return "actionDate";
	}

	public LazyDataModel<WorkflowHistoryAction> getActionsAndReports(WorkflowHistory workflowHistory) {
		
		LazyDataModel<WorkflowHistoryAction> result = null;
		HashMap<String, Object> filters = new HashMap<String, Object>();
		log.debug("entity:"+entity);
		log.debug("workflowHistory:"+workflowHistory);
		if (workflowHistory != null && workflowHistory.getActionsAndReports() != null) {			
			filters.put("workflowHistory", workflowHistory);			
			result = workflowHistoryActionListBean.getLazyDataModel(filters, true);
		}
		return result;
	}
    
}
