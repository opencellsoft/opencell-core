package org.meveo.admin.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;


@Stateless
public class CatalogImportJobBean {

	@Inject
	private PricePlanMatrixService pricePlanService;

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	@Inject
	private Logger log;

	String fileName;
	File file;
	String inputDir;
	String outputDir;
	PrintWriter outputFileWriter;
	String rejectDir;
	PrintWriter rejectFileWriter;
	String report;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class })
	public void execute(JobExecutionResultImpl result, String parameter,
			User currentUser) {
		Provider provider = currentUser.getProvider();

		InputStream inputFileStream = null;
		try {

			ParamBean parambean = ParamBean.getInstance();
			String catalogDir = parambean.getProperty("providers.rootDir",
					"/tmp/meveo/")
					+ File.separator
					+ provider.getCode()
					+ File.separator
					+ "imports"
					+ File.separator
					+ "catalog"
					+ File.separator;

			inputDir = catalogDir + "input";
			String fileExtension = parambean.getProperty("catalogImport.extensions",
					"xls");
			ArrayList<String> fileExtensions = new ArrayList<String>();
			fileExtensions.add(fileExtension);
			outputDir = catalogDir + "output";
			rejectDir = catalogDir + "reject";
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
			file = FileUtils.getFileForParsing(inputDir, fileExtensions);
			if (file != null) {
				fileName = file.getName();
				report = "parse " + fileName;
				file = FileUtils.addExtension(file, ".processing");
				inputFileStream = new FileInputStream(file);
				
				int processed= pricePlanService.importFromExcel(em, inputFileStream, currentUser, provider);

				result.setNbItemsToProcess(processed);

				if (FileUtils.getFileForParsing(inputDir, fileExtensions) != null) {
					result.setDone(false);
				}

				if (processed == 0) {
					FileUtils.replaceFileExtension(file, ".xsl.processed");
					report += "\r\n file is empty ";
					try {
						if (inputFileStream != null) {
							inputFileStream.close();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				} else {
					try {
						if (inputFileStream != null) {
							inputFileStream.close();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
					if (!file.delete()) {
						report += "\r\n cannot delete "
								+ file.getAbsolutePath();
					}
				}

				result.setReport(report);
			} else {
				log.info("No file to process.");
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


}
