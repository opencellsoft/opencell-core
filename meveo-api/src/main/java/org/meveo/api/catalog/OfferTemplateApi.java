package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.OfferProductTemplateDto;
import org.meveo.api.dto.catalog.OfferServiceTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferTemplateApi extends BaseCrudApi<OfferTemplate, OfferTemplateDto> {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private BusinessOfferModelService businessOfferModelService;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Inject
	private ProductTemplateService productTemplateService;
	
	public OfferTemplate create(OfferTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
			handleMissingParameters();
		}

		Provider provider = currentUser.getProvider();

		if (offerTemplateService.findByCode(postData.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(OfferTemplate.class, postData.getCode());
		}

		BusinessOfferModel businessOffer = null;
		if (!StringUtils.isBlank(postData.getBomCode())) {
			businessOffer = businessOfferModelService.findByCode(postData.getBomCode(), currentUser.getProvider());
			if (businessOffer == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
			}
		}

		OfferTemplate offerTemplate = new OfferTemplate();
		offerTemplate.setBusinessOfferModel(businessOffer);
		offerTemplate.setProvider(provider);
		offerTemplate.setCode(postData.getCode());
		offerTemplate.setDescription(postData.getDescription());
		offerTemplate.setName(postData.getName());
		offerTemplate.setLongDescription(postData.getLongDescription());
		offerTemplate.setDisabled(postData.isDisabled());
		 
		if (!StringUtils.isBlank(postData.getOfferTemplateCategoryCode())) {
			OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postData.getOfferTemplateCategoryCode(), currentUser.getProvider());
			if (offerTemplateCategory == null) {
				throw new EntityDoesNotExistsException(OfferTemplateCategory.class, postData.getOfferTemplateCategoryCode());
			}
			offerTemplate.getOfferTemplateCategories().add(offerTemplateCategory);
		}
		
		if(postData.getOfferTemplateCategories() != null) {
			for(String categoryCode : postData.getOfferTemplateCategories()) {				
				OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(categoryCode, currentUser.getProvider());
				if (offerTemplateCategory == null) {
					throw new EntityDoesNotExistsException(OfferTemplateCategory.class, categoryCode);
				}
				offerTemplate.getOfferTemplateCategories().add(offerTemplateCategory);
			}
		}

		offerTemplateService.create(offerTemplate, currentUser);

		// check service templates
		if (postData.getOfferServiceTemplates() != null && postData.getOfferServiceTemplates().size() > 0) {
			List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
			for (OfferServiceTemplateDto offerServiceTemplateDto : postData.getOfferServiceTemplates()) {
				ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
				ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(), provider);
				if (serviceTemplate == null) {
					throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
				}

				OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
				if (offerServiceTemplateDto.getMandatory() == null) {
					offerServiceTemplate.setMandatory(serviceTemplateDto.isMandatory());
				} else {
					offerServiceTemplate.setMandatory(offerServiceTemplateDto.getMandatory());
				}
				offerServiceTemplate.setOfferTemplate(offerTemplate);
				offerServiceTemplate.setServiceTemplate(serviceTemplate);
				offerServiceTemplate.setProvider(currentUser.getProvider());

				if (offerServiceTemplateDto.getIncompatibleServices() != null) {
					List<ServiceTemplate> incompatibleServices = new ArrayList<>();
					for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
						ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode(), provider);
						if (incompatibleService == null) {
							throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
						}
						incompatibleServices.add(incompatibleService);
					}
					offerServiceTemplate.setIncompatibleServices(incompatibleServices);
				}

				offerServiceTemplates.add(offerServiceTemplate);
			}
			if (offerServiceTemplates.size() > 0) {
				offerTemplate.setOfferServiceTemplates(offerServiceTemplates);
			}
			offerTemplateService.update(offerTemplate, currentUser);
		}

		// check offer product templates
		processOfferProductTemplates(postData, offerTemplate, currentUser);
		offerTemplateService.update(offerTemplate, currentUser);

		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), offerTemplate, true, currentUser);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
		return offerTemplate;
	}

	public OfferTemplate update(OfferTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
			handleMissingParameters();
		}

		Provider provider = currentUser.getProvider();

		OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), provider);
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
		}

		BusinessOfferModel businessOffer = null;
		if (!StringUtils.isBlank(postData.getBomCode())) {
			businessOffer = businessOfferModelService.findByCode(postData.getBomCode(), currentUser.getProvider());
			if (businessOffer == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
			}
			offerTemplate.setBusinessOfferModel(businessOffer);
		}

		offerTemplate.setBusinessOfferModel(businessOffer);
		offerTemplate.setDescription(postData.getDescription());
		offerTemplate.setName(postData.getName());
		offerTemplate.setLongDescription(postData.getLongDescription());
		offerTemplate.setDisabled(postData.isDisabled());

		if (!StringUtils.isBlank(postData.getOfferTemplateCategoryCode())) {
			offerTemplate.getOfferTemplateCategories().clear();
			OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postData.getOfferTemplateCategoryCode(), currentUser.getProvider());
			if (offerTemplateCategory == null) {
				throw new EntityDoesNotExistsException(OfferTemplateCategory.class, postData.getOfferTemplateCategoryCode());
			}
			offerTemplate.getOfferTemplateCategories().add(offerTemplateCategory);
		}
		
		if(postData.getOfferTemplateCategories() != null) {
			offerTemplate.getOfferTemplateCategories().clear();
			for(String categoryCode : postData.getOfferTemplateCategories()) {				
				OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(categoryCode, currentUser.getProvider());
				if (offerTemplateCategory == null) {
					throw new EntityDoesNotExistsException(OfferTemplateCategory.class, categoryCode);
				}
				offerTemplate.getOfferTemplateCategories().add(offerTemplateCategory);
			}
		}

		// check service templates
		if (postData.getOfferServiceTemplates() != null && postData.getOfferServiceTemplates().size() > 0) {
			if (offerTemplate.getOfferServiceTemplates() != null) {
				List<OfferServiceTemplate> toBeDeleted = new ArrayList<>();
				List<OfferServiceTemplateDto> toBeAdded = new ArrayList<>();

				for (OfferServiceTemplateDto offerServiceTemplateDto : postData.getOfferServiceTemplates()) {
					boolean found = false;

					// check if already exists
					for (OfferServiceTemplate offerServiceTemplate : offerTemplate.getOfferServiceTemplates()) {
						if (offerServiceTemplate.getServiceTemplate().getCode().equals(offerServiceTemplateDto.getServiceTemplate().getCode())) {
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
						if (offerServiceTemplate.getServiceTemplate().getCode().equals(offerServiceTemplateDto.getServiceTemplate().getCode())) {
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
						if (offerServiceTemplate.getServiceTemplate().getCode().equals(offerServiceTemplateDto.getServiceTemplate().getCode())) {
							if (offerServiceTemplateDto.getIncompatibleServices() != null && offerServiceTemplateDto.getIncompatibleServices().size() > 0) {
								offerServiceTemplate.getIncompatibleServices().clear();
								for (ServiceTemplateDto serviceTemplateDto : offerServiceTemplateDto.getIncompatibleServices()) {
									ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(), provider);
									if (serviceTemplate == null) {
										throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
									}
									offerServiceTemplate.getIncompatibleServices().add(serviceTemplate);
								}
							}
						}
					}
				}

				if (toBeDeleted.size() > 0) {
					for (OfferServiceTemplate offerServiceTemplate : toBeDeleted) {
						offerTemplate.getOfferServiceTemplates().remove(offerServiceTemplate);
					}
				}

				if (toBeAdded.size() > 0) {
					for (OfferServiceTemplateDto offerServiceTemplateDto : toBeAdded) {
						ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
						ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(), provider);
						if (serviceTemplate == null) {
							throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
						}

						OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
						if (offerServiceTemplateDto.getMandatory() == null) {
							offerServiceTemplate.setMandatory(serviceTemplateDto.isMandatory());
						} else {
							offerServiceTemplate.setMandatory(offerServiceTemplateDto.getMandatory());
						}
						offerServiceTemplate.setOfferTemplate(offerTemplate);
						offerServiceTemplate.setServiceTemplate(serviceTemplate);
						offerServiceTemplate.setProvider(currentUser.getProvider());

						if (offerServiceTemplateDto.getIncompatibleServices() != null) {
							List<ServiceTemplate> incompatibleServices = new ArrayList<>();
							for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
								ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode(), provider);
								if (incompatibleService == null) {
									throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
								}
								incompatibleServices.add(incompatibleService);
							}
							offerServiceTemplate.setIncompatibleServices(incompatibleServices);
						}

						offerTemplate.getOfferServiceTemplates().add(offerServiceTemplate);
					}

				}
			} else {
				List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
				for (OfferServiceTemplateDto offerServiceTemplateDto : postData.getOfferServiceTemplates()) {
					ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
					ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(), provider);
					if (serviceTemplate == null) {
						throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
					}

					OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
					if (offerServiceTemplateDto.getMandatory() == null) {
						offerServiceTemplate.setMandatory(serviceTemplateDto.isMandatory());
					} else {
						offerServiceTemplate.setMandatory(offerServiceTemplateDto.getMandatory());
					}
					offerServiceTemplate.setOfferTemplate(offerTemplate);
					offerServiceTemplate.setServiceTemplate(serviceTemplate);
					offerServiceTemplate.setProvider(currentUser.getProvider());

					if (offerServiceTemplateDto.getIncompatibleServices() != null) {
						List<ServiceTemplate> incompatibleServices = new ArrayList<>();
						for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
							ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode(), provider);
							if (incompatibleService == null) {
								throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
							}
							incompatibleServices.add(incompatibleService);
						}
						offerServiceTemplate.setIncompatibleServices(incompatibleServices);
					}

					offerServiceTemplates.add(offerServiceTemplate);
				}

				if (offerServiceTemplates.size() > 0) {
					offerTemplate.setOfferServiceTemplates(offerServiceTemplates);
				}

			}
		}

		offerTemplate = offerTemplateService.update(offerTemplate, currentUser);

		// check offer product templates
		processOfferProductTemplates(postData, offerTemplate, currentUser);
		offerTemplate = offerTemplateService.update(offerTemplate, currentUser);

		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), offerTemplate, false, currentUser);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
		
		return offerTemplate;
	}

	private void processOfferProductTemplates(OfferTemplateDto postData, OfferTemplate offerTemplate, User currentUser) throws MeveoApiException, BusinessException {
		List<OfferProductTemplateDto> offerProductTemplateDtos = postData.getOfferProductTemplates();
		boolean hasOfferProductTemplateDtos = offerProductTemplateDtos != null && !offerProductTemplateDtos.isEmpty();
		List<OfferProductTemplate> existingProductTemplates = offerTemplate.getOfferProductTemplates();
		boolean hasExistingProductTemplates = existingProductTemplates != null && !existingProductTemplates.isEmpty();
		if (hasOfferProductTemplateDtos) {
			List<OfferProductTemplate> newOfferProductTemplates = new ArrayList<>();
			OfferProductTemplate offerProductTemplate = null;
			for (OfferProductTemplateDto offerProductTemplateDto : offerProductTemplateDtos) {
				offerProductTemplate = getOfferProductTemplatesFromDto(offerProductTemplateDto, currentUser);
				offerProductTemplate.setOfferTemplate(offerTemplate);
				newOfferProductTemplates.add(offerProductTemplate);
			}
			if (hasExistingProductTemplates) {
				List<OfferProductTemplate> offerProductTemplatesForRemoval = new ArrayList<>(existingProductTemplates);
				offerProductTemplatesForRemoval.removeAll(newOfferProductTemplates);
				newOfferProductTemplates.removeAll(new ArrayList<>(existingProductTemplates));
				// for (OfferProductTemplate offerProductTemplateForRemoval :
				// offerProductTemplatesForRemoval) {
				// offerProductTemplateService.remove(offerProductTemplateForRemoval);
				// }
				offerTemplate.getOfferProductTemplates().removeAll(new ArrayList<>(offerProductTemplatesForRemoval));
			}
			// for (OfferProductTemplate newOfferProductTemplate :
			// newOfferProductTemplates) {
			// newOfferProductTemplate.setOfferTemplate(offerTemplate);
			// offerProductTemplateService.create(newOfferProductTemplate,
			// currentUser);
			// }
			offerTemplate.getOfferProductTemplates().addAll(newOfferProductTemplates);
		} else if (hasExistingProductTemplates) {
			// for (OfferProductTemplate offerProductTemplateForRemoval :
			// existingProductTemplates) {
			// offerProductTemplateService.remove(offerProductTemplateForRemoval);
			// }
			offerTemplate.getOfferProductTemplates().removeAll(existingProductTemplates);
		}
	}

	private OfferProductTemplate getOfferProductTemplatesFromDto(OfferProductTemplateDto offerProductTemplateDto, User currentUser) throws MeveoApiException, BusinessException {

		ProductTemplateDto productTemplateDto = offerProductTemplateDto.getProductTemplate();
		ProductTemplate productTemplate = null;
		if (productTemplateDto != null) {
			productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), currentUser.getProvider());
			if (productTemplate == null) {
				throw new MeveoApiException(String.format("ProductTemplate[code = %s]does not exist.", productTemplateDto.getCode()));
			}
		}

		OfferProductTemplate offerProductTemplate = new OfferProductTemplate();
		Boolean mandatory = offerProductTemplateDto.getMandatory();
		mandatory = mandatory == null ? false : mandatory;

		offerProductTemplate.setProductTemplate(productTemplate);
		offerProductTemplate.setMandatory(mandatory);

		return offerProductTemplate;
	}

	public OfferTemplateDto find(String code, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("offerTemplateCode");
			handleMissingParameters();
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(code, currentUser.getProvider());
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, code);
		}

		OfferTemplateDto offerTemplateDto = new OfferTemplateDto(offerTemplate, entityToDtoConverter.getCustomFieldsDTO(offerTemplate));

		List<OfferProductTemplate> childOfferProductTemplates = offerTemplate.getOfferProductTemplates();
		if (childOfferProductTemplates != null && !childOfferProductTemplates.isEmpty()) {
			List<OfferProductTemplateDto> offerProductTemplates = new ArrayList<>();
			OfferProductTemplateDto offerProductTemplateDto = null;
			ProductTemplateDto productTemplateDto = null;
			ProductTemplate productTemplate = null;
			for (OfferProductTemplate offerProductTemplate : childOfferProductTemplates) {
				productTemplate = offerProductTemplate.getProductTemplate();
				offerProductTemplateDto = new OfferProductTemplateDto();
				offerProductTemplateDto.setMandatory(offerProductTemplate.isMandatory());
				if (productTemplate != null) {
					productTemplateDto = new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate));
					offerProductTemplateDto.setProductTemplate(productTemplateDto);
				}
				offerProductTemplates.add(offerProductTemplateDto);
			}
			offerTemplateDto.setOfferProductTemplates(offerProductTemplates);
		}

		return offerTemplateDto;

	}

	public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("offerTemplateCode");
			handleMissingParameters();
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(code, currentUser.getProvider());
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, code);
		}

		offerTemplateService.remove(offerTemplate, currentUser);
	}

	/**
	 * Create or updates the OfferTemplate based on code
	 *
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public OfferTemplate createOrUpdate(OfferTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
		OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), currentUser.getProvider());

		if (offerTemplate == null) {
			return create(postData, currentUser);
		} else {
			return update(postData, currentUser);
		}
	}
}