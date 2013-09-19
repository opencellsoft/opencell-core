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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

@FacesConverter("bigDecimal4DigitsConverter")
public class BigDecimal4DigitsConverter extends BigDecimalConverter {

	@Override
	protected DecimalFormat getDecimalFormat() {
		DecimalFormatSymbols decimalFormatSymbol = new DecimalFormatSymbols(FacesContext
				.getCurrentInstance().getViewRoot().getLocale());
		DecimalFormat format = new DecimalFormat("#,##0.0000", decimalFormatSymbol);

		return format;
	}
}