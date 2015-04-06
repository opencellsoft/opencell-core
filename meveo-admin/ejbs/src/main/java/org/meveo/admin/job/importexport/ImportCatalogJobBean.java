package org.meveo.admin.job.importexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.IteratorUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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

	@Inject
	private Logger log;

	String fileName;
	File file;
	String inputDir;
	String outputDir;
	String rejectDir;
	String report;

	String[] colNames = { "Priceplan code", "Priceplan description", "Charge code", "Seller", "Country", "Currency", "Start appli.", "End appli.", "Offer code", "Priority",
			"Amount w/out tax", "Amount with tax", "Min quantity", "Max quantity", "Criteria 1", "Criteria 2", "Criteria 3", "Criteria EL", "Start rating", "End rating",
			"Min subscr age", "Max subscr age", "Validity calendar" };

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser) {
		Provider provider = currentUser.getProvider();

		InputStream excelInputStream = null;
		try {

			ParamBean parambean = ParamBean.getInstance();
			String catalogDir = parambean.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + provider.getCode() + File.separator + "imports" + File.separator
					+ "catalog" + File.separator;

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
				report = "parse " + fileName + ";";
				file = FileUtils.addExtension(file, ".processing");
				excelInputStream = new FileInputStream(file);

				int processed = 0;
				Workbook workbook;

				// TODO cache entities
				try {
					workbook = WorkbookFactory.create(excelInputStream);
					Sheet sheet = workbook.getSheetAt(0);

					Iterator<Row> rowIterator = sheet.rowIterator();
					Object[] rowsObj = IteratorUtils.toArray(rowIterator);
					Row row0 = (Row) rowsObj[0];
					Object[] headerCellsObj = IteratorUtils.toArray(row0.cellIterator());

					if (headerCellsObj.length != colNames.length) {
						throw new BusinessException("Invalid number of columns in the excel file.");
					}

					for (int i = 0; i < headerCellsObj.length; i++) {
						if (!colNames[i].equalsIgnoreCase(((Cell) headerCellsObj[i]).getStringCellValue())) {
							throw new BusinessException("Invalid column " + i + " found [" + ((Cell) headerCellsObj[i]).getStringCellValue() + "] but was expecting ["
									+ colNames[i] + "]");
						}
					}
					result.setNbItemsToProcess(rowsObj.length - 1);
					for (int rowIndex = 1; rowIndex < rowsObj.length; rowIndex++) {
						Row row = (Row) rowsObj[rowIndex];
						try {
							pricePlanService.importExcelLine(row, currentUser, provider);
							result.registerSucces();
						} catch (BusinessException ex) {
							result.registerError(ex.getMessage() + ";");
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
					File fi = FileUtils.replaceFileExtension(file, "");
					FileUtils.moveFile(rejectDir, fi, null);
					try {
						if (excelInputStream != null) {
							excelInputStream.close();
						}
					} catch (Exception ex) {
						log.error(ex.getMessage());
					} finally {
						fi.delete();
					}
					throw new BusinessException("Error while parsing the excel file." + e.getMessage());
				}
				report += result.getErrorsAString();

				if (FileUtils.getFileForParsing(inputDir, fileExtensions) != null) {
					result.setDone(false);
				}

				if (processed > 0) {
					File fi = FileUtils.replaceFileExtension(file, ".processed");
					FileUtils.moveFile(outputDir, fi, null);
					try {
						if (excelInputStream != null) {
							excelInputStream.close();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				} else {
					File fi = FileUtils.replaceFileExtension(file, "");
					FileUtils.moveFile(rejectDir, fi, null);
					try {
						if (excelInputStream != null) {
							excelInputStream.close();
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
