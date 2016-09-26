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
package org.meveo.admin.action.payments;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.wf.WFActionService;
import org.meveo.service.wf.WFTransitionService;
import org.meveo.service.wf.WorkflowService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link WFTransition} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
@ViewBean
public class WfTransitionBean extends BaseBean<WFTransition> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link DunningPlanTransition} service. Extends {@link PersistenceService}.
     */
    @Inject
    private WFTransitionService wfTransitionService;

    @Inject
    private WorkflowService wfService;
    
    @Inject
    private WFActionService wfActionService;

    private Workflow workflow;
    
    private transient WFAction wfAction = new WFAction();

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public WfTransitionBean() {
        super(WFTransition.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public WFTransition initEntity() {
        if (workflow != null && workflow.getId() == null) {
            try {
                wfService.create(workflow, getCurrentUser());
            } catch (BusinessException e) {
                messages.info(new BundleKey("messages", "message.exception.business"));
            }
        }
        entity = super.initEntity();
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
        super.saveOrUpdate(killConversation);

        return "/pages/admin/workflow/wfTransitionDetail?wfTransitionId=" + entity.getId() + "&faces-redirect=true&includeViewParams=true";

    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<WFTransition> getPersistenceService() {
        return wfTransitionService;
    }

    @Override
    public void delete(Long id) {
        try {
            entity = getPersistenceService().findById(id);
            log.info(String.format("Deleting entity %s with id = %s", entity.getClass().getName(), id));
            entity = null;
            messages.info(new BundleKey("messages", "delete.successful"));
        } catch (Throwable t) {
            if (t.getCause() instanceof EntityExistsException) {
                log.info("delete was unsuccessful because entity is used in the system", t);
                messages.error(new BundleKey("messages", "error.delete.entityUsed"));
            } else {
                log.info("unexpected exception when deleting!", t);
                messages.error(new BundleKey("messages", "error.delete.unexpected"));
            }
        }
    }
    
    public void saveWfAction() throws BusinessException {
        boolean isPriorityUnique = checkUnicityOfPriority();
        if(isPriorityUnique) {
            if (wfAction.getId() != null) {
            	WFAction action = wfActionService.findById(wfAction.getId());
            	action.setActionEl(wfAction.getActionEl());
            	action.setConditionEl(wfAction.getConditionEl());
            	action.setPriority(wfAction.getPriority());
                wfActionService.update(action, getCurrentUser());
                messages.info(new BundleKey("messages", "update.successful"));
            } else {
                wfAction.setWfTransition(entity);
                wfActionService.create(wfAction, getCurrentUser());
                entity.getWfActions().add(wfAction);
                messages.info(new BundleKey("messages", "save.successful"));

            }
            wfAction = new WFAction();
        } else {
            messages.error(new BundleKey("messages", "crmAccount.wfAction.uniquePriority"), new Object[]{wfAction.getPriority()});
        }
        
    }
    
    private boolean checkUnicityOfPriority() {
        for(WFAction action : entity.getWfActions()) {
            if(wfAction.getPriority() == action.getPriority() && 
                    !action.getId().equals(wfAction.getId())) {
                return false;
            }
        }
        return true;
    }

    public void deleteWfAction(WFAction wfAction) {
        WFAction action = wfActionService.findById(wfAction.getId()); 
        wfActionService.remove(action);
        entity.getWfActions().remove(wfAction);
        messages.info(new BundleKey("messages", "delete.successful"));
    }
    
    public void newWfActionInstance() {
        this.wfAction = new WFAction();
    }
    
    public void editWfAction(WFAction wfAction) {
        this.wfAction = wfAction;
    }
    
    public WFAction getWfAction() {
        return wfAction;
    }
    
    public void setWfAction(WFAction wfAction) {
        this.wfAction = wfAction;
    }

}
