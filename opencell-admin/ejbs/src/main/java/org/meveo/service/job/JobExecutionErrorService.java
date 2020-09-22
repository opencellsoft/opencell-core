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
package org.meveo.service.job;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.DatePeriod;
import org.meveo.model.jobs.JobExecutionError;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.PersistenceService;

/**
 * Persistence service for JobExecutionError entity
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 10.0
 * 
 */
@Stateless
public class JobExecutionErrorService extends PersistenceService<JobExecutionError> {

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registerJobError(JobInstance jobInstance, Map<String, Object> errorContext, Exception e) {

        JobExecutionError jobError = new JobExecutionError();
        jobError.setJobInstance(jobInstance);
        String errorReason = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        if (errorReason.length() > 2000) {
            errorReason = errorReason.substring(0, 2000);
        }
        jobError.setErrorReason(errorReason);

        if (errorContext.containsKey(BusinessException.ErrorContextAttributeEnum.RATING_PERIOD.name())) {
            jobError.setPeriodFrom(((DatePeriod) errorContext.get(BusinessException.ErrorContextAttributeEnum.RATING_PERIOD.name())).getFrom());
            jobError.setPeriodTo(((DatePeriod) errorContext.get(BusinessException.ErrorContextAttributeEnum.RATING_PERIOD.name())).getTo());
        }

        if (errorContext.containsKey(BusinessException.ErrorContextAttributeEnum.CHARGE_INSTANCE.name())) {
            jobError.setEntityId((Long) errorContext.get(BusinessException.ErrorContextAttributeEnum.CHARGE_INSTANCE.name()));
        }

        getEntityManager().persist(jobError);
    }

    /**
     * Clear job execution errors of a given job instance
     * 
     * @param jobInstance Job instance
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void purgeJobErrors(JobInstance jobInstance) {

        getEntityManager().createNamedQuery("JobExecutionError.purgeByJobInstance").setParameter("jobInstance", jobInstance).executeUpdate();
    }
}