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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.meveo.audit.logging.dto.ClassAndMethods;
import org.meveo.audit.logging.handler.ConsoleAuditHandler;
import org.meveo.audit.logging.handler.Handler;
import org.meveo.audit.logging.layout.Layout;
import org.meveo.audit.logging.layout.SimpleLayout;

/**
 * @author Edward P. Legaspi
 **/
public class AuditConfiguration {

	private boolean enabled;
	private Layout layout;
	private List<Handler> handlers = new ArrayList<>();
	private List<ClassAndMethods> classes = new ArrayList<>();

	public void init() {
		setEnabled(false);
		getHandlers().add(new ConsoleAuditHandler());
		setLayout(new SimpleLayout());
		setClasses(new ArrayList<>());
	}

	public ClassAndMethods findByClassName(String className) {
		if (classes == null) {
			return null;
		}

		for (ClassAndMethods cm : classes) {
			if (cm.getClassName().equals(className)) {
				return cm;
			}
		}

		return null;
	}

	public boolean isMethodLoggable(String className, String methodName) {
		ClassAndMethods cm = findByClassName(className);
		if (cm == null) {
			return false;
		}

		if (cm.getMethods() != null) {
			for (String m : cm.getMethods()) {
				m = StringUtils.substringBetween(m, "methodName=", "]");
				if (m != null && m.equals(methodName)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public List<Handler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<Handler> handlers) {
		this.handlers = handlers;
	}

	public List<ClassAndMethods> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassAndMethods> classes) {
		this.classes = classes;
	}

}
