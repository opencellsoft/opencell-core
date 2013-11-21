package org.meveo.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.OfferPricePlanDto;
import org.meveo.api.dto.RecurringChargeDto;
import org.meveo.api.dto.SubscriptionFeeDto;
import org.meveo.api.dto.TerminationFeeDto;
import org.meveo.api.dto.UsageChargeDto;
import org.meveo.api.exception.MeveoApiException;
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
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
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
public class OfferPricePlanServiceApi extends BaseApi {

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
	private InvoiceSubCategoryService invoiceSubCategoryService;

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

			// Create a charged service for defined offer and organization.
			// Service code is '_CH_OF_[OrganizationId]_[OferId]'. Prefix '_CH_OF_'
			// must be settable in properties file.
			String offerTemplatePrefix = paramBean.getProperty(
					"asg.api.offer.charged.prefix", "_CH_OF_");

			String serviceTemplateCode = offerTemplatePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId();

			// check if template exists
			if (serviceTemplateService.findByCode(em, serviceTemplateCode,
					provider) != null) {
				throw new MeveoApiException("Service template with code="
						+ serviceTemplateCode + " already exists.");
			}

			ServiceTemplate serviceTemplate = new ServiceTemplate();
			serviceTemplate.setCode(offerTemplatePrefix
					+ offerPricePlanDto.getOfferId() + "_"
					+ offerPricePlanDto.getOrganizationId());
			serviceTemplate.setActive(true);
			serviceTemplateService.create(em, serviceTemplate, currentUser,
					provider);

			// Create a recurring charge with associated services and
			// parameters. Charge code is'_RE_OF_[OrganizationId]_[OfferId]'
			// ('_RE_OF_' must be settable). Charge is associate to step 1
			// service.
			String recurringChargePrefix = paramBean.getProperty(
					"asg.api.offer.recurring.prefix", "_RE_OF_");
			RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
			recurringChargeTemplate.setActive(true);
			recurringChargeTemplate.setCode(recurringChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			recurringChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			recurringChargeTemplate
					.setRecurrenceType(RecurrenceTypeEnum.CALENDAR);
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
									recurringChargeDto.getCurrencyCode(),
									provider);

					PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
					pricePlanMatrix.setEventCode(recurringChargeTemplate
							.getCode());
					pricePlanMatrix.setAmountWithoutTax(recurringChargeDto
							.getPrice());
					pricePlanMatrix.setTradingCurrency(tradingCurrency);
					pricePlanMatrix.setStartRatingDate(recurringChargeDto
							.getStartDate());
					pricePlanMatrix.setSeller(seller);
					pricePlanMatrix.setEndRatingDate(recurringChargeDto
							.getEndDate());
					pricePlanMatrix.setMinSubscriptionAgeInMonth(Long
							.valueOf(recurringChargeDto.getMinAge()));
					pricePlanMatrix.setMaxSubscriptionAgeInMonth(Long
							.valueOf(recurringChargeDto.getMaxAge()));
					pricePlanMatrix.setCriteria1Value(offerPricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(offerPricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(offerPricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

			// Create a subscription one point charge. Charge code
			// is'_SO_OF_[OrganizationId]_[OfferId]' ('_SO_OF_' must be
			// settable). Charge is associate to step 1 service.
			String subscriptionPointChargePrefix = paramBean
					.getProperty(
							"asg.api.offer.subscription.point.charge.prefix",
							"_SO_OF_");
			OneShotChargeTemplate subscriptionTemplate = new OneShotChargeTemplate();
			subscriptionTemplate.setActive(true);
			subscriptionTemplate.setCode(subscriptionPointChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
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
									subscriptionFeeDto.getCurrencyCode(),
									provider);

					PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
					pricePlanMatrix
							.setEventCode(subscriptionTemplate.getCode());
					pricePlanMatrix.setAmountWithoutTax(subscriptionFeeDto
							.getPrice());
					pricePlanMatrix.setTradingCurrency(tradingCurrency);
					pricePlanMatrix.setStartRatingDate(subscriptionFeeDto
							.getStartDate());
					pricePlanMatrix.setSeller(seller);
					pricePlanMatrix.setEndRatingDate(subscriptionFeeDto
							.getEndDate());
					pricePlanMatrix.setCriteria1Value(offerPricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(offerPricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(offerPricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

			// Create e termination point charge. Charge code is
			// '_TE_OF_[OrganizationId]_[OfferId]' ('_TE_OF_' must be settable).
			// Charge is associate to step 1 service.
			String terminationPointChargePrefix = paramBean.getProperty(
					"asg.api.offer.termination.point.charge.prefix", "_TE_OF_");
			OneShotChargeTemplate terminationTemplate = new OneShotChargeTemplate();
			terminationTemplate.setActive(true);
			terminationTemplate.setCode(terminationPointChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
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
									terminationFeeDto.getCurrencyCode(),
									provider);

					PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
					pricePlanMatrix.setEventCode(terminationTemplate.getCode());
					pricePlanMatrix.setAmountWithoutTax(terminationFeeDto
							.getPrice());
					pricePlanMatrix.setTradingCurrency(tradingCurrency);
					pricePlanMatrix.setStartRatingDate(terminationFeeDto
							.getStartDate());
					pricePlanMatrix.setSeller(seller);
					pricePlanMatrix.setEndRatingDate(terminationFeeDto
							.getEndDate());
					pricePlanMatrix.setCriteria1Value(offerPricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(offerPricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(offerPricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

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
				String usageChargeTemplatePrefix = paramBean.getProperty(
						"asg.api.offer.usage.charged.prefix", "_US_OF_");
				UsageChargeTemplate usageChargeTemplate = new UsageChargeTemplate();
				usageChargeTemplate.setCode(usageChargeTemplatePrefix
						+ offerPricePlanDto.getOfferId() + "_"
						+ offerPricePlanDto.getOrganizationId() + "_" + min);
				usageChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
				usageChargeTemplate
						.setUnityFormatter(UsageChgTemplateEnum.INTEGER);
				usageChargeTemplate.setUnityDescription(offerPricePlanDto
						.getUsageUnit());
				usageChargeTemplate.setPriority(min);
				usageChargeTemplateService.create(em, usageChargeTemplate,
						currentUser, provider);

				ServiceUsageChargeTemplate serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
				serviceUsageChargeTemplate
						.setChargeTemplate(usageChargeTemplate);
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
				pricePlanMatrix.setAmountWithoutTax(usageChargeDto.getPrice());
				pricePlanMatrix.setTradingCurrency(tradingCurrency);
				pricePlanMatrix.setStartRatingDate(usageChargeDto
						.getStartDate());
				pricePlanMatrix.setSeller(seller);
				pricePlanMatrix.setEndRatingDate(usageChargeDto.getEndDate());
				pricePlanMatrix
						.setCriteria1Value(offerPricePlanDto.getParam1());
				pricePlanMatrix
						.setCriteria2Value(offerPricePlanDto.getParam2());
				pricePlanMatrix
						.setCriteria3Value(offerPricePlanDto.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}

			serviceTemplate.getRecurringCharges().add(recurringChargeTemplate);
			serviceTemplate.getSubscriptionCharges().add(subscriptionTemplate);
			serviceTemplate.getTerminationCharges().add(terminationTemplate);
			serviceTemplate.setServiceUsageCharges(serviceUsageChargeTemplates);
			serviceTemplateService.update(em, serviceTemplate, currentUser);

			List<ServiceTemplate> serviceTemplates = new ArrayList<ServiceTemplate>();
			serviceTemplates.add(serviceTemplate);
			String offerTemplateCode = offerTemplatePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId();
			OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
					offerTemplateCode, provider);
			if (offerTemplate != null) {
				offerTemplate.setServiceTemplates(serviceTemplates);
				offerTemplateService.update(em, offerTemplate, currentUser);
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

			throw new MeveoApiException(sb.toString());
		}
	}

	public void remove(String offerId, String organizationId, Long userId,
			Long providerId) throws MeveoApiException {
		Provider provider = providerService.findById(providerId);
		User currentUser = userService.findById(userId);

		String offerCodePrefix = paramBean.getProperty(
				"asg.api.offer.offer.prefix", "_OF_");
		String offerTemplateCode = offerCodePrefix + organizationId + "_"
				+ offerId;

		try {
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(em, offerTemplateCode, provider);

			if (serviceTemplate != null) {
				serviceTemplate.setRecurringCharges(null);
				serviceTemplate.setSubscriptionCharges(null);
				serviceTemplate.setTerminationCharges(null);
				serviceTemplate.setServiceUsageCharges(null);
				serviceTemplateService.update(em, serviceTemplate, currentUser);
			}

			// delete usageCharge link
			String usageChargeTemplatePrefix = paramBean.getProperty(
					"asg.api.offer.usage.charged.prefix", "_US_OF_");
			String usageChargeCode = usageChargeTemplatePrefix + organizationId
					+ "_" + offerId;

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

			// delete subscription fee
			String subscriptionPointChargePrefix = paramBean
					.getProperty(
							"asg.api.offer.subscription.point.charge.prefix",
							"_SO_OF_");
			String subscriptionTemplateCode = subscriptionPointChargePrefix
					+ organizationId + "_" + offerId;

			// delete price plan
			pricePlanMatrixService.removeByCode(em, subscriptionTemplateCode,
					provider);

			OneShotChargeTemplate subscriptionTemplate = oneShotChargeTemplateService
					.findByCode(em, subscriptionTemplateCode, provider);
			if (subscriptionTemplate != null) {
				oneShotChargeTemplateService.remove(em, subscriptionTemplate);
			}

			// delete termination fee
			String terminationPointChargePrefix = paramBean.getProperty(
					"asg.api.offer.termination.point.charge.prefix", "_TE_OF_");
			String terminationTemplateCode = terminationPointChargePrefix
					+ organizationId + "_" + offerId;

			// delete price plan
			pricePlanMatrixService.removeByCode(em, terminationTemplateCode,
					provider);

			OneShotChargeTemplate terminationTemplate = oneShotChargeTemplateService
					.findByCode(em, terminationTemplateCode, provider);
			if (terminationTemplate != null) {
				oneShotChargeTemplateService.remove(em, terminationTemplate);
			}

			// delete recurring charge
			String recurringChargePrefix = paramBean.getProperty(
					"asg.api.offer.recurring.prefix", "_RE_OF_");
			String recurringChargeCode = recurringChargePrefix + organizationId
					+ "_" + offerId;

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
			log.error("Error deleting offer price plan with code={}: {}",
					offerTemplateCode, e.getMessage());
			throw new MeveoApiException(
					"Failed deleting offerPricePlan with code="
							+ offerTemplateCode + ".");
		}
	}

}
