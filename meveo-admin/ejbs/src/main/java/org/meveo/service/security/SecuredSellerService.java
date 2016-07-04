package org.meveo.service.security;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.service.admin.impl.SellerService;

@Stateless
public class SecuredSellerService extends SecuredBusinessEntityService {

	@Inject
	private SellerService sellerService;

	@Override
	public BusinessEntity getEntityByCode(String code, User user) {
		return sellerService.findByCode(code, user.getProvider());
	}

	@Override
	public List<? extends BusinessEntity> list() {
		return sellerService.list();
	}

	@Override
	public Class<? extends BusinessEntity> getEntityClass() {
		return sellerService.getEntityClass();
	}
}
