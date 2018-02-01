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
import org.meveo.service.base.PersistenceService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.finance.ReportExtractScript;

/**
 * @author Edward P. Legaspi
 * @created 29 Jan 2018
 **/
@Stateless
public class ReportExtractService extends PersistenceService<ReportExtract> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @SuppressWarnings("rawtypes")
    public void runReport(ReportExtract entity) throws BusinessException {
        Map<String, Object> context = new HashMap<>();
        if (entity.getParams() != null) {
            Iterator it = entity.getParams().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (!StringUtils.isBlank(pair.getValue())) {
                    if (pair.getKey().toString().endsWith("_DATE")) {
                        context.put(pair.getKey().toString(), DateUtils.parseDateWithPattern(pair.getValue().toString(), ParamBean.getInstance().getDateFormat()));
                    } else {
                        context.put(pair.getKey().toString(), pair.getValue());
                    }
                }
            }
        }

        StringBuilder sbDir = new StringBuilder(ParamBean.getInstance().getProperty("providers.rootDir", "/opt/opencelldata"));
        sbDir.append(File.separator + appProvider.getCode() + File.separator + ReportExtractScript.REPORTS_DIR);

        if (!StringUtils.isBlank(entity.getCategory())) {
            sbDir.append(File.separator + entity.getCategory());
        }

        String filename = DateUtils.evaluteDateFormat(entity.getFilenameFormat());

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

}
