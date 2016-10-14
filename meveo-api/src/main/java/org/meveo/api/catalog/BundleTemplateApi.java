package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.BundleProductTemplateDto;
import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;

@Stateless
public class BundleTemplateApi extends ProductOfferingApi<BundleTemplate, BundleTemplateDto> {

	@Inject
	private BundleTemplateService bundleTemplateService;

	@Inject
	private ProductTemplateService productTemplateService;

	public BundleTemplateDto find(String code, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("bundleTemplate code");
			handleMissingParameters();
		}

		BundleTemplate bundleTemplate = bundleTemplateService.findByCode(code, currentUser.getProvider());
		if (bundleTemplate == null) {
			throw new EntityDoesNotExistsException(BundleTemplate.class, code);
		}

		BundleTemplateDto bundleTemplateDto = new BundleTemplateDto(bundleTemplate, entityToDtoConverter.getCustomFieldsDTO(bundleTemplate));

		processProductChargeTemplateToDto(bundleTemplate, bundleTemplateDto);

		// process all bundleProductTemplates then create
		// bundleProductTemplateDtos accordingly.
		Set<BundleProductTemplate> bundleProducts = bundleTemplate.getBundleProducts();
		if (bundleProducts != null && !bundleProducts.isEmpty()) {
			List<BundleProductTemplateDto> bundleProductTemplates = new ArrayList<>();
			BundleProductTemplateDto bundleProductTemplateDto = null;
			ProductTemplateDto childProductTemplateDto = null;
			ProductTemplate productTemplate = null;
			for (BundleProductTemplate bundleProductTemplate : bundleProducts) {
				bundleProductTemplateDto = new BundleProductTemplateDto();
				bundleProductTemplateDto.setQuantity(bundleProductTemplate.getQuantity());
				productTemplate = bundleProductTemplate.getProductTemplate();
				if (productTemplate != null) {
					childProductTemplateDto = new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate));
					bundleProductTemplateDto.setProductTemplate(childProductTemplateDto);
				}
				bundleProductTemplates.add(bundleProductTemplateDto);
			}
			bundleTemplateDto.setBundleProductTemplates(bundleProductTemplates);
		}

		return bundleTemplateDto;
	}

	public BundleTemplate createOrUpdate(BundleTemplateDto bundleTemplateDto, User currentUser) throws MeveoApiException, BusinessException {
		BundleTemplate bundleTemplate = bundleTemplateService.findByCode(bundleTemplateDto.getCode(), currentUser.getProvider());

		if (bundleTemplate == null) {
			return create(bundleTemplateDto, currentUser);
		} else {
		    return update(bundleTemplateDto, currentUser);
		}
	}

	public BundleTemplate create(BundleTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}

		List<BundleProductTemplateDto> bundleProductTemplates = postData.getBundleProductTemplates();
		if (bundleProductTemplates == null || bundleProductTemplates.isEmpty()) {
			missingParameters.add("bundleProductTemplates");
		}

		handleMissingParameters();

		Provider provider = currentUser.getProvider();

		if (bundleTemplateService.findByCode(postData.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(ProductTemplate.class, postData.getCode());
		}

		BundleTemplate bundleTemplate = new BundleTemplate();
		bundleTemplate.setCode(postData.getCode());
		bundleTemplate.setDescription(postData.getDescription());
		bundleTemplate.setName(postData.getName());
		bundleTemplate.setValidFrom(postData.getValidFrom());
		bundleTemplate.setValidTo(postData.getValidTo());
		bundleTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());

		processImage(postData, bundleTemplate);

		// save product template now so that they can be referenced by the
		// related entities below.
		bundleTemplateService.create(bundleTemplate, currentUser);

		processProductChargeTemplate(postData, bundleTemplate, provider);
		
		processDigitalResources(postData, bundleTemplate, currentUser);

		processOfferTemplateCategories(postData, bundleTemplate, provider);

		processBundleProductTemplates(postData, bundleTemplate, currentUser);

		bundleTemplateService.update(bundleTemplate, currentUser);
		
		return bundleTemplate;

	}

	public BundleTemplate update(BundleTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}

		List<BundleProductTemplateDto> bundleProductTemplates = postData.getBundleProductTemplates();
		if (bundleProductTemplates == null || bundleProductTemplates.isEmpty()) {
			missingParameters.add("bundleProductTemplates");
		}

		handleMissingParameters();

		Provider provider = currentUser.getProvider();

		BundleTemplate bundleTemplate = bundleTemplateService.findByCode(postData.getCode(), provider);

		if (bundleTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
		}

		bundleTemplate.setDescription(postData.getDescription());
		bundleTemplate.setName(postData.getName());
		bundleTemplate.setValidFrom(postData.getValidFrom());
		bundleTemplate.setValidTo(postData.getValidTo());
		bundleTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());

		processImage(postData, bundleTemplate);

		processProductChargeTemplate(postData, bundleTemplate, provider);

		processOfferTemplateCategories(postData, bundleTemplate, provider);

		processDigitalResources(postData, bundleTemplate, currentUser);

		processBundleProductTemplates(postData, bundleTemplate, currentUser);

		bundleTemplate = bundleTemplateService.update(bundleTemplate, currentUser);

		return bundleTemplate;
	}

	public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("bundleTemplate code");
			handleMissingParameters();
		}

		BundleTemplate bundleTemplate = bundleTemplateService.findByCode(code, currentUser.getProvider());
		if (bundleTemplate == null) {
			throw new EntityDoesNotExistsException(BundleTemplate.class, code);
		}

		bundleTemplateService.remove(bundleTemplate, currentUser);
	}

	private void processBundleProductTemplates(BundleTemplateDto postData, BundleTemplate bundleTemplate, User user) throws MeveoApiException, BusinessException {
		List<BundleProductTemplateDto> bundleProductTemplates = postData.getBundleProductTemplates();
		boolean hasBundleProductTemplateDtos = bundleProductTemplates != null && !bundleProductTemplates.isEmpty();
		Set<BundleProductTemplate> existingProductTemplates = bundleTemplate.getBundleProducts();
		boolean hasExistingProductTemplates = existingProductTemplates != null && !existingProductTemplates.isEmpty();
		if (hasBundleProductTemplateDtos) {
			List<BundleProductTemplate> newBundleProductTemplates = new ArrayList<>();
			BundleProductTemplate bundleProductTemplate = null;
			for (BundleProductTemplateDto bundleProductTemplateDto : bundleProductTemplates) {
				bundleProductTemplate = getBundleProductTemplatesFromDto(bundleProductTemplateDto, user.getProvider());
				bundleProductTemplate.setBundleTemplate(bundleTemplate);
				newBundleProductTemplates.add(bundleProductTemplate);
			}
			if (hasExistingProductTemplates) {
				List<BundleProductTemplate> bundleProductTemplatesForRemoval = new ArrayList<>(existingProductTemplates);
				List<BundleProductTemplate> newBundleProductTemplateForRemoval = new ArrayList<>();
				bundleProductTemplatesForRemoval.removeAll(newBundleProductTemplates);
				bundleTemplate.getBundleProducts().removeAll(bundleProductTemplatesForRemoval);
				for(BundleProductTemplate currentBundleProductTemplate : bundleTemplate.getBundleProducts()){
					for(BundleProductTemplate newBundleProductTemplate : newBundleProductTemplates){
						if(newBundleProductTemplate.equals(currentBundleProductTemplate)){
							currentBundleProductTemplate.setQuantity(newBundleProductTemplate.getQuantity());
							newBundleProductTemplateForRemoval.add(currentBundleProductTemplate);
							break;
						}
					}
				}
				newBundleProductTemplates.removeAll(newBundleProductTemplateForRemoval);
			}
			bundleTemplate.getBundleProducts().addAll(newBundleProductTemplates);
		} else if (hasExistingProductTemplates) {
			bundleTemplate.getBundleProducts().removeAll(existingProductTemplates);
		}

	}

	private BundleProductTemplate getBundleProductTemplatesFromDto(BundleProductTemplateDto bundleProductTemplateDto, Provider provider)
			throws MeveoApiException, BusinessException {

		ProductTemplateDto productTemplateDto = bundleProductTemplateDto.getProductTemplate();
		ProductTemplate productTemplate = null;
		if (productTemplateDto != null) {
			productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), provider);
			if (productTemplate == null) {
				throw new MeveoApiException(String.format("ProductTemplate[code = %s]does not exist.", productTemplateDto.getCode()));
			}
		}

		BundleProductTemplate bundleProductTemplate = new BundleProductTemplate();

		bundleProductTemplate.setProductTemplate(productTemplate);
		bundleProductTemplate.setQuantity(bundleProductTemplateDto.getQuantity());

		return bundleProductTemplate;
	}

}
