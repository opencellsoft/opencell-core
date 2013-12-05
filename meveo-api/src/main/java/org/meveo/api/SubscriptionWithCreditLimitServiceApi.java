package org.meveo.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.api.dto.CreditLimitDto;
import org.meveo.api.dto.ServiceToAddDto;
import org.meveo.api.dto.SubscriptionWithCreditLimitDto;
import org.meveo.api.exception.BillingAccountDoesNotExistsException;
import org.meveo.api.exception.CreditLimitExceededException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.OfferTemplateDoesNotExistsException;
import org.meveo.api.exception.ParentSellerDoesNotExistsException;
import org.meveo.api.exception.SellerDoesNotExistsException;
import org.meveo.api.exception.ServiceTemplateDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.RealtimeChargingService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SubscriptionWithCreditLimitServiceApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private SellerService sellerService;

	@Inject
	private RealtimeChargingService realtimeChargingService;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private UserAccountService userAccountService;

	public void create(
			SubscriptionWithCreditLimitDto subscriptionWithCreditLimitDto)
			throws MeveoApiException, BusinessException {

		if (!StringUtils.isBlank(subscriptionWithCreditLimitDto.getUserId())
				&& !StringUtils.isBlank(subscriptionWithCreditLimitDto
						.getOrganizationId())
				&& !StringUtils.isBlank(subscriptionWithCreditLimitDto
						.getOfferId())
				&& subscriptionWithCreditLimitDto.getServicesToAdd() != null
				&& subscriptionWithCreditLimitDto.getServicesToAdd().size() > 0) {

			Provider provider = providerService
					.findById(subscriptionWithCreditLimitDto.getProviderId());
			User currentUser = userService
					.findById(subscriptionWithCreditLimitDto.getCurrentUserId());

			String offerTemplateCode = paramBean.getProperty(
					"asg.api.offer.offer.prefix", "_OF_")
					+ subscriptionWithCreditLimitDto.getOfferId();
			OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
					offerTemplateCode, provider);

			if (offerTemplate == null) {
				throw new OfferTemplateDoesNotExistsException(offerTemplateCode);
			}

			Seller seller = sellerService.findByCode(em,
					subscriptionWithCreditLimitDto.getOrganizationId(),
					provider);
			if (seller == null) {
				throw new SellerDoesNotExistsException(
						subscriptionWithCreditLimitDto.getOrganizationId());
			}

			if (seller.getSeller() == null) {
				throw new ParentSellerDoesNotExistsException(
						subscriptionWithCreditLimitDto.getOrganizationId());
			} else {
				Seller parentSeller = seller.getSeller();
				String sellerId = parentSeller.getCode();

				ForSubscription forSubscription = new ForSubscription();
				forSubscription.setSeller(seller);

				// We look if this SELLER has a "charged offer service"
				// associated
				// at the offer with the code begining with
				// "_CH_OF_["OfferId"]_[SellerID] (we use
				// asg.api.offer.charged.prefix).
				// In the case the offer to select for this seller is too
				// "_OF_["OfferId"]"
				// chargedOffer + offerCode + sellerId
				ServiceTemplate chargedServiceTemplate = serviceTemplateService
						.findByCode(
								em,
								paramBean.getProperty(
										"asg.api.offer.charged.prefix",
										"_CH_OF_")
										+ subscriptionWithCreditLimitDto
												.getOfferId() + "_" + sellerId,
								provider);
				if (chargedServiceTemplate != null) {
					forSubscription.getOfferTemplateForSubscription()
							.setOfferTemplate(offerTemplate);
					forSubscription
							.getOfferTemplateForSubscription()
							.getServiceTemplatesForsuForSubscriptions()
							.add(new ServiceTemplateForSubscription(
									chargedServiceTemplate));
					forSubscription.setChargedOffer(true);
				} else {
					// It's not the case we have to take the offers linked at
					// each
					// service "_SE_["ServiceId"]"
					// (asg.api.service.offer.prefix).
					forSubscription.setChargedOffer(false);
					for (ServiceToAddDto serviceToAddDto : subscriptionWithCreditLimitDto
							.getServicesToAdd()) {
						// find offer
						String tempOfferTemplateCode = paramBean.getProperty(
								"asg.api.service.offer.prefix", "_SE_")
								+ serviceToAddDto.getServiceId();
						OfferTemplate tempOfferTemplate = offerTemplateService
								.findByCode(em, tempOfferTemplateCode, provider);
						if (tempOfferTemplate == null) {
							throw new OfferTemplateDoesNotExistsException(
									tempOfferTemplateCode);
						}

						// find service template
						String tempChargedServiceTemplateCode = paramBean
								.getProperty("asg.api.service.charged.prefix",
										"_CH_SE_")
								+ serviceToAddDto.getServiceId()
								+ "_"
								+ sellerId;
						ServiceTemplate tempChargedServiceTemplate = serviceTemplateService
								.findByCode(em, tempChargedServiceTemplateCode,
										provider);
						if (tempChargedServiceTemplate == null) {
							throw new ServiceTemplateDoesNotExistsException(
									tempChargedServiceTemplateCode);
						}
						forSubscription.getOfferTemplateForSubscription()
								.setOfferTemplate(tempOfferTemplate);
						forSubscription
								.getOfferTemplateForSubscription()
								.getServiceTemplatesForsuForSubscriptions()
								.add(new ServiceTemplateForSubscription(
										chargedServiceTemplate, serviceToAddDto));
					}
				}

				// processParent
				ForSubscription finalForSubscription = processParent(
						forSubscription, parentSeller,
						subscriptionWithCreditLimitDto.getOfferId(), provider,
						subscriptionWithCreditLimitDto.getServicesToAdd());

				// validate credit limit
				if (!validateCreditLimit(finalForSubscription,
						subscriptionWithCreditLimitDto, provider)) {
					throw new CreditLimitExceededException();
				}

				// check offer and service template association
				updateOfferAndServiceTemplateAssociation(forSubscription,
						currentUser);

				createSubscription(finalForSubscription,
						subscriptionWithCreditLimitDto, currentUser, provider);

			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(subscriptionWithCreditLimitDto.getUserId())) {
				missingFields.add("userId");
			}
			if (StringUtils.isBlank(subscriptionWithCreditLimitDto
					.getOrganizationId())) {
				missingFields.add("organizationId");
			}
			if (StringUtils
					.isBlank(subscriptionWithCreditLimitDto.getOfferId())) {
				missingFields.add("offerId");
			}
			if (subscriptionWithCreditLimitDto.getServicesToAdd() == null
					|| subscriptionWithCreditLimitDto.getServicesToAdd().size() == 0) {
				missingFields.add("servicesToAdd");
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

	private void createSubscription(ForSubscription forSubscription,
			SubscriptionWithCreditLimitDto subscriptionWithCreditLimitDto,
			User currentUser, Provider provider)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {

		// subscription
		while (forSubscription != null) {
			// get userAccount
			UserAccount userAccount = null;
			String userAccountCode = null;

			if (forSubscription.hasChild()) {
				userAccountCode = paramBean.getProperty(
						"asg.api.default.organization.userAccount", "USER_")
						+ forSubscription.getChild().getSeller().getCode();
			} else {
				userAccountCode = paramBean.getProperty("asg.api.default",
						"_DEF_") + forSubscription.getSeller().getCode();
			}
			userAccount = userAccountService.findByCode(em, userAccountCode,
					provider);

			Subscription subscription = new Subscription();
			subscription.setOffer(forSubscription
					.getOfferTemplateForSubscription().getOfferTemplate());
			subscription.setCode(forSubscription.getSeller().getCode()); // ?
			subscription.setDescription("");
			subscription.setSubscriptionDate(subscriptionWithCreditLimitDto
					.getSubscriptionDate());
			subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
			subscription.setUserAccount(userAccount);

			subscriptionService.create(em, subscription, currentUser, provider);

			try {
				for (ServiceTemplateForSubscription serviceTemplateForSubscription : forSubscription
						.getOfferTemplateForSubscription()
						.getServiceTemplatesForsuForSubscriptions()) {
					ServiceTemplate serviceTemplate = serviceTemplateForSubscription
							.getServiceTemplate();

					ServiceInstance serviceInstance = new ServiceInstance();
					serviceInstance.setCode(serviceTemplate.getCode());
					serviceInstance.setDescription(serviceTemplate
							.getDescription());
					serviceInstance.setServiceTemplate(serviceTemplate);
					serviceInstance.setSubscription(subscription);
					serviceInstance.setProvider(provider);
					serviceInstance.setQuantity(new Integer(1));

					if (serviceTemplateForSubscription.getServiceToAddDto() != null) {
						serviceInstance
								.setSubscriptionDate(serviceTemplateForSubscription
										.getServiceToAddDto()
										.getSubscriptionDate());
					} else {
						serviceInstance
								.setSubscriptionDate(subscriptionWithCreditLimitDto
										.getSubscriptionDate());
					}

					serviceInstanceService.serviceInstanciation(em,
							serviceInstance, currentUser);

					if (serviceInstance.getRecurringChargeInstances() != null) {
						for (RecurringChargeInstance recurringChargeInstance : serviceInstance
								.getRecurringChargeInstances()) {
							if (serviceTemplateForSubscription
									.getServiceToAddDto() == null) {
								continue;
							}
							recurringChargeInstance
									.setCriteria1(serviceTemplateForSubscription
											.getServiceToAddDto().getParam1());
							recurringChargeInstance
									.setCriteria2(serviceTemplateForSubscription
											.getServiceToAddDto().getParam2());
							recurringChargeInstance
									.setCriteria3(serviceTemplateForSubscription
											.getServiceToAddDto().getParam3());
						}
					}

					if (serviceInstance.getSubscriptionChargeInstances() != null) {
						for (ChargeInstance subscriptionChargeInstance : serviceInstance
								.getSubscriptionChargeInstances()) {
							if (serviceTemplateForSubscription
									.getServiceToAddDto() == null) {
								continue;
							}
							subscriptionChargeInstance
									.setCriteria1(serviceTemplateForSubscription
											.getServiceToAddDto().getParam1());
							subscriptionChargeInstance
									.setCriteria2(serviceTemplateForSubscription
											.getServiceToAddDto().getParam2());
							subscriptionChargeInstance
									.setCriteria3(serviceTemplateForSubscription
											.getServiceToAddDto().getParam3());
						}
					}

					subscriptionService.update(em, subscription, currentUser);
					serviceInstanceService.serviceActivation(em,
							serviceInstance, null, null, currentUser);
				}
			} catch (Exception e) {
				log.error("Error instantiating seller with code={}. {}",
						forSubscription.getSeller().getCode(), e.getMessage());
				e.printStackTrace();
			}

			forSubscription = forSubscription.getChild();
		}
	}

	private void updateOfferAndServiceTemplateAssociation(
			ForSubscription forSubscription, User currentUser) {

		while (forSubscription != null) {
			OfferTemplate offerTemplate = forSubscription
					.getOfferTemplateForSubscription().getOfferTemplate();

			if (offerTemplate.getServiceTemplates() == null) {
				List<ServiceTemplate> serviceTemplates = new ArrayList<ServiceTemplate>();
				for (ServiceTemplateForSubscription serviceTemplateForSubscription : forSubscription
						.getOfferTemplateForSubscription()
						.getServiceTemplatesForsuForSubscriptions()) {
					ServiceTemplate st = serviceTemplateForSubscription
							.getServiceTemplate();
					serviceTemplates.add(st);
				}
				offerTemplate.setServiceTemplates(serviceTemplates);
			} else {
				for (ServiceTemplateForSubscription serviceTemplateForSubscription : forSubscription
						.getOfferTemplateForSubscription()
						.getServiceTemplatesForsuForSubscriptions()) {
					ServiceTemplate st = serviceTemplateForSubscription
							.getServiceTemplate();
					if (!offerTemplate.getServiceTemplates().contains(st)) {
						offerTemplate.getServiceTemplates().add(st);
					}
				}
			}

			offerTemplateService.update(em, offerTemplate, currentUser);

			forSubscription = forSubscription.getChild();
		}
	}

	private boolean validateCreditLimit(ForSubscription forSubscription,
			SubscriptionWithCreditLimitDto subscriptionWithCreditLimitDto,
			Provider provider) throws BusinessException, MeveoApiException {

		// validate children
		while (forSubscription != null) {
			Date startDate = null;
			Date endDate = null;

			String billingAccountCode = paramBean.getProperty(
					"asp.api.default.billingAccount.prefix", "BA_")
					+ forSubscription.getSeller().getSeller().getCode();
			BillingAccount billingAccount = billingAccountService.findByCode(
					em, billingAccountCode, provider);
			if (billingAccount == null) {
				throw new BillingAccountDoesNotExistsException(
						billingAccountCode);
			}

			BigDecimal servicesSum = new BigDecimal(0);
			for (ServiceTemplateForSubscription serviceTemplateForSubscription : forSubscription
					.getOfferTemplateForSubscription()
					.getServiceTemplatesForsuForSubscriptions()) {
				ServiceTemplate st = serviceTemplateForSubscription
						.getServiceTemplate();
				if (st.getRecurringCharges() != null
						&& st.getRecurringCharges().size() > 0) {
					for (RecurringChargeTemplate ct : st.getRecurringCharges()) {
						try {
							if (startDate == null
									|| ct.getCalendar()
											.previousCalendarDate(
													subscriptionWithCreditLimitDto
															.getSubscriptionDate())
											.before(startDate)) {
								startDate = ct.getCalendar()
										.previousCalendarDate(
												subscriptionWithCreditLimitDto
														.getSubscriptionDate());
							}
							if (endDate == null
									|| ct.getCalendar()
											.nextCalendarDate(
													subscriptionWithCreditLimitDto
															.getSubscriptionDate())
											.after(endDate)) {
								endDate = ct.getCalendar().nextCalendarDate(
										subscriptionWithCreditLimitDto
												.getSubscriptionDate());
							}
						} catch (NullPointerException e) {
							log.debug(
									"Next or Previous calendar value is null for recurringChargeTemplate with code={}",
									ct.getCode());
						}
					}
				}
				if (serviceTemplateForSubscription.getServiceToAddDto() == null) {
					servicesSum.add(realtimeChargingService
							.getActivationServicePrice(em, billingAccount, st,
									subscriptionWithCreditLimitDto
											.getSubscriptionDate(),
									new BigDecimal(1), null, null, null, true));
				} else {
					servicesSum.add(realtimeChargingService
							.getActivationServicePrice(em, billingAccount, st,
									serviceTemplateForSubscription
											.getServiceToAddDto()
											.getSubscriptionDate(),
									new BigDecimal(1),
									serviceTemplateForSubscription
											.getServiceToAddDto().getParam1(),
									serviceTemplateForSubscription
											.getServiceToAddDto().getParam2(),
									serviceTemplateForSubscription
											.getServiceToAddDto().getParam3(),
									true));
				}
			}

			BigDecimal ratedAmount = walletOperationService.getRatedAmount(em,
					provider, forSubscription.getSeller(), null, null,
					billingAccount, null, startDate, endDate, true);

			BigDecimal spentCredit = servicesSum.add(ratedAmount);

			for (CreditLimitDto creditLimitDto : subscriptionWithCreditLimitDto
					.getCreditLimits()) {
				if (forSubscription.getSeller().getCode()
						.equals(creditLimitDto.getOrganizationId())) {
					if (spentCredit.compareTo(creditLimitDto.getCreditLimit()) > 0) {
						log.debug("Credit limit exceeded for seller code={}",
								forSubscription.getSeller().getCode());
						return false;
					}
				}
			}

			// next node
			forSubscription = forSubscription.getChild();
		}

		return true;
	}

	private ForSubscription processParent(ForSubscription forSubscription,
			Seller seller, String offerId, Provider provider,
			List<ServiceToAddDto> servicesToAdd) throws MeveoApiException {
		if (seller.getSeller() == null) {
			return forSubscription;
		}

		String sellerId = seller.getSeller().getCode();
		ForSubscription newForSubscription = new ForSubscription();
		newForSubscription.setChild(forSubscription);

		if (forSubscription.isChargedOffer()) {
			String chargedServiceTemplateCode = paramBean.getProperty(
					"asg.api.offer.charged.prefix", "_CH_OF_")
					+ offerId
					+ "_"
					+ sellerId;
			ServiceTemplate tempChargedOfferServiceTemplate = serviceTemplateService
					.findByCode(em, chargedServiceTemplateCode, provider);

			if (tempChargedOfferServiceTemplate != null) {
				newForSubscription.setSeller(seller);
				newForSubscription.setChargedOffer(true);
				newForSubscription.getOfferTemplateForSubscription()
						.setOfferTemplate(
								forSubscription
										.getOfferTemplateForSubscription()
										.getOfferTemplate());
				newForSubscription
						.getOfferTemplateForSubscription()
						.getServiceTemplatesForsuForSubscriptions()
						.add(new ServiceTemplateForSubscription(
								tempChargedOfferServiceTemplate));
				newForSubscription = processParent(newForSubscription,
						seller.getSeller(), offerId, provider, servicesToAdd);
			} else {
				newForSubscription.setChargedOffer(false);
				for (ServiceToAddDto serviceToAddDto : servicesToAdd) {
					// find offer
					String tempOfferTemplateCode = paramBean.getProperty(
							"asg.api.service.offer.prefix", "_SE_")
							+ serviceToAddDto.getServiceId();
					OfferTemplate tempOfferTemplate = offerTemplateService
							.findByCode(em, tempOfferTemplateCode, provider);

					// find service template
					String tempChargedServiceTemplateCode = paramBean
							.getProperty("asg.api.service.charged.prefix",
									"_CH_SE_")
							+ serviceToAddDto.getServiceId() + "_" + sellerId;
					ServiceTemplate tempChargedServiceTemplate = serviceTemplateService
							.findByCode(em, tempChargedServiceTemplateCode,
									provider);

					if (tempOfferTemplate != null
							&& tempChargedServiceTemplate != null) {
						forSubscription.getOfferTemplateForSubscription()
								.setOfferTemplate(tempOfferTemplate);

						forSubscription
								.getOfferTemplateForSubscription()
								.getServiceTemplatesForsuForSubscriptions()
								.add(new ServiceTemplateForSubscription(
										tempChargedServiceTemplate,
										serviceToAddDto));
					}

					newForSubscription = processParent(newForSubscription,
							seller.getSeller(), offerId, provider,
							servicesToAdd);
				}
			}
		} else {
			newForSubscription.setChargedOffer(false);
			for (ServiceToAddDto serviceToAddDto : servicesToAdd) {
				// find offer
				String tempOfferTemplateCode = paramBean.getProperty(
						"asg.api.service.offer.prefix", "_SE_")
						+ serviceToAddDto.getServiceId();
				OfferTemplate tempOfferTemplate = offerTemplateService
						.findByCode(em, tempOfferTemplateCode, provider);
				if (tempOfferTemplate == null) {
					throw new OfferTemplateDoesNotExistsException(
							tempOfferTemplateCode);
				}

				// find service template
				String tempChargedServiceTemplateCode = paramBean.getProperty(
						"asg.api.service.charged.prefix", "_CH_SE_")
						+ serviceToAddDto.getServiceId() + "_" + sellerId;
				ServiceTemplate tempChargedServiceTemplate = serviceTemplateService
						.findByCode(em, tempChargedServiceTemplateCode,
								provider);
				if (tempChargedServiceTemplate == null) {
					throw new ServiceTemplateDoesNotExistsException(
							tempChargedServiceTemplateCode);
				}
				forSubscription.getOfferTemplateForSubscription()
						.setOfferTemplate(tempOfferTemplate);
				forSubscription
						.getOfferTemplateForSubscription()
						.getServiceTemplatesForsuForSubscriptions()
						.add(new ServiceTemplateForSubscription(
								tempChargedServiceTemplate, serviceToAddDto));

				newForSubscription = processParent(newForSubscription,
						seller.getSeller(), offerId, provider, servicesToAdd);
			}
		}

		return newForSubscription;
	}

	class ForSubscription {
		private Seller seller;
		private OfferTemplateForSubscription offerTemplateForSubscription = new OfferTemplateForSubscription();
		private ForSubscription child;
		private boolean chargedOffer;

		public Seller getSeller() {
			return seller;
		}

		public void setSeller(Seller seller) {
			this.seller = seller;
		}

		public OfferTemplateForSubscription getOfferTemplateForSubscription() {
			return offerTemplateForSubscription;
		}

		public void setOfferTemplateForSubscription(
				OfferTemplateForSubscription offerTemplateForSubscription) {
			this.offerTemplateForSubscription = offerTemplateForSubscription;
		}

		public boolean isChargedOffer() {
			return chargedOffer;
		}

		public void setChargedOffer(boolean chargedOffer) {
			this.chargedOffer = chargedOffer;
		}

		public ForSubscription getChild() {
			return child;
		}

		public void setChild(ForSubscription child) {
			this.child = child;
		}

		public boolean hasChild() {
			return child != null;
		}

	}

	class OfferTemplateForSubscription {
		private OfferTemplate offerTemplate;
		private List<ServiceTemplateForSubscription> serviceTemplatesForsuForSubscriptions = new ArrayList<ServiceTemplateForSubscription>();

		public OfferTemplate getOfferTemplate() {
			return offerTemplate;
		}

		public void setOfferTemplate(OfferTemplate offerTemplate) {
			this.offerTemplate = offerTemplate;
		}

		public List<ServiceTemplateForSubscription> getServiceTemplatesForsuForSubscriptions() {
			return serviceTemplatesForsuForSubscriptions;
		}

		public void setServiceTemplatesForsuForSubscriptions(
				List<ServiceTemplateForSubscription> serviceTemplatesForsuForSubscriptions) {
			this.serviceTemplatesForsuForSubscriptions = serviceTemplatesForsuForSubscriptions;
		}

	}

	class ServiceTemplateForSubscription {
		private ServiceTemplate serviceTemplate;
		private ServiceToAddDto serviceToAddDto;

		public ServiceTemplateForSubscription() {

		}

		public ServiceTemplateForSubscription(ServiceTemplate serviceTemplate) {
			this.serviceTemplate = serviceTemplate;
		}

		public ServiceTemplateForSubscription(ServiceTemplate serviceTemplate,
				ServiceToAddDto serviceToAddDto) {
			this.serviceTemplate = serviceTemplate;
			this.serviceToAddDto = serviceToAddDto;
		}

		public ServiceTemplate getServiceTemplate() {
			return serviceTemplate;
		}

		public void setServiceTemplate(ServiceTemplate serviceTemplate) {
			this.serviceTemplate = serviceTemplate;
		}

		public ServiceToAddDto getServiceToAddDto() {
			return serviceToAddDto;
		}

		public void setServiceToAddDto(ServiceToAddDto serviceToAddDto) {
			this.serviceToAddDto = serviceToAddDto;
		}

	}

}
