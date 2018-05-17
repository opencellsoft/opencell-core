package org.meveo.admin.action.finance;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.finance.ReportExtractExecutionResult;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.finance.ReportExtractExecutionResultService;

/**
 * Bean class for managing ReportExtractExecutionResult entity.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 16 May 2018
 **/
@Named
@ViewScoped
public class ReportExtractExecutionResultBean extends BaseBean<ReportExtractExecutionResult> {

    private static final long serialVersionUID = 7729137918906130036L;

    @Inject
    private ReportExtractExecutionResultService reportExtractExecutionResultService;

    public ReportExtractExecutionResultBean() {
        super(ReportExtractExecutionResult.class);
    }
    
    @Override
    public String getEditViewName() {
        return "reportExtractHistories";
    }

    @Override
    protected IPersistenceService<ReportExtractExecutionResult> getPersistenceService() {
        return reportExtractExecutionResultService;
    }

}
