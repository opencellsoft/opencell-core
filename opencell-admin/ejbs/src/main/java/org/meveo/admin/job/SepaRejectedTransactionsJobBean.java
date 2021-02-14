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

import java.io.File;
import java.io.PrintWriter;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.DDRequestBuilderInterface;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.slf4j.Logger;

/**
 * The Class SepaRejectedTransactionsJobBean consume sepa/paynum or any custom rejected files (ddRequest file callBacks).
 * 
 * @author anasseh
 * @lastModifiedVersion 5.2
 * 
 */
@Stateless
public class SepaRejectedTransactionsJobBean {

    /** The log. */
    @Inject
    private Logger log;

    /** The ddRequestLotService service. */
    @Inject
    private DDRequestLOTService ddRequestLotService;
    
    @Inject
    protected JobExecutionService jobExecutionService;

    /** The file name. */
    String fileName;

    /** The output dir. */
    String outputDir;

    /** The output file writer. */
    PrintWriter outputFileWriter;

    /** The reject dir. */
    String rejectDir;

    /** The archive dir. */
    String archiveDir;

    /** The reject file writer. */
    PrintWriter rejectFileWriter;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance, File file, DDRequestBuilderInterface ddRequestBuilderInterface, String inputDir)
            throws BusinessException {
        File currentFile = null;
        try {
            outputDir = inputDir + File.separator + "output";
            rejectDir = inputDir + File.separator + "reject";
            archiveDir = inputDir + File.separator + "archive";
            File f = new File(outputDir);
            if (!f.exists()) {
                log.debug("outputDir {} not exist", outputDir);
                f.mkdirs();
                log.debug("outputDir {} creation ok", outputDir);
            }
            f = new File(rejectDir);
            if (!f.exists()) {
                log.debug("rejectDir {} not exist", rejectDir);
                f.mkdirs();
                log.debug("rejectDir {} creation ok", rejectDir);
            }
            f = new File(archiveDir);
            if (!f.exists()) {
                log.debug("saveDir {} not exist", archiveDir);
                f.mkdirs();
                log.debug("saveDir {} creation ok", archiveDir);
            }
            fileName = file.getName();
            log.info(file.getName() + " in progress");
            currentFile = FileUtils.addExtension(file, ".processing_" + EjbUtils.getCurrentClusterNode());
            OperationCategoryEnum operationCategory = OperationCategoryEnum.CREDIT;
            operationCategory = OperationCategoryEnum.valueOf(((String) jobInstance.getCfValue("RejectSepaJob_creditOrDebit")).toUpperCase());
            DDRejectFileInfos ddRejectFileInfos = null;
            if (operationCategory == OperationCategoryEnum.CREDIT) {
                ddRejectFileInfos = ddRequestBuilderInterface.processSDDRejectedFile(currentFile);
            } else {
                ddRejectFileInfos = ddRequestBuilderInterface.processSCTRejectedFile(currentFile);
            }

            ddRequestLotService.processRejectFile(ddRejectFileInfos);

            FileUtils.moveFile(archiveDir, currentFile, fileName);
            log.info("Processing " + file.getName() + " done");
            jobExecutionService.registerSucces(result);
            result.setNbItemsCorrectlyProcessed(ddRejectFileInfos.getNbItemsOk());
            result.setNbItemsProcessedWithError(ddRejectFileInfos.getNbItemsKo());
            result.addReport(ddRejectFileInfos.formatErrorsReport());
            
        } catch (Exception e) {
            jobExecutionService.registerError(result, e.getMessage());
            log.error("Processing " + file.getName() + " failed", e);
            FileUtils.moveFile(rejectDir, currentFile, fileName);
        } finally {
            if (currentFile != null) {
                currentFile.delete();
            }
        }
    }
}
