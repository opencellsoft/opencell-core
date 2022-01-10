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

package org.meveo.audit.logging.configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.ejb.Stateless;

import org.meveo.audit.logging.annotations.IgnoreAudit;
import org.meveo.audit.logging.dto.MethodWithParameter;
import org.meveo.commons.utils.ReflectionUtils;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AuditConfigurationProvider {

    public List<Class<?>> getServiceClasses() {
        List<Class<?>> result = new ArrayList<>();

        Set<Class<?>> classes = ReflectionUtils.getClassesAnnotatedWith("org.meveo.service", Stateless.class, Stateful.class);
        for (Class<?> clazz : classes) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                result.add(clazz);
            }
        }

        Collections.sort(result, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> lhs, Class<?> rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        return result;
    }

    public List<MethodWithParameter> getMethods(Class<?> clazz) {
        List<MethodWithParameter> result = new ArrayList<>();
        for (Method m : clazz.getMethods()) {
            if (!m.isAnnotationPresent(IgnoreAudit.class) && Modifier.isPublic(m.getModifiers())) {
                MethodWithParameter methodWithParameter = new MethodWithParameter(m.getName());
                result.add(methodWithParameter);
            }
        }

        Collections.sort(result, new Comparator<MethodWithParameter>() {
            @Override
            public int compare(MethodWithParameter o1, MethodWithParameter o2) {
                return o1.getMethodName().compareTo(o2.getMethodName());
            }
        });

        return result;
    }

}
