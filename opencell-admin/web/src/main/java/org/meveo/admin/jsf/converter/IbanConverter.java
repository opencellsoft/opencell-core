package org.meveo.admin.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;

@FacesConverter("ibanConverter")
public class IbanConverter implements Converter {
	
	@Inject
    protected ParamBeanFactory paramBeanFactory;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return value;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String iban = value.toString();
		if (iban != null && isMaskingIbanEnabled()) {
			int ibanLength = iban.length();
			String fisrtCaracters = iban.substring(0, 4);
			String lastCaracters = iban.substring(ibanLength - 2, ibanLength);
			return fisrtCaracters + new String(new char[ibanLength - 6]).replace("\0", "X") + lastCaracters;
		} else {
			return iban;
		}
	}
	
	 /**
     * Check in application configuration the status of maskIban property. If false, iban is not masked. defaultValue is false.
     * @return boolean
     */
    public boolean isMaskingIbanEnabled() {
    	
    	ParamBean paramBean = paramBeanFactory.getInstance();
        boolean statusMasking = Boolean.parseBoolean(paramBean.getProperty("opencell.maskIban", "false").toLowerCase());
        return statusMasking;
    	
    }


}
