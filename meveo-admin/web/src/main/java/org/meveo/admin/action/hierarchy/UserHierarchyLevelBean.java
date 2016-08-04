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

import org.apache.commons.collections.CollectionUtils;
import org.jboss.seam.international.status.Messages;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard backing bean for {@link org.meveo.model.hierarchy.UserHierarchyLevel} (extends {@link org.meveo.admin.action.BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class UserHierarchyLevelBean extends BaseBean<UserHierarchyLevel> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link UserHierarchyLevel} service. Extends {@link org.meveo.service.base.PersistenceService}. */
    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    @Inject
    private ProviderService providerService;

    @Inject
    private Messages messages;

    private TreeNode rootNode;

    private TreeNode selectedNode;

    private List<UserHierarchyLevel> roots;

    private static final Logger log = LoggerFactory.getLogger(UserHierarchyLevelBean.class);

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

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        log.debug("saving new user={}", entity.getCode());
        return super.saveOrUpdate(killConversation);
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
            rootNode = new SortedTreeNode("Root", null);
            roots = userHierarchyLevelService.findRoots();
            if (CollectionUtils.isNotEmpty(roots)) {
                for (UserHierarchyLevel tree : roots) {
                    createTree(tree, rootNode);
                }
            }
        }
        return rootNode;
    }

    public void onNodeSelect(NodeSelectEvent event) {
        TreeNode treeNode = event.getTreeNode();
        UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) treeNode.getData();
        setEntity(userHierarchyLevelService.findById(userHierarchyLevel.getId()));
        selectedNode = treeNode;
    }

    public void newUserHierarchyLevel() {
        UserHierarchyLevel userHierarchyLevel = initEntity();
        UserHierarchyLevel userHierarchyLevelParent = null;
        if (selectedNode != null) {
            userHierarchyLevelParent = (UserHierarchyLevel) selectedNode.getData();
        }
        userHierarchyLevel.setParentLevel(userHierarchyLevelParent);
        setEntity(userHierarchyLevel);
    }

    public void newUserHierarchyRoot() {
        UserHierarchyLevel userHierarchyLevel = initEntity();
        userHierarchyLevel.setParentLevel(null);
        setEntity(userHierarchyLevel);
    }

    public void removeUserHierarchyLevel() {
        UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) selectedNode.getData();
        userHierarchyLevelService.remove(userHierarchyLevel.getId());
        selectedNode.getParent().getChildren().remove(selectedNode);
        userHierarchyLevel = initEntity();
        userHierarchyLevel.setParentLevel(null);
        setEntity(userHierarchyLevel);
    }

    public void moveUp() {

    }

    public void moveDown() {

    }

    // Recursive function to create tree
    private TreeNode createTree(HierarchyLevel userHierarchyLevel, TreeNode rootNode) {
        TreeNode newNode = new SortedTreeNode(userHierarchyLevel, rootNode);
        newNode.setExpanded(true);
        List<UserHierarchyLevel> subTree = new ArrayList<UserHierarchyLevel>(userHierarchyLevel.getChildLevels());

        for (HierarchyLevel child : subTree) {
            createTree(child, newNode);
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
            return !(getIndexInParent() == 0);
        }

        public boolean canMoveDown() {
            return !(isLast());
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

        public SortedTreeNode getSiblingDown() {
            int currentIndex = this.getIndexInParent();
            if (getParent().getChildCount() > currentIndex + 1) {
                return (SortedTreeNode) getParent().getChildren().get(currentIndex + 1);
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
    }
}