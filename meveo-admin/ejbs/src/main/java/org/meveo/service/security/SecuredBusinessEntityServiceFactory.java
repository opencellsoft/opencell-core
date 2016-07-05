package org.meveo.service.security;

import java.io.Serializable;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

@Singleton
public class SecuredBusinessEntityServiceFactory implements Serializable{

	private static final long serialVersionUID = 21208948861701852L;
	
	@Inject
	@Any
	private Instance<SecuredBusinessEntityService> securedServices;
	
	@Inject
	private Logger log;
	
	
	public SecuredBusinessEntityService getService(String entityClassName){
		try {
			Class<?> entityClass = Class.forName(entityClassName);
			for(SecuredBusinessEntityService service : securedServices){
				if(service.getEntityClass().equals(entityClass)){
					return service;
				}
			}
		} catch (ClassNotFoundException e) {
			log.error("Class: {} not found.", entityClassName);
		}
		return null;
	}
	
}
