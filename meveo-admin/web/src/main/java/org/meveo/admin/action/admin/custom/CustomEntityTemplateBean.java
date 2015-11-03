package org.meveo.admin.action.admin.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named
@ViewScoped
public class CustomEntityTemplateBean extends BaseBean<CustomEntityTemplate> {

    private static final long serialVersionUID = 1187554162639618526L;

    private static String TYPE_ROOT = "root";
    private static String TYPE_TAB = "tab";
    private static String TYPE_FIELD_GROUP = "fieldGroup";
    private static String TYPE_FIELD = "field";

    /**
     * Request parameter
     */
    private String entityClassName;

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

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private JobInstanceService jobInstanceService;

    public CustomEntityTemplateBean() {
        super(CustomEntityTemplate.class);
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {

        if (entityClassName != null) {
            int pos = entityClassName.indexOf("$$");
            if (pos > 0) {
                entityClassName = entityClassName.substring(0, pos);
            }
        }

        this.entityClassName = entityClassName;
    }

    @Override
    protected CustomEntityTemplateService getPersistenceService() {
        return customEntityTemplateService;
    }

    public boolean isCustomEntityTemplate() {
        return entityClassName == null || CustomEntityTemplate.class.getName().equals(entityClassName);
    }

    @SuppressWarnings("unchecked")
    public CustomizedEntity getCustomizedEntity() throws ClassNotFoundException {

        if (customizedEntity == null && entityClassName != null && !CustomEntityTemplate.class.getName().equals(entityClassName)) {
            entityClass = Class.forName(entityClassName);
            customizedEntity = new CustomizedEntity(entityClass.getSimpleName(), entityClass, null, null);
            if (Job.class.isAssignableFrom(entityClass)) {
                cetPrefix = "JOB_" + entityClass.getSimpleName();

                // Check and instantiate missing custom field templates for a given job
                Job job = jobInstanceService.getJobByName(entityClass.getSimpleName());
                Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();

                // Create missing custom field templates if needed
                customFieldTemplateService.createMissingTemplates(cetPrefix, jobCustomFields.values(), getCurrentProvider());

            } else {

                cetPrefix = ((CustomFieldEntity) entityClass.getAnnotation(CustomFieldEntity.class)).cftCodePrefix();
            }
        }

        return customizedEntity;
    }

    public TreeNode getFields() {
        if (groupedFields != null || cetPrefix == null) {
            return groupedFields;
        }

        groupedFields = new SortedTreeNode(TYPE_ROOT, "Root", null, null);
        groupedFields.setExpanded(true);

        List<CustomFieldTemplate> fields = customFieldTemplateService.findByAppliesTo(cetPrefix, getCurrentProvider());

        TreeNode defaultTab = groupedFields;
        defaultTab = new SortedTreeNode(TYPE_TAB, CustomEntityTemplate.class.isAssignableFrom(entityClass) ? entity.getCode() : "Custom fields", groupedFields, null);
        defaultTab.setExpanded(true);

        Map<String, TreeNode> groupingNodes = new HashMap<String, TreeNode>();

        for (CustomFieldTemplate field : fields) {

            // Add field to a tree
            if (StringUtils.isBlank(field.getGuiPosition())) {
                new SortedTreeNode(TYPE_FIELD, field, defaultTab, null);
                continue;
            }

            Map<String, String> guiPositionParsed = field.getGuiPositionParsed();

            // Add missing grouping nodes to a tree
            String tabName = guiPositionParsed.get(CustomFieldTemplate.POSITION_TAB + "_name");
            String fieldGroupName = guiPositionParsed.get(CustomFieldTemplate.POSITION_FIELD_GROUP + "_name");
            String positionKey = tabName + "_" + fieldGroupName;
            if (!groupingNodes.containsKey(positionKey)) {

                if (tabName != null) {
                    TreeNode tabNode = groupingNodes.get(tabName + "_null");

                    // Add tab
                    if (tabNode == null) {
                        tabNode = new SortedTreeNode(TYPE_TAB, tabName, groupedFields, guiPositionParsed.get(CustomFieldTemplate.POSITION_TAB + "_pos"));
                        tabNode.setExpanded(true);
                        groupingNodes.put(tabName + "_null", tabNode);
                    }

                    // Add field group
                    if (fieldGroupName != null) {
                        TreeNode fieldGroupNode = new SortedTreeNode(TYPE_FIELD_GROUP, fieldGroupName, tabNode, guiPositionParsed.get(CustomFieldTemplate.POSITION_FIELD_GROUP
                                + "_pos"));
                        fieldGroupNode.setExpanded(true);
                        groupingNodes.put(positionKey, fieldGroupNode);

                    }

                    // Fieldgroup is supported only under a tab, so add field to a default tab
                } else if (fieldGroupName != null) {
                    // TreeNode fieldGroupNode = new SortedTreeNode(TYPE_FIELD_GROUP, fieldGroupName, defaultTab, guiPositionParsed.get(CustomFieldTemplate.POSITION_FIELD_GROUP
                    // + "_pos"));
                    // fieldGroupNode.setExpanded(true);
                    // groupingNodes.put(positionKey, fieldGroupNode);

                    new SortedTreeNode(TYPE_FIELD, field, defaultTab, guiPositionParsed.get(CustomFieldTemplate.POSITION_FIELD + "_pos"));
                    continue;
                }
            }

            // Add field to a tree
            TreeNode fieldParentNode = groupingNodes.get(positionKey);
            new SortedTreeNode(TYPE_FIELD, field, fieldParentNode, guiPositionParsed.get(CustomFieldTemplate.POSITION_FIELD + "_pos"));
        }

        // Remove default node if it is empty and there are more nodes
        if (defaultTab.getChildCount() == 0 && groupedFields.getChildCount() > 1) {
            groupedFields.getChildren().remove(defaultTab);
        }

        // Sort tree by position
        groupedFields.sort();

        return groupedFields;
    }

    public void refreshFields() {
        groupedFields = null;
    }

    public void setSelectedFieldGrouping(TreeNode selectedFieldGrouping) {
        this.selectedFieldGrouping = selectedFieldGrouping;
    }

    public TreeNode getSelectedFieldGrouping() {
        return selectedFieldGrouping;
    }

    @Override
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
            cetPrefix = entity.getCFTPrefix();
            return cetPrefix;
        }
        return null;
    }

    public void newTab() {
        selectedFieldGrouping = new SortedTreeNode(TYPE_TAB, "", groupedFields, Integer.toString(groupedFields.getChildCount()));
        selectedFieldGrouping.setExpanded(true);
    }

    public void newFieldGroup(TreeNode parentNode) {
        selectedFieldGrouping = new SortedTreeNode(TYPE_FIELD_GROUP, "", parentNode, Integer.toString(parentNode.getChildCount()));
        selectedFieldGrouping.setExpanded(true);
    }

    public void saveUpdateFieldGrouping() {

        updateFieldGuiPositionValue((SortedTreeNode) selectedFieldGrouping);
    }

    public void cancelFieldGrouping() {
        if (StringUtils.isBlank((String) selectedFieldGrouping.getData())) {
            selectedFieldGrouping.getParent().getChildren().remove(selectedFieldGrouping);
        }
    }

    public void removeFieldGrouping() {

        for (TreeNode childNode : selectedFieldGrouping.getChildren()) {
            if (childNode.getType().equals(TYPE_FIELD)) {
                customFieldTemplateService.remove(((CustomFieldTemplate) childNode.getData()).getId());
            } else if (childNode.getType().equals(TYPE_FIELD_GROUP)) {
                for (TreeNode childChildNode : childNode.getChildren()) {
                    customFieldTemplateService.remove((CustomFieldTemplate) childChildNode.getData());
                }
            }
        }

        selectedFieldGrouping.getParent().getChildren().remove(selectedFieldGrouping);

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

        updateFieldGuiPositionValue((SortedTreeNode) node.getParent());

    }

    public void moveDown(SortedTreeNode node) {

        int currentIndex = node.getIndexInParent();
        boolean isLast = node.isLast();

        // Move a position down within the same branch
        if (!isLast) {
            TreeNode parent = node.getParent();
            if (node.getType().equals(TYPE_FIELD)) {
                SortedTreeNode siblingDown = node.getSiblingDown();
                if (siblingDown != null && !siblingDown.getType().equals(TYPE_FIELD)) {
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

                if (parentSibling.getType().equals(TYPE_FIELD)) {
                    parentSibling.getParent().getChildren().add(parentSibling.getIndexInParent(), node);
                } else {
                    parentSibling.getChildren().add(0, node);
                }
            }
        }

        updateFieldGuiPositionValue((SortedTreeNode) node.getParent());
    }

    private void updateFieldGuiPositionValue(SortedTreeNode nodeToUpdate) {

        List<TreeNode> nodes = nodeToUpdate.getChildren();
        if (!nodeToUpdate.getType().equals(TYPE_ROOT)) {
            nodes = new ArrayList<TreeNode>();
            nodes.add(nodeToUpdate);
        }

        for (TreeNode treeNode : nodes) {
            SortedTreeNode sortedNode = (SortedTreeNode) treeNode;

            String currentPosition = null;
            if (sortedNode.getType().equals(TYPE_TAB)) {
                currentPosition = CustomFieldTemplate.POSITION_TAB + ":" + sortedNode.getData() + ":" + sortedNode.getIndexInParent();

            } else if (sortedNode.getType().equals(TYPE_FIELD_GROUP)) {
                if (!sortedNode.getParent().getType().equals(TYPE_ROOT)) {
                    currentPosition = CustomFieldTemplate.POSITION_TAB + ":" + sortedNode.getParent().getData() + ":"
                            + ((SortedTreeNode) sortedNode.getParent()).getIndexInParent() + ";";
                }
                currentPosition = currentPosition + CustomFieldTemplate.POSITION_FIELD_GROUP + ":" + sortedNode.getData() + ":" + sortedNode.getIndexInParent();

            }
            for (TreeNode node : sortedNode.getChildren()) {
                SortedTreeNode sortedChildNode = (SortedTreeNode) node;
                if (sortedChildNode.getType().equals(TYPE_FIELD)) {
                    String guiPosition = currentPosition + ";" + CustomFieldTemplate.POSITION_FIELD + ":" + sortedChildNode.getIndexInParent();
                    CustomFieldTemplate cft = (CustomFieldTemplate) sortedChildNode.getData();
                    if (!guiPosition.equals(cft.getGuiPosition())) {
                        cft.setGuiPosition(guiPosition);
                        cft = customFieldTemplateService.update(cft);
                        sortedChildNode.setData(cft);
                    }

                } else if (sortedChildNode.getType().equals(TYPE_FIELD_GROUP)) {
                    String childGroupPosition = currentPosition + ";" + TYPE_FIELD_GROUP + ":" + sortedChildNode.getData() + ":" + sortedChildNode.getIndexInParent();
                    for (TreeNode childNode : sortedChildNode.getChildren()) {
                        SortedTreeNode sortedChildChildNode = (SortedTreeNode) childNode;
                        String guiPosition = childGroupPosition + ";" + CustomFieldTemplate.POSITION_FIELD + ":" + sortedChildChildNode.getIndexInParent();
                        CustomFieldTemplate cft = (CustomFieldTemplate) sortedChildChildNode.getData();
                        if (!guiPosition.equals(cft.getGuiPosition())) {
                            cft.setGuiPosition(guiPosition);
                            cft = customFieldTemplateService.update(cft);
                            sortedChildChildNode.setData(cft);
                        }
                    }
                }
            }
        }
    }

    public class SortedTreeNode extends DefaultTreeNode {

        private static final long serialVersionUID = 3694377290046737073L;

        int position;

        protected int getPosition() {
            return position;
        }

        public SortedTreeNode() {
            super();
        }

        public SortedTreeNode(String type, Object data, TreeNode parent, String position) {
            super(type, data, parent);
            if (position == null) {
                this.position = 0;
            } else {
                this.position = Integer.parseInt(position);
            }
        }

        public void sort() {
            if (getChildCount() > 0) {

                List<TreeNode> childrenNodes = getChildren();
                Collections.sort(childrenNodes, new Comparator<TreeNode>() {

                    @Override
                    public int compare(TreeNode o1, TreeNode o2) {
                        return ((SortedTreeNode) o1).getPosition() - ((SortedTreeNode) o2).getPosition();
                    }
                });

                // this.setChildren(childrenNodes);
                for (TreeNode treeNode : childrenNodes) {
                    treeNode.setParent(this);
                    ((SortedTreeNode) treeNode).sort();
                }
            }
        }

        public String getGuiPositionForField() {

            if (getType().equals(TYPE_TAB)) {
                return CustomFieldTemplate.POSITION_TAB + ":" + getData() + ":" + getParent().getChildren().indexOf(this) + ";field:" + getChildCount();

            } else if (getType().equals(TYPE_FIELD_GROUP)) {
                String guiPosition = CustomFieldTemplate.POSITION_FIELD_GROUP + ":" + getData() + ":" + getParent().getChildren().indexOf(this) + ";field:" + getChildCount();
                if (getParent().getType().equals(TYPE_TAB)) {
                    guiPosition = CustomFieldTemplate.POSITION_TAB + ":" + getParent().getData() + ":" + getParent().getParent().getChildren().indexOf(getParent()) + ";"
                            + guiPosition;
                }
                return guiPosition;
            }
            return null;
        }

        public boolean canMoveUp() {
            // Can not move if its is a first item in a tree and nowhere to move
            return !(getIndexInParent() == 0 && (this.getType().equals(TYPE_TAB)
                    || (this.getType().equals(TYPE_FIELD_GROUP) && ((SortedTreeNode) this.getParent()).getIndexInParent() == 0) || (this.getType().equals(TYPE_FIELD)
                    && this.getParent().getType().equals(TYPE_TAB) && ((SortedTreeNode) this.getParent()).getIndexInParent() == 0)));

        }

        public boolean canMoveDown() {

            return !(isLast() && (this.getType().equals(TYPE_TAB) || (this.getType().equals(TYPE_FIELD_GROUP) && ((SortedTreeNode) this.getParent()).isLast())
                    || (this.getType().equals(TYPE_FIELD) && this.getParent().getType().equals(TYPE_TAB) && ((SortedTreeNode) this.getParent()).isLast()) || (this.getType()
                .equals(TYPE_FIELD) && this.getParent().getType().equals(TYPE_FIELD_GROUP) && !((SortedTreeNode) this.getParent()).canMoveDown())));

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