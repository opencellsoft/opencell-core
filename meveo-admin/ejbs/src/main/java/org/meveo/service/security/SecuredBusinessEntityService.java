package org.meveo.service.security;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

/**
 * SecuredBusinessEntity Service implementation.
 *
 * @author Tony Alejandro
 */
public abstract class SecuredBusinessEntityService {
	@Inject
	protected Logger log;
	
	@Inject
	private SecuredBusinessEntityServiceFactory factory;
	
	public abstract BusinessEntity getEntityByCode(String code, User user);

	public abstract List<? extends BusinessEntity> list();

	public abstract Class<? extends BusinessEntity> getEntityClass();
	
	public abstract Set<BusinessEntity> getParentEntities(BusinessEntity entity);
	
	protected void parentLookup(Set<BusinessEntity> parents, BusinessEntity entity){
		String serviceName = ReflectionUtils.getCleanClassName(entity.getClass().getName());
		SecuredBusinessEntityService service = factory.getService(serviceName);
		parents.addAll(service.getParentEntities(entity));
	}
	
}