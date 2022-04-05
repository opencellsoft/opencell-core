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

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.meveo.model.IAuditable;
import org.meveo.model.IEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.cpq.Product;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Inheritance;
import java.lang.reflect.Modifier;


public class DefaultStatusConverter extends ReflectionConverter {

    private Logger log = LoggerFactory.getLogger(this.getClass());



    public DefaultStatusConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        super(mapper, reflectionProvider);
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public boolean canConvert(Class clazz) {


        return Product.class.isAssignableFrom(clazz) ||
                OfferTemplate.class.isAssignableFrom(clazz) ||
                ProductOffering.class.isAssignableFrom(clazz);
    }

    /**
     * Append class name when serialising an abstract or inheritance class' implementation
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void marshal(Object original, final HierarchicalStreamWriter writer, final MarshallingContext context) {

        if (original instanceof Product) {

            writer.addAttribute("status","DRAFT");
        }else if (original instanceof  ProductOffering || original instanceof OfferTemplate) {
            writer.addAttribute("status","IN_DESIGN");
        }
        super.marshal(original, writer, context);
    }


}