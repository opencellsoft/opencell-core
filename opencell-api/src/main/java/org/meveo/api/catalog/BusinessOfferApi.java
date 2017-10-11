package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.BSMConfigurationDto;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;

@Stateless
public class BusinessOfferApi extends BaseApi {

	@Inject
	private BusinessOfferModelService businessOfferModelService;
	
	@Inject
	private BusinessServiceModelService businessServiceModelService;

	public Long createOfferFromBOM(BomOfferDto postData) throws MeveoApiException {

		if (StringUtils.isBlank(postData.getBomCode())) {
			missingParameters.add("bomCode");
		}

		handleMissingParametersAndValidate(postData);
	      
		// find bom
		BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(postData.getBomCode());
		if (businessOfferModel == null) {
			throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
		}

		// get the offer from bom
		OfferTemplate bomOffer = businessOfferModel.getOfferTemplate();
		if (bomOffer == null) {
			throw new MeveoApiException("No offer template attached");
		}

		if ((bomOffer.getOfferServiceTemplates() == null || bomOffer.getOfferServiceTemplates().isEmpty())
				&& (bomOffer.getOfferProductTemplates() == null || bomOffer.getOfferProductTemplates().isEmpty())) {
			throw new MeveoApiException("No service or product template attached");
		}
		
		// process bsm
		List<ServiceConfigurationDto> serviceConfigurationDtoFromBSM = getServiceConfiguration(postData.getBusinessServiceModels());
		if (!serviceConfigurationDtoFromBSM.isEmpty()) {
			postData.getServicesToActivate().addAll(serviceConfigurationDtoFromBSM);
		}

		OfferTemplate newOfferTemplate = null;
		try {
			newOfferTemplate = businessOfferModelService.createOfferFromBOM(businessOfferModel, postData.getCustomFields(), postData.getCode(), postData.getName(),
					postData.getDescription(), postData.getServicesToActivate(), postData.getProductsToActivate());
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}

		// populate service custom fields
		for (OfferServiceTemplate ost : newOfferTemplate.getOfferServiceTemplates()) {
			ServiceTemplate serviceTemplate = ost.getServiceTemplate();

			for (ServiceConfigurationDto serviceCodeDto : postData.getServicesToActivate()) {
				//Caution the servicode building algo must match that of BusinessOfferModelService.createOfferFromBOM
				String serviceCode = ost.getOfferTemplate().getId() + "_" + serviceCodeDto.getCode();
				if (serviceCode.equals(serviceTemplate.getCode())) {
					if (serviceCodeDto.getCustomFields() != null) {
						try {
							CustomFieldsDto cfsDto = new CustomFieldsDto();
							cfsDto.setCustomField(serviceCodeDto.getCustomFields());
							populateCustomFields(cfsDto, serviceTemplate, true);
			            } catch (MissingParameterException e) {
			                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
			                throw e;
			            } catch (Exception e) {
			                log.error("Failed to associate custom field instance to an entity", e);
							throw e;
						}
						break;
					}
				}
			}
			
			// populate bsm service custom fields
			if (serviceConfigurationDtoFromBSM != null && !serviceConfigurationDtoFromBSM.isEmpty()) {
				for (ServiceConfigurationDto serviceCodeDto : serviceConfigurationDtoFromBSM) {
					String serviceTemplateCode = ost.getOfferTemplate().getId() + "_" + serviceCodeDto.getCode();
					if (serviceTemplate.isInstantiatedFromBSM()) {
						serviceTemplateCode = newOfferTemplate.getId() + "_" + serviceTemplate.getId() + "_" + serviceTemplate.getCode();
					}
					if (serviceTemplateCode.equals(serviceTemplate.getCode())) {
						if (serviceCodeDto.getCustomFields() != null) {
							try {
								CustomFieldsDto cfsDto = new CustomFieldsDto();
								cfsDto.setCustomField(serviceCodeDto.getCustomFields());
								populateCustomFields(cfsDto, serviceTemplate, true);
							} catch (MissingParameterException e) {
								log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
								throw e;
							} catch (Exception e) {
								log.error("Failed to associate custom field instance to an entity", e);
								throw e;
							}
							break;
						}
					}
				}
			}
		}
		
		// populate product custom fields
		for (OfferProductTemplate opt : newOfferTemplate.getOfferProductTemplates()) {
			ProductTemplate productTemplate = opt.getProductTemplate();

			for (ServiceConfigurationDto productCodeDto : postData.getProductsToActivate()) {
				// Caution the productCode building algo must match that of
				// BusinessOfferModelService.createOfferFromBOM
				String productCode = opt.getOfferTemplate().getId() + "_" + productCodeDto.getCode();
				if (productCode.equals(productTemplate.getCode())) {
					if (productCodeDto.getCustomFields() != null) {
						try {
							CustomFieldsDto cfsDto = new CustomFieldsDto();
							cfsDto.setCustomField(productCodeDto.getCustomFields());
							populateCustomFields(cfsDto, productTemplate, true);
						} catch (MissingParameterException e) {
							log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
							throw e;
						} catch (Exception e) {
							log.error("Failed to associate custom field instance to an entity", e);
							throw e;
						}
						break;
					}
				}
			}
		}

		// populate offer custom fields
		if (newOfferTemplate != null && postData.getCustomFields() != null) {
			try {
				CustomFieldsDto cfsDto = new CustomFieldsDto();
				cfsDto.setCustomField(postData.getCustomFields());
				populateCustomFields(cfsDto, newOfferTemplate, true);
            } catch (MissingParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw e;
			}
		}
		
		return newOfferTemplate.getId();
	}
	
	private List<ServiceConfigurationDto> getServiceConfiguration(List<BSMConfigurationDto> bsmsConfig) throws MeveoApiException {
		List<ServiceConfigurationDto> result = new ArrayList<>();

		if (bsmsConfig != null && !bsmsConfig.isEmpty()) {
			for (BSMConfigurationDto bsmConfig : bsmsConfig) {
				BusinessServiceModel bsm = businessServiceModelService.findByCode(bsmConfig.getCode());
				if (bsm == null) {
					throw new EntityDoesNotExistsException(BusinessServiceModel.class, bsmConfig.getCode());
				}

				if (!bsm.getServiceTemplate().getCode().equals(bsmConfig.getServiceConfiguration().getCode())) {
					throw new MeveoApiException("Service template with code=" + bsmConfig.getServiceConfiguration().getCode() + " is not linked to BSM with code="
							+ bsm.getCode());
				}
				bsmConfig.getServiceConfiguration().setInstantiatedFromBSM(true);
				
				result.add(bsmConfig.getServiceConfiguration());
			}
		}

		return result;
	}
	
}
