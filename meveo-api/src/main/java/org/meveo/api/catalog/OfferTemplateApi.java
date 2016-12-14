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
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
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
		}
		if (StringUtils.isBlank(postData.getName())) {
		    postData.setName(postData.getCode());
		}
		handleMissingParameters();

		Provider provider = currentUser.getProvider();

		if (offerTemplateService.findByCode(postData.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(OfferTemplate.class, postData.getCode());
		}		

		OfferTemplate offerTemplate = new OfferTemplate();
		populateFromDto(offerTemplate, postData, currentUser);

		offerTemplateService.create(offerTemplate, currentUser);

		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), offerTemplate, true, currentUser);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
		} catch (Exception e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw e;
		}
		return offerTemplate;
	}

	public OfferTemplate update(OfferTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getName())) {
		    postData.setName(postData.getCode());
		}
		handleMissingParameters();
		
		Provider provider = currentUser.getProvider();

		OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), provider);
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
		}
		
		populateFromDto(offerTemplate, postData, currentUser);

		offerTemplate = offerTemplateService.update(offerTemplate, currentUser);

		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), offerTemplate, false, currentUser);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
		} catch (Exception e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw e;
		}

		return offerTemplate;
	}
	
	private void populateFromDto(OfferTemplate offerTemplate, OfferTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
		Provider provider = currentUser.getProvider();
	
		BusinessOfferModel businessOffer = null;
		if (!StringUtils.isBlank(postData.getBomCode())) {
			businessOffer = businessOfferModelService.findByCode(postData.getBomCode(), provider);
			if (businessOffer == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
			}
		}
		
		if (!StringUtils.isBlank(postData.getOfferTemplateCategoryCode())) {
			OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postData.getOfferTemplateCategoryCode(), provider);
			if (offerTemplateCategory == null) {
				throw new EntityDoesNotExistsException(OfferTemplateCategory.class, postData.getOfferTemplateCategoryCode());
			}
			offerTemplate.addOfferTemplateCategory(offerTemplateCategory);
		}
		
		if (postData.getOfferTemplateCategories() != null) {
			offerTemplate.getOfferTemplateCategories().clear();
			for (String categoryCode : postData.getOfferTemplateCategories()) {
				OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(categoryCode, provider);
				if (offerTemplateCategory == null) {
					throw new EntityDoesNotExistsException(OfferTemplateCategory.class, categoryCode);
				}
				offerTemplate.addOfferTemplateCategory(offerTemplateCategory);
			}
		}
		
		offerTemplate.setBusinessOfferModel(businessOffer);
		offerTemplate.setProvider(provider);
		offerTemplate.setCode(postData.getCode());
		offerTemplate.setDescription(postData.getDescription());
		offerTemplate.setName(postData.getName());
		offerTemplate.setLongDescription(postData.getLongDescription());
		offerTemplate.setDisabled(postData.isDisabled());
		
		try {
			saveImage(offerTemplate, postData.getImagePath(), postData.getImageBase64(), currentUser.getProvider().getCode());
		} catch (IOException e1) {
			log.error("Invalid image data={}", e1.getMessage());
			throw new InvalidImageData();
		}

		// check service templates
		processOfferServiceTemplates(postData, offerTemplate, currentUser);

		// check offer product templates
		processOfferProductTemplates(postData, offerTemplate, currentUser);
	}

	private void processOfferServiceTemplates(OfferTemplateDto postData, OfferTemplate offerTemplate, User currentUser) throws MeveoApiException, BusinessException {
		List<OfferServiceTemplateDto> offerServiceTemplateDtos = postData.getOfferServiceTemplates();
		boolean hasOfferServiceTemplateDtos = offerServiceTemplateDtos != null && !offerServiceTemplateDtos.isEmpty();

		List<OfferServiceTemplate> existingServiceTemplates = offerTemplate.getOfferServiceTemplates();
		boolean hasExistingServiceTemplates = existingServiceTemplates != null && !existingServiceTemplates.isEmpty();

		if (hasOfferServiceTemplateDtos) {
			List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();
			OfferServiceTemplate offerServiceTemplate = null;
			for (OfferServiceTemplateDto offerServiceTemplateDto : offerServiceTemplateDtos) {
				offerServiceTemplate = getOfferServiceTemplatesFromDto(offerServiceTemplateDto, currentUser);
				offerServiceTemplate.setOfferTemplate(offerTemplate);
				newOfferServiceTemplates.add(offerServiceTemplate);
			}

			if (hasExistingServiceTemplates) {
				List<OfferServiceTemplate> offerServiceTemplatesForRemoval = new ArrayList<>(existingServiceTemplates);
				offerServiceTemplatesForRemoval.removeAll(newOfferServiceTemplates);
				List<OfferServiceTemplate> retainOfferServiceTemplates = new ArrayList<>(newOfferServiceTemplates);
				retainOfferServiceTemplates.retainAll(existingServiceTemplates);
				offerServiceTemplatesForRemoval.addAll(retainOfferServiceTemplates);
				newOfferServiceTemplates.removeAll(new ArrayList<>(existingServiceTemplates));
				offerTemplate.getOfferServiceTemplates().removeAll(new ArrayList<>(offerServiceTemplatesForRemoval));
				offerTemplate.getOfferServiceTemplates().addAll(retainOfferServiceTemplates);
			}

			offerTemplate.getOfferServiceTemplates().addAll(newOfferServiceTemplates);

		} else if (hasExistingServiceTemplates) {
			offerTemplate.getOfferServiceTemplates().removeAll(existingServiceTemplates);
		}
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

	private OfferServiceTemplate getOfferServiceTemplatesFromDto(OfferServiceTemplateDto offerServiceTemplateDto, User currentUser) throws MeveoApiException, BusinessException {

		ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
		ServiceTemplate serviceTemplate = null;
		if (serviceTemplateDto != null) {
			serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(), currentUser.getProvider());
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
				ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode(), currentUser.getProvider());
				if (incompatibleService == null) {
					throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
				}
				incompatibleServices.add(incompatibleService);
			}
			offerServiceTemplate.setIncompatibleServices(incompatibleServices);
		}

		return offerServiceTemplate;
	}

	private OfferProductTemplate getOfferProductTemplatesFromDto(OfferProductTemplateDto offerProductTemplateDto, User currentUser) throws MeveoApiException, BusinessException {

		ProductTemplateDto productTemplateDto = offerProductTemplateDto.getProductTemplate();
		ProductTemplate productTemplate = null;
		if (productTemplateDto != null) {
			productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), currentUser.getProvider());
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

		// deleteImage(offerTemplate, currentUser.getProvider().getCode());
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