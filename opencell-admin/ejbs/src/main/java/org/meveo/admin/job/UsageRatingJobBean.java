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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;

@Stateless
public class UsageRatingJobBean extends IteratorBasedScopedJobBean<Long> {

    private static final long serialVersionUID = 6091764740338888327L;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageRatingService usageRatingService;

    private Date rateUntilDate = null;
    private String ratingGroup = null;
    private boolean hasMore = false;
    private String parameter1 = null;
    private String parameter2 = null;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {

        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::rateEDR, this::rateEDRBatch, this::hasMore, null, null);

        rateUntilDate = null;
        ratingGroup = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return getIterator(jobExecutionResult);
    }

    /**
     * Rate EDR usage
     * 
     * @param edrId EDR id to rate
     * @param jobExecutionResult Job execution result
     */
    private void rateEDR(Long edrId, JobExecutionResultImpl jobExecutionResult) {
        usageRatingService.ratePostpaidUsage(edrId);
    }

    /**
     * Rate EDR usage
     * 
     * @param edrId A list of EDR ids to rate
     * @param jobExecutionResult Job execution result
     */
    private void rateEDRBatch(List<Long> edrIds, JobExecutionResultImpl jobExecutionResult) {

        // In case of no need to rollback when rating fails, an error will be recorded directly in EDR, error will never be thrown and only batch mode will be used
        usageRatingService.ratePostpaidUsage(edrIds);
    }

    private boolean hasMore(JobInstance jobInstance) {
        return hasMore;
    }

    @Override
    protected boolean isProcessItemInNewTx() {
        return false;
    }

    private Optional<Iterator<Long>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        JobInstance jobInstance = jobExecutionResult.getJobInstance();
        rateUntilDate = null;
        ratingGroup = null;
        parameter1 = null;
        parameter2 = null;
        try {
            rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");
            ratingGroup = (String) this.getParamOrCFValue(jobInstance, "ratingGroup");
            parameter1 = (String) this.getParamOrCFValue(jobInstance, "parameter1");
            parameter2 = (String) this.getParamOrCFValue(jobInstance, "parameter2");
        } catch (Exception e) {
            log.warn("Can't get customFields for {}. {}", jobInstance.getJobTemplate(), e.getMessage());
        }

        // Number of EDRs to process in a single job run
        int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("jobs.usageRatingJob.processNrInJobRun", 2000000);

        List<Long> ids = edrService.getEDRsToRate(rateUntilDate, ratingGroup,parameter1,parameter2, jobItemsLimit);
        hasMore = ids.size() == processNrInJobRun;

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    @Override
    Optional<Iterator<Long>> getSynchronizedIteratorWithLimit(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        return getSynchronizedIterator(jobExecutionResult, jobItemsLimit);
    }

    @Override
    Optional<Iterator<Long>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult) {
        return getSynchronizedIterator(jobExecutionResult, 0);
    }
}