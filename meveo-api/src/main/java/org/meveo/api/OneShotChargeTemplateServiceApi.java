package org.meveo.api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.OneShotChargeTemplateDto;
import org.meveo.api.dto.OneShotChargeTemplateListDto;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.billing.impl.RealtimeChargingService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OneShotChargeTemplateServiceApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@Inject
	private RealtimeChargingService realtimeChargingService;

	@Inject
	private SellerService sellerService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private TradingCountryService tradingCountryService;

	public OneShotChargeTemplateListDto getOneShotChargeTemplates(
			String languageCode, String countryCode, String currencyCode,
			String providerCode, String sellerCode, Date date) {
		Provider provider = providerService.findByCode(em, providerCode);
		Seller seller = sellerService.findByCode(em, sellerCode, provider);
		TradingCurrency currency = tradingCurrencyService
				.findByTradingCurrencyCode(em, currencyCode, provider);
		TradingCountry country = tradingCountryService
				.findByTradingCountryCode(em, countryCode, provider);

		List<OneShotChargeTemplate> oneShotChargeTemplates = oneShotChargeTemplateService
				.getSubscriptionChargeTemplates(em, provider);
		OneShotChargeTemplateListDto oneShotChargeTemplateListDto = new OneShotChargeTemplateListDto();
		for (OneShotChargeTemplate oneShotChargeTemplate : oneShotChargeTemplates) {
			OneShotChargeTemplateDto oneShotChargeDto = new OneShotChargeTemplateDto();
			oneShotChargeDto.setChargeCode(oneShotChargeTemplate.getCode());
			oneShotChargeDto.setDescription(oneShotChargeTemplate
					.getDescription());
			InvoiceSubCategory invoiceSubCategory = oneShotChargeTemplate
					.getInvoiceSubCategory();

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
					.findInvoiceSubCategoryCountry(em,
							invoiceSubCategory.getId(), country.getId());
			if (invoiceSubcategoryCountry != null
					&& invoiceSubcategoryCountry.getTax() != null) {
				Tax tax = invoiceSubcategoryCountry.getTax();
				oneShotChargeDto.setTaxCode(tax.getCode());
				oneShotChargeDto.setTaxDescription(tax.getDescription());
				oneShotChargeDto.setTaxPercent(tax.getPercent() == null ? 0.0
						: tax.getPercent().doubleValue());
			}
			try {
				BigDecimal unitPrice = realtimeChargingService
						.getApplicationPrice(em, provider, seller, currency,
								country, oneShotChargeTemplate, date,
								BigDecimal.ONE, null, null, null, true);
				if (unitPrice != null) {
					oneShotChargeDto.setUnitPriceWithoutTax(unitPrice
							.doubleValue());
				}
			} catch (BusinessException e) {
				log.warn(e.getMessage());
			}

			oneShotChargeTemplateListDto.getOneShotChargeTemplateDtos().add(
					oneShotChargeDto);
		}

		return oneShotChargeTemplateListDto;
	}

}
