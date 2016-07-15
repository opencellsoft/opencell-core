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

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
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
import org.meveo.util.PersistenceUtils;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link Workflow} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class DunningPlanBean extends BaseBean<Workflow> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link DunningPlan} service. Extends {@link PersistenceService}.
     */
    @Inject
    private WorkflowService dunningPlanService;

    @Inject
    private WFTransitionService dunningPlanTransitionService;

    @Inject
    private WFActionService actionPlanItemService;

    // @Produces
    // @Named
    private transient WFTransition dunningPlanTransition = new WFTransition();

    // @Produces
    // @Named
    private transient WFAction actionPlanItem = new WFAction();

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public DunningPlanBean() {
        super(Workflow.class);
    }

    @Override
    public Workflow initEntity() {
        super.initEntity();
     //   PersistenceUtils.initializeAndUnproxy(entity.getActions());
        return entity;
    }

    public WFTransition getDunningPlanTransition() {
        return dunningPlanTransition;
    }

    public void setDunningPlanTransition(WFTransition dunningPlanTransition) {
        this.dunningPlanTransition = dunningPlanTransition;
    }

    public WFAction getActionPlanItem() {
        return actionPlanItem;
    }

    public void setActionPlanItem(WFAction actionPlanItem) {
        this.actionPlanItem = actionPlanItem;
    }

    public void newDunningPlanTransitionInstance() {
        this.dunningPlanTransition = new WFTransition();
    }

    public void newActionPlanItemInstance() {
        this.actionPlanItem = new WFAction();
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return "/pages/payments/dunning/dunningPlanDetail?dunningPlanId=" + entity.getId() + "&faces-redirect=true&includeViewParams=true";
    }

    public void saveDunningPlanTransition() {

        try {
            if (dunningPlanTransition.getId() != null) {
                dunningPlanTransitionService.update(dunningPlanTransition, getCurrentUser());
                messages.info(new BundleKey("messages", "update.successful"));
            } else {

                for (WFTransition transition : entity.getTransitions()) {

//                    if ((transition.getDunningLevelFrom().equals(dunningPlanTransition.getDunningLevelFrom()))
//                            && (transition.getDunningLevelTo().equals(dunningPlanTransition.getDunningLevelTo()))) {
//                        throw new BusinessEntityException();
//                    }
                }
               // dunningPlanTransition.setDunningPlan(entity);
                dunningPlanTransitionService.create(dunningPlanTransition, getCurrentUser());
               // entity.getTransitions().add(dunningPlanTransition);
                messages.info(new BundleKey("messages", "save.successful"));
            }
        } catch (BusinessEntityException e) {
            messages.error(new BundleKey("messages", "dunningPlanTransition.uniqueField"));

        } catch (Exception e) {
            log.error("failed to save dunning plan transition", e);

            messages.error(new BundleKey("messages", "dunningPlanTransition.uniqueField"));
        }

        dunningPlanTransition = new WFTransition();
    }

    public void saveActionPlanItem() throws BusinessException {

        if (actionPlanItem.getId() != null) {
            actionPlanItemService.update(actionPlanItem, getCurrentUser());
            messages.info(new BundleKey("messages", "update.successful"));
        } else {
         //   actionPlanItem.setDunningPlan(entity);
            actionPlanItemService.create(actionPlanItem, getCurrentUser());
          //  entity.getActions().add(actionPlanItem);
            messages.info(new BundleKey("messages", "save.successful"));

        }
        actionPlanItem = new WFAction();
    }

    public void deleteDunningPlanTransition(WFTransition dunningPlanTransition) {
        dunningPlanTransitionService.remove(dunningPlanTransition);
        entity.getTransitions().remove(dunningPlanTransition);
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void deleteActionPlanItem(WFAction actionPlanItem) {
        actionPlanItemService.remove(actionPlanItem);
       // entity.getActions().remove(actionPlanItem);
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void editDunningPlanTransition(WFTransition dunningPlanTransition) {
        this.dunningPlanTransition = dunningPlanTransition;
    }

    public void editActionPlanItem(WFAction actionPlanItem) {
        this.actionPlanItem = actionPlanItem;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Workflow> getPersistenceService() {
        return dunningPlanService;
    }

    @Produces
    public Workflow getDunningPlan() {
        return entity;
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider", "transitions");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("provider");
    }

}