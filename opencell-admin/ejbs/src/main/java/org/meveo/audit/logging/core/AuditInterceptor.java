package org.meveo.audit.logging.core;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.meveo.audit.logging.annotations.MeveoAudit;

/**
 * @author Edward P. Legaspi
 **/
@MeveoAudit
@Interceptor
public class AuditInterceptor implements Serializable {

	private static final long serialVersionUID = -6043606820916354437L;

	@Inject
	private AuditManagerService auditManagerService;

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
		}

		return joinPoint.proceed();
	}
}
