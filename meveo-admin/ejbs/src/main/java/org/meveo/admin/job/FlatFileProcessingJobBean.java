package org.meveo.admin.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ExcelToCsv;
import org.meveo.commons.utils.FileUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;


@Stateless
public class FlatFileProcessingJobBean {

	@Inject
	private Logger log;
	
	@Inject
	private ScriptInstanceService scriptInstanceService;

	String fileName;
	String inputDir;
	String outputDir;
	PrintWriter outputFileWriter;
	String rejectDir;
	PrintWriter rejectFileWriter;
	String report;
    String username;
    
	

	@SuppressWarnings({ "unused", "unused" })
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void execute(JobExecutionResultImpl result, String inputDir, User currentUser,File file,String mappingConf, String scriptInstanceFlowCode, String recordVariableName, Map<String, Object> context, String originFilename,String formatTransfo) {
		log.debug("Running for user={}, inputDir={}, scriptInstanceFlowCode={},formatTransfo={}", currentUser, inputDir,scriptInstanceFlowCode,formatTransfo);
		Provider provider = currentUser.getProvider();

		outputDir =inputDir + File.separator + "output";
		rejectDir =inputDir + File.separator + "reject";

		File f = new File(outputDir);
		if (!f.exists()) {
			log.debug("outputDir {} not exist",outputDir);
			f.mkdirs();
			log.debug("outputDir {} creation ok",outputDir);
		}
		f = new File(rejectDir);
		if (!f.exists()) {
			log.debug("rejectDir {} not exist",rejectDir);
			f.mkdirs();
			log.debug("rejectDir {} creation ok",rejectDir);
		}
		report = "";

		if (file != null) {
			fileName = file.getName();
			ScriptInterface script = null;
			BeanReader beanReader = null;
			File currentFile = null;
			try {
				log.info("InputFiles job {} in progress...",  file.getAbsolutePath());
				if("XLSX_TO_CSV".equals(formatTransfo)){
				     ExcelToCsv excelToCsv = new ExcelToCsv();
					 excelToCsv.convertExcelToCSV(file.getAbsolutePath(), file.getParent(), ";");
					 file.delete();
					 file = new File( file.getAbsolutePath().replaceAll("xlsx", "csv"));					 
				}
				currentFile = FileUtils.addExtension(file, ".processing");
								
				result.setNbItemsToProcess(1);										
				Class<org.meveo.service.script.ScriptInterface> flowScriptClass = scriptInstanceService.getScriptInterface(provider,scriptInstanceFlowCode);
				Object recordObject = null;
				int processed = 0;
				script = flowScriptClass.newInstance();
				script.init(context, provider,currentUser);
				
				
//				ConfigurationReader parser = new ConfigurationReader();
//				FileFormat ff = parser.loadConfigurationFile( new ByteArrayInputStream(mappingConf.getBytes(StandardCharsets.UTF_8)));
//				InputStream in = new FileInputStream(currentFile);
//				BufferedReader bufIn = new BufferedReader(new InputStreamReader(in));
//				MatchedRecord record = null;
//              while ((record = ff.getNextRecord(bufIn)) != null) {	
//				Object recordBean = record.getBean(recordVariableName);			
												
		        StreamFactory factory = StreamFactory.newInstance();		       
		        factory.load( new ByteArrayInputStream(mappingConf.getBytes(StandardCharsets.UTF_8)));
		        beanReader = factory.createReader(recordVariableName, currentFile);
		      
				while ((recordObject = beanReader.read()) != null) {																
					try {							
						log.debug("recordObject:{}",recordObject.toString());
						Map<String, Object> executeParams = new HashMap<String, Object>();
						executeParams.put(recordVariableName, recordObject);
						executeParams.put(originFilename, fileName);
						script.execute(executeParams,provider,currentUser);	 							
						outputRecord(recordObject);
						result.registerSucces();
					} catch (Exception e) {
						log.warn("error on reject record ",e);
						result.registerError("file=" + fileName + ", line=" + processed + ": " + e.getMessage());
						rejectRecord(recordObject, e.getMessage());
					} finally{
						processed++;
					}
				}

				if (processed == 0) {
					report += "\r\n file is empty ";
				}

				log.info("InputFiles job {} done.", fileName);
			} catch (Exception e) {
				log.error("Failed to process Record file {}", fileName, e);
				result.registerError(e.getMessage());
				FileUtils.moveFile(rejectDir, currentFile, fileName);
			} finally {
				try{
					if(script!=null){
						script.finalize(context,provider,currentUser);
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
					log.error("Failed to close rejected Record writer for file {}", fileName, e);
				}

				try {
					if (outputFileWriter != null) {
						outputFileWriter.close();
						outputFileWriter = null;
					}
				} catch (Exception e) {
					log.error("Failed to close output file writer for file {}", fileName, e);
				}
			}
			result.setReport(report);
		} else {
			log.info("no file to process");
		}

	}

	private void outputRecord(Object record) throws FileNotFoundException {
		if (outputFileWriter == null) {
			File outputFile = new File(outputDir + File.separator + fileName + ".processed");
			outputFileWriter = new PrintWriter(outputFile);
		}
		outputFileWriter.println(record.toString());
	}

	private void rejectRecord(Object record, String reason) {

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
