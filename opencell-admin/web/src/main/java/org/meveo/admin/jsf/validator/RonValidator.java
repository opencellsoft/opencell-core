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

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;

@Named("ronValidator")
public class RonValidator implements Validator{

    public RonValidator(){
    }

    @Override
    public void validate(FacesContext context, UIComponent component,
                         Object value) throws ValidatorException {

        boolean resultOk = true;

        FacesMessage msg = null;

        FacesMessage msg1 =
                new FacesMessage("Format validation failed.",
                        "Invalid format.");
        msg1.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesMessage msg2 =
                new FacesMessage("Invalid range of numbers.",
                        "Invalid range of numbers.");
        msg2.setSeverity(FacesMessage.SEVERITY_ERROR);

        try {

            if (value == null){
                resultOk = false;
            } else {

                String ronValue = value.toString();
                ronValue = ronValue.trim();

                if (ronValue.lastIndexOf("<") != ronValue.indexOf("<")) {
                    resultOk = false;
                    msg = msg1;
                } else {
                    String[] parts = ronValue.split("<");
                    if ((parts != null) && (parts.length == 2)) {
                        if (!(ronValue.startsWith("<"))) {
                            Double parts0 = Double.valueOf(parts[0]);
                            Double parts1 = Double.valueOf(parts[1]);
                            if (parts0 >= parts1) {
                                resultOk = false;
                                msg = msg2;
                            }
                        }
                    } else {
                        if (!(  (parts != null) && (parts.length == 1) && (ronValue.endsWith("<") ) ) ) {
                            resultOk = false;
                            msg = msg1;
                        }

                    }
                }
            }


        } catch (NumberFormatException nfe) {
            resultOk = false;
        }

        if (!resultOk) {
            throw new ValidatorException(msg);
        }

    }
}