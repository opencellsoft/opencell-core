/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.report;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.ResourceBundle;
import org.jboss.seam.log.Log;
import org.meveo.model.bi.JobNameEnum;
import org.meveo.model.bi.Report;
import org.meveo.service.bi.local.ReportServiceLocal;

/**
 * Class to generate PDF reports.
 * 
 * @author Gediminas Ubartas
 * @created 2010.10.07
 */
@Name("reportExecution")
public class ReportExecution {

    private static String REPORTS_URL = ResourceBundle.instance().getString("reportsURL");

    private static String DATE_PATERN = "yyyy.MM.dd";

    @Logger
    protected Log log;

    @In
    private ReportServiceLocal reportService;

    // @In(create=true)
    // private RecurringChargeCronLocal recurringChargeCron;

    public JasperReport jasperReport;

    public JasperPrint jasperPrint;

    public JasperDesign jasperDesign;

    public Map<String, Object> parameters = new HashMap<String, Object>();

    /**
     * Generates PDF report using String data source.
     * 
     * @param filename
     *            Report template filename.
     * @param reportName
     *            Report name.
     * @param DSstring
     *            XML DS string.
     * @param recordPath
     *            Current XML DS row path.
     */
    public void generatePDF(String filename, String reportName, String DSstring, String recordPath, Date executionDate) {
        InputStream reportTemplate = this.getClass().getClassLoader().getResourceAsStream(filename);
        InputStream xmlDS;
        try {
            xmlDS = new ByteArrayInputStream(DSstring.getBytes("UTF-8"));
            // parameters.put("param_03", "value of your param");

            JRXmlDataSource dataSource = new JRXmlDataSource(xmlDS, recordPath);
            dataSource.setDatePattern(DATE_PATERN);
            jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, generateFileName(reportName, executionDate));
            log.info("Created file: " + generateFileName(reportName, executionDate));
        } catch (JRException e) {
            log.error(e);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
    }

    /**
     * Generates PDF report using XML file Data source.
     * 
     * @param filename
     *            Report template filename.
     * @param reportName
     *            Report name
     * @param DSstring
     *            XML DS string.
     * @param recordPath
     *            Current XML DS row path.
     */
    public void generatePDF(String filename, String reportName, InputStream xmlDS, String recordPath,
            Date executionDate, String exportFileName) {
        InputStream reportTemplate = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            JRXmlDataSource dataSource = new JRXmlDataSource(xmlDS, recordPath);
            dataSource.setDatePattern(DATE_PATERN);
            jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, generateFileName(exportFileName, executionDate));
            log.info("Created file: " + generateFileName(exportFileName, executionDate));
        } catch (JRException e) {
            log.error(e);
        }
    }

    /**
     * Generates filename for report.
     * 
     * @param report
     *            Report name.
     * @return Report filename.
     */
    public String generateFileName(String name, Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder(name);
        sb.delete(name.length() - 4, name.length());
        return REPORTS_URL + sb.toString() + df.format(date) + ".pdf";
    }

    /**
     * Execute Report when DS is string.
     * 
     * @param report
     *            Report to execute.
     * @param params
     *            Parameters to report.
     */
    @SuppressWarnings("unchecked")
    public void executeReport(Report report) {
        log.info("executeReport({0})", report.getName());
        try {
            Class clazz = Class.forName(report.getProducerClassName());
            Object obj = clazz.newInstance();
            Component.getInstance(clazz);
            if (obj instanceof ReportDBSourceProducer) {
                log.info("executeReport report class is ReportDBSourceProducer");
                ReportDBSourceProducer reportSourceProducer = (ReportDBSourceProducer) obj;
                String xmlString = reportSourceProducer.generateXmlString(reportService.getRows(reportSourceProducer
                        .getQuery()));
                generatePDF(report.getFileName(), report.getName(), xmlString, report.getRecordPath(), report
                        .getSchedule());
                report.computeNextExecutionDate();
                reportService.update(report);
            }
            if (obj instanceof ReportXMLFileSourceProducer) {
                log.info("executeReport report class is ReportXMLFileSourceProducer");
                ReportXMLFileSourceProducer reportXMLFileSourceProducer = (ReportXMLFileSourceProducer) obj;
                reportXMLFileSourceProducer.export(report);
            }
            if (obj instanceof Reporting) {
                log.info("executeReport report class is Reporting");
                Reporting reporting = (Reporting) obj;
                reporting.export(report);
                log.info("computeNextExecutionDate");
                report.computeNextExecutionDate();
                reportService.update(report);
            }
        } catch (ClassNotFoundException e) {
            log.error(e);
        } catch (InstantiationException e) {
            log.error(e);

        } catch (IllegalAccessException e) {
            log.error(e);

        }

    }

    /**
     * Execute Report when DS is XML file.
     * 
     * @param report
     *            Report to execute.
     * @param params
     *            Parameters to report.
     * @param dataSource
     *            Data source file name.
     */
    public void executeReport(Report report, Map<String, Object> params, String dataSource, String exportFileName) {
        InputStream xmlDS;
        try {
            xmlDS = new FileInputStream(dataSource);
            setParameters(params);
            generatePDF(report.getFileName(), report.getName(), xmlDS, report.getRecordPath(), report.getSchedule(),
                    exportFileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Adds default parameters for report.
     */
    public void addParameters(String parameter, String parameterValue) {
        parameters.put(parameter, parameterValue);
    }

    /**
     * Execute all reports from DB.
     */
    @SuppressWarnings("unchecked")
    public void reportsExecution() {
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

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
