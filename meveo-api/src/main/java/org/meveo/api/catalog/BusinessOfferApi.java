package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessOfferApi extends BaseApi {

	@Inject
	private OfferTemplateApi offerTemplateApi;

	@Inject
	private ServiceTemplateApi serviceTemplateApi;

	public void createOfferFromBOM(BomOfferDto postData, User currentUser) throws MeveoApiException {
		validate(postData);

		// create counters

		// create charges

		// create services
		if (postData.getServicesToActivate() != null) {
			for (String serviceCode : postData.getServicesToActivate()) {
				ServiceTemplateDto serviceTemplateDto = new ServiceTemplateDto();
				serviceTemplateDto.setCode(postData.getServiceCodePrefix() + serviceCode);
				serviceTemplateDto.setDescription(postData.getServiceCodePrefix() + serviceCode);
				serviceTemplateApi.create(serviceTemplateDto, currentUser);
			}
		}

		// create offers
		OfferTemplateDto offerTemplateDto = new OfferTemplateDto();
		offerTemplateDto.setCode(postData.getOfferCode());
		offerTemplateDto.setDescription(postData.getOfferCode());
		offerTemplateApi.create(offerTemplateDto, currentUser);

		// create script
	}

}
