package org.meveo.admin.job;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.sepa.SepaService;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.TimerEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class SepaRejectedTransactionsJob implements Job {
	private Logger log = LoggerFactory.getLogger(SepaRejectedTransactionsJob.class);

	@Resource
	private TimerService timerService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private UserService userService;

	@Inject
	private SepaService sepaService;

	ParamBean param = ParamBean.getInstance();

	String importDir = param
			.getProperty("sepaRejectedTransactionsJob.importDir", "/tmp/meveo/SepaRejectedTransactions");

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	@Asynchronous
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(TimerInfo info, User currentUser) {
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		if (!running && (info.isActive() || currentUser != null)) {
			try {
				running = true;
				if (currentUser == null) {
					currentUser = userService.findByIdLoadProvider(info.getUserId());
				}
				Provider provider = currentUser.getProvider();

				String dirIN = importDir + File.separator + provider.getCode() + File.separator
						+ "rejectedSepaTransactions" + File.separator + "input";
				log.info("dirIN=" + dirIN);
				String dirOK = importDir + File.separator + provider.getCode() + File.separator
						+ "rejectedSepaTransactions" + File.separator + "output";
				String dirKO = importDir + File.separator + provider.getCode() + File.separator
						+ "rejectedSepaTransactions" + File.separator + "reject";
				String prefix = param.getProperty("sepaRejectedTransactionsJob.file.prefix", "Pain002_");
				String ext = param.getProperty("sepaRejectedTransactionsJob.file.extension", "xml");

				try {

					File dir = new File(dirIN);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					List<File> files = sepaService.getFilesToProcess(dir, prefix, ext);
					int numberOfFiles = files.size();
					log.info("InputFiles job " + numberOfFiles + " to import");
					result.setNbItemsToProcess(numberOfFiles);
					for (File file : files) {
						File currentFile = null;
						try {
							log.info("InputFiles job " + file.getName() + " in progres");
							currentFile = FileUtils.addExtension(file, ".processing");
							sepaService.processRejectFile(currentFile, file.getName(), provider);
							FileUtils.moveFile(dirOK, currentFile, file.getName());
							log.info("InputFiles job " + file.getName() + " done");
							result.registerSucces();
						} catch (Exception e) {
							result.registerError(e.getMessage());
							log.info("InputFiles job " + file.getName() + " failed");
							FileUtils.moveFile(dirKO, currentFile, file.getName());
							log.error(e.getMessage());
						} finally {
							if (currentFile != null)
								currentFile.delete();
						}
					}

				} catch (Exception e) {
					log.error(e.getMessage());

				}

				result.close("");
				jobExecutionService.persistResult(this, result, info, currentUser, getJobCategory());
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				running = false;
			}
		}
	}

	public Timer createTimer(ScheduleExpression scheduleExpression, TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		timerConfig.setPersistent(false);
		return this.timerService.createCalendarTimer(scheduleExpression, timerConfig);
	}

	boolean running = false;

	@Override
	@Timeout
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void trigger(Timer timer) {
		execute((TimerInfo) timer.getInfo(), null);
	}

	public Collection<Timer> getTimers() {
		return this.timerService.getTimers();
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

	@Override
	public void cleanAllTimers() {
		Collection<Timer> alltimers = timerService.getTimers();
		log.info("Cancel " + alltimers.size() + " timers for" + this.getClass().getSimpleName());
		for (Timer timer : alltimers) {
			try {
				timer.cancel();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.ACCOUNT_RECEIVABLES;
	}

}
