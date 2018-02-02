package org.meveo.admin.job;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.finance.ReportExtractService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @created 2 Feb 2018
 **/
@Stateless
public class UnitReportExtractJobBean {

    @Inject
    private Logger log;

    @Inject
    private ReportExtractService reportExtractService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long id, Date startDate, Date endDate) {

        try {
            ReportExtract entity = reportExtractService.findById(id);
            reportExtractService.runReport(entity, startDate, endDate);

            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to generate acount operations", e);
            result.registerError(e.getMessage());
        }
    }

}
