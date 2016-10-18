package org.meveo.service.catalog.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
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
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Provider;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.GenericModuleService;
import org.meveo.service.script.offer.OfferModelScriptService;
import org.meveo.service.script.service.ServiceModelScriptService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessOfferModelService extends GenericModuleService<BusinessOfferModel> {

	@Inject
	private BusinessServiceModelService businessServiceModelService;

	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

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
	private CounterTemplateService<CounterTemplate> counterTemplateService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private ServiceModelScriptService serviceModelScriptService;

	@Inject
	private OfferModelScriptService offerModelScriptService;

	public OfferTemplate createOfferFromBOM(BusinessOfferModel businessOfferModel, List<CustomFieldDto> customFields, String code, String name, String offerDescription,
			List<ServiceConfigurationDto> serviceCodes, User currentUser) throws BusinessException {
		return createOfferFromBOM(businessOfferModel, customFields, code, name, offerDescription, serviceCodes, null, null, null, currentUser);
	}

	public OfferTemplate createOfferFromBOM(BusinessOfferModel businessOfferModel, List<CustomFieldDto> customFields, String code, String name,
			String offerDescription, List<ServiceConfigurationDto> serviceCodes, List<Channel> channels, List<BusinessAccountModel> bams,
			List<OfferTemplateCategory> offerTemplateCategories, User currentUser) throws BusinessException {
		OfferTemplate bomOffer = businessOfferModel.getOfferTemplate();
		bomOffer = offerTemplateService.refreshOrRetrieve(bomOffer);

		// 1 create offer
		OfferTemplate newOfferTemplate = new OfferTemplate();

		// check if offer already exists
		if (offerTemplateService.findByCode(code, currentUser.getProvider()) != null) {
			throw new BusinessException("" + MeveoApiErrorCodeEnum.ENTITY_ALREADY_EXISTS_EXCEPTION);
		}

		if (businessOfferModel != null && businessOfferModel.getScript() != null) {
			try {
				offerModelScriptService.beforeCreateOfferFromBOM(customFields, businessOfferModel.getScript().getCode(), currentUser);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}", businessOfferModel.getScript().getCode(), e);
			}
		}
		
		newOfferTemplate.setCode(code);		
		
		newOfferTemplate.setDescription(offerDescription);
		if (StringUtils.isBlank(name)) {
			newOfferTemplate.setName(bomOffer.getName());
		} else {
			newOfferTemplate.setName(name);
		}
		newOfferTemplate.setValidFrom(bomOffer.getValidFrom());
		newOfferTemplate.setValidTo(bomOffer.getValidTo());
		newOfferTemplate.setBusinessOfferModel(businessOfferModel);
		newOfferTemplate.setImage(bomOffer.getImage());
		if (bomOffer.getAttachments() != null) {
			newOfferTemplate.getAttachments().addAll(bomOffer.getAttachments());
		}
		if (offerTemplateCategories != null) {
			newOfferTemplate.getOfferTemplateCategories().addAll(offerTemplateCategories);
		}
		if (channels != null) {
			newOfferTemplate.getChannels().addAll(channels);
		}
		if (bams != null) {
			newOfferTemplate.getBusinessAccountModels().addAll(bams);
		}
		newOfferTemplate.setActive(true);
		newOfferTemplate.setLifeCycleStatus(LifeCycleStatusEnum.IN_DESIGN);

		offerTemplateService.create(newOfferTemplate, currentUser);

		String prefix = newOfferTemplate.getId() + "_";

		List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();
		// 2 create services
		if (bomOffer.getOfferServiceTemplates() != null) {
			// check if service template exists
			if (serviceCodes != null && serviceCodes.size() > 0) {
				boolean serviceFound = false;
				for (ServiceConfigurationDto serviceCodeDto : serviceCodes) {
					String serviceCode = serviceCodeDto.getCode();

					for (OfferServiceTemplate offerServiceTemplate : bomOffer.getOfferServiceTemplates()) {
						ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
						if (serviceCode.equals(serviceTemplate.getCode())) {
							serviceFound = true;
							break;
						}
					}

					if (!serviceFound) {
						throw new BusinessException("Service " + serviceCode + " is not defined in the offer");
					}
				}
			}

			List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
			List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();
			for (OfferServiceTemplate offerServiceTemplate : bomOffer.getOfferServiceTemplates()) {
				ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(offerServiceTemplate.getServiceTemplate().getCode(), currentUser.getProvider());

				boolean serviceFound = false;
				ServiceConfigurationDto serviceConfigurationDto = new ServiceConfigurationDto();
				for (ServiceConfigurationDto tempServiceCodeDto : serviceCodes) {
					String serviceCode = tempServiceCodeDto.getCode();
					if (serviceCode.equals(serviceTemplate.getCode())) {
						serviceConfigurationDto = tempServiceCodeDto;
						serviceFound = true;
					}
				}
				if (!serviceFound) {
					continue;
				}

				// get the BSM from BOM
				BusinessServiceModel bsm = null;
				for (MeveoModuleItem item : businessOfferModel.getModuleItems()) {
					if (item.getItemClass().equals(BusinessServiceModel.class.getName())) {
						bsm = businessServiceModelService.findByCode(item.getItemCode(), currentUser.getProvider());
						if (bsm.getServiceTemplate().equals(serviceTemplate)) {
							break;
						}
					}
				}

				if (bsm != null && bsm.getScript() != null) {
					try {
						serviceModelScriptService.beforeCreateServiceFromBSM(serviceConfigurationDto.getCustomFields(), bsm.getScript().getCode(), currentUser);
					} catch (BusinessException e) {
						log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
					}
				}

				OfferServiceTemplate newOfferServiceTemplate = new OfferServiceTemplate();
				newOfferServiceTemplate.setMandatory(serviceConfigurationDto.isMandatory());
				if (offerServiceTemplate.getIncompatibleServices() != null) {
					newOfferServiceTemplate.getIncompatibleServices().addAll(offerServiceTemplate.getIncompatibleServices());
				}
				ServiceTemplate newServiceTemplate = new ServiceTemplate();

				try {
					BeanUtils.copyProperties(newServiceTemplate, serviceTemplate);
					newServiceTemplate.setCode(prefix + serviceTemplate.getCode());
					newServiceTemplate.setDescription(serviceConfigurationDto.getDescription());
					newServiceTemplate.setBusinessServiceModel(bsm);
					newServiceTemplate.setAuditable(null);
					newServiceTemplate.setId(null);
					newServiceTemplate.clearUuid();
					newServiceTemplate.setVersion(0);
					newServiceTemplate.setServiceRecurringCharges(new ArrayList<ServiceChargeTemplateRecurring>());
					newServiceTemplate.setServiceTerminationCharges(new ArrayList<ServiceChargeTemplateTermination>());
					newServiceTemplate.setServiceSubscriptionCharges(new ArrayList<ServiceChargeTemplateSubscription>());
					newServiceTemplate.setServiceUsageCharges(new ArrayList<ServiceChargeTemplateUsage>());
					newOfferServiceTemplate.setServiceTemplate(newServiceTemplate);

					serviceTemplateService.create(newOfferServiceTemplate.getServiceTemplate(), currentUser);

					// create price plans
					if (serviceTemplate.getServiceRecurringCharges() != null && serviceTemplate.getServiceRecurringCharges().size() > 0) {
						for (ServiceChargeTemplateRecurring serviceCharge : serviceTemplate.getServiceRecurringCharges()) {
							// create price plan
							String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
							List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCode(chargeTemplateCode, currentUser.getProvider());
							if (pricePlanMatrixes != null) {
								for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
									String ppCode = prefix + pricePlanMatrix.getCode();
									PricePlanMatrix ppMatrix = pricePlanMatrixService.findByCode(ppCode, currentUser.getProvider());
									if (ppMatrix != null) {
										continue;
									}

									PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix();
									BeanUtils.copyProperties(newPriceplanmaMatrix, pricePlanMatrix);
									newPriceplanmaMatrix.setAuditable(null);
									newPriceplanmaMatrix.setId(null);
									newPriceplanmaMatrix.setEventCode(prefix + chargeTemplateCode);
									newPriceplanmaMatrix.setCode(ppCode);
									newPriceplanmaMatrix.setVersion(0);
									newPriceplanmaMatrix.setOfferTemplate(null);

									if (pricePlansInMemory.contains(newPriceplanmaMatrix)) {
										continue;
									} else {
										pricePlansInMemory.add(newPriceplanmaMatrix);
									}

									pricePlanMatrixService.create(newPriceplanmaMatrix, currentUser);
								}
							}
						}
					}

					if (serviceTemplate.getServiceSubscriptionCharges() != null && serviceTemplate.getServiceSubscriptionCharges().size() > 0) {
						for (ServiceChargeTemplateSubscription serviceCharge : serviceTemplate.getServiceSubscriptionCharges()) {
							// create price plan
							String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
							List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCode(chargeTemplateCode, currentUser.getProvider());
							if (pricePlanMatrixes != null) {
								for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
									String ppCode = prefix + pricePlanMatrix.getCode();
									if (pricePlanMatrixService.findByCode(ppCode, currentUser.getProvider()) != null) {
										continue;
									}

									PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix();
									BeanUtils.copyProperties(newPriceplanmaMatrix, pricePlanMatrix);
									newPriceplanmaMatrix.setAuditable(null);
									newPriceplanmaMatrix.setId(null);
									newPriceplanmaMatrix.setEventCode(prefix + chargeTemplateCode);
									newPriceplanmaMatrix.setCode(ppCode);
									newPriceplanmaMatrix.setVersion(0);
									newPriceplanmaMatrix.setOfferTemplate(null);

									if (pricePlansInMemory.contains(newPriceplanmaMatrix)) {
										continue;
									} else {
										pricePlansInMemory.add(newPriceplanmaMatrix);
									}

									pricePlanMatrixService.create(newPriceplanmaMatrix, currentUser);
								}
							}
						}
					}

					if (serviceTemplate.getServiceTerminationCharges() != null && serviceTemplate.getServiceTerminationCharges().size() > 0) {
						for (ServiceChargeTemplateTermination serviceCharge : serviceTemplate.getServiceTerminationCharges()) {
							// create price plan
							String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
							List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCode(chargeTemplateCode, currentUser.getProvider());
							if (pricePlanMatrixes != null) {
								for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
									String ppCode = prefix + pricePlanMatrix.getCode();
									if (pricePlanMatrixService.findByCode(ppCode, currentUser.getProvider()) != null) {
										continue;
									}

									PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix();
									BeanUtils.copyProperties(newPriceplanmaMatrix, pricePlanMatrix);
									newPriceplanmaMatrix.setAuditable(null);
									newPriceplanmaMatrix.setId(null);
									newPriceplanmaMatrix.setEventCode(prefix + chargeTemplateCode);
									newPriceplanmaMatrix.setCode(ppCode);
									newPriceplanmaMatrix.setVersion(0);
									newPriceplanmaMatrix.setOfferTemplate(null);

									if (pricePlansInMemory.contains(newPriceplanmaMatrix)) {
										continue;
									} else {
										pricePlansInMemory.add(newPriceplanmaMatrix);
									}

									pricePlanMatrixService.create(newPriceplanmaMatrix, currentUser);
								}
							}
						}
					}

					if (serviceTemplate.getServiceUsageCharges() != null && serviceTemplate.getServiceUsageCharges().size() > 0) {
						for (ServiceChargeTemplateUsage serviceCharge : serviceTemplate.getServiceUsageCharges()) {
							String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
							List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCode(chargeTemplateCode, currentUser.getProvider());
							if (pricePlanMatrixes != null) {
								for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
									String ppCode = prefix + pricePlanMatrix.getCode();
									if (pricePlanMatrixService.findByCode(ppCode, currentUser.getProvider()) != null) {
										continue;
									}

									PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix();
									BeanUtils.copyProperties(newPriceplanmaMatrix, pricePlanMatrix);
									newPriceplanmaMatrix.setAuditable(null);
									newPriceplanmaMatrix.setId(null);
									newPriceplanmaMatrix.setEventCode(prefix + chargeTemplateCode);
									newPriceplanmaMatrix.setCode(ppCode);
									newPriceplanmaMatrix.setVersion(0);
									newPriceplanmaMatrix.setOfferTemplate(null);

									if (pricePlansInMemory.contains(newPriceplanmaMatrix)) {
										continue;
									} else {
										pricePlansInMemory.add(newPriceplanmaMatrix);
									}

									pricePlanMatrixService.create(newPriceplanmaMatrix, currentUser);
								}
							}
						}
					}

					// get charges
					if (serviceTemplate.getServiceRecurringCharges() != null && serviceTemplate.getServiceRecurringCharges().size() > 0) {
						for (ServiceChargeTemplateRecurring serviceCharge : serviceTemplate.getServiceRecurringCharges()) {
							RecurringChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
							RecurringChargeTemplate newChargeTemplate = new RecurringChargeTemplate();

							BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
							newChargeTemplate.setAuditable(null);
							newChargeTemplate.setId(null);
							newChargeTemplate.setCode(prefix + chargeTemplate.getCode());
							newChargeTemplate.clearUuid();
							newChargeTemplate.setVersion(0);
							newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
							newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());

							if (chargeTemplateInMemory.contains(newChargeTemplate)) {
								continue;
							} else {
								chargeTemplateInMemory.add(newChargeTemplate);
							}

							recurringChargeTemplateService.create(newChargeTemplate, currentUser);

							ServiceChargeTemplateRecurring serviceChargeTemplate = new ServiceChargeTemplateRecurring();
							serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
							serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
							if (serviceCharge.getWalletTemplates() != null) {
								serviceChargeTemplate.setWalletTemplates(new ArrayList<WalletTemplate>());
								serviceChargeTemplate.getWalletTemplates().addAll(serviceCharge.getWalletTemplates());
							}
							serviceChargeTemplateRecurringService.create(serviceChargeTemplate, currentUser);

							newServiceTemplate.getServiceRecurringCharges().add(serviceChargeTemplate);
						}
					}

					if (serviceTemplate.getServiceSubscriptionCharges() != null && serviceTemplate.getServiceSubscriptionCharges().size() > 0) {
						for (ServiceChargeTemplateSubscription serviceCharge : serviceTemplate.getServiceSubscriptionCharges()) {
							OneShotChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
							OneShotChargeTemplate newChargeTemplate = new OneShotChargeTemplate();

							BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
							newChargeTemplate.setAuditable(null);
							newChargeTemplate.setId(null);
							newChargeTemplate.setCode(prefix + chargeTemplate.getCode());
							newChargeTemplate.clearUuid();
							newChargeTemplate.setVersion(0);
							newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
							newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());

							if (chargeTemplateInMemory.contains(newChargeTemplate)) {
								continue;
							} else {
								chargeTemplateInMemory.add(newChargeTemplate);
							}

							oneShotChargeTemplateService.create(newChargeTemplate, currentUser);

							ServiceChargeTemplateSubscription serviceChargeTemplate = new ServiceChargeTemplateSubscription();
							serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
							serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
							if (serviceCharge.getWalletTemplates() != null) {
								serviceChargeTemplate.setWalletTemplates(new ArrayList<WalletTemplate>());
								serviceChargeTemplate.getWalletTemplates().addAll(serviceCharge.getWalletTemplates());
							}
							serviceChargeTemplateSubscriptionService.create(serviceChargeTemplate, currentUser);

							newServiceTemplate.getServiceSubscriptionCharges().add(serviceChargeTemplate);
						}
					}

					if (serviceTemplate.getServiceTerminationCharges() != null && serviceTemplate.getServiceTerminationCharges().size() > 0) {
						for (ServiceChargeTemplateTermination serviceCharge : serviceTemplate.getServiceTerminationCharges()) {
							OneShotChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
							OneShotChargeTemplate newChargeTemplate = new OneShotChargeTemplate();

							BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
							newChargeTemplate.setAuditable(null);
							newChargeTemplate.setId(null);
							newChargeTemplate.setCode(prefix + chargeTemplate.getCode());
							newChargeTemplate.clearUuid();
							newChargeTemplate.setVersion(0);
							newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
							newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());

							if (chargeTemplateInMemory.contains(newChargeTemplate)) {
								continue;
							} else {
								chargeTemplateInMemory.add(newChargeTemplate);
							}

							oneShotChargeTemplateService.create(newChargeTemplate, currentUser);

							ServiceChargeTemplateTermination serviceChargeTemplate = new ServiceChargeTemplateTermination();
							serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
							serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
							if (serviceCharge.getWalletTemplates() != null) {
								serviceChargeTemplate.setWalletTemplates(new ArrayList<WalletTemplate>());
								serviceChargeTemplate.getWalletTemplates().addAll(serviceCharge.getWalletTemplates());
							}
							serviceChargeTemplateTerminationService.create(serviceChargeTemplate, currentUser);

							newServiceTemplate.getServiceTerminationCharges().add(serviceChargeTemplate);
						}
					}

					if (serviceTemplate.getServiceUsageCharges() != null && serviceTemplate.getServiceUsageCharges().size() > 0) {
						for (ServiceChargeTemplateUsage serviceCharge : serviceTemplate.getServiceUsageCharges()) {
							UsageChargeTemplate chargeTemplate = serviceCharge.getChargeTemplate();
							UsageChargeTemplate newChargeTemplate = new UsageChargeTemplate();

							BeanUtils.copyProperties(newChargeTemplate, chargeTemplate);
							newChargeTemplate.setAuditable(null);
							newChargeTemplate.setId(null);
							newChargeTemplate.setCode(prefix + chargeTemplate.getCode());
							newChargeTemplate.clearUuid();
							newChargeTemplate.setVersion(0);
							newChargeTemplate.setChargeInstances(new ArrayList<ChargeInstance>());
							newChargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());

							if (chargeTemplateInMemory.contains(newChargeTemplate)) {
								continue;
							} else {
								chargeTemplateInMemory.add(newChargeTemplate);
							}

							usageChargeTemplateService.create(newChargeTemplate, currentUser);

							ServiceChargeTemplateUsage serviceChargeTemplate = new ServiceChargeTemplateUsage();
							serviceChargeTemplate.setChargeTemplate(newChargeTemplate);
							serviceChargeTemplate.setServiceTemplate(newServiceTemplate);
							if (serviceCharge.getWalletTemplates() != null) {
								serviceChargeTemplate.setWalletTemplates(new ArrayList<WalletTemplate>());
								serviceChargeTemplate.getWalletTemplates().addAll(serviceCharge.getWalletTemplates());
							}
							serviceChargeTemplateUsageService.create(serviceChargeTemplate, currentUser);

							if (serviceCharge.getCounterTemplate() != null) {
								CounterTemplate newCounterTemplate = new CounterTemplate();
								BeanUtils.copyProperties(newCounterTemplate, serviceCharge.getCounterTemplate());
								newCounterTemplate.setAuditable(null);
								newCounterTemplate.setId(null);
								newCounterTemplate.setCode(prefix + serviceCharge.getCounterTemplate().getCode());

								counterTemplateService.create(newCounterTemplate, currentUser);

								serviceChargeTemplate.setCounterTemplate(newCounterTemplate);
							}

							newServiceTemplate.getServiceUsageCharges().add(serviceChargeTemplate);
						}
					}

					if (bsm != null && bsm.getScript() != null) {
						try {
							serviceModelScriptService.afterCreateServiceFromBSM(newServiceTemplate, serviceConfigurationDto.getCustomFields(), bsm.getScript().getCode(), currentUser);
						} catch (BusinessException e) {
							log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
						}
					}

					newOfferServiceTemplate.setServiceTemplate(newServiceTemplate);
					newOfferServiceTemplates.add(newOfferServiceTemplate);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new BusinessException(e.getMessage());
				}
			}
		}

		// add to offer
		for (OfferServiceTemplate newOfferServiceTemplate : newOfferServiceTemplates) {
			newOfferServiceTemplate.setOfferTemplate(newOfferTemplate);
			newOfferTemplate.addOfferServiceTemplate(newOfferServiceTemplate);
		}

		offerTemplateService.update(newOfferTemplate, currentUser);
		
		if (newOfferTemplate.getBusinessOfferModel() != null && newOfferTemplate.getBusinessOfferModel().getScript() != null) {
			try {
				offerModelScriptService.afterCreateOfferFromBOM(newOfferTemplate, customFields, newOfferTemplate.getBusinessOfferModel().getScript().getCode(), currentUser);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}", newOfferTemplate.getBusinessOfferModel().getScript().getCode(), e);
			}
		}

		// save the cf

		return newOfferTemplate;
	}
	
	@SuppressWarnings("unchecked")
	public List<BusinessOfferModel> listInstalled(Provider provider) {
		QueryBuilder qb = new QueryBuilder(BusinessOfferModel.class, "b", null, null, provider);
		qb.addCriterion("installed", "=", true, true);
		try {
			return (List<BusinessOfferModel>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
}