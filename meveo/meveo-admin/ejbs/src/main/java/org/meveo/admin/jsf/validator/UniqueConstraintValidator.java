/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.meveo.model.crm.Provider;
import org.meveo.service.validation.ValidationService;
import org.meveo.service.validation.ValidationServiceLocal;

/**
 * @author Ignas Lelys
 * @created Jan 5, 2011
 * 
 */
@Name("uniqueConstraintValidator")
@org.jboss.seam.annotations.faces.Validator
@BypassInterceptors
public class UniqueConstraintValidator implements Validator {
	private ValidationService validationService;

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        ModelValidator modelValidator = new ModelValidator();
        modelValidator.validate(context, component, value);
        
        Provider currentProvider = (Provider) Component.getInstance("currentProvider");

        String className = (String) component.getAttributes().get("className");
        String fieldName = (String) component.getAttributes().get("fieldName");
        Object id = component.getAttributes().get("idValue");
        if (!validationService.validateUniqueField(className, fieldName, id, value, currentProvider)) {
            FacesMessage facesMessage = new FacesMessage();
            String message = SeamResourceBundle.getBundle().getString("commons.unqueField");
            facesMessage.setDetail(message);
            facesMessage.setSummary(message);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }
}
