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
import javax.persistence.EntityManager;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.commons.utils.ParamBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.RatingResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.job.Job;

@Stateless
public class UsageRatingNoRollbackJobBean extends IteratorBasedJobBean<EDR> {

    private static final long serialVersionUID = 6091764740338888327L;

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    private Date rateUntilDate = null;
    private String ratingGroup = null;
    private boolean hasMore = false;
    private StatelessSession statelessSession;
    private ScrollableResults scrollableResults;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {

        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, this::rateEDRBatch, this::hasMore, null, null);

        rateUntilDate = null;
        ratingGroup = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    private Optional<Iterator<EDR>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        rateUntilDate = null;
        ratingGroup = null;
        try {
            rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");
            ratingGroup = (String) this.getParamOrCFValue(jobInstance, "ratingGroup");
        } catch (Exception e) {
            log.warn("Can't get customFields for {}. {}", jobInstance.getJobTemplate(), e.getMessage());
        }

        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 10000L);
        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        int fetchSize = batchSize.intValue() * nbThreads.intValue();

        // Number of EDRs to process in a single job run
        int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("usageRatingJob.processNrInJobRun", 2000000);

        EntityManager em = emWrapper.getEntityManager();

        Object[] convertSummary = null;

        if (rateUntilDate == null && ratingGroup == null) {
            convertSummary = (Object[]) em.createNamedQuery("EDR.getListToRateSummary").getSingleResult();

        } else if (rateUntilDate != null && ratingGroup == null) {
            convertSummary = (Object[]) em.createNamedQuery("EDR.getListToRateLimitByDateSummary").setParameter("rateUntilDate", rateUntilDate).getSingleResult();

        } else if (rateUntilDate == null && ratingGroup != null) {
            convertSummary = (Object[]) em.createNamedQuery("EDR.getListToRateLimitByRGSummary").setParameter("ratingGroup", ratingGroup).getSingleResult();

        } else {
            convertSummary = (Object[]) em.createNamedQuery("EDR.getListToRateLimitByDateAndRGSummary").setParameter("rateUntilDate", rateUntilDate).setParameter("ratingGroup", ratingGroup).getSingleResult();
        }

        Long nrOfRecords = (Long) convertSummary[0];
        Long maxId = (Long) convertSummary[1];

        if (nrOfRecords.intValue() == 0) {
            return Optional.empty();
        }

        statelessSession = emWrapper.getEntityManager().unwrap(Session.class).getSessionFactory().openStatelessSession();

        if (rateUntilDate == null && ratingGroup == null) {
            scrollableResults = statelessSession.createNamedQuery("EDR.listToRate").setParameter("maxId", maxId).setReadOnly(true).setCacheable(false).setMaxResults(processNrInJobRun).setFetchSize(fetchSize)
                .scroll(ScrollMode.FORWARD_ONLY);

        } else if (rateUntilDate != null && ratingGroup == null) {
            scrollableResults = statelessSession.createNamedQuery("EDR.listToRateLimitByDate").setParameter("rateUntilDate", rateUntilDate).setParameter("maxId", maxId).setReadOnly(true).setCacheable(false)
                .setMaxResults(processNrInJobRun).setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

        } else if (rateUntilDate == null && ratingGroup != null) {
            scrollableResults = statelessSession.createNamedQuery("EDR.listToRateLimitByRG").setParameter("ratingGroup", ratingGroup).setParameter("maxId", maxId).setReadOnly(true).setCacheable(false)
                .setMaxResults(processNrInJobRun).setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

        } else {
            scrollableResults = statelessSession.createNamedQuery("EDR.listToRateLimitByDateAndRG").setParameter("rateUntilDate", rateUntilDate).setParameter("ratingGroup", ratingGroup).setParameter("maxId", maxId)
                .setReadOnly(true).setCacheable(false).setMaxResults(processNrInJobRun).setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);
        }

        hasMore = nrOfRecords >= processNrInJobRun;

        return Optional.of(new SynchronizedIterator<EDR>(scrollableResults, nrOfRecords.intValue()));
    }

    /**
     * Rate EDR usage
     * 
     * @param edrs A list of EDRs to rate
     * @param jobExecutionResult Job execution result
     */
    private void rateEDRBatch(List<EDR> edrs, JobExecutionResultImpl jobExecutionResult) {

        for (EDR edr : edrs) {
            RatingResult ratingResult = usageRatingService.rateUsage(edr, false, false, 0, 0, null, true);
            if (ratingResult.getRatingException() != null) {
                jobExecutionResult.registerError(edr.getId(), edr.getRejectReason());
            } else {
                jobExecutionResult.registerSucces();
            }
        }
    }

    private boolean hasMore(JobInstance jobInstance) {
        return hasMore;
    }

    @Override
    protected boolean isProcessItemInNewTx() {
        return true;
    }

    @Override
    protected boolean isProcessMultipleItemFunctionUpdateProgress() {
        return true;
    }
}