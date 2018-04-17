package org.meveo.admin.action.billing;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.AccountingCode;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.AccountingCodeService;

/**
 * Controller to manage detail view of {@link AccountingCode}.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
@Named
@ViewScoped
public class AccountingCodeBean extends BaseBean<AccountingCode> {

    private static final long serialVersionUID = 2651189307182763842L;

    @Inject
    private AccountingCodeService accountingCodeService;

    public AccountingCodeBean() {
        super(AccountingCode.class);
    }

    @Override
    protected IPersistenceService<AccountingCode> getPersistenceService() {
        return accountingCodeService;
    }

}
