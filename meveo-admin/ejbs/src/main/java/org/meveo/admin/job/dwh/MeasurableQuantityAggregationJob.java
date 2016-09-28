package org.meveo.admin.job.dwh;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;

@Startup
@Singleton
public class MeasurableQuantityAggregationJob extends Job {

    @Inject
    private MeasurableQuantityService mqService;

    @Inject
    private MeasuredValueService mvService;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void aggregateMeasuredValues(JobExecutionResultImpl result, StringBuilder report, MeasurableQuantity mq,User currentUser) throws BusinessException {
        if (report.length() == 0) {
            report.append("Generate Measured Value for : " + mq.getCode());
        } else {
            report.append(",").append(mq.getCode());
        }
        Object[] mvObject = mqService.executeMeasurableQuantitySQL(mq);

        try {
            if (mvObject!=null&&mvObject.length > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                MeasuredValue mv = new MeasuredValue();
                mv.setMeasurableQuantity(mq);
                mv.setMeasurementPeriod(mq.getMeasurementPeriod());
                mv.setDate(sdf.parse(mvObject[0] + ""));
                mv.setValue(new BigDecimal(mvObject[1] + ""));
                mvService.create(mv,currentUser);
            }
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument exception in create measured values",e);
        } catch (SecurityException e) {
        	 log.error("security exception in create measured values ",e);
        } catch (ParseException e) {
        	 log.error("parse exception in create measured values",e);
        } 
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    private void aggregateMeasuredValues(JobExecutionResultImpl result, StringBuilder report, List<MeasurableQuantity> mq,User currentUser) throws BusinessException {
        for (MeasurableQuantity measurableQuantity : mq) {
            aggregateMeasuredValues(result, report, measurableQuantity,currentUser);
        }
    }

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
    	log.info("Running for user={}, parameter={}, provider={}", currentUser, null, currentUser.getProvider().getCode());
		
        StringBuilder report = new StringBuilder();
        if (jobInstance.getParametres() != null && !jobInstance.getParametres().isEmpty()) {

            MeasurableQuantity mq = mqService.listByCode(jobInstance.getParametres(), currentUser.getProvider()).get(0);
            aggregateMeasuredValues(result, report, mq,currentUser);
            result.setReport(report.toString());

        } else {
            aggregateMeasuredValues(result, report, mqService.list(currentUser.getProvider()),currentUser);
            result.setReport(report.toString());
        }
    }

    public BigDecimal getMeasuredValueListValueSum(List<MeasuredValue> mvList) {
        BigDecimal mvTotal = BigDecimal.ZERO;
        for (MeasuredValue mv : mvList) {
            mvTotal = mvTotal.add(mv.getValue());
        }
        return mvTotal;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.DWH;
    }
}