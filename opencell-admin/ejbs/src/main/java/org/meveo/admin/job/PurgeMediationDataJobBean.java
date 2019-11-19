package org.meveo.admin.job;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

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
    private ParamBeanFactory paramBeanFactory;

    @Inject
    private EdrService edrService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

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
            boolean edrCf = (boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_edrCf", false);
            boolean woCf = (boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_woCf", false);
            boolean rtCf = (boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_rtCf", false);
            long nbItems = 0;
            String formattedStartDate = DateUtils.formatDateWithPattern(firstTransactionDate, "yyyy-MM-dd");
            String formattedEndDate = DateUtils.formatDateWithPattern(lastTransactionDate, "yyyy-MM-dd");
            if (woCf) {
                log.info("=> starting purge wallet operation between {} and {}", formattedStartDate, formattedEndDate);
                nbItems = walletOperationService.purge(firstTransactionDate,lastTransactionDate);
                log.info("==>{} WOs rows purged", nbItems);
            }
            if (rtCf) {
                log.info("=> starting purge rated transactions between {} and {}", formattedStartDate, formattedEndDate);
                long itemsRemoved = ratedTransactionService.purge(firstTransactionDate,lastTransactionDate);
                log.info("==>{} RTs rows purged", itemsRemoved);
                nbItems += itemsRemoved;
            }
            if (edrCf) {
                log.info("=> starting purge rated transactions between {} and {}", formattedStartDate, formattedEndDate);
                long itemsRemoved = edrService.purge(firstTransactionDate, lastTransactionDate);
                log.info("==>{} EDRs rows purged ", itemsRemoved);
                nbItems += itemsRemoved;
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