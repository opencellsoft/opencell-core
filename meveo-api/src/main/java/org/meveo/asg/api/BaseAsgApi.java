package org.meveo.asg.api;

import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.asg.api.model.EntityCodeEnum;
import org.meveo.asg.api.service.AsgIdMappingService;

/**
 * @author Edward P. Legaspi
 **/
public abstract class BaseAsgApi extends BaseApi {

	@Inject
	protected AsgIdMappingService asgIdMappingService;

	public void removeAsgMapping(String asgId, EntityCodeEnum entityType) {
		asgIdMappingService.removeByCodeAndType(em, asgId, entityType);
	}

}
