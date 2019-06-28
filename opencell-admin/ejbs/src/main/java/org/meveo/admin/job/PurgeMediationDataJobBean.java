package org.meveo.admin.job;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.billing.EDRDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.jaxb.mediation.EDRs;
import org.meveo.model.jaxb.mediation.RatedTransactions;
import org.meveo.model.jaxb.mediation.WalletOperations;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.EDR;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    @Inject
    private WalletService walletService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());
        try {
            Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_firstTransactionDate");
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_lastTransactionDate");
            Boolean edrCf = (Boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_edrCf");
            Boolean woCf = (Boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_woCf");
            Boolean rtCf = (Boolean) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJob_rtCf");
            long nbItems = 0;
            if (woCf) {
                nbItems = walletOperationService.purge(firstTransactionDate,lastTransactionDate);
            }
            if (rtCf) {
                long itemsRemoved = ratedTransactionService.purge(firstTransactionDate,lastTransactionDate);
                nbItems += itemsRemoved;
            }
            if (edrCf) {
                long itemsRemoved = edrService.purge(firstTransactionDate,lastTransactionDate);
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