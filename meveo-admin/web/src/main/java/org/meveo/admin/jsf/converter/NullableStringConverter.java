/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

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
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent, java.lang.String)
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
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent, java.lang.Object)
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