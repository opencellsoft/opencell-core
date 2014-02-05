package org.meveo.asg.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.RecurringChargeDto;
import org.meveo.api.dto.ServicePricePlanDto;
import org.meveo.api.dto.SubscriptionFeeDto;
import org.meveo.api.dto.TerminationFeeDto;
import org.meveo.api.dto.UsageChargeDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.exception.ServiceTemplateAlreadyExistsException;
import org.meveo.api.exception.ServiceTemplateDoesNotExistsException;
import org.meveo.asg.api.model.EntityCodeEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurrenceTypeEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.ServiceUsageChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.UsageChgTemplateEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.ServiceUsageChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ServicePricePlanServiceApi extends BaseAsgApi {

	@Inject
	private ParamBean paramBean;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private TaxService taxService;

	@Inject
	private SellerService sellerService;

	@Inject
	private CalendarService calendarService;

	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private CounterTemplateService<CounterTemplate> counterTemplateService;

	@Inject
	private ServiceUsageChargeTemplateService serviceUsageChargeTemplateService;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private Logger log;

	public void create(ServicePricePlanDto servicePricePlanDto)
			throws MeveoApiException {
		if (!StringUtils.isBlank(servicePricePlanDto.getServiceId())
				&& !StringUtils
						.isBlank(servicePricePlanDto.getOrganizationId())
				&& !StringUtils.isBlank(servicePricePlanDto.getTaxId())
				&& !StringUtils.isBlank(servicePricePlanDto.getBillingPeriod())) {

			Provider provider = providerService.findById(servicePricePlanDto
					.getProviderId());
			User currentUser = userService.findById(servicePricePlanDto
					.getCurrentUserId());

			try {
				servicePricePlanDto.setServiceId(asgIdMappingService
						.getNewCode(em, servicePricePlanDto.getServiceId(),
								EntityCodeEnum.SPF));

				servicePricePlanDto.setOrganizationId(asgIdMappingService
						.getMeveoCode(em,
								servicePricePlanDto.getOrganizationId(),
								EntityCodeEnum.ORG));
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			Calendar calendar = calendarService.findByName(em,
					servicePricePlanDto.getBillingPeriod());
			if (calendar == null) {
				throw new MeveoApiException("Calendar with name="
						+ servicePricePlanDto.getBillingPeriod()
						+ " does not exists.");
			}

			Seller seller = sellerService.findByCode(em,
					servicePricePlanDto.getOrganizationId(), provider);

			// get invoice sub category
			Tax tax = taxService.findByCode(em, servicePricePlanDto.getTaxId());
			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryCountryService
					.findByTaxId(em, tax).getInvoiceSubCategory();

			ServiceTemplate serviceTemplate = createServiceTemplate(false,
					servicePricePlanDto, currentUser, provider);
			RecurringChargeTemplate recurringChargeTemplate = createRecurringCharge(
					false, servicePricePlanDto, currentUser, provider,
					invoiceSubCategory, calendar, seller);
			OneShotChargeTemplate subscriptionTemplate = createSubscriptionTemplate(
					false, servicePricePlanDto, currentUser, provider,
					invoiceSubCategory, seller);
			OneShotChargeTemplate terminationTemplate = createTerminationTemplate(
					false, servicePricePlanDto, currentUser, provider,
					invoiceSubCategory, seller);
			List<ServiceUsageChargeTemplate> serviceUsageChargeTemplates = createServiceUsageChargeTemplates(
					false, servicePricePlanDto, currentUser, provider,
					calendar, serviceTemplate, invoiceSubCategory, seller);

			serviceTemplate.getRecurringCharges().add(recurringChargeTemplate);
			serviceTemplate.getSubscriptionCharges().add(subscriptionTemplate);
			serviceTemplate.getTerminationCharges().add(terminationTemplate);
			serviceTemplate.setServiceUsageCharges(serviceUsageChargeTemplates);
			serviceTemplateService.update(em, serviceTemplate, currentUser);

			// recommended prices
			ServiceTemplate recommendedServiceTemplate = createServiceTemplate(
					true, servicePricePlanDto, currentUser, provider);
			RecurringChargeTemplate recommendedRecurringChargeTemplate = createRecurringCharge(
					true, servicePricePlanDto, currentUser, provider,
					invoiceSubCategory, calendar, seller);
			OneShotChargeTemplate recommendedSubscriptionTemplate = createSubscriptionTemplate(
					true, servicePricePlanDto, currentUser, provider,
					invoiceSubCategory, seller);
			OneShotChargeTemplate recommendedTerminationTemplate = createTerminationTemplate(
					true, servicePricePlanDto, currentUser, provider,
					invoiceSubCategory, seller);
			List<ServiceUsageChargeTemplate> recommendedServiceUsageChargeTemplates = createServiceUsageChargeTemplates(
					true, servicePricePlanDto, currentUser, provider, calendar,
					serviceTemplate, invoiceSubCategory, seller);

			recommendedServiceTemplate.getRecurringCharges().add(
					recommendedRecurringChargeTemplate);
			recommendedServiceTemplate.getSubscriptionCharges().add(
					recommendedSubscriptionTemplate);
			recommendedServiceTemplate.getTerminationCharges().add(
					recommendedTerminationTemplate);
			recommendedServiceTemplate
					.setServiceUsageCharges(recommendedServiceUsageChargeTemplates);
			serviceTemplateService.update(em, recommendedServiceTemplate,
					currentUser);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(servicePricePlanDto.getServiceId())) {
				missingFields.add("serviceId");
			}
			if (StringUtils.isBlank(servicePricePlanDto.getOrganizationId())) {
				missingFields.add("organizationId");
			}
			if (StringUtils.isBlank(servicePricePlanDto.getBillingPeriod())) {
				missingFields.add("billingPeriod");
			}
			if (StringUtils.isBlank(servicePricePlanDto.getTaxId())) {
				missingFields.add("taxId");
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

	private ServiceTemplate createServiceTemplate(boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider) throws MeveoApiException {
		// Create a charged service for offer linked to service and
		// organization. Service code is
		// '_CH_SE_[OrganizationId]_[ServiceId]'.
		// '_CH_SE_' must be settable in properties file..
		String serviceOfferCodePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.service.charged.prefix",
						"_REC_CH_SE_") : paramBean.getProperty(
				"asg.api.service.charged.prefix", "_CH_SE_");

		String serviceTemplateCode = serviceOfferCodePrefix
				+ servicePricePlanDto.getServiceId() + "_"
				+ servicePricePlanDto.getOrganizationId();
		// check if template exists
		if (serviceTemplateService
				.findByCode(em, serviceTemplateCode, provider) != null) {
			throw new ServiceTemplateAlreadyExistsException(serviceTemplateCode);
		}

		ServiceTemplate serviceTemplate = new ServiceTemplate();
		serviceTemplate.setCode(serviceTemplateCode);

		serviceTemplate.setActive(true);
		serviceTemplateService.create(em, serviceTemplate, currentUser,
				provider);

		return serviceTemplate;
	}

	private RecurringChargeTemplate createRecurringCharge(
			boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider, InvoiceSubCategory invoiceSubCategory,
			Calendar calendar, Seller seller) {
		// Create a recurring charge with service descriptions and
		// parameters. Charge code is '_RE_SE_[OrganizationId]_[ServceId]'
		// ('_RE_SE_' must be settable). This charge must be associated to
		// step 1 service.
		String recurringChargePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.service.recurring.prefix",
						"_REC_RE_SE_") : paramBean.getProperty(
				"asg.api.service.recurring.prefix", "_RE_SE_");
		RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
		recurringChargeTemplate.setActive(true);
		recurringChargeTemplate.setCode(recurringChargePrefix
				+ servicePricePlanDto.getServiceId() + "_"
				+ servicePricePlanDto.getOrganizationId());
		recurringChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
		recurringChargeTemplate.setRecurrenceType(RecurrenceTypeEnum.CALENDAR);
		recurringChargeTemplate.setSubscriptionProrata(servicePricePlanDto
				.getSubscriptionProrata());
		recurringChargeTemplate.setTerminationProrata(servicePricePlanDto
				.getTerminationProrata());
		recurringChargeTemplate.setApplyInAdvance(servicePricePlanDto
				.getApplyInAdvance());
		recurringChargeTemplate.setType(OperationTypeEnum.CREDIT);
		recurringChargeTemplate.setCalendar(calendar);
		recurringChargeTemplateService.create(em, recurringChargeTemplate,
				currentUser, provider);

		// create price plans
		if (servicePricePlanDto.getRecurringCharges() != null
				&& servicePricePlanDto.getRecurringCharges().size() > 0) {
			for (RecurringChargeDto recurringChargeDto : servicePricePlanDto
					.getRecurringCharges()) {
				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(
								recurringChargeDto.getCurrencyCode(), provider);

				PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
				pricePlanMatrix.setEventCode(recurringChargeTemplate.getCode());
				if (isRecommendedPrice) {
					pricePlanMatrix.setAmountWithoutTax(recurringChargeDto
							.getRecommendedPrice());
				} else {
					pricePlanMatrix.setAmountWithoutTax(recurringChargeDto
							.getPrice());
				}
				pricePlanMatrix.setTradingCurrency(tradingCurrency);
				pricePlanMatrix.setStartRatingDate(recurringChargeDto
						.getStartDate());
				// pricePlanMatrix.setSeller(seller);
				pricePlanMatrix.setEndRatingDate(recurringChargeDto
						.getEndDate());
				pricePlanMatrix.setMinSubscriptionAgeInMonth(Long
						.valueOf(recurringChargeDto.getMinAge()));
				pricePlanMatrix.setMaxSubscriptionAgeInMonth(Long
						.valueOf(recurringChargeDto.getMaxAge()));
				pricePlanMatrix.setCriteria1Value(servicePricePlanDto
						.getParam1());
				pricePlanMatrix.setCriteria2Value(servicePricePlanDto
						.getParam2());
				pricePlanMatrix.setCriteria3Value(servicePricePlanDto
						.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}
		}

		return recurringChargeTemplate;
	}

	private OneShotChargeTemplate createSubscriptionTemplate(
			boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider, InvoiceSubCategory invoiceSubCategory,
			Seller seller) {
		// Create a subscription point charge. Charge code is
		// '_SO_SE_[OrganizationId]_[ServiceId]' ('_SO_SE_' must be
		// settable). This charge must be associated to step 1 service.
		String subscriptionPointChargePrefix = isRecommendedPrice ? paramBean
				.getProperty(
						"asg.api.recommended.service.subscription.point.charge.prefix",
						"_REC_SO_SE_")
				: paramBean.getProperty(
						"asg.api.service.subscription.point.charge.prefix",
						"_SO_SE_");
		OneShotChargeTemplate subscriptionTemplate = new OneShotChargeTemplate();
		subscriptionTemplate.setActive(true);
		subscriptionTemplate.setCode(subscriptionPointChargePrefix
				+ servicePricePlanDto.getServiceId() + "_"
				+ servicePricePlanDto.getOrganizationId());
		subscriptionTemplate.setInvoiceSubCategory(invoiceSubCategory);
		subscriptionTemplate
				.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.SUBSCRIPTION);
		oneShotChargeTemplateService.create(em, subscriptionTemplate,
				currentUser, provider);

		if (servicePricePlanDto.getSubscriptionFees() != null
				&& servicePricePlanDto.getSubscriptionFees().size() > 0) {
			for (SubscriptionFeeDto subscriptionFeeDto : servicePricePlanDto
					.getSubscriptionFees()) {
				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(
								subscriptionFeeDto.getCurrencyCode(), provider);

				PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
				pricePlanMatrix.setEventCode(subscriptionTemplate.getCode());
				if (isRecommendedPrice) {
					pricePlanMatrix.setAmountWithoutTax(subscriptionFeeDto
							.getRecommendedPrice());
				} else {
					pricePlanMatrix.setAmountWithoutTax(subscriptionFeeDto
							.getPrice());
				}
				pricePlanMatrix.setTradingCurrency(tradingCurrency);
				pricePlanMatrix.setStartRatingDate(subscriptionFeeDto
						.getStartDate());
				// pricePlanMatrix.setSeller(seller);
				pricePlanMatrix.setEndRatingDate(subscriptionFeeDto
						.getEndDate());
				pricePlanMatrix.setCriteria1Value(servicePricePlanDto
						.getParam1());
				pricePlanMatrix.setCriteria2Value(servicePricePlanDto
						.getParam2());
				pricePlanMatrix.setCriteria3Value(servicePricePlanDto
						.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}
		}

		return subscriptionTemplate;
	}

	private OneShotChargeTemplate createTerminationTemplate(
			boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider, InvoiceSubCategory invoiceSubCategory,
			Seller seller) {
		// Create a termination point charge. Charge code is
		// '_TE_SE_[OrganizationId]_[ServiceId]' ('_TE_SE_' must be
		// settable). This charge must be associated to step 1 service.
		String terminationPointChargePrefix = isRecommendedPrice ? paramBean
				.getProperty(
						"asg.api.recommended.service.termination.point.charge.prefix",
						"_REC_TE_SE_")
				: paramBean.getProperty(
						"asg.api.service.termination.point.charge.prefix",
						"_TE_SE_");
		OneShotChargeTemplate terminationTemplate = new OneShotChargeTemplate();
		terminationTemplate.setActive(true);
		terminationTemplate.setCode(terminationPointChargePrefix
				+ servicePricePlanDto.getServiceId() + "_"
				+ servicePricePlanDto.getOrganizationId());
		terminationTemplate.setInvoiceSubCategory(invoiceSubCategory);
		terminationTemplate
				.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.TERMINATION);
		oneShotChargeTemplateService.create(em, terminationTemplate,
				currentUser, provider);

		if (servicePricePlanDto.getTerminationFees() != null
				&& servicePricePlanDto.getTerminationFees().size() > 0) {
			for (TerminationFeeDto terminationFeeDto : servicePricePlanDto
					.getTerminationFees()) {
				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(
								terminationFeeDto.getCurrencyCode(), provider);

				PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
				pricePlanMatrix.setEventCode(terminationTemplate.getCode());
				if (isRecommendedPrice) {
					pricePlanMatrix.setAmountWithoutTax(terminationFeeDto
							.getRecommendedPrice());
				} else {
					pricePlanMatrix.setAmountWithoutTax(terminationFeeDto
							.getPrice());
				}
				pricePlanMatrix.setTradingCurrency(tradingCurrency);
				pricePlanMatrix.setStartRatingDate(terminationFeeDto
						.getStartDate());
				// pricePlanMatrix.setSeller(seller);
				pricePlanMatrix
						.setEndRatingDate(terminationFeeDto.getEndDate());
				pricePlanMatrix.setCriteria1Value(servicePricePlanDto
						.getParam1());
				pricePlanMatrix.setCriteria2Value(servicePricePlanDto
						.getParam2());
				pricePlanMatrix.setCriteria3Value(servicePricePlanDto
						.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}
		}

		return terminationTemplate;
	}

	private List<ServiceUsageChargeTemplate> createServiceUsageChargeTemplates(
			boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider, Calendar calendar,
			ServiceTemplate serviceTemplate,
			InvoiceSubCategory invoiceSubCategory, Seller seller) {
		String serviceOfferCodePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.service.charged.prefix",
						"_REC_CH_SE_") : paramBean.getProperty(
				"asg.api.service.charged.prefix", "_CH_SE_");

		List<ServiceUsageChargeTemplate> serviceUsageChargeTemplates = new ArrayList<ServiceUsageChargeTemplate>();
		for (UsageChargeDto usageChargeDto : servicePricePlanDto
				.getUsageCharges()) {
			// Create a counter for each min range values used as
			// parameters.
			// Counters codes are '_SE_[OrganizationId]_[ServiceId]_[Valeur
			// Min]' ('_SE_' must be settable). Counters are ordered by
			// values.
			CounterTemplate counterTemplate = new CounterTemplate();
			counterTemplate.setCode(serviceOfferCodePrefix
					+ servicePricePlanDto.getServiceId() + "_"
					+ servicePricePlanDto.getOrganizationId() + "_"
					+ usageChargeDto.getMin());
			counterTemplate.setCounterType(CounterTypeEnum.QUANTITY);
			counterTemplate.setCalendar(calendar);
			counterTemplate.setUnityDescription(servicePricePlanDto
					.getUsageUnit());
			Integer min = 0;
			if (usageChargeDto.getMin() != null) {
				min = usageChargeDto.getMin();
			}
			Integer max = null;
			if (usageChargeDto.getMax() != null) {
				max = usageChargeDto.getMax();
			}
			if (max != null) {
				counterTemplate.setLevel(new BigDecimal(max - min));
			}
			counterTemplateService.create(em, counterTemplate, currentUser,
					provider);

			// Create an usage charge for each counter. Charges codes are
			// '_US_SE_[OrganizationId]_[ServiceId]_[Valeur Min]' ('_US_SE_'
			// must be settable). This charge must be associated to step 1
			// service
			String usageChargeTemplatePrefix = isRecommendedPrice ? paramBean
					.getProperty(
							"asg.api.recommended.service.usage.charged.prefix",
							"_REC_US_SE_") : paramBean.getProperty(
					"asg.api.service.usage.charged.prefix", "_US_SE_");
			UsageChargeTemplate usageChargeTemplate = new UsageChargeTemplate();
			usageChargeTemplate.setCode(usageChargeTemplatePrefix
					+ servicePricePlanDto.getServiceId() + "_"
					+ servicePricePlanDto.getOrganizationId() + "_" + min);
			usageChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			usageChargeTemplate.setUnityFormatter(UsageChgTemplateEnum.INTEGER);
			usageChargeTemplate.setUnityDescription(servicePricePlanDto
					.getUsageUnit());
			usageChargeTemplate.setPriority(min);
			usageChargeTemplateService.create(em, usageChargeTemplate,
					currentUser, provider);

			ServiceUsageChargeTemplate serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
			serviceUsageChargeTemplate.setChargeTemplate(usageChargeTemplate);
			serviceUsageChargeTemplate.setCounterTemplate(counterTemplate);
			serviceUsageChargeTemplate.setServiceTemplate(serviceTemplate);
			serviceUsageChargeTemplateService.create(em,
					serviceUsageChargeTemplate, currentUser, provider);
			serviceUsageChargeTemplates.add(serviceUsageChargeTemplate);

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(
							usageChargeDto.getCurrencyCode(), provider);

			PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
			pricePlanMatrix.setEventCode(usageChargeTemplate.getCode());
			if (isRecommendedPrice) {
				pricePlanMatrix.setAmountWithoutTax(usageChargeDto
						.getRecommendedPrice());
			} else {
				pricePlanMatrix.setAmountWithoutTax(usageChargeDto.getPrice());
			}
			pricePlanMatrix.setTradingCurrency(tradingCurrency);
			pricePlanMatrix.setStartRatingDate(usageChargeDto.getStartDate());
			// pricePlanMatrix.setSeller(seller);
			pricePlanMatrix.setEndRatingDate(usageChargeDto.getEndDate());
			pricePlanMatrix.setCriteria1Value(servicePricePlanDto.getParam1());
			pricePlanMatrix.setCriteria2Value(servicePricePlanDto.getParam2());
			pricePlanMatrix.setCriteria3Value(servicePricePlanDto.getParam3());
			pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
					provider);
		}

		return serviceUsageChargeTemplates;
	}

	public void remove(String serviceId, String organizationId, Long userId,
			Long providerId) throws MeveoApiException {

		if (StringUtils.isBlank(serviceId)
				|| StringUtils.isBlank(organizationId)) {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(serviceId)) {
				missingFields.add("serviceId");
			}
			if (StringUtils.isBlank(organizationId)) {
				missingFields.add("organizationId");
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

		removeService(true, serviceId, organizationId, userId, providerId);
		removeService(false, serviceId, organizationId, userId, providerId);
	}

	public void removeService(boolean isRecommendedPrice, String serviceId,
			String organizationId, Long userId, Long providerId)
			throws MeveoApiException {

		Provider provider = providerService.findById(providerId);
		User currentUser = userService.findById(userId);

		try {
			serviceId = asgIdMappingService.getMeveoCode(em, serviceId,
					EntityCodeEnum.SPF);

			organizationId = asgIdMappingService.getMeveoCode(em,
					organizationId, EntityCodeEnum.ORG);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}

		String serviceOfferCodePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.service.charged.prefix",
						"_REC_CH_SE_") : paramBean.getProperty(
				"asg.api.service.charged.prefix", "_CH_SE_");

		String serviceTemplateCode = serviceOfferCodePrefix + organizationId
				+ "_" + serviceId;

		try {
			// remove service template
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(em, serviceTemplateCode, provider);

			if (serviceTemplate != null) {
				serviceTemplate.setRecurringCharges(null);
				serviceTemplate.setSubscriptionCharges(null);
				serviceTemplate.setTerminationCharges(null);
				serviceTemplate.setServiceUsageCharges(null);
				serviceTemplateService.update(em, serviceTemplate, currentUser);
			}

			// delete subscription fee
			String subscriptionPointChargePrefix = isRecommendedPrice ? paramBean
					.getProperty(
							"asg.api.recommended.service.subscription.point.charge.prefix",
							"_REC_SO_SE_")
					: paramBean.getProperty(
							"asg.api.service.subscription.point.charge.prefix",
							"_SO_SE_");
			String subscriptionTemplateCode = subscriptionPointChargePrefix
					+ serviceId + "_" + organizationId;

			// delete price plan
			pricePlanMatrixService.removeByCode(em, subscriptionTemplateCode,
					provider);

			OneShotChargeTemplate subscriptionTemplate = oneShotChargeTemplateService
					.findByCode(em, subscriptionTemplateCode, provider);
			if (subscriptionTemplate != null) {
				oneShotChargeTemplateService.remove(em, subscriptionTemplate);
			}

			// delete termination fee
			String terminationPointChargePrefix = isRecommendedPrice ? paramBean
					.getProperty(
							"asg.api.recommended.service.termination.point.charge.prefix",
							"_REC_TE_SE_")
					: paramBean.getProperty(
							"asg.api.service.termination.point.charge.prefix",
							"_TE_SE_");
			String terminationTemplateCode = terminationPointChargePrefix
					+ serviceId + "_" + organizationId;

			// delete price plan
			pricePlanMatrixService.removeByCode(em, terminationTemplateCode,
					provider);

			OneShotChargeTemplate terminationTemplate = oneShotChargeTemplateService
					.findByCode(em, terminationTemplateCode, provider);
			if (terminationTemplate != null) {
				oneShotChargeTemplateService.remove(em, terminationTemplate);
			}

			// delete usageCharge link
			String usageChargeTemplatePrefix = isRecommendedPrice ? paramBean
					.getProperty(
							"asg.api.recommended.service.usage.charged.prefix",
							"_REC_US_SE_") : paramBean.getProperty(
					"asg.api.service.usage.charged.prefix", "_US_SE_");
			String usageChargeCode = usageChargeTemplatePrefix + serviceId
					+ "_" + organizationId;

			// delete usage charge price plans
			pricePlanMatrixService
					.removeByPrefix(em, usageChargeCode, provider);

			// delete usageCharge counter link
			List<UsageChargeTemplate> usageChargeTemplates = usageChargeTemplateService
					.findByPrefix(em, usageChargeCode, provider);
			if (usageChargeTemplates != null) {
				for (UsageChargeTemplate usageChargeTemplate : usageChargeTemplates) {
					// getservice usage charge
					List<ServiceUsageChargeTemplate> serviceUsageChargeTemplates = serviceUsageChargeTemplateService
							.findByUsageChargeTemplate(em, usageChargeTemplate,
									provider);
					if (serviceUsageChargeTemplates != null) {
						for (ServiceUsageChargeTemplate serviceUsageChargeTemplate : serviceUsageChargeTemplates) {
							serviceUsageChargeTemplateService.remove(em,
									serviceUsageChargeTemplate);
						}
					}

					usageChargeTemplateService.remove(em, usageChargeTemplate);
				}
			}

			counterTemplateService.removeByPrefix(em, serviceTemplateCode,
					provider);

			// delete recurring charge
			String recurringChargePrefix = isRecommendedPrice ? paramBean
					.getProperty(
							"asg.api.recommended.service.recurring.prefix",
							"_REC_RE_SE_") : paramBean.getProperty(
					"asg.api.service.recurring.prefix", "_RE_SE_");
			String recurringChargeCode = recurringChargePrefix + serviceId
					+ "_" + organizationId;

			// delete price plan
			pricePlanMatrixService.removeByCode(em, recurringChargeCode,
					provider);

			RecurringChargeTemplate recurringChargeTemplate = recurringChargeTemplateService
					.findByCode(em, recurringChargeCode, provider);
			if (recurringChargeTemplate != null) {
				recurringChargeTemplateService.remove(em,
						recurringChargeTemplate);
			}

			if (serviceTemplate != null) {
				List<OfferTemplate> offerTemplates = offerTemplateService
						.findByServiceTemplate(em, serviceTemplate, provider);
				if (offerTemplates != null) {
					for (OfferTemplate offerTemplate : offerTemplates) {
						offerTemplate.getServiceTemplates().remove(
								serviceTemplate);
						offerTemplateService.update(em, offerTemplate,
								currentUser);
					}
				}

				serviceTemplateService.remove(em, serviceTemplate);
			}
		} catch (Exception e) {
			log.error("Error deleting service price plan with code={}: {}",
					serviceTemplateCode, e.getMessage());
			throw new MeveoApiException(
					"Failed deleting servicePricePlan with code="
							+ serviceTemplateCode + ".");
		}
	}

	public void update(ServicePricePlanDto servicePricePlanDto)
			throws MeveoApiException {
		if (!StringUtils.isBlank(servicePricePlanDto.getServiceId())
				&& !StringUtils
						.isBlank(servicePricePlanDto.getOrganizationId())) {

			Provider provider = providerService.findById(servicePricePlanDto
					.getProviderId());
			User currentUser = userService.findById(servicePricePlanDto
					.getCurrentUserId());

			try {
				servicePricePlanDto.setServiceId(asgIdMappingService
						.getMeveoCode(em, servicePricePlanDto.getServiceId(),
								EntityCodeEnum.SPF));

				servicePricePlanDto.setOrganizationId(asgIdMappingService
						.getMeveoCode(em,
								servicePricePlanDto.getOrganizationId(),
								EntityCodeEnum.ORG));
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			String serviceOfferCodePrefix = paramBean.getProperty(
					"asg.api.service.charged.prefix", "_CH_SE_");
			String serviceTemplateCode = serviceOfferCodePrefix
					+ servicePricePlanDto.getServiceId() + "_"
					+ servicePricePlanDto.getOrganizationId();
			// check if template exists
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(em, serviceTemplateCode, provider);
			if (serviceTemplate == null) {
				throw new ServiceTemplateDoesNotExistsException(
						serviceTemplateCode);
			}

			updateRecurringCharge(false, servicePricePlanDto, currentUser,
					provider);
			updateSubscriptionTemplate(false, servicePricePlanDto, currentUser,
					provider);
			updateTerminationTemplate(false, servicePricePlanDto, currentUser,
					provider);
			updateServiceUsageChargeTemplates(false, servicePricePlanDto,
					currentUser, provider);

			// recommended prices
			updateRecurringCharge(true, servicePricePlanDto, currentUser,
					provider);
			updateSubscriptionTemplate(true, servicePricePlanDto, currentUser,
					provider);
			updateTerminationTemplate(true, servicePricePlanDto, currentUser,
					provider);
			updateServiceUsageChargeTemplates(true, servicePricePlanDto,
					currentUser, provider);

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(servicePricePlanDto.getServiceId())) {
				missingFields.add("serviceId");
			}
			if (StringUtils.isBlank(servicePricePlanDto.getOrganizationId())) {
				missingFields.add("organizationId");
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

	private void updateRecurringCharge(boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider) {
		// Create a recurring charge with service descriptions and
		// parameters. Charge code is '_RE_SE_[OrganizationId]_[ServceId]'
		// ('_RE_SE_' must be settable). This charge must be associated to
		// step 1 service.
		String recurringChargePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.service.recurring.prefix",
						"_REC_RE_SE_") : paramBean.getProperty(
				"asg.api.service.recurring.prefix", "_RE_SE_");
		String recurringChargeCode = recurringChargePrefix
				+ servicePricePlanDto.getServiceId() + "_"
				+ servicePricePlanDto.getOrganizationId();

		// create price plans
		if (servicePricePlanDto.getRecurringCharges() != null
				&& servicePricePlanDto.getRecurringCharges().size() > 0) {
			for (RecurringChargeDto recurringChargeDto : servicePricePlanDto
					.getRecurringCharges()) {

				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(
								recurringChargeDto.getCurrencyCode(), provider);

				PricePlanMatrix pricePlanMatrix = pricePlanMatrixService
						.findByEventCodeAndCurrency(em, recurringChargeCode,
								tradingCurrency);
				if (pricePlanMatrix != null) {
					if (isRecommendedPrice) {
						pricePlanMatrix.setAmountWithoutTax(recurringChargeDto
								.getRecommendedPrice());
					} else {
						pricePlanMatrix.setAmountWithoutTax(recurringChargeDto
								.getPrice());
					}

					pricePlanMatrixService.update(em, pricePlanMatrix,
							currentUser);
				}
			}
		}

	}

	private void updateSubscriptionTemplate(boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider) {
		// Create a subscription point charge. Charge code is
		// '_SO_SE_[OrganizationId]_[ServiceId]' ('_SO_SE_' must be
		// settable). This charge must be associated to step 1 service.
		String subscriptionPointChargePrefix = isRecommendedPrice ? paramBean
				.getProperty(
						"asg.api.recommended.service.subscription.point.charge.prefix",
						"_REC_SO_SE_")
				: paramBean.getProperty(
						"asg.api.service.subscription.point.charge.prefix",
						"_SO_SE_");
		String subscriptionPointChargeCode = subscriptionPointChargePrefix
				+ servicePricePlanDto.getServiceId() + "_"
				+ servicePricePlanDto.getOrganizationId();

		if (servicePricePlanDto.getSubscriptionFees() != null
				&& servicePricePlanDto.getSubscriptionFees().size() > 0) {
			for (SubscriptionFeeDto subscriptionFeeDto : servicePricePlanDto
					.getSubscriptionFees()) {
				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(
								subscriptionFeeDto.getCurrencyCode(), provider);

				PricePlanMatrix pricePlanMatrix = pricePlanMatrixService
						.findByEventCodeAndCurrency(em,
								subscriptionPointChargeCode, tradingCurrency);
				if (pricePlanMatrix != null) {
					if (isRecommendedPrice) {
						pricePlanMatrix.setAmountWithoutTax(subscriptionFeeDto
								.getRecommendedPrice());
					} else {
						pricePlanMatrix.setAmountWithoutTax(subscriptionFeeDto
								.getPrice());
					}

					pricePlanMatrixService.update(em, pricePlanMatrix,
							currentUser);
				}
			}
		}

	}

	private void updateTerminationTemplate(boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider) {
		String terminationPointChargePrefix = isRecommendedPrice ? paramBean
				.getProperty(
						"asg.api.recommended.service.termination.point.charge.prefix",
						"_REC_TE_SE_")
				: paramBean.getProperty(
						"asg.api.service.termination.point.charge.prefix",
						"_TE_SE_");
		String terminationPointChargeCode = terminationPointChargePrefix
				+ servicePricePlanDto.getServiceId() + "_"
				+ servicePricePlanDto.getOrganizationId();

		if (servicePricePlanDto.getTerminationFees() != null
				&& servicePricePlanDto.getTerminationFees().size() > 0) {
			for (TerminationFeeDto terminationFeeDto : servicePricePlanDto
					.getTerminationFees()) {
				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(
								terminationFeeDto.getCurrencyCode(), provider);

				PricePlanMatrix pricePlanMatrix = pricePlanMatrixService
						.findByEventCodeAndCurrency(em,
								terminationPointChargeCode, tradingCurrency);
				if (pricePlanMatrix != null) {
					if (isRecommendedPrice) {
						pricePlanMatrix.setAmountWithoutTax(terminationFeeDto
								.getRecommendedPrice());
					} else {
						pricePlanMatrix.setAmountWithoutTax(terminationFeeDto
								.getPrice());
					}

					pricePlanMatrixService.update(em, pricePlanMatrix,
							currentUser);
				}
			}
		}

	}

	private void updateServiceUsageChargeTemplates(boolean isRecommendedPrice,
			ServicePricePlanDto servicePricePlanDto, User currentUser,
			Provider provider) {

		for (UsageChargeDto usageChargeDto : servicePricePlanDto
				.getUsageCharges()) {
			Integer min = 0;
			if (usageChargeDto.getMin() != null) {
				min = usageChargeDto.getMin();
			}

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(
							usageChargeDto.getCurrencyCode(), provider);

			String usageChargeTemplatePrefix = isRecommendedPrice ? paramBean
					.getProperty(
							"asg.api.recommended.service.usage.charged.prefix",
							"_REC_US_SE_") : paramBean.getProperty(
					"asg.api.service.usage.charged.prefix", "_US_SE_");
			String usageChargeTemplateCode = usageChargeTemplatePrefix
					+ servicePricePlanDto.getServiceId() + "_"
					+ servicePricePlanDto.getOrganizationId() + "_" + min;

			PricePlanMatrix pricePlanMatrix = pricePlanMatrixService
					.findByEventCodeAndCurrency(em, usageChargeTemplateCode,
							tradingCurrency);
			if (pricePlanMatrix != null) {
				if (isRecommendedPrice) {
					pricePlanMatrix.setAmountWithoutTax(usageChargeDto
							.getRecommendedPrice());
				} else {
					pricePlanMatrix.setAmountWithoutTax(usageChargeDto
							.getPrice());
				}

				pricePlanMatrixService.update(em, pricePlanMatrix, currentUser);
			}
		}

	}

}
