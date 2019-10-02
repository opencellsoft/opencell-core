package org.meveo.admin.job;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_FIRST_TRANSACTION_DATE;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_LAST_TRANSACTION_DATE;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_EDR_CF;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_WO_CF;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_RT_CF;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_DAYS_TO_PURGE;
import static org.meveo.admin.job.PurgeMediationDataJob.PURGE_MEDIATION_DATA_JOB_PACKETS_NUMBER;
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
    
    private static final int OLD_DATE = 10;

    private static final String SPLIT_CHAR = ";";
    
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
    
    	log.debug("Running with parameter={}", jobInstance.getParametres());
        try {
        	
            Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_FIRST_TRANSACTION_DATE);
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_LAST_TRANSACTION_DATE);
            if (lastTransactionDate == null) {
                lastTransactionDate = new Date();
            }
            
            Boolean edrCf = (Boolean) this.getParamOrCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_EDR_CF);
            Boolean woCf = (Boolean) this.getParamOrCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_WO_CF);
            Boolean rtCf = (Boolean) this.getParamOrCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_RT_CF);
            
            long daysToPurge = (long) this.getParamOrCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_DAYS_TO_PURGE);
            if (daysToPurge > 0) {
                firstTransactionDate = java.sql.Date.valueOf(LocalDate.now().minusYears(OLD_DATE));
                lastTransactionDate = java.sql.Date.valueOf(LocalDate.now().minusDays(daysToPurge));
            }

            long packetsNumber = (long) this.getParamOrCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_PACKETS_NUMBER);
            if(packetsNumber == 0  || packetsNumber < 0) {
            	packetsNumber = 100;
            }
            
            long nbItems = 0;
            
            if (woCf) {
            	List<WalletOperationStatusEnum> targetStatusList = getTargetStatusList(jobInstance, WalletOperationStatusEnum.class, PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF);
            	if(!targetStatusList.isEmpty()) {
            		long itemsToRemove = walletOperationService.count(firstTransactionDate, lastTransactionDate, targetStatusList);
                    long paquetSize = Math.round(itemsToRemove/packetsNumber);
                    paquetSize = paquetSize == 0 ? 1 : paquetSize;
                    for (long i = 0; i < itemsToRemove; i = i + paquetSize) {
                        nbItems += processWoPaquet(firstTransactionDate, lastTransactionDate, targetStatusList, paquetSize);
                    }
            	}
            }
            
            if (rtCf) {
            	List<RatedTransactionStatusEnum> targetStatusList = getTargetStatusList(jobInstance, RatedTransactionStatusEnum.class, PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF);
            	if(!targetStatusList.isEmpty()) {
            		long itemsToRemove = ratedTransactionService.count(firstTransactionDate, lastTransactionDate, targetStatusList);
                    long paquetSize = Math.round(itemsToRemove/packetsNumber);
                    paquetSize = paquetSize == 0 ? 1 : paquetSize;
                    for (long i = 0; i < itemsToRemove; i = i + paquetSize) {
                    	nbItems += processRtPaquet(firstTransactionDate, lastTransactionDate, targetStatusList, paquetSize);
                    }
            	}
            }
            
            if (edrCf) {
            	List<EDRStatusEnum> targetStatusList = getTargetStatusList(jobInstance, EDRStatusEnum.class, PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF);
            	if(!targetStatusList.isEmpty()) {
            		long itemsToRemove = edrService.count(firstTransactionDate, lastTransactionDate, targetStatusList);
                    long paquetSize = Math.round(itemsToRemove/packetsNumber);
                    paquetSize = paquetSize == 0 ? 1 : paquetSize;
                    for (long i = 0; i < itemsToRemove; i = i + paquetSize) {
                    	nbItems += processEdrPaquet(firstTransactionDate, lastTransactionDate, targetStatusList, paquetSize);
                    }
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private long processWoPaquet(Date firstTransactionDate, Date lastTransactionDate, List<WalletOperationStatusEnum> targetStatusList, long paquetSize) {
        return walletOperationService.purge(firstTransactionDate, lastTransactionDate, targetStatusList, paquetSize);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private long processRtPaquet(Date firstTransactionDate, Date lastTransactionDate, List<RatedTransactionStatusEnum> targetStatusList, long paquetSize) {
        return ratedTransactionService.purge(firstTransactionDate, lastTransactionDate, targetStatusList, paquetSize);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private long processEdrPaquet(Date firstTransactionDate, Date lastTransactionDate, List<EDRStatusEnum> targetStatusList, long paquetSize) {
        return edrService.purge(firstTransactionDate, lastTransactionDate, targetStatusList, paquetSize);
    }
    
    
    private  <T extends Enum<T>> List<T> getTargetStatusList(JobInstance jobInstance, Class<T> clazz, String cfCode) {
        List<T> formattedStatus = new ArrayList<T>();
        String woStatusListStr = (String) this.getParamOrCFValue(jobInstance, cfCode);
        if (woStatusListStr != null && !woStatusListStr.isEmpty()) {
            List<String> edrStatusList = Arrays.asList(woStatusListStr.split(SPLIT_CHAR));
            for (String status : edrStatusList) {
                T statusEnum = T.valueOf(clazz, status.toUpperCase());
                if (statusEnum != null) {
                    formattedStatus.add(statusEnum);
                }
            }
        }
        return formattedStatus;
    }
    
    
}