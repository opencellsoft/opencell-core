/**
 * 
 */
package org.meveo.admin.async;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.MediationJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * @author anasseh
 * 
 */

@Stateless
public class MediationAsync {

    @Inject
    private MediationJobBean mediationJobBean;

    @Inject
    protected Logger log;

    @Inject
    private JobExecutionService jobExecutionService;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<File> files, JobExecutionResultImpl result, String parameter) {
        for (File file : files) {
            if (!jobExecutionService.isJobRunning(result.getJobInstance())) {
                break;
            }
            mediationJobBean.execute(result, parameter, file);
        }

        return new AsyncResult<String>("OK");
    }
}