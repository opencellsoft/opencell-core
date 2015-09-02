package org.meveo.admin.job;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.script.JavaCompilerManager;
import org.meveo.service.script.ScriptInterface;
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

	String fileName;
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
	public void execute(JobExecutionResultImpl result, String inputDir, User currentUser,File file,String mappingConf, String scriptInstanceFlowCode, String recordVariableName, Map<String, Object> context, String originFilename) {
		log.debug("Running for user={}, inputDir={}", currentUser, inputDir);

		Provider provider = currentUser.getProvider();

		outputDir =inputDir + File.separator + "output";
		rejectDir =inputDir + File.separator + "reject";

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
			fileName = file.getAbsolutePath();
			result.setNbItemsToProcess(1);
			log.info("InputFiles job {} in progress...", file.getName());
			fileName = file.getName();
			File currentFile = FileUtils.addExtension(file, ".processing");			
			Class<org.meveo.service.script.ScriptInterface> flowScriptClass = javaCompilerManager.getScriptInterface(provider,scriptInstanceFlowCode);
			ScriptInterface script = null;
			try {					
				ConfigurationReader parser = new ConfigurationReader();
				FileFormat ff = parser.loadConfigurationFile( new ByteArrayInputStream(mappingConf.getBytes(StandardCharsets.UTF_8)));
				InputStream in = new FileInputStream(currentFile);
				BufferedReader bufIn = new BufferedReader(new InputStreamReader(in));
				MatchedRecord record = null;
				int processed = 0;
				script = flowScriptClass.newInstance();
				script.init(context, provider);
				while ((record = ff.getNextRecord(bufIn)) != null) {	
					Object recordBean = record.getBean(recordVariableName);											
					try {						
						Map<String, Object> executeParams = new HashMap<String, Object>();
						executeParams.put(recordVariableName, recordBean);
						executeParams.put(originFilename, file.getName());
						script.execute(executeParams,provider);	 				    	
						outputRecord(record);
						result.registerSucces();
					} catch (Exception e) {
						log.warn("error on reject record ",e);
						result.registerError("file=" + file.getName() + ", line=" + processed + ": " + e.getMessage());
						rejectRecord(record, e.getMessage());
					} finally{
						processed++;
					}
				}

				if (processed == 0) {
					report += "\r\n file is empty ";
				}

				log.info("InputFiles job {} done.", file.getName());
			} catch (Exception e) {
				log.error("Failed to process Record file {}", file.getName(), e);
				result.registerError(e.getMessage());
				FileUtils.moveFile(rejectDir, currentFile, file.getName());
			} finally {
				try{
					if(script!=null){
						script.finalize(context,provider);
					}
				} catch(Exception e){
					report+="\r\n error in script finailzation"+e.getMessage();
				}
				try {
					if (currentFile != null) {
						currentFile.delete();
					}
				} catch (Exception e) {
					report += "\r\n cannot delete " + fileName;
				}

				try {
					if (rejectFileWriter != null) {
						rejectFileWriter.close();
						rejectFileWriter = null;
					}
				} catch (Exception e) {
					log.error("Failed to close rejected Record writer for file {}", file.getName(), e);
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

	private void outputRecord(MatchedRecord record) throws FileNotFoundException {
		if (outputFileWriter == null) {
			File outputFile = new File(outputDir + File.separator + fileName + ".processed");
			outputFileWriter = new PrintWriter(outputFile);
		}
		outputFileWriter.println(record.toString());
	}

	private void rejectRecord(MatchedRecord record, String reason) {

		if (rejectFileWriter == null) {
			File rejectFile = new File(rejectDir + File.separator + fileName + ".rejected");
			try {
				rejectFileWriter = new PrintWriter(rejectFile);
			} catch (FileNotFoundException e) {
				log.error("Failed to create a rejection file {}", rejectFile.getAbsolutePath());
			}
		}
			rejectFileWriter.println(record.toString()+";"+reason);
	}

}
