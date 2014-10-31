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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.meveo.commons.utils.ParamBean;

@FacesConverter("dateConverter")
public class DateConverter implements Converter {


    private ParamBean paramBean=ParamBean.getInstance();

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String str) {
		String dateFormat = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");
		DateFormat df = new SimpleDateFormat(dateFormat, FacesContext.getCurrentInstance()
				.getViewRoot().getLocale());

		try {
			return df.parse(str);
		} catch (ParseException e) {
			return "";
		}
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object obj) {
		String dateFormat = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");
		DateFormat df = new SimpleDateFormat(dateFormat, FacesContext.getCurrentInstance()
				.getViewRoot().getLocale());

		return df.format(obj);
	}

}
