package org.meveo.admin.async;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitReportExtractJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;

/**
 * @author Edward P. Legaspi
 * @created 2 Feb 2018
 **/
@Stateless
public class ReportExtractAsync {

    @Inject
    private UnitReportExtractJobBean unitReportExtractJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, Date startDate, Date endDate) {
        for (Long id : ids) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }
            unitReportExtractJobBean.execute(result, id, startDate, endDate);
        }
        return new AsyncResult<>("OK");
    }

}
