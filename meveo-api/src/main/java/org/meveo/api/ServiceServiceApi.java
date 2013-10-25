package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.ServiceDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ServiceServiceApi extends BaseApi {

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private OfferTemplateService offerTemplateService;

	public void create(ServiceDto serviceDto) throws MeveoApiException {
		if (!StringUtils.isBlank(serviceDto.getServiceId())
				&& serviceDto.getDescriptions() != null
				&& serviceDto.getDescriptions().size() > 0) {

			Provider provider = providerService.findById(serviceDto
					.getProviderId());
			User currentUser = userService.findById(serviceDto
					.getCurrentUserId());

			String serviceTemplateCode = paramBean.getProperty(
					"asg.api.serviceTemplate.prefix", "_NC_")
					+ serviceDto.getServiceId();

			if (serviceTemplateService
					.findByCode(serviceTemplateCode, provider) != null) {
				throw new MeveoApiException("Service template with code="
						+ serviceTemplateCode + " already exists.");
			}

			ServiceTemplate serviceTemplate = new ServiceTemplate();
			serviceTemplate.setActive(true);
			serviceTemplate.setCode(serviceTemplateCode);
			serviceTemplate.setProvider(provider);
			serviceTemplate.setDescription(serviceDto.getDescriptions().get(0)
					.getDescription());
			serviceTemplateService.create(em, serviceTemplate, currentUser,
					provider);

			String offerTemplateCode = paramBean.getProperty(
					"asg.api.service.offer.prefix", "_SE_")
					+ serviceDto.getServiceId();
			List<ServiceTemplate> serviceTemplates = new ArrayList<ServiceTemplate>();
			serviceTemplates.add(serviceTemplate);
			OfferTemplate offerTemplate = new OfferTemplate();
			offerTemplate.setCode(offerTemplateCode);
			offerTemplate.setActive(true);
			offerTemplate.setServiceTemplates(serviceTemplates);
			offerTemplateService.create(em, offerTemplate, currentUser,
					provider);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(serviceDto.getServiceId())) {
				missingFields.add("Service Id");
			}
			if (serviceDto.getDescriptions() == null
					|| serviceDto.getDescriptions().size() == 0) {
				missingFields.add("Description");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void remove(Long providerId, String serviceId)
			throws MeveoApiException {
		if (!StringUtils.isBlank(serviceId)) {
			Provider provider = providerService.findById(providerId);

			String serviceTemplateCode = paramBean.getProperty(
					"asg.api.serviceTemplate.prefix", "_NC_") + serviceId;

			if (serviceTemplateService
					.findByCode(serviceTemplateCode, provider) != null) {
				throw new MeveoApiException("Service template with code="
						+ serviceTemplateCode + " already exists.");
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(serviceId)) {
				missingFields.add("Service Id");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

}
