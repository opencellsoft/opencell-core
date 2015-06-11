package org.meveo.admin.job;

import java.io.File;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.sepa.SepaService;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class SepaRejectedTransactionsJob extends Job {

    @Inject
    private SepaService sepaService;

    ParamBean param = ParamBean.getInstance();

    String importDir = param.getProperty("sepaRejectedTransactionsJob.importDir", "/tmp/meveo/SepaRejectedTransactions");

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
        Provider provider = currentUser.getProvider();

        String dirIN = importDir + File.separator + provider.getCode() + File.separator + "rejectedSepaTransactions" + File.separator + "input";
        log.info("dirIN=" + dirIN);
        String dirOK = importDir + File.separator + provider.getCode() + File.separator + "rejectedSepaTransactions" + File.separator + "output";
        String dirKO = importDir + File.separator + provider.getCode() + File.separator + "rejectedSepaTransactions" + File.separator + "reject";
        String prefix = param.getProperty("sepaRejectedTransactionsJob.file.prefix", "Pain002_");
        String ext = param.getProperty("sepaRejectedTransactionsJob.file.extension", "xml");

        try {

            File dir = new File(dirIN);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            List<File> files = sepaService.getFilesToProcess(dir, prefix, ext);
            int numberOfFiles = files.size();
            log.info("InputFiles job " + numberOfFiles + " to import");
            result.setNbItemsToProcess(numberOfFiles);
            for (File file : files) {
                File currentFile = null;
                try {
                    log.info("InputFiles job " + file.getName() + " in progres");
                    currentFile = FileUtils.addExtension(file, ".processing");
                    sepaService.processRejectFile(currentFile, file.getName(), currentUser);
                    FileUtils.moveFile(dirOK, currentFile, file.getName());
                    log.info("InputFiles job " + file.getName() + " done");
                    result.registerSucces();
                    
                } catch (Exception e) {
                    result.registerError(e.getMessage());
                    log.error("InputFiles job " + file.getName() + " failed", e);
                    FileUtils.moveFile(dirKO, currentFile, file.getName());
                } finally {
                    if (currentFile != null) {
                        currentFile.delete();
                    }
                }
            }

        } catch (Exception e) {
            log.error("Failed to sepa reject transaction", e);
        }
    }

    // public Collection<Timer> getTimers() {
    // return this.timerService.getTimers();
    // }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.ACCOUNT_RECEIVABLES;
    }
}
