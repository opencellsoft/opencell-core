package org.meveo.api.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.catalog.impl.ProductTemplateService;

@Stateless
public class ProductTemplateApi extends ProductOfferingApi<ProductTemplate, ProductTemplateDto> {

	@Inject
	private ProductTemplateService productTemplateService;

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
	public ProductTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("productTemplate code");
			handleMissingParameters();
		}

		ProductTemplate productTemplate = productTemplateService.findByCode(code);
		if (productTemplate == null) {
			throw new EntityDoesNotExistsException(ProductTemplate.class, code);
		}

		ProductTemplateDto productTemplateDto = new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate));

		processProductChargeTemplateToDto(productTemplate, productTemplateDto);

		return productTemplateDto;
	}

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#findIgnoreNotFound(java.lang.String)
     */
    @Override
    public ProductTemplateDto findIgnoreNotFound(String code) throws MissingParameterException, InvalidParameterException, MeveoApiException {
        try {
            return find(code);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }
    
	public ProductTemplate createOrUpdate(ProductTemplateDto productTemplateDto) throws MeveoApiException, BusinessException {
		ProductTemplate productTemplate = productTemplateService.findByCode(productTemplateDto.getCode());

		if (productTemplate == null) {
			return create(productTemplateDto);
		} else {
			return update(productTemplateDto);
		}
	}

	public ProductTemplate create(ProductTemplateDto postData) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getName())) {
			missingParameters.add("name");
		}		

		if (postData.getProductChargeTemplates() != null) {
			List<ProductChargeTemplateDto> productChargeTemplateDtos = postData.getProductChargeTemplates();
			for (ProductChargeTemplateDto productChargeTemplateDto : productChargeTemplateDtos) {
				if (productChargeTemplateDto == null || StringUtils.isBlank(productChargeTemplateDto.getCode())) {
					missingParameters.add("productChargeTemplate");
				}
			}
		} else {
			missingParameters.add("productChargeTemplates");
		}

		handleMissingParameters();

		

		if (productTemplateService.findByCode(postData.getCode()) != null) {
			throw new EntityAlreadyExistsException(ProductTemplate.class, postData.getCode());
		}

		ProductTemplate productTemplate = new ProductTemplate();
		productTemplate.setCode(postData.getCode());
		productTemplate.setDescription(postData.getDescription());
		productTemplate.setName(postData.getName());
		productTemplate.setValidFrom(postData.getValidFrom());
		productTemplate.setValidTo(postData.getValidTo());
		productTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());
		try {
			saveImage(productTemplate, postData.getImagePath(), postData.getImageBase64());
		} catch (IOException e1) {
			log.error("Invalid image data={}", e1.getMessage());
			throw new InvalidImageData();
		}
				
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), productTemplate, false);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        // save product template now so that they can be referenced by the
        // related entities below.
        productTemplateService.create(productTemplate);
        
		if(postData.getProductChargeTemplates()!= null){
			processProductChargeTemplate(postData, productTemplate);
		}
		if(postData.getAttachments() != null){
			processDigitalResources(postData, productTemplate);
		}
		if( postData.getOfferTemplateCategories() != null){
			processOfferTemplateCategories(postData, productTemplate);
		}

		productTemplateService.update(productTemplate);
		

		return productTemplate;
	}

	public ProductTemplate update(ProductTemplateDto postData) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");			
		}
		if (StringUtils.isBlank(postData.getName())) {
			missingParameters.add("name");
		}
		handleMissingParameters();

		
		ProductTemplate productTemplate = productTemplateService.findByCode(postData.getCode());

		if (productTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
		}
		productTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode())?postData.getCode():postData.getUpdatedCode());
		productTemplate.setDescription(postData.getDescription());
		productTemplate.setName(postData.getName());
		productTemplate.setValidFrom(postData.getValidFrom());
		productTemplate.setValidTo(postData.getValidTo());
		productTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());
		try {
			saveImage(productTemplate, postData.getImagePath(), postData.getImageBase64());
		} catch (IOException e1) {
			log.error("Invalid image data={}", e1.getMessage());
			throw new InvalidImageData();
		}
		
		if(postData.getProductChargeTemplates()!= null){
			processProductChargeTemplate(postData, productTemplate);	
		}		
		if( postData.getOfferTemplateCategories() != null){
			processOfferTemplateCategories(postData, productTemplate);
		}
		if(postData.getAttachments() != null){
			processDigitalResources(postData, productTemplate);
		}

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), productTemplate, false);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        productTemplate= productTemplateService.update(productTemplate);

		return productTemplate;
	}

	public void remove(String code) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("productTemplate code");
			handleMissingParameters();
		}

		ProductTemplate productTemplate = productTemplateService.findByCode(code);
		if (productTemplate == null) {
			throw new EntityDoesNotExistsException(ProductTemplate.class, code);
		}
		//deleteImage(productTemplate);
		productTemplateService.remove(productTemplate);
	}

	public List<ProductTemplateDto> list() {
		List<ProductTemplate> listProductTemplate = productTemplateService.list();
		List<ProductTemplateDto> dtos = new ArrayList<ProductTemplateDto>();
		if(listProductTemplate != null){
			for(ProductTemplate productTemplate : listProductTemplate){
				dtos.add(new ProductTemplateDto(productTemplate, null));			
			}
		}
		return dtos;
	}

}
