package org.meveo.admin.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;

@Startup
@Singleton
public class MediationJob implements Job {

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private Logger log;

	@Inject
	private CDRParsingService cdrParser;

	@Inject
	private MediationJobBean mediationJobBean;

	String cdrFileName;
	File cdrFile;
	String inputDir;
	String outputDir;
	PrintWriter outputFileWriter;
	String rejectDir;
	PrintWriter rejectFileWriter;
	String report;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("execute MediationJob.");

		Provider provider = currentUser.getProvider();
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		BufferedReader cdrReader = null;
		try {

			ParamBean parambean = ParamBean.getInstance();
			String meteringDir = parambean.getProperty("providers.rootDir",
					"/tmp/meveo/")
					+ File.separator
					+ provider.getCode()
					+ File.separator
					+ "imports"
					+ File.separator
					+ "metering"
					+ File.separator;

			inputDir = meteringDir + "input";
			String cdrExtension = parambean.getProperty("mediation.extensions",
					"csv");
			ArrayList<String> cdrExtensions = new ArrayList<String>();
			cdrExtensions.add(cdrExtension);
			outputDir = meteringDir + "output";
			rejectDir = meteringDir + "reject";
			// TODO creer les reps
			File f = new File(inputDir);
			if (!f.exists()) {
				f.mkdirs();
			}
			f = new File(outputDir);
			if (!f.exists()) {
				f.mkdirs();
			}
			f = new File(rejectDir);
			if (!f.exists()) {
				f.mkdirs();
			}
			report = "";
			CDRParsingService.resetAccessPointCache();
			cdrFile = FileUtils.getFileForParsing(inputDir, cdrExtensions);
			if (cdrFile != null) {
				cdrFileName = cdrFile.getName();
				cdrParser.init(cdrFile);
				report = "parse " + cdrFileName;
				cdrFile = FileUtils.addExtension(cdrFile, ".processing");
				cdrReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(cdrFile)));
				String line = null;
				int processed = 0;
				while ((line = cdrReader.readLine()) != null) {
					processed++;
					try {
						List<EDR> edrs = cdrParser.getEDRList(line);
						if (edrs != null && edrs.size() > 0) {
							for (EDR edr : edrs) {
								// edrService.create(edr);
								mediationJobBean.createEdr(edr);
							}
						}
						outputCDR(line);
						result.registerSucces();
					} catch (CDRParsingException e) {
						result.registerError("line " + processed + " :"
								+ e.getRejectionCause().name());
						rejectCDR(e.getCdr(), e.getRejectionCause());
					} catch (Exception e) {
						result.registerError("line " + processed + " :"
								+ e.getMessage());
						rejectCDR(line, CDRRejectionCauseEnum.TECH_ERR);
					}
				}
				result.setNbItemsToProcess(processed);
				if (FileUtils.getFileForParsing(inputDir, cdrExtensions) != null) {
					result.setDone(false);
				}
				if (processed == 0) {
					FileUtils.replaceFileExtension(cdrFile, ".csv.processed");
					report += "\r\n file is empty ";
					try {
						if (cdrReader != null) {
							cdrReader.close();
						}
					} catch (Exception e) {
					}
				} else {
					try {
						if (cdrReader != null) {
							cdrReader.close();
						}
					} catch (Exception e) {
					}
					if (!cdrFile.delete()) {
						report += "\r\n cannot delete "
								+ cdrFile.getAbsolutePath();
					}
				}
				result.setReport(report);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (rejectFileWriter != null) {
					rejectFileWriter.close();
					rejectFileWriter = null;
				}
			} catch (Exception e) {
			}
			try {
				if (outputFileWriter != null) {
					outputFileWriter.close();
					outputFileWriter = null;
				}
			} catch (Exception e) {
			}

		}
		result.close("");
		return result;
	}

	private void outputCDR(String line) {
		try {
			if (outputFileWriter == null) {
				File outputFile = new File(outputDir + File.separator
						+ cdrFileName + ".processed");
				outputFileWriter = new PrintWriter(outputFile);
			}
			outputFileWriter.println(line);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void rejectCDR(Serializable cdr, CDRRejectionCauseEnum reason) {
		try {
			if (rejectFileWriter == null) {
				File rejectFile = new File(rejectDir + File.separator
						+ cdrFileName + ".rejected");
				rejectFileWriter = new PrintWriter(rejectFile);
			}
			if (cdr instanceof String) {
				rejectFileWriter.println(cdr + "\t" + reason.name());
			} else {
				rejectFileWriter.println(cdrParser.getCDRLine(cdr,
						reason.name()));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
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
