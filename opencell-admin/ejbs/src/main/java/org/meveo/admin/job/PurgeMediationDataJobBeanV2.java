package org.meveo.admin.job;

import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_EDR_CF;
import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF;
import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_FIRST_TRANSACTION_DATE;
import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_LAST_TRANSACTION_DATE;
import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_RT_CF;
import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF;
import static org.meveo.admin.job.PurgeMediationDataJobV2.PURGE_MEDIATION_DATA_JOB_WO_CF;
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
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.slf4j.Logger;

/**
 * @author mohamed EL YOUSSOUFI
 */
@Stateless
public class PurgeMediationDataJobBeanV2 extends BaseJobBean {

	@Inject
	private Logger log;

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private EdrService edrService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

		Date firstTransactionDate = null, lastTransactionDate = null;
		Boolean edrCf = Boolean.FALSE, woCf = Boolean.FALSE, rtCf = Boolean.FALSE;
		String formattedStartDate = null, formattedEndDate = null;
		
		List<WalletOperationStatusEnum> targetWoStatus = null;
		List<RatedTransactionStatusEnum> targetRtStatus = null;
		List<EDRStatusEnum> targetEdrStatus = null;

		try {

			firstTransactionDate = (Date) customFieldInstanceService.getCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_FIRST_TRANSACTION_DATE);
			lastTransactionDate = (Date) customFieldInstanceService.getCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_LAST_TRANSACTION_DATE);
			if (lastTransactionDate == null) {
				lastTransactionDate = new Date();
			}

			formattedStartDate = DateUtils.formatDateWithPattern(firstTransactionDate, "yyyy-MM-dd");
			formattedEndDate = DateUtils.formatDateWithPattern(lastTransactionDate, "yyyy-MM-dd");


			edrCf = (Boolean) customFieldInstanceService.getCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_EDR_CF);
			woCf = (Boolean) customFieldInstanceService.getCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_WO_CF);
			rtCf = (Boolean) customFieldInstanceService.getCFValue(jobInstance, PURGE_MEDIATION_DATA_JOB_RT_CF);

			if (woCf) {
				targetWoStatus = getTargetStatusList(jobInstance, WalletOperationStatusEnum.class, PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF);
			}

			if (rtCf) {
				targetRtStatus = getTargetStatusList(jobInstance, RatedTransactionStatusEnum.class, PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF);
			}

			if (edrCf) {
				targetEdrStatus = getTargetStatusList(jobInstance, EDRStatusEnum.class, PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF);
			}

		} catch (Exception e) {
			log.warn("Error while getting customFields for {} {}", jobInstance.getJobTemplate(), e.getMessage());
			return;
		}

		DateTime fd = new DateTime(firstTransactionDate);
		DateTime ed = new DateTime(lastTransactionDate);

		// Calcul de nombre de jours entre les deux dates
		int nbDays = Days.daysBetween(fd, ed).getDays();
		if (nbDays <= 0) {
			nbDays = 1;
		}
		
		long totalPurgedRows = 0l;
		StringBuilder report = new StringBuilder();

		// Purge Wallet Operations
		if (targetWoStatus != null && !targetWoStatus.isEmpty()) {
			log.info("=> starting purge Wallet Operations between {} and {}", formattedStartDate, formattedEndDate);
			totalPurgedRows = purgeMediationData(jobInstance, firstTransactionDate, nbDays, targetWoStatus, WalletOperation.class);
			report.append("- Purged WOs : ").append(totalPurgedRows);
			result.setReport(report.toString());
		}

		// Purge Rated Transactions
		if (targetRtStatus!= null && !targetRtStatus.isEmpty()) {
			log.info("=> starting purge Rated Transactions between {} and {}", formattedStartDate, formattedEndDate);
			long purgedRTs = purgeMediationData(jobInstance, firstTransactionDate, nbDays, targetRtStatus, RatedTransaction.class);
			totalPurgedRows += purgedRTs;
			report.append(" - Purged RTs : ").append(purgedRTs);
			result.setReport(report.toString());
		}
		// Purge Edrs
		if (targetEdrStatus != null && !targetEdrStatus.isEmpty()) {
			log.info("=> starting purge Edrs between {} and {}", formattedStartDate, formattedEndDate);
			long purgedEdrs = purgeMediationData(jobInstance, firstTransactionDate, nbDays, targetEdrStatus, EDR.class);
			totalPurgedRows += purgedEdrs;
			report.append(" - Purged Edrs : ").append(purgedEdrs);
			result.setReport(report.toString());
		}
		
		result.addNbItemsCorrectlyProcessed(totalPurgedRows);

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
