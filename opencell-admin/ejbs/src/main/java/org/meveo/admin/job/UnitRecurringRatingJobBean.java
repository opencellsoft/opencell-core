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

package org.meveo.admin.job;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.RatingStatus;
import org.meveo.model.billing.RatingStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author anasseh
 */

@Stateless
public class UnitRecurringRatingJobBean implements Serializable {

    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long ID_activeRecurringChargeInstance, Date maxDate) {

        log.debug("Running with activeRecurringChargeInstanceID={}", ID_activeRecurringChargeInstance);
        try {
            RatingStatus ratingStatus = recurringChargeInstanceService.applyRecurringCharge(ID_activeRecurringChargeInstance, maxDate);
            if (ratingStatus.getNbRating() == 1) {
                result.registerSucces();
            } else if (ratingStatus.getNbRating() > 1) {
                result.registerWarning(ID_activeRecurringChargeInstance + " rated " + ratingStatus.getNbRating() + " times");
            } else {
                if (ratingStatus.getStatus() != RatingStatusEnum.NOT_RATED_FALSE_FILTER) {
                    result.registerWarning(ID_activeRecurringChargeInstance + " not rated");
                }
            }
        } catch (BusinessException e) {
            result.registerError(ID_activeRecurringChargeInstance, e.getMessage());
        }
    }
}