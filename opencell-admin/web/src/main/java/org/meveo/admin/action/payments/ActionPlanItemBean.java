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
package org.meveo.admin.action.payments;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.wf.WFActionService;
import org.meveo.service.wf.WorkflowService;

/**
 * Standard backing bean for {@link WFAction} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class ActionPlanItemBean extends BaseBean<WFAction> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link ActionPlanItem} service. Extends {@link PersistenceService}.
     */
    @Inject
    private WFActionService actionPlanItemService;

    @Inject
    private WorkflowService dunningPlanService;

    // TODO comment @Inject because remove @Produre Workflow in workflowBean and Abdelhadi consider to reuse this bean on his purpose or clean later
    // @Inject
    private Workflow dunningPlan;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public ActionPlanItemBean() {
        super(WFAction.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * @return workflow action 
     */
    public WFAction initEntity() {
        if (dunningPlan != null && dunningPlan.getId() == null) {
            try {
                dunningPlanService.create(dunningPlan);
            } catch (BusinessException e) {
                messages.info(new BundleKey("messages", "message.exception.business"));
            }
        }
        super.initEntity();
        // entity.setDunningPlan(dunningPlan);
        return entity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        // dunningPlan.getActions().add(entity);
        // super.saveOrUpdate(killConversation);
        return "/pages/payments/dunning/dunningPlanDetail.xhtml?objectId=" + dunningPlan.getId() + "&edit=true";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<WFAction> getPersistenceService() {
        return actionPlanItemService;
    }
}