package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

@Stateless
public class BusinessOfferApi extends BaseApi {

	@Inject
	private BusinessOfferModelService businessOfferModelService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private OfferTemplateService offerTemplateService;

	public void createOfferFromBOM(BomOfferDto postData, User currentUser) throws MeveoApiException {
		validate(postData);
		if (!StringUtils.isBlank(postData.getBomCode())) {
			// find bom
			BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(postData.getBomCode(), currentUser.getProvider());
			if (businessOfferModel == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
			}

			// get the offer from bom
			OfferTemplate bomOffer = businessOfferModel.getOfferTemplate();
			if (bomOffer == null) {
				throw new MeveoApiException("NO_OFFER_TEMPLATE_ATTACHED");
			}

			if (bomOffer.getOfferServiceTemplates() == null || bomOffer.getOfferServiceTemplates().size() == 0) {
				throw new MeveoApiException("NO_SERVICE_TEMPLATES_ATTACHED");
			}

			OfferTemplate newOfferTemplate = null;
			try {
				newOfferTemplate = businessOfferModelService.createOfferFromBOM(businessOfferModel, postData.getPrefix(), postData.getServiceCodes(), currentUser);
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			// populate service custom fields
			if (postData.getServiceCustomFields() != null) {
				for (OfferServiceTemplate ost : newOfferTemplate.getOfferServiceTemplates()) {
					ServiceTemplate serviceTemplate = ost.getServiceTemplate();
					try {
						populateCustomFields(postData.getServiceCustomFields(), serviceTemplate, true, currentUser);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new MeveoApiException(e.getMessage());
					}
					try {
						serviceTemplateService.update(serviceTemplate, currentUser);
					} catch (BusinessException e) {
						throw new MeveoApiException(e.getMessage());
					}
				}
			}

			// populate offer custom fields
			if (newOfferTemplate != null && postData.getOfferCustomFields() != null) {
				try {
					populateCustomFields(postData.getOfferCustomFields(), newOfferTemplate, true, currentUser);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new MeveoApiException(e.getMessage());
				}
				try {
					offerTemplateService.update(newOfferTemplate, currentUser);
				} catch (BusinessException e) {
					throw new MeveoApiException(e.getMessage());
				}
			}
		} else {
			if (StringUtils.isBlank(postData.getBomCode())) {
				missingParameters.add("bomCode");
			}

			handleMissingParameters();
		}
	}
}
