package org.meveo.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CurrencyApi extends BaseApi {

	@Inject
	private CurrencyService currencyService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	public void create(CurrencyDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			Provider provider = currentUser.getProvider();

			if (tradingCurrencyService.findByTradingCurrencyCode(
					postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(TradingCurrency.class,
						postData.getCode());
			}

			Currency currency = currencyService.findByCode(postData.getCode());

			Auditable auditable = new Auditable();
			auditable.setCreated(new Date());
			auditable.setCreator(currentUser);

			if (currency == null) {
				// create
				currency = new Currency();
				currency.setCurrencyCode(postData.getCode());
				currency.setDescriptionEn(postData.getDescription());
				currency.setAuditable(auditable);
				currencyService.create(currency, currentUser);
			}

			TradingCurrency tradingCurrency = new TradingCurrency();
			tradingCurrency.setAuditable(auditable);
			tradingCurrency.setCurrency(currency);
			tradingCurrency.setCurrencyCode(postData.getCode());
			tradingCurrency.setPrDescription(postData.getDescription());
			tradingCurrency.setProvider(provider);
			tradingCurrency.setActive(true);
			tradingCurrencyService.create(tradingCurrency, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

	}

	public CurrencyDto find(String code, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(code, provider);

			if (tradingCurrency != null) {
				return new CurrencyDto(tradingCurrency);
			}

			throw new EntityDoesNotExistsException(TradingLanguage.class, code);
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(code, provider);
			if (tradingCurrency == null) {
				throw new EntityDoesNotExistsException(TradingCurrency.class,
						code);
			} else {
				tradingCurrencyService.remove(tradingCurrency);
			}
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(CurrencyDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(postData.getCode(),
							currentUser.getProvider());
			if (tradingCurrency == null) {
				throw new EntityDoesNotExistsException(TradingCurrency.class,
						postData.getCode());
			}

			Currency currency = currencyService.findByCode(postData.getCode());

			if (currency != null) {
				Auditable auditable = new Auditable();
				auditable.setUpdated(new Date());
				auditable.setUpdater(currentUser);

				currency.setDescriptionEn(postData.getDescription());
				currency.setAuditable(auditable);

				tradingCurrency.setAuditable(auditable);
				tradingCurrency.setCurrency(currency);
				tradingCurrency.setCurrencyCode(postData.getCode());
				tradingCurrency.setPrDescription(postData.getDescription());
			} else {
				throw new EntityDoesNotExistsException(Currency.class,
						postData.getCode());
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

}
