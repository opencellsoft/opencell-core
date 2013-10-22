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

import org.jboss.solder.logging.Logger;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class MediationJob implements Job {
	@Resource
	TimerService timerService;

	@Inject
	private ProviderService providerService;
	
	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	EdrService edrService;

	@Inject
	private Logger log;

	@EJB
	CDRParsingService cdrParser;

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
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute ASGMediationJob.");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		BufferedReader cdrReader = null;
		try {

			ParamBean parambean = ParamBean.getInstance("meveo-admin.properties");

			inputDir = parambean.getProperty("mediation.inputDirectory",
					"/tmp/meveo/metering/input");
			String cdrExtension = parambean.getProperty("mediation.extensions", "csv");
			ArrayList<String> cdrExtensions = new ArrayList<String>();
			cdrExtensions.add(cdrExtension);
			outputDir = parambean.getProperty("mediation.outputDirectory",
					"/tmp/meveo/metering/output");
			rejectDir = parambean.getProperty("mediation.rejectDirectory",
					"/tmp/meveo/metering/output");
			report = "";
			CDRParsingService.resetAccessPointCache();
			cdrFile = FileUtils.getFileForParsing(inputDir, cdrExtensions);
			if (cdrFile != null) {
				cdrFileName = cdrFile.getName();
				cdrParser.init(cdrFile);
				report = "parse " + cdrFileName;
				cdrFile = FileUtils.addExtension(cdrFile, ".processing");
				cdrReader = new BufferedReader(new InputStreamReader(new FileInputStream(cdrFile)));
				String line = null;
				int processed = 0;
				while ((line = cdrReader.readLine()) != null) {
					processed++;
					try {
						List<EDR> edrs = cdrParser.getEDRList(line);
						if (edrs != null && edrs.size() > 0) {
							for (EDR edr : edrs) {
								edrService.create(edr);
							}
						}
						outputCDR(line);
						result.registerSucces();
					} catch (CDRParsingException e) {
						result.registerError("line " + processed + " :"
								+ e.getRejectionCause().name());
						rejectCDR(e.getCdr(), e.getRejectionCause());
					} catch (Exception e) {
						result.registerError("line " + processed + " :" + e.getMessage());
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
						report += "\r\n cannot delete " + cdrFile.getAbsolutePath();
					}
				}
				result.setReport(report);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				File outputFile = new File(outputDir + File.separator + cdrFileName + ".processed");
				outputFileWriter = new PrintWriter(outputFile);
			}
			outputFileWriter.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void rejectCDR(Serializable cdr, CDRRejectionCauseEnum reason) {
		try {
			if (rejectFileWriter == null) {
				File rejectFile = new File(rejectDir + File.separator + cdrFileName + ".rejected");
				rejectFileWriter = new PrintWriter(rejectFile);
			}
			if (cdr instanceof String) {
				rejectFileWriter.println(cdr + "\t" + reason.name());
			} else {
				rejectFileWriter.println(cdrParser.getCDRLine(cdr, reason.name()));
			}
		} catch (Exception e) {
			e.printStackTrace();
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
