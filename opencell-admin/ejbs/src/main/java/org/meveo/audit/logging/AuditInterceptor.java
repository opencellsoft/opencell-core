package org.meveo.audit.logging;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.meveo.audit.logging.annotations.MeveoAudit;

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
	@AroundInvoke
	public Object before(InvocationContext joinPoint) throws Throwable {
		auditManagerService.audit(joinPoint.getTarget().getClass(), joinPoint.getMethod(), joinPoint.getParameters());
		return joinPoint.proceed();
	}
}
