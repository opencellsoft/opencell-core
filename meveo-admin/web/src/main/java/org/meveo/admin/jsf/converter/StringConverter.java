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

@FacesConverter("stringConverter")
public class StringConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	
	    return (value != null && value.trim().length() != 0) ? convert(value) : null;
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object o) {
		
	    if (o == null)
			return null;

		if (o instanceof String)
			return (String) o;
		else
			throw new IllegalArgumentException(o.getClass().getName()
					+ " is not supported for this converter");
	}

	public String convert(String value) {
		return convert(value, false);
	}

	public String convert(String value, boolean searchPattern) {
		if (value == null)
			return null;

		// Remplacement
		value = value.toLowerCase().trim();
		value = value.replaceAll("[àâä]", "a");
		value = value.replaceAll("[éèêë]", "e");
		value = value.replaceAll("[îï]", "i");
		value = value.replaceAll("[ôö]", "o");
		value = value.replaceAll("[ûüù]", "u");
		value = value.replaceAll("[ç]", "c");

		// Suppression
		if (!searchPattern)
			value = value.replaceAll("[^a-z0-9@\\-\\&\\_ ]", " ");
		else
			value = value.replaceAll("[^a-z0-9@\\-\\&\\_\\* ]", " ");

		// Uppercase
		return value.toUpperCase();
	}
}
