package org.meveo.api;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.SellersDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class SellerApi extends BaseApi {

	@Inject
	private SellerService sellerService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private TradingLanguageService tradingLanguageService;

	public void create(SellerDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			if (sellerService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(Seller.class, postData.getCode());
			}

			Seller seller = new Seller();
			seller.setCode(postData.getCode());
			seller.setDescription(postData.getDescription());
			seller.setInvoicePrefix(postData.getInvoicePrefix());
			seller.setProvider(provider);

			// check trading entities
			if (!StringUtils.isBlank(postData.getCurrencyCode())) {
				TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(
						postData.getCurrencyCode(), provider);
				if (tradingCurrency == null) {
					throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrencyCode());
				}

				seller.setTradingCurrency(tradingCurrency);
			}

			if (!StringUtils.isBlank(postData.getCountryCode())) {
				TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(
						postData.getCountryCode(), provider);
				if (tradingCountry == null) {
					throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
				}

				seller.setTradingCountry(tradingCountry);
			}

			if (!StringUtils.isBlank(postData.getLanguageCode())) {
				TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(
						postData.getLanguageCode(), provider);
				if (tradingLanguage == null) {
					throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguageCode());
				}

				seller.setTradingLanguage(tradingLanguage);
			}

			// check parent seller
			if (!StringUtils.isBlank(postData.getParentSeller())) {
				Seller parentSeller = sellerService.findByCode(postData.getParentSeller(), provider);
				if (parentSeller == null) {
					throw new EntityDoesNotExistsException(Seller.class, postData.getParentSeller());
				}

				seller.setSeller(parentSeller);
			}

			sellerService.create(seller, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(SellerDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			Seller seller = sellerService.findByCode(postData.getCode(), provider);
			if (seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, postData.getCode());
			}

			seller.setDescription(postData.getDescription());
			seller.setInvoicePrefix(postData.getInvoicePrefix());

			// check trading entities
			if (!StringUtils.isBlank(postData.getCurrencyCode())) {
				TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(
						postData.getCurrencyCode(), provider);
				if (tradingCurrency == null) {
					throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrencyCode());
				}

				seller.setTradingCurrency(tradingCurrency);
			}

			if (!StringUtils.isBlank(postData.getCountryCode())) {
				TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(
						postData.getCountryCode(), provider);
				if (tradingCountry == null) {
					throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
				}

				seller.setTradingCountry(tradingCountry);
			}

			if (!StringUtils.isBlank(postData.getLanguageCode())) {
				TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(
						postData.getLanguageCode(), provider);
				if (tradingLanguage == null) {
					throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguageCode());
				}

				seller.setTradingLanguage(tradingLanguage);
			}

			// check parent seller
			if (!StringUtils.isBlank(postData.getParentSeller())) {
				Seller parentSeller = sellerService.findByCode(postData.getParentSeller(), provider);
				if (parentSeller == null) {
					throw new EntityDoesNotExistsException(Seller.class, postData.getParentSeller());
				}

				seller.setSeller(parentSeller);
			}

			sellerService.update(seller, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public SellerDto find(String sellerCode, Provider provider) throws MeveoApiException {
		SellerDto result = new SellerDto();

		if (!StringUtils.isBlank(sellerCode)) {
			Seller seller = sellerService.findByCode(sellerCode, provider,
					Arrays.asList("tradingCountry", "tradingCurrency", "tradingLanguage"));
			if (seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, sellerCode);
			}

			result = new SellerDto(seller);
		} else {
			if (StringUtils.isBlank(sellerCode)) {
				missingParameters.add("sellerCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void remove(String sellerCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(sellerCode)) {
			Seller seller = sellerService.findByCode(sellerCode, provider);
			if (seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, sellerCode);
			}

			sellerService.remove(seller);
		} else {
			if (StringUtils.isBlank(sellerCode)) {
				missingParameters.add("sellerCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public SellersDto list(Provider provider) {
		SellersDto result = new SellersDto();

		List<Seller> sellers = sellerService.list(provider);
		if (sellers != null) {
			for (Seller seller : sellers) {
				result.getSeller().add(new SellerDto(seller));
			}
		}

		return result;
	}

}
