package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
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
		List<BillingRun> billingRuns = new ArrayList<BillingRun>();
		if(parameter!=null && parameter.trim().length()>0){
			try{
				billingRuns.add(billingRunService.getBillingRunById(Long.parseLong(parameter), provider));
			} catch (Exception e){
				e.printStackTrace();
				result.registerError(e.getMessage());
			}
		}else {
			billingRuns = billingRunService.getValidatedBillingRuns(provider);
		}
		log.info("# billingRuns to process:" + billingRuns.size());
			for (BillingRun billingRun : billingRuns) {
				try {
					
			        ParamBean param = ParamBean.getInstance("meveo-admin.properties");
			        String invoicesDir = param.getProperty("meveo.dir","/tmp/meveo");
				        File billingRundir = new File(invoicesDir + File.separator +provider.getCode()+File.separator+"invoices"+File.separator+"xml"+File.separator+billingRun.getId());
				        billingRundir.mkdirs();
				        for (Invoice invoice : billingRun.getInvoices()) {
				        	long startDate=System.currentTimeMillis();
				            xmlInvoiceCreator.createXMLInvoice(invoice, billingRundir);
				            log.info("invoice creation delay : " +(System.currentTimeMillis()-startDate));
				        }
				        billingRun.setXmlInvoiceGenerated(true);
				        billingRunService.update(billingRun);
				} catch (Exception e) {
					e.printStackTrace();
					result.registerError(e.getMessage());
				}
			}
		
		result.close("");
		return result;
	}


	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression, TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		//timerConfig.setPersistent(false);
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

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}
}
