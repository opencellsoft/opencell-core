package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.catalog.impl.BusinessOfferModelService;

@Stateless
public class BusinessOfferApi extends BaseApi {

	@Inject
	private BusinessOfferModelService businessOfferModelService;

	public Long createOfferFromBOM(BomOfferDto postData, User currentUser) throws MeveoApiException {


		if (StringUtils.isBlank(postData.getBomCode())) {
			missingParameters.add("bomCode");
		}

		handleMissingParameters();

	    validate(postData);
	      
		// find bom
		BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(postData.getBomCode(), currentUser.getProvider());
		if (businessOfferModel == null) {
			throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
		}

		// get the offer from bom
		OfferTemplate bomOffer = businessOfferModel.getOfferTemplate();
		if (bomOffer == null) {
			throw new MeveoApiException("No offer template attached");
		}

		if (bomOffer.getOfferServiceTemplates() == null || bomOffer.getOfferServiceTemplates().size() == 0) {
			throw new MeveoApiException("No service template attached");
		}

		OfferTemplate newOfferTemplate = null;
		try {
			newOfferTemplate = businessOfferModelService.createOfferFromBOM(businessOfferModel, postData.getCustomFields(), postData.getCode(), postData.getName(),
					postData.getDescription(), postData.getServicesToActivate(), currentUser);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}

		// populate service custom fields
		for (OfferServiceTemplate ost : newOfferTemplate.getOfferServiceTemplates()) {
			ServiceTemplate serviceTemplate = ost.getServiceTemplate();

			for (ServiceConfigurationDto serviceCodeDto : postData.getServicesToActivate()) {
				String serviceCode = postData.getPrefix() + "_" + serviceCodeDto.getCode();
				if (serviceCode.equals(serviceTemplate.getCode())) {
					if (serviceCodeDto.getCustomFields() != null) {
						try {
							CustomFieldsDto cfsDto = new CustomFieldsDto();
							cfsDto.setCustomField(serviceCodeDto.getCustomFields());
							populateCustomFields(cfsDto, serviceTemplate, true, currentUser);
						} catch (Exception e) {
						    log.error("Failed to associate custom field instance to an entity", e);
							throw e;
						}
						break;
					}
				}
			}
		}

		// populate offer custom fields
		if (newOfferTemplate != null && postData.getCustomFields() != null) {
			try {
				CustomFieldsDto cfsDto = new CustomFieldsDto();
				cfsDto.setCustomField(postData.getCustomFields());
				populateCustomFields(cfsDto, newOfferTemplate, true, currentUser);
			} catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw e;
			}
		}
		
		return newOfferTemplate.getId();
	}
}
