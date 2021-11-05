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