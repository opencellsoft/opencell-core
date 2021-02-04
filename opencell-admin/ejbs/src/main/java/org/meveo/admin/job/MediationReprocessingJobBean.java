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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.MediationReprocessing;
import org.meveo.admin.parse.csv.MEVEOCdrParser;
import org.meveo.admin.parse.csv.MEVEOCdrReader;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;
import org.slf4j.Logger;

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
public class MediationReprocessingJobBean {

    /** The log. */
    @Inject
    private Logger log;
    
    @Inject
    private MediationReprocessing mediationReprocessing;

    @Inject
    private MEVEOCdrReader meveoCdrReader;

    @Inject
    private MEVEOCdrParser meveoCdrParser;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    protected JobExecutionService jobExecutionService;

    /** The report. */
    String report;

    /**
     * Process a single file
     *
     * @param result Job execution result
     * @param nbRuns Number of parallel executions
     * @param readerCode CDR Reader code 
     * @param parserCode CDR Parser code
     * @param waitingMills Number of milliseconds to wait between launching parallel processing threads
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String parametres, Long nbRuns, Long waitingMillis, String readerCode, String parserCode) {
        log.debug("Reprocessing CDR from database");

        ICdrReader cdrReader = null;
        ICdrParser cdrParser = null;

        try {
            cdrReader = (ICdrReader) EjbUtils.getServiceInterface(readerCode);
            if(cdrReader == null) {
                cdrReader = meveoCdrReader;
            }
            cdrReader.init("DB");

            cdrParser = (ICdrParser) EjbUtils.getServiceInterface(parserCode);
            if(cdrParser == null) {
                cdrParser = meveoCdrParser;
            }

            // Launch parallel processing of a file
            List<Future<String>> futures = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            for (long i = 0; i < nbRuns; i++) {

                futures.add(mediationReprocessing.processAsync(cdrReader, cdrParser, result,lastCurrentUser));

                if (waitingMillis > 0) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }

            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    jobExecutionService.registerError(result, cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            if (result.getNbItemsProcessed() == 0) {
                String errorDescription = "\r\n Record is empty";
                result.addReport(errorDescription);
            }

            log.info("Finished processing mediation");

        } catch (Exception e) {
            log.error("Failed to process mediation", e);
            result.addReport(e.getMessage());
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