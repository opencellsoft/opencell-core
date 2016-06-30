package org.meveo.service.security;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.service.billing.impl.BillingAccountService;

@Stateless
public class SecuredBillingAccountService extends SecuredBusinessEntityService {

	@Inject
	private BillingAccountService billingAccountService;
	
	@Override
	public List<? extends BusinessEntity> list() {
		return billingAccountService.list();
	}

	@Override
	public Class<? extends BusinessEntity> getEntityClass() {
		return billingAccountService.getEntityClass();
	}

}
