package org.meveo.service.finance;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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
                context.put(pair.getKey().toString(), pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        StringBuilder sb = new StringBuilder(ParamBean.getInstance().getProperty("providers.rootDir", "/opt/opencelldata"));
        sb.append(File.separator + appProvider.getCode() + File.separator + ReportExtractScript.REPORTS_DIR);

        if (!StringUtils.isBlank(entity.getCategory())) {
            sb.append(File.separator + entity.getCategory());
        }

        context.put(ReportExtractScript.DIR, sb.toString());

        if (!StringUtils.isBlank(entity.getFilenameFormat())) {
            context.put(ReportExtractScript.FILENAME, DateUtils.evaluteDateFormat(entity.getFilenameFormat()));
        }

        if (entity.getScriptType().equals(ReportExtractScriptTypeEnum.SQL)) {
            Object resultList = scriptInstanceService.executeSelectQuery(entity.getSqlQuery(), context);

        } else {
            scriptInstanceService.execute(entity.getScriptInstance().getCode(), context);
        }
    }

}
