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
package org.meveo.admin.action.hierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ExistsRelatedEntityException;
import org.meveo.model.admin.User;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard backing bean for {@link org.meveo.model.hierarchy.UserHierarchyLevel} (extends {@link org.meveo.admin.action.BaseBean} that provides almost all common methods to handle
 * entities filtering/sorting in datatable, their create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class UserHierarchyLevelBean extends BaseBean<UserHierarchyLevel> {

    private static final long serialVersionUID = 1L;
    private static final String ROOT = "Root";

    /**
     * Injected @{link UserHierarchyLevel} service. Extends {@link org.meveo.service.base.PersistenceService}.
     */
    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    private SortedTreeNode rootNode;

    private TreeNode selectedNode;

    private Boolean isEdit = Boolean.FALSE;

    private Boolean showUserGroupDetail = Boolean.FALSE;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link org.meveo.admin.action.BaseBean}.
     */
    public UserHierarchyLevelBean() {
        super(UserHierarchyLevel.class);
    }

    @PostConstruct
    public void init() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("users", "childLevels");
    }

    @Override
    protected String getListViewName() {
        return "userGroupHierarchy";
    }

    @Override
    public String getEditViewName() {
        return "userGroupHierarchy";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<UserHierarchyLevel> getPersistenceService() {
        return userHierarchyLevelService;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getRootNode() {
        if (rootNode == null) {
            rootNode = new SortedTreeNode(ROOT, null);
            List<UserHierarchyLevel> roots  = userHierarchyLevelService.findRoots();

            if (CollectionUtils.isNotEmpty(roots)) {
                Collections.sort(roots);
                for (UserHierarchyLevel tree : roots) {
                    createTree(tree, rootNode);
                }
            }
        }
        return rootNode;
    }

    public Boolean getShowUserGroupDetail() {
        return showUserGroupDetail;
    }

    public void setShowUserGroupDetail(Boolean showUserGroupDetail) {
        this.showUserGroupDetail = showUserGroupDetail;
    }

    public Boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void onNodeSelect(NodeSelectEvent event) {
        if (selectedNode != null) {
            selectedNode.setSelected(false);
        }
        TreeNode treeNode = event.getTreeNode();
        UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) treeNode.getData();
        userHierarchyLevel = userHierarchyLevelService.refreshOrRetrieve(userHierarchyLevel);
        setEntity(userHierarchyLevel);
        selectedNode = treeNode;
        selectedNode.setSelected(true);
        showUserGroupDetail = true;
        isEdit = true;
    }

    @SuppressWarnings("unchecked")
    public void onDragDrop(TreeDragDropEvent event) {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();
        UserHierarchyLevel child = null;
        UserHierarchyLevel parent = null;
        UserHierarchyLevel previousParent = null;
        Set<UserHierarchyLevel> updatedSiblings = new HashSet<>();
        Long index = event.getDropIndex() + 1L;
        Long previousIndex = 0L;
        boolean validChild = dragNode != null && dragNode.getData() instanceof UserHierarchyLevel;
        boolean hasParent = dropNode != null && dropNode.getData() instanceof UserHierarchyLevel;
        boolean hasPreviousParent = false;
        boolean sameParent = false;
        if (hasParent) {
            parent = (UserHierarchyLevel) dropNode.getData();
            parent = userHierarchyLevelService.refreshOrRetrieve(parent);
        }
        if (validChild) {
            child = (UserHierarchyLevel) dragNode.getData();
            child = userHierarchyLevelService.refreshOrRetrieve(child);
            previousIndex = child.getOrderLevel();
            hasPreviousParent = child.getParentLevel() != null;
            if (hasPreviousParent) {
                previousParent = userHierarchyLevelService.findByCode(child.getParentLevel().getCode());
                sameParent = previousParent.equals(parent);
                if (!sameParent) {
                    for (HierarchyLevel<User> sibling : previousParent.getChildLevels()) {
                        if (sibling.getOrderLevel() > previousIndex) {
                            sibling.setOrderLevel(sibling.getOrderLevel() - 1);
                            updatedSiblings.add((UserHierarchyLevel) sibling);
                        }
                    }
                    previousParent.getChildLevels().remove(child);
                } else {
                    parent.getChildLevels().remove(child);
                }
            }
            child.setParentLevel(null);
        }
        if (hasParent) {
            child.setParentLevel(parent);
            for (HierarchyLevel<User> sibling : parent.getChildLevels()) {
                Long siblingIndex = sibling.getOrderLevel();
                if (sameParent) {
                    boolean movedUp = previousIndex - index > 0;
                    if(movedUp) { // the previous index was greater than new index
                        if(siblingIndex < previousIndex && siblingIndex >= index) {
                            sibling.setOrderLevel(siblingIndex + 1);
                            updatedSiblings.add((UserHierarchyLevel) sibling);
                        }
                    } else {
                        if(siblingIndex <= index && siblingIndex > previousIndex) {
                            sibling.setOrderLevel(siblingIndex - 1);
                            updatedSiblings.add((UserHierarchyLevel) sibling);
                        }
                    }
                } else {
                    if(!hasPreviousParent) {
                        List<UserHierarchyLevel> roots = userHierarchyLevelService.findRoots();
                        for(UserHierarchyLevel root : roots) {
                            if (root.getOrderLevel() > previousIndex) {
                                root.setOrderLevel(root.getOrderLevel() - 1);
                                updatedSiblings.add(root);
                            }
                        }
                    } else if (siblingIndex >= index) {
                        sibling.setOrderLevel(siblingIndex + 1);
                        updatedSiblings.add((UserHierarchyLevel) sibling);
                    }
                }
            }
            parent.getChildLevels().add(child);
        } else if(!hasParent && !hasPreviousParent) {
            List<UserHierarchyLevel> roots = userHierarchyLevelService.findRoots();
            for(UserHierarchyLevel root : roots) {
                Long rootIndex = root.getOrderLevel();
                boolean movedUp = previousIndex - index > 0;
                if(movedUp) { // the previous index was greater than new index
                    if(rootIndex < previousIndex && rootIndex >= index) {
                        root.setOrderLevel(root.getOrderLevel() + 1);
                        updatedSiblings.add(root);
                    }
                } else {
                    if(rootIndex <= index && rootIndex > previousIndex) {
                        root.setOrderLevel(root.getOrderLevel() - 1);
                        updatedSiblings.add(root);
                    }
                }
            }
        } else if(!hasParent && hasPreviousParent) {
            List<UserHierarchyLevel> roots = userHierarchyLevelService.findRoots();
            for(UserHierarchyLevel root : roots) {
                Long rootIndex = root.getOrderLevel();
                if (rootIndex >= index) {
                    root.setOrderLevel(rootIndex + 1);
                    updatedSiblings.add(root);
                }
            }
        }

        try {
            child.setOrderLevel(index);
            userHierarchyLevelService.update(child);
            if (parent != null) {
                userHierarchyLevelService.update(parent);
            }
            if (previousParent != null && !sameParent) {
                userHierarchyLevelService.update(previousParent);
            }
            for (UserHierarchyLevel sibling : updatedSiblings) {
                userHierarchyLevelService.update(sibling);
            }
        } catch (BusinessException e) {
            messages.error(new BundleKey("messages", "userGroupHierarchy.errorChangeLevel"));
        }
    }

    public void newUserHierarchyLevel() {
        showUserGroupDetail = true;
        isEdit = false;
        UserHierarchyLevel userHierarchyLevel = initEntity(null);
        UserHierarchyLevel userHierarchyLevelParent = null;
        if (selectedNode != null) {
            userHierarchyLevelParent = (UserHierarchyLevel) selectedNode.getData();
            userHierarchyLevel.setParentLevel(userHierarchyLevelParent);
            if (CollectionUtils.isNotEmpty(selectedNode.getChildren())) {
                UserHierarchyLevel userHierarchyLast = (UserHierarchyLevel) selectedNode.getChildren().get(selectedNode.getChildCount() - 1).getData();
                userHierarchyLevel.setOrderLevel(userHierarchyLast.getOrderLevel() + 1);
            } else {
                userHierarchyLevel.setOrderLevel(1L);
            }
            selectedNode = null;
        }
    }

    public void newUserHierarchyRoot() {
        showUserGroupDetail = true;
        selectedNode = null;
        isEdit = false;
        UserHierarchyLevel userHierarchyLevel = initEntity(null);
        userHierarchyLevel.setParentLevel(null);
        if (CollectionUtils.isNotEmpty(rootNode.getChildren())) {
            UserHierarchyLevel userHierarchyLast = (UserHierarchyLevel) rootNode.getChildren().get(rootNode.getChildCount() - 1).getData();
            userHierarchyLevel.setOrderLevel(userHierarchyLast.getOrderLevel() + 1);
        } else {
            userHierarchyLevel.setOrderLevel(1L);
        }
    }

    public void removeUserHierarchyLevel() {
        UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) selectedNode.getData();
        if (userHierarchyLevel != null) {
            try {
                userHierarchyLevelService.remove(userHierarchyLevel.getId());
                selectedNode.getParent().getChildren().remove(selectedNode);
                selectedNode = null;
                showUserGroupDetail = false;
                initEntity();

                messages.info(new BundleKey("messages", "delete.successful"));

            } catch (ExistsRelatedEntityException e) {
                messages.error(new BundleKey("messages", "userGroupHierarchy.errorDelete"));

            } catch (Exception e) {
                messages.error(new BundleKey("messages", "error.delete.unexpected"));
            }

        }
    }

    public void moveUp() {
        SortedTreeNode node = (SortedTreeNode) selectedNode;
        int currentIndex = node.getIndexInParent();

        // Move a position up within the same branch
        if (currentIndex > 0) {
            TreeNode parent = node.getParent();
            parent.getChildren().remove(currentIndex);
            parent.getChildren().add(currentIndex - 1, node);

            // Move a position up outside the branch
        } else if (currentIndex == 0 && node.canMoveUp()) {
            TreeNode parentSibling = node.getParentSiblingUp();
            if (parentSibling != null) {
                node.getParent().getChildren().remove(currentIndex);
                UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) node.getData();
                UserHierarchyLevel parent = (UserHierarchyLevel) parentSibling.getData();
                userHierarchyLevel.setParentLevel(parent);
                node.setData(userHierarchyLevel);
                parentSibling.getChildren().add(node);
            }
        }

        try {
            updatePositionValue((SortedTreeNode) node.getParent());
            node.setSelected(true);
            setEntity(userHierarchyLevelService.refreshOrRetrieve((UserHierarchyLevel) node.getData()));

        } catch (BusinessException e) {
            log.error("Failed to move up {}", node, e);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    public void moveDown() {
        SortedTreeNode node = (SortedTreeNode) selectedNode;
        int currentIndex = node.getIndexInParent();
        boolean isLast = node.isLast();

        // Move a position down within the same branch
        if (!isLast) {
            TreeNode parent = node.getParent();

            parent.getChildren().remove(currentIndex);
            parent.getChildren().add(currentIndex + 1, node);

            // Move a position down outside the branch
        } else if (isLast && node.canMoveDown()) {
            SortedTreeNode parentSibling = node.getParentSiblingDown();
            if (parentSibling != null) {
                node.getParent().getChildren().remove(currentIndex);

                UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) node.getData();
                UserHierarchyLevel parent = (UserHierarchyLevel) parentSibling.getData();
                userHierarchyLevel.setParentLevel(parent);
                node.setData(userHierarchyLevel);
                parentSibling.getChildren().add(0, node);
            }
        }

        try {
            updatePositionValue((SortedTreeNode) node.getParent());
            node.setSelected(true);
            setEntity(userHierarchyLevelService.refreshOrRetrieve((UserHierarchyLevel) node.getData()));

        } catch (BusinessException e) {
            log.error("Failed to move down {}", node, e);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * Reset values to the last state.
     */
    @Override
    public void resetFormEntity() {
        if (isEdit && selectedNode != null) {
            UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) selectedNode.getData();
            setEntity(userHierarchyLevel);
        } else {
            entity.setCode(null);
            entity.setDescription(null);
        }
    }

    @SuppressWarnings("rawtypes")
    private void updatePositionValue(SortedTreeNode nodeToUpdate) throws BusinessException {

        // Re-position current and child nodes
        List<TreeNode> nodes = nodeToUpdate.getChildren();
        HierarchyLevel parent = null;
        if (!ROOT.equals(nodeToUpdate.getData())) {
            parent = (HierarchyLevel) nodeToUpdate.getData();
        }

        if (CollectionUtils.isNotEmpty(nodes)) {
            for (TreeNode treeNode : nodes) {
                SortedTreeNode sortedNode = (SortedTreeNode) treeNode;
                UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) sortedNode.getData();
                userHierarchyLevel = userHierarchyLevelService.refreshOrRetrieve(userHierarchyLevel);
                Long order = Long.valueOf(sortedNode.getIndexInParent());

                userHierarchyLevel.setParentLevel(parent);
                userHierarchyLevel.setOrderLevel(order + 1);
                userHierarchyLevelService.update(userHierarchyLevel);
            }
        }
        // selectedNode = null;
        // showUserGroupDetail = false;
    }

    // Recursive function to create tree
    @SuppressWarnings({"rawtypes", "unchecked"})
    private TreeNode createTree(HierarchyLevel userHierarchyLevel, TreeNode rootNode) {
        TreeNode newNode = new SortedTreeNode(userHierarchyLevel, rootNode);
        newNode.setExpanded(true);
        if (userHierarchyLevel.getChildLevels() != null) {

            List<UserHierarchyLevel> subTree = new ArrayList<UserHierarchyLevel>(userHierarchyLevel.getChildLevels());
            if (CollectionUtils.isNotEmpty(subTree)) {
                Collections.sort(subTree);
                for (HierarchyLevel child : subTree) {
                    createTree(child, newNode);
                }
            }
        }
        return newNode;
    }

    public class SortedTreeNode extends DefaultTreeNode {

        private static final long serialVersionUID = 3694377290046737073L;

        public SortedTreeNode() {
            super();
        }

        public SortedTreeNode(Object data, TreeNode parent) {
            super(data, parent);
        }

        public boolean canMoveUp() {
            // Can not move if its is a first item in a tree and nowhere to move
            return !(getIndexInParent() == 0 && this.getParent() == null);
        }

        public boolean canMoveDown() {
            return !(isLast() && this.getParent() == null);
        }

        protected int getIndexInParent() {
            return getParent().getChildren().indexOf(this);
        }

        protected boolean isLast() {
            return getIndexInParent() == this.getParent().getChildCount() - 1;
        }

        public SortedTreeNode getParentSiblingDown() {

            SortedTreeNode parent = (SortedTreeNode) this.getParent();
            while (parent.getParent() != null) {
                int parentIndex = parent.getIndexInParent();
                if (parent.getParent().getChildCount() > parentIndex + 1) {
                    SortedTreeNode sibling = (SortedTreeNode) parent.getParent().getChildren().get(parentIndex + 1);
                    return sibling;
                }
                parent = (SortedTreeNode) parent.getParent();
            }

            return null;
        }

        public SortedTreeNode getParentSiblingUp() {

            SortedTreeNode parent = (SortedTreeNode) this.getParent();
            while (parent.getParent() != null) {
                int parentIndex = parent.getIndexInParent();
                if (parentIndex > 0) {
                    SortedTreeNode sibling = (SortedTreeNode) parent.getParent().getChildren().get(parentIndex - 1);
                    return sibling;
                }
                parent = (SortedTreeNode) parent.getParent();
            }

            return null;
        }

        public TreeNode findNodeByData(Object dataToFind) {
            if (this.getData().equals(dataToFind)) {
                return this;
            }

            if (this.getChildCount() > 0) {
                for (TreeNode childNode : this.getChildren()) {
                    TreeNode nodeMatched = ((SortedTreeNode) childNode).findNodeByData(dataToFind);
                    if (nodeMatched != null) {
                        return nodeMatched;
                    }
                }
            }
            return null;
        }
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);

        rootNode = null;
        getRootNode();

        if (selectedNode != null) {
            selectedNode.setSelected(false);
        }
        selectedNode = rootNode.findNodeByData(entity);
        if (selectedNode != null) {
            selectedNode.setSelected(true);
        }
        showUserGroupDetail = true;
        isEdit = true;

        return null;
    }

    public UserHierarchyLevel getUserHierarchyLevelFromCode(String code) {
        UserHierarchyLevel userLevelFound = userHierarchyLevelService.findByCode(code);
        if (userLevelFound == null) {
            return new UserHierarchyLevel();
        }
        return userLevelFound;
    }
}