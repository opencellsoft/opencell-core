package org.meveo.admin.action.admin.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.CurrentProviderBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.meveo.service.custom.EntityCustomActionService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.meveo.util.EntityCustomizationUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named
@ViewScoped
public class CustomEntityTemplateBean extends BaseBean<CustomEntityTemplate> {

    private static final long serialVersionUID = 1187554162639618526L;

    @Inject
    private CurrentProviderBean currentProviderBean;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    /**
     * Request parameter
     */
    private String entityClassName;

    /**
     * Request parameter
     */
    private String appliesTo;

    /**
     * Class corresponding to a entityClassName value of CustomEntityTemplate class if null
     */
    @SuppressWarnings("rawtypes")
    private Class entityClass = CustomEntityTemplate.class;

    /**
     * Object being customized in case customization corresponds to a non CustomEntityTemplate class instance
     */
    private CustomizedEntity customizedEntity;

    /**
     * Prefix to apply to custom field templates (appliesTo value)
     */
    private String cetPrefix;

    private SortedTreeNode groupedFields;

    private TreeNode selectedFieldGrouping;
    private List<TreeNode> cachedTreeNodes;

    private List<EntityCustomAction> entityActions;

    private EntityCustomAction selectedEntityAction;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private EntityCustomActionService entityActionScriptService;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private CustomizedEntityService customizedEntityService;

