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

import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Xstream converter for hibernate proxy classes. For classes, that need to be serialised into a value of ID only - directly output an ID value. For others - retrieve an entity
 * from a hibernate proxy and send it for processing to another converter.
 * 
 * @author Andrius Karpavicius
 * 
 */
public class IEntityHibernateProxyConverter implements Converter {

    private ExportImportConfig exportImportConfig;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public IEntityHibernateProxyConverter(ExportImportConfig exportImportConfig) {
        this.exportImportConfig = exportImportConfig;
    }

    @SuppressWarnings("rawtypes")
    public boolean canConvert(final Class clazz) {

        // be responsible for Hibernate proxy.
        boolean willConvert = HibernateProxy.class.isAssignableFrom(clazz);
        if (willConvert) {
            log.debug("Will be using " + this.getClass().getSimpleName() + " for " + clazz);
        }
        return willConvert;
    }

    /**
     * For classes, that need to be serialised into a value of ID only - directly output an ID value. For others - retrieve an entity from a hibernate proxy and send it for
     * processing to another converter.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void marshal(final Object object, final HierarchicalStreamWriter writer, final MarshallingContext context) {

        Class baseClass = ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass();

        // Export directly ID value if instructed so
        if (exportImportConfig.isExportIdOnly(baseClass)) {
            writer.addAttribute("id", ((HibernateProxy) object).getHibernateLazyInitializer().getIdentifier().toString());

            // Retrieve an entity from a hibernate proxy and send it for processing to another converter.
        } else {
            Object item = ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
            context.convertAnother(item);
        }
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        throw new ConversionException("Cannot deserialize Hibernate proxy");
    }
}