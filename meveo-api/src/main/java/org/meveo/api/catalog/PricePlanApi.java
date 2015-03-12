package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.PricePlanDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class PricePlanApi extends BaseApi {

	@Inject
	private ChargeTemplateServiceAll chargeTemplateServiceAll;

	@Inject
	private SellerService sellerService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private PricePlanMatrixService pricePlanMatrixService;
	
	@Inject
	private CalendarService calendarService;

	public void create(PricePlanDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getEventCode()) && !StringUtils.isBlank(postData.getCode())) {
			Provider provider = currentUser.getProvider();

			// search for eventCode
			if (chargeTemplateServiceAll.findByCode(postData.getEventCode(), provider) == null) {
				throw new EntityDoesNotExistsException(ChargeTemplate.class, postData.getEventCode());
			}

			if (pricePlanMatrixService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(PricePlanMatrix.class, postData.getCode());
			}

			PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
			pricePlanMatrix.setCode(postData.getCode());
			pricePlanMatrix.setProvider(provider);
			pricePlanMatrix.setEventCode(postData.getEventCode());

			if (!StringUtils.isBlank(postData.getSeller())) {
				Seller seller = sellerService.findByCode(postData.getSeller(), provider);
				if (seller == null) {
					throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
				}
				pricePlanMatrix.setSeller(seller);
			}

			if (!StringUtils.isBlank(postData.getCountry())) {
				TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry(),
						provider);
				if (tradingCountry == null) {
					throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
				}
				pricePlanMatrix.setTradingCountry(tradingCountry);
			}

			if (!StringUtils.isBlank(postData.getCurrency())) {
				TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(
						postData.getCurrency(), provider);
				if (tradingCurrency == null) {
					throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
				}
				pricePlanMatrix.setTradingCurrency(tradingCurrency);
			}

			if (!StringUtils.isBlank(postData.getOfferTemplate())) {
				OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), provider);
				if (offerTemplate == null) {
					throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate());
				}
				pricePlanMatrix.setOfferTemplate(offerTemplate);
			}
			
            if (!StringUtils.isBlank(postData.getValidityCalendarCode())) {
                Calendar calendar = calendarService.findByCode(postData.getValidityCalendarCode(), provider);
                if (calendar == null) {
                    throw new EntityDoesNotExistsException(Calendar.class, postData.getValidityCalendarCode());
                }
                pricePlanMatrix.setValidityCalendar(calendar);
            }

			pricePlanMatrix.setMinQuantity(postData.getMinQuantity());
			pricePlanMatrix.setMaxQuantity(postData.getMaxQuantity());
			pricePlanMatrix.setStartSubscriptionDate(postData.getStartSubscriptionDate());
			pricePlanMatrix.setEndSubscriptionDate(postData.getEndSubscriptionDate());
			pricePlanMatrix.setStartRatingDate(postData.getStartRatingDate());
			pricePlanMatrix.setEndRatingDate(postData.getEndRatingDate());
			pricePlanMatrix.setMinSubscriptionAgeInMonth(postData.getMinSubscriptionAgeInMonth());
			pricePlanMatrix.setMaxSubscriptionAgeInMonth(postData.getMaxSubscriptionAgeInMonth());
			pricePlanMatrix.setAmountWithoutTax(postData.getAmountWithoutTax());
			pricePlanMatrix.setAmountWithTax(postData.getAmountWithTax());
			pricePlanMatrix.setPriority(postData.getPriority());
			pricePlanMatrix.setCriteria1Value(postData.getCriteria1());
			pricePlanMatrix.setCriteria2Value(postData.getCriteria2());
			pricePlanMatrix.setCriteria3Value(postData.getCriteria3());

			pricePlanMatrixService.create(pricePlanMatrix, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getEventCode())) {
				missingParameters.add("eventCode");
			}
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(PricePlanDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getEventCode()) && !StringUtils.isBlank(postData.getCode())) {
			Provider provider = currentUser.getProvider();

			// search for eventCode
			if (chargeTemplateServiceAll.findByCode(postData.getEventCode(), provider) == null) {
				throw new EntityDoesNotExistsException(ChargeTemplate.class, postData.getEventCode());
			}

			// search for price plan
			PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(postData.getCode(), provider);
			if (pricePlanMatrix == null) {
				throw new EntityDoesNotExistsException(PricePlanMatrix.class, postData.getCode());
			}
			pricePlanMatrix.setEventCode(postData.getEventCode());

			if (!StringUtils.isBlank(postData.getSeller())) {
				Seller seller = sellerService.findByCode(postData.getSeller(), provider);
				if (seller == null) {
					throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
				}
				pricePlanMatrix.setSeller(seller);
			}

			if (!StringUtils.isBlank(postData.getCountry())) {
				TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry(),
						provider);
				if (tradingCountry == null) {
					throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
				}
				pricePlanMatrix.setTradingCountry(tradingCountry);
			}

			if (!StringUtils.isBlank(postData.getCurrency())) {
				TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(
						postData.getCurrency(), provider);
				if (tradingCurrency == null) {
					throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
				}
				pricePlanMatrix.setTradingCurrency(tradingCurrency);
			}

			if (!StringUtils.isBlank(postData.getOfferTemplate())) {
				OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), provider);
				if (offerTemplate == null) {
					throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate());
				}
				pricePlanMatrix.setOfferTemplate(offerTemplate);
			}
			
            if (!StringUtils.isBlank(postData.getValidityCalendarCode())) {
                Calendar calendar = calendarService.findByCode(postData.getValidityCalendarCode(), provider);
                if (calendar == null) {
                    throw new EntityDoesNotExistsException(Calendar.class, postData.getValidityCalendarCode());
                }
                pricePlanMatrix.setValidityCalendar(calendar);
            } else {
                pricePlanMatrix.setValidityCalendar(null);
            }

			pricePlanMatrix.setMinQuantity(postData.getMinQuantity());
			pricePlanMatrix.setMaxQuantity(postData.getMaxQuantity());
			pricePlanMatrix.setStartSubscriptionDate(postData.getStartSubscriptionDate());
			pricePlanMatrix.setEndSubscriptionDate(postData.getEndSubscriptionDate());
			pricePlanMatrix.setStartRatingDate(postData.getStartRatingDate());
			pricePlanMatrix.setEndRatingDate(postData.getEndRatingDate());
			pricePlanMatrix.setMinSubscriptionAgeInMonth(postData.getMinSubscriptionAgeInMonth());
			pricePlanMatrix.setMaxSubscriptionAgeInMonth(postData.getMaxSubscriptionAgeInMonth());
			pricePlanMatrix.setAmountWithoutTax(postData.getAmountWithoutTax());
			pricePlanMatrix.setAmountWithTax(postData.getAmountWithTax());
			pricePlanMatrix.setPriority(postData.getPriority());
			pricePlanMatrix.setCriteria1Value(postData.getCriteria1());
			pricePlanMatrix.setCriteria2Value(postData.getCriteria2());
			pricePlanMatrix.setCriteria3Value(postData.getCriteria3());

			pricePlanMatrixService.update(pricePlanMatrix, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getEventCode())) {
				missingParameters.add("eventCode");
			}
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public PricePlanDto find(String pricePlanCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(pricePlanCode)) {
			PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanCode, provider);
			if (pricePlanMatrix == null) {
				throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanCode);
			}

			return new PricePlanDto(pricePlanMatrix);
		} else {
			missingParameters.add("pricePlanCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String pricePlanCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(pricePlanCode)) {
			PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanCode, provider);
			if (pricePlanMatrix == null) {
				throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanCode);
			}

			pricePlanMatrixService.remove(pricePlanMatrix);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
