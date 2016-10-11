package org.meveo.api.catalog;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.ProductTemplateService;

@Stateless
public class ProductTemplateApi extends ProductOfferingApi<ProductTemplate, ProductTemplateDto> {

	@Inject
	private ProductTemplateService productTemplateService;

	public ProductTemplateDto find(String code, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("productTemplate code");
			handleMissingParameters();
		}

		ProductTemplate productTemplate = productTemplateService.findByCode(code, currentUser.getProvider());
		if (productTemplate == null) {
			throw new EntityDoesNotExistsException(ProductTemplate.class, code);
		}

		ProductTemplateDto productTemplateDto = new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate));

		processProductChargeTemplateToDto(productTemplate, productTemplateDto);

		return productTemplateDto;
	}

	public ProductTemplate createOrUpdate(ProductTemplateDto productTemplateDto, User currentUser) throws MeveoApiException, BusinessException {
		ProductTemplate productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), currentUser.getProvider());

		if (productTemplate == null) {
			return create(productTemplateDto, currentUser);
		} else {
			return update(productTemplateDto, currentUser);
		}
	}

	public ProductTemplate create(ProductTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}

		List<ProductChargeTemplateDto> productChargeTemplateDtos = postData.getProductChargeTemplates();
		for(ProductChargeTemplateDto productChargeTemplateDto : productChargeTemplateDtos){
			if (productChargeTemplateDto == null || StringUtils.isBlank(productChargeTemplateDto.getCode())) {
				missingParameters.add("productChargeTemplate");
			}
		}

		handleMissingParameters();

		Provider provider = currentUser.getProvider();

		if (productTemplateService.findByCode(postData.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(ProductTemplate.class, postData.getCode());
		}

		ProductTemplate productTemplate = new ProductTemplate();
		productTemplate.setCode(postData.getCode());
		productTemplate.setDescription(postData.getDescription());
		productTemplate.setName(postData.getName());
		productTemplate.setValidFrom(postData.getValidFrom());
		productTemplate.setValidTo(postData.getValidTo());
		productTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());

		processImage(postData, productTemplate);

		// save product template now so that they can be referenced by the
		// related entities below.
		productTemplateService.create(productTemplate, currentUser);
		
		processProductChargeTemplate(postData, productTemplate, provider);

		processDigitalResources(postData, productTemplate, currentUser);

		processOfferTemplateCategories(postData, productTemplate, provider);

		productTemplateService.update(productTemplate, currentUser);

		return productTemplate;
	}

	public ProductTemplate update(ProductTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
			handleMissingParameters();
		}

		Provider provider = currentUser.getProvider();

		ProductTemplate productTemplate = productTemplateService.findByCode(postData.getCode(), provider);

		if (productTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
		}

		productTemplate.setDescription(postData.getDescription());
		productTemplate.setName(postData.getName());
		productTemplate.setValidFrom(postData.getValidFrom());
		productTemplate.setValidTo(postData.getValidTo());
		productTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());

		processImage(postData, productTemplate);

		processProductChargeTemplate(postData, productTemplate, provider);

		processOfferTemplateCategories(postData, productTemplate, provider);

		processDigitalResources(postData, productTemplate, currentUser);

		productTemplate= productTemplateService.update(productTemplate, currentUser);

		return productTemplate;
	}

	public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("productTemplate code");
			handleMissingParameters();
		}

		ProductTemplate productTemplate = productTemplateService.findByCode(code, currentUser.getProvider());
		if (productTemplate == null) {
			throw new EntityDoesNotExistsException(ProductTemplate.class, code);
		}
		productTemplateService.remove(productTemplate, currentUser);
	}

}
