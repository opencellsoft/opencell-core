package org.meveo.apiv2.mediation.service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.mediation.ImmutableEdrVersioningRule;
import org.meveo.apiv2.mediation.ImmutableMediationSetting;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;

public class EdrVersioningRuleMapper extends ResourceMapper<org.meveo.apiv2.mediation.EdrVersioningRule, BaseEntity> {

	@Override
	public org.meveo.apiv2.mediation.EdrVersioningRule toResource(BaseEntity entity) {
		try {
			EdrVersioningRule edrVersioningRule = (EdrVersioningRule)entity;
			return ImmutableEdrVersioningRule.builder()
					.id(entity.getId())
					.priority(edrVersioningRule.getPriority())
					.criteriaEL(edrVersioningRule.getCriteriaEL())
					.isNewVersionEL(edrVersioningRule.getIsNewVersionEL())
					.keyEL(edrVersioningRule.getKeyEL())
					.mediationSetting(edrVersioningRule.getMediationSetting() != null ? ImmutableMediationSetting.builder().id(edrVersioningRule.getMediationSetting().getId()).enableEdrVersioning(edrVersioningRule.getMediationSetting().isEnableEdrVersioning()).build() : null)
					.build();
		}catch(Exception e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public EdrVersioningRule toEntity(org.meveo.apiv2.mediation.EdrVersioningRule resource) {
		try {
			EdrVersioningRule entity = new EdrVersioningRule();
			entity.setId(resource.getId());
			entity.setPriority(resource.getPriority());
			entity.setCriteriaEL(resource.getCriteriaEL());
			entity.setKeyEL(resource.getKeyEL());
			entity.setIsNewVersionEL(resource.getIsNewVersionEL());
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
	

	public Set<org.meveo.apiv2.mediation.EdrVersioningRule> toResource(Set<EdrVersioningRule> entity) {
		return CollectionUtils.isEmpty(entity) ? Collections.emptySet() : entity.stream().map(this::toResource).collect(Collectors.toSet());
	}
	

	public Set<EdrVersioningRule> toEntity(Set<org.meveo.apiv2.mediation.EdrVersioningRule> resource) {
		return CollectionUtils.isEmpty(resource) ? Collections.emptySet() : resource.stream().map(this::toEntity).collect(Collectors.toSet());
	}

}