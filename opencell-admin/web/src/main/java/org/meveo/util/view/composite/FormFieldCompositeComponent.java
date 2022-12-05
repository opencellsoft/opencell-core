/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.util.view.composite;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;

/**
 * Backing UINamingContainer for searchField.xhtml composite component.
 */
@FacesComponent(value = "formField")
public class FormFieldCompositeComponent extends BackingBeanBasedCompositeComponent {

    /**
     * Check if in edit mode in the following order: component's attribute, formPanel's attribute, backing bean's edit property
     * 
     * @return Flag that indicates if edit mode or not
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
