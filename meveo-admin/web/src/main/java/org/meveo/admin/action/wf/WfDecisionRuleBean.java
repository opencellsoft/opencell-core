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

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.wf.WFDecisionRule;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.wf.WFDecisionRuleService;
import org.meveo.service.wf.WFTransitionService;
import org.meveo.service.wf.WorkflowService;

/**
 * Standard backing bean for {@link org.meveo.model.wf.Workflow} (extends {@link org.meveo.admin.action.BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class WfDecisionRuleBean extends BaseBean<WFDecisionRule> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link Workflow} service. Extends {@link org.meveo.service.base.PersistenceService}.
     */
    @Inject
    private WorkflowService workflowService;

    @Inject
    private WFTransitionService wFTransitionService;

    @Inject
    private WFDecisionRuleService wFDecisionRuleService;


    // @Produces
    // @Named
    private transient WFDecisionRule wfDecisionRule = new WFDecisionRule();

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link org.meveo.admin.action.BaseBean}.
     */
    public WfDecisionRuleBean() {
        super(WFDecisionRule.class);
    }

    @Override
    public WFDecisionRule initEntity() {
        super.initEntity();
     //   PersistenceUtils.initializeAndUnproxy(entity.getActions());
        return entity;
    }

    @Override
    public String getEditViewName() {
        return "wfTransitionRuleDetail";
    }

    /**
     * Generating back link.
     */
    @Override
    protected String getListViewName() {
        return "wfTransitionRules";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<WFDecisionRule> getPersistenceService() {
        return wFDecisionRuleService;
    }
}