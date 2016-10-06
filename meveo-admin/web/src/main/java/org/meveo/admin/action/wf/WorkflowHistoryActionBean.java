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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.wf.WorkflowHistoryAction;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.wf.WorkflowHistoryActionService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link WorkflowHistoryAction} (extends {@link BaseBean} that provides
 * almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF
 * components.
 */
@Named
@ViewScoped
public class WorkflowHistoryActionBean extends BaseBean<WorkflowHistoryAction> {
	private static final long serialVersionUID = 1L;
	/**
	 * Injected @{link WorkflowHistoryAction} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private WorkflowHistoryActionService workflowHistoryActionService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public WorkflowHistoryActionBean() {
		super(WorkflowHistoryAction.class);		
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public WorkflowHistoryAction initEntity() {		
		WorkflowHistoryAction workflowHistoryAction = super.initEntity();
		return workflowHistoryAction;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<WorkflowHistoryAction> getPersistenceService() {
		return workflowHistoryActionService;
	}

	
	@Override
	protected String getListViewName() {
		return "workflowHistoryActions";
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
		return "action";
	}
}
