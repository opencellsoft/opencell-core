package org.meveo.admin.action.admin.custom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTemplate.GroupedCustomFieldTreeItemType;
import org.meveo.model.crm.custom.CustomFieldValueHolder;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.service.base.ValueExpressionWrapper;

public class GroupedCustomField implements Serializable {

    private static final long serialVersionUID = 5027554537383208719L;

    private GroupedCustomFieldTreeItemType type;

    private Object data;

    private List<GroupedCustomField> children = new ArrayList<GroupedCustomField>();

    private int position;

    private Collection<CustomFieldTemplate> fields = null;
    private Boolean hasVisibleCustomFields = null;

    public GroupedCustomField(GroupedCustomFieldTreeItemType type, Object data, GroupedCustomField parent, String position) {
        this.type = type;

        if (type == GroupedCustomFieldTreeItemType.tab || type == GroupedCustomFieldTreeItemType.fieldGroup) {
            data = new TranslatableLabel((String) data);
        }

        this.data = data;

        if (position == null) {
            this.position = 0;
        } else {
            this.position = Integer.parseInt(position);
        }
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    public GroupedCustomField(Collection<CustomFieldTemplate> fields, String defaultTabLabel, boolean leaveDefaultTab) {

        this(GroupedCustomFieldTreeItemType.root, "Root", null, null);

        if (fields == null) {
            fields = new ArrayList<CustomFieldTemplate>();
        }
        this.fields = fields;

        Map<String, GroupedCustomField> groupingNodes = new HashMap<String, GroupedCustomField>();
        GroupedCustomField defaultTab = new GroupedCustomField(GroupedCustomFieldTreeItemType.tab, defaultTabLabel, this, null);
        groupingNodes.put(defaultTabLabel + "_null", defaultTab);

        for (CustomFieldTemplate field : fields) {

            // Add field to a tree
            if (StringUtils.isBlank(field.getGuiPosition())) {
                new GroupedCustomField(GroupedCustomFieldTreeItemType.field, field, defaultTab, null);
                continue;
            }

            Map<String, String> guiPositionParsed = field.getGuiPositionParsed();

            // Add missing grouping nodes to a tree
            String tabName = guiPositionParsed.get(GroupedCustomFieldTreeItemType.tab.positionTag + "_name");
            String fieldGroupName = guiPositionParsed.get(GroupedCustomFieldTreeItemType.fieldGroup.positionTag + "_name");
            String positionKey = tabName + "_" + fieldGroupName;
            if (!groupingNodes.containsKey(positionKey)) {

                if (tabName != null) {
                    GroupedCustomField tabNode = groupingNodes.get(tabName + "_null");

                    // Add tab
                    if (tabNode == null) {
                        tabNode = new GroupedCustomField(GroupedCustomFieldTreeItemType.tab, tabName, this,
                            guiPositionParsed.get(GroupedCustomFieldTreeItemType.tab.positionTag + "_pos"));
                        groupingNodes.put(tabName + "_null", tabNode);
                    }

                    // Add field group
                    if (fieldGroupName != null) {
                        GroupedCustomField fieldGroupNode = new GroupedCustomField(GroupedCustomFieldTreeItemType.fieldGroup, fieldGroupName, tabNode,
                            guiPositionParsed.get(GroupedCustomFieldTreeItemType.fieldGroup.positionTag + "_pos"));
                        groupingNodes.put(positionKey, fieldGroupNode);

                    }

                    // Fieldgroup is supported only under a tab, so add field to
                    // a default tab
                } else if (fieldGroupName != null) {

                    new GroupedCustomField(GroupedCustomFieldTreeItemType.field, field, defaultTab,
                        guiPositionParsed.get(GroupedCustomFieldTreeItemType.field.positionTag + "_pos"));
                    continue;
                }
            }

            // Add field to a tree
            GroupedCustomField fieldParentNode = groupingNodes.get(positionKey);
            new GroupedCustomField(GroupedCustomFieldTreeItemType.field, field, fieldParentNode, guiPositionParsed.get(GroupedCustomFieldTreeItemType.field.positionTag + "_pos"));
        }

        // Remove default node if it is empty and there are more nodes
        if (defaultTab.getChildCount() == 0 && (!leaveDefaultTab || (leaveDefaultTab && this.getChildCount() > 1))) {
            this.getChildren().remove(defaultTab);
        }

        sort();
    }

    /**
     * Find a tab matching a given name in a root tree node
     * 
     * @param name Tab name
     */
    private GroupedCustomField findTab(String name) {
        if (getType() != GroupedCustomFieldTreeItemType.root) {
            return null;
        }
        for (GroupedCustomField groupedCustomField : children) {
            if (groupedCustomField.getType() == GroupedCustomFieldTreeItemType.tab && groupedCustomField.getData().equals(name)) {
                return groupedCustomField;
            }
        }
        return null;
    }

    /**
     * Find a field group matching a given name in a tab tree node
     * 
     * @param name Field group name
     */
    private GroupedCustomField findFieldGroup(String name) {
        if (getType() != GroupedCustomFieldTreeItemType.tab) {
            return null;
        }
        for (GroupedCustomField groupedCustomField : children) {
            if (groupedCustomField.getType() == GroupedCustomFieldTreeItemType.fieldGroup && groupedCustomField.getData().equals(name)) {
                return groupedCustomField;
            }
        }
        return null;
    }

    /**
     * Append custom actions to grouped custom fields
     * 
     * @param actions Actions to append
     */
    public void append(Collection<EntityCustomAction> actions) {

        if (actions == null || actions.isEmpty()) {
            return;
        }

        for (EntityCustomAction action : actions) {

            // Add action to a tree that does belong to any tab
            if (StringUtils.isBlank(action.getGuiPosition())) {
                new GroupedCustomField(GroupedCustomFieldTreeItemType.action, action, this, null);
                continue;
            }

            Map<String, String> guiPositionParsed = action.getGuiPositionParsed();

            // Add missing grouping nodes to a tree
            String tabName = guiPositionParsed.get(GroupedCustomFieldTreeItemType.tab.positionTag + "_name");
            String fieldGroupName = guiPositionParsed.get(GroupedCustomFieldTreeItemType.fieldGroup.positionTag + "_name");

            GroupedCustomField actionParentNode = this;
            if (tabName != null) {
                GroupedCustomField tabNode = findTab(tabName);
                if (tabNode == null) {
                    tabNode = new GroupedCustomField(GroupedCustomFieldTreeItemType.tab, tabName, this,
                        guiPositionParsed.get(GroupedCustomFieldTreeItemType.tab.positionTag + "_pos"));
                }
                actionParentNode = tabNode;

                if (fieldGroupName != null) {
                    GroupedCustomField fieldGroupNode = findFieldGroup(fieldGroupName);
                    if (fieldGroupNode == null) {
                        fieldGroupNode = new GroupedCustomField(GroupedCustomFieldTreeItemType.fieldGroup, fieldGroupName, tabNode,
                            guiPositionParsed.get(GroupedCustomFieldTreeItemType.fieldGroup.positionTag + "_pos"));
                    }
                    actionParentNode = fieldGroupNode;
                }
            }

            // Add action to a tree
            new GroupedCustomField(GroupedCustomFieldTreeItemType.action, action, actionParentNode,
                guiPositionParsed.get(GroupedCustomFieldTreeItemType.action.positionTag + "_pos"));
        }

        sort();
    }

    public GroupedCustomFieldTreeItemType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<GroupedCustomField> getChildren() {
        return children;
    }

    public int getChildCount() {
        return children.size();

    }

    protected int getPosition() {
        return position;
    }

    private void sort() {
        if (getChildCount() > 0) {

            Collections.sort(children, new Comparator<GroupedCustomField>() {

                @Override
                public int compare(GroupedCustomField o1, GroupedCustomField o2) {
                    return ((GroupedCustomField) o1).getPosition() - ((GroupedCustomField) o2).getPosition();
                }
            });

            for (GroupedCustomField child : children) {
                child.sort();
            }
        }
    }

    public Collection<CustomFieldTemplate> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("GroupedCustomField [type=%s, data=%s, children=%s]", type, data, children != null ? children.subList(0, Math.min(children.size(), maxLen)) : null);
    }

