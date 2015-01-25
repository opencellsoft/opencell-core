package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.slf4j.Logger;

@Stateless
public class RealtimeChargingService {

	@Inject
	protected Logger log;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@Inject
	private RatingService chargeApplicationRatingService;

	@Inject
	private WalletOperationService walletOperationService;


	public BigDecimal getApplicationPrice(BillingAccount ba,
			OneShotChargeTemplate chargeTemplate, Date subscriptionDate,
			String offerCode, BigDecimal quantity, String param1,
			String param2, String param3, boolean priceWithoutTax)
			throws BusinessException {

		TradingCurrency currency = ba.getCustomerAccount().getTradingCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException(
					"no currency exists for customerAccount id="
							+ ba.getCustomerAccount().getId());
		}

		TradingCountry tradingCountry = ba.getTradingCountry();
		if (tradingCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no country exists for billingAccount id=" + ba.getId());
		}

		Provider provider = ba.getProvider();

		Seller seller = ba.getCustomerAccount().getCustomer().getSeller();

		return getApplicationPrice(provider, seller, currency,
				tradingCountry, chargeTemplate, subscriptionDate, offerCode,
				quantity, param1, param2, param3, priceWithoutTax);
	}

	public BigDecimal getApplicationPrice(Provider provider,
			Seller seller, TradingCurrency currency,
			TradingCountry tradingCountry,
			OneShotChargeTemplate chargeTemplate, Date subscriptionDate,
			String offerCode, BigDecimal quantity, String param1,
			String param2, String param3, boolean priceWithoutTax)
			throws BusinessException {

		InvoiceSubCategory invoiceSubCategory = chargeTemplate
				.getInvoiceSubCategory();

		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException(
					"invoiceSubCategory is null for chargeTemplate code="
							+ chargeTemplate.getCode());
		}

		Long tradingCountryId = tradingCountry.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(),
						tradingCountryId, provider);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
							+ invoiceSubCategory.getCode()
							+ " and trading country="
							+ tradingCountry.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException(
					"no tax exists for invoiceSubcategoryCountry id="
							+ invoiceSubcategoryCountry.getId());
		}

		WalletOperation op = new WalletOperation();

		op.setOperationDate(subscriptionDate);
		op.setParameter1(param1);
		op.setParameter2(param2);
		op.setParameter3(param3);

		op.setProvider(provider);
		OneShotChargeInstance ci = new OneShotChargeInstance();
		ci.setCountry(tradingCountry);
		ci.setCurrency(currency);
		op.setChargeInstance(ci);

		op.setWallet(null);
		op.setCode(chargeTemplate.getCode());

		op.setDescription("");
		op.setQuantity(quantity);
		op.setTaxPercent(tax.getPercent());
		op.setCurrency(currency.getCurrency());
		op.setStartDate(null);
		op.setEndDate(null);
		op.setOfferCode(offerCode);
		op.setStatus(WalletOperationStatusEnum.OPEN);
		op.setSeller(seller);

		chargeApplicationRatingService.rateBareWalletOperation(op, null,
				null, tradingCountryId, currency, provider);

		return priceWithoutTax ? op.getAmountWithoutTax() : op
				.getAmountWithTax();
	}

	public BigDecimal getFirstRecurringPrice(BillingAccount ba,
			RecurringChargeTemplate chargeTemplate, Date subscriptionDate,
			BigDecimal quantity, String param1, String param2, String param3,
			boolean priceWithoutTax) throws BusinessException {
		RecurringChargeInstance chargeInstance = new RecurringChargeInstance();
		chargeInstance.setRecurringChargeTemplate(chargeTemplate);
		chargeInstance.setSubscriptionDate(subscriptionDate);
		chargeInstance.setChargeDate(subscriptionDate);
		chargeInstance.setSeller(ba.getCustomerAccount().getCustomer()
				.getSeller());
		chargeInstance.setProvider(ba.getProvider());
		chargeInstance.setCountry(ba.getTradingCountry());
		chargeInstance
				.setCurrency(ba.getCustomerAccount().getTradingCurrency());
		Date nextApplicationDate = walletOperationService
				.getNextApplicationDate(chargeInstance);
		WalletOperation op = walletOperationService.prerateSubscription(
				subscriptionDate, chargeInstance, nextApplicationDate);
		return priceWithoutTax ? op.getAmountWithoutTax() : op
				.getAmountWithTax();
	}

	public BigDecimal getActivationServicePrice(BillingAccount ba,
			ServiceTemplate serviceTemplate, Date subscriptionDate,
			String offerCode, BigDecimal quantity, String param1,
			String param2, String param3, boolean priceWithoutTax)
			throws BusinessException {
		return getActivationServicePrice(null, ba, serviceTemplate,
				subscriptionDate, offerCode, quantity, param1, param2, param3,
				priceWithoutTax);
	}

	/*
	 * Warning : this method does not handle calendars at service level
	 */
	public BigDecimal getActivationServicePrice(EntityManager em,
			BillingAccount ba, ServiceTemplate serviceTemplate,
			Date subscriptionDate, String offerCode, BigDecimal quantity,
			String param1, String param2, String param3, boolean priceWithoutTax)
			throws BusinessException {

		BigDecimal result = BigDecimal.ZERO;

		if (serviceTemplate.getServiceSubscriptionCharges() != null) {
			for (ServiceChargeTemplate<OneShotChargeTemplate> charge : serviceTemplate
					.getServiceSubscriptionCharges()) {
				result = result.add(getApplicationPrice(ba, charge.getChargeTemplate(),
						subscriptionDate,offerCode, quantity, param1, param2, param3,
						priceWithoutTax));
			}
		}

		if (serviceTemplate.getServiceRecurringCharges() != null) {
			for (ServiceChargeTemplate<RecurringChargeTemplate> charge : serviceTemplate
					.getServiceRecurringCharges()) {
				if (charge.getChargeTemplate().getApplyInAdvance()) {
					result = result.add(getFirstRecurringPrice(ba, charge.getChargeTemplate(),
							subscriptionDate, quantity, param1, param2, param3,
							priceWithoutTax));
				}
			}
		}

		return result;
	}

}
