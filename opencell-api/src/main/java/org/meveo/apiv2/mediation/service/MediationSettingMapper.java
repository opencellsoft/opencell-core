package org.meveo.apiv2.mediation.service;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.mediation.ImmutableMediationSetting;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.mediation.MediationSetting;

public class MediationSettingMapper extends ResourceMapper<org.meveo.apiv2.mediation.MediationSetting, BaseEntity>{

	
	private EdrVersioningRuleMapper edrVersioningRuleMapper = new EdrVersioningRuleMapper();
	
	@Override
	public org.meveo.apiv2.mediation.MediationSetting toResource(BaseEntity entity) {
		try {
			MediationSetting mediationSetting = (MediationSetting) entity;
			var builder = ImmutableMediationSetting.builder()
					.id(entity.getId())
					.isEnableEdrVersioning(mediationSetting.isEnableEdrVersioning());
			if(!CollectionUtils.isEmpty(mediationSetting.getRules())) {
				builder.rules(edrVersioningRuleMapper.toResource(mediationSetting.getRules()));
			}
			return builder.build();
		}catch(Exception e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public MediationSetting toEntity(org.meveo.apiv2.mediation.MediationSetting resource) {
		try {
			MediationSetting mediationSetting =  new MediationSetting();
			mediationSetting.setId(resource.getId());
			mediationSetting.setEnableEdrVersioning(resource.isEnableEdrVersioning());
			if(!CollectionUtils.isEmpty(resource.getRules())) {
				mediationSetting.setRules(edrVersioningRuleMapper.toEntity(resource.getRules()));
			}
			return mediationSetting;
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
	}

}
