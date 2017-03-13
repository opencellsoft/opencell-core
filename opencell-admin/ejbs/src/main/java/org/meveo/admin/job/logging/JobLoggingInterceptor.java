package org.meveo.admin.job.logging;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
@JobLogged
@Interceptor
public class JobLoggingInterceptor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8509493016090519457L;
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@AroundInvoke
	public Object aroundInvoke(InvocationContext invocationContext)
			throws Exception {
		log.debug("\r\n\r\n===========================================================");
		log.debug("Entering method: "
				+ invocationContext.getMethod().getName().toUpperCase()
				+ " in class "
				+ invocationContext.getMethod().getDeclaringClass().getName());

		return invocationContext.proceed();
	}

}
