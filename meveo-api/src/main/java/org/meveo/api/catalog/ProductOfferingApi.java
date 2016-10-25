package org.meveo.api.catalog;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.catalog.DigitalResourcesDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;

public abstract class ProductOfferingApi<E extends IEntity, T extends BaseDto> extends BaseCrudApi<E, T> {

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Inject
	private DigitalResourceService digitalResourceService;

	@Inject
	private ProductChargeTemplateService productChargeTemplateService;

	@Inject
	private DigitalResourceApi digitalResourceApi;

	protected void processProductChargeTemplateToDto(ProductTemplate productTemplate, ProductTemplateDto productTemplateDto) {
		List<ProductChargeTemplate> productChargeTemplates = productTemplate.getProductChargeTemplates();
		ProductChargeTemplateDto productChargeTemplateDto = null;
		List<ProductChargeTemplateDto> chargeDtos = new ArrayList<>();
		if(productChargeTemplates != null) {
			for(ProductChargeTemplate productChargeTemplate : productChargeTemplates) {
				if (productChargeTemplate != null) {
					productChargeTemplate.setProductTemplate(productTemplate);
					productChargeTemplateDto = new ProductChargeTemplateDto(productChargeTemplate, entityToDtoConverter.getCustomFieldsDTO(productChargeTemplate));
					chargeDtos.add(productChargeTemplateDto);
				}
			}
			productTemplateDto.setProductChargeTemplates(chargeDtos);
		}
	}

	protected void processOfferTemplateCategories(ProductTemplateDto postData, ProductTemplate productTemplate, Provider provider) throws EntityDoesNotExistsException {
		List<OfferTemplateCategoryDto> offerTemplateCategories = postData.getOfferTemplateCategories();
		if (offerTemplateCategories != null && !offerTemplateCategories.isEmpty()) {
			productTemplate.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());
			for (OfferTemplateCategoryDto offerTemplateCategoryDto : offerTemplateCategories) {
				OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(offerTemplateCategoryDto.getCode(), provider);
				if (offerTemplateCategory == null) {
					throw new EntityDoesNotExistsException(OfferTemplateCategory.class, offerTemplateCategoryDto.getCode());
				}
				productTemplate.getOfferTemplateCategories().add(offerTemplateCategory);
			}
		}
	}

	protected void processImage(ProductTemplateDto postData, ProductTemplate productTemplate) throws MeveoApiException {
		if (postData.getImageValue() != null) {
			byte[] byteContent = postData.getImageValue().getBytes();
			try {
				Blob blobImg = new SerialBlob(byteContent);
				productTemplate.setImage(blobImg);
			} catch (SerialException e) {
				throw new MeveoApiException("Invalid base64 encoded image string.");
			} catch (SQLException e) {
				throw new MeveoApiException("System error.");
			}
		}
	}

	protected void processDigitalResources(ProductTemplateDto postData, ProductTemplate productTemplate, User currentUser) throws BusinessException, MeveoApiException {
		List<DigitalResourcesDto> attachmentDtos = postData.getAttachments();
		boolean hasAttachmentDtos = attachmentDtos != null && !attachmentDtos.isEmpty();
		List<DigitalResource> existingAttachments = productTemplate.getAttachments();
		boolean hasExistingAttachments = existingAttachments != null && !existingAttachments.isEmpty();
		if (hasAttachmentDtos) {
			DigitalResource attachment = null;
			List<DigitalResource> newAttachments = new ArrayList<>();
			for (DigitalResourcesDto attachmentDto : attachmentDtos) {
				attachment = digitalResourceService.findByCode(attachmentDto.getCode(), currentUser.getProvider());
				if (attachment == null) {
					throw new EntityDoesNotExistsException(DigitalResource.class, attachmentDto.getCode());
				}
				attachment = digitalResourceApi.populateDigitalResourceEntity(attachment, attachmentDto, currentUser);
				newAttachments.add(attachment);
			}
			productTemplate.setAttachments(newAttachments);
		} else if (hasExistingAttachments) {
			productTemplate.setAttachments(null);
		}
	}

	protected void processProductChargeTemplate(ProductTemplateDto postData, ProductTemplate productTemplate, Provider provider) throws BusinessException, MeveoApiException {
		List<ProductChargeTemplate> newProductChargeTemplates = new ArrayList<>();
		ProductChargeTemplate productChargeTemplate = null;
		for(ProductChargeTemplateDto productChargeTemplateDto : postData.getProductChargeTemplates()){
			productChargeTemplate = productChargeTemplateService.findByCode(productChargeTemplateDto.getCode(), provider);
			if (productChargeTemplate == null) {
				throw new EntityDoesNotExistsException(ProductChargeTemplate.class, productChargeTemplateDto.getCode());
			}
			productChargeTemplate.setProductTemplate(productTemplate);
			newProductChargeTemplates.add(productChargeTemplate);
		}
		
		List<ProductChargeTemplate> existingProductChargeTemplates = productTemplate.getProductChargeTemplates();
		boolean hasExistingProductChargeTemplates = existingProductChargeTemplates != null && !existingProductChargeTemplates.isEmpty();
		boolean hasNewProductChargeTemplates = !newProductChargeTemplates.isEmpty();
		
		if(hasNewProductChargeTemplates){
			if(hasExistingProductChargeTemplates){
				List<ProductChargeTemplate> productChargeTemplatesForRemoval = new ArrayList<>(existingProductChargeTemplates);
				productChargeTemplatesForRemoval.removeAll(newProductChargeTemplates);
				productTemplate.getProductChargeTemplates().removeAll(productChargeTemplatesForRemoval);
			}
			newProductChargeTemplates.removeAll(existingProductChargeTemplates);
			productTemplate.getProductChargeTemplates().addAll(newProductChargeTemplates);
		} else if (hasExistingProductChargeTemplates) {
			productTemplate.getProductChargeTemplates().removeAll(existingProductChargeTemplates);
		}
	}

}
