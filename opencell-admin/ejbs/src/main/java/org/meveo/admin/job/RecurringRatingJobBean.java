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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RatingStatus;
import org.meveo.model.billing.RatingStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;

/**
 * Job implementation to apply recurring charges for next billing cycle
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class RecurringRatingJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private BillingCycleService billingCycleService;

    private Date rateUntilDate = null;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::createRecurringCharges, null, null);
        rateUntilDate = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of recurring charge instances to create recurring charges for
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        rateUntilDate = null;
        try {
            rateUntilDate = (Date) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "rateUntilDate");
        } catch (Exception e) {
            log.warn("Cant get customFields for " + jobExecutionResult.getJobInstance().getJobTemplate(), e.getMessage());
        }
        if (rateUntilDate == null) {
            String rateUntilDateEL = (String) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "rateUntilDateEL");
            if (rateUntilDateEL != null) {
                rateUntilDate = ValueExpressionWrapper.evaluateExpression(rateUntilDateEL, null, Date.class);
            }
        }

        if (rateUntilDate == null) {
            rateUntilDate = new Date();
        }

        // Resolve billing cycles from CF value
        List<BillingCycle> billingCycles = null;
        List<EntityReferenceWrapper> billingCycleReferences = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "rateBC");
        if (billingCycleReferences != null && !billingCycleReferences.isEmpty()) {
            billingCycles = billingCycleService.findByCodes(billingCycleReferences.stream().map(er -> er.getCode()).collect(Collectors.toList()));
        }

        List<Long> ids = recurringChargeInstanceService.findRecurringChargeInstancesToRate(InstanceStatusEnum.ACTIVE, rateUntilDate, billingCycles);

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    /**
     * Create recurring charges
     * 
     * @param chargeInstanceId Recurring charge instance id
     * @param jobExecutionResult Job execution result
     */
    private void createRecurringCharges(Long chargeInstanceId, JobExecutionResultImpl jobExecutionResult) throws BusinessException {

        RatingStatus ratingStatus = recurringChargeInstanceService.applyRecurringCharge(chargeInstanceId, rateUntilDate, false);
        if (ratingStatus.getNbRating() == 1) {
            // jobExecutionResult.registerSucces();

        } else if (ratingStatus.getNbRating() > 1) {
            jobExecutionResult.unRegisterSucces(); // Reduce success as success is added automatically in main loop of IteratorBasedJobBean
            jobExecutionResult.registerWarning(chargeInstanceId + " rated " + ratingStatus.getNbRating() + " times");

        } else {
            if (ratingStatus.getStatus() != RatingStatusEnum.NOT_RATED_FALSE_FILTER) {
                jobExecutionResult.unRegisterSucces(); // Reduce success as success is added automatically in main loop of IteratorBasedJobBean
                jobExecutionResult.registerWarning(chargeInstanceId + " not rated");
            }
        }
    }
}