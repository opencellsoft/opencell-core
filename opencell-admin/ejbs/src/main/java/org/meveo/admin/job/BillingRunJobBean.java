package org.meveo.admin.job;

import static org.meveo.model.billing.BillingRunStatusEnum.NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.POSTVALIDATED;
import static org.meveo.model.billing.BillingRunStatusEnum.PREINVOICED;
import static org.meveo.model.billing.BillingRunStatusEnum.PREVALIDATED;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.slf4j.Logger;

/**
 * TODO add javadoc
 */
@Stateless
public class BillingRunJobBean extends BaseJobBean {

	@Inject
	private Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	protected ParamBeanFactory paramBeanFactory;

	@Inject
	protected ResourceBundle resourceMessages;

    @JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

		List<EntityReferenceWrapper> billingCyclesCf = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "BillingRunJob_billingCycle");
		Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "BillingRunJob_lastTransactionDate");
		Date invoiceDate = (Date) this.getParamOrCFValue(jobInstance, "BillingRunJob_invoiceDate");
		String billingCycleTypeId = (String) this.getParamOrCFValue(jobInstance, "BillingRunJob_billingRun_Process");
		List<String> billingCyclesCode = Collections.EMPTY_LIST;
		if (billingCyclesCf != null) {
			billingCyclesCode = billingCyclesCf.stream().map(EntityReferenceWrapper::getCode).collect(Collectors.toList());
		}
    	log.debug("Creating Billing Runs for billingCycles ={} with invoiceDate = {} and lastTransactionDate={}", billingCyclesCode, invoiceDate, lastTransactionDate);

		try {
			int nbItemsToProcess = 0;
			int nbItemsProcessedWithError = 0;
			BillingProcessTypesEnum billingCycleType = BillingProcessTypesEnum.FULL_AUTOMATIC;
			if (billingCycleTypeId != null) {
				billingCycleType = BillingProcessTypesEnum.getValue(Integer.valueOf(billingCycleTypeId));
			}
			ParamBean param = paramBeanFactory.getInstance();
			String allowManyInvoicing = param.getProperty("billingRun.allowManyInvoicing", "true");
			boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
			log.info("launchInvoicing allowManyInvoicing={}", isAllowed);
			for (String billingCycleCode : billingCyclesCode) {
				List<BillingRun> billruns = billingRunService.getBillingRuns(billingCycleCode, POSTVALIDATED, NEW, PREVALIDATED, PREINVOICED);
				boolean alreadyLaunched = billruns != null && billruns.size() > 0;
				if (alreadyLaunched && !isAllowed) {
					log.warn("Not allowed to launch many invoicing for the billingCycle = {}", billingCycleCode);
					result.registerError(resourceMessages.getString("error.invoicing.alreadyLunched"));
					result.setNbItemsProcessedWithError(++nbItemsProcessedWithError);
					continue;
				}

				BillingCycle billingCycle = billingCycleService.findByCode(billingCycleCode);

				if (billingCycle == null) {
					result.registerError("Cannot create a biling run with billing cycle '" + billingCycleCode);
					result.setNbItemsProcessedWithError(++nbItemsProcessedWithError);
					continue;
				}
						BillingRun billingRun = new BillingRun();
						billingRun.setBillingCycle(billingCycle);
						billingRun.setProcessDate(new Date());

						if (invoiceDate != null) {
							billingRun.setInvoiceDate(invoiceDate);
						} else if (billingCycle.getInvoiceDateProductionDelay() != null) {
							billingRun.setInvoiceDate(DateUtils.addDaysToDate(billingRun.getProcessDate(),
									billingCycle.getInvoiceDateProductionDelay()));
						} else if (billingRun.getProcessDate() != null) {
							billingRun.setInvoiceDate(billingRun.getProcessDate());
						}
						if (lastTransactionDate != null) {
							billingRun.setLastTransactionDate(lastTransactionDate);
						} else if (billingCycle.getTransactionDateDelay() != null) {
							billingRun.setLastTransactionDate(DateUtils.addDaysToDate(billingRun.getProcessDate(),
									billingCycle.getTransactionDateDelay()));
						} else {
							billingRun.setLastTransactionDate(billingRun.getProcessDate());
						}
						billingRun.setProcessType(billingCycleType);
						billingRun.setStatus(BillingRunStatusEnum.NEW);
						billingRunService.create(billingRun);
						//result.setNbItemsCorrectlyProcessed(++nbItemsToProcess);
						result.registerSucces();


			}
			result.setNbItemsToProcess(nbItemsToProcess+nbItemsProcessedWithError);
		} catch (Exception e) {
			result.registerError(e.getMessage());
			log.error("Failed to run billing ", e);
		}
	}

}
