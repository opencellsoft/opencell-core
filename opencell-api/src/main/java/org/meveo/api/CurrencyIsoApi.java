package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CurrencyIsoDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.service.admin.impl.CurrencyService;


/**
 * @author Mounir HAMMAM
 **/
@Stateless
public class CurrencyIsoApi extends BaseApi {

    @Inject
    private CurrencyService currencyService;

    public void create(CurrencyIsoDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
 
        handleMissingParameters();

        if (currencyService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Currency.class, postData.getCode());
        }

        Currency currency = new Currency();
        currency.setCurrencyCode(postData.getCode());
        currency.setDescriptionEn(postData.getDescription());
        currencyService.create(currency);

    }

    public void update(CurrencyIsoDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        Currency currency = currencyService.findByCode(postData.getCode());

        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCode());
        }
        currency.setDescriptionEn(postData.getDescription());

        currencyService.update(currency);
    }

    public CurrencyIsoDto find(String currencyCode) throws MeveoApiException {

        if (StringUtils.isBlank(currencyCode)) {
            missingParameters.add("currencyCode");
            handleMissingParameters();
        }

        CurrencyIsoDto result = new CurrencyIsoDto();

        Currency currency = currencyService.findByCode(currencyCode);
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, currencyCode);
        }

        result = new CurrencyIsoDto(currency);

        return result;
    }

    public void remove(String currencyCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(currencyCode)) {
            missingParameters.add("currencyCode");
            handleMissingParameters();
        }

        Currency currency = currencyService.findByCode(currencyCode);
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, currencyCode);
        }

        currencyService.remove(currency);
    }

    public void createOrUpdate(CurrencyIsoDto postData) throws MeveoApiException, BusinessException {

        Currency currency = currencyService.findByCode(postData.getCode());
        if (currency == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
    
	public List<CurrencyIsoDto> list() {
		List<CurrencyIsoDto> result = new ArrayList<>();

		List<Currency> currencies = currencyService.list();
		if (currencies != null) {
			for (Currency country : currencies) {
				result.add(new CurrencyIsoDto(country));
			}
		}

		return result;
	}
	
}