package org.meveo.api.catalog;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.meveo.service.script.offer.OfferScriptService;

@Stateless
public class BusinessOfferApi extends BaseApi {

	@Inject
	private BusinessOfferService businessOfferService;

	@Inject
	private OfferScriptService offerScriptService;

	public void createOfferFromBOM(BomOfferDto postData, User currentUser) throws MeveoApiException {
		validate(postData);
		if (!StringUtils.isBlank(postData.getBomCode())) {
			// find bom
			BusinessOfferModel businessOfferModel = businessOfferService.findByCode(postData.getBomCode(),
					currentUser.getProvider());
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
				newOfferTemplate = businessOfferService.createOfferFromBOM(businessOfferModel, postData.getPrefix(),
						currentUser);
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			// populate offer custom fields
			if (newOfferTemplate != null && postData.getOfferCustomFields() != null) {
				try {
					populateCustomFields(postData.getOfferCustomFields(), newOfferTemplate, true, currentUser);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new MeveoApiException(e.getMessage());
				}
			}

			if (businessOfferModel.getScript() != null) {
				Map<String, Object> scriptContext = new HashMap<String, Object>();
				scriptContext.put("event", newOfferTemplate);
				try {
					offerScriptService.onCreated(businessOfferModel.getScript().getCode(), scriptContext, currentUser,
							currentUser.getProvider());
				} catch (BusinessException e) {
					throw new MeveoApiException(e.getMessage());
				}
			}
		} else {
			if (StringUtils.isBlank(postData.getBomCode())) {
				missingParameters.add("bomCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
