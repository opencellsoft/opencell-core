package org.meveo.admin.job.importexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.slf4j.Logger;

@Stateless
public class ImportCatalogJobBean {

	@Inject
	private PricePlanMatrixService pricePlanService;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private Logger log;

	String fileName;
	File file;
	String inputDir;
	String outputDir;
	String rejectDir;
	String report;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser) {
		Provider provider = currentUser.getProvider();

		InputStream inputFileStream = null;
		try {

			ParamBean parambean = ParamBean.getInstance();
			String catalogDir = parambean.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator
					+ provider.getCode() + File.separator + "imports" + File.separator + "catalog" + File.separator;

			inputDir = catalogDir + "input";
			String fileExtension = parambean.getProperty("catalogImport.extensions", "xls");
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

				int processed = 0;
				try {
					processed = pricePlanService.importFromExcel(em, inputFileStream, currentUser, provider);
				} catch (BusinessException e) {
					report += "Error " + e.getMessage();
					log.error(e.getMessage());
				}
				result.setNbItemsToProcess(processed);

				if (FileUtils.getFileForParsing(inputDir, fileExtensions) != null) {
					result.setDone(false);
				}

				if (processed > 0) {
					File fi = FileUtils.replaceFileExtension(file, ".processed");
					FileUtils.moveFile(outputDir, fi, null);
					try {
						if (inputFileStream != null) {
							inputFileStream.close();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				} else {
					File fi = FileUtils.replaceFileExtension(file, "");
					FileUtils.moveFile(rejectDir, fi, null);
					try {
						if (inputFileStream != null) {
							inputFileStream.close();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					} finally {
						fi.delete();
					}
				}

				result.setReport(report);
			} else {
				log.info("No file to process.");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
