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

package org.meveo.audit.logging.core;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.CustomMeveoAudit;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.audit.logging.custom.CustomAuditManagerService;

/**
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 * @lastModifiedVersion 7.2
 **/
@MeveoAudit
@Interceptor
public class AuditInterceptor implements Serializable {

	private static final long serialVersionUID = -6043606820916354437L;

	@Inject
	private AuditManagerService auditManagerService;

	/**
	 * A CustomAuditManagerService instance
	 */
	@Inject
	@CustomMeveoAudit
	Instance<CustomAuditManagerService> customAuditManagerServiceInstance;

	/**
	 * Before method invocation.
	 * 
	 * @param joinPoint
	 *            the join point
	 * @return the object
	 * @throws Throwable
	 *             the throwable
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@AroundInvoke
	public Object before(InvocationContext joinPoint) throws Throwable {
		// check if method is in our list
		Class clazz = joinPoint.getTarget().getClass();
		if (AuditContext.getInstance().getAuditConfiguration().isEnabled() && AuditContext.getInstance()
				.getAuditConfiguration().isMethodLoggable(clazz.getName(), joinPoint.getMethod().getName())) {
			auditManagerService.audit(clazz, joinPoint.getMethod(), joinPoint.getParameters());
			customAuditManagerServiceInstance.forEach(customManagerService -> {
				try {
					customManagerService.audit(clazz, joinPoint.getMethod(), joinPoint.getParameters());
				} catch (BusinessException e) {
					throw new RuntimeException(e);
				}

			});
		}

		return joinPoint.proceed();
	}
}
