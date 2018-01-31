package org.meveo.admin.action.finance;

import java.util.Calendar;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.finance.ReportExtractService;
import org.meveo.service.script.finance.ReportExtractScript;

/**
 * @author Edward P. Legaspi
 * @created 29 Jan 2018
 **/
@Named
@ViewScoped
public class ReportExtractBean extends UpdateMapTypeFieldBean<ReportExtract> {

    private static final long serialVersionUID = -3817116164208834748L;

    @Inject
    private ReportExtractService reportExtractService;

    public ReportExtractBean() {
        super(ReportExtract.class);
    }

    @Override
    public ReportExtract initEntity() {
        entity = super.initEntity();

        if (entity.isTransient()) {
            Calendar cal = Calendar.getInstance();
            entity.getParams().put(ReportExtractScript.START_DATE, DateUtils.formatDateWithPattern(cal.getTime(), ParamBean.getInstance().getDateFormat()));
            cal.add(Calendar.MONTH, 1);
            entity.getParams().put(ReportExtractScript.END_DATE, DateUtils.formatDateWithPattern(cal.getTime(), ParamBean.getInstance().getDateFormat()));
        }

        extractMapTypeFieldFromEntity(entity.getParams(), "params");

        return entity;
    }

    @Override
    protected IPersistenceService<ReportExtract> getPersistenceService() {
        return reportExtractService;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        if (entity.getScriptType().equals(ReportExtractScriptTypeEnum.SQL)) {
            entity.setScriptInstance(null);
        } else {
            entity.setSqlQuery(null);
        }
        return super.saveOrUpdate(killConversation);
    }

    @ActionMethod
    public void runReport() {
        try {
            reportExtractService.runReport(entity);
        } catch (BusinessException e) {
            log.error("Failed running report: {}", e.getMessage());
            messages.error(e.getMessage());
        }
    }

}
