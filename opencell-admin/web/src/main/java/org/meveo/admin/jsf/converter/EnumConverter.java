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

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import org.meveo.commons.utils.EnumBuilder;
import org.meveo.commons.utils.MeveoEnum;

/**
 * Custom enum converter.
 * 
 */
@FacesConverter("enumConverter")
public class EnumConverter implements jakarta.faces.convert.Converter {

	private static final String ATTRIBUTE_ENUM_TYPE = "GenericEnumConverter.enumType";

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && !"".equals(value)) {
			Object enumType = null;
			try {
				enumType = component.getAttributes().get(ATTRIBUTE_ENUM_TYPE);
				if (enumType != null && enumType instanceof String) {
					enumType = Class.forName((String) enumType);
				}
				Class<?> enumClass = (Class) enumType;

				if (enumClass.isAnnotationPresent(MeveoEnum.class)) {
					MeveoEnum meveoEnum = enumClass.getAnnotation(MeveoEnum.class);
					return EnumBuilder.build(value, meveoEnum.identifier());
				}
				return Enum.valueOf((Class<Enum>) enumType, value);
			} catch (IllegalArgumentException e) {
				throw new ConverterException(new FacesMessage("Value is not an enum of type: " + enumType));
			} catch (ClassNotFoundException e) {
				throw new ConverterException(new FacesMessage("Unknown enum classname: " + enumType));
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null && !"".equals(value)) {
			if (value instanceof Enum) {
				component.getAttributes().put(ATTRIBUTE_ENUM_TYPE, value.getClass());
				return ((Enum<?>) value).name();
			} else {
				throw new ConverterException(new FacesMessage("Value is not an enum: " + value.getClass()));
			}
		} else {
			return "";
		}
	}

}