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
package org.meveo.commons.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Utils class for working with jaxb.
 * 
 * @author anasseh
 * 
 */
public class JAXBUtils {

    @SuppressWarnings("rawtypes")
    public static Object unmarshaller(Class clazz, File file) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return unmarshaller.unmarshal(file);
    }

    /**
     * @param obj object to marshall
     * @param file file contains the result
     * @throws JAXBException jaxb exception.
     */
    public static void marshaller(Object obj, File file) throws JAXBException {
        marshaller(obj, file, null);
    }

    /**
     * @param obj object to marshal
     * @param file file contains the result
     * @param schemaLocation schema location
     * @throws JAXBException jaxb exception.
     */
    public static void marshaller(Object obj, File file, String schemaLocation) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        if (schemaLocation != null) {
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
        }
        marshaller.marshal(obj, file);

    }
}
