/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CountryIsoDto;
import org.meveo.api.dto.response.GetCountriesIsoResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.catalog.Calendar;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

    public void create(CountryIsoDto postData) throws MeveoApiException, BusinessException {

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
            throw new EntityDoesNotExistsException(Language.class, postData.getLanguageCode());
        }

        Currency currency = currencyService.findByCode(postData.getCurrencyCode());
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
        }

        Country country = new Country();
        country.setCountryCode(postData.getCountryCode());
        country.setDescription(postData.getDescription());

        country.setLanguage(language);
        country.setCurrency(currency);

        country.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        countryService.create(country);

    }

    public void update(CountryIsoDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCountryCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        Country country = countryService.findByCode(postData.getCountryCode());

        if (country == null) {
            throw new EntityDoesNotExistsException(Country.class, postData.getCountryCode());
        }

        Language language = null;
        if (!StringUtils.isBlank(postData.getLanguageCode())) {
            language = languageService.findByCode(postData.getLanguageCode());
            if (language == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getLanguageCode());
            }
        }
        Currency currency = null;
        if (!StringUtils.isBlank(postData.getCurrencyCode())) {
            currency = currencyService.findByCode(postData.getCurrencyCode());
            if (currency == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getCurrencyCode());
            }
        }

        country.setDescription(postData.getDescription());
        if (language != null) {
            country.setLanguage(language);
        }
        if (currency != null) {
            country.setCurrency(currency);
        }

        if (postData.getLanguageDescriptions() != null) {
            country.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), country.getDescriptionI18n()));
        }
        countryService.update(country);
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

    public void remove(String countryCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
            handleMissingParameters();
        }

        Country country = countryService.findByCode(countryCode);
        if (country == null) {
            throw new EntityDoesNotExistsException(Country.class, countryCode);
        }

        countryService.remove(country);
    }

    public void createOrUpdate(CountryIsoDto postData) throws MeveoApiException, BusinessException {

        Country country = countryService.findByCode(postData.getCountryCode());
        if (country == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    public List<CountryIsoDto> list() {
        List<CountryIsoDto> result = new ArrayList<>();

        List<Country> countries = countryService.list();
        if (countries != null) {
            for (Country country : countries) {
                result.add(new CountryIsoDto(country));
            }
        }

        return result;
    }

    public GetCountriesIsoResponse list(PagingAndFiltering pagingAndFiltering) {
        GetCountriesIsoResponse result = new GetCountriesIsoResponse();
        result.setPaging( pagingAndFiltering );

        List<Country> countries = countryService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (countries != null) {
            for (Country country : countries) {
                result.getCountries().add(new CountryIsoDto(country));
            }
        }

        return result;
    }
}