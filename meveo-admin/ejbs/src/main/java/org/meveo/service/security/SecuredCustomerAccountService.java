package org.meveo.service.security;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
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

}
