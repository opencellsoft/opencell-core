package org.meveo.api.catalog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.OfferServiceTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateRecurringService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateSubscriptionService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateTerminationService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateUsageService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

@Stateless
public class BusinessOfferApi extends BaseApi {

	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

	@Inject
	private OfferServiceTemplateService offerServiceTemplateService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

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
	private BusinessOfferService businessOfferService;

	@Inject
	private CounterTemplateService<CounterTemplate> counterTemplateService;

	@Inject
	private OfferTemplateService offerTemplateService;

	public void createOfferFromBOM(BomOfferDto postData, User currentUser) throws MeveoApiException {
		validate(postData);
		if (!StringUtils.isBlank(postData.getBomCode())) {
			// find bom
			BusinessOfferModel businessOfferModel = businessOfferService.findByCode(postData.getBomCode(),
					currentUser.getProvider());
			if (businessOfferModel == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
			}

			// get the offer from bom
			OfferTemplate bomOffer = businessOfferModel.getOfferTemplate();
			if (bomOffer == null) {
				throw new MeveoApiException("NO_OFFER_TEMPLATE_ATTACHED");
			}

			if (bomOffer.getOfferServiceTemplates() == null || bomOffer.getOfferServiceTemplates().size() == 0) {
				throw new MeveoApiException("NO_SERVICE_TEMPLATES_ATTACHED");
			}

			// 1 create offer
			OfferTemplate newOfferTemplate = new OfferTemplate();
			newOfferTemplate.setCode(postData.getPrefix() + bomOffer.getCode());

			newOfferTemplate.setBusinessOfferModel(businessOfferModel);

			List<ServiceTemplate> newServiceTemplates = new ArrayList<>();
			// 2 create services
			if (bomOffer.getOfferServiceTemplates() != null) {
				for (OfferServiceTemplate offerServiceTemplate : bomOffer.getOfferServiceTemplates()) {
					ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
					ServiceTemplate newServiceTemplate = new ServiceTemplate();
					try {
						BeanUtils.copyProperties(newServiceTemplate, serviceTemplate);
						newServiceTemplate.setCode(postData.getPrefix() + serviceTemplate.getCode());
						newServiceTemplate.setAuditable(null);
						newServiceTemplate.setId(null);
						newServiceTemplate.clearUuid();
						newServiceTemplate.setVersion(0);
						newServiceTemplate.setServiceRecurringCharges(new ArrayList<ServiceChargeTemplateRecurring>());
						newServiceTemplate
								.setServiceTerminationCharges(new ArrayList<ServiceChargeTemplateTermination>());
						newServiceTemplate
								.setServiceSubscriptionCharges(new ArrayList<ServiceChargeTemplateSubscription>());
						newServiceTemplate.setServiceUsageCharges(new ArrayList<ServiceChargeTemplateUsage>());

						// create price plans
						if (serviceTemplate.getServiceRecurringCharges() != null
								&& serviceTemplate.getServiceRecurringCharges().size() > 0) {
							for (ServiceChargeTemplateRecurring serviceCharge : serviceTemplate
									.getServiceRecurringCharges()) {
								// create price plan
								String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
								List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCode(
										chargeTemplateCode, currentUser.getProvider());
								if (pricePlanMatrixes != null) {
									for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
										PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix();
										BeanUtils.copyProperties(newPriceplanmaMatrix, pricePlanMatrix);
										newPriceplanmaMatrix.setAuditable(null);
										newPriceplanmaMatrix.setId(null);
										newPriceplanmaMatrix.setEventCode(postData.getPrefix() + chargeTemplateCode);
										newPriceplanmaMatrix.setCode(postData.getPrefix() + pricePlanMatrix.getCode());
										newPriceplanmaMatrix.setVersion(0);
										newPriceplanmaMatrix.setOfferTemplate(null);

										pricePlanMatrixService.create(newPriceplanmaMatrix, currentUser,
												currentUser.getProvider());
									}
								}
							}
						}

						if (serviceTemplate.getServiceSubscriptionCharges() != null
								&& serviceTemplate.getServiceSubscriptionCharges().size() > 0) {
							for (ServiceChargeTemplateSubscription serviceCharge : serviceTemplate
									.getServiceSubscriptionCharges()) {
								// create price plan
								String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
								List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCode(
										chargeTemplateCode, currentUser.getProvider());
								if (pricePlanMatrixes != null) {
									for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
										PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix();
										BeanUtils.copyProperties(newPriceplanmaMatrix, pricePlanMatrix);
										newPriceplanmaMatrix.setAuditable(null);
										newPriceplanmaMatrix.setId(null);
										newPriceplanmaMatrix.setEventCode(postData.getPrefix() + chargeTemplateCode);
										newPriceplanmaMatrix.setCode(postData.getPrefix() + pricePlanMatrix.getCode());
										newPriceplanmaMatrix.setVersion(0);
										newPriceplanmaMatrix.setOfferTemplate(null);

										pricePlanMatrixService.create(newPriceplanmaMatrix, currentUser,
												currentUser.getProvider());
									}
								}
							}
						}

						if (serviceTemplate.getServiceTerminationCharges() != null
								&& serviceTemplate.getServiceTerminationCharges().size() > 0) {
							for (ServiceChargeTemplateTermination serviceCharge : serviceTemplate
									.getServiceTerminationCharges()) {
								// create price plan
								String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
								List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCode(
										chargeTemplateCode, currentUser.getProvider());
								if (pricePlanMatrixes != null) {
									for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
										PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix();
										BeanUtils.copyProperties(newPriceplanmaMatrix, pricePlanMatrix);
										newPriceplanmaMatrix.setAuditable(null);
										newPriceplanmaMatrix.setId(null);
										newPriceplanmaMatrix.setEventCode(postData.getPrefix() + chargeTemplateCode);
										newPriceplanmaMatrix.setCode(postData.getPrefix() + pricePlanMatrix.getCode());
										newPriceplanmaMatrix.setVersion(0);
										newPriceplanmaMatrix.setOfferTemplate(null);

										pricePlanMatrixService.create(newPriceplanmaMatrix, currentUser,
												currentUser.getProvider());
									}
								}
							}
						}

						if (serviceTemplate.getServiceUsageCharges() != null
								&& serviceTemplate.getServiceUsageCharges().size() > 0) {
							for (ServiceChargeTemplateUsage serviceCharge : serviceTemplate.getServiceUsageCharges()) {
								String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
								List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCode(
										chargeTemplateCode, currentUser.getProvider());
								if (pricePlanMatrixes != null) {
									for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
										PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix();
										BeanUtils.copyProperties(newPriceplanmaMatrix, pricePlanMatrix);
										newPriceplanmaMatrix.setAuditable(null);
										newPriceplanmaMatrix.setId(null);
										newPriceplanmaMatrix.setEventCode(postData.getPrefix() + chargeTemplateCode);
										newPriceplanmaMatrix.setCode(postData.getPrefix() + pricePlanMatrix.getCode());
										newPriceplanmaMatrix.setVersion(0);
										newPriceplanmaMatrix.setOfferTemplate(null);

										pricePlanMatrixService.create(newPriceplanmaMatrix, currentUser,
												currentUser.getProvider());
									}
								}
							}
						}

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
								newChargeTemplate.setCode(postData.getPrefix() + chargeTemplate.getCode());
								newChargeTemplate.clearUuid();
								newChargeTemplate.setVersion(0);
								newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
								newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
								recurringChargeTemplateService.create(newChargeTemplate, currentUser,
										currentUser.getProvider());

								ServiceChargeTemplateRecurring serviceChargeTemplate = new ServiceChargeTemplateRecurring();
								serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
								serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
								serviceChargeTemplateRecurringService.create(serviceChargeTemplate, currentUser,
										currentUser.getProvider());

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
								newChargeTemplate.setCode(postData.getPrefix() + chargeTemplate.getCode());
								newChargeTemplate.clearUuid();
								newChargeTemplate.setVersion(0);
								newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
								newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
								oneShotChargeTemplateService.create(newChargeTemplate, currentUser,
										currentUser.getProvider());

								ServiceChargeTemplateSubscription serviceChargeTemplate = new ServiceChargeTemplateSubscription();
								serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
								serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
								serviceChargeTemplateSubscriptionService.create(serviceChargeTemplate, currentUser,
										currentUser.getProvider());

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
								newChargeTemplate.setCode(postData.getPrefix() + chargeTemplate.getCode());
								newChargeTemplate.clearUuid();
								newChargeTemplate.setVersion(0);
								newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
								newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
								oneShotChargeTemplateService.create(newChargeTemplate, currentUser,
										currentUser.getProvider());

								ServiceChargeTemplateTermination serviceChargeTemplate = new ServiceChargeTemplateTermination();
								serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
								serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
								serviceChargeTemplateTerminationService.create(serviceChargeTemplate, currentUser,
										currentUser.getProvider());

								newServiceTemplate.getServiceTerminationCharges().add(serviceChargeTemplate);
							}
						}

						if (serviceTemplate.getServiceUsageCharges() != null
								&& serviceTemplate.getServiceUsageCharges().size() > 0) {
							for (ServiceChargeTemplateUsage serviceCharge : serviceTemplate.getServiceUsageCharges()) {
								UsageChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
								UsageChargeTemplate newChargeTemplate = new UsageChargeTemplate();

								BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
								newChargeTemplate.setAuditable(null);
								newChargeTemplate.setId(null);
								newChargeTemplate.setCode(postData.getPrefix() + chargeTemplate.getCode());
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
									BeanUtils.copyProperties(newCounterTemplate, serviceCharge.getCounterTemplate());
									newCounterTemplate.setAuditable(null);
									newCounterTemplate.setId(null);
									newCounterTemplate.setCode(postData.getPrefix()
											+ serviceCharge.getCounterTemplate().getCode());

									counterTemplateService.create(newCounterTemplate, currentUser,
											currentUser.getProvider());

									serviceChargeTemplate.setCounterTemplate(newCounterTemplate);
								}

								newServiceTemplate.getServiceUsageCharges().add(serviceChargeTemplate);
							}
						}

						serviceTemplateService.create(newServiceTemplate, currentUser, currentUser.getProvider());
						newServiceTemplates.add(newServiceTemplate);
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new MeveoApiException(e.getMessage());
					}
				}
			}

			// add to offer
			for (ServiceTemplate newServiceTemplate : newServiceTemplates) {
				OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
				offerServiceTemplate.setServiceTemplate(newServiceTemplate);
				offerServiceTemplate.setOfferTemplate(newOfferTemplate);
				offerServiceTemplateService.create(offerServiceTemplate, currentUser, currentUser.getProvider());

				newOfferTemplate.addOfferServiceTemplate(offerServiceTemplate);
			}

			offerTemplateService.create(newOfferTemplate, currentUser, currentUser.getProvider());

			// populate offer custom fields
			if (postData.getOfferCustomFields() != null) {
				try {
					populateCustomFields(postData.getOfferCustomFields(), newOfferTemplate, true, currentUser);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new MeveoApiException(e.getMessage());
				}
			}
		} else {
			if (StringUtils.isBlank(postData.getBomCode())) {
				missingParameters.add("bomCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
