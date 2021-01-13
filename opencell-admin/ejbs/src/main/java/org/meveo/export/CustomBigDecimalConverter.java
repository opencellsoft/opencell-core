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

package org.meveo.export;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;

import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;

/**
 * Converts a java.math.BigDecimal to a String, with custom precision.
 * 
 * @author Edward P. Legaspi
 */
public class CustomBigDecimalConverter extends BigDecimalConverter {

	ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return type.equals(BigDecimal.class);
	}

	@Override
	public Object fromString(String str) {
		return new BigDecimal(str);
	}

	@Override
	public String toString(Object obj) {
		return obj == null ? BigDecimal.ZERO.toString() : NumberFormat.getNumberInstance(Locale.ENGLISH).format(new BigDecimal(obj.toString()));
	}

}
