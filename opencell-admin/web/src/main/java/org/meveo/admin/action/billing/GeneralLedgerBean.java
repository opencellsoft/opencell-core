package org.meveo.admin.action.billing;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.GeneralLedger;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.GeneralLedgerService;

@Named
@ViewScoped
public class GeneralLedgerBean extends BaseBean<GeneralLedger> {

    private static final long serialVersionUID = 1L;

    @Inject
    GeneralLedgerService generalLedgerService;

    public GeneralLedgerBean() {
        super(GeneralLedger.class);
    }

    @Override
    protected IPersistenceService<GeneralLedger> getPersistenceService() {
        return generalLedgerService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }
}