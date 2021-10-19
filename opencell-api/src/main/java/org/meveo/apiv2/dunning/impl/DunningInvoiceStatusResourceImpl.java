package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningInvoiceStatus;
import org.meveo.apiv2.dunning.resource.DunningInvoiceStatusResource;
import org.meveo.apiv2.dunning.service.DunningInvoiceStatusApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class DunningInvoiceStatusResourceImpl implements DunningInvoiceStatusResource {

	@Inject
	private DunningInvoiceStatusApiService dunningInvoiceStatusApiService;
	private DunningInvoiceStatusMapper mapper = new DunningInvoiceStatusMapper();
	
	@Override
	public Response create(org.meveo.apiv2.dunning.DunningInvoiceStatus dunningInvoiceStatus) {
		var entity = mapper.toEntity(dunningInvoiceStatus);
		var savedEntity = dunningInvoiceStatusApiService.create(entity);
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningInvoiceStatusResource.class, savedEntity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedEntity)))
				.build();
	}

	@Override
	public Response update(org.meveo.apiv2.dunning.DunningInvoiceStatus entityDto, String dunningSettingsCode, Long id) {
		var updatedEntity = dunningInvoiceStatusApiService.update(id, mapper.toEntity(entityDto)).get();
		return Response.status(Status.ACCEPTED).entity(toResourceOrderWithLink(mapper.toResource(updatedEntity))).build();
	}
	
	private org.meveo.apiv2.dunning.DunningInvoiceStatus toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningInvoiceStatus entityDto) {
		return ImmutableDunningInvoiceStatus.copyOf(entityDto)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(DunningInvoiceStatusResource.class)
											.withId(entityDto.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}

	@Override
	public Response delete(String dunningSettingsCode, Long id) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningInvoiceStatusApiService.delete(id).get()))).build();
	}


}
