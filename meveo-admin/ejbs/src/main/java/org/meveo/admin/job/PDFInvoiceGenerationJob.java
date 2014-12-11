package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

import org.meveo.model.admin.User;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class PDFInvoiceGenerationJob implements Job {

	private Logger log = LoggerFactory.getLogger(PDFInvoiceGenerationJob.class);

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private PDFParametersConstruction pDFParametersConstruction;

	@Inject
	private PDFFilesOutputProducer pDFFilesOutputProducer;

	@Inject
	private BillingRunService billingRunService;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("execute PDFInvoiceGenerationJob.");

		JobExecutionResultImpl result = new JobExecutionResultImpl();
		List<Invoice> invoices = new ArrayList<Invoice>();
		if (parameter != null && parameter.trim().length() > 0) {
			try {
				invoices = invoiceService.getInvoices(billingRunService
						.getBillingRunById(Long.parseLong(parameter),
								currentUser.getProvider()));
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		} else {
			invoices = invoiceService.getValidatedInvoicesWithNoPdf(null);
		}

		log.info("PDFInvoiceGenerationJob number of invoices to process="
				+ invoices.size());
		for (Invoice invoice : invoices) {
			try {
				Map<String, Object> parameters = pDFParametersConstruction
						.constructParameters(invoice);
				log.info("PDFInvoiceGenerationJob parameters=" + parameters);
				Future<Boolean> isPdfgenerated = pDFFilesOutputProducer
						.producePdf(parameters, result);
				isPdfgenerated.get();
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
