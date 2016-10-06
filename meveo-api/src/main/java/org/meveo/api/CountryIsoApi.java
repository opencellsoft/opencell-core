package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CountryIsoDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.catalog.Calendar;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;


/**
 * @author Mounir HAMMAM
 **/
@Stateless
public class CountryIsoApi extends BaseApi {

    @Inject
    private CountryService countryService;

    @Inject
    private CurrencyService currencyService;
    
    @Inject
    private LanguageService languageService;

    public void create(CountryIsoDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCountryCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getLanguageCode())) {
            missingParameters.add("languageCode");
        }
        if (StringUtils.isBlank(postData.getCurrencyCode())) {
            missingParameters.add("currencyCode");
        }

        handleMissingParameters();
        
        if (countryService.findByCode(postData.getCountryCode()) != null) {
            throw new EntityAlreadyExistsException(Country.class, postData.getCountryCode());
        }

        Language language = languageService.findByCode(postData.getLanguageCode());
        if (language == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getLanguageCode());
        }
        
        Currency currency = currencyService.findByCode(postData.getCurrencyCode());
        if (currency == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCurrencyCode());
        }

        Country country = new Country();
        country.setCountryCode(postData.getCountryCode());
        country.setDescriptionEn(postData.getNameEn());
        country.setDescriptionFr(postData.getNameFr());
        country.setLanguage(language);
        country.setCurrency(currency);

        countryService.create(country, currentUser);

    }

    public void update(CountryIsoDto postData, User currentUser) throws MeveoApiException, BusinessException {

    	 if (StringUtils.isBlank(postData.getCountryCode())) {
             missingParameters.add("code");
         }
    	 if (StringUtils.isBlank(postData.getNameEn())) {
             missingParameters.add("nameEn");
         }
         if (StringUtils.isBlank(postData.getLanguageCode())) {
             missingParameters.add("languageCode");
         }
         if (StringUtils.isBlank(postData.getCurrencyCode())) {
             missingParameters.add("currencyCode");
         }

        handleMissingParameters();

        Country country = countryService.findByCode(postData.getCountryCode());

        if (country == null) {
            throw new EntityDoesNotExistsException(Country.class, postData.getCountryCode());
        }

        Language language = languageService.findByCode(postData.getLanguageCode());
        if (language == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getLanguageCode());
        }
        
        Currency currency = currencyService.findByCode(postData.getCurrencyCode());
        if (currency == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCurrencyCode());
        }

        
        country.setCountryCode(postData.getCountryCode());
        country.setDescriptionEn(postData.getNameEn());
        country.setDescriptionFr(postData.getNameFr());
        country.setLanguage(language);
        country.setCurrency(currency);

        countryService.update(country, currentUser);
    }

    public CountryIsoDto find(String countryCode) throws MeveoApiException {

        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
            handleMissingParameters();
        }

        CountryIsoDto result = new CountryIsoDto();

        Country country = countryService.findByCode(countryCode);
        if (country == null) {
            throw new EntityDoesNotExistsException(Country.class, countryCode);
        }

        result = new CountryIsoDto(country);

        return result;
    }

    public void remove(String countryCode, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
            handleMissingParameters();
        }

        Country country = countryService.findByCode(countryCode);
        if (country == null) {
            throw new EntityDoesNotExistsException(Country.class, countryCode);
        }

        countryService.remove(country, currentUser);
    }

    public void createOrUpdate(CountryIsoDto postData, User currentUser) throws MeveoApiException, BusinessException {

        Country country = countryService.findByCode(postData.getCountryCode());
        if (country == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}