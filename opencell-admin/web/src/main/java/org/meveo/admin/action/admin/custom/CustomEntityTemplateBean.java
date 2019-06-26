package org.meveo.admin.action.admin.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTemplate.GroupedCustomFieldTreeItemType;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
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

/**
 * A GUI backing bean class for CustomEntityTemplate entity for detail view
 * 
 * @author Andrius Karpavicius
 */
@Named
@ViewScoped
public class CustomEntityTemplateBean extends BaseBean<CustomEntityTemplate> {

    private static final long serialVersionUID = 1187554162639618526L;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    /**
     * Name of entity class, for which to manage custom fields. An alternative to appliesTo parameter. Is passed as a request parameter
     */
    private String entityClassName;

    /**
     * Applies to value, for which to manage custom fields. An alternative to entityClassName parameter. Is passed as a request parameter
     */
    private String appliesTo;

    /**
     * Class corresponding to a entityClassName value or CustomEntityTemplate class if null
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

    private TranslatableLabel selectedFieldGroupingLabel = new TranslatableLabel();

    /**
     * Remember tree nodes before refreshing them - that way tabs and fieldgroups are not lost after refresh if they had no children
     */
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

    /**
     * Constructor
     */
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

    /**
     * Is entity being customized is a Custom entity template
     * 
     * @return True if entity being customized is a Custom entity template
     */
    public boolean isCustomEntityTemplate() {
        return entityClassName == null || CustomEntityTemplate.class.getName().equals(entityClassName);
    }

    /**
     * Is entity being customized is a Custom entity template and will be stored as a separate table
     * 
     * @return True if entity being customized is a Custom entity template and will be stored as a separate table
     */
    public boolean isCustomTable() {
        return isCustomEntityTemplate() && entity != null && entity.isStoreAsTable();
    }

