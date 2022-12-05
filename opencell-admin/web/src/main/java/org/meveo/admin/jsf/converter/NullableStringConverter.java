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

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

/**
 * String converter, that converts empty string to null if needed for backend.
 * 
 * (Currently used only in billingAccount email field, because if email is not
 * null validator still validates it as wrong, so it has to be passed as null)
 */
@FacesConverter("nullableStringConverter")
public class NullableStringConverter implements Converter {

	/**
	 * Get the given value as String. In case of an empty String, null is
	 * returned.
	 * 
	 * @param value
	 *            the value of the control
	 * @param facesContext
	 *            current facesContext
	 * @param uiComponent
	 *            the uicomponent providing the value
	 * @return the given value as String. In case of an empty String, null is
	 *         returned.
	 * 
	 * @see jakarta.faces.convert.Converter#getAsObject(jakarta.faces.context.FacesContext,
	 *      jakarta.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {

		if (facesContext == null) {
			throw new NullPointerException("facesContext");
		}
		if (uiComponent == null) {
			throw new NullPointerException("uiComponent");
		}

		return stringToValue(value);
	}

	/**
	 * Convert the String to value (String or null).
	 * 
	 * @param value
	 *            the string from webcomponent
	 * @return the object (null if trimmed String is Empty String)
	 */
	protected Object stringToValue(String value) {

		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				return value + "";
			}
		}
		return null;
	}

	/**
	 * Convert the value to String for web control.
	 * 
	 * @param value
	 *            the value to be set
	 * @param facesContext
	 *            current facesContext
	 * @param uiComponent
	 *            the uicomponent to show the value
	 * @return the String-converted parameter
	 * 
	 * @see jakarta.faces.convert.Converter#getAsString(jakarta.faces.context.FacesContext,
	 *      jakarta.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {

		if (facesContext == null) {
			throw new NullPointerException("facesContext");
		}
		if (uiComponent == null) {
			throw new NullPointerException("uiComponent");
		}
		return valueToString(value);
	}

	/**
	 * Converts the value to HTMLized String.
	 * 
	 * @param value
	 *            the object to be converted
	 * @return String representation
	 */
	protected String valueToString(Object value) {

		if (value == null) {
			return "";
		}
		if (value instanceof String) {
			return (String) value;
		}
		return value + "";
	}
}