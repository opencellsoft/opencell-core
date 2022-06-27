package org.meveo.apiv2.mediation.impl;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.mediation.ImmutableMediationSetting;
import org.meveo.apiv2.mediation.resource.MediationSettingResource;
import org.meveo.apiv2.mediation.service.MediationSettingApiService;
import org.meveo.apiv2.mediation.service.MediationSettingMapper;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.mediation.MediationSetting;

/**
 * 
 * @author Tarik FA.
 * @version 13.0.0
 *
 */
public class MediationSettingResourceImpl implements MediationSettingResource {

	private MediationSettingMapper mapper = new MediationSettingMapper();
	
	@Inject
	private MediationSettingApiService mediationSettingApiService;
	
	
	@Override
	public Response create(org.meveo.apiv2.mediation.MediationSetting mediationSetting) {
		MediationSetting entity = mediationSettingApiService.create(mapper.toEntity(mediationSetting));
		return Response
				.created(LinkGenerator.getUriBuilderFromResource(MediationSettingResource.class, entity.getId()).build())
				.entity(toResourceWithLink(mapper.toResource(entity)))
				.build();
	}

	@Override
	public Response update(Long mediationRuleId, org.meveo.apiv2.mediation.MediationSetting mediationSetting) {
		Optional.of(mediationRuleId).orElseThrow(BadRequestException::new);
		MediationSetting entity = mediationSettingApiService.update(mediationRuleId, mapper.toEntity(mediationSetting)).get();
		return Response
				.created(LinkGenerator.getUriBuilderFromResource(MediationSettingResource.class, entity.getId()).build())
				.entity(toResourceWithLink(mapper.toResource(entity)))
				.build();
	}
	

    private org.meveo.apiv2.mediation.ImmutableMediationSetting toResourceWithLink(org.meveo.apiv2.mediation.MediationSetting mediationSetting) {
        return ImmutableMediationSetting.copyOf(mediationSetting)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(MediationSettingResource.class)
                                .withId(mediationSetting.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }


}
