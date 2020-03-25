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

import javax.ejb.Stateless;

import org.meveo.audit.logging.annotations.IgnoreAudit;
import org.meveo.audit.logging.dto.MethodWithParameter;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.reflections.Reflections;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AuditConfigurationProvider {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Class<? extends IPersistenceService>> getServiceClasses() {
		List<Class<? extends IPersistenceService>> result = new ArrayList<>();

		Reflections reflections = new Reflections("org.meveo.service");
		List<Class<? extends IPersistenceService>> classes = new ArrayList(
				reflections.getSubTypesOf(IPersistenceService.class));
		for (Class<? extends IPersistenceService> clazz : classes) {
			if (!Modifier.isAbstract(clazz.getModifiers())) {
				result.add(clazz);
			}
		}

		Collections.sort(result, new Comparator<Class<? extends IPersistenceService>>() {
			@Override
			public int compare(Class<? extends IPersistenceService> lhs, Class<? extends IPersistenceService> rhs) {
				return lhs.getName().compareTo(rhs.getName());
			}
		});

		return result;
	}

	@SuppressWarnings("rawtypes")
	public List<MethodWithParameter> getMethods(Class<? extends IPersistenceService> clazz) {
		List<MethodWithParameter> result = new ArrayList<>();
		for (Method m : clazz.getMethods()) {
			if (!m.isAnnotationPresent(IgnoreAudit.class)
					&& ReflectionUtils.isMethodImplemented(clazz, m.getName(), m.getParameterTypes())) {
				MethodWithParameter methodWithParameter = new MethodWithParameter(m.getName());
				if (!result.contains(methodWithParameter)) {
					result.add(methodWithParameter);
				}
			}
		}
		
		// get PersistenceService public methods
		Method[] allMethods = PersistenceService.class.getDeclaredMethods();
		for (Method m : allMethods) {
		    if (Modifier.isPublic(m.getModifiers())) {
				MethodWithParameter methodWithParameter = new MethodWithParameter(m.getName());
				if (!result.contains(methodWithParameter)) {
					result.add(methodWithParameter);
				}
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
