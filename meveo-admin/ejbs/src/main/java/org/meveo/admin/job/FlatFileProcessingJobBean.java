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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.ScriptInstance;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.service.script.JavaCompilerManager;
import org.slf4j.Logger;

import com.blackbear.flatworm.ConfigurationReader;
import com.blackbear.flatworm.FileFormat;
import com.blackbear.flatworm.MatchedRecord;


@Stateless
public class FlatFileProcessingJobBean {



	@Inject
	private Logger log;
	
	@Inject
	private JavaCompilerManager javaCompilerManager;

	String cdrFileName;
	File cdrFile;
	String inputDir;
	String outputDir;
	PrintWriter outputFileWriter;
	String rejectDir;
	PrintWriter rejectFileWriter;
	String report;
    String batchName;
    String originBatch;
    String username;
    
	static MessageDigest messageDigest = null;
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			
		}
	}

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser,File file,String mappingConf, ScriptInstance scriptInstanceFlow) {
		log.debug("Running for user={}, parameter={}", currentUser, parameter);

		Provider provider = currentUser.getProvider();
		init(file);

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
				int processed = 0;
				while ((results = ff.getNextRecord(bufIn)) != null) {	
					CDR cdr = (CDR) results.getBean("cdr");											
					try {
						Class<org.meveo.service.script.ScriptInterface> mediationFlowScript = javaCompilerManager.getScriptInterface(provider,scriptInstanceFlow.getCode());
						Map<String, Object> executeParams = new HashMap<String, Object>();
						executeParams.put("cdr", cdr);
						executeParams.put("originBatch", getOriginBatch());
						executeParams.put("originRecord", getOriginRecord(cdr));
						mediationFlowScript.newInstance().execute(executeParams,provider);		
				    					    		 				    	
						outputCDR(cdr);
						processed++;
						result.registerSucces();

					} catch (Exception e) {
						log.warn("error on reject cdr ",e);
						result.registerError("file=" + file.getName() + ", line=" + processed + ": " + e.getMessage());
						rejectCDR(cdr, CDRRejectionCauseEnum.TECH_ERR);
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


	private void outputCDR(CDR line) throws FileNotFoundException {
		if (outputFileWriter == null) {
			File outputFile = new File(outputDir + File.separator + cdrFileName + ".processed");
			outputFileWriter = new PrintWriter(outputFile);
		}
		outputFileWriter.println(line.toString());
	}

	private void rejectCDR(CDR cdr, CDRRejectionCauseEnum reason) {

		if (rejectFileWriter == null) {
			File rejectFile = new File(rejectDir + File.separator + cdrFileName + ".rejected");
			try {
				rejectFileWriter = new PrintWriter(rejectFile);
			} catch (FileNotFoundException e) {
				log.error("Failed to create a rejection file {}", rejectFile.getAbsolutePath());
			}
		}
			rejectFileWriter.println(cdr.toString()+";"+reason);
	}
	
	public void init(File CDRFile) {
		batchName = "CDR_" + CDRFile.getName();
	}

	
	public void initByApi(String username, String ip) {
		originBatch = "API_" + ip;
		this.username = username;
	}

	
	public String getOriginBatch() {
		if (StringUtils.isBlank(originBatch)) {
			return batchName == null ? "CDR_CONS_CSV" : batchName;
		} else {
			return originBatch;
		}
	}
	
	public String getOriginRecord(Serializable object) {
		String result = null;
		if (StringUtils.isBlank(username)) {
			CDR cdr = (CDR) object;
			result = cdr.toString();

			if (messageDigest != null) {
				synchronized (messageDigest) {
					messageDigest.reset();
					messageDigest.update(result.getBytes(Charset.forName("UTF8")));
					final byte[] resultByte = messageDigest.digest();
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < resultByte.length; ++i) {
						sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
					}
					result = sb.toString();
				}
			}
		} else {
			return username + "_" + new Date().getTime();
		}

		return result;
	}
}
