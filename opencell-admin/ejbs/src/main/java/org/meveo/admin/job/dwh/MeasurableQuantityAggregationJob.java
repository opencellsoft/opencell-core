package org.meveo.admin.job.dwh;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;

@Stateless
public class MeasurableQuantityAggregationJob extends Job {

    @Inject
    private MeasurableQuantityService mqService;

    @Inject
    private MeasuredValueService mvService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        StringBuilder report = new StringBuilder();
        if (jobInstance.getParametres() != null && !jobInstance.getParametres().isEmpty()) {

            MeasurableQuantity mq = mqService.listByCode(jobInstance.getParametres()).get(0);
            aggregateMeasuredValues(result, report, mq);
            result.setReport(report.toString());

        } else {
            aggregateMeasuredValues(result, report, mqService.list());
            result.setReport(report.toString());
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void aggregateMeasuredValues(JobExecutionResultImpl result, StringBuilder report, MeasurableQuantity mq) throws BusinessException {
        if (report.length() == 0) {
            report.append("Generate Measured Value for : " + mq.getCode());
        } else {
            report.append(",").append(mq.getCode());
        }
        Object[] mvObject = mqService.executeMeasurableQuantitySQL(mq);

        try {
            if (mvObject != null && mvObject.length > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                MeasuredValue mv = new MeasuredValue();
                mv.setMeasurableQuantity(mq);
                mv.setMeasurementPeriod(mq.getMeasurementPeriod());
                mv.setDate(sdf.parse(mvObject[0] + ""));
                mv.setValue(new BigDecimal(mvObject[1] + ""));
                mvService.create(mv);
            }
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument exception in create measured values", e);
        } catch (SecurityException e) {
            log.error("security exception in create measured values ", e);
        } catch (ParseException e) {
            log.error("parse exception in create measured values", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    private void aggregateMeasuredValues(JobExecutionResultImpl result, StringBuilder report, List<MeasurableQuantity> mq) throws BusinessException {
        for (MeasurableQuantity measurableQuantity : mq) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            aggregateMeasuredValues(result, report, measurableQuantity);
        }
    }

    public BigDecimal getMeasuredValueListValueSum(List<MeasuredValue> mvList, Long jobInstanceId) {
        BigDecimal mvTotal = BigDecimal.ZERO;
        for (MeasuredValue mv : mvList) {
            if (!jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            mvTotal = mvTotal.add(mv.getValue());
        }
        return mvTotal;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.DWH;
    }
}