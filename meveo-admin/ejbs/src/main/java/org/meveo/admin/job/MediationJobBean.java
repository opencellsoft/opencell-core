package org.meveo.admin.job;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.slf4j.Logger;

import com.blackbear.flatworm.ConfigurationReader;
import com.blackbear.flatworm.FileFormat;
import com.blackbear.flatworm.MatchedRecord;

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

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser,File file,String mappingConf) {
		log.debug("Running for user={}, parameter={}", currentUser, parameter);

		Provider provider = currentUser.getProvider();

		ParamBean parambean = ParamBean.getInstance();
		String meteringDir = parambean.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + provider.getCode() + File.separator + "imports" + File.separator
				+ "metering" + File.separator;

		outputDir = meteringDir + "output";
		rejectDir = meteringDir + "reject";

		File f = new File(outputDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(rejectDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		report = "";

		if (file != null) {
			cdrFileName = file.getAbsolutePath();
			result.setNbItemsToProcess(1);
			log.info("InputFiles job {} in progress...", file.getName());
			cdrFileName = file.getName();
			File currentFile = FileUtils.addExtension(file, ".processing");			
			try {								
				ConfigurationReader parser = new ConfigurationReader();
				FileFormat ff = parser.loadConfigurationFile( new ByteArrayInputStream(mappingConf.getBytes(StandardCharsets.UTF_8)));
				InputStream in = new FileInputStream(currentFile);
				BufferedReader bufIn = new BufferedReader(new InputStreamReader(in));
				MatchedRecord results = null;
				cdrParser.init(file);
				String line = null;
				int processed = 0;
				while ((results = ff.getNextRecord(bufIn)) != null) {	
					CDR cdr = (CDR) results.getBean("cdr");											
					try {
						createEdr(cdr, currentUser);
						outputCDR(cdr);
						processed++;
						result.registerSucces();
					} catch (CDRParsingException e) {
						log.warn("error while parsing cdr ",e);
						result.registerError("file=" + file.getName() + ", line=" + processed + ": " + e.getRejectionCause().name());
						rejectCDR(e.getCdr(), e.getRejectionCause());
					} catch (Exception e) {
						log.warn("error on reject cdr ",e);
						result.registerError("file=" + file.getName() + ", line=" + processed + ": " + e.getMessage());
						rejectCDR(line, CDRRejectionCauseEnum.TECH_ERR);
					}
				}

				if (processed == 0) {
					report += "\r\n file is empty ";
				}

				log.info("InputFiles job {} done.", file.getName());
				result.setDone(true);
			} catch (Exception e) {
				log.error("Failed to process CDR file {}", file.getName(), e);
				result.registerError(e.getMessage());
				FileUtils.moveFile(rejectDir, currentFile, file.getName());

			} finally {
				
				try {
					if (currentFile != null) {
						currentFile.delete();
					}
				} catch (Exception e) {
					report += "\r\n cannot delete " + cdrFileName;
				}

				try {
					if (rejectFileWriter != null) {
						rejectFileWriter.close();
						rejectFileWriter = null;
					}
				} catch (Exception e) {
					log.error("Failed to close rejected CDR writer for file {}", file.getName(), e);
				}

				try {
					if (outputFileWriter != null) {
						outputFileWriter.close();
						outputFileWriter = null;
					}
				} catch (Exception e) {
					log.error("Failed to close output file writer for file {}", file.getName(), e);
				}
			}


			result.setReport(report);
		} else {
			log.info("no file to process");
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void createEdr(Serializable cdr, User currentUser) throws CDRParsingException, BusinessException {
		List<EDR> edrs = cdrParser.getEDRList(cdr, currentUser.getProvider());
		if (edrs != null && edrs.size() > 0) {
			for (EDR edr : edrs) {
				createEdr(edr, currentUser);
			}
		}
	}

	private void outputCDR(CDR line) throws FileNotFoundException {
		if (outputFileWriter == null) {
			File outputFile = new File(outputDir + File.separator + cdrFileName + ".processed");
			outputFileWriter = new PrintWriter(outputFile);
		}
		outputFileWriter.println(line.toString());
	}

	private void rejectCDR(Serializable cdr, CDRRejectionCauseEnum reason) {

		if (rejectFileWriter == null) {
			File rejectFile = new File(rejectDir + File.separator + cdrFileName + ".rejected");
			try {
				rejectFileWriter = new PrintWriter(rejectFile);
			} catch (FileNotFoundException e) {
				log.error("Failed to create a rejection file {}", rejectFile.getAbsolutePath());
			}
		}
		if (cdr instanceof String) {
			rejectFileWriter.println(cdr + "\t" + reason.name());
		} else {
			rejectFileWriter.println(cdrParser.getCDRLine(cdr, reason.name()));
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createEdr(EDR edr, User currentUser) throws BusinessException {
		edrService.create(edr, currentUser, currentUser.getProvider());
	}

}
