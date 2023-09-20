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

package org.meveo.jpa;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

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

//    @Inject
//    private Logger log;

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

            /*
            if (emWrapper.isAmp()) {
                allowNesting = emWrapper.isNestingAllowed();
                if (allowNesting) {
                    // log.error("AKK will create a new EM for new TX");
                    emWrapper.newEntityManager(entityManagerProvider.getEntityManager().getEntityManager());
                }
            }
            */

            obj = invocationContext.proceed();

            // Original comment: Re #INTRD-1692 RT job performance improvements
            // emWrapper.getEntityManager().flush();
            // emWrapper.getEntityManager().clear();
            return obj;

        } finally {
            if (allowNesting) {
                emWrapper.popEntityManager();
            }
        }
    }
}
