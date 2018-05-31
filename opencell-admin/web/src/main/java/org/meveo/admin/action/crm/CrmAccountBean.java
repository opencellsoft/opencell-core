package org.meveo.admin.action.crm;

import java.util.ArrayList;
import java.util.List;

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
 * Standard backing bean for {@link AccountEntity} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class CrmAccountBean extends BaseBean<AccountEntity> {

	private static final long serialVersionUID = 2601228067924700331L;
	/**
	 * Injected @{link AccountEntity} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private CrmAccountService crmAccountService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CrmAccountBean() {
		super(AccountEntity.class);
	}
	
	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @return Account entity
	 */
	@Produces
	@Named("accountEntity")
	public AccountEntity init() {
		return initEntity();
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<AccountEntity> getPersistenceService() {
		return crmAccountService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
	public String getView(String type) {
		if (type.equals(Customer.ACCOUNT_TYPE)) {
			return "/pages/crm/customers/customerDetail.xhtml";
		} else if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
			return "/pages/payments/customerAccounts/customerAccountDetail.xhtml";
		}
		if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
			return "/pages/billing/billingAccounts/billingAccountDetail.xhtml";
		}
		if (type.equals(UserAccount.ACCOUNT_TYPE)) {
			return "/pages/billing/userAccounts/userAccountDetail.xhtml";
		} else {
			return "/pages/crm/customers/customerDetail.xhtml";
		}
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
	
	public List<AccountEntity> listDistinctAccounts() {
		List<AccountEntity> fetchedAccountEntities = super.listAll();
		List<AccountEntity> accountEntities = new ArrayList<>();
		boolean accountWithCodeExists = false;
		for(AccountEntity ae : fetchedAccountEntities) {
			for(AccountEntity e : accountEntities) {
				if(ae.getCode().equals(e.getCode())) {
					accountWithCodeExists = true;
				}
			}
			if(!accountWithCodeExists) {
				accountEntities.add(ae);
			}
		}
		return accountEntities;
	}
	
}
