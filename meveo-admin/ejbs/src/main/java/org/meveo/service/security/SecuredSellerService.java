package org.meveo.service.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
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

	@Override
	public Set<BusinessEntity> getParentEntities(BusinessEntity entity) {
		Set<BusinessEntity> parents = new HashSet<>();
		if (entity != null && entity instanceof Seller) {
			fetchParents(parents, (Seller) entity);
		}
		return parents;
	}

	private void fetchParents(Set<BusinessEntity> parents, Seller seller) {
		if (seller != null && seller.getSeller() != null) {
			parents.add(seller.getSeller());
			fetchParents(parents, seller.getSeller());
		}
	}
}
