/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.jsf.validator;

import java.text.MessageFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.seam.security.Identity;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.crm.Provider;
import org.meveo.security.MeveoUser;
import org.meveo.service.validation.ValidationService;

@FacesValidator("uniqueConstraintValidator")
public class UniqueConstraintValidator implements Validator {
    @Inject
    private ValidationService validationService;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    private Identity identity;

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        /*
         * TODO: ModelValidator modelValidator = new ModelValidator(); modelValidator.validate(context, component, value);
         */

        String className = (String) component.getAttributes().get("className");
        String fieldName = (String) component.getAttributes().get("fieldName");
        Object id = component.getAttributes().get("idValue");
        
        Provider currentProvider=((MeveoUser)identity.getUser()).getCurrentProvider();

        if (!validationService.validateUniqueField(className, fieldName, id, value, currentProvider)) {
            FacesMessage facesMessage = new FacesMessage();
            String message = resourceMessages.getString("commons.unqueField");
            message = MessageFormat.format(message, getLabel(context, component));
            facesMessage.setDetail(message);
            facesMessage.setSummary(message);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(facesMessage);
        }

    }

    private Object getLabel(FacesContext context, UIComponent component) {

        Object o = component.getAttributes().get("label");
        if (o == null || (o instanceof String && ((String) o).length() == 0)) {
            o = component.getValueExpression("label");
        }
        // Use the "clientId" if there was no label specified.
        if (o == null) {
            o = component.getClientId(context);
        }
        return o;
    }

}
