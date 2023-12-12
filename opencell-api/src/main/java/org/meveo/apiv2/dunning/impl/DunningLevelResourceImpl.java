package org.meveo.apiv2.dunning.impl;

import static javax.ws.rs.core.Response.ok;

import java.util.Optional;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.resource.DunningLevelResource;
import org.meveo.apiv2.dunning.service.DunningLevelApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.dunning.DunningLevel;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningLevelResourceImpl implements DunningLevelResource {

	@Inject
	private DunningLevelApiService dunningLevelApiService;
	private DunningLevelMapper mapper = new DunningLevelMapper();

	@Override
	public Response create(org.meveo.apiv2.dunning.DunningLevel dunningLevel) {
		var entity = mapper.toEntity(dunningLevel);
		var savedDunningLevel = dunningLevelApiService.create(entity);
		return ok(LinkGenerator.getUriBuilderFromResource(DunningLevelResource.class, savedDunningLevel.getId()).build())
				.entity(mapper.toResource(savedDunningLevel))
				.build();
	}

	@Override
	public Response update(org.meveo.apiv2.dunning.DunningLevel dunningLevel, Long dunningLevelId) {
		var entity = mapper.toEntity(dunningLevel);
		var updated = dunningLevelApiService.update(dunningLevelId, entity).get();
		return ok(LinkGenerator.getUriBuilderFromResource(DunningLevelResource.class, updated.getId()).build())
				.entity(mapper.toResource(updated))
				.build();
	}

	@Override
	public Response delete(Long dunningLevelId) {
		Optional<DunningLevel> dunningLevel = dunningLevelApiService.delete(dunningLevelId);
		ResponseBuilder response = ok();
		if( dunningLevel.isPresent()) {
			response.entity(mapper.toResource(dunningLevel.get()));
		}
		return response.build();
	}
	
	@Override
	public Response findByCode(String dunningLevelCode) {
		Optional<DunningLevel> dunningLevel = dunningLevelApiService.findByCode(dunningLevelCode);
		ResponseBuilder response = ok();
		if( dunningLevel.isPresent()) {
			response.entity(mapper.toResource(dunningLevel.get()));
		}
		return response.build();
	}
}