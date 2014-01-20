package org.meveo.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.OneShotChargeTemplateDto;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OneShotChargeTemplateServiceApi extends BaseApi {

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	public List<OneShotChargeTemplateDto> getOneShotChargeTemplates(
			String languageCode, String countryCode, String currencyCode,
			String providerCode, String sellerCode, Date date) {
		Provider provider = providerService.findByCode(providerCode);

		List<OneShotChargeTemplate> oneShotChargeTemplates = oneShotChargeTemplateService
				.getSubscriptionChargeTemplates(provider);
		List<OneShotChargeTemplateDto> oneShotChargeDtos = new ArrayList<OneShotChargeTemplateDto>();
		for (OneShotChargeTemplate oneShotChargeTemplate : oneShotChargeTemplates) {
			OneShotChargeTemplateDto oneShotChargeDto = new OneShotChargeTemplateDto();
			oneShotChargeDto.setChargeCode(oneShotChargeTemplate.getCode());
			oneShotChargeDto.setDescription(oneShotChargeTemplate
					.getDescription());
			oneShotChargeDtos.add(oneShotChargeDto);
		}

		return oneShotChargeDtos;
	}

}
