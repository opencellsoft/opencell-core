package org.meveo.api.catalog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.bom.BOMEntity;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.service.catalog.impl.BOMEntityService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateRecurringService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateSubscriptionService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateTerminationService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateUsageService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessOfferApi extends BaseApi {

	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Inject
	private ServiceChargeTemplateSubscriptionService serviceChargeTemplateSubscriptionService;

	@Inject
	private ServiceChargeTemplateTerminationService serviceChargeTemplateTerminationService;

	@Inject
	private ServiceChargeTemplateRecurringService serviceChargeTemplateRecurringService;

	@Inject
	private ServiceChargeTemplateUsageService serviceChargeTemplateUsageService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private BOMEntityService bomEntityService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private CounterTemplateService<CounterTemplate> counterTemplateService;

	@Inject
	private OfferTemplateService offerTemplateService;

	public void createOfferFromBOM(BomOfferDto postData, User currentUser) throws MeveoApiException {
		validate(postData);

		if (!StringUtils.isBlank(postData.getBomCode()) && postData.getServicesToActivate() != null
				&& postData.getServicesToActivate().size() != 0) {
			// find bom
			BOMEntity bomEntity = bomEntityService.findByCode(postData.getBomCode(), currentUser.getProvider());
			if (bomEntity == null) {
				throw new EntityDoesNotExistsException(BOMEntity.class, postData.getBomCode());
			}

			// get the offer from bom
			OfferTemplate bomOffer = bomEntity.getOfferTemplate();
			if (bomOffer.getServiceTemplates() == null || bomOffer.getServiceTemplates().size() == 0) {
				throw new MeveoApiException("NO_SERVICE_TEMPLATES");
			}

			OfferTemplate newOfferTemplate = new OfferTemplate();
			newOfferTemplate.setCode(postData.getOfferCode());
			newOfferTemplate.setBomCode(postData.getBomCode());

			// get the services from bom offer
			if (bomOffer.getServiceTemplates() != null) {
				for (ServiceTemplate serviceTemplate : bomOffer.getServiceTemplates()) {
					// check if service is in parameter
					for (String stCode : postData.getServicesToActivate()) {
						if (serviceTemplate.getCode().equalsIgnoreCase(stCode)) {
							ServiceTemplate newServiceTemplate = new ServiceTemplate();
							try {
								BeanUtils.copyProperties(newServiceTemplate, serviceTemplate);
								newServiceTemplate.setCode(postData.getServiceCodePrefix() + serviceTemplate.getCode());
								newServiceTemplate.setAuditable(null);
								newServiceTemplate.setId(null);
								newServiceTemplate.clearUuid();
								newServiceTemplate.setVersion(0);
								newServiceTemplate
										.setServiceRecurringCharges(new ArrayList<ServiceChargeTemplateRecurring>());
								newServiceTemplate
										.setServiceTerminationCharges(new ArrayList<ServiceChargeTemplateTermination>());
								newServiceTemplate
										.setServiceSubscriptionCharges(new ArrayList<ServiceChargeTemplateSubscription>());
								newServiceTemplate.setServiceUsageCharges(new ArrayList<ServiceChargeTemplateUsage>());

								// get charges
								if (serviceTemplate.getServiceRecurringCharges() != null
										&& serviceTemplate.getServiceRecurringCharges().size() > 0) {
									for (ServiceChargeTemplateRecurring serviceCharge : serviceTemplate
											.getServiceRecurringCharges()) {
										RecurringChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
										RecurringChargeTemplate newChargeTemplate = new RecurringChargeTemplate();

										BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
										newChargeTemplate.setAuditable(null);
										newChargeTemplate.setId(null);
										newChargeTemplate.setCode(postData.getServiceCodePrefix()
												+ chargeTemplate.getCode());
										newChargeTemplate.clearUuid();
										newChargeTemplate.setVersion(0);
										newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
										newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
										recurringChargeTemplateService.create(newChargeTemplate, currentUser,
												currentUser.getProvider());

										ServiceChargeTemplateRecurring serviceChargeTemplate = new ServiceChargeTemplateRecurring();
										serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
										serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
										serviceChargeTemplateRecurringService.create(serviceChargeTemplate,
												currentUser, currentUser.getProvider());

										newServiceTemplate.getServiceRecurringCharges().add(serviceChargeTemplate);
									}
								}
								if (serviceTemplate.getServiceSubscriptionCharges() != null
										&& serviceTemplate.getServiceSubscriptionCharges().size() > 0) {
									for (ServiceChargeTemplateSubscription serviceCharge : serviceTemplate
											.getServiceSubscriptionCharges()) {
										OneShotChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
										OneShotChargeTemplate newChargeTemplate = new OneShotChargeTemplate();

										BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
										newChargeTemplate.setAuditable(null);
										newChargeTemplate.setId(null);
										newChargeTemplate.setCode(postData.getServiceCodePrefix()
												+ chargeTemplate.getCode());
										newChargeTemplate.clearUuid();
										newChargeTemplate.setVersion(0);
										newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
										newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
										oneShotChargeTemplateService.create(newChargeTemplate, currentUser,
												currentUser.getProvider());

										ServiceChargeTemplateSubscription serviceChargeTemplate = new ServiceChargeTemplateSubscription();
										serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
										serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
										serviceChargeTemplateSubscriptionService.create(serviceChargeTemplate,
												currentUser, currentUser.getProvider());

										newServiceTemplate.getServiceSubscriptionCharges().add(serviceChargeTemplate);
									}
								}
								if (serviceTemplate.getServiceTerminationCharges() != null
										&& serviceTemplate.getServiceTerminationCharges().size() > 0) {
									for (ServiceChargeTemplateTermination serviceCharge : serviceTemplate
											.getServiceTerminationCharges()) {
										OneShotChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
										OneShotChargeTemplate newChargeTemplate = new OneShotChargeTemplate();

										BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
										newChargeTemplate.setAuditable(null);
										newChargeTemplate.setId(null);
										newChargeTemplate.setCode(postData.getServiceCodePrefix()
												+ chargeTemplate.getCode());
										newChargeTemplate.clearUuid();
										newChargeTemplate.setVersion(0);
										newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
										newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
										oneShotChargeTemplateService.create(newChargeTemplate, currentUser,
												currentUser.getProvider());

										ServiceChargeTemplateTermination serviceChargeTemplate = new ServiceChargeTemplateTermination();
										serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
										serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
										serviceChargeTemplateTerminationService.create(serviceChargeTemplate,
												currentUser, currentUser.getProvider());

										newServiceTemplate.getServiceTerminationCharges().add(serviceChargeTemplate);
									}
								}
								if (serviceTemplate.getServiceUsageCharges() != null
										&& serviceTemplate.getServiceUsageCharges().size() > 0) {
									for (ServiceChargeTemplateUsage serviceCharge : serviceTemplate
											.getServiceUsageCharges()) {
										UsageChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
										UsageChargeTemplate newChargeTemplate = new UsageChargeTemplate();

										BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
										newChargeTemplate.setAuditable(null);
										newChargeTemplate.setId(null);
										newChargeTemplate.setCode(postData.getServiceCodePrefix()
												+ chargeTemplate.getCode());
										newChargeTemplate.clearUuid();
										newChargeTemplate.setVersion(0);
										newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
										newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
										usageChargeTemplateService.create(newChargeTemplate, currentUser,
												currentUser.getProvider());

										ServiceChargeTemplateUsage serviceChargeTemplate = new ServiceChargeTemplateUsage();
										serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
										serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
										serviceChargeTemplateUsageService.create(serviceChargeTemplate, currentUser,
												currentUser.getProvider());

										if (serviceCharge.getCounterTemplate() != null) {
											CounterTemplate newCounterTemplate = new CounterTemplate();
											BeanUtils.copyProperties(newCounterTemplate,
													serviceCharge.getCounterTemplate());
											newCounterTemplate.setAuditable(null);
											newCounterTemplate.setId(null);
											newCounterTemplate.setCode(postData.getServiceCodePrefix()
													+ serviceCharge.getCounterTemplate().getCode());

											counterTemplateService.create(newCounterTemplate, currentUser,
													currentUser.getProvider());

											serviceChargeTemplate.setCounterTemplate(newCounterTemplate);
										}

										newServiceTemplate.getServiceUsageCharges().add(serviceChargeTemplate);
									}
								}

								// add to offer
								if (newOfferTemplate.getServiceTemplates() == null) {
									serviceTemplateService.create(newServiceTemplate, currentUser,
											currentUser.getProvider());
									newOfferTemplate.addServiceTemplate(newServiceTemplate);
								} else if (!newOfferTemplate.getServiceTemplates().contains(newServiceTemplate)) {
									serviceTemplateService.create(newServiceTemplate, currentUser,
											currentUser.getProvider());
									newOfferTemplate.addServiceTemplate(newServiceTemplate);
								}

								// process custom fields
								if (postData.getServiceCFVs() != null) {
									Set<String> keys = postData.getServiceCFVs().keySet();
									Iterator<String> it = keys.iterator();
									while (it.hasNext()) {
										String key = it.next();
										if (serviceTemplate.getCode().equalsIgnoreCase(key)) {
											CustomFieldsDto customFieldsDto = postData.getServiceCFVs().get(key);
											populateCustomFields(customFieldsDto, newServiceTemplate, false,
													currentUser);
											break;
										}
									}
								}

							} catch (IllegalAccessException | InvocationTargetException e) {
								throw new MeveoApiException(e.getMessage());
							}
							break;
						}
					}
				}

				offerTemplateService.create(newOfferTemplate, currentUser, currentUser.getProvider());

				// execute bom scripts
				if (bomEntity.getCreationScript() != null) {
					Map<String, Object> creationScriptContext = new HashMap<>();
					creationScriptContext.put("prefix", postData.getServiceCodePrefix());

					scriptInstanceService.execute(currentUser.getProvider(), bomEntity.getCreationScript().getCode(),
							creationScriptContext, currentUser);
				}
			}
		} else {
			if (StringUtils.isBlank(postData.getBomCode())) {
				missingParameters.add("bomCode");
			}
			if (postData.getServicesToActivate() == null || postData.getServicesToActivate().size() == 0) {
				missingParameters.add("servicesToActivate");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
