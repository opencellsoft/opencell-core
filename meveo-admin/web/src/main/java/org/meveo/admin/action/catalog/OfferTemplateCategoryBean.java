package org.meveo.admin.action.catalog;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ExistsRelatedEntityException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class OfferTemplateCategoryBean extends CustomFieldBean<OfferTemplateCategory> {

    private static final long serialVersionUID = 1L;

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    private UploadedFile uploadedFile;

    private SortedTreeNode rootOfferTemplateCategory;

    private TreeNode selectedOfferTemplateCategory;

    private Boolean isEdit = Boolean.FALSE;

    public OfferTemplateCategoryBean() {
        super(OfferTemplateCategory.class);
    }

    @Override
    protected IPersistenceService<OfferTemplateCategory> getPersistenceService() {
        return offerTemplateCategoryService;
    }

    @Override
    protected String getListViewName() {
        return "offerTemplateCategories";
    }

    @Override
    public String getEditViewName() {
        return "offerTemplateCategories";
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        rootOfferTemplateCategory = null;
        getRootOfferTemplateCategory();

        if (selectedOfferTemplateCategory != null) {
            selectedOfferTemplateCategory.setSelected(false);
        }
        selectedOfferTemplateCategory = rootOfferTemplateCategory.findNodeByData(entity);
        if (selectedOfferTemplateCategory != null) {
            selectedOfferTemplateCategory.setSelected(true);
        }
        isEdit = true;
        return null;
    }

    public void handleFileUpload(FileUploadEvent event) throws BusinessException {
        uploadedFile = event.getFile();

        if (uploadedFile != null) {
            byte[] contents = uploadedFile.getContents();
            try {
                entity.setImage(new SerialBlob(contents));
            } catch (SQLException e) {
                entity.setImage(null);
            }
            entity.setImageContentType(uploadedFile.getContentType());

            saveOrUpdate(entity);

            initEntity();

            messages.info(new BundleKey("messages", "message.upload.succesful"));
        }
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public SortedTreeNode getRootOfferTemplateCategory() {
        if (rootOfferTemplateCategory == null) {
            rootOfferTemplateCategory = new SortedTreeNode(new OfferTemplateCategory(), null);
            List<OfferTemplateCategory> roots = null;
            if (entity != null && entity.getProvider() != null) {
                roots = offerTemplateCategoryService.findRoots(entity.getProvider());
            }

            if (CollectionUtils.isNotEmpty(roots)) {
                for (OfferTemplateCategory tree : roots) {
                    createTree(tree, rootOfferTemplateCategory);
                }
            }
        }
        return rootOfferTemplateCategory;
    }

    public void setRootOfferTemplateCategory(SortedTreeNode rootOfferTemplateCategory) {
        this.rootOfferTemplateCategory = rootOfferTemplateCategory;
    }

    public TreeNode getSelectedOfferTemplateCategory() {
        return selectedOfferTemplateCategory;
    }

    public void setSelectedOfferTemplateCategory(TreeNode selectedOfferTemplateCategory) {
        this.selectedOfferTemplateCategory = selectedOfferTemplateCategory;
    }

    public void onNodeSelect(NodeSelectEvent event) {
        if (selectedOfferTemplateCategory != null) {
            selectedOfferTemplateCategory.setSelected(false);
        }
        TreeNode treeNode = event.getTreeNode();
        OfferTemplateCategory offerTemplateCategory = (OfferTemplateCategory) treeNode.getData();
        offerTemplateCategory = offerTemplateCategoryService.refreshOrRetrieve(offerTemplateCategory);
        setEntity(offerTemplateCategory);
        selectedOfferTemplateCategory = treeNode;
        selectedOfferTemplateCategory.setSelected(true);
        setObjectId(offerTemplateCategory.getId());
        isEdit = true;
    }

    public void newOfferTemplateCategory() {
        OfferTemplateCategory offerTemplateCategory = initEntity(null);
        OfferTemplateCategory offerTemplateCategoryParent = null;
        if (selectedOfferTemplateCategory != null) {
            offerTemplateCategoryParent = (OfferTemplateCategory) selectedOfferTemplateCategory.getData();
            offerTemplateCategory.setOfferTemplateCategory(offerTemplateCategoryParent);
            if (CollectionUtils.isNotEmpty(selectedOfferTemplateCategory.getChildren())) {
                OfferTemplateCategory offerTemplateCategoryLast = (OfferTemplateCategory) selectedOfferTemplateCategory.getChildren()
                    .get(selectedOfferTemplateCategory.getChildCount() - 1).getData();
                offerTemplateCategory.setOrderLevel(offerTemplateCategoryLast.getOrderLevel() + 1);
            } else {
                offerTemplateCategory.setOrderLevel(1);
            }
            selectedOfferTemplateCategory = null;
        }
    }

    public void newOfferTemplateCategoryRoot() {
        selectedOfferTemplateCategory = null;

        OfferTemplateCategory offerTemplateCategory = initEntity(null);
        offerTemplateCategory.setOfferTemplateCategory(null);
        if (CollectionUtils.isNotEmpty(rootOfferTemplateCategory.getChildren())) {
            OfferTemplateCategory offerTemplateCategoryLast = (OfferTemplateCategory) rootOfferTemplateCategory.getChildren().get(rootOfferTemplateCategory.getChildCount() - 1)
                .getData();
            offerTemplateCategory.setOrderLevel(offerTemplateCategoryLast.getOrderLevel() + 1);
        } else {
            offerTemplateCategory.setOrderLevel(1);
        }
    }

    @ActionMethod
    public void removeOfferTemplateCategory() {
        OfferTemplateCategory offerTemplateCategory = (OfferTemplateCategory) selectedOfferTemplateCategory.getData();
        if (offerTemplateCategory != null) {
            try {
                offerTemplateCategoryService.remove(offerTemplateCategory.getId(), getCurrentUser());
                selectedOfferTemplateCategory.getParent().getChildren().remove(selectedOfferTemplateCategory);
                selectedOfferTemplateCategory = null;
                initEntity();
                messages.info(new BundleKey("messages", "delete.successful"));
            } catch (ExistsRelatedEntityException e) {
                messages.error(new BundleKey("messages", "offerTemplateCategory.errorDelete"));

            } catch (Exception e) {
                messages.error(new BundleKey("messages", "error.delete.unexpected"));
            }
        }
    }

    @ActionMethod
    public void moveUp() {
        SortedTreeNode node = (SortedTreeNode) selectedOfferTemplateCategory;
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
                OfferTemplateCategory offerTemplateCategory = (OfferTemplateCategory) node.getData();
                OfferTemplateCategory parent = (OfferTemplateCategory) parentSibling.getData();
                offerTemplateCategory.setOfferTemplateCategory(parent);
                node.setData(offerTemplateCategory);
                parentSibling.getChildren().add(node);
            }
        }

        try {
            updatePositionValue((SortedTreeNode) node.getParent());
            node.setSelected(true);
            setEntity(offerTemplateCategoryService.refreshOrRetrieve((OfferTemplateCategory) node.getData()));
            messages.info(new BundleKey("messages", "update.successful"));

        } catch (BusinessException e) {
            log.error("Failed to move up {}", node, e);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    @ActionMethod
    public void moveDown() {
        SortedTreeNode node = (SortedTreeNode) selectedOfferTemplateCategory;
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

                OfferTemplateCategory offerTemplateCategory = (OfferTemplateCategory) node.getData();
                OfferTemplateCategory parent = (OfferTemplateCategory) parentSibling.getData();
                offerTemplateCategory.setOfferTemplateCategory(parent);
                node.setData(offerTemplateCategory);
                parentSibling.getChildren().add(0, node);
            }
        }

        try {
            updatePositionValue((SortedTreeNode) node.getParent());
            node.setSelected(true);
            setEntity(offerTemplateCategoryService.refreshOrRetrieve((OfferTemplateCategory) node.getData()));
            messages.info(new BundleKey("messages", "update.successful"));

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
        if (isEdit && selectedOfferTemplateCategory != null && entity.getId() != null) {
            OfferTemplateCategory offerTemplateCategory = (OfferTemplateCategory) selectedOfferTemplateCategory.getData();
            setEntity(offerTemplateCategory);
        } else {
            entity.setCode(null);
            entity.setDescription(null);
            entity.setName(null);
        }
    }

    private void updatePositionValue(SortedTreeNode nodeToUpdate) throws BusinessException {

        // Re-position current and child nodes
        List<TreeNode> nodes = nodeToUpdate.getChildren();
        OfferTemplateCategory parent = null;
        if (((OfferTemplateCategory) nodeToUpdate.getData()).getId() != null) {
            parent = (OfferTemplateCategory) nodeToUpdate.getData();
        }

        if (CollectionUtils.isNotEmpty(nodes)) {
            for (TreeNode treeNode : nodes) {
                SortedTreeNode sortedNode = (SortedTreeNode) treeNode;
                OfferTemplateCategory offerTemplateCategory = (OfferTemplateCategory) sortedNode.getData();
                offerTemplateCategory = offerTemplateCategoryService.refreshOrRetrieve(offerTemplateCategory);
                int order = sortedNode.getIndexInParent();

                offerTemplateCategory.setOfferTemplateCategory(parent);
                offerTemplateCategory.setOrderLevel(order + 1);
                offerTemplateCategoryService.update(offerTemplateCategory, getCurrentUser());
            }
        }
    }

    public void onDragDrop(TreeDragDropEvent event) {
        SortedTreeNode dragNode = (SortedTreeNode) event.getDragNode();
        SortedTreeNode dropNode = (SortedTreeNode) event.getDropNode();
        if (dropNode != null) {
            OfferTemplateCategory dragOfferTemplateCategory = (OfferTemplateCategory) dragNode.getData();
            OfferTemplateCategory dropOfferTemplateCategory = (OfferTemplateCategory) dropNode.getData();
            if (dropOfferTemplateCategory.getId() != null) {
                dragOfferTemplateCategory.setOfferTemplateCategory(dropOfferTemplateCategory);
            } else {
                dragOfferTemplateCategory.setOfferTemplateCategory(null);
            }
            dragNode.setData(dragOfferTemplateCategory);
            try {
                // UnSelected previous selected node to make sure one node selected after drag drop
                unSelectedAllNode(rootOfferTemplateCategory);
                updatePositionValue(dropNode);
                dragNode.setSelected(true);
                setEntity(offerTemplateCategoryService.refreshOrRetrieve((OfferTemplateCategory) dragNode.getData()));
                messages.info(new BundleKey("messages", "update.successful"));

            } catch (BusinessException e) {
                log.error("Failed to drop {}", dragNode, e);
                messages.error(new BundleKey("messages", "error.unexpected"));
            }
        }
    }

    // Recursive function to create tree
    private TreeNode createTree(OfferTemplateCategory offerTemplateCategory, TreeNode rootNode) {
        TreeNode newNode = new SortedTreeNode(offerTemplateCategory, rootNode);
        newNode.setExpanded(true);
        if (offerTemplateCategory.getChildren() != null) {

            List<OfferTemplateCategory> subTree = offerTemplateCategory.getChildren();
            if (CollectionUtils.isNotEmpty(subTree)) {
                for (OfferTemplateCategory child : subTree) {
                    createTree(child, newNode);
                }
            }
        }
        return newNode;
    }

    /**
     * UnSelect previous selected node
     * 
     * @param rootNode
     */
    private void unSelectedAllNode(TreeNode rootNode) {
        if (rootNode.getChildCount() == 0) {
            rootNode.setSelected(false);
        }

        if (rootNode.getChildCount() > 0) {
            for (TreeNode childNode : rootNode.getChildren()) {
                unSelectedAllNode(childNode);
            }
        }
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
}
