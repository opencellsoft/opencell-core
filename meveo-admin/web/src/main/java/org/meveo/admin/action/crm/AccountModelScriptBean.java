package org.meveo.admin.action.crm;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.crm.AccountModelScript;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.AccountModelScriptService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class AccountModelScriptBean extends BaseBean<AccountModelScript> {

	private static final long serialVersionUID = -8844358920768852531L;

	@Inject
	private AccountModelScriptService accountModelScriptService;

	public AccountModelScriptBean() {
		super(AccountModelScript.class);
	}

	@Override
	protected IPersistenceService<AccountModelScript> getPersistenceService() {
		return accountModelScriptService;
	}

}
