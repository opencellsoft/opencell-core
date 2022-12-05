/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.wf;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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
	protected String getDefaultSort() {
		return "actionDate";
	}

	/**
	 * This method is called from some businessEntityDetail's page.
	 * 
	 * @param entity Entity object
	 * @return List of Workflow history
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
