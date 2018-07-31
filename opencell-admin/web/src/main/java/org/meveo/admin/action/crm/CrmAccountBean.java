package org.meveo.admin.action.crm;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CrmAccountService;

/**
 * Standard backing bean for {@link AccountEntity} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */
@Named
@ViewScoped
public class CrmAccountBean extends BaseBean<AccountEntity> {

    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link AccountEntity} service. Extends {@link PersistenceService}.
     */
    @Inject
    private CrmAccountService crmAccountService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CrmAccountBean() {
        super(AccountEntity.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return Account entity
     */
    @Produces
    @Named("accountEntity")
    public AccountEntity init() {
        return initEntity();
    }

    public String getIdParameterName(String type) {
        if (type.equals(Customer.ACCOUNT_TYPE)) {
            return "customerId";
        }
        if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
            return "customerAccountId";
        }
        if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
            return "billingAccountId";
        }
        if (type.equals(UserAccount.ACCOUNT_TYPE)) {
            return "userAccountId";
        }
        return "customerId";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<AccountEntity> getPersistenceService() {
        return crmAccountService;
    }

    /*
     * @see org.meveo.admin.action.BaseBean#getDefaultSort()
     */
    @Override
    protected String getDefaultSort() {
        return "code";
    }

}
