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

import java.math.BigDecimal;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.convert.ByteConverter;
import javax.faces.convert.Converter;
import javax.faces.convert.DoubleConverter;
import javax.faces.convert.FloatConverter;
import javax.faces.convert.IntegerConverter;
import javax.faces.convert.LongConverter;
import javax.faces.convert.ShortConverter;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.StringUtils;

@Named
public class GetConverter {

	@Inject
	private BeanManager beanManager;

	/**
	 * Gets converter for type and by parameter.
	 * 
	 * @param obj
	 *            Obj for which converter is searched.
	 * 
	 * @return Converter.
	 */
	public Converter forType(Object obj) {
		return forType(obj, null);
	}

	/**
	 * Gets converter for type and by parameter.
	 * 
	 * @param param parameter
	 * @return converter.
	 */
	@SuppressWarnings("unchecked")
	public Converter forParam(String param) {
		if ("date".equals(param)) {

			Bean<DateConverter> bean = (Bean<DateConverter>) beanManager
					.getBeans(DateConverter.class).iterator().next();
			CreationalContext<DateConverter> ctx = beanManager
					.createCreationalContext(bean);
			return (DateConverter) beanManager.getReference(bean,
					DateConverter.class, ctx);

		} else if ("dateTime".equals(param)) {

			Bean<DateTimeConverter> bean = (Bean<DateTimeConverter>) beanManager
					.getBeans(DateTimeConverter.class).iterator().next();
			CreationalContext<DateTimeConverter> ctx = beanManager
					.createCreationalContext(bean);
			return (DateTimeConverter) beanManager.getReference(bean,
					DateTimeConverter.class, ctx);

		}

		return null;
	}

	/**
	 * Gets converter for type and by parameter.
	 * 
	 * @param obj
	 *            Obj for which converter is searched.
	 * @param param
	 *            Parameter that can be used for finding out converter.
	 * 
	 * @return Converter.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Converter forType(Object obj, String param) {

		if (obj == null) {
			return null;
		}

		// log.debug("Getting converter={} for class={}", param,
		// obj.getClass());

        if (StringUtils.isBlank(param)) {

            Class type = obj.getClass();

            if (type == BigDecimal.class) {
                Bean<BigDecimalConverter> bean = (Bean<BigDecimalConverter>) beanManager.getBeans(BigDecimalConverter.class).iterator().next();
                CreationalContext<BigDecimalConverter> ctx = beanManager.createCreationalContext(bean);
                return (BigDecimalConverter) beanManager.getReference(bean, BigDecimalConverter.class, ctx);

            } else if (type == Integer.class || (type.isPrimitive() && type.getName().equals("int"))) {
                return new IntegerConverter();

            } else if (type == Long.class || (type.isPrimitive() && type.getName().equals("long"))) {
                return new LongConverter();

            } else if (type == Byte.class || (type.isPrimitive() && type.getName().equals("byte"))) {
                return new ByteConverter();

            } else if (type == Short.class || (type.isPrimitive() && type.getName().equals("short"))) {
                return new ShortConverter();

            } else if (type == Double.class || (type.isPrimitive() && type.getName().equals("double"))) {
                return new DoubleConverter();

            } else if (type == Float.class || (type.isPrimitive() && type.getName().equals("float"))) {
                return new FloatConverter();
            }
            
		} else if ("4digits".equals(param)
				&& obj.getClass() == BigDecimal.class) {

			Bean<BigDecimal4DigitsConverter> bean = (Bean<BigDecimal4DigitsConverter>) beanManager
					.getBeans(BigDecimal4DigitsConverter.class).iterator()
					.next();
			CreationalContext<BigDecimal4DigitsConverter> ctx = beanManager
					.createCreationalContext(bean);
			return (BigDecimal4DigitsConverter) beanManager.getReference(bean,
					BigDecimal4DigitsConverter.class, ctx);

		} else if ("10digits".equals(param)
				&& obj.getClass() == BigDecimal.class) {

			Bean<BigDecimal10DigitsConverter> bean = (Bean<BigDecimal10DigitsConverter>) beanManager
					.getBeans(BigDecimal10DigitsConverter.class).iterator()
					.next();
			CreationalContext<BigDecimal10DigitsConverter> ctx = beanManager
					.createCreationalContext(bean);
			return (BigDecimal10DigitsConverter) beanManager.getReference(bean,
					BigDecimal10DigitsConverter.class, ctx);

		} else if ("12digits".equals(param)
				&& obj.getClass() == BigDecimal.class) {

			Bean<BigDecimal12DigitsConverter> bean = (Bean<BigDecimal12DigitsConverter>) beanManager
					.getBeans(BigDecimal12DigitsConverter.class).iterator()
					.next();
			CreationalContext<BigDecimal12DigitsConverter> ctx = beanManager
					.createCreationalContext(bean);
			return (BigDecimal12DigitsConverter) beanManager.getReference(bean,
					BigDecimal12DigitsConverter.class, ctx);

		} else if ("date".equals(param)) {

			Bean<DateConverter> bean = (Bean<DateConverter>) beanManager
					.getBeans(DateConverter.class).iterator().next();
			CreationalContext<DateConverter> ctx = beanManager
					.createCreationalContext(bean);
			return (DateConverter) beanManager.getReference(bean,
					DateConverter.class, ctx);

		}else if("customFieldAppliesTo".equals(param)){
			Bean<CustomFieldAppliesToConverter> bean = (Bean<CustomFieldAppliesToConverter>) beanManager
					.getBeans(CustomFieldAppliesToConverter.class).iterator().next();
			CreationalContext<CustomFieldAppliesToConverter> ctx = beanManager
					.createCreationalContext(bean);
			return (CustomFieldAppliesToConverter) beanManager.getReference(bean,
					CustomFieldAppliesToConverter.class, ctx);
		}
		return null;
	}
}
