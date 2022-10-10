package org.meveo.apiv2.securityDeposit.securityDepositTemplate.impl;

import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositTemplate;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;

public class SecurityDepositTemplateMapper extends ResourceMapper<org.meveo.apiv2.securityDeposit.SecurityDepositTemplate, SecurityDepositTemplate> {


    @Override
    protected org.meveo.apiv2.securityDeposit.SecurityDepositTemplate toResource(SecurityDepositTemplate entity) {
        return ImmutableSecurityDepositTemplate.builder()
                .id(entity.getId())
                .currency(buildById(entity.getCurrency()))
                .templateName(entity.getTemplateName())
                .allowValidityDate(entity.isAllowValidityDate())
                .allowValidityPeriod(entity.isAllowValidityPeriod())
                .minAmount(entity.getMinAmount())
                .maxAmount(entity.getMaxAmount())
                .status(entity.getStatus())
                .numberOfInstantiation(entity.getNumberOfInstantiation() == null ? 0  : entity.getNumberOfInstantiation())
                .build();
    }

    @Override
    protected SecurityDepositTemplate toEntity(org.meveo.apiv2.securityDeposit.SecurityDepositTemplate resource) {
        return toEntity(new SecurityDepositTemplate(), resource);
    }

    protected SecurityDepositTemplate toEntity(SecurityDepositTemplate securityDepositTemplate, org.meveo.apiv2.securityDeposit.SecurityDepositTemplate resource) {
        securityDepositTemplate.setTemplateName(resource.getTemplateName());

        if(resource.getCurrency() != null && (resource.getCurrency().getId() != null || resource.getCurrency().getCode() != null))
        {
            Currency currency = new Currency();
            currency.setId(resource.getCurrency().getId());
            currency.setCurrencyCode(resource.getCurrency().getCode());
            securityDepositTemplate.setCurrency(currency);
        } else {
        	securityDepositTemplate.setCurrency(null);
        }

        securityDepositTemplate.setAllowValidityDate(resource.getAllowValidityDate());
        securityDepositTemplate.setAllowValidityPeriod(resource.getAllowValidityPeriod());
        securityDepositTemplate.setMinAmount(resource.getMinAmount());
        securityDepositTemplate.setMaxAmount(resource.getMaxAmount());
        securityDepositTemplate.setStatus(resource.getStatus());
        securityDepositTemplate.setNumberOfInstantiation(resource.getNumberOfInstantiation() == null ?  0 : resource.getNumberOfInstantiation());
        return securityDepositTemplate;
    }

    private ImmutableResource buildById(BaseEntity entity) {
		return entity != null ? ImmutableResource.builder().id(entity.getId()).build() : null;
	}
}