    /**
     * Prepare to show entity customozation for a particular class - To be used from GUI action button/link
     * 
     * @param entityClassName Entity class
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
     * @return CustomizedEntity instance which is a representation of customizable class (e.g. Customer)
     * @throws ClassNotFoundException Class was not found
     */
    public CustomizedEntity getCustomizedEntity() throws ClassNotFoundException {

        // Convert appliesTo parameter to a entityClassName of objectId value in case of customEntity template
        if (customizedEntity == null && appliesTo != null) {

            CustomizedEntity customizedEntityMatched = customizedEntityService.getCustomizedEntity(appliesTo);
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
                    customFieldTemplateService.createMissingTemplates(cetPrefix, jobCustomFields.values());

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

        Map<String, CustomFieldTemplate> fields = customFieldTemplateService.findByAppliesToNoCache(cetPrefix);

        GroupedCustomField groupedCFTAndActions = new GroupedCustomField(fields.values(),
            CustomEntityTemplate.class.isAssignableFrom(entityClass) ? entity.getName() : "Custom fields", true);

        // Append actions into the hierarchy of tabs and fieldgroups
        Map<String, EntityCustomAction> customActions = entityActionScriptService.findByAppliesTo(cetPrefix);
        groupedCFTAndActions.append(customActions.values());

        groupedFields = new SortedTreeNode(groupedCFTAndActions.getType(), groupedCFTAndActions.getData(), null, true);

        // Create tabs
        for (GroupedCustomField level1 : groupedCFTAndActions.getChildren()) {
            SortedTreeNode level1Node = new SortedTreeNode(level1.getType(), level1.getData(), groupedFields, level1.getType() == GroupedCustomFieldTreeItemType.tab);

            // Create fields of field groups
            for (GroupedCustomField level2 : level1.getChildren()) {
                SortedTreeNode level2Node = new SortedTreeNode(level2.getType(), level2.getData(), level1Node, level2.getType() == GroupedCustomFieldTreeItemType.fieldGroup);

                // Create fields
                for (GroupedCustomField level3 : level2.getChildren()) {
                    new SortedTreeNode(level3.getType(), level3.getData(), level2Node, null);
                }
            }
        }

        if (cachedTreeNodes != null && !cachedTreeNodes.isEmpty()) {
            for (TreeNode tabNode : cachedTreeNodes) {// tab
                TreeNode existedTab = getChildNodeByValue(groupedFields, tabNode.getData().toString());
                if (existedTab == null) {// check tab
                    existedTab = new SortedTreeNode(GroupedCustomFieldTreeItemType.tab, tabNode.getData(), groupedFields, true);

                }
                for (TreeNode fieldGroupNode : tabNode.getChildren()) {// field groups of tab
                    TreeNode existedFieldGroup = getChildNodeByValue(existedTab, fieldGroupNode.getData().toString());
                    if (existedFieldGroup == null) {
                        existedFieldGroup = new SortedTreeNode(GroupedCustomFieldTreeItemType.fieldGroup, fieldGroupNode.getData(), existedTab, null);
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

        Map<String, EntityCustomAction> scripts = entityActionScriptService.findByAppliesTo(cetPrefix);

        entityActions = new ArrayList<EntityCustomAction>();
        entityActions.addAll(scripts.values());

        return entityActions;
    }

    public void removeField(TreeNode currentNode) {
        currentNode.getParent().getChildren().remove(currentNode);
        refreshFields();
    }

    /**
     * Remember the tabs and fieldgroups as they are reconstructed from field and action guiPosition fields. And then clear groupedFields value so it would be reconstructed again
     * uppon the first request.
     */
    public void refreshFields() {
        if (groupedFields != null) {
            cachedTreeNodes = new ArrayList<TreeNode>();
            for (TreeNode rootChildNode : groupedFields.getChildren()) {
                if (rootChildNode.getType().equals(GroupedCustomFieldTreeItemType.tab.name())) {
                    SortedTreeNode tab = new SortedTreeNode(GroupedCustomFieldTreeItemType.tab, rootChildNode.getData(), null, null);
                    cachedTreeNodes.add(tab);
                    if (rootChildNode.getChildCount() != 0) {
                        for (TreeNode fieldGroupNode : rootChildNode.getChildren()) {// fieldgroup
                            SortedTreeNode fieldGroup = (SortedTreeNode) fieldGroupNode;
                            if (fieldGroup.getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name()) && fieldGroupNode.getChildCount() == 0) {
                                new SortedTreeNode(GroupedCustomFieldTreeItemType.fieldGroup, fieldGroupNode.getData(), tab, null);
                            }
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

    public void editFieldGrouping(TreeNode selectedFieldGrouping) {
        setSelectedFieldGrouping(selectedFieldGrouping);
        // this.selectedFieldGroupingLabel = (TranslatableLabel) selectedFieldGrouping.getData();
    }

    public void setSelectedFieldGrouping(TreeNode selectedFieldGrouping) {
        this.selectedFieldGrouping = selectedFieldGrouping;
        this.selectedFieldGroupingLabel = (TranslatableLabel) selectedFieldGrouping.getData();
    }

    public TreeNode getSelectedFieldGrouping() {
        return selectedFieldGrouping;
    }

    public TranslatableLabel getSelectedFieldGroupingLabel() {
        return selectedFieldGroupingLabel;
    }

    public void setSelectedFieldGroupingLabel(TranslatableLabel selectedFieldGroupingLabel) {
        this.selectedFieldGroupingLabel = selectedFieldGroupingLabel;
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
        super.saveOrUpdate(killConversation);

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
        setSelectedFieldGrouping(new SortedTreeNode(GroupedCustomFieldTreeItemType.tab, new TranslatableLabel(""), groupedFields, true));
        // this.selectedFieldGroupingLabel = (TranslatableLabel) selectedFieldGrouping.getData();
    }

    public void newFieldGroup(TreeNode parentNode) {
        setSelectedFieldGrouping(new SortedTreeNode(GroupedCustomFieldTreeItemType.fieldGroup, new TranslatableLabel(""), parentNode, true));
        // this.selectedFieldGroupingLabel = (TranslatableLabel) selectedFieldGrouping.getData();
    }

    public void saveUpdateFieldGrouping() {

        try {
            ((SortedTreeNode) selectedFieldGrouping).setData(selectedFieldGroupingLabel);

            updateFieldGuiPositionValue((SortedTreeNode) selectedFieldGrouping);

        } catch (BusinessException e) {
            log.error("Failed to update field grouping {}", selectedFieldGrouping, e);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    public void cancelFieldGrouping() {
        if (((TranslatableLabel) selectedFieldGrouping.getData()).isEmpty()) {
            selectedFieldGrouping.getParent().getChildren().remove(selectedFieldGrouping);
        }
    }

    public void removeFieldGrouping() {

        try {
            for (TreeNode childNode : selectedFieldGrouping.getChildren()) {
                if (childNode.getType().equals(GroupedCustomFieldTreeItemType.field.name())) {
                    customFieldTemplateService.remove(((CustomFieldTemplate) childNode.getData()).getId());
                } else if (childNode.getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name())) {
                    for (TreeNode childChildNode : childNode.getChildren()) {
                        customFieldTemplateService.remove(((CustomFieldTemplate) childChildNode.getData()).getId());
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
            boolean moved = false;
            if (node.getType().equals(GroupedCustomFieldTreeItemType.field.name()) || node.getType().equals(GroupedCustomFieldTreeItemType.action.name())) {
                SortedTreeNode siblingDown = node.getSiblingDown();
                if (siblingDown != null && !(siblingDown.getType().equals(GroupedCustomFieldTreeItemType.field.name())
                        || siblingDown.getType().equals(GroupedCustomFieldTreeItemType.action.name()))) {
                    parent.getChildren().remove(currentIndex);
                    siblingDown.getChildren().add(0, node);
                    moved = true;
                    // return;
                }
            }
            if (!moved) {
                parent.getChildren().remove(currentIndex);
                parent.getChildren().add(currentIndex + 1, node);
            }
            // Move a position down outside the branch
        } else if (isLast && node.canMoveDown()) {
            SortedTreeNode parentSibling = node.getParentSiblingDown();
            if (parentSibling != null) {
                node.getParent().getChildren().remove(currentIndex);

                if (parentSibling.getType().equals(GroupedCustomFieldTreeItemType.field.name()) || parentSibling.getType().equals(GroupedCustomFieldTreeItemType.action.name())) {
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
        if (!nodeToUpdate.getType().equals(GroupedCustomFieldTreeItemType.root.name())) {
            nodes = new ArrayList<TreeNode>();
            nodes.add(nodeToUpdate);
        }

        for (TreeNode treeNode : nodes) {
            SortedTreeNode sortedNode = (SortedTreeNode) treeNode;
            String currentPosition = null;

            // Only tab, fieldGroup and action can be under root
            if (sortedNode.getType().equals(GroupedCustomFieldTreeItemType.tab.name())) {
                currentPosition = GroupedCustomFieldTreeItemType.tab.positionTag + ":" + sortedNode.getData() + ":" + sortedNode.getIndexInParent();

            } else if (sortedNode.getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name())) {
                if (!sortedNode.getParent().getType().equals(GroupedCustomFieldTreeItemType.root.name())) {
                    currentPosition = GroupedCustomFieldTreeItemType.tab.positionTag + ":" + sortedNode.getParent().getData() + ":"
                            + ((SortedTreeNode) sortedNode.getParent()).getIndexInParent() + ";";
                }
                currentPosition = currentPosition + GroupedCustomFieldTreeItemType.fieldGroup.positionTag + ":" + sortedNode.getData() + ":" + sortedNode.getIndexInParent();

            } else if (sortedNode.getType().equals(GroupedCustomFieldTreeItemType.action.name())) {
                String guiPosition = GroupedCustomFieldTreeItemType.action.positionTag + ":" + sortedNode.getIndexInParent();
                EntityCustomAction action = (EntityCustomAction) sortedNode.getData();
                action = entityActionScriptService.refreshOrRetrieve(action);
                if (!guiPosition.equals(action.getGuiPosition())) {
                    action.setGuiPosition(guiPosition);
                    action = entityActionScriptService.update(action);
                    sortedNode.setData(action);
                }
                // No children under action, so continue
                continue;
            }

            for (TreeNode node : sortedNode.getChildren()) {
                SortedTreeNode sortedChildNode = (SortedTreeNode) node;
                if (sortedChildNode.getType().equals(GroupedCustomFieldTreeItemType.field.name())) {
                    String guiPosition = currentPosition + ";" + GroupedCustomFieldTreeItemType.field.positionTag + ":" + sortedChildNode.getIndexInParent();
                    CustomFieldTemplate cft = (CustomFieldTemplate) sortedChildNode.getData();
                    cft = customFieldTemplateService.refreshOrRetrieve(cft);
                    if (!guiPosition.equals(cft.getGuiPosition())) {
                        cft.setGuiPosition(guiPosition);
                        cft = customFieldTemplateService.update(cft);
                        sortedChildNode.setData(cft);
                    }

                } else if (sortedChildNode.getType().equals(GroupedCustomFieldTreeItemType.action.name())) {
                    String guiPosition = currentPosition + ";" + GroupedCustomFieldTreeItemType.action.positionTag + ":" + sortedChildNode.getIndexInParent();
                    EntityCustomAction action = (EntityCustomAction) sortedChildNode.getData();
                    action = entityActionScriptService.refreshOrRetrieve(action);
                    if (!guiPosition.equals(action.getGuiPosition())) {
                        action.setGuiPosition(guiPosition);
                        action = entityActionScriptService.update(action);
                        sortedChildNode.setData(action);
                    }

                } else if (sortedChildNode.getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name())) {
                    String childGroupPosition = currentPosition + ";" + GroupedCustomFieldTreeItemType.fieldGroup + ":" + sortedChildNode.getData() + ":"
                            + sortedChildNode.getIndexInParent();
                    for (TreeNode childNode : sortedChildNode.getChildren()) {
                        SortedTreeNode sortedChildChildNode = (SortedTreeNode) childNode;

                        if (sortedChildChildNode.getType().equals(GroupedCustomFieldTreeItemType.field.name())) {
                            String guiPosition = childGroupPosition + ";" + GroupedCustomFieldTreeItemType.field.positionTag + ":" + sortedChildChildNode.getIndexInParent();
                            CustomFieldTemplate cft = (CustomFieldTemplate) sortedChildChildNode.getData();
                            cft = customFieldTemplateService.refreshOrRetrieve(cft);
                            if (!guiPosition.equals(cft.getGuiPosition())) {
                                cft.setGuiPosition(guiPosition);
                                cft = customFieldTemplateService.update(cft);
                                sortedChildChildNode.setData(cft);
                            }

                        } else if (sortedChildChildNode.getType().equals(GroupedCustomFieldTreeItemType.action.name())) {
                            String guiPosition = childGroupPosition + ";" + GroupedCustomFieldTreeItemType.action.positionTag + ":" + sortedChildChildNode.getIndexInParent();
                            EntityCustomAction action = (EntityCustomAction) sortedChildChildNode.getData();
                            action = entityActionScriptService.refreshOrRetrieve(action);
                            if (!guiPosition.equals(action.getGuiPosition())) {
                                action.setGuiPosition(guiPosition);
                                action = entityActionScriptService.update(action);
                                sortedChildChildNode.setData(action);
                            }
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

        public SortedTreeNode(GroupedCustomFieldTreeItemType type, Object data, TreeNode parent, Boolean expanded) {
            super(type.name(), data, parent);
            if (expanded != null && expanded) {
                this.setExpanded(true);
            }
        }

        public String getGuiPositionForField() {

            if (getType().equals(GroupedCustomFieldTreeItemType.tab.name())) {
                return GroupedCustomFieldTreeItemType.tab.positionTag + ":" + getData() + ":" + getParent().getChildren().indexOf(this) + ";"
                        + GroupedCustomFieldTreeItemType.field.positionTag + ":" + getChildCount();

            } else if (getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name())) {
                String guiPosition = GroupedCustomFieldTreeItemType.fieldGroup.positionTag + ":" + getData() + ":" + getParent().getChildren().indexOf(this) + ";"
                        + GroupedCustomFieldTreeItemType.field.positionTag + ":" + getChildCount();
                if (getParent().getType().equals(GroupedCustomFieldTreeItemType.tab.name())) {
                    guiPosition = GroupedCustomFieldTreeItemType.tab.positionTag + ":" + getParent().getData() + ":" + getParent().getParent().getChildren().indexOf(getParent())
                            + ";" + guiPosition;
                }
                return guiPosition;
            }
            return null;
        }

        public String getGuiPositionForAction() {

            if (getType().equals(GroupedCustomFieldTreeItemType.tab.name())) {
                return GroupedCustomFieldTreeItemType.tab.positionTag + ":" + getData() + ":" + getParent().getChildren().indexOf(this) + ";"
                        + GroupedCustomFieldTreeItemType.action.positionTag + ":" + getChildCount();

            } else if (getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name())) {
                String guiPosition = GroupedCustomFieldTreeItemType.fieldGroup.positionTag + ":" + getData() + ":" + getParent().getChildren().indexOf(this) + ";"
                        + GroupedCustomFieldTreeItemType.action.positionTag + ":" + getChildCount();
                if (getParent().getType().equals(GroupedCustomFieldTreeItemType.tab.name())) {
                    guiPosition = GroupedCustomFieldTreeItemType.tab.positionTag + ":" + getParent().getData() + ":" + getParent().getParent().getChildren().indexOf(getParent())
                            + ";" + guiPosition;
                }
                return guiPosition;
            }
            return null;
        }

        public boolean canMoveUp() {
            // Can not move if its is a first item in a tree and nowhere to move
            return !(getIndexInParent() == 0 && (this.getType().equals(GroupedCustomFieldTreeItemType.tab.name())
                    || (this.getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name()) && ((SortedTreeNode) this.getParent()).getIndexInParent() == 0)
                    || ((this.getType().equals(GroupedCustomFieldTreeItemType.field.name()) || this.getType().equals(GroupedCustomFieldTreeItemType.action.name()))
                            && this.getParent().getType().equals(GroupedCustomFieldTreeItemType.tab.name()) && ((SortedTreeNode) this.getParent()).getIndexInParent() == 0)));

        }

        public boolean canMoveDown() {

            return !(isLast() && (this.getType().equals(GroupedCustomFieldTreeItemType.tab.name())
                    || (this.getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name()) && ((SortedTreeNode) this.getParent()).isLast())
                    || ((this.getType().equals(GroupedCustomFieldTreeItemType.field.name()) || this.getType().equals(GroupedCustomFieldTreeItemType.action.name()))
                            && this.getParent().getType().equals(GroupedCustomFieldTreeItemType.tab.name()) && ((SortedTreeNode) this.getParent()).isLast())
                    || (this.getType().equals(GroupedCustomFieldTreeItemType.field.name()) && this.getParent().getType().equals(GroupedCustomFieldTreeItemType.fieldGroup.name())
                            && !((SortedTreeNode) this.getParent()).canMoveDown())));

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

    /**
     * Get applicable field types. For custom tables only some of options are enabled.
     * 
     * @return Applicable field types
     */
    public CustomFieldTypeEnum[] getFieldTypes() {
        if (isCustomTable()) {
            CustomFieldTypeEnum[] enums = new CustomFieldTypeEnum[] { CustomFieldTypeEnum.DATE, CustomFieldTypeEnum.DOUBLE, CustomFieldTypeEnum.LIST, CustomFieldTypeEnum.LONG,
                    CustomFieldTypeEnum.STRING };
            Arrays.sort(enums, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });

            return enums;
        } else {
            return null;
        }
    }
}