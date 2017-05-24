package org.meveo.api.catalog;

import java.io.IOException;
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
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
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

	public OfferTemplate create(OfferTemplateDto postData) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getName())) {
		    postData.setName(postData.getCode());
		}
		handleMissingParameters();
		
		if (offerTemplateService.findByCode(postData.getCode()) != null) {
			throw new EntityAlreadyExistsException(OfferTemplate.class, postData.getCode());
		}		

		OfferTemplate offerTemplate = new OfferTemplate();
		populateFromDto(offerTemplate, postData);

		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), offerTemplate, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
		} catch (Exception e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw e;
		}

        offerTemplateService.create(offerTemplate);
        
		return offerTemplate;
	}

	public OfferTemplate update(OfferTemplateDto postData) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getName())) {
		    postData.setName(postData.getCode());
		}
		handleMissingParameters();
		
		
		OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode());
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
		}
		
		populateFromDto(offerTemplate, postData);
		offerTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode())?postData.getCode():postData.getUpdatedCode());
		
		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), offerTemplate, false);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
		} catch (Exception e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw e;
		}
		
		offerTemplate = offerTemplateService.update(offerTemplate);

		return offerTemplate;
	}
	
	private void populateFromDto(OfferTemplate offerTemplate, OfferTemplateDto postData) throws MeveoApiException, BusinessException {
		
	
		BusinessOfferModel businessOffer = null;
		if (!StringUtils.isBlank(postData.getBomCode())) {
			businessOffer = businessOfferModelService.findByCode(postData.getBomCode());
			if (businessOffer == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
			}
		}
		
		if (!StringUtils.isBlank(postData.getOfferTemplateCategoryCode())) {
			OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postData.getOfferTemplateCategoryCode());
			if (offerTemplateCategory == null) {
				throw new EntityDoesNotExistsException(OfferTemplateCategory.class, postData.getOfferTemplateCategoryCode());
			}
			offerTemplate.addOfferTemplateCategory(offerTemplateCategory);
		}
		
		if (postData.getOfferTemplateCategories() != null) {
			offerTemplate.getOfferTemplateCategories().clear();
			for (String categoryCode : postData.getOfferTemplateCategories()) {
				OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(categoryCode);
				if (offerTemplateCategory == null) {
					throw new EntityDoesNotExistsException(OfferTemplateCategory.class, categoryCode);
				}
				offerTemplate.addOfferTemplateCategory(offerTemplateCategory);
			}
		}
		
		offerTemplate.setBusinessOfferModel(businessOffer);
		offerTemplate.setCode(postData.getCode());
		offerTemplate.setDescription(postData.getDescription());
		offerTemplate.setName(postData.getName());
		offerTemplate.setLongDescription(postData.getLongDescription());
		offerTemplate.setDisabled(postData.isDisabled());
		offerTemplate.setValidFrom(postData.getValidFrom());
		offerTemplate.setValidTo(postData.getValidTo());
		
		try {
			saveImage(offerTemplate, postData.getImagePath(), postData.getImageBase64());
		} catch (IOException e1) {
			log.error("Invalid image data={}", e1.getMessage());
			throw new InvalidImageData();
		}

		// check service templates
		processOfferServiceTemplates(postData, offerTemplate);

		// check offer product templates
		processOfferProductTemplates(postData, offerTemplate);
	}

	private void processOfferServiceTemplates(OfferTemplateDto postData, OfferTemplate offerTemplate) throws MeveoApiException, BusinessException {
		List<OfferServiceTemplateDto> offerServiceTemplateDtos = postData.getOfferServiceTemplates();
		boolean hasOfferServiceTemplateDtos = offerServiceTemplateDtos != null && !offerServiceTemplateDtos.isEmpty();

		List<OfferServiceTemplate> existingServiceTemplates = offerTemplate.getOfferServiceTemplates();
		boolean hasExistingServiceTemplates = existingServiceTemplates != null && !existingServiceTemplates.isEmpty();

		if (hasOfferServiceTemplateDtos) {
			List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();
			OfferServiceTemplate offerServiceTemplate = null;
			for (OfferServiceTemplateDto offerServiceTemplateDto : offerServiceTemplateDtos) {
				offerServiceTemplate = getOfferServiceTemplatesFromDto(offerServiceTemplateDto);
				offerServiceTemplate.setOfferTemplate(offerTemplate);
				newOfferServiceTemplates.add(offerServiceTemplate);
			}

            if (!hasExistingServiceTemplates) {
                offerTemplate.getOfferServiceTemplates().addAll(newOfferServiceTemplates);

            } else {

                // Keep only services that repeat
                existingServiceTemplates.retainAll(newOfferServiceTemplates);

                // Update existing services or add new ones
                for (OfferServiceTemplate ostNew : newOfferServiceTemplates) {

                    int index = existingServiceTemplates.indexOf(ostNew);
                    if (index >= 0) {
                        OfferServiceTemplate ostOld = existingServiceTemplates.get(index);
                        ostOld.update(ostNew);

                    } else {
                        existingServiceTemplates.add(ostNew);
                    }
                }
			}
			
		} else if (hasExistingServiceTemplates) {
			offerTemplate.getOfferServiceTemplates().removeAll(existingServiceTemplates);
		}
	}

	private void processOfferProductTemplates(OfferTemplateDto postData, OfferTemplate offerTemplate) throws MeveoApiException, BusinessException {
		List<OfferProductTemplateDto> offerProductTemplateDtos = postData.getOfferProductTemplates();
		boolean hasOfferProductTemplateDtos = offerProductTemplateDtos != null && !offerProductTemplateDtos.isEmpty();
		List<OfferProductTemplate> existingProductTemplates = offerTemplate.getOfferProductTemplates();
		boolean hasExistingProductTemplates = existingProductTemplates != null && !existingProductTemplates.isEmpty();
		if (hasOfferProductTemplateDtos) {
			List<OfferProductTemplate> newOfferProductTemplates = new ArrayList<>();
			OfferProductTemplate offerProductTemplate = null;
			for (OfferProductTemplateDto offerProductTemplateDto : offerProductTemplateDtos) {
				offerProductTemplate = getOfferProductTemplatesFromDto(offerProductTemplateDto);
				offerProductTemplate.setOfferTemplate(offerTemplate);
				newOfferProductTemplates.add(offerProductTemplate);
			}

			if (hasExistingProductTemplates) {
				List<OfferProductTemplate> offerProductTemplatesForRemoval = new ArrayList<>(existingProductTemplates);
				offerProductTemplatesForRemoval.removeAll(newOfferProductTemplates);
				List<OfferProductTemplate> retainOfferProductTemplates = new ArrayList<>(newOfferProductTemplates);
				retainOfferProductTemplates.retainAll(existingProductTemplates);
				offerProductTemplatesForRemoval.addAll(retainOfferProductTemplates);
				newOfferProductTemplates.removeAll(new ArrayList<>(existingProductTemplates));
				offerTemplate.getOfferProductTemplates().removeAll(new ArrayList<>(offerProductTemplatesForRemoval));
				offerTemplate.getOfferProductTemplates().addAll(retainOfferProductTemplates);
			}

			offerTemplate.getOfferProductTemplates().addAll(newOfferProductTemplates);

		} else if (hasExistingProductTemplates) {
			offerTemplate.getOfferProductTemplates().removeAll(existingProductTemplates);
		}
	}

	private OfferServiceTemplate getOfferServiceTemplatesFromDto(OfferServiceTemplateDto offerServiceTemplateDto) throws MeveoApiException, BusinessException {

		ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
		ServiceTemplate serviceTemplate = null;
		if (serviceTemplateDto != null) {
			serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode());
			if (serviceTemplate == null) {
				throw new MeveoApiException(String.format("ServiceTemplatecode %s does not exist.", serviceTemplateDto.getCode()));
			}
		}

		OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
		Boolean mandatory = offerServiceTemplateDto.getMandatory();
		mandatory = mandatory == null ? false : mandatory;

		offerServiceTemplate.setServiceTemplate(serviceTemplate);
		offerServiceTemplate.setMandatory(mandatory);

		if (offerServiceTemplateDto.getIncompatibleServices() != null) {
			List<ServiceTemplate> incompatibleServices = new ArrayList<>();
			for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
				ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode());
				if (incompatibleService == null) {
					throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
				}
				incompatibleServices.add(incompatibleService);
			}
			offerServiceTemplate.setIncompatibleServices(incompatibleServices);
		}

		return offerServiceTemplate;
	}

	private OfferProductTemplate getOfferProductTemplatesFromDto(OfferProductTemplateDto offerProductTemplateDto) throws MeveoApiException, BusinessException {

		ProductTemplateDto productTemplateDto = offerProductTemplateDto.getProductTemplate();
		ProductTemplate productTemplate = null;
		if (productTemplateDto != null) {
			productTemplate = productTemplateService.findByCode(productTemplateDto.getCode());
			if (productTemplate == null) {
				throw new MeveoApiException(String.format("ProductTemplate %s does not exist.", productTemplateDto.getCode()));
			}
		}

		OfferProductTemplate offerProductTemplate = new OfferProductTemplate();
		Boolean mandatory = offerProductTemplateDto.getMandatory();
		mandatory = mandatory == null ? false : mandatory;

		offerProductTemplate.setProductTemplate(productTemplate);
		offerProductTemplate.setMandatory(mandatory);

		return offerProductTemplate;
	}

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
	public OfferTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("offerTemplateCode");
			handleMissingParameters();
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(code);
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, code);
		}

		OfferTemplateDto offerTemplateDto = convertOfferTemplateToDto(offerTemplate);

		return offerTemplateDto;
	}

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#findIgnoreNotFound(java.lang.String)
     */
    @Override
    public OfferTemplateDto findIgnoreNotFound(String code) throws MissingParameterException, InvalidParameterException, MeveoApiException {
        try {
            return find(code);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }
    
	public void remove(String code) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("offerTemplateCode");
			handleMissingParameters();
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(code);
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, code);
		}

		// deleteImage(offerTemplate);
		offerTemplateService.remove(offerTemplate);
	}

	/**
	 * Create or updates the OfferTemplate based on code
	 *
	 * @param postData

	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public OfferTemplate createOrUpdate(OfferTemplateDto postData) throws MeveoApiException, BusinessException {		
		OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode());

		if (offerTemplate == null) {
			return create(postData);
		} else {
			return update(postData);
		}
	}

    public OfferTemplateDto convertOfferTemplateToDto(OfferTemplate offerTemplate) {

        OfferTemplateDto dto = new OfferTemplateDto();
        dto.setCode(offerTemplate.getCode());
        dto.setDescription(offerTemplate.getDescription());
        dto.setName(offerTemplate.getName());
        dto.setLongDescription(offerTemplate.getLongDescription());
        dto.setDisabled(offerTemplate.isDisabled());
        dto.setImagePath(offerTemplate.getImagePath());
        dto.setValidFrom(offerTemplate.getValidFrom());
        dto.setValidTo(offerTemplate.getValidTo());

        if (offerTemplate.getBusinessOfferModel() != null) {
            dto.setBomCode(offerTemplate.getBusinessOfferModel().getCode());
        }

        if (offerTemplate.getOfferTemplateCategories() != null && !offerTemplate.getOfferTemplateCategories().isEmpty()) {
            List<String> offerTemplateCategories = new ArrayList<>();
            for (OfferTemplateCategory oc : offerTemplate.getOfferTemplateCategories()) {
                offerTemplateCategories.add(oc.getCode());
            }
            dto.setOfferTemplateCategories(offerTemplateCategories);
        }

        if (offerTemplate.getOfferServiceTemplates() != null && offerTemplate.getOfferServiceTemplates().size() > 0) {
            List<OfferServiceTemplateDto> offerTemplateServiceDtos = new ArrayList<OfferServiceTemplateDto>();
            for (OfferServiceTemplate st : offerTemplate.getOfferServiceTemplates()) {
                offerTemplateServiceDtos.add(new OfferServiceTemplateDto(st, entityToDtoConverter.getCustomFieldsDTO(st.getServiceTemplate())));
            }
            dto.setOfferServiceTemplates(offerTemplateServiceDtos);
        }

        dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(offerTemplate));

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
            dto.setOfferProductTemplates(offerProductTemplates);
        }

        return dto;
    }
}