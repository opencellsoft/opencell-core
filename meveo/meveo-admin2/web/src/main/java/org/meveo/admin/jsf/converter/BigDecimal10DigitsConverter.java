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

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * 
 * @author anasseh
 * @created 18.01.2011
 */
@Name("bigDecimal10DigitsConverter")
@org.jboss.seam.annotations.faces.Converter
@BypassInterceptors
public class BigDecimal10DigitsConverter extends BigDecimalConverter {

    private DecimalFormat format = new DecimalFormat("#,##0.0000000000");

    @Override
    protected DecimalFormat getDecimalFormat() {
        return format;
    }
}