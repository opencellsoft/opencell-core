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

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.meveo.admin.async.SynchronizedIteratorGrouped;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.audit.AuditDataLogRecord;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.audit.logging.AuditDataLogService;
import org.meveo.service.job.Job;

/**
 * A job implementation to convert Open Wallet operations to Rated transactions
 * 
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 */
@Stateless
public class AuditDataLogAggregationJobBean extends IteratorBasedJobBean<List<AuditDataLogRecord>> {

    private static final long serialVersionUID = -5529560027721628734L;

    @Inject
    private AuditDataLogService auditDataLogService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    private StatelessSession statelessSession;
    private ScrollableResults scrollableResults;

    private Long maxId = null;
    private Long nrOfRecords = null;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, this::aggregateAuditDataLogs, this::hasMore, this::closeResultset, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    private Optional<Iterator<List<AuditDataLogRecord>>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 1000L);
        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        int fetchSize = batchSize.intValue() * nbThreads.intValue();

        Object[] convertSummary = (Object[]) emWrapper.getEntityManager().createNamedQuery("AuditDataLogRecord.getConvertToAggregateSummary").getSingleResult();

        nrOfRecords = ((BigInteger) convertSummary[0]).longValue();

        if (nrOfRecords.intValue() == 0) {
            return Optional.empty();
        }

        maxId = ((BigInteger)  convertSummary[1]).longValue();

        statelessSession = emWrapper.getEntityManager().unwrap(Session.class).getSessionFactory().openStatelessSession();
        scrollableResults = statelessSession.createNamedQuery("AuditDataLogRecord.listConvertToAggregate").setParameter("maxId", maxId).setReadOnly(true).setCacheable(false).setFetchSize(fetchSize)
            .scroll(ScrollMode.FORWARD_ONLY);

        return Optional.of(new SynchronizedIteratorGrouped<AuditDataLogRecord>(scrollableResults, nrOfRecords.intValue()) {

            @Override
            public Object getGroupByValue(AuditDataLogRecord item) {
                return item.getTxId();
            }
        });
    }

    /**
     * Aggregate audit data log records
     * 
     * @param auditDataLogRecords Audit data log records
     * @param jobExecutionResult Job execution result
     */
    private void aggregateAuditDataLogs(List<List<AuditDataLogRecord>> auditDataLogRecords, JobExecutionResultImpl jobExecutionResult) {

        List<AuditDataLogRecord> auditDataLogRecordsFlat = auditDataLogRecords.stream().flatMap(list -> list.stream()).collect(Collectors.toList());

        auditDataLogService.aggregateAuditLogs(auditDataLogRecordsFlat);
    }

    private boolean hasMore(JobInstance jobInstance) {
        return false;
    }

    /**
     * Close data resultset
     * 
     * @param jobExecutionResult Job execution result
     */
    private void closeResultset(JobExecutionResultImpl jobExecutionResult) {
        scrollableResults.close();
        statelessSession.close();
    }

    @Override
    protected boolean isProcessItemInNewTx() {
        return true;
    }
}