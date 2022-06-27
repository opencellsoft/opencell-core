package org.meveo.apiv2.mediation.service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.mediation.ImmutableEdrVersioningRule;
import org.meveo.apiv2.mediation.ImmutableMediationSetting;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;

public class EdrVersioningRuleMapper extends ResourceMapper<org.meveo.apiv2.mediation.EdrVersioningRule, EdrVersioningRule> {

	@Override
	protected org.meveo.apiv2.mediation.EdrVersioningRule toResource(EdrVersioningRule entity) {
		try {
			ImmutableEdrVersioningRule immutableEdrVersioningRule = (ImmutableEdrVersioningRule) initResource(ImmutableEdrVersioningRule.class, entity);
			return ImmutableEdrVersioningRule.builder().from(immutableEdrVersioningRule)
					.id(entity.getId())
					.priority(entity.getPriority())
					.criterialEl(entity.getCriterialEl())
					.isNewVersionEl(entity.getIsNewVersionEl())
					.keyEl(entity.getKeyEl())
					.mediationSetting(entity.getMediationSetting() != null ? ImmutableMediationSetting.builder().id(entity.getMediationSetting().getId()).isEnableEdrVersioning(entity.getMediationSetting().isEnableEdrVersioning()).build() : null)
					.build();
		}catch(Exception e) {
			throw new BusinessException(e);
		}
	}

	@Override
	protected EdrVersioningRule toEntity(org.meveo.apiv2.mediation.EdrVersioningRule resource) {
		try {
			EdrVersioningRule entity = initEntity(resource, new EdrVersioningRule());
			entity.setId(resource.getId());
			entity.setPriority(resource.getPriority());
			entity.setCriterialEl(resource.getCriterialEl());
			entity.setKeyEl(resource.getKeyEl());
			if(resource.getMediationSetting() != null) {
				MediationSetting mediationSetting = new MediationSetting();
				mediationSetting.setId(resource.getMediationSetting().getId());
				entity.setMediationSetting(mediationSetting);
			}
			return entity;
		}catch(Exception e) {
			throw new BusinessException(e);
		}
		
	}
	

	protected Set<org.meveo.apiv2.mediation.EdrVersioningRule> toResource(Set<EdrVersioningRule> entity) {
		return CollectionUtils.isEmpty(entity) ? Collections.emptySet() : entity.stream().map(this::toResource).collect(Collectors.toSet());
	}
	

	protected Set<EdrVersioningRule> toEntity(Set<org.meveo.apiv2.mediation.EdrVersioningRule> resource) {
		return CollectionUtils.isEmpty(resource) ? Collections.emptySet() : resource.stream().map(this::toEntity).collect(Collectors.toSet());
	}

}
