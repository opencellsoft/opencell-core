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

package org.meveo.admin.jsf.validator;

import java.text.MessageFormat;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 **/
@FacesValidator(value = "codeValidator", managed = true)
public class CodeValidator implements Validator {

    @Inject
    private ResourceBundle resourceMessages;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value != null && !StringUtils.isMatch(value.toString(), ParamBeanFactory.getAppScopeInstance().getProperty("meveo.code.pattern", StringUtils.CODE_REGEX))) {
            FacesMessage facesMessage = new FacesMessage();
            String message = resourceMessages.getString("message.validation.code.pattern");
            message = MessageFormat.format(message, getLabel(context, component), ParamBeanFactory.getAppScopeInstance().getProperty("meveo.code.pattern", StringUtils.CODE_REGEX));
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
