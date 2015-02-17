package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferTemplateApi extends BaseApi {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	public void create(OfferTemplateDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			if (offerTemplateService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(OfferTemplate.class, postData.getCode());
			}

			OfferTemplate offerTemplate = new OfferTemplate();
			offerTemplate.setProvider(provider);
			offerTemplate.setCode(postData.getCode());
			offerTemplate.setDescription(postData.getDescription());
			offerTemplate.setDisabled(postData.isDisabled());

			// check service templates
			if (postData.getServiceTemplates() != null
					&& postData.getServiceTemplates().getServiceTemplate().size() > 0) {
				List<ServiceTemplate> serviceTemplates = new ArrayList<ServiceTemplate>();
				for (ServiceTemplateDto serviceTemplateDto : postData.getServiceTemplates().getServiceTemplate()) {
					ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(),
							provider);
					if (serviceTemplate == null) {
						throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
					}

					serviceTemplates.add(serviceTemplate);
				}

				offerTemplate.setServiceTemplates(serviceTemplates);
			}

			offerTemplateService.create(offerTemplate, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(OfferTemplateDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode());
			}

			offerTemplate.setDescription(postData.getDescription());
			offerTemplate.setDisabled(postData.isDisabled());

			// check service templates
			if (postData.getServiceTemplates() != null
					&& postData.getServiceTemplates().getServiceTemplate().size() > 0) {
				List<ServiceTemplate> serviceTemplates = new ArrayList<ServiceTemplate>();
				for (ServiceTemplateDto serviceTemplateDto : postData.getServiceTemplates().getServiceTemplate()) {
					ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode(),
							provider);
					if (serviceTemplate == null) {
						throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateDto.getCode());
					}

					serviceTemplates.add(serviceTemplate);
				}

				offerTemplate.getServiceTemplates().clear();
				offerTemplate.setServiceTemplates(serviceTemplates);
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public OfferTemplateDto find(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			OfferTemplate offerTemplate = offerTemplateService.findByCode(code, provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, code);
			}

			return new OfferTemplateDto(offerTemplate);
		} else {
			missingParameters.add("offerTemplateCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			OfferTemplate offerTemplate = offerTemplateService.findByCode(code, provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, code);
			}

			offerTemplateService.remove(offerTemplate);
		} else {
			missingParameters.add("offerTemplateCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
