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

package org.meveo.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.meveo.audit.logging.configuration.AuditConfigurationProvider;
import org.meveo.audit.logging.core.AuditContext;
import org.meveo.audit.logging.dto.ClassAndMethods;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
public class GenericTest {

    private static final Logger log = LoggerFactory.getLogger(GenericTest.class);

    public static void main(String args[]) {
        new GenericTest();
    }

    public GenericTest() {
        try {
            writeLogConfig();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("error = {}", e);
        }
    }

    private void writerOfferAndMethod() {
        for (Method m : OfferTemplateService.class.getMethods()) {
            System.out.println(m.getParameterTypes());
        }
    }

    private void testGetMethod() {
        for (Method m : OfferTemplateService.class.getMethods()) {
            System.out.println(m.getName() + " " + m.getParameterTypes().length + " " + ReflectionUtils.isMethodImplemented(OfferTemplateService.class, m.getName(), m.getParameterTypes()));
            // System.out.println(m.getName() + " " +
            // ReflectionUtils.isMethodOverrriden(m));
            // clazz.getMethod(name).getDeclaringClass().equals(clazz);
        }

    }

    private void writeLogConfig() throws IOException {
        AuditContext ac = new AuditContext();
        ac.init();
        ClassAndMethods cm = new ClassAndMethods();
        cm.setClassName(OfferTemplateService.class.getName());
        cm.getMethods().add("findByServiceTemplate");
        cm.getMethods().add("create");
        ac.getAuditConfiguration().getClasses().add(cm);
        ac.saveConfiguration();
    }

    private void writeAllClassesAndMethods() {
        AuditConfigurationProvider x = new AuditConfigurationProvider();
        List<Class<?>> y = x.getServiceClasses();
        for (Class a : y) {
            System.out.println(a.getName());
            for (Method m : a.getMethods()) {
                System.out.println(m.toString());
            }
        }
    }

}
