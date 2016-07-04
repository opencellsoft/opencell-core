package org.meveo.service.security;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.service.crm.impl.CustomerService;

@Stateless
public class SecuredCustomerService extends SecuredBusinessEntityService {

	@Inject
	private CustomerService customerService;

	@Override
	public BusinessEntity getEntityByCode(String code, User user) {
		return customerService.findByCode(code, user.getProvider());
	}

	@Override
	public List<? extends BusinessEntity> list() {
		return customerService.list();
	}

	@Override
	public Class<? extends BusinessEntity> getEntityClass() {
		return customerService.getEntityClass();
	}

}
