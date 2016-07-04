package org.meveo.service.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
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

	@Override
	public Set<BusinessEntity> getParentEntities(BusinessEntity entity) {
		Set<BusinessEntity> parents = new HashSet<>();
		if (entity != null && entity instanceof Customer) {
			Customer customer = (Customer) entity;
			if (customer != null && customer.getSeller() != null) {
				Seller seller = customer.getSeller();
				// add the Seller entity as a parent
				parents.add(seller);
				// lookup the parents of the Seller entity
				parentLookup(parents, seller);
			}
		}
		return parents;
	}

}
