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
package org.meveo.admin.job.logging;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Amine BEN AICHA
 **/
@Interceptor
public class JobMultithreadingHistoryInterceptor implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7768369529533486722L;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @EJB
    private JobExecutionService jobExecutionService;

    @AroundInvoke
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object jobExecutionResultUpdate(InvocationContext invocationContext) throws Exception {

        try {
            return invocationContext.proceed();
        } finally {

            Object[] parameters = invocationContext.getParameters();

            for (Object parameter : parameters) {

                if (parameter instanceof JobExecutionResultImpl) {
                    JobExecutionResultImpl executionResult = (JobExecutionResultImpl) parameter;
                    synchronized (executionResult) {
                        JobExecutionResultImpl updateEntity = jobExecutionService.findById(executionResult.getId());
                        if (updateEntity != null) {
                            JobExecutionResultImpl.updateFromInterface(executionResult, updateEntity);
                            jobExecutionService.update(updateEntity);
                            log.debug("Job execution result updated");
                            break;
                        }
                    }
                }
            }
        }
    }
}
