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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;

/**
 * 
 * @author anasseh
 * @created 18.01.2011
 */
@FacesConverter("bigDecimalConverter")
public class BigDecimalConverter implements Converter {

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    private ParamBean paramBean;
    
	private DecimalFormat format = new DecimalFormat("#,##0.00");
	
	@Override
	public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object obj) {
		if (obj == null) {
			return "";
		}

		BigDecimal montant = (BigDecimal) obj;
		String value = getDecimalFormat().format(montant);
		value = value.replace(" ", "");
		value = value.replace("\u00a0", "");
		return value;
	}

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String str) {
		if (str == null || str.equals("")) {
			return null;
		}
		/*if (!str.matches(paramBean.getProperty("bigDecimal.pattern"))) {
			throw new ConverterException(resourceMessages.getString(
					"javax.faces.converter.BigDecimalConverter.DECIMAL_detail"));
		}*/
		str = str.replace(" ", "");
		str = str.replace("\u00a0", "");
		str = str.replace(",", ".");

		return new BigDecimal(str);
	}

	protected DecimalFormat getDecimalFormat() {
		return format;
	}
}