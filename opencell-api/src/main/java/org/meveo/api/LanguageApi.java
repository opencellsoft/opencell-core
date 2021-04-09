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
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.LanguagesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.billing.impl.TradingLanguageService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Edward P. Legaspi
 * 
 **/
@Stateless
public class LanguageApi extends BaseApi {

    @Inject
    private LanguageService languageService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    public LanguagesDto list() {
        LanguagesDto result = new LanguagesDto();

        List<TradingLanguage> tradingLanguages =
                tradingLanguageService.list(GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration());
        if (tradingLanguages != null) {
            for (TradingLanguage language : tradingLanguages) {
                result.getLanguage().add(new LanguageDto(language));
            }
        }

        return result;
    }

    public void create(LanguageDto postData) throws MissingParameterException, EntityAlreadyExistsException, EntityDoesNotExistsException, BusinessException {

        validateCode(postData);

        if (tradingLanguageService.findByTradingLanguageCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(TradingLanguage.class, postData.getCode());
        }

        Language language = languageService.findByCode(postData.getCode());

        if (language == null) {
            // create
            language = new Language();
            language.setLanguageCode(postData.getCode());
            language.setDescriptionEn(postData.getDescription());
            language.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
            languageService.create(language);
        }

        TradingLanguage tradingLanguage = new TradingLanguage();
        tradingLanguage.setLanguage(language);
        tradingLanguage.setLanguageCode(postData.getCode());
        tradingLanguage.setPrDescription(postData.getDescription());
        if (postData.isDisabled() != null) {
            tradingLanguage.setDisabled(postData.isDisabled());
        }
        tradingLanguageService.create(tradingLanguage);
    }

    private void validateCode(LanguageDto postData) {
        if (StringUtils.isBlank(postData.getCode())) {
            String generatedCode = getGenericCode(Language.class.getName());
            if (generatedCode != null) {
                postData.setCode(generatedCode);
            } else {
                missingParameters.add("code");
            }
            handleMissingParameters();
        }
    }

    public void remove(String code) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(code);
        if (tradingLanguage == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, code);
        }
        tradingLanguageService.remove(tradingLanguage);
    }

    public void update(LanguageDto postData) throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getCode());
        if (tradingLanguage == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getCode());
        }

        Language language = languageService.findByCode(postData.getCode());

        if (language == null) {
            throw new EntityDoesNotExistsException(Language.class, postData.getCode());
        }

        language.setDescriptionEn(postData.getDescription());
        language.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        tradingLanguage.setLanguage(language);
        tradingLanguage.setLanguageCode(postData.getCode());
        tradingLanguage.setPrDescription(postData.getDescription());

        tradingLanguageService.update(tradingLanguage);

    }

    public LanguageDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(code);

        if (tradingLanguage == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, code);
        }
        return new LanguageDto(tradingLanguage);
    }

    /**
     * Create or update Language based on the trading language code.
     * 
     * @param postData posted data
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(LanguageDto postData) throws MeveoApiException, BusinessException {
        if(postData.getCode() != null && tradingLanguageService.findByTradingLanguageCode(postData.getCode()) != null) {
            update(postData);
        } else {
            create(postData);
        }
    }

    public void findOrCreate(String languageCode) throws EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(languageCode)) {
            return;
        }
        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(languageCode);
        if (tradingLanguage == null) {
            Language language = languageService.findByCode(languageCode);
            if (language == null) {
                throw new EntityDoesNotExistsException(Language.class, languageCode);
            }
            tradingLanguage = new TradingLanguage();
            tradingLanguage.setLanguage(language);
            tradingLanguage.setPrDescription(language.getDescriptionEn());
            tradingLanguageService.create(tradingLanguage);
        }
    }

    /**
     * Enable or disable Trading language
     * 
     * @param code Language code
     * @param enable Should Trading language be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(code);
        if (tradingLanguage == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, code);
        }
        if (enable) {
            tradingLanguageService.enable(tradingLanguage);
        } else {
            tradingLanguageService.disable(tradingLanguage);
        }
    }
}