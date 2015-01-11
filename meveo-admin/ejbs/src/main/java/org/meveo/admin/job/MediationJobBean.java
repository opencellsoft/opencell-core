package org.meveo.admin.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class MediationJobBean {

	@Inject
	private EdrService edrService;

	@Inject
	private CDRParsingService cdrParser;

	@Inject
	private Logger log;

	String cdrFileName;
	File cdrFile;
	String inputDir;
	String outputDir;
	PrintWriter outputFileWriter;
	String rejectDir;
	PrintWriter rejectFileWriter;
	String report;

	@Interceptors({ JobLoggingInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter,
			User currentUser) {
		Provider provider = currentUser.getProvider();

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
								createEdr(edr, currentUser);
							}
						}
						outputCDR(line);
						result.registerSucces();
					} catch (CDRParsingException e) {
						log.warn(e.getMessage());
						result.registerError("line " + processed + " :"
								+ e.getRejectionCause().name());
						rejectCDR(e.getCdr(), e.getRejectionCause());
					} catch (Exception e) {
						log.error(e.getMessage());
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
						log.error(e.getMessage());
					}
				} else {
					try {
						if (cdrReader != null) {
							cdrReader.close();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
					if (!cdrFile.delete()) {
						report += "\r\n cannot delete "
								+ cdrFile.getAbsolutePath();
					}
				}

				result.setReport(report);
			} else {
				log.info("No CDR to process.");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (cdrReader != null) {
					cdrReader.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			try {
				if (rejectFileWriter != null) {
					rejectFileWriter.close();
					rejectFileWriter = null;
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			try {
				if (outputFileWriter != null) {
					outputFileWriter.close();
					outputFileWriter = null;
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
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

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createEdr(EDR edr, User currentUser) throws BusinessException {
		edrService.create(edr, currentUser, currentUser.getProvider());
	}

}