    public boolean hasVisibleCustomFields(ICustomFieldEntity entity, CustomFieldValueHolder cfValueHolder) {
        if (getChildCount() == 0) {
            return false;
        }

        if (hasVisibleCustomFields != null) {
            return hasVisibleCustomFields;
        }

        hasVisibleCustomFields = false;

        boolean newEntity = ((IEntity) entity).isTransient();
        for (GroupedCustomField cfFieldOrOrg : getChildren()) {
            if (cfFieldOrOrg.getType().equals(GroupedCustomFieldTreeItemType.field)) {
                CustomFieldTemplate cft = (CustomFieldTemplate) cfFieldOrOrg.getData();
                try {
                    if (!cft.isHideInGUI() && (!cft.isDisabled() || (cft.isDisabled() && !cfValueHolder.isAnyFieldEmptyForGui(cft)))
                            && (!newEntity || (newEntity && !cft.isHideOnNew()))
                            && ValueExpressionWrapper.evaluateToBooleanOneVariable(cft.getApplicableOnEl(), "entity", entity)) {
                        hasVisibleCustomFields = true;
                        return hasVisibleCustomFields;
                    }
                } catch (BusinessException e) {
                }
            } else if (cfFieldOrOrg.getType().equals(GroupedCustomFieldTreeItemType.fieldGroup)) {
                for (GroupedCustomField cfField : cfFieldOrOrg.getChildren()) {
                    CustomFieldTemplate cft = (CustomFieldTemplate) cfField.getData();
                    try {
                        if (!cft.isHideInGUI() && (!cft.isDisabled() || (cft.isDisabled() && !cfValueHolder.isAnyFieldEmptyForGui(cft)))
                                && (!newEntity || (newEntity && !cft.isHideOnNew()))
                                && ValueExpressionWrapper.evaluateToBooleanOneVariable(cft.getApplicableOnEl(), "entity", entity)) {
                            hasVisibleCustomFields = true;
                            return hasVisibleCustomFields;
                        }
                    } catch (BusinessException e) {
                    }
                }
            }
        }

        return hasVisibleCustomFields;
    }
}