package org.meveo.asg.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.ServiceDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.exception.ServiceTemplateAlreadyExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ServiceTemplateServiceApi extends BaseApi {

	private static Logger log = LoggerFactory
			.getLogger(ServiceTemplateServiceApi.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private SubscriptionService subscriptionService;

	public void create(ServiceDto serviceDto) throws MeveoApiException {
		if (!StringUtils.isBlank(serviceDto.getServiceId())) {
			Provider provider = providerService.findById(serviceDto
					.getProviderId());
			User currentUser = userService.findById(serviceDto
					.getCurrentUserId());

			String serviceTemplateCode = paramBean.getProperty(
					"asg.api.service.notcharged.prefix", "_NC_SE_")
					+ serviceDto.getServiceId();

			if (serviceTemplateService
					.findByCode(serviceTemplateCode, provider) != null) {
				throw new ServiceTemplateAlreadyExistsException(
						serviceTemplateCode);
			}

			ServiceTemplate serviceTemplate = new ServiceTemplate();
			serviceTemplate.setActive(true);
			serviceTemplate.setCode(serviceTemplateCode);
			serviceTemplate.setProvider(provider);
			try {
				serviceTemplate.setDescription(serviceDto.getDescriptions()
						.get(0).getDescription());
			} catch (NullPointerException e) {
				log.warn("Description is null.");
			} catch (IndexOutOfBoundsException e) {
				log.warn("Description is null.");
			}
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
				missingFields.add("serviceId");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public void update(ServiceDto serviceDto) throws MeveoApiException {
		if (!StringUtils.isBlank(serviceDto.getServiceId())) {
			Provider provider = providerService.findById(serviceDto
					.getProviderId());
			User currentUser = userService.findById(serviceDto
					.getCurrentUserId());

			String offerTemplateCode = paramBean.getProperty(
					"asg.api.service.offer.prefix", "_SE_")
					+ serviceDto.getServiceId();
			OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
					offerTemplateCode, provider);
			if (offerTemplateCode != null) {
				if (serviceDto.getDescriptions() != null
						&& serviceDto.getDescriptions().size() > 0) {
					offerTemplate.setDescription(serviceDto.getDescriptions()
							.get(0).getDescription());
					offerTemplateService.update(em, offerTemplate, currentUser);
				}
			}

			String serviceTemplateCode = paramBean.getProperty(
					"asg.api.service.notcharged.prefix", "_NC_SE_")
					+ serviceDto.getServiceId();
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(serviceTemplateCode, provider);
			if (serviceTemplate != null) {
				if (serviceDto.getDescriptions() != null
						&& serviceDto.getDescriptions().size() > 0) {
					serviceTemplate.setDescription(serviceDto.getDescriptions()
							.get(0).getDescription());
					serviceTemplateService.update(em, serviceTemplate,
							currentUser);
				}
			}

			String chargedServiceTemplateCode = paramBean.getProperty(
					"asg.api.service.charged.prefix", "_CH_SE_")
					+ serviceDto.getServiceId();
			ServiceTemplate chargedServiceTemplate = serviceTemplateService
					.findByCode(chargedServiceTemplateCode, provider);
			if (chargedServiceTemplate != null) {
				if (serviceDto.getDescriptions() != null
						&& serviceDto.getDescriptions().size() > 0) {
					chargedServiceTemplate.setDescription(serviceDto
							.getDescriptions().get(0).getDescription());
					serviceTemplateService.update(em, chargedServiceTemplate,
							currentUser);
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(serviceDto.getServiceId())) {
				missingFields.add("serviceId");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public void remove(Long providerId, String serviceId)
			throws MeveoApiException {
		if (!StringUtils.isBlank(serviceId)) {
			Provider provider = providerService.findById(providerId);

			String serviceTemplateCode = paramBean.getProperty(
					"asg.api.service.notcharged.prefix", "_NC_SE_") + serviceId;
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(serviceTemplateCode, provider);
			if (serviceTemplate != null) {
				List<ServiceInstance> serviceInstances = serviceInstanceService
						.findByServiceTemplate(em, serviceTemplate, provider);
				if (serviceInstances != null && serviceInstances.size() > 0) {
					return;
				}
			}

			String chargedServiceTemplateCode = paramBean.getProperty(
					"asg.api.service.charged.prefix", "_CH_SE_") + serviceId;
			ServiceTemplate chargedServiceTemplate = serviceTemplateService
					.findByCode(chargedServiceTemplateCode, provider);
			if (chargedServiceTemplate != null) {
				List<ServiceInstance> chargedServiceInstances = serviceInstanceService
						.findByServiceTemplate(em, chargedServiceTemplate,
								provider);
				if (chargedServiceInstances != null
						&& chargedServiceInstances.size() > 0) {
					return;
				}
			}

			String offerTemplateCode = paramBean.getProperty(
					"asg.api.service.offer.prefix", "_SE_") + serviceId;
			OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
					offerTemplateCode, provider);
			if (offerTemplate != null) {
				List<Subscription> subscriptions = subscriptionService
						.findByOfferTemplate(em, offerTemplate, provider);
				if (subscriptions != null && subscriptions.size() > 0) {
					return;
				}
			}

			// delete
			if (serviceTemplate != null) {
				serviceTemplateService.remove(em, serviceTemplate);
			}

			if (chargedServiceTemplate != null) {
				serviceTemplateService.remove(em, chargedServiceTemplate);
			}

			if (offerTemplate != null) {
				offerTemplateService.remove(em, offerTemplate);
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

			throw new MissingParameterException(sb.toString());
		}
	}

}
