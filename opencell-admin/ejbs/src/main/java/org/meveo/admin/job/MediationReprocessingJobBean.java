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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.parse.csv.MEVEOCdrParser;
import org.meveo.admin.parse.csv.MEVEOCdrReader;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;

/**
 * The Class MediationJobBean.
 *
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Houssine ZNIBAR
 * @lastModifiedVersion 10.0
 * 
 */
@Stateless
public class MediationReprocessingJobBean extends BaseJobBean {

    private static final long serialVersionUID = -8981175215897218406L;

    @Inject
    private MEVEOCdrReader meveoCdrReader;

    @Inject
    private MEVEOCdrParser meveoCdrParser;

    /** The cdr parser. */
    @Inject
    private CDRParsingService cdrParserService;

    @Inject
    private CDRService cdrService;

    /** The report. */
    String report;

    /**
     * Process a single file
     *
     * @param jobExecutionResult Job execution result
     * @param nbRuns Number of parallel executions
     * @param readerCode CDR Reader code
     * @param parserCode CDR Parser code
     * @param waitingMills Number of milliseconds to wait between launching parallel processing threads
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, String parametres, Long nbRuns, Long waitingMillis, String readerCode, String parserCode) {
        log.debug("Reprocessing CDR from database");

        ICdrReader cdrReader = null;
        ICdrParser cdrParser = null;

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        try {
            cdrReader = (ICdrReader) EjbUtils.getServiceInterface(readerCode);
            if (cdrReader == null) {
                cdrReader = meveoCdrReader;
            }
            cdrReader.init("DB");

            cdrParser = (ICdrParser) EjbUtils.getServiceInterface(parserCode);
            if (cdrParser == null) {
                cdrParser = meveoCdrParser;
            }

            // Launch parallel processing of a file
            List<Future> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();

            int checkJobStatusEveryNr = JobSpeedEnum.FAST.getCheckNb();
            int updateJobStatusEveryNr = JobSpeedEnum.FAST.getUpdateNb();

            ICdrReader cdrReaderFinal = cdrReader;
            ICdrParser cdrParserFinal = cdrParser;

            Runnable task = () -> {

                currentUserProvider.reestablishAuthentication(lastCurrentUser);

                int i = 0;
                CDR cdr = null;

                while (true) {

                    if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                        break;
                    }

                    try {
                        // Record progress
                        if (i>0 && i % updateJobStatusEveryNr == 0) {
                            jobExecutionResultService.persistResult(jobExecutionResult);
                        }
                    } catch (EJBTransactionRolledbackException e) {
                        // Will ignore the error here, as its most likely to happen - updating jobExecutionResultImpl entity from multiple threads
                    } catch (Exception e) {
                        log.error("Failed to update job progress", e);
                    }

                    // TODO this was original code - a loop.See if it still works with iterators
//                    List<CDR> cdrs = cdrReader.getRecords(cdrParser, null);
//                    for (CDR cdr : cdrs) {
                    try {
                        cdr = cdrReaderFinal.getNextRecord(cdrParserFinal);
                        if (cdr == null) {
                            break;
                        }

                        if (StringUtils.isBlank(cdr.getRejectReason())) {
                            List<Access> accessPoints = cdrParserFinal.accessPointLookup(cdr);
                            List<EDR> edrs = cdrParserFinal.convertCdrToEdr(cdr, accessPoints);
                            log.debug("Processing cdr id:{}", cdr.getId());

                            cdrParserService.createEdrs(edrs, cdr);

                            jobExecutionResult.registerSucces();
                        } else {
                            jobExecutionResult.registerError("cdr =" + (cdr != null ? cdr.getLine() : "") + ": " + cdr.getRejectReason());
                            cdr.setStatus(CDRStatusEnum.ERROR);
                            cdrService.updateReprocessedCdr(cdr);
                        }

                    } catch (Exception e) {

                        String errorReason = e.getMessage();
                        if (e instanceof CDRParsingException) {
                            log.error("Failed to process CDR id: {} error {}", cdr != null ? cdr.getId() : null, errorReason);
                        } else {
                            log.error("Failed to process CDR id: {}  error {}", cdr != null ? cdr.getId() : null, errorReason, e);
                        }
                        jobExecutionResult.registerError("cdr id=" + (cdr != null ? cdr.getId() : "") + ": " + errorReason);
                        cdr.setStatus(CDRStatusEnum.ERROR);
                        cdr.setRejectReason(e.getMessage());
                        cdrService.updateReprocessedCdr(cdr);
                    }

                    i++;
                }
            };

            for (int i = 0; i < nbRuns; i++) {
                log.info("{}/{} Will submit task to run", jobInstance.getJobTemplate(), jobInstance.getCode());
                futures.add(executor.submit(task));
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }

            // Mark number of threads it will be running on
            JobRunningStatusEnum jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), futures);

            boolean wasCanceled = jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

            // Wait for all async methods to finish
            for (Future future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    wasCanceled = true;
                    log.error("Thread/future for job {} was canceled", jobInstance);

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    jobExecutionResult.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            // Mark that all threads are finished
            jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), null);

            wasCanceled = wasCanceled || jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

            if (wasCanceled) {
                log.info("Canceled reprocessing mediation data");
            } else {
                log.info("Finished reprocessing mediation data");
            }

        } catch (Exception e) {
            log.error("Failed to process mediation", e);
            jobExecutionResult.addReport(e.getMessage());
        } finally {
            try {
                if (cdrReader != null) {
                    cdrReader.close();
                }
            } catch (Exception e) {
                log.error("Failed to close cdr parser");
            }
        }
    }
}