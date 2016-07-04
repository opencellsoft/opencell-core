package org.meveo.service.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.BillingAccountService;

@Stateless
public class SecuredBillingAccountService extends SecuredBusinessEntityService {

	@Inject
	private BillingAccountService billingAccountService;

	@Override
	public BusinessEntity getEntityByCode(String code, User user) {
		return billingAccountService.findByCode(code, user.getProvider());
	}

	@Override
	public List<? extends BusinessEntity> list() {
		return billingAccountService.list();
	}

	@Override
	public Class<? extends BusinessEntity> getEntityClass() {
		return billingAccountService.getEntityClass();
	}

	@Override
	public Set<BusinessEntity> getParentEntities(BusinessEntity entity) {
		Set<BusinessEntity> parents = new HashSet<>();
		if (entity != null && entity instanceof BillingAccount) {
			BillingAccount billingAccount = (BillingAccount) entity;
			if (billingAccount != null && billingAccount.getCustomerAccount() != null) {
				CustomerAccount customerAccount = billingAccount.getCustomerAccount();
				// add the CustomerAccount entity as a parent
				parents.add(customerAccount);
				// lookup the parents of the Customer entity
				parentLookup(parents, customerAccount);
			}
		}
		return parents;
	}
}
