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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.StringUtils;

@Named
public class GetConverter {

    @Inject
    BeanManager beanManager;

    /**
     * Gets converter for type and by parameter.
     * 
     * @param obj Obj for which converter is searched.
     * 
     * @return Converter.
     */
    public Converter forType(Object obj) {
        return forType(obj, null);
    }

    /**
     * Gets converter for type and by parameter.
     * 
     * @param obj Obj for which converter is searched.
     * @param param Parameter that can be used for finding out converter.
     * 
     * @return Converter.
     */
    @SuppressWarnings("unchecked")
    public Converter forType(Object obj, String param) {

        if (obj == null) {
            return null;
        }

        if (StringUtils.isBlank(param) && obj.getClass() == BigDecimal.class) {

            Bean<BigDecimalConverter> bean = (Bean<BigDecimalConverter>) beanManager.getBeans(BigDecimalConverter.class).iterator().next();
            CreationalContext<BigDecimalConverter> ctx = beanManager.createCreationalContext(bean);
            return (BigDecimalConverter) beanManager.getReference(bean, BigDecimalConverter.class, ctx);

        } else if ("4digits".equals(param) && obj.getClass() == BigDecimal.class) {

            Bean<BigDecimal4DigitsConverter> bean = (Bean<BigDecimal4DigitsConverter>) beanManager.getBeans(BigDecimal4DigitsConverter.class).iterator().next();
            CreationalContext<BigDecimal4DigitsConverter> ctx = beanManager.createCreationalContext(bean);
            return (BigDecimal4DigitsConverter) beanManager.getReference(bean, BigDecimal4DigitsConverter.class, ctx);

        } else if ("10digits".equals(param) && obj.getClass() == BigDecimal.class) {

            Bean<BigDecimal10DigitsConverter> bean = (Bean<BigDecimal10DigitsConverter>) beanManager.getBeans(BigDecimal10DigitsConverter.class).iterator().next();
            CreationalContext<BigDecimal10DigitsConverter> ctx = beanManager.createCreationalContext(bean);
            return (BigDecimal10DigitsConverter) beanManager.getReference(bean, BigDecimal10DigitsConverter.class, ctx);

        }
        return null;
    }
}
