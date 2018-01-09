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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.action.admin.custom.GroupedDecisionRule;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.admin.wf.types.OrderWF;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFDecisionRule;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.wf.WFActionService;
import org.meveo.service.wf.WFDecisionRuleService;
import org.meveo.service.wf.WFTransitionService;
import org.meveo.service.wf.WorkflowService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Standard backing bean for {@link WFTransition} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class WfTransitionBean extends BaseBean<WFTransition> {

    private static final long serialVersionUID = 1L;

    private static final String EL = "#{mv:getBean('OrderService').routeToUserGroup(entity,'%s')}";

    private static final String WF_ORDER = "Customer_Care_Assignation_of_Orders";
    private static final String CATCH_ALL = "Catch all";
    public static final int CATCH_ALL_PRIORITY = 100;

    /**
     * Injected @{link DunningPlanTransition} service. Extends {@link PersistenceService}.
     */
    @Inject
    private WFTransitionService wfTransitionService;

    @Inject
    private WFDecisionRuleService wfDecisionRuleService;

    @Inject
    private WorkflowService wfService;

    @Inject
    private WFActionService wfActionService;

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    @Inject
    @ViewBean
    protected WorkflowBean workflowBean;

    private transient WFAction wfAction = new WFAction();

    private Workflow workflowOrder;

    private WFTransition wfTransition = new WFTransition();

    private TreeNode userGroupRootNode;

    private TreeNode userGroupSelectedNode;

    private List<String> wfDecisionRulesName;

    private List<List<WFDecisionRule>> wfDecisionRulesByName = new ArrayList<>();

    private List<GroupedDecisionRule> selectedRules = new ArrayList<>();

    private List<WFAction> wfActions = new ArrayList<>();

    private List<WFTransition> operationList = new ArrayList<>();

    private WFTransition catchAll;

    private boolean disabledOrderWF = false;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public WfTransitionBean() {
        super(WFTransition.class);
    }

    public WorkflowBean getWorkflowBean() {
        return workflowBean;
    }

    public void setWorkflowBean(WorkflowBean workflowBean) {
        this.workflowBean = workflowBean;
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * @return workflow transition 
     */
    public WFTransition initEntity() {
        entity = super.initEntity();
        if (entity.getId() != null) {
            editWfTransition(entity);
        }
        return entity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        List<WFDecisionRule> wfDecisionRules = new ArrayList<>();
        boolean isUniqueNameValue = workflowBean.checkAndPopulateDecisionRules(selectedRules, wfDecisionRules);
        if (!isUniqueNameValue) {
            return null;
        }
        for (WFDecisionRule wfTransitionRuleFor : wfDecisionRules) {
            if (wfTransitionRuleFor.getId() == null) {
                wfDecisionRuleService.create(wfTransitionRuleFor);
            }
        }

        entity.setDescription(wfTransition.getDescription());

        if (entity.getId() == null) {
            // Calculate max priority +1
            int priority = 1;
            if (operationList.size() > 0) {
                for (WFTransition wfTransitionInList : operationList) {
                    if (CATCH_ALL_PRIORITY != wfTransitionInList.getPriority() && priority <= wfTransitionInList.getPriority()) {
                        priority = wfTransitionInList.getPriority() + 1;
                    }
                }
            }
            entity.setPriority(priority);
            entity.setFromStatus(OrderStatusEnum.ACKNOWLEDGED.toString());
            entity.setToStatus(OrderStatusEnum.IN_PROGRESS.toString());
        }
        entity.getWfDecisionRules().clear();
        entity.getWfDecisionRules().addAll(wfDecisionRules);
        entity.setWorkflow(workflowOrder);
        super.saveOrUpdate(killConversation);

        WFTransition currentTransition = wfTransitionService.findById(entity.getId(), Arrays.asList("wfActions"), true);
        List<WFAction> actionList = currentTransition.getWfActions();
        if (this.userGroupSelectedNode != null) {
            UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) this.userGroupSelectedNode.getData();
            String actionEL = String.format(EL, userHierarchyLevel.getCode());
            if (CollectionUtils.isNotEmpty(actionList)) {
                for (WFAction wfAction : actionList) {
                    WFAction action = wfActionService.findById(wfAction.getId());
                    action.setActionEl(actionEL);
                    wfActionService.update(action);
                }
            } else {
                WFAction wfAction = new WFAction();
                wfAction.setActionEl(actionEL);
                wfAction.setPriority(1);
                wfAction.setWfTransition(entity);
                wfActionService.create(wfAction);
            }
        } else if (CollectionUtils.isNotEmpty(actionList)) {
            for (WFAction wfAction : actionList) {
                WFAction action = wfActionService.findById(wfAction.getId());
                wfActionService.remove(action);
            }
        }

        return back();
    }

    public Workflow getWorkflowOrder() throws BusinessException {
        if (workflowOrder == null) {
            List<Workflow> list = wfService.findByWFTypeWithoutStatus(OrderWF.class.getName());
            if (CollectionUtils.isNotEmpty(list)) {
                workflowOrder = list.get(0);
                if (workflowOrder.isDisabled()) {
                    disabledOrderWF = true;
                }
            } else {
                workflowOrder = new Workflow();
                workflowOrder.setWfType(OrderWF.class.getName());
                workflowOrder.setCode(WF_ORDER);
                wfService.create(workflowOrder);
                WFTransition catchAllDefault = createCatchAll();
                workflowOrder.getTransitions().add(catchAllDefault);
            }
        }
        if (workflowOrder != null) {
            operationList = wfTransitionService.listWFTransitionByStatusWorkFlow(OrderStatusEnum.ACKNOWLEDGED.toString(), OrderStatusEnum.IN_PROGRESS.toString(), workflowOrder);
            if (CollectionUtils.isNotEmpty(operationList)) {
                Collections.sort(operationList);
                int indexCatchAll = operationList.size() - 1;
                if (operationList.get(indexCatchAll).getPriority() == CATCH_ALL_PRIORITY) {
                    catchAll = operationList.get(indexCatchAll);
                    operationList.remove(indexCatchAll);
                }
            }
            if (catchAll == null) {
                catchAll = createCatchAll();
            }
        }
        return workflowOrder;
    }

    private WFTransition createCatchAll() throws BusinessException {
        WFTransition catchAllDefault = new WFTransition();
        catchAllDefault.setPriority(CATCH_ALL_PRIORITY);
        catchAllDefault.setDescription(CATCH_ALL);
        catchAllDefault.setFromStatus(OrderStatusEnum.ACKNOWLEDGED.toString());
        catchAllDefault.setToStatus(OrderStatusEnum.IN_PROGRESS.toString());
        catchAllDefault.setWorkflow(workflowOrder);
        wfTransitionService.create(catchAllDefault);
        return catchAllDefault;
    }

    public void setWorkflowOrder(Workflow workflowOrder) {
        this.workflowOrder = workflowOrder;
    }

    public List<WFTransition> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<WFTransition> operationList) {
        this.operationList = operationList;
    }

    public WFTransition getCatchAll() {
        return catchAll;
    }

    public void setCatchAll(WFTransition catchAll) {
        this.catchAll = catchAll;
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
            // entity.getDunningPlan().getTransitions().remove(entity);
            getPersistenceService().remove(id);
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

    @ActionMethod
    public void saveWfAction() throws BusinessException {
        boolean isPriorityUnique = checkUnicityOfPriority();
        if (isPriorityUnique) {
            if (wfAction.getId() != null) {
                WFAction action = wfActionService.findById(wfAction.getId());
                action.setActionEl(wfAction.getActionEl());
                action.setConditionEl(wfAction.getConditionEl());
                action.setPriority(wfAction.getPriority());
                wfActionService.update(action);
                messages.info(new BundleKey("messages", "update.successful"));
            } else {
                wfAction.setWfTransition(entity);
                wfActionService.create(wfAction);
                entity.getWfActions().add(wfAction);
                messages.info(new BundleKey("messages", "save.successful"));

            }
            wfAction = new WFAction();
        } else {
            messages.error(new BundleKey("messages", "crmAccount.wfAction.uniquePriority"), new Object[] { wfAction.getPriority() });
        }

    }

    private boolean checkUnicityOfPriority() {
        for (WFAction action : entity.getWfActions()) {
            if (wfAction.getPriority() == action.getPriority() && !action.getId().equals(wfAction.getId())) {
                return false;
            }
        }
        return true;
    }

    @ActionMethod
    public void deleteWfAction(WFAction wfAction) {
        try {
            WFAction action = wfActionService.findById(wfAction.getId());
            wfActionService.remove(action);
            entity.getWfActions().remove(wfAction);
            messages.info(new BundleKey("messages", "delete.successful"));

        } catch (Exception e) {
            log.info("Failed to delete!", e);
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
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

    public WFTransition getWfTransition() {
        return wfTransition;
    }

    public void setWfTransition(WFTransition wfTransition) {
        this.wfTransition = wfTransition;
    }

    public List<String> getWfDecisionRulesName() {
        if (wfDecisionRulesName == null) {
            wfDecisionRulesName = wfDecisionRuleService.getDistinctNameWFTransitionRules();
        }
        return wfDecisionRulesName;
    }

    public void setWfDecisionRulesName(List<String> wfDecisionRulesName) {
        this.wfDecisionRulesName = wfDecisionRulesName;
    }

    public List<List<WFDecisionRule>> getWfDecisionRulesByName() {
        return wfDecisionRulesByName;
    }

    public void setWfDecisionRulesByName(List<List<WFDecisionRule>> wfDecisionRulesByName) {
        this.wfDecisionRulesByName = wfDecisionRulesByName;
    }

    public List<GroupedDecisionRule> getSelectedRules() {
        return selectedRules;
    }

    public void setSelectedRules(List<GroupedDecisionRule> selectedRules) {
        this.selectedRules = selectedRules;
    }

    public List<WFAction> getWfActions() {
        return wfActions;
    }

    public void setWfActions(List<WFAction> wfActions) {
        this.wfActions = wfActions;
    }

    @ActionMethod
    public void editWfTransition(WFTransition wfTransition) {
        this.wfTransition = wfTransition;
        if (wfTransition != null && wfTransition.getWfDecisionRules() != null) {
            wfDecisionRulesByName.clear();
            selectedRules.clear();
            for (WFDecisionRule wfTransitionRule : wfTransition.getWfDecisionRules()) {
                GroupedDecisionRule groupedTransitionRule = new GroupedDecisionRule();
                groupedTransitionRule.setName(wfTransitionRule.getName());
                groupedTransitionRule.setValue(wfTransitionRule);
                List<WFDecisionRule> list = wfDecisionRuleService.getWFDecisionRules(wfTransitionRule.getName());
                Collections.sort(list);
                wfDecisionRulesByName.add(list);
                selectedRules.add(groupedTransitionRule);
            }
        }
    }

    public void addNewRule() {
        selectedRules.add(new GroupedDecisionRule());
    }

    @ActionMethod
    public void deleteWfDecisionRule(int indexRule) {
        if (wfDecisionRulesByName.size() > indexRule && wfDecisionRulesByName.get(indexRule) != null) {
            wfDecisionRulesByName.remove(indexRule);
        }
        selectedRules.remove(indexRule);
    }

    @ActionMethod
    public String duplicateWfTransition(WFTransition wfTransition) {
        try {
            workflowOrder = wfService.refreshOrRetrieve(workflowOrder);
            this.wfTransition = wfTransitionService.duplicate(wfTransition, workflowOrder);

            // Set max priority +1
            int priority = 1;
            if (operationList.size() > 0) {
                for (WFTransition wfTransitionInList : operationList) {
                    if (CATCH_ALL_PRIORITY != wfTransitionInList.getPriority() && priority <= wfTransitionInList.getPriority()) {
                        priority = wfTransitionInList.getPriority() + 1;
                    }
                }
            }
            this.wfTransition.setPriority(priority);
            this.setObjectId(this.wfTransition.getId());
            editWfTransition(this.wfTransition);
            return "mm_workflowDetail";

        } catch (Exception e) {
            log.error("Failed to duplicate WF transition!", e);
            messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
            return null;
        }
    }

    @ActionMethod
    public void deleteWfTransition(WFTransition wfTransition) {
        try {
            wfTransitionService.remove(wfTransition.getId());
            workflowOrder = wfService.refreshOrRetrieve(workflowOrder);
            wfDecisionRulesByName.clear();
            selectedRules.clear();
            wfActions.clear();
            messages.info(new BundleKey("messages", "delete.successful"));

        } catch (Exception e) {
            log.error("Failed to delete!", e);
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
    }

    public TreeNode getUserGroupRootNode() {
        if (userGroupRootNode == null) {
            userGroupRootNode = new DefaultTreeNode("Root", null);
            List<UserHierarchyLevel> roots = userHierarchyLevelService.findRoots();
            if (CollectionUtils.isNotEmpty(roots)) {
                Collections.sort(roots);
                for (UserHierarchyLevel userGroupTree : roots) {
                    createTree(userGroupTree, userGroupRootNode, entity.getWfActions());
                }
            }
        }
        return userGroupRootNode;
    }

    public void setUserGroupRootNode(TreeNode rootNode) {
        this.userGroupRootNode = rootNode;
    }

    public TreeNode getUserGroupSelectedNode() {
        return userGroupSelectedNode;
    }

    public void setUserGroupSelectedNode(TreeNode userGroupSelectedNode) {
        this.userGroupSelectedNode = userGroupSelectedNode;
    }

    // Recursive function to create tree with node checked if selected
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private TreeNode createTree(HierarchyLevel hierarchyLevel, TreeNode rootNode, List<WFAction> wfActions) {
        TreeNode newNode = new DefaultTreeNode(hierarchyLevel, rootNode);
        List<UserHierarchyLevel> subTree = new ArrayList<UserHierarchyLevel>(hierarchyLevel.getChildLevels());
        newNode.setExpanded(true);
        if (wfActions != null) {
            for (WFAction wfAction1 : wfActions) {
                if (wfAction1 != null && hierarchyLevel.getCode().equals(wfAction1.getUserGroupCode())) {
                    newNode.setSelected(true);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(subTree)) {
            Collections.sort(subTree);
            for (HierarchyLevel userGroupTree : subTree) {
                createTree(userGroupTree, newNode, wfActions);
            }
        }
        return newNode;
    }

    public void changedRuleName(int indexRule) {
        List<WFDecisionRule> list = wfDecisionRuleService.getWFDecisionRules(selectedRules.get(indexRule).getName());
        Collections.sort(list);
        if (wfDecisionRulesByName.size() > indexRule && wfDecisionRulesByName.get(indexRule) != null) {
            wfDecisionRulesByName.remove(indexRule);
            wfDecisionRulesByName.add(indexRule, list);
        } else {
            wfDecisionRulesByName.add(indexRule, list);
        }
    }

    @ActionMethod
    public void moveUpOperation(WFTransition selectedWfTransition) throws BusinessException {
        int index = operationList.indexOf(selectedWfTransition);
        if (index > 0) {
            WFTransition upWfTransition = operationList.get(index);
            WFTransition needUpdate = wfTransitionService.findById(upWfTransition.getId(), true);
            needUpdate.setPriority(index);
            wfTransitionService.update(needUpdate);
            WFTransition downWfTransition = operationList.get(index - 1);
            needUpdate = wfTransitionService.findById(downWfTransition.getId(), true);
            needUpdate.setPriority(index + 1);
            wfTransitionService.update(needUpdate);
            Collections.swap(operationList, index, index - 1);
            messages.info(new BundleKey("messages", "update.successful"));
        }
    }

    @ActionMethod
    public void moveDownOperation(WFTransition selectedWfTransition) throws BusinessException {
        int index = operationList.indexOf(selectedWfTransition);
        if (index < operationList.size() - 1) {
            WFTransition upWfTransition = operationList.get(index);
            WFTransition needUpdate = wfTransitionService.findById(upWfTransition.getId(), true);
            needUpdate.setPriority(index + 2);
            wfTransitionService.update(needUpdate);
            WFTransition downWfTransition = operationList.get(index + 1);
            needUpdate = wfTransitionService.findById(downWfTransition.getId(), true);
            needUpdate.setPriority(index + 1);
            wfTransitionService.update(needUpdate);
            Collections.swap(operationList, index, index + 1);
            messages.info(new BundleKey("messages", "update.successful"));
        }
    }

    public boolean isDisabledOrderWF() {
        return disabledOrderWF;
    }

    public void setDisabledOrderWF(boolean disabledOrderWF) {
        this.disabledOrderWF = disabledOrderWF;
    }

    @Override
    protected String getListViewName() {
        return "mm_workflows";
    }

    @Override
    public String getEditViewName() {
        return "mm_workflowDetail";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
     */
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("wfDecisionRules", "wfActions");
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
     */
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("wfDecisionRules", "wfActions");
    }
}
