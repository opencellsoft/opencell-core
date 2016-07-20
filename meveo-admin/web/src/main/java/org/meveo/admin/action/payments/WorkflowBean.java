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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.wf.WFTransitionService;
import org.meveo.service.wf.WorkflowService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link Workflow} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class WorkflowBean extends BaseBean<Workflow> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link Workflow} service. Extends {@link PersistenceService}.
     */
    @Inject
    private WorkflowService workflowService;

    @Inject
    private WFTransitionService wFTransitionService;


    // @Produces
    // @Named
    private transient WFTransition wfTransition = new WFTransition();

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public WorkflowBean() {
        super(Workflow.class);
    }

    @Override
    public Workflow initEntity() {
        super.initEntity();
     //   PersistenceUtils.initializeAndUnproxy(entity.getActions());
        return entity;
    }

    public WFTransition getWfTransition() {
        return wfTransition;
    }

    public void setWfTransition(WFTransition wfTransition) {
        this.wfTransition = wfTransition;
    }

    public void newWfTransitionInstance() {
        this.wfTransition = new WFTransition();
    }


    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return "/pages/admin/workflow/workflowDetail?workflowId=" + entity.getId() + "&faces-redirect=true&includeViewParams=true";
    }

    public void saveWfTransition() {

        try {
            if (wfTransition.getId() != null) {
                WFTransition wfTrs = wFTransitionService.findById(wfTransition.getId());
                wfTrs.setFromStatus(wfTransition.getFromStatus());
                wfTrs.setToStatus(wfTransition.getToStatus());
                wfTrs.setConditionEl(wfTransition.getConditionEl());
                wFTransitionService.update(wfTrs, getCurrentUser());
                messages.info(new BundleKey("messages", "update.successful"));
            } else {

                for (WFTransition transition : entity.getTransitions()) {

                    if ((transition.getFromStatus().equals(wfTransition.getFromStatus()))
                            && (transition.getToStatus().equals(wfTransition.getToStatus()))) {
                        throw new BusinessEntityException();
                    }
                }
                wfTransition.setWorkflow(entity);
                wFTransitionService.create(wfTransition, getCurrentUser());
                entity.getTransitions().add(wfTransition);
                messages.info(new BundleKey("messages", "save.successful"));
            }
        } catch (BusinessEntityException e) {
            messages.error(new BundleKey("messages", "dunningPlanTransition.uniqueField"));

        } catch (Exception e) {
            log.error("failed to save dunning plan transition", e);

            messages.error(new BundleKey("messages", "dunningPlanTransition.uniqueField"));
        }

        wfTransition = new WFTransition();
    }

    public void deleteWfTransition(WFTransition dunningPlanTransition) {
        WFTransition transition = wFTransitionService.findById(dunningPlanTransition.getId()); 
        wFTransitionService.remove(transition);
        entity.getTransitions().remove(dunningPlanTransition);
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void editWfTransition(WFTransition dunningPlanTransition) {
        this.wfTransition = dunningPlanTransition;
    }
    
    /**
     * Autocomplete method for class filter field - search entity type classes with @ObservableEntity annotation
     * 
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<String> autocompleteClassNames(String query) {

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
            return null;
        }

        String queryLc = query.toLowerCase();
        List<String> classNames = new ArrayList<String>();
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(WorkflowTypeClass.class)) {
                classNames.add(clazz.getName());
            }
        }

        Collections.sort(classNames);
        return classNames;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Workflow> getPersistenceService() {
        return workflowService;
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