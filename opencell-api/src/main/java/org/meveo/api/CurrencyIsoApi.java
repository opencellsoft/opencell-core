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
import org.meveo.api.dto.CurrencyIsoDto;
import org.meveo.api.dto.response.GetCurrenciesIsoResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.service.admin.impl.CurrencyService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


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

    public GetCurrenciesIsoResponse list(PagingAndFiltering pagingAndFiltering) {
        GetCurrenciesIsoResponse result = new GetCurrenciesIsoResponse();
        result.setPaging( pagingAndFiltering );

        List<Currency> currencies = currencyService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (currencies != null) {
            for (Currency currency : currencies) {
                result.getCurrencies().add(new CurrencyIsoDto(currency));
            }
        }

        return result;
    }
	
}