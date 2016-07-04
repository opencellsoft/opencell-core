package org.meveo.service.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.billing.impl.UserAccountService;

@Stateless
public class SecuredUserAccountService extends SecuredBusinessEntityService {

	@Inject
	private UserAccountService userAccountService;

	@Override
	public BusinessEntity getEntityByCode(String code, User user) {
		return userAccountService.findByCode(code, user.getProvider());
	}

	@Override
	public List<? extends BusinessEntity> list() {
		return userAccountService.list();
	}

	@Override
	public Class<? extends BusinessEntity> getEntityClass() {
		return userAccountService.getEntityClass();
	}

	@Override
	public Set<BusinessEntity> getParentEntities(BusinessEntity entity) {
		Set<BusinessEntity> parents = new HashSet<>();
		if (entity != null && entity instanceof UserAccount) {
			UserAccount userAccount = (UserAccount) entity;
			if (userAccount != null && userAccount.getBillingAccount() != null) {
				BillingAccount billingAccount = userAccount.getBillingAccount();
				// add the BillingAccount entity as a parent
				parents.add(billingAccount);
				// lookup the parents of the BillingAccount entity
				parentLookup(parents, billingAccount);
			}
		}
		return parents;
	}

}
