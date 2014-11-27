package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.ServiceUsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.ServiceUsageChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.ServiceUsageChargeTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ServiceTemplateApi extends BaseApi {

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private ServiceUsageChargeTemplateService serviceUsageChargeTemplateService;

	@SuppressWarnings("rawtypes")
	@Inject
	private CounterTemplateService counterTemplateService;

	public void create(ServiceTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();
			// check if code already exists
			if (serviceTemplateService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(
						ServiceTemplateService.class, postData.getCode());
			}

			ServiceTemplate serviceTemplate = new ServiceTemplate();
			serviceTemplate.setCode(postData.getCode());
			serviceTemplate.setDescription(postData.getDescription());
			serviceTemplate.setProvider(provider);

			// check for recurring charges
			if (postData.getRecurringCharges() != null
					&& postData.getRecurringCharges().size() > 0) {
				for (String chargeCode : postData.getRecurringCharges()) {
					RecurringChargeTemplate chargeTemplate = recurringChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTemplate == null) {
						throw new EntityDoesNotExistsException(
								RecurringChargeTemplate.class, chargeCode);
					}

					serviceTemplate.getRecurringCharges().add(chargeTemplate);
				}
			}
			// check for subscription charges
			if (postData.getSubscriptionCharges() != null
					&& postData.getSubscriptionCharges().size() > 0) {
				for (String chargeCode : postData.getSubscriptionCharges()) {
					OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTemplate == null) {
						throw new EntityDoesNotExistsException(
								OneShotChargeTemplate.class, chargeCode);
					}

					serviceTemplate.getSubscriptionCharges()
							.add(chargeTemplate);
				}
			}
			// check for termination charges
			if (postData.getTerminationCharges() != null
					&& postData.getTerminationCharges().size() > 0) {
				for (String chargeCode : postData.getTerminationCharges()) {
					OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTemplate == null) {
						throw new EntityDoesNotExistsException(
								OneShotChargeTemplate.class, chargeCode);
					}

					serviceTemplate.getTerminationCharges().add(chargeTemplate);
				}
			}

			// check for usage charges
			List<ServiceUsageChargeTemplate> serviceUsageChargeTemplates = new ArrayList<ServiceUsageChargeTemplate>();
			if (postData.getServiceUsageCharges() != null
					&& postData.getServiceUsageCharges().size() > 0) {
				for (ServiceUsageChargeTemplateDto serviceUsageChargeTemplateDto : postData
						.getServiceUsageCharges()) {
					// search for usageCharge
					UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService
							.findByCode(serviceUsageChargeTemplateDto
									.getUsageChargeTemplate(), provider);
					if (usageChargeTemplate == null) {
						throw new EntityDoesNotExistsException(
								UsageChargeTemplate.class,
								serviceUsageChargeTemplateDto
										.getUsageChargeTemplate());
					}

					// search for counter
					CounterTemplate counterTemplate = (CounterTemplate) counterTemplateService
							.findByCode(serviceUsageChargeTemplateDto
									.getCounterTemplate(), provider);
					if (counterTemplate == null) {
						throw new EntityDoesNotExistsException(
								CounterTemplate.class,
								serviceUsageChargeTemplateDto
										.getCounterTemplate());
					}

					ServiceUsageChargeTemplate serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
					serviceUsageChargeTemplate
							.setChargeTemplate(usageChargeTemplate);
					serviceUsageChargeTemplate
							.setCounterTemplate(counterTemplate);
					serviceUsageChargeTemplate.setProvider(provider);
					serviceUsageChargeTemplates.add(serviceUsageChargeTemplate);
				}
			}

			// create service template before adding serviceUsageCharge
			serviceTemplateService.create(serviceTemplate, currentUser,
					provider);

			for (ServiceUsageChargeTemplate serviceUsageChargeTemplate : serviceUsageChargeTemplates) {
				serviceUsageChargeTemplate.setServiceTemplate(serviceTemplate);
				serviceUsageChargeTemplateService.create(
						serviceUsageChargeTemplate, currentUser, provider);
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(ServiceTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();
			// check if code already exists
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(postData.getCode(), provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(
						ServiceTemplateService.class, postData.getCode());
			}

			serviceTemplate.setDescription(postData.getDescription());

			// check for recurring charges
			if (postData.getRecurringCharges() != null
					&& postData.getRecurringCharges().size() > 0) {
				List<RecurringChargeTemplate> updatedCharges = new ArrayList<RecurringChargeTemplate>();

				for (String chargeCode : postData.getRecurringCharges()) {
					RecurringChargeTemplate chargeTemplate = recurringChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTemplate == null) {
						throw new EntityDoesNotExistsException(
								RecurringChargeTemplate.class, chargeCode);
					}

					updatedCharges.add(chargeTemplate);
				}

				serviceTemplate.getRecurringCharges().clear();
				serviceTemplate.setRecurringCharges(updatedCharges);
			} else {
				serviceTemplate.getRecurringCharges().clear();
			}

			// check for subscription charges
			if (postData.getSubscriptionCharges() != null
					&& postData.getSubscriptionCharges().size() > 0) {
				List<OneShotChargeTemplate> updatedCharges = new ArrayList<OneShotChargeTemplate>();

				for (String chargeCode : postData.getSubscriptionCharges()) {
					OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTemplate == null) {
						throw new EntityDoesNotExistsException(
								OneShotChargeTemplate.class, chargeCode);
					}

					updatedCharges.add(chargeTemplate);
				}

				serviceTemplate.getSubscriptionCharges().clear();
				serviceTemplate.setSubscriptionCharges(updatedCharges);
			} else {
				serviceTemplate.getSubscriptionCharges().clear();
			}

			// check for termination charges
			if (postData.getTerminationCharges() != null
					&& postData.getTerminationCharges().size() > 0) {
				List<OneShotChargeTemplate> updatedCharges = new ArrayList<OneShotChargeTemplate>();

				for (String chargeCode : postData.getTerminationCharges()) {
					OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTemplate == null) {
						throw new EntityDoesNotExistsException(
								OneShotChargeTemplate.class, chargeCode);
					}

					updatedCharges.add(chargeTemplate);
				}

				serviceTemplate.getTerminationCharges().clear();
				serviceTemplate.setTerminationCharges(updatedCharges);
			} else {
				serviceTemplate.getTerminationCharges().clear();
			}

			if (postData.getServiceUsageCharges() != null
					&& postData.getServiceUsageCharges().size() > 0) {
				List<ServiceUsageChargeTemplate> updatedCharges = new ArrayList<ServiceUsageChargeTemplate>();

				for (ServiceUsageChargeTemplateDto serviceUsageChargeTemplateDto : postData
						.getServiceUsageCharges()) {
					// search for usageCharge
					UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService
							.findByCode(serviceUsageChargeTemplateDto
									.getUsageChargeTemplate(), provider);
					if (usageChargeTemplate == null) {
						throw new EntityDoesNotExistsException(
								UsageChargeTemplate.class,
								serviceUsageChargeTemplateDto
										.getUsageChargeTemplate());
					}

					ServiceUsageChargeTemplate serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
					serviceUsageChargeTemplate.setProvider(provider);
					serviceUsageChargeTemplate
							.setServiceTemplate(serviceTemplate);
					serviceUsageChargeTemplate
							.setChargeTemplate(usageChargeTemplate);

					// search for counter
					if (!StringUtils.isBlank(serviceUsageChargeTemplateDto
							.getCounterTemplate())) {
						CounterTemplate counterTemplate = (CounterTemplate) counterTemplateService
								.findByCode(serviceUsageChargeTemplateDto
										.getCounterTemplate(), provider);
						if (counterTemplate == null) {
							throw new EntityDoesNotExistsException(
									CounterTemplate.class,
									serviceUsageChargeTemplateDto
											.getCounterTemplate());
						}
						serviceUsageChargeTemplate
								.setCounterTemplate(counterTemplate);
					}

					updatedCharges.add(serviceUsageChargeTemplate);
				}

				if (serviceTemplate.getServiceUsageCharges() == null
						|| serviceTemplate.getServiceUsageCharges().size() == 0) {
					// add
					for (ServiceUsageChargeTemplate serviceUsageChargeTemplate : updatedCharges) {
						serviceUsageChargeTemplateService.create(
								serviceUsageChargeTemplate, currentUser,
								provider);
					}
				} else {
					// update
					List<ServiceUsageChargeTemplate> oldCharges = new ArrayList<ServiceUsageChargeTemplate>();
					oldCharges.addAll(serviceTemplate.getServiceUsageCharges());

					List<ServiceUsageChargeTemplate> newCharges = new ArrayList<ServiceUsageChargeTemplate>();
					newCharges.addAll(updatedCharges);

					// remove old charges
					Iterator<ServiceUsageChargeTemplate> a = oldCharges
							.iterator();
					while (a.hasNext()) {
						ServiceUsageChargeTemplate a1 = a.next();
						Iterator<ServiceUsageChargeTemplate> b = newCharges
								.iterator();
						while (b.hasNext()) {
							ServiceUsageChargeTemplate b1 = b.next();
							if (a1.getChargeTemplate().getCode()
									.equals(b1.getChargeTemplate().getCode())) {
								a.remove();
							}
						}
					}

					for (ServiceUsageChargeTemplate serviceUsageChargeTemplate : oldCharges) {
						serviceUsageChargeTemplateService
								.remove(serviceUsageChargeTemplate);
					}

					oldCharges = new ArrayList<ServiceUsageChargeTemplate>();
					oldCharges.addAll(serviceTemplate.getServiceUsageCharges());

					newCharges = new ArrayList<ServiceUsageChargeTemplate>();
					newCharges.addAll(updatedCharges);

					// add new charges
					Iterator<ServiceUsageChargeTemplate> x = newCharges
							.iterator();
					while (x.hasNext()) {
						ServiceUsageChargeTemplate x1 = x.next();
						Iterator<ServiceUsageChargeTemplate> y = oldCharges
								.iterator();
						while (y.hasNext()) {
							ServiceUsageChargeTemplate y1 = y.next();
							if (x1.getChargeTemplate().getCode()
									.equals(y1.getChargeTemplate().getCode())) {
								x.remove();
							}
						}
					}

					for (ServiceUsageChargeTemplate serviceUsageChargeTemplate : newCharges) {
						serviceUsageChargeTemplateService.create(
								serviceUsageChargeTemplate, currentUser,
								provider);
					}

					// update the match
					for (ServiceUsageChargeTemplate oldCharge : serviceTemplate
							.getServiceUsageCharges()) {
						for (ServiceUsageChargeTemplate newCharge : updatedCharges) {
							if (oldCharge
									.getChargeTemplate()
									.getCode()
									.equals(newCharge.getChargeTemplate()
											.getCode())) {
								oldCharge.setCounterTemplate(newCharge
										.getCounterTemplate());

								serviceUsageChargeTemplateService.update(
										oldCharge, currentUser);
							}
						}
					}
				}
			} else {
				serviceUsageChargeTemplateService.removeByServiceTemplate(
						serviceTemplate, provider);
				serviceTemplate.getServiceUsageCharges().clear();
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public ServiceTemplateDto find(String serviceTemplateCode, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(serviceTemplateCode)) {
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(serviceTemplateCode, provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(ServiceTemplate.class,
						serviceTemplateCode);
			}

			ServiceTemplateDto result = new ServiceTemplateDto(serviceTemplate);

			return result;
		} else {
			missingParameters.add("serviceTemplateCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void remove(String serviceTemplateCode, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(serviceTemplateCode)) {
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(serviceTemplateCode, provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(ServiceTemplate.class,
						serviceTemplateCode);
			}

			// remove serviceUsageCharge
			serviceUsageChargeTemplateService.removeByServiceTemplate(
					serviceTemplate, provider);

			serviceTemplateService.remove(serviceTemplate);
		} else {
			missingParameters.add("serviceTemplateCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

}
