/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF;
import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF;
import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * The Class job bean to remove not open EDR, WO, RTx between two dates.
 *
 * @author Khalid HORRI
 * @author mohamed EL YOUSSOUFI
 * @lastModifiedVersion 7.6.1
 */
@Stateless
public class PurgeMediationDataJobBeanV2 extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private EdrService edrService;
    
    @Inject
    protected JobExecutionService jobExecutionService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());
        try {
            
        	Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJobV2_firstTransactionDate");
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "PurgeMediationDataJobV2_lastTransactionDate");
            if (lastTransactionDate == null) {
                lastTransactionDate = new Date();
            }
            
            DateTime fd = new DateTime(firstTransactionDate);
    		DateTime ed = new DateTime(lastTransactionDate);

    		// Calcul de nombre de jours entre les deux dates
    		int nbDays = Days.daysBetween(fd, ed).getDays();
    		if (nbDays <= 0) {
    			nbDays = 1;
    		}
            
            long nbItems = 0;
            StringBuilder report = new StringBuilder();
            
            String formattedStartDate = DateUtils.formatDateWithPattern(firstTransactionDate, "yyyy-MM-dd");
            String formattedEndDate = DateUtils.formatDateWithPattern(lastTransactionDate, "yyyy-MM-dd");
            
            List<WalletOperationStatusEnum> woStatusList = getTargetStatusList(jobInstance, WalletOperationStatusEnum.class, PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF);
            if (!woStatusList.isEmpty()) {
            	log.info("=> starting purge Wallet Operations between {} and {}", formattedStartDate, formattedEndDate);
            	nbItems = purgeMediationData(jobInstance, firstTransactionDate, nbDays, woStatusList, WalletOperation.class);
            	report.append("- Purged WOs : ").append(nbItems);
    			result.setReport(report.toString());
            }
            
            List<RatedTransactionStatusEnum> rtStatusList = getTargetStatusList(jobInstance, RatedTransactionStatusEnum.class, PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF);
            if (!rtStatusList.isEmpty()) {
            	log.info("=> starting purge Rated Transactions between {} and {}", formattedStartDate, formattedEndDate);
            	long itemsRemoved = purgeMediationData(jobInstance, firstTransactionDate, nbDays, rtStatusList, RatedTransaction.class);
                nbItems += itemsRemoved;
                report.append(" - Purged RTs : ").append(itemsRemoved);
    			result.setReport(report.toString());
            }
            
            List<EDRStatusEnum> edrStatusList = getTargetStatusList(jobInstance, EDRStatusEnum.class, PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF);
            if (!edrStatusList.isEmpty()) {
            	log.info("=> starting purge Edrs between {} and {}", formattedStartDate, formattedEndDate);
            	long itemsRemoved = purgeMediationData(jobInstance, firstTransactionDate, nbDays, edrStatusList, EDR.class);
                nbItems += itemsRemoved;
                report.append(" - Purged Edrs : ").append(itemsRemoved);
    			result.setReport(report.toString());
            }
            
            result.setNbItemsCorrectlyProcessed(nbItems);
            
        } catch (Exception e) {
            log.error("Failed to run purge EDR/WO/RT job", e);
            jobExecutionService.registerError(result, e.getMessage());
            result.addReport(e.getMessage());
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
	private <T extends Enum<T>, E extends BaseEntity> long purgeMediationData(JobInstance jobInstance, Date firstTransactionDate, int nbDays, List<T> targetStatus, Class<E> clazz) {
		
		long allPeriodPurgedDays = 0l;
		String formattedStartDate = null;
		
		Date tmpFirstTransactionDate = DateUtils.addDaysToDate(firstTransactionDate, 0);

		for (int i = 0; i < nbDays; i++) {
			
			tmpFirstTransactionDate = DateUtils.addDaysToDate(firstTransactionDate, i);
			formattedStartDate = DateUtils.formatDateWithPattern(tmpFirstTransactionDate, "yyyy-MM-dd");
			log.info("=> Day : {} date : {}", i + 1, formattedStartDate);
			formattedStartDate = DateUtils.formatDateWithPattern(tmpFirstTransactionDate, "yyyyMMdd");
			long countToPurge = edrService.countMediationDataToPurge(formattedStartDate, targetStatus, clazz);
			long lastId = 0l;
			long dayPurgedRows = 0l;

			do {
				List<BigInteger> ids = edrService.getMediationDataIdsToPurge(formattedStartDate, targetStatus, clazz, lastId);
				if (ids != null && !ids.isEmpty()) {
					log.debug("=> process packet with size {}", ids.size());
					long packetPurgedRows = edrService.purgeMediationDataPacket(ids, clazz);
					dayPurgedRows += packetPurgedRows;
					lastId = ids.get(ids.size() - 1).longValue();
				}
			} while (countToPurge > dayPurgedRows);
			
			log.info("=> Purged rows for the day : {}", dayPurgedRows);
			allPeriodPurgedDays += dayPurgedRows;
			
		}
		
		return allPeriodPurgedDays;
		
	}
    
}