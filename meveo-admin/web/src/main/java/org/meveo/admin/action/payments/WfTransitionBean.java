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

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.action.admin.custom.GroupedTransitionRule;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.WFTransitionRule;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.wf.WFActionService;
import org.meveo.service.wf.WFTransitionService;
import org.meveo.service.wf.WFTransitionRuleService;
import org.meveo.service.wf.WorkflowService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Standard backing bean for {@link WFTransition} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class WfTransitionBean extends BaseBean<WFTransition> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link DunningPlanTransition} service. Extends {@link PersistenceService}.
     */
    @Inject
    private WFTransitionService wfTransitionService;

    @Inject
    private WFTransitionRuleService wFTransitionServiceRule;

    @Inject
    private WorkflowService wfService;
    
    @Inject
    private WFActionService wfActionService;

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    /** Entity to edit. */
    @Inject
    private Workflow workflow;

    @Inject
    @ViewBean
    protected WorkflowBean workflowBean;
    
    private transient WFAction wfAction = new WFAction();

    private Workflow workflowOrder;

    private WFTransition wfTransition = new WFTransition();

    private TreeNode userGroupRootNode;

    private TreeNode[] userGroupSelectedNodes;

    private List<String> wfTransitionRulesName;

    private List<List<WFTransitionRule>> wfTransitionRulesByName = new ArrayList<>();

    private List<GroupedTransitionRule> selectedRules = new ArrayList<>();

    private List<WFAction> wfActions = new ArrayList<>();

    private List<WFTransition> operationList = new ArrayList<>();

    private WFTransition catchAll;

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
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public WFTransition initEntity() {
        if (workflowOrder == null) {
            workflowOrder = wfService.getWorkflowOrder(getCurrentUser().getProvider());
        }

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
        List<WFTransitionRule> wfTransitionRules = new ArrayList<>();
        boolean isUniqueNameValue = workflowBean.checkAndPopulateTransitionRules(selectedRules, wfTransitionRules);
        if (!isUniqueNameValue) {
            return null;
        }

        for (WFTransitionRule wfTransitionRuleFor : wfTransitionRules) {
            if (wfTransitionRuleFor.getId() == null) {
                wFTransitionServiceRule.create(wfTransitionRuleFor, getCurrentUser());
            }
        }

        entity.setDescription(wfTransition.getDescription());

        if (entity.getId() == null) {
            WFTransition lastWfTransition = operationList.get(operationList.size() - 1);
            if (lastWfTransition != null) {
                entity.setPriority(lastWfTransition.getPriority() + 1);
            }
        }
        entity.getWfTransitionRules().clear();
        entity.getWfTransitionRules().addAll(wfTransitionRules);
        entity.setWorkflow(workflowOrder);
        super.saveOrUpdate(killConversation);

        if (this.userGroupSelectedNodes != null) {
            WFTransition currentTransition = wfTransitionService.findById(entity.getId(), Arrays.asList("wfActions"), true);
            List<WFAction> deletedActions = currentTransition.getWfActions();
            int priority = 1;
            if (CollectionUtils.isNotEmpty(deletedActions)) {
                for (WFAction wfAction : deletedActions) {
                    if (wfAction.getPriority() > priority) {
                        priority = wfAction.getPriority();
                    }
                    WFAction action = wfActionService.findById(wfAction.getId());
                    wfActionService.remove(action);
                }
            }

            List<TreeNode> assignedTeams = Arrays.asList(this.userGroupSelectedNodes);
            for (TreeNode treeNode: assignedTeams) {
                UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) treeNode.getData();
                WFAction wfAction = new WFAction();
                wfAction.setActionEl(userHierarchyLevel.getDescriptionOrCode());
                wfAction.setPriority(priority + 1);
                wfAction.setWfTransition(entity);
                wfActionService.create(wfAction, getCurrentUser());
                priority++;
            }
        }

        return back();
    }

    public Workflow getWorkflowOrder() {
        if (workflowOrder == null) {
            workflowOrder = wfService.getWorkflowOrder(getCurrentUser().getProvider());
        }
        if (workflowOrder != null) {
            operationList = workflowOrder.getTransitions();
            Collections.sort(operationList);
            int indexCatchAll = operationList.size() - 1;
            catchAll = operationList.get(indexCatchAll);
            operationList.remove(indexCatchAll);
        }
        return workflowOrder;
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
          //  getPersistenceService().remove(id);
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

    public WFTransition getWfTransition() {
        return wfTransition;
    }

    public void setWfTransition(WFTransition wfTransition) {
        this.wfTransition = wfTransition;
    }

    public List<String> getWfTransitionRulesName() {
        if (wfTransitionRulesName == null) {
            wfTransitionRulesName = wFTransitionServiceRule.getDistinctNameWFTransitionRules(getCurrentProvider());
        }
        return wfTransitionRulesName;
    }

    public void setWfTransitionRulesName(List<String> wfTransitionRulesName) {
        this.wfTransitionRulesName = wfTransitionRulesName;
    }

    public List<List<WFTransitionRule>> getWfTransitionRulesByName() {
        return wfTransitionRulesByName;
    }

    public void setWfTransitionRulesByName(List<List<WFTransitionRule>> wfTransitionRulesByName) {
        this.wfTransitionRulesByName = wfTransitionRulesByName;
    }

    public List<GroupedTransitionRule> getSelectedRules() {
        return selectedRules;
    }

    public void setSelectedRules(List<GroupedTransitionRule> selectedRules) {
        this.selectedRules = selectedRules;
    }

    public List<WFAction> getWfActions() {
        return wfActions;
    }

    public void setWfActions(List<WFAction> wfActions) {
        this.wfActions = wfActions;
    }

    public void editWfTransition(WFTransition wfTransition) {
        this.wfTransition = wfTransition;
        if (wfTransition != null && wfTransition.getWfTransitionRules() != null) {
            wfTransitionRulesByName.clear();
            selectedRules.clear();
            for (WFTransitionRule wfTransitionRule : wfTransition.getWfTransitionRules()) {
                GroupedTransitionRule groupedTransitionRule = new GroupedTransitionRule();
                groupedTransitionRule.setName(wfTransitionRule.getName());
                groupedTransitionRule.setValue(wfTransitionRule);
                List<WFTransitionRule> list = wFTransitionServiceRule.getWFTransitionRules(wfTransitionRule.getName(), entity.getProvider());
                Collections.sort(list);
                wfTransitionRulesByName.add(list);
                selectedRules.add(groupedTransitionRule);
            }
        }
    }

    public void addNewRule() {
        selectedRules.add(new GroupedTransitionRule());
    }

    public void deleteWfTransitionRule(int indexRule) {
        if (wfTransitionRulesByName.size() > indexRule && wfTransitionRulesByName.get(indexRule) != null) {
            wfTransitionRulesByName.remove(indexRule);
        }
        selectedRules.remove(indexRule);
    }

    public void deleteWfTransition(WFTransition dunningPlanTransition) {
        WFTransition transition = wfTransitionService.findById(dunningPlanTransition.getId());
        wfTransitionService.remove(transition);
        workflowOrder.getTransitions().remove(dunningPlanTransition);
        wfTransitionRulesByName.clear();
        selectedRules.clear();
        wfActions.clear();
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public TreeNode getUserGroupRootNode() {
        if (userGroupRootNode == null) {
            userGroupRootNode = new DefaultTreeNode("Root", null);
            List<UserHierarchyLevel> roots;
            if (entity != null && entity.getProvider() != null) {
                roots = userHierarchyLevelService.findRoots(entity.getProvider());
            } else {
                roots = userHierarchyLevelService.findRoots();
            }
            UserHierarchyLevel userHierarchyLevel = null;
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

    public TreeNode[] getUserGroupSelectedNodes() {
        return userGroupSelectedNodes;
    }

    public void setUserGroupSelectedNodes(TreeNode[] userGroupSelectedNodes) {
        this.userGroupSelectedNodes = userGroupSelectedNodes;
    }

    // Recursive function to create tree with node checked if selected
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private TreeNode createTree(HierarchyLevel hierarchyLevel, TreeNode rootNode, List<WFAction> wfActions) {
        TreeNode newNode = new DefaultTreeNode(hierarchyLevel, rootNode);
        List<UserHierarchyLevel> subTree = new ArrayList<UserHierarchyLevel>(hierarchyLevel.getChildLevels());
        newNode.setExpanded(true);
        if (wfActions != null) {
            for (WFAction wfAction1 : wfActions) {
                if (wfAction1 != null && wfAction1.getActionEl().equals(hierarchyLevel.getDescriptionOrCode())) {
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
        List<WFTransitionRule> list = wFTransitionServiceRule.getWFTransitionRules(selectedRules.get(indexRule).getName(), entity.getProvider());
        Collections.sort(list);
        if (wfTransitionRulesByName.size() > indexRule && wfTransitionRulesByName.get(indexRule) != null) {
            wfTransitionRulesByName.remove(indexRule);
            wfTransitionRulesByName.add(indexRule, list);
        } else {
            wfTransitionRulesByName.add(indexRule, list);
        }
    }

    public void moveUpOperation(WFTransition selectedWfTransition) throws BusinessException {
        int index = operationList.indexOf(selectedWfTransition);
        if (index > 0) {
            WFTransition upWfTransition = operationList.get(index);
            WFTransition needUpdate = wfTransitionService.findById(upWfTransition.getId(), true);
            needUpdate.setPriority(index);
            wfTransitionService.update(needUpdate, getCurrentUser());
            WFTransition downWfTransition = operationList.get(index - 1);
            needUpdate = wfTransitionService.findById(downWfTransition.getId(), true);
            needUpdate.setPriority(index + 1);
            wfTransitionService.update(needUpdate, getCurrentUser());
            Collections.swap(operationList, index, index - 1);
        }
    }

    public void moveDownOperation(WFTransition selectedWfTransition) throws BusinessException {
        int index = operationList.indexOf(selectedWfTransition);
        if (index < operationList.size() - 1) {
            WFTransition upWfTransition = operationList.get(index);
            WFTransition needUpdate = wfTransitionService.findById(upWfTransition.getId(), true);
            needUpdate.setPriority(index + 2);
            wfTransitionService.update(needUpdate, getCurrentUser());
            WFTransition downWfTransition = operationList.get(index + 1);
            needUpdate = wfTransitionService.findById(downWfTransition.getId(), true);
            needUpdate.setPriority(index + 1);
            wfTransitionService.update(needUpdate, getCurrentUser());
            Collections.swap(operationList, index, index + 1);
        }
    }

    @Override
    protected String getListViewName() {
        return "mmWorkflows";
    }

    @Override
    public String getEditViewName() {
        return "mmWorkflowDetail";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
     */
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider", "wfTransitionRules", "wfActions");
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
     */
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("provider", "wfTransitionRules", "wfActions");
    }
}
