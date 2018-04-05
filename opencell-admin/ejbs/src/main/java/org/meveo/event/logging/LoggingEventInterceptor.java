package org.meveo.event.logging;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
@LoggedEvent
@Interceptor
public class LoggingEventInterceptor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception {
        log.debug("\r\n\r\n===========================================================");
        log.debug("Entering method: {} in class {}", invocationContext.getMethod().getName().toUpperCase(), invocationContext.getMethod().getDeclaringClass().getName());

        if (invocationContext.getParameters() != null) {
            for (Object obj : invocationContext.getParameters()) {
                log.debug("{}", obj);
            }
        }

        return invocationContext.proceed();
    }

}
