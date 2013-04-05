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
package org.meveo.admin.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * 
 * @author Gediminas Ubartas
 * @created 2010.12.10
 */
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
