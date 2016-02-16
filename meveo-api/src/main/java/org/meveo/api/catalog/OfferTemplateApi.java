package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.meveo.service.catalog.impl.OfferServiceTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferTemplateApi extends BaseApi {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private OfferServiceTemplateService offerServiceTemplateService;
	
	@Inject
	private BusinessOfferService businessOfferService;
	
	public void create(OfferTemplateDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			if (offerTemplateService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(OfferTemplate.class, postData.getCode());
			}
			
			BusinessOfferModel businessOffer = null;
			if (!StringUtils.isBlank(postData.getBomCode())) {
				businessOffer = businessOfferService.findByCode(postData.getBomCode(),
						currentUser.getProvider());
				if (businessOffer == null) {
					throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
				}
			}

			OfferTemplate offerTemplate = new OfferTemplate();
			offerTemplate.setBusinessOfferModel(businessOffer);
			offerTemplate.setProvider(provider);
			offerTemplate.setCode(postData.getCode());
			offerTemplate.setDescription(postData.getDescription());
			offerTemplate.setDisabled(postData.isDisabled());

			offerTemplateService.create(offerTemplate, currentUser, provider);

			// check service templates
			if (postData.getServiceTemplates() != null
					&& postData.getServiceTemplates().getServiceTemplate().size() > 0) {
				List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
				for (ServiceTemplateDto serviceTemplateDto : postData.getServiceTemplates().getServiceTemplate()) {
					ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(),
							provider);
					if (serviceTemplate == null) {
						throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
					}

					if (offerTemplate.getOfferServiceTemplates() != null) {
						// check if exists
						boolean found = false;
						for (OfferServiceTemplate offerServiceTemplate : offerTemplate.getOfferServiceTemplates()) {
							if (offerServiceTemplate.getServiceTemplate().equals(serviceTemplate)) {
								found = true;
								break;
							}
						}
						if (!found) {
							OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
							offerServiceTemplate.setMandatory(serviceTemplateDto.isMandatory());
							offerServiceTemplate.setOfferTemplate(offerTemplate);
							offerServiceTemplate.setServiceTemplate(serviceTemplate);
							offerServiceTemplate.setProvider(currentUser.getProvider());
							offerServiceTemplateService.create(offerServiceTemplate, currentUser);
							offerServiceTemplates.add(offerServiceTemplate);
						}
					}
				}
				if (offerServiceTemplates.size() > 0) {
					offerTemplate.setOfferServiceTemplates(offerServiceTemplates);
				}
				offerTemplateService.update(offerTemplate, currentUser);
			}

			// populate customFields
			try {
				populateCustomFields(postData.getCustomFields(), offerTemplate, true, currentUser);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("Failed to associate custom field instance to an entity", e);
				throw new MeveoApiException("Failed to associate custom field instance to an entity");
			}

		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(OfferTemplateDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
			}
			
			BusinessOfferModel businessOffer = null;
			if (!StringUtils.isBlank(postData.getBomCode())) {
				businessOffer = businessOfferService.findByCode(postData.getBomCode(),
						currentUser.getProvider());
				if (businessOffer == null) {
					throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
				}
				offerTemplate.setBusinessOfferModel(businessOffer);
			}
			
			offerTemplate.setBusinessOfferModel(businessOffer);
			offerTemplate.setDescription(postData.getDescription());
			offerTemplate.setDisabled(postData.isDisabled());

			// check service templates
			if (postData.getServiceTemplates() != null
					&& postData.getServiceTemplates().getServiceTemplate().size() > 0) {
				List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
				for (ServiceTemplateDto serviceTemplateDto : postData.getServiceTemplates().getServiceTemplate()) {
					ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(),
							provider);
					if (serviceTemplate == null) {
						throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
					}

					OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
					offerServiceTemplate.setMandatory(serviceTemplateDto.isMandatory());
					offerServiceTemplate.setOfferTemplate(offerTemplate);
					offerServiceTemplate.setServiceTemplate(serviceTemplate);
					offerServiceTemplate.setProvider(currentUser.getProvider());
					offerServiceTemplateService.create(offerServiceTemplate, currentUser);

					offerServiceTemplates.add(offerServiceTemplate);
				}
				offerTemplate.setOfferServiceTemplates(offerServiceTemplates);
				offerTemplateService.update(offerTemplate, currentUser);
			}

			// populate customFields
			try {
				populateCustomFields(postData.getCustomFields(), offerTemplate, false, currentUser);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("Failed to associate custom field instance to an entity", e);
				throw new MeveoApiException("Failed to associate custom field instance to an entity");
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public OfferTemplateDto find(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			OfferTemplate offerTemplate = offerTemplateService.findByCode(code, provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, code);
			}

			return new OfferTemplateDto(offerTemplate,
					customFieldInstanceService.getCustomFieldInstances(offerTemplate));
		} else {
			missingParameters.add("offerTemplateCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			OfferTemplate offerTemplate = offerTemplateService.findByCode(code, provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, code);
			}

			offerTemplateService.remove(offerTemplate);
		} else {
			missingParameters.add("offerTemplateCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	/**
	 * Create or updates the OfferTemplate based on code
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(OfferTemplateDto postData, User currentUser) throws MeveoApiException {
		OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), currentUser.getProvider());

		if (offerTemplate == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}

}
