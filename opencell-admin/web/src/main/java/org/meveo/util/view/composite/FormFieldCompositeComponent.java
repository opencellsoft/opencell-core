package org.meveo.util.view.composite;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;

/**
 * Backing UINamingContainer for searchField.xhtml composite component.
 */
@FacesComponent(value = "formField")
public class FormFieldCompositeComponent extends BackingBeanBasedCompositeComponent {

    /**
     * Check if in edit mode in the following order: component's attribute, formPanel's attribute, backing bean's edit property
     * 
     * @return
     */
    public boolean isFieldEdit() {

        if (getAttributes().containsKey("edit")) {
            if (getAttributes().get("edit") instanceof String) {
                return Boolean.parseBoolean((String) getAttributes().get("edit"));
            } else {
                return (boolean) getAttributes().get("edit");
            }
        }

        UIComponent parent = getCompositeComponentParent(this);
        while (parent != null) {
            if (parent instanceof FormPanelCompositeComponent) {
                if (parent.getAttributes().containsKey("edit")) {

                    if (parent.getAttributes().get("edit") instanceof String) {
                        return Boolean.parseBoolean((String) parent.getAttributes().get("edit"));
                    } else {
                        return (boolean) parent.getAttributes().get("edit");
                    }
                }
                break;
            }
            
            parent = getCompositeComponentParent(parent);
        }
        try {
            return getBackingBeanFromParentOrCurrent().isEdit();
        } catch (Exception e){
            log.error("Failed to access backing bean for field {} {}", getAttributes().get("field"), getAttributes().get("childField"), e);
            throw e;
        }
    }
}
