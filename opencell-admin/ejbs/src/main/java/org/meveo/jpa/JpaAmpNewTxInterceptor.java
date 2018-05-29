package org.meveo.jpa;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

/**
 * Interceptor that in case of application managed persistence context, a new EM will be instantiated for the period of a method call
 * 
 * @author Andrius Karpavicius
 */
@JpaAmpNewTx
@Interceptor
public class JpaAmpNewTxInterceptor implements Serializable {

    private static final long serialVersionUID = -7397037942696135998L;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private EntityManagerProvider entityManagerProvider;

    @Inject
    private Logger log;

    /**
     * Instantiate a new EM if EM is application managed persistence context
     * 
     * @param invocationContext Method invocation context
     * @return Method invocation result
     * @throws Exception General exception
     */
    @AroundInvoke
    public Object createNewTx(InvocationContext invocationContext) throws Exception {

        Object obj = null;
        boolean allowNesting = false;
        try {

            if (emWrapper.isAmp()) {
                allowNesting = emWrapper.isNestingAllowed();
                if (allowNesting) {
                    // log.error("AKK will create a new EM for new TX");
                    emWrapper.newEntityManager(entityManagerProvider.getEntityManager().getEntityManager());
                }
            }
            obj = invocationContext.proceed();
            return obj;

        } finally {
            if (allowNesting) {
                emWrapper.popEntityManager();
            }
        }
    }
}
