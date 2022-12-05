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
