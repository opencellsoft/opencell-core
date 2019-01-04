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
package org.meveo.admin.action.generic.wf;

import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.generic.wf.WorkflowInstanceHistoryService;
import org.meveo.service.generic.wf.WorkflowInstanceService;

/**
 * Standard backing bean for {@link WorkflowInstanceHistory} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class WorkflowInstanceHistoryBean extends BaseBean<WorkflowInstanceHistory> {

    private static final long serialVersionUID = 1L;

    /**
     * 
     * Injected @{link WorkflowInstanceHistory} service. Extends {@link PersistenceService}.
     */
    @Inject
    private WorkflowInstanceHistoryService workflowInstanceHistoryService;

    @Inject
    private WorkflowInstanceService workflowInstanceService;

    private GenericWorkflow selectedGenericWF = null;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public WorkflowInstanceHistoryBean() {
        super(WorkflowInstanceHistory.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<WorkflowInstanceHistory> getPersistenceService() {
        return workflowInstanceHistoryService;
    }

    @Override
    protected String getListViewName() {
        return "workflowInstanceHistories";
    }

    @Override
    protected String getDefaultSort() {
        return "actionDate";
    }

    /**
     * This method is called from some businessEntityDetail's page.
     * 
     * @param entity Entity object
     * @return List of Workflow instance
     */

    public List<WorkflowInstance> getWorkflowInstances(BusinessEntity entity) {
        return workflowInstanceService.findByEntityInstanceCode(entity.getCode());
    }

    /**
     * This method is called from some businessEntityDetail's page.
     * 
     * @param entity Entity object
     * @return List of Workflow instance history
     */

    public List<WorkflowInstanceHistory> getWorkflowHistories(BusinessEntity entity) {
        return workflowInstanceHistoryService.findByEntityInstanceCode(entity.getCode());
    }

    public List<WorkflowInstanceHistory> getWorkflowHistories(GenericWorkflow genericWorkflow) {
        return workflowInstanceHistoryService.findByGenericWorkflow(genericWorkflow);
    }

    public GenericWorkflow getSelectedGenericWF() {
        return selectedGenericWF;
    }

    public void setSelectedGenericWF(GenericWorkflow selectedGenericWF) {
        this.selectedGenericWF = selectedGenericWF;
    }

    public void selectGenericWF(GenericWorkflow genericWF) {
        this.selectedGenericWF = genericWF;
    }
}
