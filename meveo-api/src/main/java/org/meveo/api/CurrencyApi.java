package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CurrencyService;


/**
 * @author Mounir HAMMAM
 **/
@Stateless
public class CurrencyApi extends BaseApi {

    @Inject
    private CurrencyService currencyService;

    public void create(CurrencyDto postData, User currentUser) throws MeveoApiException, BusinessException {

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
        currencyService.create(currency, currentUser);

    }

    public void update(CurrencyDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        Currency currency = currencyService.findByCode(postData.getCode());

        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCode());
        }
        currency.setDescriptionEn(postData.getDescription());

        currencyService.update(currency, currentUser);
    }

    public CurrencyDto find(String currencyCode) throws MeveoApiException {

        if (StringUtils.isBlank(currencyCode)) {
            missingParameters.add("currencyCode");
            handleMissingParameters();
        }

        CurrencyDto result = new CurrencyDto();

        Currency currency = currencyService.findByCode(currencyCode);
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, currencyCode);
        }

        result = new CurrencyDto(currency);

        return result;
    }

    public void remove(String currencyCode) throws MeveoApiException {
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

    public void createOrUpdate(CurrencyDto postData, User currentUser) throws MeveoApiException, BusinessException {

        Currency currency = currencyService.findByCode(postData.getCode());
        if (currency == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}