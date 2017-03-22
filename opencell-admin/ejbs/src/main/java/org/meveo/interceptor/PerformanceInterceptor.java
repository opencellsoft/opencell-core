package org.meveo.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
public class PerformanceInterceptor {

	private Logger log = LoggerFactory.getLogger(PerformanceInterceptor.class);

	@AroundInvoke
	Object measureTime(InvocationContext ctx) throws Exception {
		long beforeTime = System.nanoTime();
		Object obj = null;
		try {
			obj = ctx.proceed();
			return obj;
		} finally {
			long time = System.nanoTime() - beforeTime;
			log.info("{}.{} total runTime={}", new Object[] { ctx.getClass(), ctx.getMethod(), time });
		}
	}

}
