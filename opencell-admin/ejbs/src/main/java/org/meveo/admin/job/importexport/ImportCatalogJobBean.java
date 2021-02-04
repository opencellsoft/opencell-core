/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 */
@Stateless
public class ImportCatalogJobBean {

    @Inject
    private PricePlanMatrixService pricePlanService;

    @Inject
    private Logger log;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobExecutionService jobExecutionService;

    private String fileName;
    private File file;
    private String inputDir;
    private String outputDir;
    private String rejectDir;
    private String report;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    private String[] colNames = { "Priceplan code", "Priceplan description", "Charge code", "Seller", "Country", "Currency", "Start appli.", "End appli.", "Offer code", "Priority",
            "Amount w/out tax", "Amount with tax", "Min quantity", "Max quantity", "Criteria 1", "Criteria 2", "Criteria 3", "Criteria EL", "Start rating", "End rating",
            "Min subscr age", "Max subscr age", "Validity calendar" };

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    public void execute(JobExecutionResultImpl result, String parameter) {

        InputStream excelInputStream = null;
        try {

            ParamBean parambean = paramBeanFactory.getInstance();
            String catalogDir = paramBeanFactory.getChrootDir() + File.separator + "imports" + File.separator + "catalog" + File.separator;

            inputDir = catalogDir + "input";
            String fileExtension = parambean.getProperty("catalogImport.extensions", "xls");
            ArrayList<String> fileExtensions = new ArrayList<String>();
            fileExtensions.add(fileExtension);
            outputDir = catalogDir + "output";
            rejectDir = catalogDir + "reject";

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
            file = FileUtils.getFirstFile(inputDir, fileExtensions);
            if (file != null) {
                fileName = file.getName();
                report = "parse " + fileName + ";";
                file = FileUtils.addExtension(file, ".processing");
                excelInputStream = new FileInputStream(file);

                int processed = 0;

                try (Workbook workbook = WorkbookFactory.create(excelInputStream)) {
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
                            throw new BusinessException(
                                "Invalid column " + i + " found [" + ((Cell) headerCellsObj[i]).getStringCellValue() + "] but was expecting [" + colNames[i] + "]");
                        }
                    }
                    result.setNbItemsToProcess(rowsObj.length - 1);
                    for (int rowIndex = 1; rowIndex < rowsObj.length; rowIndex++) {         
                        if (rowIndex % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                            break;
                        }
                        Row row = (Row) rowsObj[rowIndex];
                        try {
                            pricePlanService.importExcelLine(row);
                            jobExecutionService.registerSucces(result);
                        } catch (BusinessException ex) {
                            jobExecutionService.registerError(result, ex.getMessage() + ";");
                        }
                    }

                } catch (Exception e) {
                    log.error("failed to create excel file", e);
                    File fi = FileUtils.replaceFileExtension(file, "");
                    FileUtils.moveFile(rejectDir, fi, null);
                    try {
                        if (excelInputStream != null) {
                            excelInputStream.close();
                        }
                    } catch (Exception ex) {
                        log.error("excel error ", ex);
                    } finally {
                        fi.delete();
                    }
                    throw new BusinessException("Error while parsing the excel file." + e.getMessage());
                }
                report += result.getErrorsAString();

                if (FileUtils.getFirstFile(inputDir, fileExtensions) != null) {
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
                        log.error("excel exception ", e);
                    }
                } else {
                    File fi = FileUtils.replaceFileExtension(file, "");
                    FileUtils.moveFile(rejectDir, fi, null);
                    try {
                        if (excelInputStream != null) {
                            excelInputStream.close();
                        }
                    } catch (Exception e) {
                        log.error("excel error ", e);
                    } finally {
                        fi.delete();
                    }
                }

                result.setReport(report);
            } else {
                log.info("No file to process.");
            }
        } catch (Exception e) {
            log.error("Failed to import catalog job", e);
        }
    }

}
