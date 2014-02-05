package org.meveo.asg.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.OfferPricePlanDto;
import org.meveo.api.dto.RecurringChargeDto;
import org.meveo.api.dto.SubscriptionFeeDto;
import org.meveo.api.dto.TerminationFeeDto;
import org.meveo.api.dto.UsageChargeDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
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
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OfferPricePlanServiceApi extends BaseAsgApi {

	@Inject
	private ParamBean paramBean;

	@Inject
	private OfferTemplateService offerTemplateService;

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

	private static Logger log = LoggerFactory
			.getLogger(OfferPricePlanServiceApi.class);

	public void create(OfferPricePlanDto offerPricePlanDto)
			throws MeveoApiException {
		if (!StringUtils.isBlank(offerPricePlanDto.getOfferId())
				&& !StringUtils.isBlank(offerPricePlanDto.getOrganizationId())
				&& !StringUtils.isBlank(offerPricePlanDto.getTaxId())
				&& !StringUtils.isBlank(offerPricePlanDto.getBillingPeriod())) {

			Provider provider = providerService.findById(offerPricePlanDto
					.getProviderId());
			User currentUser = userService.findById(offerPricePlanDto
					.getCurrentUserId());

			try {
				offerPricePlanDto.setOfferId(asgIdMappingService.getNewCode(em,
						offerPricePlanDto.getOfferId(), EntityCodeEnum.OPF));

				offerPricePlanDto.setOrganizationId(asgIdMappingService
						.getMeveoCode(em,
								offerPricePlanDto.getOrganizationId(),
								EntityCodeEnum.ORG));
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			Calendar calendar = calendarService.findByName(em,
					offerPricePlanDto.getBillingPeriod().toString());
			if (calendar == null) {
				throw new MeveoApiException("Calendar with name="
						+ offerPricePlanDto.getBillingPeriod()
						+ " does not exists.");
			}
			Seller seller = sellerService.findByCode(em,
					offerPricePlanDto.getOrganizationId(), provider);

			// get invoice sub category
			Tax tax = taxService.findByCode(em, offerPricePlanDto.getTaxId());
			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryCountryService
					.findByTaxId(em, tax).getInvoiceSubCategory();

			ServiceTemplate serviceTemplate = createServiceTemplate(false,
					offerPricePlanDto, currentUser, provider);
			RecurringChargeTemplate recurringChargeTemplate = createRecurringChargeTemplate(
					false, offerPricePlanDto, currentUser, provider,
					invoiceSubCategory, calendar, seller);
			OneShotChargeTemplate subscriptionTemplate = createSubscriptionTemplate(
					false, offerPricePlanDto, currentUser, provider,
					invoiceSubCategory, seller);
			OneShotChargeTemplate terminationTemplate = createTerminationTemplate(
					false, offerPricePlanDto, currentUser, provider,
					invoiceSubCategory, seller);
			List<ServiceUsageChargeTemplate> serviceUsageChargeTemplates = createServiceUsageChargeTemplate(
					false, offerPricePlanDto, currentUser, provider,
					invoiceSubCategory, calendar, serviceTemplate, seller);

			serviceTemplate.getRecurringCharges().add(recurringChargeTemplate);
			serviceTemplate.getSubscriptionCharges().add(subscriptionTemplate);
			serviceTemplate.getTerminationCharges().add(terminationTemplate);
			serviceTemplate.setServiceUsageCharges(serviceUsageChargeTemplates);
			serviceTemplateService.update(em, serviceTemplate, currentUser);

			String offerTemplatePrefix = paramBean.getProperty(
					"asg.api.offer.charged.prefix", "_CH_OF_");
			List<ServiceTemplate> serviceTemplates = new ArrayList<ServiceTemplate>();
			serviceTemplates.add(serviceTemplate);
			String offerTemplateCode = offerTemplatePrefix
					+ offerPricePlanDto.getOfferId() + "_"
					+ offerPricePlanDto.getOrganizationId();
			OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
					offerTemplateCode, provider);
			if (offerTemplate != null) {
				offerTemplate.setServiceTemplates(serviceTemplates);
				offerTemplateService.update(em, offerTemplate, currentUser);
			}

			// recommended prices
			ServiceTemplate recommendedServiceTemplate = createServiceTemplate(
					true, offerPricePlanDto, currentUser, provider);
			RecurringChargeTemplate recommendedRecurringChargeTemplate = createRecurringChargeTemplate(
					true, offerPricePlanDto, currentUser, provider,
					invoiceSubCategory, calendar, seller);
			OneShotChargeTemplate recommendedSubscriptionTemplate = createSubscriptionTemplate(
					true, offerPricePlanDto, currentUser, provider,
					invoiceSubCategory, seller);
			OneShotChargeTemplate recommendedTerminationTemplate = createTerminationTemplate(
					true, offerPricePlanDto, currentUser, provider,
					invoiceSubCategory, seller);
			List<ServiceUsageChargeTemplate> recommendedServiceUsageChargeTemplates = createServiceUsageChargeTemplate(
					true, offerPricePlanDto, currentUser, provider,
					invoiceSubCategory, calendar, recommendedServiceTemplate,
					seller);

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

			String recommendedOfferTemplatePrefix = paramBean.getProperty(
					"asg.api.recommended.offer.charged.prefix", "_REC_CH_OF_");
			List<ServiceTemplate> recommendedServiceTemplates = new ArrayList<ServiceTemplate>();
			recommendedServiceTemplates.add(recommendedServiceTemplate);
			String recommendedOfferTemplateCode = recommendedOfferTemplatePrefix
					+ offerPricePlanDto.getOfferId()
					+ "_"
					+ offerPricePlanDto.getOrganizationId();

			OfferTemplate recommendedOfferTemplate = offerTemplateService
					.findByCode(em, recommendedOfferTemplateCode, provider);
			if (recommendedOfferTemplate != null) {
				recommendedOfferTemplate
						.setServiceTemplates(recommendedServiceTemplates);
				offerTemplateService.update(em, recommendedOfferTemplate,
						currentUser);
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(offerPricePlanDto.getOfferId())) {
				missingFields.add("serviceId");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getOrganizationId())) {
				missingFields.add("organizationId");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getBillingPeriod())) {
				missingFields.add("billingPeriod");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getTaxId())) {
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
			OfferPricePlanDto offerPricePlanDto, User currentUser,
			Provider provider) throws MeveoApiException {
		// Create a charged service for defined offer and organization.
		// Service code is '_CH_OF_[OrganizationId]_[OferId]'. Prefix '_CH_OF_'
		// must be settable in properties file.
		String offerTemplatePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.offer.charged.prefix",
						"_REC_CH_OF_") : paramBean.getProperty(
				"asg.api.offer.charged.prefix", "_CH_OF_");

		String serviceTemplateCode = offerTemplatePrefix
				+ offerPricePlanDto.getOfferId() + "_"
				+ offerPricePlanDto.getOrganizationId();

		// check if template exists
		if (serviceTemplateService
				.findByCode(em, serviceTemplateCode, provider) != null) {
			throw new MeveoApiException("Service template with code="
					+ serviceTemplateCode + " already exists.");
		}

		ServiceTemplate serviceTemplate = new ServiceTemplate();
		serviceTemplate.setCode(serviceTemplateCode);
		serviceTemplate.setActive(true);
		serviceTemplateService.create(em, serviceTemplate, currentUser,
				provider);

		return serviceTemplate;
	}

	private RecurringChargeTemplate createRecurringChargeTemplate(
			boolean isRecommendedPrice, OfferPricePlanDto offerPricePlanDto,
			User currentUser, Provider provider,
			InvoiceSubCategory invoiceSubCategory, Calendar calendar,
			Seller seller) {
		// Create a recurring charge with associated services and
		// parameters. Charge code is'_RE_OF_[OrganizationId]_[OfferId]'
		// ('_RE_OF_' must be settable). Charge is associate to step 1
		// service.
		String recurringChargePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.offer.recurring.prefix",
						"_REC_RE_OF_") : paramBean.getProperty(
				"asg.api.offer.recurring.prefix", "_RE_OF_");
		RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
		recurringChargeTemplate.setActive(true);
		recurringChargeTemplate.setCode(recurringChargePrefix
				+ offerPricePlanDto.getOfferId() + "_"
				+ offerPricePlanDto.getOrganizationId());
		recurringChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
		recurringChargeTemplate.setRecurrenceType(RecurrenceTypeEnum.CALENDAR);
		recurringChargeTemplate.setSubscriptionProrata(offerPricePlanDto
				.getSubscriptionProrata());
		recurringChargeTemplate.setTerminationProrata(offerPricePlanDto
				.getTerminationProrata());
		recurringChargeTemplate.setApplyInAdvance(offerPricePlanDto
				.getApplyInAdvance());
		recurringChargeTemplate.setType(OperationTypeEnum.CREDIT);
		recurringChargeTemplate.setCalendar(calendar);
		recurringChargeTemplateService.create(em, recurringChargeTemplate,
				currentUser, provider);

		// create price plans
		if (offerPricePlanDto.getRecurringCharges() != null
				&& offerPricePlanDto.getRecurringCharges().size() > 0) {
			for (RecurringChargeDto recurringChargeDto : offerPricePlanDto
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
				pricePlanMatrix
						.setCriteria1Value(offerPricePlanDto.getParam1());
				pricePlanMatrix
						.setCriteria2Value(offerPricePlanDto.getParam2());
				pricePlanMatrix
						.setCriteria3Value(offerPricePlanDto.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}
		}

		return recurringChargeTemplate;
	}

	private OneShotChargeTemplate createSubscriptionTemplate(
			boolean isRecommendedPrice, OfferPricePlanDto offerPricePlanDto,
			User currentUser, Provider provider,
			InvoiceSubCategory invoiceSubCategory, Seller seller) {
		// Create a subscription one point charge. Charge code
		// is'_SO_OF_[OrganizationId]_[OfferId]' ('_SO_OF_' must be
		// settable). Charge is associate to step 1 service.
		String subscriptionPointChargePrefix = isRecommendedPrice ? paramBean
				.getProperty(
						"asg.api.recommended.offer.subscription.point.charge.prefix",
						"_REC_SO_OF_")
				: paramBean.getProperty(
						"asg.api.offer.subscription.point.charge.prefix",
						"_SO_OF_");
		OneShotChargeTemplate subscriptionTemplate = new OneShotChargeTemplate();
		subscriptionTemplate.setActive(true);
		subscriptionTemplate.setCode(subscriptionPointChargePrefix
				+ offerPricePlanDto.getOfferId() + "_"
				+ offerPricePlanDto.getOrganizationId());
		subscriptionTemplate.setInvoiceSubCategory(invoiceSubCategory);
		subscriptionTemplate
				.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.SUBSCRIPTION);
		oneShotChargeTemplateService.create(em, subscriptionTemplate,
				currentUser, provider);

		if (offerPricePlanDto.getSubscriptionFees() != null
				&& offerPricePlanDto.getSubscriptionFees().size() > 0) {
			for (SubscriptionFeeDto subscriptionFeeDto : offerPricePlanDto
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
				pricePlanMatrix
						.setCriteria1Value(offerPricePlanDto.getParam1());
				pricePlanMatrix
						.setCriteria2Value(offerPricePlanDto.getParam2());
				pricePlanMatrix
						.setCriteria3Value(offerPricePlanDto.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}
		}

		return subscriptionTemplate;
	}

	private OneShotChargeTemplate createTerminationTemplate(
			boolean isRecommendedPrice, OfferPricePlanDto offerPricePlanDto,
			User currentUser, Provider provider,
			InvoiceSubCategory invoiceSubCategory, Seller seller) {
		// Create e termination point charge. Charge code is
		// '_TE_OF_[OrganizationId]_[OfferId]' ('_TE_OF_' must be settable).
		// Charge is associate to step 1 service.
		String terminationPointChargePrefix = isRecommendedPrice ? paramBean
				.getProperty(
						"asg.api.recommended.offer.termination.point.charge.prefix",
						"_REC_TE_OF_")
				: paramBean.getProperty(
						"asg.api.offer.termination.point.charge.prefix",
						"_TE_OF_");
		OneShotChargeTemplate terminationTemplate = new OneShotChargeTemplate();
		terminationTemplate.setActive(true);
		terminationTemplate.setCode(terminationPointChargePrefix
				+ offerPricePlanDto.getOfferId() + "_"
				+ offerPricePlanDto.getOrganizationId());
		terminationTemplate.setInvoiceSubCategory(invoiceSubCategory);
		terminationTemplate
				.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.TERMINATION);
		oneShotChargeTemplateService.create(em, terminationTemplate,
				currentUser, provider);

		if (offerPricePlanDto.getTerminationFees() != null
				&& offerPricePlanDto.getTerminationFees().size() > 0) {
			for (TerminationFeeDto terminationFeeDto : offerPricePlanDto
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
				pricePlanMatrix
						.setCriteria1Value(offerPricePlanDto.getParam1());
				pricePlanMatrix
						.setCriteria2Value(offerPricePlanDto.getParam2());
				pricePlanMatrix
						.setCriteria3Value(offerPricePlanDto.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}
		}

		return terminationTemplate;
	}

	private List<ServiceUsageChargeTemplate> createServiceUsageChargeTemplate(
			boolean isRecommendedPrice, OfferPricePlanDto offerPricePlanDto,
			User currentUser, Provider provider,
			InvoiceSubCategory invoiceSubCategory, Calendar calendar,
			ServiceTemplate serviceTemplate, Seller seller) {
		String offerTemplatePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.offer.charged.prefix",
						"_REC_CH_OF_") : paramBean.getProperty(
				"asg.api.offer.charged.prefix", "_CH_OF_");

		List<ServiceUsageChargeTemplate> serviceUsageChargeTemplates = new ArrayList<ServiceUsageChargeTemplate>();
		for (UsageChargeDto usageChargeDto : offerPricePlanDto
				.getUsageCharges()) {
			// Create a counter for each min range values used as
			// parameters.
			// Counters codes are '_SE_[OrganizationId]_[ServiceId]_[Valeur
			// Min]' ('_SE_' must be settable). Counters are ordered by
			// values.
			CounterTemplate counterTemplate = new CounterTemplate();
			counterTemplate.setCode(offerTemplatePrefix
					+ offerPricePlanDto.getOfferId() + "_"
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ usageChargeDto.getMin());
			counterTemplate.setCounterType(CounterTypeEnum.QUANTITY);
			counterTemplate.setCalendar(calendar);
			counterTemplate.setUnityDescription(offerPricePlanDto
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
							"asg.api.recommended.offer.usage.charged.prefix",
							"_REC_US_OF_") : paramBean.getProperty(
					"asg.api.offer.usage.charged.prefix", "_US_OF_");
			UsageChargeTemplate usageChargeTemplate = new UsageChargeTemplate();
			usageChargeTemplate.setCode(usageChargeTemplatePrefix
					+ offerPricePlanDto.getOfferId() + "_"
					+ offerPricePlanDto.getOrganizationId() + "_" + min);
			usageChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			usageChargeTemplate.setUnityFormatter(UsageChgTemplateEnum.INTEGER);
			usageChargeTemplate.setUnityDescription(offerPricePlanDto
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
			pricePlanMatrix.setCriteria1Value(offerPricePlanDto.getParam1());
			pricePlanMatrix.setCriteria2Value(offerPricePlanDto.getParam2());
			pricePlanMatrix.setCriteria3Value(offerPricePlanDto.getParam3());
			pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
					provider);
		}

		return serviceUsageChargeTemplates;
	}

	public void remove(String offerId, String organizationId, Long userId,
			Long providerId) throws MeveoApiException {

		if (StringUtils.isBlank(offerId) || StringUtils.isBlank(organizationId)) {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(offerId)) {
				missingFields.add("offerId");
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

		removeOffer(true, offerId, organizationId, userId, providerId);
		removeOffer(false, offerId, organizationId, userId, providerId);
	}

	public void removeOffer(boolean isRecommendedPrice, String offerId,
			String organizationId, Long userId, Long providerId)
			throws MeveoApiException {

		Provider provider = providerService.findById(providerId);
		User currentUser = userService.findById(userId);

		try {
			offerId = asgIdMappingService.getMeveoCode(em, offerId,
					EntityCodeEnum.OPF);

			organizationId = asgIdMappingService.getMeveoCode(em,
					organizationId, EntityCodeEnum.ORG);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}

		String offerTemplatePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.offer.charged.prefix",
						"_REC_CH_OF_") : paramBean.getProperty(
				"asg.api.offer.charged.prefix", "_CH_OF_");

		String offerTemplateCode = offerTemplatePrefix + offerId + "_"
				+ organizationId;

		String serviceTemplateCode = offerTemplatePrefix + offerId + "_"
				+ organizationId;

		try {
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
							"asg.api.recommended.offer.subscription.point.charge.prefix",
							"_REC_SO_OF_")
					: paramBean.getProperty(
							"asg.api.offer.subscription.point.charge.prefix",
							"_SO_OF_");
			String subscriptionTemplateCode = subscriptionPointChargePrefix
					+ offerId + "_" + organizationId;

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
							"asg.api.recommended.offer.termination.point.charge.prefix",
							"_REC_TE_OF_")
					: paramBean.getProperty(
							"asg.api.offer.termination.point.charge.prefix",
							"_TE_OF_");
			String terminationTemplateCode = terminationPointChargePrefix
					+ offerId + "_" + organizationId;

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
							"asg.api.recommended.offer.usage.charged.prefix",
							"_REC_US_OF_") : paramBean.getProperty(
					"asg.api.offer.usage.charged.prefix", "_US_OF_");
			String usageChargeCode = usageChargeTemplatePrefix + offerId + "_"
					+ organizationId;

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

			counterTemplateService.removeByPrefix(em, offerTemplateCode,
					provider);

			// delete recurring charge
			String recurringChargePrefix = isRecommendedPrice ? paramBean
					.getProperty("asg.api.recommended.offer.recurring.prefix",
							"_REC_RE_OF_") : paramBean.getProperty(
					"asg.api.offer.recurring.prefix", "_RE_OF_");
			String recurringChargeCode = recurringChargePrefix + offerId + "_"
					+ organizationId;

			// delete price plan
			pricePlanMatrixService.removeByCode(em, recurringChargeCode,
					provider);
			RecurringChargeTemplate recurringChargeTemplate = recurringChargeTemplateService
					.findByCode(em, recurringChargeCode, provider);
			if (recurringChargeTemplate != null) {
				recurringChargeTemplateService.remove(em,
						recurringChargeTemplate);
			}

			// remove service template
			if (serviceTemplate != null) {
				List<OfferTemplate> offerTemplates = offerTemplateService
						.findByServiceTemplate(em, serviceTemplate, provider);
				if (offerTemplates != null) {
					for (OfferTemplate ot : offerTemplates) {
						ot.getServiceTemplates().remove(serviceTemplate);
						offerTemplateService.update(em, ot, currentUser);
					}
				}
				serviceTemplateService.remove(em, serviceTemplate);
			}
		} catch (Exception e) {
			log.error("Error deleting offer price plan with code={}: {}",
					offerTemplateCode, e.getMessage());
			throw new MeveoApiException(
					"Failed deleting offerPricePlan with code="
							+ offerTemplateCode + ".");
		}
	}

	public void update(OfferPricePlanDto offerPricePlanDto)
			throws MeveoApiException {
		if (!StringUtils.isBlank(offerPricePlanDto.getOfferId())
				&& !StringUtils.isBlank(offerPricePlanDto.getOrganizationId())) {

			Provider provider = providerService.findById(offerPricePlanDto
					.getProviderId());
			User currentUser = userService.findById(offerPricePlanDto
					.getCurrentUserId());

			try {
				offerPricePlanDto
						.setOfferId(asgIdMappingService.getMeveoCode(em,
								offerPricePlanDto.getOfferId(),
								EntityCodeEnum.OPF));

				offerPricePlanDto.setOrganizationId(asgIdMappingService
						.getMeveoCode(em,
								offerPricePlanDto.getOrganizationId(),
								EntityCodeEnum.ORG));
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			updateRecurringChargeTemplate(false, offerPricePlanDto,
					currentUser, provider);
			updateSubscriptionTemplate(false, offerPricePlanDto, currentUser,
					provider);
			updateTerminationTemplate(false, offerPricePlanDto, currentUser,
					provider);
			updateServiceUsageChargeTemplate(false, offerPricePlanDto,
					currentUser, provider);

			// recommended prices
			updateRecurringChargeTemplate(true, offerPricePlanDto, currentUser,
					provider);
			updateSubscriptionTemplate(true, offerPricePlanDto, currentUser,
					provider);
			updateTerminationTemplate(true, offerPricePlanDto, currentUser,
					provider);
			updateServiceUsageChargeTemplate(true, offerPricePlanDto,
					currentUser, provider);

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(offerPricePlanDto.getOfferId())) {
				missingFields.add("serviceId");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getOrganizationId())) {
				missingFields.add("organizationId");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getBillingPeriod())) {
				missingFields.add("billingPeriod");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getTaxId())) {
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

	private void updateRecurringChargeTemplate(boolean isRecommendedPrice,
			OfferPricePlanDto offerPricePlanDto, User currentUser,
			Provider provider) {
		// Create a recurring charge with associated services and
		// parameters. Charge code is'_RE_OF_[OrganizationId]_[OfferId]'
		// ('_RE_OF_' must be settable). Charge is associate to step 1
		// service.
		String recurringChargePrefix = isRecommendedPrice ? paramBean
				.getProperty("asg.api.recommended.offer.recurring.prefix",
						"_REC_RE_OF_") : paramBean.getProperty(
				"asg.api.offer.recurring.prefix", "_RE_OF_");
		String recurringChargeCode = recurringChargePrefix
				+ offerPricePlanDto.getOfferId() + "_"
				+ offerPricePlanDto.getOrganizationId();

		// create price plans
		if (offerPricePlanDto.getRecurringCharges() != null
				&& offerPricePlanDto.getRecurringCharges().size() > 0) {
			for (RecurringChargeDto recurringChargeDto : offerPricePlanDto
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
			OfferPricePlanDto offerPricePlanDto, User currentUser,
			Provider provider) {
		String subscriptionPointChargePrefix = isRecommendedPrice ? paramBean
				.getProperty(
						"asg.api.recommended.offer.subscription.point.charge.prefix",
						"_REC_SO_OF_")
				: paramBean.getProperty(
						"asg.api.offer.subscription.point.charge.prefix",
						"_SO_OF_");
		String subscriptionPointChargeCode = subscriptionPointChargePrefix
				+ offerPricePlanDto.getOfferId() + "_"
				+ offerPricePlanDto.getOrganizationId();

		if (offerPricePlanDto.getSubscriptionFees() != null
				&& offerPricePlanDto.getSubscriptionFees().size() > 0) {
			for (SubscriptionFeeDto subscriptionFeeDto : offerPricePlanDto
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
			OfferPricePlanDto offerPricePlanDto, User currentUser,
			Provider provider) {

		String terminationPointChargePrefix = isRecommendedPrice ? paramBean
				.getProperty(
						"asg.api.recommended.offer.termination.point.charge.prefix",
						"_REC_TE_OF_")
				: paramBean.getProperty(
						"asg.api.offer.termination.point.charge.prefix",
						"_TE_OF_");
		String terminationPointChargeCode = terminationPointChargePrefix
				+ offerPricePlanDto.getOfferId() + "_"
				+ offerPricePlanDto.getOrganizationId();

		if (offerPricePlanDto.getTerminationFees() != null
				&& offerPricePlanDto.getTerminationFees().size() > 0) {
			for (TerminationFeeDto terminationFeeDto : offerPricePlanDto
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

	private void updateServiceUsageChargeTemplate(boolean isRecommendedPrice,
			OfferPricePlanDto offerPricePlanDto, User currentUser,
			Provider provider) {

		for (UsageChargeDto usageChargeDto : offerPricePlanDto
				.getUsageCharges()) {
			Integer min = 0;
			if (usageChargeDto.getMin() != null) {
				min = usageChargeDto.getMin();
			}

			String usageChargeTemplatePrefix = isRecommendedPrice ? paramBean
					.getProperty(
							"asg.api.recommended.offer.usage.charged.prefix",
							"_REC_US_OF_") : paramBean.getProperty(
					"asg.api.offer.usage.charged.prefix", "_US_OF_");
			String usageChargeTemplateCode = usageChargeTemplatePrefix
					+ offerPricePlanDto.getOfferId() + "_"
					+ offerPricePlanDto.getOrganizationId() + "_" + min;

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(
							usageChargeDto.getCurrencyCode(), provider);

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
