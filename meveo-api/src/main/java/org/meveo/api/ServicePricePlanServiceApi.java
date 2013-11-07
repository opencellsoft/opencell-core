package org.meveo.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.RecurringChargeDto;
import org.meveo.api.dto.ServicePricePlanDto;
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
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.ServiceUsageChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ServicePricePlanServiceApi extends BaseApi {

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
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

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

			String serviceOfferCodePrefix = paramBean.getProperty(
					"asg.api.service.offer.prefix", "_SE_");

			Calendar calendar = calendarService.findByName(em,
					servicePricePlanDto.getBillingPeriod().toString());
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

			// Create a charged service for offer linked to service and
			// organization. Service code is '_SE_[OrganizationId]_[ServiceId]'.
			// '_SE_' must be settable in properties file..

			String serviceTemplateCode = serviceOfferCodePrefix
					+ servicePricePlanDto.getOrganizationId() + "_"
					+ servicePricePlanDto.getServiceId();
			// check if template exists
			if (serviceTemplateService.findByCode(em, serviceTemplateCode,
					provider) != null) {
				throw new MeveoApiException("Service template with code="
						+ serviceTemplateCode + " already exists.");
			}

			ServiceTemplate serviceTemplate = new ServiceTemplate();
			serviceTemplate.setCode(serviceTemplateCode);

			serviceTemplate.setActive(true);
			serviceTemplateService.create(em, serviceTemplate,
					currentUser, provider);

			// Create a recurring charge with service descriptions and
			// parameters. Charge code is '_RE_SE_[OrganizationId]_[ServceId]'
			// ('_RE_SE_' must be settable). This charge must be associated to
			// step 1 service.
			String recurringChargePrefix = paramBean.getProperty(
					"asg.api.service.recurring.prefix", "_RE_SE_");
			RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
			recurringChargeTemplate.setActive(true);
			recurringChargeTemplate.setCode(recurringChargePrefix
					+ servicePricePlanDto.getOrganizationId() + "_"
					+ servicePricePlanDto.getServiceId());
			recurringChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			recurringChargeTemplate
					.setRecurrenceType(RecurrenceTypeEnum.CALENDAR);
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
					pricePlanMatrix.setCriteria1Value(servicePricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(servicePricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(servicePricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

			// Create a subscription point charge. Charge code is
			// '_SO_SE_[OrganizationId]_[ServiceId]' ('_SO_SE_' must be
			// settable). This charge must be associated to step 1 service.
			String subscriptionPointChargePrefix = paramBean.getProperty(
					"asg.api.service.subscription.point.charge.prefix",
					"_SO_SE_");
			OneShotChargeTemplate subscriptionTemplate = new OneShotChargeTemplate();
			subscriptionTemplate.setActive(true);
			subscriptionTemplate.setCode(subscriptionPointChargePrefix
					+ servicePricePlanDto.getOrganizationId() + "_"
					+ servicePricePlanDto.getServiceId());
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
									subscriptionFeeDto.getCurrencyCode(),
									provider);

					PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
					pricePlanMatrix.setEventCode(subscriptionTemplate
							.getCode());
					pricePlanMatrix.setAmountWithoutTax(subscriptionFeeDto
							.getPrice());
					pricePlanMatrix.setTradingCurrency(tradingCurrency);
					pricePlanMatrix.setStartRatingDate(subscriptionFeeDto
							.getStartDate());
					pricePlanMatrix.setSeller(seller);
					pricePlanMatrix.setEndRatingDate(subscriptionFeeDto
							.getEndDate());
					pricePlanMatrix.setCriteria1Value(servicePricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(servicePricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(servicePricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

			// Create a termination point charge. Charge code is
			// '_TE_SE_[OrganizationId]_[ServiceId]' ('_TE_SE_' must be
			// settable). This charge must be associated to step 1 service.
			String terminationPointChargePrefix = paramBean.getProperty(
					"asg.api.service.termination.point.charge.prefix",
					"_TE_SE_");
			OneShotChargeTemplate terminationTemplate = new OneShotChargeTemplate();
			terminationTemplate.setActive(true);
			terminationTemplate.setCode(terminationPointChargePrefix
					+ servicePricePlanDto.getOrganizationId() + "_"
					+ servicePricePlanDto.getServiceId());
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
									terminationFeeDto.getCurrencyCode(),
									provider);

					PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
					pricePlanMatrix.setEventCode(terminationTemplate
							.getCode());
					pricePlanMatrix.setAmountWithoutTax(terminationFeeDto
							.getPrice());
					pricePlanMatrix.setTradingCurrency(tradingCurrency);
					pricePlanMatrix.setStartRatingDate(terminationFeeDto
							.getStartDate());
					pricePlanMatrix.setSeller(seller);
					pricePlanMatrix.setEndRatingDate(terminationFeeDto
							.getEndDate());
					pricePlanMatrix.setCriteria1Value(servicePricePlanDto
							.getParam1());
					pricePlanMatrix.setCriteria2Value(servicePricePlanDto
							.getParam2());
					pricePlanMatrix.setCriteria3Value(servicePricePlanDto
							.getParam3());
					pricePlanMatrixService.create(em, pricePlanMatrix,
							currentUser, provider);
				}
			}

			for (UsageChargeDto usageChargeDto : servicePricePlanDto
					.getUsageCharges()) {
				// Create a counter for each min range values used as
				// parameters.
				// Counters codes are '_SE_[OrganizationId]_[ServiceId]_[Valeur
				// Min]' ('_SE_' must be settable). Counters are ordered by
				// values.
				CounterTemplate counterTemplate = new CounterTemplate();
				counterTemplate.setCode(serviceOfferCodePrefix
						+ servicePricePlanDto.getOrganizationId() + "_"
						+ servicePricePlanDto.getServiceId() + "_"
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
				String usageChargeTemplatePrefix = paramBean.getProperty(
						"asg.api.service.usage.charged.prefix", "_US_SE_");
				UsageChargeTemplate usageChargeTemplate = new UsageChargeTemplate();
				usageChargeTemplate.setCode(usageChargeTemplatePrefix
						+ servicePricePlanDto.getOrganizationId() + "_"
						+ servicePricePlanDto.getServiceId() + "_" + min);
				usageChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
				usageChargeTemplate
						.setUnityFormatter(UsageChgTemplateEnum.INTEGER);
				usageChargeTemplate.setUnityDescription(servicePricePlanDto
						.getUsageUnit());
				usageChargeTemplate.setPriority(min);
				usageChargeTemplateService.create(em, usageChargeTemplate,
						currentUser, provider);

				ServiceUsageChargeTemplate serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
				serviceUsageChargeTemplate
						.setChargeTemplate(usageChargeTemplate);
				serviceUsageChargeTemplate.setCounterTemplate(counterTemplate);
				serviceUsageChargeTemplate
						.setServiceTemplate(serviceTemplate);
				serviceUsageChargeTemplateService.create(em,
						serviceUsageChargeTemplate, currentUser, provider);

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
				pricePlanMatrix.setCriteria1Value(servicePricePlanDto
						.getParam1());
				pricePlanMatrix.setCriteria2Value(servicePricePlanDto
						.getParam2());
				pricePlanMatrix.setCriteria3Value(servicePricePlanDto
						.getParam3());
				pricePlanMatrixService.create(em, pricePlanMatrix, currentUser,
						provider);
			}

			serviceTemplate.getRecurringCharges().add(
					recurringChargeTemplate);
			serviceTemplate.getSubscriptionCharges().add(
					subscriptionTemplate);
			serviceTemplate.getTerminationCharges().add(
					terminationTemplate);
			serviceTemplateService.update(em, serviceTemplate,
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

			throw new MeveoApiException(sb.toString());
		}
	}

	public void remove(Long providerId, Long userId, String serviceId,
			String organizationId) throws MeveoApiException {
		Provider provider = providerService.findById(providerId);
		User currentUser = userService.findById(userId);
	}

}
