package org.meveo.service.finance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ReportExtractExecutionException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractExecutionOrigin;
import org.meveo.model.finance.ReportExtractExecutionResult;
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
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
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
    public void runReport(ReportExtract entity, Map<String, String> mapParams, ReportExtractExecutionOrigin origin) throws ReportExtractExecutionException, BusinessException {
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
        reportDir.append(File.separator + ReportExtractScript.REPORTS_DIR);

        if (!StringUtils.isBlank(entity.getCategory())) {
            reportDir.append(File.separator + entity.getCategory());
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
            List<Map<String, Object>> resultList = null;

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

            FileWriter fileWriter = null;
            StringBuilder line = new StringBuilder("");
            if (resultList != null && !resultList.isEmpty()) {
                log.debug("{} record/s found", resultList.size());

                reportExtractExecutionResult.setLineCount(resultList.size());

                try {
                    File dir = new File(reportDir.toString());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(reportDir.toString() + File.separator + filename);
                    file.createNewFile();
                    fileWriter = new FileWriter(file);

                    // get the header
                    Map<String, Object> firstRow = resultList.get(0);
                    Iterator ite = firstRow.keySet().iterator();
                    while (ite.hasNext()) {
                        line.append(ite.next() + ",");
                    }
                    line.deleteCharAt(line.length() - 1);
                    fileWriter.write(line.toString());
                    fileWriter.write(System.lineSeparator());

                    line = new StringBuilder("");
                    for (Map<String, Object> row : resultList) {
                        ite = firstRow.keySet().iterator();
                        while (ite.hasNext()) {
                            line.append(row.get(ite.next()) + ",");
                        }
                        line.deleteCharAt(line.length() - 1);
                        fileWriter.write(line.toString());
                        fileWriter.write(System.lineSeparator());
                        line = new StringBuilder("");
                    }
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (IOException e) {
                        }
                    }
                } catch (Exception e) {
                    reportExtractExecutionResult.setErrorMessage(e.getMessage());
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (IOException e1) {
                        }
                    }
                    log.error("Cannot write report to file: {}", e.getMessage());
                    be = new ReportExtractExecutionException("Cannot write report to file.");
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
    }

    @SuppressWarnings("unchecked")
    public List<Long> listIds() {
        return (List<Long>) getEntityManager().createNamedQuery("ReportExtract.listIds").getResultList();
    }

    private String evaluateStringExpression(String expression, ReportExtract re) throws BusinessException {
        if (!expression.startsWith("#{")) {
            return expression;
        }

        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = new HashMap<>();
        userMap.put("re", re);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        return result;
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

}
