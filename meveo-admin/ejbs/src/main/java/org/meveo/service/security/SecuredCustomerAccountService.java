package org.meveo.service.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class SecuredCustomerAccountService extends SecuredBusinessEntityService {

	@Inject
	private CustomerAccountService customerAccountService;

	@Override
	public BusinessEntity getEntityByCode(String code, User user) {
		return customerAccountService.findByCode(code, user.getProvider());
	}

	@Override
	public List<? extends BusinessEntity> list() {
		return customerAccountService.list();
	}

	@Override
	public Class<? extends BusinessEntity> getEntityClass() {
		return customerAccountService.getEntityClass();
	}

	@Override
	public Set<BusinessEntity> getParentEntities(BusinessEntity entity) {
		Set<BusinessEntity> parents = new HashSet<>();
		if (entity != null && entity instanceof CustomerAccount) {
			CustomerAccount customerAccount = (CustomerAccount) entity;
			if (customerAccount != null && customerAccount.getCustomer() != null) {
				Customer customer = customerAccount.getCustomer();
				// add the Customer entity as a parent
				parents.add(customer);
				// lookup the parents of the Customer entity
				parentLookup(parents, customer);
			}
		}
		return parents;
	}

}
