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

import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.wf.Workflow;
import org.meveo.model.wf.WorkflowHistory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.wf.WorkflowHistoryService;
import org.meveo.service.wf.WorkflowService;

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

	/**
	 * 
	 * Injected @{link WorkflowHistory} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private WorkflowHistoryService workflowHistoryService;

	@Inject
	private WorkflowService workflowService;
	
	private List<WorkflowHistory> wfHistories = new ArrayList<WorkflowHistory>();
	
	private BusinessEntity oldConsultedEntity = null;


	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public WorkflowHistoryBean() {
		super(WorkflowHistory.class);		
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

	@Override
	protected String getDefaultSort() {
		return "actionDate";
	}

	/**
	 * This method is called from some businessEntityDetail's page
	 * 
	 * @param entity
	 * @return
	 */
	
	public List<WorkflowHistory> getWorkflowHistory(BusinessEntity entity){		
		if(oldConsultedEntity == null || !entity.equals(oldConsultedEntity) ){	
			oldConsultedEntity = entity;
			List<Workflow> workflows = workflowService.findByEntity(entity.getClass());		
			wfHistories = workflowHistoryService.findByEntityCode(entity.getCode(), workflows);
		}
		return wfHistories;
	}

}
