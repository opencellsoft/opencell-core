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
