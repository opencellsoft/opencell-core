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

package org.meveo.service.finance;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.ScrollableResults;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ReportExtractExecutionException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractExecutionOrigin;
import org.meveo.model.finance.ReportExtractExecutionResult;
import org.meveo.model.finance.ReportExtractResultTypeEnum;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.finance.ReportExtractScript;

/**
 * Service for managing ReportExtract entity.
 * 
 * @author Edward P. Legaspi
 * @author Mohammed Amine TAZI
 * @author Abdellatif BARI
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.3
 **/
@Stateless
public class ReportExtractService extends BusinessService<ReportExtract> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ReportExtractExecutionResultService reportExtractExecutionResultService;

    public void runReport(ReportExtract entity) throws ReportExtractExecutionException, BusinessException {
        runReport(entity, null, ReportExtractExecutionOrigin.GUI);
    }

    public void runReport(ReportExtract entity, Map<String, String> mapParams) throws ReportExtractExecutionException, BusinessException {
        runReport(entity, mapParams, ReportExtractExecutionOrigin.GUI);
    }

    @SuppressWarnings("rawtypes")
    public ReportExtractExecutionResult runReport(ReportExtract entity, Map<String, String> mapParams, ReportExtractExecutionOrigin origin) throws ReportExtractExecutionException, BusinessException {
        Map<String, Object> context = new HashMap<>();
        int reportSize = 0;
        // use params parameter if set, otherwise use the set from entity
        Map<String, String> params = new HashMap<>();
        if (mapParams != null && !mapParams.isEmpty()) {
            params = mapParams;
        } else if (entity.getParams() != null) {
            params = entity.getParams();
        }

        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            // if type is sql check if parameter exists
            if (entity.getScriptType().equals(ReportExtractScriptTypeEnum.SQL) && !entity.getSqlQuery().contains(":" + pair.getKey().toString())) {
                continue;
            }

            if (!StringUtils.isBlank(pair.getValue())) {
                if (pair.getKey().toString().endsWith("_DATE")) {
                    context.put(pair.getKey().toString(), DateUtils.parseDateWithPattern(pair.getValue().toString(), ParamBean.getInstance().getDateFormat()));
                } else {
                    context.put(pair.getKey().toString(), pair.getValue());
                }
            }
        }

        StringBuilder reportDir = new StringBuilder(ParamBean.getInstance().getChrootDir(appProvider.getCode()));
        if(!StringUtils.isBlank(entity.getOutputDir())) {
            reportDir.append(File.separator + entity.getOutputDir());
        } else {
            reportDir.append(File.separator + ReportExtractScript.REPORTS_DIR);
        }

        String filename = DateUtils.evaluteDateFormat(entity.getFilenameFormat());
        filename = evaluateStringExpression(filename, entity);

        ReportExtractExecutionResult reportExtractExecutionResult = new ReportExtractExecutionResult();
        reportExtractExecutionResult.updateAudit(currentUser);
        reportExtractExecutionResult.setReportExtract(entity);
        reportExtractExecutionResult.setStartDate(new Date());
        reportExtractExecutionResult.setFilePath(filename);
        reportExtractExecutionResult.setLineCount(0);
        reportExtractExecutionResult.setOrigin(origin);
        reportExtractExecutionResult.setStatus(true);

        ReportExtractExecutionException be = null;
        if (entity.getScriptType().equals(ReportExtractScriptTypeEnum.SQL)) {
            ScrollableResults results = null;
            try {
                results = getScrollableResultNativeQuery(entity.getSqlQuery(), context);
            } catch (Exception e) {
                if (e.getCause() != null && e.getCause().getCause() != null) {
                    reportExtractExecutionResult.setErrorMessage(e.getCause().getCause().getMessage());
                } else {
                    reportExtractExecutionResult.setErrorMessage(e.getMessage());
                }
                log.error("Invalid SQL query: {}", e.getMessage());
                be = new ReportExtractExecutionException("Invalid SQL query.");
            }
            if (results != null && results.next()) {
                log.debug("{} record/s found", reportSize);
                if (entity.getReportExtractResultType().equals(ReportExtractResultTypeEnum.CSV)) {
                    reportSize = writeAsFile(filename, entity.getFileSeparator(), reportDir, results);
                } else {
                    reportSize = writeAsHtml(filename, reportDir, results, entity);
                }
                reportExtractExecutionResult.setLineCount(reportSize);
                results.close();
            } else {
                String header = "";
                switch (entity.getCode()) {
                case "REPORT_ACCOUNT_OPERATIONS" : header = "Account Operation Id|Customer Account Id|Customer Account Code|Transaction Type|Type Code|Description|Transaction Direction|Transaction Date|Due Date|Amount Without Tax|Tax Amount|Amount with Tax|Matching Amount|Unmatched Amount|Matching Status|Billing Account Number|Payment File AC Number|From Billing Account|To Billing Account|Payment File Name|Reason|User|Accounting Code|Payment Reference|Invoice Number|Invoice Date|Order Number"; break;
                case "REPORT_ACCOUNTING_CODE" : header = "Accounting Code Id|Parent Code|Description|Account Type|Reporting Account|Disabled"; break;
                case "REPORT_ASSOCIATIONS" : header = "Subscription Id|Association Id|Name|Circuit Id|Association Type|Association End|Status|State|Order|Address Line 1|Address Line 2|Address Line 3|Address Line 4|Address Line 5|County|Postal District|Country|Eircode"; break;
                case "REPORT_BILLED_NONUSAGE" : header = "Subscription Id|Transaction Id|Charge Code|Charge Description|Offer|Offer Type|Price Plan|Invoice|Billing Account|Billing Run|Wallet Operation|Re-Rated WO Id|Amount Without Tax|Tax Class|Tax Rate|Tax Amount|Amount with Tax|Raw Amount with Tax|Raw Amount without Tax|Input Quantity|Quantity|Invoice Subcategory|Accounting Code|Date|Start Date|End Date|Status|Billed Date"; break;
                case "REPORT_BILLED_USAGE" : header = "Subscription Id|Transaction Id|Charge Code|Charge Description|Price Plan|Invoice|Billing Account|Billing Run|CDR|EDR|Wallet Operation|Amount Without Tax|Tax Class|Tax Rate|Tax Amount|Amount with Tax|Raw Amount with Tax|Raw Amount without Tax|Call Duration|Invoice Subcategory|Accounting Code|Usage Date|Start Date|End Date|Connection Charge|Min Call Charge|Time Period|Traffic Class|Status|Billed Date"; break;
                case "REPORT_BILLING_ACCOUNT" : header = "Billing Account Id|Billing Account Code|Billing Account Number|Billing Account Name|Customer Account Id|Customer Account Code|Title|First Name|Last Name|Status|Status Date|Language|Service Type|Last Bill Date|Next Bill Date|Bill Cycle|Bill Delivery Type|HUB Directory|Electronic Billing|Phone|Email Address|Address Line 1|Address Line 2|Address Line 3|Postal Code|City|Country|Tax Category|Operator Id|VAT Authentication Number|VAT Authentication Expiry Date|Registration Number"; break;
                case "REPORT_BILLING_ACCOUNT_BALANCE" : header = "Billing Account Id|Billing Account Code|Billing Account Name|Billing Account Number|Date|Outstanding Balance|Last Bill Date|Next Bill Date|Customer Account Id|Customer Id"; break;
                case "REPORT_CUSTOMER" : header = "Customer Id|Customer Code|Company|Title|First Name|Last Name|Customer Category|Seller|RAP Customer"; break;
                case "REPORT_CUSTOMER_ACCOUNT" : header = "Customer Account Id|Customer Account Code|Customer Account Name|Customer Id|Status|Title|First Name|Last Name|Currency|Address Line 1|Address Line 2|Address Line 3|Postal Code|City|Country"; break;
                case "REPORT_ENDPOINTS" : header = "Subscription Id|End Point Id|End|A End Point Name|Order|Status|End Point Type|ARD Id|End Point State|End Address Line 1|End Address Line 2|End Address Line 3|End Address Line 4|End Address Line 5|End County|End Postal District|End Country|End Eircode"; break;
                case "REPORT_SUBSCRIPTION" : header = "Subscription Id|User Account Id|Subscription Code|Offer|Circuit Type|Billing Account Id|Billing Account Code|Billing Account Number|Status|Status Date|End of Engagement Date|Subscription Start Date|Subscription End Date|UAN|End Point A Id|End Point B Id|Association A Id|Association B Id|Association C Id"; break;
                case "REPORT_SUBSCRIPTION_SERVICES" : header = "Service Instance Id|Subscription Id|Subscription Code|Service|Service Name|Service Type|User Account|User Account Id|Quantity|Operator Order Number|Order Id|Activation Date|Reccuring Charge|Charge Interval|Reccuring Charge ex VAT|Specially Initiation Price|Special Rental Price|Next Charge Date|Charged To Date|Action Qualifier|A End ARD Id|AF|Agent Code|Analogue Quantity|Analogue Rate|Bandwidth|B End ARD Id|Charge Type|Class of Service|Commitment Period|Commitment Treatment|Component Id|CPE Description|CPE Type|Density|Distance|EF|Egress Group|ETS Type|Exchange Code|IND 01|IND 02|IND 03|IND 04|IND 05|IND 06|IND 07|IND 08|IND 09|IND 10|Item Component Id|Main Service Bill Code|Not Before Date OMS|Operator Order No|Order Type|Price Plan|Price Plan ind|Price Plan Text|Processing Status|Quantity*|Range Start|Range End|Rating Scheme|Ref Service Id|Region|Rental Liability Date|Service Category|Service Type*|SGN STD Code|Site General Number|SLA|State|Status|Sub Order|Transmission|Unique id of the Component in OMS|XREF|Zone A|Zone B"; break;
                case "REPORT_UNBILLED_NONUSAGE" : header = "Subscription Id|Transaction Id|Charge Code|Charge Description|Offer|Offer Type|Price Plan|Invoice|Billing Account|Billing Run|Wallet Operation|Re-Rated WO Id|Amount Without Tax|Tax Class|Tax Rate|Tax Amount|Amount with Tax|Raw Amount with Tax|Raw Amount without Tax|Input Quantity|Quantity|Invoice Subcategory|Accounting Code|Date|Start Date|End Date|Status"; break;
                case "REPORT_UNBILLED_USAGE" : header = "Subscription Id|Transaction Id|Charge Code|Charge Description|Price Plan|Invoice|Billing Account|Billing Run|CDR|EDR|Wallet Operation|Amount Without Tax|Tax Class|Tax Rate|Tax Amount|Amount with Tax|Raw Amount with Tax|Raw Amount without Tax|Call Duration|Invoice Subcategory|Accounting Code|Usage Date|Start Date|End Date|Connection Charge|Min Call Charge|Time Period|Traffic Class|Status"; break;
                case "REPORT_VAT_DETAIL" : header = "Bill Run Id|Bill Cycle Id|Bill Cycle Name|Billing Account Number|Invoice Number|Tax Class|Bill Date|Tax Category|Tax Rate|Tax Amount|Billed Amount Net (ex VAT)|Billed Amount Gross (incl VAT)|Service Type|Currency"; break;
                case "REPORT_VAT_SUMMARY" : header = "Tax Class|Bill Date|Tax Category|Tax Rate|Tax Amount|Billed Amount Net (ex VAT)|Billed Amount Gross (incl VAT)|Service Type|Currency"; break;
                case "REPORT1_AGED_DEBT_ANALYSIS" : header = "Customer code|Customer account code|Billing account number|Billing Account Name|Service Type|Tax Category|Active Subscriptions|Not Overdue (Amount)|1-30 days|31-60 days|61-90 days|91-120 days|121-150 days|151-180 days|181-210 days|211-240 days|241-270 days|271-300 days|301-330 days|331-365 days|1-2 year|> 2 years|due_total"; break;
                case "REPORT2_RWO_AGED_DEBT_ANALYSIS" : header = "Customer code|Customer account code|Billing account number|Billing Account Name|Service Type|Tax Category|RWO Number|Transaction number|Transaction date|Transaction type|Not Overdue (Amount)|1-30 days|31-60 days|61-90 days|91-120 days|121-150 days|151-180 days|181-210 days|211-240 days|241-270 days|271-300 days|301-330 days|331-365 days|1-2 year|> 2 years|due_total"; break;
                case "REPORT3_RWO_PAYMENTS_DAILY" : header = "Tax category|Billing account number|Billing account code|Billing account name|Amount|Currency|Account operation code|Transaction date"; break;
                case "REPORT4_RWO_PAYMENTS_MONTHLY" : header = "Tax category|Billing account number|Billing account code|Billing account name|Amount|Currency|Account operation code|Transaction date"; break;
                case "REPORT5_RWO_BILLING" : header = "Tax category|Billing account number|Billing account code|RWO number|Billing account name|Address line 1|Address line 2|Address line 3|Postal code|City|Bill date|Invoice number|Bill text|From date|To date|Amount ex. tax|Tax"; break;
                case "REPORT6_UNBILLED_CHARGES" : header = "Service type|Bill cycle name|Billing account number|Billing account name|Next invoice date|Status|Amount (ex VAT)"; break;             
                }
                if(StringUtils.isNotBlank(header)) {
                    File dir = new File(reportDir.toString());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(reportDir.toString() + File.separator + filename);
                    try (FileWriter fileWriter = new FileWriter(file);) {
                        fileWriter.write(header);
                        fileWriter.flush();
                    } catch (Exception e) {
                        throw new BusinessException("Cannot write report to file "+ file.getAbsolutePath(), e);            
                    } 
                }
            }

        } else {
            context.put(ReportExtractScript.DIR, reportDir.toString());
            if (!StringUtils.isBlank(entity.getFilenameFormat())) {
                context.put(ReportExtractScript.FILENAME, filename);
            }

            Map<String, Object> resultContext = scriptInstanceService.execute(entity.getScriptInstance().getCode(), context);
            reportExtractExecutionResult.setErrorMessage((String) resultContext.getOrDefault(ReportExtractScript.ERROR_MESSAGE, ""));
            reportExtractExecutionResult.setLineCount((int) resultContext.getOrDefault(ReportExtractScript.LINE_COUNT, 0));
        }

        reportExtractExecutionResult.setEndDate(new Date());
        reportExtractExecutionResultService.createInNewTransaction(reportExtractExecutionResult);

        if (be != null) {
            throw be;
        }
        
        return reportExtractExecutionResult;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private int writeAsHtml(String filename, StringBuilder sbDir, ScrollableResults results, ReportExtract entity) throws BusinessException {
        FileWriter fileWriter = null;
        Map<String, Object> row = null;
        int rowNumber = 0;
        StringBuilder tableHeader = new StringBuilder();
        StringBuilder tableBody = new StringBuilder();
        StringBuilder table = new StringBuilder("<table>");

        if (FilenameUtils.getExtension(filename.toLowerCase()).equals("csv")) {
            filename = FileUtils.changeExtension(filename, ".html");
        }

        try {
            // load the html template
            String template = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("reportExtract/default_html_report.html"));
            Object value = null;
            // get the header
            Map<String, Object> firstRow = (Map<String, Object>) results.get()[0];
            Iterator ite = firstRow.keySet().iterator();
            tableHeader.append("<thead><tr>");
            while (ite.hasNext()) {
                tableHeader.append("<th>" + ite.next() + "</th>");
            }
            tableHeader.append("</tr></thead>");
            table.append(tableHeader);

            int ctr = 1;
            tableBody.append("<tbody>");
            do {
                row = (Map<String, Object>) results.get()[0];
                ite = firstRow.keySet().iterator();
                tableBody.append("<tr class='" + (ctr++ % 2 == 0 ? "odd" : "even") + "'>");
                while (ite.hasNext()) {
                    value = row.get(ite.next());
                    value = value == null ? "" : value;
                    tableBody.append("<td>" + value + "</td>");
                }
                tableBody.append("</tr>");
                rowNumber++;
            } while(results.next());
            
            tableBody.append("</tbody>");
            table.append(tableBody);

            table.append("</table>");

            if (!StringUtils.isBlank(entity.getImagePath())) {
                String imagePath = ModuleUtil.getPicturePath(currentUser.getProviderCode(), "reportExtract") + File.separator + entity.getImagePath();
                String strImage = FileUtils.encodeFileToBase64Binary(new File(imagePath));
                String imageExt = FilenameUtils.getExtension(imagePath);

                imagePath = "data:image/" + imageExt + ";charset=utf-8;base64, " + strImage;
                template = template.replace("#{REPORT_IMG_SRC}", imagePath);
            } else {
                template = template.replace("<div><img src=\"#{REPORT_IMG_SRC}\" /></div>", "");
            }

            template = template.replace("#{REPORT_TITLE}", entity.getCategory() != null ? entity.getCategory() : entity.getCode());
            template = template.replace("#{REPORT_STYLE}", getEntityStyleOrInit(entity));
            template = template.replace("#{REPORT_TABLE}", table);
            template = template.replace("#{REPORT_DESCRIPTION}", entity.getDescriptionOrCode());

            // create the output file, must be html
            File dir = new File(sbDir.toString());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(sbDir.toString() + File.separator + filename);
            file.createNewFile();
            fileWriter = new FileWriter(file);
            fileWriter.write(template);
            return rowNumber;
        } catch (Exception e) {
            log.error("Cannot write report to file: {}", e);
            throw new BusinessException("Cannot write report to file.");
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }

	private String getEntityStyleOrInit(ReportExtract entity) {
		if (StringUtils.isBlank(entity.getStyle())) {
			entity.setStyle(
					"body {font-family: monospace;}\ntable {border-collapse: collapse;}\ntd,th {border: 1px solid black; padding: 3px 10px; text-align: center;}\nth {font-weight: bold; background-color: #aaa}\ntr:nth-child(odd) {background-color: #fff}\ntr:nth-child(even) {background-color: #eee}\ntr:hover {background-color: #fdd;}\ntd:hover {background-color: #fcc;}\n");
		}
		return entity.getStyle();
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private int writeAsFile(String filename, String fileSeparator, StringBuilder sbDir, ScrollableResults results) throws BusinessException {
 
        StringBuilder line = new StringBuilder("");
        Object value = null;
        int rowNumber = 0;
        Map<String, Object> row = null;
        
        File dir = new File(sbDir.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(sbDir.toString() + File.separator + filename);

        try (FileWriter fileWriter = new FileWriter(file);) {

            // Write the header - field names
            Map<String, Object> firstRow = (Map<String, Object>) results.get()[0];
            Iterator ite = firstRow.keySet().iterator();
            while (ite.hasNext()) {
                line.append(ite.next() + fileSeparator);
            }
            line.deleteCharAt(line.length() - 1);
            fileWriter.write(line.toString());
            fileWriter.write(System.lineSeparator());

            // Write the data
            do {
                line = new StringBuilder("");
                row = (Map<String, Object>) results.get()[0];
                ite = firstRow.keySet().iterator();
                while (ite.hasNext()) {
                    value = row.get(ite.next());
                    value = value == null ? "" : value;
                    line.append(value + fileSeparator);
                }
                line.deleteCharAt(line.length() - 1);
                fileWriter.write(line.toString());
                fileWriter.write(System.lineSeparator());
                rowNumber++;

                if (rowNumber % 5000 == 0) {
                    fileWriter.flush();
                }
            } while (results.next());

            fileWriter.flush();
            
            return rowNumber;
        
        } catch (Exception e) {
            throw new BusinessException("Cannot write report to file "+file.getAbsolutePath(), e);            
        } 
    }

    @SuppressWarnings("unchecked")
    public List<Long> listIds() {
        return (List<Long>) getEntityManager().createNamedQuery("ReportExtract.listIds").getResultList();
    }

    private String evaluateStringExpression(String expression, ReportExtract re) throws BusinessException {
        
        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = new HashMap<>();
        userMap.put("re", re);

        return ValueExpressionWrapper.evaluateToStringMultiVariable(expression, "re", re);
        
    }

    public String getReporFilePath(ReportExtractExecutionResult reportResult) throws BusinessException {

        StringBuilder reportFile = new StringBuilder(ParamBean.getInstance().getChrootDir(appProvider.getCode()));
        reportFile.append(reportResult.getReportExtract().getOutputDir()).append(File.separator).append(reportResult.getFilePath());

        return reportFile.toString();
    }
}