package org.meveo.admin.job;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_DAYS_TO_RETAIN;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF;

/**
 * The Class job bean to remove not open EDR, WO, RTx between two dates.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class PurgeMediationDataJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private EdrService edrService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    private static final long OLD_DATE = 50;

    private static final String SPLIT_CHAR = ",";

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());
        try {
            Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_firstTransactionDate");
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_lastTransactionDate");
            if (lastTransactionDate == null) {
                lastTransactionDate = new Date();
            }
            long daysToRetain = (long) this.getParamOrCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_DAYS_TO_RETAIN);
            if (daysToRetain > 0) {
                firstTransactionDate = java.sql.Date.valueOf(LocalDate.now().minusYears(OLD_DATE));
                lastTransactionDate = java.sql.Date.valueOf(LocalDate.now().minusDays(daysToRetain));
            }
            boolean edrCf = (boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_edrCf", false);
            boolean woCf = (boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_woCf", false);
            boolean rtCf = (boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_rtCf", false);
            long nbItems = 0;
            String formattedStartDate = DateUtils.formatDateWithPattern(firstTransactionDate, "yyyy-MM-dd");
            String formattedEndDate = DateUtils.formatDateWithPattern(lastTransactionDate, "yyyy-MM-dd");
            if (woCf) {
                log.info("=> starting purge wallet operation between {} and {}", formattedStartDate, formattedEndDate);
                List<WalletOperationStatusEnum> targetStatusList = getTargetStatusList(jobInstance, WalletOperationStatusEnum.class, PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF);
                if (!targetStatusList.isEmpty()) {
                    nbItems = walletOperationService.purge(firstTransactionDate, lastTransactionDate, targetStatusList);
                    log.info("==>{} WOs rows purged", nbItems);
                }
            }
            if (rtCf) {
                log.info("=> starting purge rated transactions between {} and {}", formattedStartDate, formattedEndDate);
                List<RatedTransactionStatusEnum> targetStatusList = getTargetStatusList(jobInstance, RatedTransactionStatusEnum.class, PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF);
                if (!targetStatusList.isEmpty()) {
                    long itemsRemoved = ratedTransactionService.purge(firstTransactionDate, lastTransactionDate, targetStatusList);
                    log.info("==>{} RTs rows purged", itemsRemoved);
                    nbItems += itemsRemoved;
                }
            }
            if (edrCf) {
                log.info("=> starting purge rated transactions between {} and {}", formattedStartDate, formattedEndDate);
                List<EDRStatusEnum> targetStatusList = getTargetStatusList(jobInstance, EDRStatusEnum.class, PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF);
                if (!targetStatusList.isEmpty()) {
                    long itemsRemoved = edrService.purge(firstTransactionDate, lastTransactionDate, targetStatusList);
                    log.info("==>{} EDRs rows purged ", itemsRemoved);
                    nbItems += itemsRemoved;
                }
            }
            result.setNbItemsToProcess(nbItems);
            result.setNbItemsCorrectlyProcessed(nbItems);
        } catch (Exception e) {
            log.error("Failed to run purge EDR/WO/RT job", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }
    }
}