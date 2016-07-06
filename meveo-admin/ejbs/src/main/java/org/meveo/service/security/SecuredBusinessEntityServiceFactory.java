package org.meveo.service.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.slf4j.Logger;

/**
 * This factory encapsulates the creation and retrieval of
 * {@link SecuredBusinessEntityService} instances.
 * 
 * @author Tony Alejandro
 *
 */
@Singleton
public class SecuredBusinessEntityServiceFactory implements Serializable {

	private static final long serialVersionUID = 21208948861701852L;

	@Inject
	@Any
	private Instance<SecuredBusinessEntityService> securedServices;

	@Inject
	private Logger log;

	private Map<String, SecuredBusinessEntityService> serviceMap = new HashMap<>();
	
	public SecuredBusinessEntityService getService(Class<? extends BusinessEntity> entityClass) {
		initialize();
		String entityClassName = ReflectionUtils.getCleanClassName(entityClass.getTypeName());
		return getService(entityClassName);
	}

	public SecuredBusinessEntityService getService(String entityClassName) {
		initialize();
		SecuredBusinessEntityService service = serviceMap.get(entityClassName);
		if (service == null) {
			log.warn("No SecuredBusinessEntityService instance of type {} found.", entityClassName);
		}
		return service;
	}

	private void initialize() {
		if (serviceMap.isEmpty()) {
			log.debug("Initializing SecuredBusinessEntityServices map.");
			String entityName = null;
			for (SecuredBusinessEntityService service : securedServices) {
				entityName = ReflectionUtils.getCleanClassName(service.getEntityClass().getTypeName());
				serviceMap.put(entityName, service);
			}
			log.debug("Services map initialization done. Found {} SecuredBusinessEntityServices.", serviceMap.size());
		}
	}

}
