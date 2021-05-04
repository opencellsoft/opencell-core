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

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ReportExtractExecutionException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractExecutionOrigin;
import org.meveo.model.finance.ReportExtractExecutionResult;
import org.meveo.model.finance.ReportExtractResultTypeEnum;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.finance.ReportExtractScript;

import static java.lang.String.format;
import static java.util.Optional.*;

/**
 * Service for managing ReportExtract entity.
 * 
 * @author Edward P. Legaspi
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

    @Inject
    private CustomTableService customTableService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void runReport(ReportExtract entity) throws ReportExtractExecutionException, BusinessException {
        runReport(entity, null, ReportExtractExecutionOrigin.GUI);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void runReport(ReportExtract entity, Map<String, String> mapParams) throws ReportExtractExecutionException, BusinessException {
        runReport(entity, mapParams, ReportExtractExecutionOrigin.GUI);
    }

    @SuppressWarnings("rawtypes")
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ReportExtractExecutionResult runReport(ReportExtract entity, Map<String, String> mapParams, ReportExtractExecutionOrigin origin) throws ReportExtractExecutionException, BusinessException {
        Map<String, Object> context = new HashMap<>();

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
        List<Map<String, Object>> resultList = null;
        if (entity.getScriptType().equals(ReportExtractScriptTypeEnum.SQL)) {

            try {
                resultList = scriptInstanceService.executeNativeSelectQuery(entity.getSqlQuery(), context);
            } catch (Exception e) {
                if (e.getCause() != null && e.getCause().getCause() != null) {
                    reportExtractExecutionResult.setErrorMessage(e.getCause().getCause().getMessage());
                } else {
                    reportExtractExecutionResult.setErrorMessage(e.getMessage());
                }
                log.error("Invalid SQL query: {}", e.getMessage());
                be = new ReportExtractExecutionException("Invalid SQL query.");
            }

            if (resultList != null && !resultList.isEmpty()) {
                log.debug("{} record/s found", resultList.size());
                if (entity.getReportExtractResultType().equals(ReportExtractResultTypeEnum.CSV)) {
                    writeAsFile(filename, reportDir, resultList, entity.getSeparator(), entity.getMaximumLine(), entity.getDecimalSeparator());
                } else {
                    writeAsHtml(filename, reportDir, resultList, entity);
                }
                reportExtractExecutionResult.setLineCount(resultList.size());
            }

            if((resultList == null || resultList.isEmpty()) && entity.isGenerateEmptyReport()) {
                generateEmptyReport(filename, reportDir, entity.getReportExtractResultType());
                reportExtractExecutionResult.setFilePath(filename);
                reportExtractExecutionResult.setLineCount(0);
            }

        } else {
            context.put(ReportExtractScript.DIR, reportDir.toString());
            if (!StringUtils.isBlank(entity.getFilenameFormat())) {
                context.put(ReportExtractScript.FILENAME, filename);
            }

            Map<String, Object> resultContext = scriptInstanceService.execute(entity.getScriptInstance().getCode(), context);
            resultList = readGeneratedFile(resultContext.get("DIR") +"\\"+ resultContext.get("FILENAME"), entity.getSeparator());
            reportExtractExecutionResult.setErrorMessage((String) resultContext.getOrDefault(ReportExtractScript.ERROR_MESSAGE, ""));
            reportExtractExecutionResult.setLineCount((int) resultContext.getOrDefault(ReportExtractScript.LINE_COUNT, 0));
        }

        if(entity.getCustomTableCode() != null && resultList != null && !resultList.isEmpty()) {
            storeDataInCT(entity.getCustomTableCode(), resultList, entity.isAccumulate());
        }

        reportExtractExecutionResult.setEndDate(new Date());
        reportExtractExecutionResultService.createInNewTransaction(reportExtractExecutionResult);

        if (be != null) {
            throw be;
        }
        
        return reportExtractExecutionResult;
    }

    @SuppressWarnings("rawtypes")
    private void writeAsHtml(String filename, StringBuilder sbDir, List<Map<String, Object>> resultList, ReportExtract entity) throws BusinessException {
        FileWriter fileWriter = null;
        StringBuilder tableHeader = new StringBuilder();
        StringBuilder tableBody = new StringBuilder();
        StringBuilder table = new StringBuilder("<table>");

        if (FilenameUtils.getExtension(filename.toLowerCase()).equals("csv")) {
            filename = FileUtils.changeExtension(filename, ".html");
        }

        try {
            // load the html template
            String template = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("reportExtract/default_html_report.html"));

            // get the header
            Map<String, Object> firstRow = resultList.get(0);
            Iterator ite = firstRow.keySet().iterator();
            tableHeader.append("<thead><tr>");
            while (ite.hasNext()) {
                tableHeader.append("<th>" + ite.next() + "</th>");
            }
            tableHeader.append("</tr></thead>");
            table.append(tableHeader);

            int ctr = 1;
            tableBody.append("<tbody>");
            for (Map<String, Object> row : resultList) {
                ite = firstRow.keySet().iterator();
                tableBody.append("<tr class='" + (ctr++ % 2 == 0 ? "odd" : "even") + "'>");
                while (ite.hasNext()) {
                    tableBody.append("<td>" + row.get(ite.next()) + "</td>");
                }
                tableBody.append("</tr>");
            }
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

    @SuppressWarnings("rawtypes")
    private List<String> writeAsFile(String filename, StringBuilder sbDir, List<Map<String, Object>> resultList,
                             String separator, long maxLinePerFile, String decimalSeparator) throws BusinessException {
        FileWriter fileWriter = null;
        StringBuilder line;

        int fileSufix = 0;
        try {
            File dir = new File(sbDir.toString());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            List<String> fileNames = new ArrayList<>();
            String[] path = filename.split("\\.");
            filename = new StringBuilder(path[0]).append("_").append(format("%04d", fileSufix)).append(".").append(path[1]).toString();
            File file = new File(sbDir + File.separator + filename);
            file.createNewFile();
            fileWriter = new FileWriter(file);
            fileNames.add(filename);

            // get the header
            Map<String, Object> firstRow = resultList.get(0);
            Iterator ite = firstRow.keySet().iterator();
            StringBuilder header = new StringBuilder();
            while (ite.hasNext()) {
                header.append(ite.next() + separator);
            }
            header.deleteCharAt(header.length() - 1);
            fileWriter.write(header.toString());
            fileWriter.write(System.lineSeparator());

            line = new StringBuilder();
            int counter = 0;
            for (Map<String, Object> row : resultList) {
                ite = firstRow.keySet().iterator();
                while (ite.hasNext()) {
                    Object item = row.get(ite.next());
                    if(item instanceof Number) {
                        line.append(formatDecimal((Number) item, decimalSeparator) + separator);
                    } else {
                        line.append(item + separator);
                    }
                }
                counter++;
                line.deleteCharAt(line.length() - 1);
                fileWriter.write(line.toString());
                fileWriter.write(System.lineSeparator());
                line = new StringBuilder();
                if(maxLinePerFile > 0 && counter >= maxLinePerFile) {
                    fileWriter.close();
                    counter = 0;
                    filename = new StringBuilder(path[0]).append("_").append(format("%04d", ++fileSufix)).append(".").append(path[1]).toString();
                    file = new File(sbDir + File.separator + filename);
                    file.createNewFile();
                    fileWriter = new FileWriter(file);
                    fileNames.add(filename);
                    fileWriter.write(header.toString());
                    fileWriter.write(System.lineSeparator());
                }
            }
            return fileNames;
        } catch (Exception e) {
            log.error("Cannot write report to file: {}", e);
            throw new BusinessException("Cannot write report to file.");
        } finally  {
            IOUtils.closeQuietly(fileWriter);
        }
    }

    private String formatDecimal(Number item, String decimalSeparator) {
        DecimalFormatSymbols symbol = new DecimalFormatSymbols();
        symbol.setDecimalSeparator(decimalSeparator.charAt(0));
        DecimalFormat formatter = new DecimalFormat("0.##########", symbol);
        return formatter.format(item);
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

    public String getReporFile(ReportExtract entity) throws BusinessException {
        entity = refreshOrRetrieve(entity);
        StringBuilder reportDir = new StringBuilder(ParamBean.getInstance().getChrootDir(appProvider.getCode()));
        reportDir.append(File.separator + ReportExtractScript.REPORTS_DIR);

        if (!StringUtils.isBlank(entity.getCategory())) {
            reportDir.append(File.separator + entity.getCategory());
        }

        String filename = DateUtils.evaluteDateFormat(entity.getFilenameFormat());
        filename = evaluateStringExpression(filename, entity);

        return reportDir + File.separator + filename;
    }


    private int storeDataInCT(String customTableCode, List<Map<String, Object>> data, boolean append) {
        CustomEntityTemplate customTable = ofNullable(customEntityTemplateService.findByCode(customTableCode))
                .orElseThrow(() -> new BusinessException("No custom table found with the given code : " + customTableCode));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> resultList : data) {
            Map<String, Object> newResultList = new HashMap<>();
            for (String key: resultList.keySet()) {
                newResultList.put(key.replaceAll("\\s+","_").toUpperCase(), resultList.get(key));
            }
            result.add(newResultList);
        }
        return customTableService.importData(customTable, result, append);
    }

    private List<Map<String, Object>> readGeneratedFile(String path, String separator) {
        List<Map<String, Object>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            String[] header =  br.readLine().split(separator);
            while ((line = br.readLine()) != null) {
                String[] values = line.split(separator);
                Map<String, Object> data = new HashMap<>();
                for(int index = 0; index < header.length; index++) {
                    data.put(header[index].replaceAll("\\s+",""), values[index]);
                }
                records.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    private void generateEmptyReport(String filename, StringBuilder sbDir, ReportExtractResultTypeEnum reportType) {
        if(reportType.equals(ReportExtractResultTypeEnum.HTML) && FilenameUtils.getExtension(filename.toLowerCase()).equals("csv")) {
                filename = FileUtils.changeExtension(filename, ".html");
        }
        File dir = new File(sbDir.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(sbDir + File.separator + filename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
