/**
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;

@Stateless
@LocalBean
public class WalletOperationService extends BusinessService<WalletOperation> {
	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private ResourceBundle resourceMessages;

	@Inject
	private RatingService chargeApplicationRatingService;

	private DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private String str_tooPerceived = null;

	@PostConstruct
	private void init() {
		str_tooPerceived = resourceMessages.getString("str_tooPerceived");
	}
	
	public BigDecimal getRatedAmount(Provider provider,Seller seller, Customer customer,
	CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount,
	Date startDate, Date endDate, boolean amountWithTax){
		BigDecimal result=BigDecimal.ZERO;
		return result;
	}


	public void usageWalletOperation(Subscription subscription, Date usageDate,
			BigDecimal quantity, String param1, String param2, String param3) {

	}
	
	public WalletOperation rateOneShotApplication(Subscription subscription,
			OneShotChargeInstance chargeInstance, Integer quantity, Date applicationDate) throws BusinessException {
		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
		if (chargeTemplate == null) {
			throw new IncorrectChargeTemplateException(
					"chargeTemplate is null for chargeInstance id=" + chargeInstance.getId()
							+ ", code=" + chargeInstance.getCode());
		}
		InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException(
					"invoiceSubCategory is null for chargeTemplate code="
							+ chargeTemplate.getCode());
		}

		TradingCurrency currency = subscription.getUserAccount().getBillingAccount()
				.getCustomerAccount().getTradingCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ subscription.getUserAccount().getBillingAccount().getCustomerAccount()
							.getId());
		}
		// FIXME: put country in charge instance
		TradingCountry country = chargeInstance.getSubscription().getUserAccount()
				.getBillingAccount().getTradingCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
							+ invoiceSubCategory.getCode() + " and trading country="
							+ country.getCountryCode());
		}
		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException(
					"no tax exists for invoiceSubcategoryCountry id="
							+ invoiceSubcategoryCountry.getId());
		}
		WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(
				chargeTemplate.getCode(), subscription, chargeInstance,
				ApplicationTypeEnum.PUNCTUAL, applicationDate,
				chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(),
				quantity == null ? null : new BigDecimal(quantity), currency, countryId,
				tax.getPercent(), null, null, invoiceSubCategory, chargeInstance.getCriteria1(),
				chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), null, null, null);
		return chargeApplication;
	}

	public void oneShotWalletOperation(Subscription subscription,
			OneShotChargeInstance chargeInstance, Integer quantity, Date applicationDate,
			User creator) throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		if (applicationDate == null) {
			applicationDate = new Date();
		}

		log.debug("WalletOperationService.oneShotWalletOperation subscriptionCode={},quantity={},"
				+ "applicationDate={},chargeInstance.getId={}", subscription.getId(), quantity,
				applicationDate, chargeInstance.getId());
		WalletOperation chargeApplication = rateOneShotApplication(subscription, chargeInstance, quantity, applicationDate);
		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
		
		create(chargeApplication, creator, chargeTemplate.getProvider());
		OneShotChargeTemplate oneShotChargeTemplate = null;
		if (chargeTemplate instanceof OneShotChargeTemplate) {
			oneShotChargeTemplate = (OneShotChargeTemplate) chargeInstance.getChargeTemplate();

		} else {
			oneShotChargeTemplate = oneShotChargeTemplateService.findById(chargeTemplate.getId());
		}

		Boolean immediateInvoicing = oneShotChargeTemplate != null ? oneShotChargeTemplate
				.getImmediateInvoicing() : false;
		if (immediateInvoicing != null && immediateInvoicing) {
			BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();
			int delay = billingAccount.getBillingCycle().getInvoiceDateDelay();
			Date nextInvoiceDate = DateUtils.addDaysToDate(billingAccount.getNextInvoiceDate(),
					-delay);
			nextInvoiceDate = DateUtils.parseDateWithPattern(nextInvoiceDate, "dd/MM/yyyy");
			applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");
			if (applicationDate.after(nextInvoiceDate)) {
				billingAccount.setNextInvoiceDate(applicationDate);
				billingAccountService.update(billingAccount, creator);
			}
		}

	}

	public void recurringWalletOperation(Subscription subscription,
			RecurringChargeInstance chargeInstance, Integer quantity, Date applicationDate,
			User creator) throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug(
				"ChargeApplicationService.recurringChargeApplication subscriptionCode={},quantity={},"
						+ "applicationDate={},chargeInstance.getId={}", subscription.getId(),
				quantity, applicationDate, chargeInstance.getId());
		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
		if (chargeTemplate == null) {
			throw new IncorrectChargeTemplateException(
					"chargeTemplate is null for chargeInstance id=" + chargeInstance.getId()
							+ ", code=" + chargeInstance.getCode());
		}
		InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException(
					"invoiceSubCategory is null for chargeTemplate code="
							+ chargeTemplate.getCode());
		}

		TradingCurrency currency = subscription.getUserAccount().getBillingAccount()
				.getCustomerAccount().getTradingCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ subscription.getUserAccount().getBillingAccount().getCustomerAccount()
							.getId());
		}
		// FIXME: put country in charge instance
		TradingCountry country = chargeInstance.getSubscription().getUserAccount()
				.getBillingAccount().getTradingCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
							+ invoiceSubCategory.getCode() + " and trading country=" + countryId);
		}
		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException(
					"no tax exists for invoiceSubcategoryCountry id="
							+ invoiceSubcategoryCountry.getId());
		}

		WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(
				chargeTemplate.getCode(), subscription, chargeInstance,
				ApplicationTypeEnum.PUNCTUAL, applicationDate,
				chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(),
				quantity == null ? null : new BigDecimal(quantity), currency, countryId,
				tax.getPercent(), null, null, invoiceSubCategory, chargeInstance.getCriteria1(),
				chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), null, null, null);

		create(chargeApplication, creator, chargeTemplate.getProvider());
	}
	
	
   public Date getNextApplicationDate(RecurringChargeInstance chargeInstance){
		Date applicationDate = chargeInstance.getSubscriptionDate();
		applicationDate = DateUtils.parseDateWithPattern(chargeInstance.getSubscriptionDate(),
				"dd/MM/yyyy");
		chargeInstance.setChargeDate(applicationDate);
		RecurringChargeTemplate recurringChargeTemplate = chargeInstance
				.getRecurringChargeTemplate();
		Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
				applicationDate);
		nextapplicationDate = DateUtils.parseDateWithPattern(nextapplicationDate, "dd/MM/yyyy");
		return nextapplicationDate;
	}
	
   public Date getNextDurationDate(RecurringChargeInstance chargeInstance) throws BusinessException {
		RecurringChargeTemplate recurringChargeTemplate = chargeInstance
				.getRecurringChargeTemplate();
		if (recurringChargeTemplate.getCalendar() == null) {
			throw new IncorrectChargeTemplateException(
					"Recurring charge template has no calendar: code="
							+ recurringChargeTemplate.getCode());
		}

		ServiceTemplate serviceTemplate = chargeInstance.getServiceInstance().getServiceTemplate();
		Calendar durationTermCalendar = null;
		Date nextDurationDate = null;
		try {
			durationTermCalendar = serviceTemplate.getDurationTermCalendar();
			nextDurationDate = durationTermCalendar != null ? durationTermCalendar
					.nextCalendarDate(chargeInstance.getChargeDate()) : null;
			log.debug("nextDurationDate=" + nextDurationDate);
		} catch (Exception e) {
			log.info("Cannot find duration term calendar for serviceTemplate.id={}",
					serviceTemplate.getId());
		}
		return nextDurationDate;
   }
	
	public WalletOperation rateSubscription(RecurringChargeInstance chargeInstance,Date nextapplicationDate)
			throws BusinessException {
			WalletOperation result= null;
			Date applicationDate=chargeInstance.getChargeDate();

			RecurringChargeTemplate recurringChargeTemplate = chargeInstance
					.getRecurringChargeTemplate();

			Date previousapplicationDate = recurringChargeTemplate.getCalendar()
					.previousCalendarDate(applicationDate);
			previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate,
					"dd/MM/yyyy");
			log.debug(
					"chargeSubscription applicationDate={}, nextapplicationDate={},previousapplicationDate={}",
					applicationDate, nextapplicationDate, previousapplicationDate);

			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
					: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());
			if (Boolean.TRUE.equals(recurringChargeTemplate.getSubscriptionProrata())) {
				Date periodStart = applicationDate;
				double prorataRatio = 1.0;
				double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
				double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);
				if (part2 > 0) {
					prorataRatio = part1 / part2;
				} else {
					log.error(
							"Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}",
							nextapplicationDate, previousapplicationDate);
				}

				quantity = quantity.multiply(new BigDecimal(prorataRatio));
				log.debug("chargeSubscription part1={}, part2={}, prorataRation={} -> quantity={}",
						part1, part2, prorataRatio, quantity);
			}

			String param2 = " " + sdf.format(applicationDate) + " au "
					+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
			log.debug("param2={}", param2);

			InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
			if (invoiceSubCategory == null) {
				throw new IncorrectChargeTemplateException(
						"invoiceSubCategory is null for chargeTemplate code="
								+ recurringChargeTemplate.getCode());
			}

			TradingCurrency currency = chargeInstance.getSubscription().getUserAccount()
					.getBillingAccount().getCustomerAccount().getTradingCurrency();
			if (currency == null) {
				throw new IncorrectChargeTemplateException(
						"no currency exists for customerAccount id="
								+ chargeInstance.getSubscription().getUserAccount()
										.getBillingAccount().getCustomerAccount().getId());
			}

			// FIXME: put country in charge instance
			TradingCountry country = chargeInstance.getSubscription().getUserAccount()
					.getBillingAccount().getTradingCountry();
			if (country == null) {
				throw new IncorrectChargeTemplateException(
						"no country exists for billingAccount id="
								+ chargeInstance.getSubscription().getUserAccount()
										.getBillingAccount().getId());
			}
			Long countryId = country.getId();

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
					.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
			if (invoiceSubcategoryCountry == null) {
				throw new IncorrectChargeTemplateException(
						"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
								+ invoiceSubCategory.getCode() + " and trading country="
								+ countryId);
			}
			Tax tax = invoiceSubcategoryCountry.getTax();
			if (tax == null) {
				throw new IncorrectChargeTemplateException(
						"no tax exists for invoiceSubcategoryCountry id="
								+ invoiceSubcategoryCountry.getId());
			}

			if (!recurringChargeTemplate.getApplyInAdvance()) {
				applicationDate = nextapplicationDate;
			}
			result = chargeApplicationRatingService
					.rateChargeApplication(chargeInstance.getCode(), chargeInstance
							.getServiceInstance().getSubscription(), chargeInstance,
							ApplicationTypeEnum.PRORATA_SUBSCRIPTION, applicationDate,
							chargeInstance.getAmountWithoutTax(),
							chargeInstance.getAmountWithTax(), quantity, currency, countryId, tax
									.getPercent(), null, nextapplicationDate,
							recurringChargeTemplate.getInvoiceSubCategory(), chargeInstance
									.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance
									.getCriteria3(), applicationDate, DateUtils.addDaysToDate(
									nextapplicationDate, -1), null);
			return result;
	}
	

	public void chargeSubscription(RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug(
				"ChargeApplicationService.chargeSubscription subscriptionCode={},chargeCode={},quantity={},"
						+ "applicationDate={},chargeInstance.getId={}",
				new Object[] { chargeInstance.getServiceInstance().getSubscription().getCode(),
						chargeInstance.getCode(),
						chargeInstance.getServiceInstance().getQuantity(),
						chargeInstance.getSubscriptionDate(), chargeInstance.getId() });
		RecurringChargeTemplate recurringChargeTemplate = chargeInstance
				.getRecurringChargeTemplate();
		Date nextapplicationDate= getNextApplicationDate(chargeInstance);
		Date nextDurationDate= getNextDurationDate(chargeInstance);
		
		if (recurringChargeTemplate.getApplyInAdvance()) {
		    WalletOperation chargeApplication=rateSubscription(chargeInstance,nextapplicationDate);
			create(chargeApplication, creator, chargeInstance.getProvider());

			chargeInstance.setNextChargeDate(nextapplicationDate);

			// If there is a durationTermCalendar then we apply all
			// necessary
			// missing periods
			if (nextDurationDate != null
					&& nextDurationDate.getTime() > nextapplicationDate.getTime()) {
				applyReccuringCharge(chargeInstance, false, recurringChargeTemplate, creator);
			}

		} else {

			if (nextDurationDate != null
					&& nextDurationDate.getTime() > nextapplicationDate.getTime()) {
				chargeInstance.setNextChargeDate(nextDurationDate);
			} else {
				chargeInstance.setNextChargeDate(nextapplicationDate);
			}
		}

	}

	public void applyReimbursment(RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {
		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug("applyReimbursment subscriptionCode={},chargeCode={},quantity={},"
				+ "applicationDate={},chargeInstance.getId={},NextChargeDate={}", chargeInstance
				.getServiceInstance().getSubscription().getCode(), chargeInstance.getCode(),
				chargeInstance.getServiceInstance().getQuantity(),
				chargeInstance.getSubscriptionDate(), chargeInstance.getId(),
				chargeInstance.getNextChargeDate());

		Date applicationDate = chargeInstance.getTerminationDate();
		applicationDate = DateUtils.addDaysToDate(applicationDate, 1);
		applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");

		BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
				: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());

		Date nextapplicationDate = null;

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance
				.getRecurringChargeTemplate();
		if (recurringChargeTemplate.getCalendar() == null) {
			throw new IncorrectChargeTemplateException(
					"Recurring charge template has no calendar: code="
							+ recurringChargeTemplate.getCode());
		}

		nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
				applicationDate);
		nextapplicationDate = DateUtils.parseDateWithPattern(nextapplicationDate, "dd/MM/yyyy");
		Date previousapplicationDate = recurringChargeTemplate.getCalendar().previousCalendarDate(
				applicationDate);
		previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate,
				"dd/MM/yyyy");
		log.debug("applicationDate={}, nextapplicationDate={},previousapplicationDate={}",
				applicationDate, nextapplicationDate, previousapplicationDate);

		Date periodStart = applicationDate;
		if (recurringChargeTemplate.getTerminationProrata()) {

			double prorataRatio = 1.0;
			double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
			double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);

			if (part2 > 0) {
				prorataRatio = (-1) * part1 / part2;
			} else {
				log.error(
						"Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}",
						nextapplicationDate, previousapplicationDate);
			}

			String param2 = " " + str_tooPerceived + " " + sdf.format(periodStart) + " / "
					+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

			quantity = quantity.multiply(new BigDecimal(prorataRatio));
			log.debug("part1={}, part2={}, prorataRatio={}, param2={} -> quantity={}", part1,
					part2, prorataRatio, param2, quantity);

			InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
			if (invoiceSubCategory == null) {
				throw new IncorrectChargeTemplateException(
						"invoiceSubCategory is null for chargeTemplate code="
								+ recurringChargeTemplate.getCode());
			}

			// FIXME: put currency in charge instance
			TradingCurrency currency = chargeInstance.getSubscription().getUserAccount()
					.getBillingAccount().getCustomerAccount().getTradingCurrency();
			if (currency == null) {
				throw new IncorrectChargeTemplateException(
						"no currency exists for customerAccount id="
								+ chargeInstance.getSubscription().getUserAccount()
										.getBillingAccount().getCustomerAccount().getId());
			}

			// FIXME: put country in charge instance
			TradingCountry country = chargeInstance.getSubscription().getUserAccount()
					.getBillingAccount().getTradingCountry();
			if (country == null) {
				throw new IncorrectChargeTemplateException(
						"no country exists for billingAccount id="
								+ chargeInstance.getSubscription().getUserAccount()
										.getBillingAccount().getId());
			}
			Long countryId = country.getId();

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
					.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
			if (invoiceSubcategoryCountry == null) {
				throw new IncorrectChargeTemplateException(
						"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
								+ invoiceSubCategory.getCode() + " and trading country="
								+ countryId);
			}
			Tax tax = invoiceSubcategoryCountry.getTax();
			if (tax == null) {
				throw new IncorrectChargeTemplateException(
						"no tax exists for invoiceSubcategoryCountry id="
								+ invoiceSubcategoryCountry.getId());
			}

			WalletOperation chargeApplication = chargeApplicationRatingService
					.rateChargeApplication(chargeInstance.getCode(), chargeInstance
							.getServiceInstance().getSubscription(), chargeInstance,
							ApplicationTypeEnum.PRORATA_TERMINATION, applicationDate,
							chargeInstance.getAmountWithoutTax(),
							chargeInstance.getAmountWithTax(), quantity, currency, countryId, tax
									.getPercent(), null, nextapplicationDate, invoiceSubCategory,
							chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
							chargeInstance.getCriteria3(), periodStart, DateUtils.addDaysToDate(
									nextapplicationDate, -1),
							ChargeApplicationModeEnum.REIMBURSMENT);
			create(chargeApplication, creator, chargeInstance.getProvider());

		}

		if (recurringChargeTemplate.getApplyInAdvance()) {
			Date nextChargeDate = chargeInstance.getNextChargeDate();
			log.debug(
					"reimbursment-applyInAdvance applicationDate={}, nextapplicationDate={},nextChargeDate={}",
					applicationDate, nextapplicationDate, nextChargeDate);
			if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
				applyReccuringCharge(chargeInstance, true, recurringChargeTemplate, creator);
			}
		} else {
			Date nextChargeDate = chargeInstance.getChargeDate();
			log.debug(
					"reimbursment-applyInAdvance applicationDate={}, nextapplicationDate={},nextChargeDate={}",
					applicationDate, nextapplicationDate, nextChargeDate);
			if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
				applyNotAppliedinAdvanceReccuringCharge(chargeInstance, true,
						recurringChargeTemplate, creator);
			}
		}
	}

	public void applyReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
			RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {

		// we apply the charge at its nextChargeDate

		Date applicationDate = chargeInstance.getNextChargeDate();

		if (reimbursement) {
			applicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
					chargeInstance.getTerminationDate());
		}

		if (applicationDate == null) {
			throw new IncorrectChargeInstanceException("nextChargeDate is null.");
		}

		// first we get the serviceInstance and check if there is an associated
		// Calendar
		ServiceTemplate serviceTemplate = chargeInstance.getServiceInstance().getServiceTemplate();
		Calendar durationTermCalendar = null;
		Date nextDurationDate = null;
		try {
			durationTermCalendar = serviceTemplate.getDurationTermCalendar();
			nextDurationDate = reimbursement ? chargeInstance.getNextChargeDate()
					: durationTermCalendar.nextCalendarDate(applicationDate);
			log.debug("reimbursement={},nextDurationDate={},applicationDate={}", reimbursement,
					nextDurationDate, applicationDate);
		} catch (Exception e) {
			log.error("Cannot find duration term calendar for serviceTemplate.id={}",
					serviceTemplate.getId());
		}
		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException(
					"invoiceSubCategory is null for chargeTemplate code="
							+ recurringChargeTemplate.getCode());
		}

		// FIXME: put currency in charge instance
		TradingCurrency currency = chargeInstance.getSubscription().getUserAccount()
				.getBillingAccount().getCustomerAccount().getTradingCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount()
							.getCustomerAccount().getId());
		}

		// FIXME: put country in charge instance
		TradingCountry country = chargeInstance.getSubscription().getUserAccount()
				.getBillingAccount().getTradingCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
							+ invoiceSubCategory.getCode() + " and trading country=" + countryId);
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException(
					"no tax exists for invoiceSubcategoryCountry id="
							+ invoiceSubcategoryCountry.getId());
		}

		while (applicationDate.getTime() < nextDurationDate.getTime()) {
			Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
					applicationDate);
			log.debug(
					"next step for {}, applicationDate={}, nextApplicationDate={},nextApplicationDate={}",
					chargeInstance.getId(), applicationDate, nextapplicationDate, nextDurationDate);

			String param2 = (reimbursement ? str_tooPerceived + " " : " ")
					+ sdf.format(applicationDate) + (reimbursement ? " / " : " au ")
					+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
					: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());
			if (reimbursement) {
				quantity = quantity.negate();
			}
			log.debug("applyReccuringCharge : nextapplicationDate={}, param2={} -> quantity={}",
					nextapplicationDate, param2, quantity);

			WalletOperation chargeApplication = chargeApplicationRatingService
					.rateChargeApplication(chargeInstance.getCode(), chargeInstance
							.getServiceInstance().getSubscription(), chargeInstance,
							reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION
									: ApplicationTypeEnum.RECURRENT, applicationDate,
							chargeInstance.getAmountWithoutTax(),
							chargeInstance.getAmountWithTax(), quantity, currency, countryId, tax
									.getPercent(), null, nextapplicationDate, invoiceSubCategory,
							chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
							chargeInstance.getCriteria3(), applicationDate, DateUtils
									.addDaysToDate(nextapplicationDate, -1),
							reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT
									: ChargeApplicationModeEnum.SUBSCRIPTION);
			chargeApplication.setSubscriptionDate(chargeInstance.getServiceInstance()
					.getSubscriptionDate());

			create(chargeApplication, creator, chargeInstance.getProvider());
			chargeInstance.setChargeDate(applicationDate);
			applicationDate = nextapplicationDate;
		}
		chargeInstance.setNextChargeDate(nextDurationDate);
	}

	public void applyNotAppliedinAdvanceReccuringCharge(RecurringChargeInstance chargeInstance,
			boolean reimbursement, RecurringChargeTemplate recurringChargeTemplate, User creator)
			throws BusinessException {

		Date applicationDate = chargeInstance.getChargeDate();

		if (reimbursement) {
			applicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
					chargeInstance.getTerminationDate());
		}

		if (applicationDate == null) {
			throw new IncorrectChargeInstanceException("ChargeDate is null.");
		}

		// first we get the serviceInstance and check if there is an associated
		// Calendar
		ServiceTemplate serviceTemplate = chargeInstance.getServiceInstance().getServiceTemplate();
		Calendar durationTermCalendar = null;
		Date nextChargeDate = reimbursement ? chargeInstance.getChargeDate() : chargeInstance
				.getNextChargeDate();
		durationTermCalendar = serviceTemplate.getDurationTermCalendar();
		
		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException(
					"invoiceSubCategory is null for chargeTemplate code="
							+ recurringChargeTemplate.getCode());
		}

		// FIXME: put currency in charge instance
		TradingCurrency currency = chargeInstance.getSubscription().getUserAccount()
				.getBillingAccount().getCustomerAccount().getTradingCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount()
							.getCustomerAccount().getId());
		}

		// FIXME: put country in charge instance
		TradingCountry country = chargeInstance.getSubscription().getUserAccount()
				.getBillingAccount().getTradingCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
							+ invoiceSubCategory.getCode() + " and trading country=" + countryId);
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException(
					"tax is null for invoiceSubCategoryCountry id="
							+ invoiceSubcategoryCountry.getId());
		}

		while (applicationDate.getTime() < nextChargeDate.getTime()) {
			Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
					applicationDate);
			log.debug(
					"applyNotAppliedinAdvanceReccuringCharge next step for {}, applicationDate={}, nextApplicationDate={},nextApplicationDate={}",
					chargeInstance.getId(), applicationDate, nextapplicationDate, nextChargeDate);

			Date previousapplicationDate = recurringChargeTemplate.getCalendar()
					.previousCalendarDate(applicationDate);
			previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate,
					"dd/MM/yyyy");
			log.debug(
					" applyNotAppliedinAdvanceReccuringCharge applicationDate={}, nextapplicationDate={},previousapplicationDate={}",
					applicationDate, nextapplicationDate, previousapplicationDate);

			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
					: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());
			ApplicationTypeEnum applicationTypeEnum = ApplicationTypeEnum.RECURRENT;
			Date periodStart = applicationDate;
			// n'appliquer le prorata que dans le cas de la 1ere application de
			// charges �chues
			log.debug(
					" applyNotAppliedinAdvanceReccuringCharge chargeInstance.getWalletOperations().size()={}",
					chargeInstance.getWalletOperations().size());
			if (chargeInstance.getWalletOperations().size() == 0
					&& recurringChargeTemplate.getSubscriptionProrata()) {
				applicationTypeEnum = ApplicationTypeEnum.PRORATA_SUBSCRIPTION;
				double prorataRatio = 1.0;
				double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
				double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);

				if (part2 > 0) {
					prorataRatio = part1 / part2;
				} else {
					log.error(
							"applyNotAppliedinAdvanceReccuringCharge Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}",
							nextapplicationDate, previousapplicationDate);
				}
				quantity = quantity.multiply(new BigDecimal(prorataRatio));
				log.debug("part1={}, part2={}, prorataRatio={} -> quantity", part1, part2,
						prorataRatio, quantity);
			}

			String param2 = (reimbursement ? str_tooPerceived + " " : " ")
					+ sdf.format(applicationDate) + (reimbursement ? " / " : " au ")
					+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

			log.debug("param2={}", param2);

			log.debug(
					"applyNotAppliedinAdvanceReccuringCharge : nextapplicationDate={}, param2={}",
					nextapplicationDate, param2);

			if (reimbursement) {
				quantity = quantity.negate();
			}

			WalletOperation chargeApplication = chargeApplicationRatingService
					.rateChargeApplication(chargeInstance.getCode(), chargeInstance
							.getServiceInstance().getSubscription(), chargeInstance,
							reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION
									: applicationTypeEnum, applicationDate, chargeInstance
									.getAmountWithoutTax(), chargeInstance.getAmountWithTax(),
							quantity, currency, countryId, tax.getPercent(), null,
							nextapplicationDate, invoiceSubCategory, chargeInstance.getCriteria1(),
							chargeInstance.getCriteria2(), chargeInstance.getCriteria3(),
							applicationDate, DateUtils.addDaysToDate(nextapplicationDate, -1),
							reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT
									: ChargeApplicationModeEnum.SUBSCRIPTION);
			chargeApplication.setSubscriptionDate(chargeInstance.getServiceInstance()
					.getSubscriptionDate());

			create(chargeApplication, creator, chargeInstance.getProvider());
			getEntityManager().flush();
			getEntityManager().refresh(chargeInstance);
			chargeInstance.setChargeDate(applicationDate);
			applicationDate = nextapplicationDate;
		}

		if (durationTermCalendar != null) {
			Date nextNextDurationDate = durationTermCalendar.nextCalendarDate(applicationDate);
			chargeInstance.setNextChargeDate(durationTermCalendar != null ? nextNextDurationDate
					: applicationDate);
			chargeInstance.setChargeDate(nextChargeDate);
		} else {
			Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
					applicationDate);
			chargeInstance.setNextChargeDate(nextapplicationDate);
			chargeInstance.setChargeDate(applicationDate);
		}

	}

	public void applyChargeAgreement(RecurringChargeInstance chargeInstance,
			RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {

		// we apply the charge at its nextChargeDate
		Date applicationDate = chargeInstance.getNextChargeDate();
		if (applicationDate == null) {
			throw new IncorrectChargeInstanceException("nextChargeDate is null.");
		}

		Date endAgreementDate = chargeInstance.getServiceInstance().getEndAgrementDate();

		if (endAgreementDate == null) {
			return;
		}

		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException(
					"invoiceSubCategory is null for chargeTemplate code="
							+ recurringChargeTemplate.getCode());
		}

		// FIXME: put currency in charge instance
		TradingCurrency currency = chargeInstance.getSubscription().getUserAccount()
				.getBillingAccount().getCustomerAccount().getTradingCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount()
							.getCustomerAccount().getId());
		}

		// FIXME: put country in charge instance
		TradingCountry country = chargeInstance.getSubscription().getUserAccount()
				.getBillingAccount().getTradingCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
							+ invoiceSubCategory.getCode() + " and trading country=" + countryId);
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException(
					"tax is null for invoiceSubcategoryCountry id="
							+ invoiceSubcategoryCountry.getId());
		}
		while (applicationDate.getTime() < endAgreementDate.getTime()) {
			Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
					applicationDate);
			log.debug("agreement next step for {}, applicationDate={}, nextApplicationDate={}",
					recurringChargeTemplate.getCode(), applicationDate, nextapplicationDate);
			Double prorataRatio = null;
			ApplicationTypeEnum type = ApplicationTypeEnum.RECURRENT;
			Date endDate = DateUtils.addDaysToDate(nextapplicationDate, -1);
			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
					: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());
			if (nextapplicationDate.getTime() > endAgreementDate.getTime()
					&& applicationDate.getTime() < endAgreementDate.getTime()) {
				Date endAgreementDateModified = DateUtils.addDaysToDate(endAgreementDate, 1);

				double part1 = endAgreementDateModified.getTime() - applicationDate.getTime();
				double part2 = nextapplicationDate.getTime() - applicationDate.getTime();
				if (part2 > 0) {
					prorataRatio = part1 / part2;
				}

				nextapplicationDate = endAgreementDate;
				endDate = nextapplicationDate;
				if (recurringChargeTemplate.getTerminationProrata()) {
					type = ApplicationTypeEnum.PRORATA_TERMINATION;
					quantity = quantity.multiply(new BigDecimal(prorataRatio));
				}
			}
			String param2 = sdf.format(applicationDate) + " au " + sdf.format(endDate);
			log.debug("applyReccuringCharge : nextapplicationDate={}, param2={}",
					nextapplicationDate, param2);

			WalletOperation chargeApplication = chargeApplicationRatingService
					.rateChargeApplication(chargeInstance.getCode(), chargeInstance
							.getServiceInstance().getSubscription(), chargeInstance, type,
							applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance
									.getAmountWithTax(), quantity, currency, countryId, tax
									.getPercent(), null, nextapplicationDate, invoiceSubCategory,
							chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
							chargeInstance.getCriteria3(), applicationDate, endDate,
							ChargeApplicationModeEnum.AGREEMENT);
			create(chargeApplication, creator, chargeInstance.getProvider());
			chargeInstance.setChargeDate(applicationDate);
			applicationDate = nextapplicationDate;
		}
	}

	// FIXME: is it deprecated or not ???
	@Deprecated
	public void chargeTermination(RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {
		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}
		/*
		 * log.debug(
		 * "ChargeApplicationService.chargeTermination subscriptionCode={},chargeCode={},quantity={},"
		 * + "applicationDate={},chargeInstance.getId={}", chargeInstance
		 * .getServiceInstance().getSubscription().getCode(),
		 * chargeInstance.getCode(),
		 * chargeInstance.getServiceInstance().getQuantity(),
		 * chargeInstance.getSubscriptionDate(), chargeInstance.getId());
		 * 
		 * Date applicationDate = chargeInstance.getTerminationDate();
		 * applicationDate = DateUtils.parseDateWithPattern(applicationDate,
		 * "dd/MM/yyyy");
		 * 
		 * String param1 = "1";// for prorata String param2 = null;// used in
		 * invoice description String param3 = "0";
		 * 
		 * Date nextapplicationDate = null;
		 * 
		 * RecurringChargeTemplate recurringChargeTemplate = chargeInstance
		 * .getRecurringChargeTemplate(); if
		 * (recurringChargeTemplate.getCalendar() == null) { throw new
		 * IncorrectChargeTemplateException(
		 * "Recurring charge template has no calendar: code=" +
		 * recurringChargeTemplate.getCode()); } Date endAgrementDate =
		 * chargeInstance.getServiceInstance().getEndAgrementDate(); if
		 * (endAgrementDate != null &&
		 * chargeInstance.getTerminationDate().before(endAgrementDate)) {
		 * applyChargeAgreement(chargeInstance, recurringChargeTemplate,
		 * creator); return; }
		 * 
		 * if
		 * (Boolean.TRUE.equals(recurringChargeTemplate.getTerminationProrata(
		 * ))) { param3 = "1";// for prorata } nextapplicationDate =
		 * recurringChargeTemplate.getCalendar().nextCalendarDate(
		 * applicationDate); nextapplicationDate =
		 * DateUtils.parseDateWithPattern(nextapplicationDate, "dd/MM/yyyy");
		 * Date previousapplicationDate =
		 * recurringChargeTemplate.getCalendar().previousCalendarDate(
		 * applicationDate); previousapplicationDate =
		 * DateUtils.parseDateWithPattern(previousapplicationDate,
		 * "dd/MM/yyyy"); log.debug(
		 * "applicationDate={}, nextapplicationDate={},previousapplicationDate={}"
		 * , applicationDate, nextapplicationDate, previousapplicationDate);
		 * 
		 * Date periodStart = applicationDate; if
		 * (recurringChargeTemplate.getTerminationProrata()) { double part1 =
		 * DateUtils.daysBetween(periodStart, nextapplicationDate); double part2
		 * = DateUtils.daysBetween(previousapplicationDate,
		 * nextapplicationDate); if (part2 > 0) { param1 = Double.toString((-1)
		 * * part1 / part2); } else { log.error(
		 * "Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}"
		 * , nextapplicationDate, previousapplicationDate); } param2 = " " +
		 * str_tooPerceived + " " + sdf.format(periodStart) + " / " +
		 * sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
		 * log.debug("part1={}, part2={}, param1={}, param2={}", part1, part2,
		 * param1, param2);
		 * 
		 * InvoiceSubCategory invoiceSubCategory =
		 * recurringChargeTemplate.getInvoiceSubCategory(); if
		 * (invoiceSubCategory == null) { throw new
		 * IncorrectChargeTemplateException(
		 * "invoiceSubCategory is null for chargeTemplate code=" +
		 * recurringChargeTemplate.getCode()); }
		 * 
		 * InvoiceSubcategoryCountry invoiceSubcategoryCountry =
		 * invoiceSubCategoryCountryService
		 * .findInvoiceSubCategoryCountry(invoiceSubCategory.getId(),
		 * chargeInstance
		 * .getSubscription().getUserAccount().getBillingAccount()
		 * .getTradingCountry().getId()); if (invoiceSubcategoryCountry == null)
		 * { throw new IncorrectChargeTemplateException(
		 * "no tax exists for invoiceSubCategory code=" +
		 * invoiceSubCategory.getCode()); } Tax tax =
		 * invoiceSubcategoryCountry.getTax(); if (tax == null) { throw new
		 * IncorrectChargeTemplateException(
		 * "tax is null for invoiceSubCategoryCountry id=" +
		 * invoiceSubcategoryCountry.getId()); }
		 * 
		 * ChargeApplication chargeApplication = new
		 * ChargeApplication(chargeInstance.getCode(),
		 * chargeInstance.getDescription(), chargeInstance.getServiceInstance()
		 * .getSubscription(), chargeInstance, chargeInstance.getCode(),
		 * ApplicationChgStatusEnum.WAITING,
		 * ApplicationTypeEnum.PRORATA_TERMINATION, applicationDate,
		 * chargeInstance.getAmountWithoutTax(), chargeInstance.getAmount2(),
		 * chargeInstance.getServiceInstance().getQuantity(), tax.getCode(),
		 * tax.getPercent(), null, nextapplicationDate, invoiceSubCategory,
		 * param1, param2, param3, null, chargeInstance.getCriteria1(),
		 * chargeInstance.getCriteria2(), chargeInstance.getCriteria3(),
		 * periodStart, DateUtils.addDaysToDate(nextapplicationDate, -1));
		 * create(chargeApplication, creator, chargeInstance.getProvider());
		 * 
		 * }
		 * 
		 * chargeInstance.setChargeDate(applicationDate);
		 * chargeInstance.setNextChargeDate(nextapplicationDate);
		 * 
		 * if (recurringChargeTemplate.getApplyInAdvance()) { // If there is a
		 * durationTermCalendar then we reimburse all // necessary // missing
		 * periods ServiceTemplate serviceTemplate =
		 * chargeInstance.getServiceInstance() .getServiceTemplate(); Calendar
		 * durationTermCalendar = null; Date nextDurationDate = null; try {
		 * durationTermCalendar = serviceTemplate.getDurationTermCalendar();
		 * nextDurationDate =
		 * durationTermCalendar.nextCalendarDate(applicationDate);
		 * log.debug("nextDurationDate=" + nextDurationDate); } catch (Exception
		 * e) {
		 * log.error("Cannot find duration term calendar for serviceTemplate.id={}"
		 * , serviceTemplate.getId()); }
		 * 
		 * if (nextDurationDate != null && nextDurationDate.getTime() >
		 * nextapplicationDate.getTime()) { applyReccuringCharge(chargeInstance,
		 * true, recurringChargeTemplate, creator); } }
		 */
	}
	
	
	@SuppressWarnings("unchecked")
	public List<WalletOperation> findByStatus(WalletOperationStatusEnum status) {
		List<WalletOperation> walletOperations = null;
		try {
			log.debug("start of find {} by status (status={})) ..", "WalletOperation",
					status);
			QueryBuilder qb = new QueryBuilder(WalletOperation.class, "c");
			qb.addCriterion("c.status", "=", status, true);
			walletOperations = qb.getQuery(getEntityManager()).getResultList();
			log.debug("end of find {} by status (status={}). Result size found={}.", new Object[] {
					"WalletOperation", status,
					walletOperations != null ? walletOperations.size() : 0 });

		} catch (Exception e) {
			log.error("findByStatus error={} ", e.getMessage());
		}
		return walletOperations;
	}

}
