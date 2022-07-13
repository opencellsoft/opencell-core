package org.meveo.interceptor;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.model.IEntity;

@ConcurrencyLock
@Interceptor
public class ConcurrencyLockInterceptor implements Serializable {

    private static final long serialVersionUID = -671251230091797949L;

    @AroundInvoke
    Object lockMethod(InvocationContext ctx) throws Exception {

        ConcurrencyLock lockConfig = ctx.getMethod().getAnnotation(ConcurrencyLock.class);

        Long lockBy = null;
        if (ctx.getParameters().length > lockConfig.lockParameter()) {

            Object parameterValue = ctx.getParameters()[lockConfig.lockParameter()];

            if (parameterValue instanceof Long) {
                lockBy = (Long) parameterValue;
            } else if (parameterValue instanceof IEntity) {
                lockBy = (Long) ((IEntity) parameterValue).getId();
            }
        }

        if (lockBy == null) {
            return ctx.proceed();
        }

        return MethodCallingUtils.executeFunctionLocked(lockBy, () -> ctx.proceed());

    }
}