    public CustomEntityTemplateBean() {
        super(CustomEntityTemplate.class);
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {

        if (entityClassName != null) {
            entityClassName = ReflectionUtils.getCleanClassName(entityClassName);
        }

        this.entityClassName = entityClassName;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    @Override
    protected CustomEntityTemplateService getPersistenceService() {
        return customEntityTemplateService;
    }

    public boolean isCustomEntityTemplate() {
        return entityClassName == null || CustomEntityTemplate.class.getName().equals(entityClassName);
    }

    /**
     * Prepare to show entity customozation for a particular class - To be used from GUI action button/link
     * 
     * @param clazz Entity class
     */
    public void initCustomization(String entityClassName) {
        customizedEntity = null;
        this.entityClassName = entityClassName;
        try {
            getCustomizedEntity();
        } catch (ClassNotFoundException e) {
            log.error("Failed to initialize entity customization for a class {}", entityClassName);
        }
    }

    /**
     * Construct customizedEntity instance which is a representation of customizable class (e.g. Customer)
     * 
     * @return
     * @throws ClassNotFoundException
     */
    public CustomizedEntity getCustomizedEntity() throws ClassNotFoundException {

        // Convert appliesTo parameter to a entityClassName of objectId value in case of customEntity template
        if (customizedEntity == null && appliesTo != null) {

            CustomizedEntity customizedEntityMatched = customizedEntityService.getCustomizedEntity(appliesTo, getCurrentProvider());
            if (customizedEntityMatched != null) {
                setEntityClassName(customizedEntityMatched.getEntityClass().getName());
                if (customizedEntityMatched.getCustomEntityId() != null) {
                    initEntity(customizedEntityMatched.getCustomEntityId());
                }
            }
        }

        if (customizedEntity == null && entityClassName != null && !CustomEntityTemplate.class.getName().equals(entityClassName)) {
            entityClass = Class.forName(entityClassName);
            customizedEntity = new CustomizedEntity(entityClass.getSimpleName(), entityClass, null, null);
            cetPrefix = EntityCustomizationUtils.getAppliesTo(entityClass, null);

            if (Job.class.isAssignableFrom(entityClass)) {

                // Check and instantiate missing custom field templates for a given job
                Job job = jobInstanceService.getJobByName(entityClass.getSimpleName());
                Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();

                // Create missing custom field templates if needed
                try {
                    customFieldTemplateService.createMissingTemplates(cetPrefix, jobCustomFields.values(), getCurrentUser());

                } catch (BusinessException e) {
                    log.error("Failed to construct customized entity", e);
                    messages.error(new BundleKey("messages", "error.unexpected"));
                }
            }
        }

        return customizedEntity;
    }

    public TreeNode getFields() {
        if (groupedFields != null || cetPrefix == null) {
            return groupedFields;
        }

        Map<String, CustomFieldTemplate> fields = customFieldTemplateService.findByAppliesToNoCache(cetPrefix, getCurrentProvider());

        GroupedCustomField groupedCFT = new GroupedCustomField(fields.values(), CustomEntityTemplate.class.isAssignableFrom(entityClass) ? entity.getName() : "Custom fields", true);

        groupedFields = new SortedTreeNode(groupedCFT.getType(), groupedCFT.getData(), null);
        groupedFields.setExpanded(true);

        // Create through tabs
        for (GroupedCustomField level1 : groupedCFT.getChildren()) {
            SortedTreeNode level1Node = new SortedTreeNode(level1.getType(), level1.getData(), groupedFields);
            level1Node.setExpanded(true);

            // Create fields of field groups
            for (GroupedCustomField level2 : level1.getChildren()) {
                SortedTreeNode level2Node = new SortedTreeNode(level2.getType(), level2.getData(), level1Node);
                if (level2.getType().equals(GroupedCustomField.TYPE_FIELD_GROUP)) {
                    level2Node.setExpanded(true);
                }
                // Create fields
                for (GroupedCustomField level3 : level2.getChildren()) {
                    new SortedTreeNode(level3.getType(), level3.getData(), level2Node);
                }
            }
        }
        if (cachedTreeNodes != null && !cachedTreeNodes.isEmpty()) {
            for (TreeNode tabNode : cachedTreeNodes) {// tab
                TreeNode existedTab = getChildNodeByValue(groupedFields, tabNode.getData().toString());
                if (existedTab == null) {// check tab
                    existedTab = new SortedTreeNode(CustomFieldTemplate.POSITION_TAB, tabNode.getData().toString(), groupedFields);
                    existedTab.setExpanded(true);
                }
                for (TreeNode fieldGroupNode : tabNode.getChildren()) {// field groups of tab
                    TreeNode existedFieldGroup = getChildNodeByValue(existedTab, fieldGroupNode.getData().toString());
                    if (existedFieldGroup == null) {
                        existedFieldGroup = new SortedTreeNode(CustomFieldTemplate.POSITION_FIELD_GROUP, fieldGroupNode.getData().toString(), existedTab);
                    }
                }
            }
        }

        return groupedFields;
    }

    private TreeNode getChildNodeByValue(TreeNode parentTreeNode, String value) {
        for (TreeNode childNode : parentTreeNode.getChildren()) {
            if (childNode.getData().equals(value)) {
                return childNode;
            }
        }
        return null;
    }

    public List<EntityCustomAction> getEntityActions() {

        if (entityActions != null || cetPrefix == null) {
            return entityActions;
        }

        Map<String, EntityCustomAction> scripts = entityActionScriptService.findByAppliesTo(cetPrefix, getCurrentProvider());

        entityActions = new ArrayList<EntityCustomAction>();
        entityActions.addAll(scripts.values());

        return entityActions;
    }

    public void removeField(TreeNode currentNode) {
        currentNode.getParent().getChildren().remove(currentNode);
        refreshFields();
    }

    public void refreshFields() {
        if (groupedFields != null) {
            cachedTreeNodes = new ArrayList<TreeNode>();
            for (TreeNode tabNode : groupedFields.getChildren()) {
                SortedTreeNode tab = new SortedTreeNode(CustomFieldTemplate.POSITION_TAB, tabNode.getData(), null);
                cachedTreeNodes.add(tab);
                if (tabNode.getChildCount() != 0) {
                    for (TreeNode fieldGroupNode : tabNode.getChildren()) {// fieldgroup
                        SortedTreeNode fieldGroup = (SortedTreeNode) fieldGroupNode;
                        if (fieldGroup.getType().equals(CustomFieldTemplate.POSITION_FIELD_GROUP) && fieldGroupNode.getChildCount() == 0) {
                            new SortedTreeNode(CustomFieldTemplate.POSITION_FIELD_GROUP, fieldGroupNode.getData(), tab);
                        }
                    }
                }
            }
        }
        groupedFields = null;
    }

    public void refreshActions() {
        entityActions = null;
    }

    public void setSelectedFieldGrouping(TreeNode selectedFieldGrouping) {
        this.selectedFieldGrouping = selectedFieldGrouping;
    }

    public TreeNode getSelectedFieldGrouping() {
        return selectedFieldGrouping;
    }

    public void setSelectedEntityAction(EntityCustomAction selectedEntityAction) {
        this.selectedEntityAction = selectedEntityAction;
    }

    public EntityCustomAction getSelectedEntityAction() {
        return selectedEntityAction;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        boolean isNew = entity.isTransient();
        super.saveOrUpdate(killConversation);
        if (isNew) {
            currentProviderBean.refreshCurrentUser();
        }

        return getEditViewName();
    }

    @Override
    public String getEditViewName() {
        return "customizedEntity";
    }

    @Override
    public String getListViewName() {
        return "customizedEntities";
    }

    public String getCetPrefix() {
        if (cetPrefix != null) {
            return cetPrefix;

        } else if (entity != null && entity.getCode() != null) {
            cetPrefix = entity.getAppliesTo();
            return cetPrefix;
        }
        return null;
    }

    public void newTab() {
        selectedFieldGrouping = new SortedTreeNode(GroupedCustomField.TYPE_TAB, "", groupedFields);
        selectedFieldGrouping.setExpanded(true);
    }

    public void newFieldGroup(TreeNode parentNode) {
        selectedFieldGrouping = new SortedTreeNode(GroupedCustomField.TYPE_FIELD_GROUP, "", parentNode);
        selectedFieldGrouping.setExpanded(true);
    }

    public void saveUpdateFieldGrouping() {

        try {
            updateFieldGuiPositionValue((SortedTreeNode) selectedFieldGrouping);

        } catch (BusinessException e) {
            log.error("Failed to update field grouping {}", selectedFieldGrouping, e);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    public void cancelFieldGrouping() {
        if (StringUtils.isBlank((String) selectedFieldGrouping.getData())) {
            selectedFieldGrouping.getParent().getChildren().remove(selectedFieldGrouping);
        }
    }

    public void removeFieldGrouping() {

        try {
            for (TreeNode childNode : selectedFieldGrouping.getChildren()) {
                if (childNode.getType().equals(GroupedCustomField.TYPE_FIELD)) {
                    customFieldTemplateService.remove(((CustomFieldTemplate) childNode.getData()).getId(), getCurrentUser());
                } else if (childNode.getType().equals(GroupedCustomField.TYPE_FIELD_GROUP)) {
                    for (TreeNode childChildNode : childNode.getChildren()) {
                        customFieldTemplateService.remove(((CustomFieldTemplate) childChildNode.getData()).getId(), getCurrentUser());
                    }
                }
            }

            selectedFieldGrouping.getParent().getChildren().remove(selectedFieldGrouping);
            
        } catch (BusinessException e) {
            log.error("Failed to remove field grouping", e);
        }
        refreshFields();
    }

    public void moveUp(SortedTreeNode node) {

        int currentIndex = node.getIndexInParent();

        // Move a position up within the same branch
        if (currentIndex > 0) {
            TreeNode parent = node.getParent();
            parent.getChildren().remove(currentIndex);
            parent.getChildren().add(currentIndex - 1, node);

            // Move a position up outside the branch
        } else if (currentIndex == 0 && node.canMoveUp()) {
            TreeNode parentSibling = node.getParentSiblingUp();
            node.getParent().getChildren().remove(currentIndex);
            parentSibling.getChildren().add(node);
        }

        try {
            updateFieldGuiPositionValue((SortedTreeNode) node.getParent());

        } catch (BusinessException e) {
            log.error("Failed to move up {}", node, e);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    public void moveDown(SortedTreeNode node) {

        int currentIndex = node.getIndexInParent();
        boolean isLast = node.isLast();

        // Move a position down within the same branch
        if (!isLast) {
            TreeNode parent = node.getParent();
            if (node.getType().equals(GroupedCustomField.TYPE_FIELD)) {
                SortedTreeNode siblingDown = node.getSiblingDown();
                if (siblingDown != null && !siblingDown.getType().equals(GroupedCustomField.TYPE_FIELD)) {
                    parent.getChildren().remove(currentIndex);
                    siblingDown.getChildren().add(0, node);
                    return;
                }
            }
            parent.getChildren().remove(currentIndex);
            parent.getChildren().add(currentIndex + 1, node);

            // Move a position down outside the branch
        } else if (isLast && node.canMoveDown()) {
            SortedTreeNode parentSibling = node.getParentSiblingDown();
            if (parentSibling != null) {
                node.getParent().getChildren().remove(currentIndex);

                if (parentSibling.getType().equals(GroupedCustomField.TYPE_FIELD)) {
                    parentSibling.getParent().getChildren().add(parentSibling.getIndexInParent(), node);
                } else {
                    parentSibling.getChildren().add(0, node);
                }
            }
        }

        try {
            updateFieldGuiPositionValue((SortedTreeNode) node.getParent());

        } catch (BusinessException e) {
            log.error("Failed to move down {}", node, e);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    private void updateFieldGuiPositionValue(SortedTreeNode nodeToUpdate) throws BusinessException {

        // Re-position current and child nodes
        List<TreeNode> nodes = nodeToUpdate.getChildren();
        if (!nodeToUpdate.getType().equals(GroupedCustomField.TYPE_ROOT)) {
            nodes = new ArrayList<TreeNode>();
            nodes.add(nodeToUpdate);
        }

        for (TreeNode treeNode : nodes) {
            SortedTreeNode sortedNode = (SortedTreeNode) treeNode;

            String currentPosition = null;
            if (sortedNode.getType().equals(GroupedCustomField.TYPE_TAB)) {
                currentPosition = CustomFieldTemplate.POSITION_TAB + ":" + sortedNode.getData() + ":" + sortedNode.getIndexInParent();

            } else if (sortedNode.getType().equals(GroupedCustomField.TYPE_FIELD_GROUP)) {
                if (!sortedNode.getParent().getType().equals(GroupedCustomField.TYPE_ROOT)) {
                    currentPosition = CustomFieldTemplate.POSITION_TAB + ":" + sortedNode.getParent().getData() + ":"
                            + ((SortedTreeNode) sortedNode.getParent()).getIndexInParent() + ";";
                }
                currentPosition = currentPosition + CustomFieldTemplate.POSITION_FIELD_GROUP + ":" + sortedNode.getData() + ":" + sortedNode.getIndexInParent();

            }
            for (TreeNode node : sortedNode.getChildren()) {
                SortedTreeNode sortedChildNode = (SortedTreeNode) node;
                if (sortedChildNode.getType().equals(GroupedCustomField.TYPE_FIELD)) {
                    String guiPosition = currentPosition + ";" + CustomFieldTemplate.POSITION_FIELD + ":" + sortedChildNode.getIndexInParent();
                    CustomFieldTemplate cft = (CustomFieldTemplate) sortedChildNode.getData();
                    cft = customFieldTemplateService.refreshOrRetrieve(cft);
                    if (!guiPosition.equals(cft.getGuiPosition())) {
                        cft.setGuiPosition(guiPosition);
                        cft = customFieldTemplateService.update(cft, getCurrentUser());
                        sortedChildNode.setData(cft);
                    }

                } else if (sortedChildNode.getType().equals(GroupedCustomField.TYPE_FIELD_GROUP)) {
                    String childGroupPosition = currentPosition + ";" + GroupedCustomField.TYPE_FIELD_GROUP + ":" + sortedChildNode.getData() + ":"
                            + sortedChildNode.getIndexInParent();
                    for (TreeNode childNode : sortedChildNode.getChildren()) {
                        SortedTreeNode sortedChildChildNode = (SortedTreeNode) childNode;
                        String guiPosition = childGroupPosition + ";" + CustomFieldTemplate.POSITION_FIELD + ":" + sortedChildChildNode.getIndexInParent();
                        CustomFieldTemplate cft = (CustomFieldTemplate) sortedChildChildNode.getData();
                        cft = customFieldTemplateService.refreshOrRetrieve(cft);
                        if (!guiPosition.equals(cft.getGuiPosition())) {
                            cft.setGuiPosition(guiPosition);
                            cft = customFieldTemplateService.update(cft, getCurrentUser());
                            sortedChildChildNode.setData(cft);
                        }
                    }
                }
            }
        }
    }

    public class SortedTreeNode extends DefaultTreeNode {

        private static final long serialVersionUID = 3694377290046737073L;

        public SortedTreeNode() {
            super();
        }

        public SortedTreeNode(String type, Object data, TreeNode parent) {
            super(type, data, parent);
        }

        public String getGuiPositionForField() {

            if (getType().equals(GroupedCustomField.TYPE_TAB)) {
                return CustomFieldTemplate.POSITION_TAB + ":" + getData() + ":" + getParent().getChildren().indexOf(this) + ";field:" + getChildCount();

            } else if (getType().equals(GroupedCustomField.TYPE_FIELD_GROUP)) {
                String guiPosition = CustomFieldTemplate.POSITION_FIELD_GROUP + ":" + getData() + ":" + getParent().getChildren().indexOf(this) + ";field:" + getChildCount();
                if (getParent().getType().equals(GroupedCustomField.TYPE_TAB)) {
                    guiPosition = CustomFieldTemplate.POSITION_TAB + ":" + getParent().getData() + ":" + getParent().getParent().getChildren().indexOf(getParent()) + ";"
                            + guiPosition;
                }
                return guiPosition;
            }
            return null;
        }

        public boolean canMoveUp() {
            // Can not move if its is a first item in a tree and nowhere to move
            return !(getIndexInParent() == 0 && (this.getType().equals(GroupedCustomField.TYPE_TAB)
                    || (this.getType().equals(GroupedCustomField.TYPE_FIELD_GROUP) && ((SortedTreeNode) this.getParent()).getIndexInParent() == 0) || (this.getType().equals(
                GroupedCustomField.TYPE_FIELD)
                    && this.getParent().getType().equals(GroupedCustomField.TYPE_TAB) && ((SortedTreeNode) this.getParent()).getIndexInParent() == 0)));

        }

        public boolean canMoveDown() {

            return !(isLast() && (this.getType().equals(GroupedCustomField.TYPE_TAB)
                    || (this.getType().equals(GroupedCustomField.TYPE_FIELD_GROUP) && ((SortedTreeNode) this.getParent()).isLast())
                    || (this.getType().equals(GroupedCustomField.TYPE_FIELD) && this.getParent().getType().equals(GroupedCustomField.TYPE_TAB) && ((SortedTreeNode) this
                        .getParent()).isLast()) || (this.getType().equals(GroupedCustomField.TYPE_FIELD) && this.getParent().getType().equals(GroupedCustomField.TYPE_FIELD_GROUP) && !((SortedTreeNode) this
                .getParent()).canMoveDown())));

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