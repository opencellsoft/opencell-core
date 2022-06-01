package org.meveo.admin.job;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.security.EncryptionService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@Stateless
public class EncryptionJobBean extends BaseJobBean {

    @Inject
    protected Logger log;

    @Inject
    private EncryptionService encryptionService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        String processedTable = jobInstance.getParametres();
        encryptionService.changeEncAlgoOrKey(processedTable);

        // Result message
        String resultMsg;
        if (processedTable.equals(EncryptionService.PROCESSED_ALL_TABLES)) {
            resultMsg = "All tables in database are decrypted, then encrypted with new encryption algorithm and new password";
        }
        else {
            resultMsg = "The table " + processedTable + " is decrypted, then encrypted with new encryption algorithm and new password";
        }

        // Set Job report
        jobExecutionResult.setReport(resultMsg);
        jobExecutionResult.setNbItemsCorrectlyProcessed(encryptionService.getNbItemsCorrectlyProcessed());
    }
}
