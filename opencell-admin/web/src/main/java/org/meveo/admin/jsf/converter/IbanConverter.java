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

package org.meveo.admin.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.jfree.util.Log;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;

/**
 * 
 * @author Hatim OUDAD
 *
 */
@FacesConverter(value = "ibanConverter", managed = true)
public class IbanConverter implements Converter {

	@Inject
	protected ParamBeanFactory paramBeanFactory;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return value;
	}

	/**
	 * The 4 First caracters of Iban and the 2 last are masked by X if iban masking
	 * is activated in opencell-admin.properties
	 */
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String iban = value.toString();
		int ibanLength = iban.length();
		if (iban != null && isMaskingIbanEnabled() && ibanLength > 6) {
			String fisrtCaracters = iban.substring(0, 4);
			String lastCaracters = iban.substring(ibanLength - 2, ibanLength);
			return fisrtCaracters + new String(new char[ibanLength - 6]).replace("\0", "X") + lastCaracters;
		} else {
			Log.info("the iban masking is disabled");
			return iban;
		}
	}

	/**
	 * Check in application configuration the status of maskIban property. If false,
	 * iban is not masked. defaultValue is false.
	 * 
	 * @return boolean
	 */
	public boolean isMaskingIbanEnabled() {

		ParamBean paramBean = paramBeanFactory.getInstance();
		boolean statusMasking = Boolean.parseBoolean(paramBean.getProperty("opencell.maskIban", "false").toLowerCase());
		return statusMasking;

	}

}
