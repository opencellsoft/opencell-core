package org.meveo.service.security;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
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
}
