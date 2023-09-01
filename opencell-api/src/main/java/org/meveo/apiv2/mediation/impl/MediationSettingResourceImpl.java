package org.meveo.apiv2.mediation.impl;

import java.util.Optional;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.mediation.EdrVersioningRuleSwapping;
import org.meveo.apiv2.mediation.ImmutableEdrVersioningRule;
import org.meveo.apiv2.mediation.ImmutableMediationSetting;
import org.meveo.apiv2.mediation.resource.MediationSettingResource;
import org.meveo.apiv2.mediation.service.EdrVersioningRuleApiService;
import org.meveo.apiv2.mediation.service.EdrVersioningRuleMapper;
import org.meveo.apiv2.mediation.service.MediationSettingApiService;
import org.meveo.apiv2.mediation.service.MediationSettingMapper;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.BaseEntity;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;

/**
 * 
 * @author Tarik FA.
 * @version 13.0.0
 *
 */
@Interceptors({ WsRestApiInterceptor.class })
public class MediationSettingResourceImpl implements MediationSettingResource {

	private MediationSettingMapper mapper = new MediationSettingMapper();
	
	private EdrVersioningRuleMapper edrVersioningRuleMapper = new EdrVersioningRuleMapper();
	
	@Inject
	private MediationSettingApiService mediationSettingApiService;
	@Inject
	private EdrVersioningRuleApiService edrVersioningRuleApiService;
	
	
	@Override
	public Response create(org.meveo.apiv2.mediation.MediationSetting mediationSetting) {
		MediationSetting entity = mediationSettingApiService.create(mapper.toEntity(mediationSetting));
		return getResponse(entity, false);
	}

	@Override
	public Response update(Long mediationRuleId, org.meveo.apiv2.mediation.MediationSetting mediationSetting) {
		Optional.of(mediationRuleId).orElseThrow(BadRequestException::new);
		MediationSetting entity = mediationSettingApiService.update(mediationRuleId, mapper.toEntity(mediationSetting)).get();
		return getResponse(entity, false);
	}


	@Override
	public Response createEdrVersionRule(org.meveo.apiv2.mediation.EdrVersioningRule edrVersioningRule) {
		EdrVersioningRule entity = edrVersioningRuleApiService.create(edrVersioningRuleMapper.toEntity(edrVersioningRule));
		return getResponse(entity, true);
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
    private org.meveo.apiv2.mediation.ImmutableEdrVersioningRule toResourceWithLink(org.meveo.apiv2.mediation.EdrVersioningRule edrVersioningRule) {
        return ImmutableEdrVersioningRule.copyOf(edrVersioningRule)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(MediationSettingResource.class)
                                .withId(edrVersioningRule.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }
    
    private Response getResponse(BaseEntity entity, boolean isRule) {
    	return Response
				.created(LinkGenerator.getUriBuilderFromResource(MediationSettingResource.class, entity.getId()).build())
				.entity(isRule ? toResourceWithLink(edrVersioningRuleMapper.toResource(entity)) : toResourceWithLink(mapper.toResource(entity)))
				.build();
    }

    @Transactional
	@Override
	public Response updateEdrVersionRule(Long edrVersionRuleId,
			org.meveo.apiv2.mediation.EdrVersioningRule edrVersioningRule) {
		Optional.of(edrVersionRuleId).orElseThrow(BadRequestException::new);
		var entity = edrVersioningRuleApiService.update(edrVersionRuleId, edrVersioningRuleMapper.toEntity(edrVersioningRule)).get();
		return getResponse(entity, true);
	}

	@Override
	public Response swapPriority(EdrVersioningRuleSwapping edrVersioningRuleSwapping) {
		edrVersioningRuleApiService.swapPriority(edrVersioningRuleSwapping.getRule1().getId(), edrVersioningRuleSwapping.getRule2().getId());
		return Response.ok().build();
	}

	@Override
	@Transactional
	public Response deleteEdrVersioningRule(Long edrVersionRuleId) {
		var deletedEdrVersion = edrVersioningRuleApiService.delete(edrVersionRuleId);
		return getResponse(deletedEdrVersion.get(), true);
	}


}
