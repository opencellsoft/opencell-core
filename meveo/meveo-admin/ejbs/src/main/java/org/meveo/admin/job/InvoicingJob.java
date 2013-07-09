package org.meveo.admin.job;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.DateUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class InvoicingJob implements Job {

	@Resource
	TimerService timerService;

	@Inject
	private ProviderService providerService;
	
	@Inject
	JobExecutionService jobExecutionService;


	@Inject
	private BillingRunService billingRunService;
	
	@Inject
	private RatedTransactionService ratedTransactionService;
	
	@Inject
	private BillingAccountService billingAccountService;
	
	@EJB
	private InvoiceService invoiceService;


	private Logger log = Logger.getLogger(InvoicingJob.class.getName());

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute RecurringRatingJob.");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		try {
			List<BillingRun> billingRuns = billingRunService.getbillingRuns(BillingRunStatusEnum.NEW,BillingRunStatusEnum.ON_GOING);
			log.info("# billingRuns to process:" + billingRuns.size());
			for (BillingRun billingRun : billingRuns) {
				try {
					
					BillingCycle billingCycle = billingRun.getBillingCycle();

			        boolean entreprise = billingRun.getProvider().isEntreprise();

			        Date startDate = billingRun.getStartDate();
			        Date endDate = billingRun.getEndDate();

			        List<BillingAccount> billingAccounts = null;
			        if (billingCycle != null) {
			            billingAccounts = billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);
			        } else {
			            billingAccounts = billingRun.getSelectedBillingAccount();
			        }
			        ratedTransactionService.sumbillingRunAmounts(billingRun, billingAccounts, RatedTransactionStatusEnum.OPEN, entreprise);
			       
			        billingRun.setBillingAccountNumber(billingAccounts.size());
			        billingRun.setBillableBillingAcountNumber(billingAccounts.size());
			        billingRun.setProcessDate(new Date());
			       
			        createAgregatesAndInvoice(billingRun,billingAccounts);
			        
			       
				} catch (Exception e) {
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.close("");
		return result;
	}
	

	public void createAgregatesAndInvoice(BillingRun billingRun,List<BillingAccount> billingAccounts) throws BusinessException, Exception {
		 for (BillingAccount billingAccount : billingAccounts) {
            BillingCycle billingCycle = billingRun.getBillingCycle();
            if (billingCycle == null) {
                billingCycle = billingAccount.getBillingCycle();
            }
            Invoice invoice = new Invoice();
            invoice.setBillingAccount(billingAccount);
            invoice.setBillingRun(billingRun);
            invoice.setAuditable(billingRun.getAuditable());
            invoice.setProvider(billingRun.getProvider());
            Date invoiceDate = new Date();
            invoice.setInvoiceDate(invoiceDate);

            Integer delay = billingCycle.getDueDateDelay();
            Date dueDate = invoiceDate;
            if (delay != null) {
                dueDate = DateUtils.addDaysToDate(invoiceDate, delay);
            }
            invoice.setDueDate(dueDate);

            invoice.setPaymentMethod(billingAccount.getPaymentMethod());
            invoice.setProvider(billingRun.getProvider());
            invoiceService.create(invoice);
            ratedTransactionService.createInvoiceAndAgregates(billingRun, billingAccount,invoice);

	        ratedTransactionService.updateRatedTransactions(billingRun, billingAccount,invoice);
	        
            StringBuffer num1 = new StringBuffer("000000000");
            num1.append(invoice.getId() + "");
            String invoiceNumber = num1.substring(num1.length() - 9);
            int key = 0;
            for (int i = 0; i < invoiceNumber.length(); i++) {
                key = key + Integer.parseInt(invoiceNumber.substring(i, i + 1));
            }
            invoice.setTemporaryInvoiceNumber(invoiceNumber + "-" + key % 10);
            invoiceService.update(invoice);
            
            billingRun.setStatus(BillingRunStatusEnum.TERMINATED);
	        billingRunService.update(billingRun);
	        
	        billingAccount.setBillingRun(billingRun);
	        billingAccountService.update(billingAccount);
               
        }
		
	}

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression, TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		Timer timer = timerService.createCalendarTimer(scheduleExpression, timerConfig);
		return timer.getHandle();
	}

	boolean running = false;

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (!running && info.isActive()) {
			try {
				running = true;
                Provider provider=providerService.findById(info.getProviderId());
                JobExecutionResult result=execute(info.getParametres(),provider);
                jobExecutionService.persistResult(this, result,info,provider);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				running = false;
			}
		}
	}

	@Override
	public Collection<Timer> getTimers() {
		// TODO Auto-generated method stub
		return timerService.getTimers();
	}

}
