package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductTemplateService;

public class ProductTemplateApi extends BaseApi {

	@Inject
	protected EntityToDtoConverter entityToDtoConverter;

	@Inject
	private ProductTemplateService productTemplateService;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

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
		productTemplate.setModelCode(postData.getModelCode());
		productTemplate.setValidFrom(postData.getValidFrom());
		productTemplate.setValidTo(postData.getValidTo());
		productTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());

		// set OfferTemplateCategories
		List<OfferTemplateCategoryDto> offerTemplateCategories = postData.getOfferTemplateCategories();
		if (offerTemplateCategories != null && !offerTemplateCategories.isEmpty()) {
			productTemplate.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());
			for (OfferTemplateCategoryDto offerTemplateCategoryDto : offerTemplateCategories) {
				OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(offerTemplateCategoryDto.getCode(), currentUser.getProvider());
				if (offerTemplateCategory == null) {
					throw new EntityDoesNotExistsException(OfferTemplateCategory.class, offerTemplateCategoryDto.getCode());
				}
				productTemplate.getOfferTemplateCategories().add(offerTemplateCategory);
			}
		}

		// set DigitalResources

		// set image

	}

	public void update(ProductTemplateDto productTemplateDto, User currentUser) {
		// TODO Auto-generated method stub

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
