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
package org.meveo.admin.action.generic.wf;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.BusinessEntity;
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

    private WorkflowInstance selectedWFInstance = null;

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
    protected String getDefaultSort() {
        return "actionDate";
    }

    /**
     * This method is called from some businessEntityDetail's page.
     * 
     * @param entity Entity object
     * @param clazz of Entity
     * @return List of Workflow instance
     */

    public List<WorkflowInstance> getWorkflowInstances(BusinessEntity entity, Class<?> clazz) {
        if(entity != null && clazz != null) {
            return workflowInstanceService.findByEntityIdAndClazz(entity.getId(), clazz);
        }
        return new ArrayList<WorkflowInstance>();
    }

    /**
     * This method is called from some businessEntityDetail's page.
     * 
     * @return List of Workflow instance history
     */

    public List<WorkflowInstanceHistory> getWorkflowHistories() {
        if (selectedWFInstance != null) {
            return workflowInstanceHistoryService.findByWorkflowInstance(selectedWFInstance);
        }

        return null;
    }

    /**
     * @param selectedWFInstance the selectedWFInstance to set
     */
    public void selectWFInstance(WorkflowInstance selectedWFInstance) {
        this.selectedWFInstance = selectedWFInstance;
    }

    /**
     * @return the selectedWFInstance
     */
    public WorkflowInstance getSelectedWFInstance() {
        return selectedWFInstance;
    }

    /**
     * @param selectedWFInstance the selectedWFInstance to set
     */
    public void setSelectedWFInstance(WorkflowInstance selectedWFInstance) {
        this.selectedWFInstance = selectedWFInstance;
    }
}
