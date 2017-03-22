package org.meveo.admin.jsf.validator;

import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.meveo.commons.utils.StringUtils;
import org.omnifaces.util.Faces;
import org.primefaces.validate.ClientValidator;

/**
 * @author Edward P. Legaspi
 **/
@FacesValidator("emailValidator")
public class EmailValidator implements Validator, ClientValidator {

	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages",
				Faces.getLocale());

		if (StringUtils.isBlank(value)) {
			return;
		} else {
			try {
				InternetAddress emailAddr = new InternetAddress(
						value.toString());
				emailAddr.validate();
			} catch (AddressException ex) {
				String message = resourceBundle
						.getString("user.error.invalidEmail");
				// check if faces messages has already an invalid email message
				FacesMessage facesMessage = new FacesMessage();
				message = MessageFormat.format(message,
						getLabel(context, component));
				facesMessage.setDetail(message);
				facesMessage.setSummary(message);
				facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);

				boolean match = false;
				for (FacesMessage fm : context.getMessageList()) {
					if (fm.getDetail().equals(message)
							|| fm.getSummary().equals(message)) {
						match = true;
						break;
					}
				}

				if (!match) {
					throw new ValidatorException(facesMessage);
				}
			}
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

	@Override
	public Map<String, Object> getMetadata() {
		return null;
	}

	@Override
	public String getValidatorId() {
		return "emailValidator";
	}

}