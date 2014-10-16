/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.jsf.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("bigDecimalConverter")
public class BigDecimalConverter implements Converter {

	private DecimalFormat format = new DecimalFormat("#,##0.00");

	@Override
	public String getAsString(FacesContext facesContext,
			UIComponent uIComponent, Object obj) {
		if (obj == null || obj.toString().length() == 0) {
			return "";
		}

		BigDecimal montant = (BigDecimal) obj;
		String value = getDecimalFormat().format(montant);
		value = value.replace(" ", "");
		value = value.replace("\u00a0", "");
		return value;
	}

	@Override
	public Object getAsObject(FacesContext facesContext,
			UIComponent uIComponent, String str) {
		if (str == null || str.equals("")) {
			return null;
		}
		/*
		 * if (!str.matches(paramBean.getProperty("bigDecimal.pattern"))) {
		 * throw new ConverterException(resourceMessages.getString(
		 * "javax.faces.converter.BigDecimalConverter.DECIMAL_detail")); }
		 */
		str = str.replace(" ", "");
		str = str.replace("\u00a0", "");
		str = str.replace(",", ".");

		return new BigDecimal(str);
	}

	protected DecimalFormat getDecimalFormat() {
		return format;
	}
}