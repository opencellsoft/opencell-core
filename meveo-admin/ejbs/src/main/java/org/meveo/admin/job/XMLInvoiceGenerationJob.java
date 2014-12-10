package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class XMLInvoiceGenerationJob implements Job {

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private XMLInvoiceCreator xmlInvoiceCreator;

	private Logger log = LoggerFactory.getLogger(XMLInvoiceGenerationJob.class);

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("execute XMLInvoiceGenerationJob.");

		Provider provider = currentUser.getProvider();
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		List<BillingRun> billingRuns = new ArrayList<BillingRun>();

		if (parameter != null && parameter.trim().length() > 0) {
			try {
				billingRuns.add(billingRunService.getBillingRunById(
						Long.parseLong(parameter), provider));
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		} else {
			billingRuns = billingRunService.getValidatedBillingRuns(provider);
		}

		log.info("billingRuns to process={}", billingRuns.size());

		for (BillingRun billingRun : billingRuns) {
			try {

				ParamBean param = ParamBean.getInstance();
				String invoicesDir = param.getProperty("providers.dir",
						"/tmp/meveo");
				File billingRundir = new File(invoicesDir + File.separator
						+ provider.getCode() + File.separator + "invoices"
						+ File.separator + "xml" + File.separator
						+ billingRun.getId());
				billingRundir.mkdirs();
				for (Invoice invoice : billingRun.getInvoices()) {
					long startDate = System.currentTimeMillis();
					Future<Boolean> xmlCreated = xmlInvoiceCreator
							.createXMLInvoice(invoice, billingRundir);
					xmlCreated.get();
					log.info("invoice creation delay :"
							+ (System.currentTimeMillis() - startDate)
							+ ",xmlCreated={1} " + xmlCreated.get() + "");
				}
				billingRun.setXmlInvoiceGenerated(true);
				billingRunService.update(billingRun);
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		}

		result.close("");

		return result;
	}

	@Override
	public Timer createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		timerConfig.setPersistent(false);

		return timerService
				.createCalendarTimer(scheduleExpression, timerConfig);

	}

	boolean running = false;

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (!running && info.isActive()) {
			try {
				running = true;

				User currentUser = userService.findById(info.getUserId());
				JobExecutionResult result = execute(info.getParametres(),
						currentUser);
				jobExecutionService.persistResult(this, result, info,
						currentUser);
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				running = false;
			}
		}
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

	@Override
	public void cleanAllTimers() {
		Collection<Timer> alltimers = timerService.getTimers();
		log.info("Cancel " + alltimers.size() + " timers for"
				+ this.getClass().getSimpleName());
		
		for (Timer timer : alltimers) {
			try {
				timer.cancel();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}
}
