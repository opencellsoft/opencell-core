package org.meveo.admin.job;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class XMLInvoiceGenerationJob implements Job {

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
	BillingAccountService billingAccountService;
	
	@Inject
	XMLInvoiceCreator xmlInvoiceCreator;
	
	@Inject
	InvoiceService invoiceService;


	private Logger log = Logger.getLogger(XMLInvoiceGenerationJob.class.getName());

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute XMLInvoiceGenerationJob.");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		List<BillingRun> billingRuns = billingRunService.getbillingRuns(BillingRunStatusEnum.VALIDATED);
		log.info("# billingRuns to process:" + billingRuns.size());
		for (BillingRun billingRun : billingRuns) {
			try {

		        ParamBean param = ParamBean.getInstance("meveo-admin.properties");
				 String invoicesDir = param.getProperty("invoices.dir");
			        File billingRundir = new File(invoicesDir + File.separator + billingRun.getId());

			        for (Invoice invoice : billingRun.getInvoices()) {
			            setInvoiceNumber(billingRun.getProvider(), invoice);

			            xmlInvoiceCreator.createXMLInvoice(invoice, billingRundir);
			            
			            BillingAccount billingAccount = invoice.getBillingAccount();
			            Date nextCalendarDate = billingAccount.getBillingCycle().getNextCalendarDate();
			            billingAccount.setNextInvoiceDate(nextCalendarDate);
			            billingAccountService.update(billingAccount);
			        }
			        billingRun.setDisabled(true);
			
			} catch (Exception e) {
				e.printStackTrace();
				result.registerError(e.getMessage());
			}
		}
		result.close("");
		return result;
	}
	 public void setInvoiceNumber(Provider provider, Invoice invoice) {
	        String prefix = provider.getInvoicePrefix();
	        if (prefix == null) {
	            prefix = "";
	        }
	        long nextInvoiceNb = getNextValue(provider);
	        StringBuffer num1 = new StringBuffer("000000000");
	        num1.append(nextInvoiceNb + "");
	        String invoiceNumber = num1.substring(num1.length() - 9);
	        int key = 0;
	        for (int i = 0; i < invoiceNumber.length(); i++) {
	            key = key + Integer.parseInt(invoiceNumber.substring(i, i + 1));
	        }
	        invoice.setInvoiceNumber(prefix + invoiceNumber + "-" + key % 10);
	        invoiceService.update(invoice);
	    }
	 

	    public synchronized long getNextValue(Provider provider) {
	        long result = 0;
	        if (provider != null) {
	            long currentInvoiceNbre = provider.getCurrentInvoiceNb() != null ? provider.getCurrentInvoiceNb() : 0;
	            result = 1 + currentInvoiceNbre;
	            provider.setCurrentInvoiceNb(result);
	            providerService.update(provider);
	        }
	        return result;
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
