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

package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceDto;
import org.meveo.api.exception.*;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.RealtimeChargingService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;

@Stateless
public class OneShotChargeTemplateApi extends ChargeTemplateApi<OneShotChargeTemplate, OneShotChargeTemplateDto> {

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private RealtimeChargingService realtimeChargingService;

    @Inject
    private SellerService sellerService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Override
    public OneShotChargeTemplate create(OneShotChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(OneShotChargeTemplate.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (StringUtils.isBlank(postData.getOneShotChargeTemplateType())) {
            missingParameters.add("oneShotChargeTemplateType");
        }
        if (StringUtils.isBlank(postData.getTaxClassCode())) {
            missingParameters.add("taxClassCode");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        if (oneShotChargeTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(OneShotChargeTemplate.class, postData.getCode());
        }

        OneShotChargeTemplate chargeTemplate = dtoToEntity(postData, null);

        oneShotChargeTemplateService.create(chargeTemplate);

        return chargeTemplate;
    }

    /**
     * Convert/update DTO object to an entity object
     *
     * @param postData DTO object
     * @param chargeTemplate Entity object to update
     * @return A new or updated entity object
     * @throws MeveoApiException General API exception
     * @throws BusinessException General exception
     */
    private OneShotChargeTemplate dtoToEntity(OneShotChargeTemplateDto postData, OneShotChargeTemplate chargeTemplate) throws MeveoApiException, BusinessException {

        boolean isNew = chargeTemplate == null;

        if (isNew) {
            chargeTemplate = new OneShotChargeTemplate();
            chargeTemplate.setCode(postData.getCode());
        } else {
            chargeTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        }

        super.dtoToEntity(postData, chargeTemplate, isNew);

        chargeTemplate.setOneShotChargeTemplateType(postData.getOneShotChargeTemplateType());

        chargeTemplate.setImmediateInvoicing(postData.getImmediateInvoicing());

        return chargeTemplate;
    }

    @Override
    public OneShotChargeTemplate update(OneShotChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getInvoiceSubCategory() != null && StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (postData.getTaxClassCode() != null && StringUtils.isBlank(postData.getTaxClassCode())) {
            missingParameters.add("taxClassCode");
        }
        if (postData.getOneShotChargeTemplateType() != null && StringUtils.isBlank(postData.getOneShotChargeTemplateType())) {
            missingParameters.add("oneShotChargeTemplateType");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(postData.getCode());
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getCode());
        }

        chargeTemplate = dtoToEntity(postData, chargeTemplate);

        chargeTemplate = oneShotChargeTemplateService.update(chargeTemplate);

        return chargeTemplate;
    }

    @Override
    public OneShotChargeTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("oneShotChargeTemplateCode");
            handleMissingParameters();
        }

        OneShotChargeTemplateDto result = new OneShotChargeTemplateDto();

        // check if code already exists
        OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(code, Arrays.asList("invoiceSubCategory"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, code);
        }

        result = new OneShotChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }

    public List<OneShotChargeTemplateWithPriceDto> listWithPrice(String languageCode, String buyersCountryCode, String buyersCurrencyCode, String sellerCode, Date date) throws MeveoApiException, BusinessException {

        Seller seller = sellerService.findByCode(sellerCode);
        TradingCurrency currency = tradingCurrencyService.findByTradingCurrencyCode(buyersCurrencyCode);
        TradingCountry buyersCountry = tradingCountryService.findByCode(buyersCountryCode);

        List<OneShotChargeTemplate> oneShotChargeTemplates = oneShotChargeTemplateService.getSubscriptionChargeTemplates();
        List<OneShotChargeTemplateWithPriceDto> oneShotChargeTemplatesWPrice = new ArrayList<>();

        for (OneShotChargeTemplate oneShotChargeTemplate : oneShotChargeTemplates) {
            OneShotChargeTemplateWithPriceDto oneShotChargeDto = new OneShotChargeTemplateWithPriceDto();
            oneShotChargeDto.setChargeCode(oneShotChargeTemplate.getCode());
            oneShotChargeDto.setDescription(oneShotChargeTemplate.getDescription());

            if (buyersCountry == null) {
                log.warn("country with code={} does not exists", buyersCountryCode);
            } else {

                Amounts unitPrice = realtimeChargingService.getApplicationPrice(seller, null, currency, buyersCountry, oneShotChargeTemplate, date, null, BigDecimal.ONE, null, null, null, true);
                if (unitPrice != null) {
                    oneShotChargeDto.setUnitPriceWithoutTax(unitPrice.getAmountWithoutTax().doubleValue());
                    oneShotChargeDto.setTaxCode(unitPrice.getTax().getCode());
                    oneShotChargeDto.setTaxDescription(unitPrice.getTax().getDescription());
                    oneShotChargeDto.setTaxPercent(unitPrice.getTax().getPercent().doubleValue());
                }
            }

            oneShotChargeTemplatesWPrice.add(oneShotChargeDto);
        }

        return oneShotChargeTemplatesWPrice;
    }

    @Override
    protected BiFunction<OneShotChargeTemplate, CustomFieldsDto, OneShotChargeTemplateDto> getEntityToDtoFunction() {
        return OneShotChargeTemplateDto::new;
    }
}