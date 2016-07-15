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
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductTemplateService;

public class ProductTemplateApi extends BaseApi {

	@Inject
	private ProductTemplateService productTemplateService;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Inject
	private DigitalResourceService digitalResourceService;

	public ProductTemplateDto find(String code, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("productTemplate code");
			handleMissingParameters();
		}

		ProductTemplate productTemplate = productTemplateService.findByCode(code, currentUser.getProvider());
		if (productTemplate == null) {
			throw new EntityDoesNotExistsException(ProductTemplate.class, code);
		}

		return new ProductTemplateDto(productTemplate);
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
			handleMissingParameters();
		}

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

		// set OfferTemplateCategories
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

		// set DigitalResources
		List<DigitalResourcesDto> attachments = postData.getAttachments();
		if (attachments != null && !attachments.isEmpty()) {
			productTemplate.setAttachments(new ArrayList<DigitalResource>());
			DigitalResource attachment = null;
			for (DigitalResourcesDto digitalResourcesDto : attachments) {
				attachment = toDigitalResourceEntity(digitalResourcesDto, currentUser);
				productTemplate.getAttachments().add(attachment);
			}
		}

		// set image
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

		productTemplateService.create(productTemplate, currentUser);

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

		productTemplate.setDescription(keepOldValueIfNull(postData.getDescription(), productTemplate.getDescription()));
		productTemplate.setName(keepOldValueIfNull(postData.getName(), productTemplate.getName()));
		productTemplate.setValidFrom(keepOldValueIfNull(postData.getValidFrom(), productTemplate.getValidFrom()));
		productTemplate.setValidTo(keepOldValueIfNull(postData.getValidTo(), productTemplate.getValidTo()));
		productTemplate.setLifeCycleStatus(keepOldValueIfNull(postData.getLifeCycleStatus(), productTemplate.getLifeCycleStatus()));

		// set OfferTemplateCategories
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

		// set DigitalResources
		List<DigitalResourcesDto> attachments = postData.getAttachments();
		if (attachments != null && !attachments.isEmpty()) {
			productTemplate.setAttachments(new ArrayList<DigitalResource>());
			DigitalResource attachment = null;
			for (DigitalResourcesDto digitalResourcesDto : attachments) {
				attachment = toDigitalResourceEntity(digitalResourcesDto, currentUser);
				productTemplate.getAttachments().add(attachment);
			}
		}

		// set image
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

		productTemplateService.update(productTemplate, currentUser);

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
	
	private DigitalResource toDigitalResourceEntity(DigitalResourcesDto digitalResourcesDto, User user) throws MeveoApiException{
		String code = digitalResourcesDto.getCode();
		if(StringUtils.isBlank(code)){
			throw new EntityDoesNotExistsException(DigitalResource.class, code);
		}
		DigitalResource attachment = digitalResourceService.findByCode(digitalResourcesDto.getCode(), user.getProvider());
		if(attachment == null){
			attachment = new DigitalResource();
			attachment.setCode(digitalResourcesDto.getCode());
		}
		attachment.setDescription(digitalResourcesDto.getDescription());
		attachment.setUri(digitalResourcesDto.getUri());
		attachment.setMimeType(digitalResourcesDto.getMimeType());
		attachment.setDisabled(digitalResourcesDto.isDisabled());
		return attachment;
	}

	private <T> T keepOldValueIfNull(T newValue, T oldValue) {
		if (newValue == null) {
			return oldValue;
		}
		return newValue;
	}

}
