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
import org.meveo.service.payments.impl.DDRequestBuilderInterface;
import org.meveo.service.payments.impl.DDRequestItemService;
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

    /** The ddRequestItemService service. */
    @Inject
    private DDRequestItemService ddRequestItemService;

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
            DDRejectFileInfos ddRejectFileInfos = ddRequestBuilderInterface.processDDRejectedFile(currentFile);
            ddRequestItemService.processRejectFile(ddRejectFileInfos);

            FileUtils.moveFile(archiveDir, currentFile, fileName);
            log.info("Processing " + file.getName() + " done");
            result.registerSucces();

        } catch (Exception e) {
            result.registerError(e.getMessage());
            log.error("Processing " + file.getName() + " failed", e);
            FileUtils.moveFile(rejectDir, currentFile, fileName);
        } finally {
            if (currentFile != null) {
                currentFile.delete();
            }
        }
    }
}
