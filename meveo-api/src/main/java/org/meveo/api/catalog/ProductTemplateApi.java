package org.meveo.api.catalog;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.DigitalResourcesDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;

public class ProductTemplateApi extends BaseApi {

	@Inject
	private ProductTemplateService productTemplateService;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Inject
	private DigitalResourceService digitalResourceService;

	@Inject
	private ProductChargeTemplateService productChargeTemplateService;

	@Inject
	private DigitalResourceApi digitalResourceApi;

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

		ProductChargeTemplate productChargeTemplate = productTemplate.getProductChargeTemplate();
		if (productChargeTemplate != null) {
			ProductChargeTemplateDto productChargeTemplateDto = new ProductChargeTemplateDto(productChargeTemplate, entityToDtoConverter.getCustomFieldsDTO(productChargeTemplate));
			productTemplateDto.setProductChargeTemplate(productChargeTemplateDto);
		}

		return productTemplateDto;
	}

	public void createOrUpdate(ProductTemplateDto productTemplateDto, User currentUser) throws MeveoApiException, BusinessException {
		ProductTemplate productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), currentUser.getProvider());

		if (productTemplate == null) {
			create(productTemplateDto, currentUser);
		} else {
			update(productTemplateDto, currentUser);
		}
	}

	public void create(ProductTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}

		ProductChargeTemplateDto productChargeTemplateDto = postData.getProductChargeTemplate();
		if (productChargeTemplateDto == null || StringUtils.isBlank(productChargeTemplateDto.getCode())) {
			missingParameters.add("productChargeTemplate");
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

		processProductChargeTemplate(postData, productTemplate, provider);

		// save product template now so that they can be referenced by the
		// related entities below.
		productTemplateService.create(productTemplate, currentUser);

		processDigitalResources(postData, productTemplate, currentUser);

		processOfferTemplateCategories(postData, productTemplate, provider);

		productTemplateService.update(productTemplate, currentUser);

	}

	public void update(ProductTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

		productTemplateService.update(productTemplate, currentUser);

	}

	private void processOfferTemplateCategories(ProductTemplateDto postData, ProductTemplate productTemplate, Provider provider) throws EntityDoesNotExistsException {
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

	private void processImage(ProductTemplateDto postData, ProductTemplate productTemplate) throws MeveoApiException {
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

	private void processDigitalResources(ProductTemplateDto postData, ProductTemplate productTemplate, User currentUser) throws BusinessException, MeveoApiException {
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

	private void processProductChargeTemplate(ProductTemplateDto postData, ProductTemplate productTemplate, Provider provider) throws BusinessException, MeveoApiException {
		ProductChargeTemplateDto productChargeTemplateDto = postData.getProductChargeTemplate();
		ProductChargeTemplate productChargeTemplate = productChargeTemplateService.findByCode(productChargeTemplateDto.getCode(), provider);
		if (productChargeTemplate == null) {
			throw new EntityDoesNotExistsException(ProductChargeTemplate.class, productChargeTemplateDto.getCode());
		}
		productTemplate.setProductChargeTemplate(productChargeTemplate);
	}

	public void remove(String code, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("productTemplate code");
			handleMissingParameters();
		}

		ProductTemplate productTemplate = productTemplateService.findByCode(code, currentUser.getProvider());
		if (productTemplate == null) {
			throw new EntityDoesNotExistsException(ProductTemplate.class, code);
		}

		productTemplateService.remove(productTemplate);
	}

}
