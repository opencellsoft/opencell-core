package org.meveo.admin.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ronValidator")
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