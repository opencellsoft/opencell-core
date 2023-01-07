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
package org.meveo.admin.report;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.bi.JobNameEnum;
import org.meveo.model.bi.Report;
import org.meveo.service.bi.impl.ReportService;
import org.slf4j.Logger;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Class to generate PDF reports.
 * 
 * @author Gediminas Ubartas
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Named
public class ReportExecution implements Serializable {

    private static final long serialVersionUID = -5414660392138922700L;

    private static String DATE_PATERN = "yyyy.MM.dd";

    @Inject
    protected Logger log;

    @Inject
    private ReportService reportService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    // @In(create=true)
    // private RecurringChargeCron recurringChargeCron;

    // public JasperReport jasperReport;
    //
    // public JasperPrint jasperPrint;
    //
    // public JasperDesign jasperDesign;

    public Map<String, Object> parameters = new HashMap<String, Object>();

    /**
     * Generates PDF report using String data source.
     * 
     * @param filename Report template filename.
     * @param reportName Report name.
     * @param dsString XML DS string.
     * @param recordPath Current XML DS row path.
     * @param executionDate execution date.
     */
    public void generatePDF(String filename, String reportName, String dsString, String recordPath, Date executionDate) {
        InputStream reportTemplate = this.getClass().getClassLoader().getResourceAsStream(filename);
        InputStream xmlDS;
        try {
            xmlDS = new ByteArrayInputStream(dsString.getBytes("UTF-8"));
            // parameters.put("param_03", "value of your param");

            JRXmlDataSource dataSource = new JRXmlDataSource(xmlDS, recordPath);
            dataSource.setDatePattern(DATE_PATERN);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, generateFileName(reportName, executionDate));
            log.debug("Created file: " + generateFileName(reportName, executionDate));
        } catch (JRException e) {
            log.error("failed to generatePDF,JR exception", e);
        } catch (UnsupportedEncodingException e) {
            log.error("failed to generatePDF,unsupported encoding exception", e);
        }
    }

    /**
     * Generates PDF report using XML file Data source.
     * 
     * @param filename Report template filename.
     * @param reportName Report name
     * @param xmlDS xml datasource XML DS string.
     * @param recordPath Current XML DS row path.
     * @param executionDate execution date.
     * @param exportFileName export file name.
     * 
     */
    public void generatePDF(String filename, String reportName, InputStream xmlDS, String recordPath, Date executionDate, String exportFileName) {
        InputStream reportTemplate = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            JRXmlDataSource dataSource = new JRXmlDataSource(xmlDS, recordPath);
            dataSource.setDatePattern(DATE_PATERN);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, generateFileName(exportFileName, executionDate));
            log.debug("Created file: " + generateFileName(exportFileName, executionDate));
        } catch (JRException e) {
            log.error("error on generate PDF", e);
        }
    }

    /**
     * Generates filename for report.
     * 
     * @param name file's name.
     * @param date date?
     * @return Report filename.
     */
    public String generateFileName(String name, Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder(name);
        sb.delete(name.length() - 4, name.length());
        String reportsUrl = paramBeanFactory.getInstance().getProperty("reportsURL", "/opt/jboss/files/reports/");
        return reportsUrl + sb.toString() + df.format(date) + ".pdf";
    }

    /**
     * Execute Report when DS is string.
     * 
     * @param report Report to execute.
     */
    @SuppressWarnings("rawtypes")
    public void executeReport(Report report) {
        log.debug("executeReport({})", report.getName());
        try {
            Class clazz = Class.forName(report.getProducerClassName());
            Object obj = clazz.newInstance();
            // TODO: Component.getInstance(clazz);
            if (obj instanceof ReportDBSourceProducer) {
                log.debug("executeReport report class is ReportDBSourceProducer");
                ReportDBSourceProducer reportSourceProducer = (ReportDBSourceProducer) obj;
                String xmlString = reportSourceProducer.generateXmlString(reportService.getRows(reportSourceProducer.getQuery()));
                generatePDF(report.getFileName(), report.getName(), xmlString, report.getRecordPath(), report.getSchedule());
                report.computeNextExecutionDate();
                reportService.update(report);
            }
            if (obj instanceof ReportXMLFileSourceProducer) {
                log.debug("executeReport report class is ReportXMLFileSourceProducer");
                ReportXMLFileSourceProducer reportXMLFileSourceProducer = (ReportXMLFileSourceProducer) obj;
                reportXMLFileSourceProducer.export(report);
            }
            if (obj instanceof Reporting) {
                log.debug("executeReport report class is Reporting");
                Reporting reporting = (Reporting) obj;
                reporting.export(report);
                log.debug("computeNextExecutionDate");
                report.computeNextExecutionDate();
                reportService.update(report);
            }
        } catch (Exception e) {
            log.error("failed to executeReport, {} exception", e.getClass(), e);
        }
    }

    /**
     * Execute Report when DS is XML file.
     * 
     * @param report Report to execute.
     * @param params Parameters to report.
     * @param dataSource Data source file name.
     * 
     * @param exportFileName file name created when exporting
     */
    public void executeReport(Report report, Map<String, Object> params, String dataSource, String exportFileName) {
        InputStream xmlDS;
        try {
            xmlDS = new FileInputStream(dataSource);
            setParameters(params);
            generatePDF(report.getFileName(), report.getName(), xmlDS, report.getRecordPath(), report.getSchedule(), exportFileName);
        } catch (FileNotFoundException e) {
            log.error("failed to execute report , file not found Exception", e);
        }
    }

    /**
     * Adds default parameters for report.
     * 
     * @param parameter name of parameter
     * @param parameterValue value of parameter
     */
    public void addParameters(String parameter, String parameterValue) {
        parameters.put(parameter, parameterValue);
    }

    /**
     * Execute all reports from DB.
     * 
     * @throws BusinessException business excepion.
     */
    public void reportsExecution() throws BusinessException {
        List<Report> reportList = (List<Report>) reportService.list();
        Date date = new Date();
        if (reportList.size() != 0) {
            for (Report report : reportList) {
                if (report.getSchedule().before(date)) {
                    if (report.getActionName() == JobNameEnum.REPORTING) {
                        executeReport(report);
                    } else if (report.getActionName() == JobNameEnum.RECURRING_CHARGES_APPLICATION) {
                        // recurringChargeCron.recurringChargeApplication();
                        report.computeNextExecutionDate();
                        reportService.update(report);
                    }

                }
            }
        }
    }

    /**
     * @return map of parameter
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * @param parameters map of parameter/its value.
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
