package org.meveo.service.security.filter;

import java.io.Serializable;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.meveo.model.SecuredBusinessEntityFilter;
import org.slf4j.Logger;

@Singleton
public class SecuredBusinessEntityFilterFactory implements Serializable{

	private static final long serialVersionUID = 2249067511854832348L;
	
	@Any
	@Inject
	private Instance<SecuredBusinessEntityFilter> filters;
	
	@Inject
	private Logger log;
	
	public SecuredBusinessEntityFilter getFilter(Class<? extends SecuredBusinessEntityFilter> filterClass){
		for(SecuredBusinessEntityFilter filter : filters){
			if(filter.getFilterClass().equals(filterClass)){
				log.debug("SecuredBusinessEntityFilter found. Returning filter: {}.", filterClass.getTypeName());
				return filter;
			}
		}
		log.warn("No SecuredBusinessEntityFilter instance of type {} found.", filterClass.getTypeName());
		return null;
	}
}
