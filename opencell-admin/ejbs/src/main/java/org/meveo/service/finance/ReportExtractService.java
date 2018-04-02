package org.meveo.service.finance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;
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

    public void runReport(ReportExtract entity) throws BusinessException {
        runReport(entity, null);
    }

    @SuppressWarnings("rawtypes")
    public void runReport(ReportExtract entity, Map<String, String> mapParams) throws BusinessException {
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

        StringBuilder sbDir = new StringBuilder(ParamBean.getInstance().getChrootDir(appProvider.getCode()));
        sbDir.append(File.separator + ReportExtractScript.REPORTS_DIR);

        if (!StringUtils.isBlank(entity.getCategory())) {
            sbDir.append(File.separator + entity.getCategory());
        }

        String filename = DateUtils.evaluteDateFormat(entity.getFilenameFormat());
        filename = evaluateStringExpression(filename, entity);

        if (entity.getScriptType().equals(ReportExtractScriptTypeEnum.SQL)) {
            List<Map<String, Object>> resultList = scriptInstanceService.executeNativeSelectQuery(entity.getSqlQuery(), context);

            FileWriter fileWriter = null;
            StringBuilder line = new StringBuilder("");
            if (resultList != null && !resultList.isEmpty()) {
                log.debug("{} record/s found", resultList.size());

                try {
                    File dir = new File(sbDir.toString());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(sbDir.toString() + File.separator + filename);
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

                } catch (IOException e) {
                    log.error("Cannot write report to file");
                    throw new BusinessException("Cannot write report to file.");

                } finally {
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }

        } else {
            context.put(ReportExtractScript.DIR, sbDir.toString());
            if (!StringUtils.isBlank(entity.getFilenameFormat())) {
                context.put(ReportExtractScript.FILENAME, filename);
            }

            scriptInstanceService.execute(entity.getScriptInstance().getCode(), context);
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

}
