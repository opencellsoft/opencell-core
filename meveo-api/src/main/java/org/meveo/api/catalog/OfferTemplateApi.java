package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.OfferServiceTemplateDto;
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
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.meveo.service.catalog.impl.OfferServiceTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
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

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	public void create(OfferTemplateDto postData, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		Provider provider = currentUser.getProvider();

		if (offerTemplateService.findByCode(postData.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(OfferTemplate.class, postData.getCode());
		}

		BusinessOfferModel businessOffer = null;
		if (!StringUtils.isBlank(postData.getBomCode())) {
			businessOffer = businessOfferService.findByCode(postData.getBomCode(), currentUser.getProvider());
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

		OfferTemplateCategory offerTemplateCategory = null;
		String categoryCode = postData.getOfferTemplateCategoryCode();
		if (!StringUtils.isBlank(categoryCode)) {
			offerTemplateCategory = offerTemplateCategoryService.findByCode(categoryCode, currentUser.getProvider());
			if (offerTemplateCategory == null) {
				throw new EntityDoesNotExistsException(OfferTemplateCategory.class, categoryCode);
			}
			offerTemplate.setOfferTemplateCategory(offerTemplateCategory);
		}

		offerTemplateService.create(offerTemplate, currentUser, provider);

		// check service templates
		if (postData.getOfferServiceTemplates() != null && postData.getOfferServiceTemplates().size() > 0) {
			List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
			for (OfferServiceTemplateDto offerServiceTemplateDto : postData.getOfferServiceTemplates()) {
				ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
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

				if (offerServiceTemplateDto.getIncompatibleServices() != null) {
					List<ServiceTemplate> incompatibleServices = new ArrayList<>();
					for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
						ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode(),
								provider);
						if (incompatibleService == null) {
							throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
						}
						incompatibleServices.add(incompatibleService);
					}
					offerServiceTemplate.setIncompatibleServices(incompatibleServices);
				}

				offerServiceTemplateService.create(offerServiceTemplate, currentUser);
				offerServiceTemplates.add(offerServiceTemplate);
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
	}

	public void update(OfferTemplateDto postData, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		Provider provider = currentUser.getProvider();

		OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), provider);
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
		}

		BusinessOfferModel businessOffer = null;
		if (!StringUtils.isBlank(postData.getBomCode())) {
			businessOffer = businessOfferService.findByCode(postData.getBomCode(), currentUser.getProvider());
			if (businessOffer == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
			}
			offerTemplate.setBusinessOfferModel(businessOffer);
		}

		offerTemplate.setBusinessOfferModel(businessOffer);
		offerTemplate.setDescription(postData.getDescription());
		offerTemplate.setDisabled(postData.isDisabled());

		OfferTemplateCategory offerTemplateCategory = null;
		String categoryCode = postData.getOfferTemplateCategoryCode();
		if (!StringUtils.isBlank(categoryCode)) {
			offerTemplateCategory = offerTemplateCategoryService.findByCode(categoryCode, currentUser.getProvider());
			if (offerTemplateCategory == null) {
				throw new EntityDoesNotExistsException(OfferTemplateCategory.class, categoryCode);
			}
			offerTemplate.setOfferTemplateCategory(offerTemplateCategory);
		}

		// check service templates
		// check service templates
		if (postData.getOfferServiceTemplates() != null && postData.getOfferServiceTemplates().size() > 0) {
			if (offerTemplate.getOfferServiceTemplates() != null) {
				List<OfferServiceTemplate> toBeDeleted = new ArrayList<>();
				List<OfferServiceTemplateDto> toBeAdded = new ArrayList<>();

				for (OfferServiceTemplateDto offerServiceTemplateDto : postData.getOfferServiceTemplates()) {
					boolean found = false;

					// check if already exists
					for (OfferServiceTemplate offerServiceTemplate : offerTemplate.getOfferServiceTemplates()) {
						if (offerServiceTemplate.getServiceTemplate().getCode()
								.equals(offerServiceTemplateDto.getServiceTemplate().getCode())) {
							found = true;
							break;
						}
					}

					if (!found) {
						toBeAdded.add(offerServiceTemplateDto);
					}
				}

				// check if it doesn't exists
				for (OfferServiceTemplate offerServiceTemplate : offerTemplate.getOfferServiceTemplates()) {
					boolean found = false;

					for (OfferServiceTemplateDto offerServiceTemplateDto : postData.getOfferServiceTemplates()) {
						if (offerServiceTemplate.getServiceTemplate().getCode()
								.equals(offerServiceTemplateDto.getServiceTemplate().getCode())) {
							found = true;
							break;
						}
					}

					if (!found) {
						toBeDeleted.add(offerServiceTemplate);
					}
				}

				// update incompatible services
				for (OfferServiceTemplateDto offerServiceTemplateDto : postData.getOfferServiceTemplates()) {
					// check if already exists
					for (OfferServiceTemplate offerServiceTemplate : offerTemplate.getOfferServiceTemplates()) {
						if (offerServiceTemplate.getServiceTemplate().getCode()
								.equals(offerServiceTemplateDto.getServiceTemplate().getCode())) {
							if (offerServiceTemplateDto.getIncompatibleServices() != null
									&& offerServiceTemplateDto.getIncompatibleServices().size() > 0) {
								offerServiceTemplate.getIncompatibleServices().clear();
								for (ServiceTemplateDto serviceTemplateDto : offerServiceTemplateDto
										.getIncompatibleServices()) {
									ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(
											serviceTemplateDto.getCode(), provider);
									if (serviceTemplate == null) {
										throw new EntityDoesNotExistsException(ServiceTemplate.class,
												serviceTemplateDto.getCode());
									}
									offerServiceTemplate.getIncompatibleServices().add(serviceTemplate);
								}
							}
						}
					}
				}

				if (toBeDeleted.size() > 0) {
					for (OfferServiceTemplate offerServiceTemplate : toBeDeleted) {
						offerServiceTemplateService.remove(offerServiceTemplate);
					}
				}

				if (toBeAdded.size() > 0) {
					List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
					for (OfferServiceTemplateDto offerServiceTemplateDto : toBeAdded) {
						ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
						ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(
								serviceTemplateDto.getCode(), provider);
						if (serviceTemplate == null) {
							throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
						}

						OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
						offerServiceTemplate.setMandatory(serviceTemplateDto.isMandatory());
						offerServiceTemplate.setOfferTemplate(offerTemplate);
						offerServiceTemplate.setServiceTemplate(serviceTemplate);
						offerServiceTemplate.setProvider(currentUser.getProvider());

						if (offerServiceTemplateDto.getIncompatibleServices() != null) {
							List<ServiceTemplate> incompatibleServices = new ArrayList<>();
							for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
								ServiceTemplate incompatibleService = serviceTemplateService.findByCode(
										stDto.getCode(), provider);
								if (incompatibleService == null) {
									throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
								}
								incompatibleServices.add(incompatibleService);
							}
							offerServiceTemplate.setIncompatibleServices(incompatibleServices);
						}

						offerServiceTemplateService.create(offerServiceTemplate, currentUser);
						offerServiceTemplates.add(offerServiceTemplate);
					}

					if (offerServiceTemplates.size() > 0) {
						offerTemplate.setOfferServiceTemplates(offerServiceTemplates);
					}
					offerTemplateService.update(offerTemplate, currentUser);
				}
			} else {
				List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
				for (OfferServiceTemplateDto offerServiceTemplateDto : postData.getOfferServiceTemplates()) {
					ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
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

					if (offerServiceTemplateDto.getIncompatibleServices() != null) {
						List<ServiceTemplate> incompatibleServices = new ArrayList<>();
						for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
							ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode(),
									provider);
							if (incompatibleService == null) {
								throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
							}
							incompatibleServices.add(incompatibleService);
						}
						offerServiceTemplate.setIncompatibleServices(incompatibleServices);
					}

					offerServiceTemplateService.create(offerServiceTemplate, currentUser);
					offerServiceTemplates.add(offerServiceTemplate);
				}

				if (offerServiceTemplates.size() > 0) {
					offerTemplate.setOfferServiceTemplates(offerServiceTemplates);
				}
				offerTemplateService.update(offerTemplate, currentUser);
			}
		}

		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), offerTemplate, false, currentUser);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw new MeveoApiException("Failed to associate custom field instance to an entity");
		}
	}

	public OfferTemplateDto find(String code, Provider provider) throws MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("offerTemplateCode");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(code, provider);
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, code);
		}

		return new OfferTemplateDto(offerTemplate, customFieldInstanceService.getCustomFieldInstances(offerTemplate));

	}

	public void remove(String code, Provider provider) throws MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("offerTemplateCode");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(code, provider);
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, code);
		}

		offerTemplateService.remove(offerTemplate);
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