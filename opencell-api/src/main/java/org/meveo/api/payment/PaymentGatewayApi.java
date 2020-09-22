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

/**
 * 
 */
package org.meveo.api.payment;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.model.SortOrder;

/**
 * PaymentGatewayDto CRUD.
 * 
 * @author anasseh
 * @author Mounir Bahije
 * @lastModifiedVersion 9.3
 */
@Stateless
public class PaymentGatewayApi extends BaseCrudApi<PaymentGateway, PaymentGatewayDto> {

    /** The payment gateway service. */
    @Inject
    private PaymentGatewayService paymentGatewayService;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    /** The trading currency service. */
    @Inject
    private TradingCurrencyService tradingCurrencyService;

    /** The country service. */
    @Inject
    private CountryService countryService;

    /** The seller service. */
    @Inject
    private SellerService sellerService;


    @Override
    public PaymentGateway create(PaymentGatewayDto paymentGatewayDto) throws MeveoApiException, BusinessException {
        String code = null;

        code = paymentGatewayDto.getCode();
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        if (paymentGatewayDto.getType() == null) {
            missingParameters.add("type");
        }

        if (paymentGatewayDto.getPaymentMethodType() == null) {
            missingParameters.add("paymentMethodType");
        }
		if (StringUtils.isBlank(paymentGatewayDto.getSellerCode())) {
			missingParameters.add("sellerCode");
		}

		if (paymentGatewayDto.getType() == PaymentGatewayTypeEnum.CUSTOM) {
			if (StringUtils.isBlank(paymentGatewayDto.getScriptInstanceCode())) {
				missingParameters.add("ScriptInstanceCode");
			}
		}

		if (paymentGatewayDto.getType() == PaymentGatewayTypeEnum.NATIF) {
			if (StringUtils.isBlank(paymentGatewayDto.getImplementationClassName())) {
				missingParameters.add("ImplementationClassName");
			}
		}

		handleMissingParameters();
        handleMissingParameters();

        PaymentGateway paymentGateway = paymentGatewayService.findByCode(code);
        if (paymentGateway != null) {
            throw new EntityAlreadyExistsException(PaymentGateway.class, code);
        }

        paymentGateway = fromDto(paymentGatewayDto, new PaymentGateway());

        paymentGatewayService.create(paymentGateway);
        return paymentGateway;
    }

    @Override
    public PaymentGateway update(PaymentGatewayDto paymentGatewayDto) throws BusinessException, MeveoApiException {
        String code = null;
        if (paymentGatewayDto == null) {
            missingParameters.add("paymentGatewayDto");
            handleMissingParameters();
            return null;
        }

        if (StringUtils.isBlank(paymentGatewayDto.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        PaymentGateway paymentGateway = null;
        code = paymentGatewayDto.getCode();
        paymentGateway = paymentGatewayService.findByCode(code);
        if (paymentGateway == null) {
            throw new EntityDoesNotExistsException(PaymentGateway.class, code);
        }

        paymentGateway = fromDto(paymentGatewayDto, paymentGateway);
        paymentGateway.setCode(StringUtils.isBlank(paymentGatewayDto.getUpdatedCode()) ? code : paymentGatewayDto.getUpdatedCode());
        paymentGateway = paymentGatewayService.update(paymentGateway);
        return paymentGateway;
    }

    @Override
    public PaymentGatewayDto find(String paymentGatewayCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(paymentGatewayCode)) {
            missingParameters.add("paymentGatewayCode");
        }

        handleMissingParameters();

        PaymentGateway paymentGateway = null;
        paymentGateway = paymentGatewayService.findByCode(paymentGatewayCode);
        if (paymentGateway == null) {
            throw new EntityDoesNotExistsException(PaymentGateway.class, paymentGatewayCode);
        }
        return new PaymentGatewayDto(paymentGateway);
    }

    /**
     * List the PaymentGateways for given criteria.
     *
     * @param pagingAndFiltering the paging and filtering
     * @return the payment gateways dto
     * @throws InvalidParameterException the invalid parameter exception
     */
    public PaymentGatewayResponseDto list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
        PaymentGatewayResponseDto result = new PaymentGatewayResponseDto();
        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.DESCENDING, null, pagingAndFiltering, PaymentGateway.class);
        Long totalCount = paymentGatewayService.count(paginationConfig);
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        if (totalCount > 0) {
            List<PaymentGateway> paymentGateways = paymentGatewayService.list(paginationConfig);
            for (PaymentGateway paymentGateway : paymentGateways) {
                result.getPaymentGateways().add(new PaymentGatewayDto(paymentGateway));
            }
        }
        return result;
    }

    /**
     * From dto.
     *
     * @param paymentGatewayDto the payment gateway dto
     * @param paymentGateway the payment gateway
     * @return the payment gateway
     * @throws MeveoApiException the meveo api exception
     */
    private PaymentGateway fromDto(PaymentGatewayDto paymentGatewayDto, PaymentGateway paymentGateway) throws MeveoApiException {
        paymentGateway.setCode(paymentGatewayDto.getCode());
        if (!StringUtils.isBlank(paymentGatewayDto.getTradingCurrencyCode())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(paymentGatewayDto.getTradingCurrencyCode());
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, paymentGatewayDto.getTradingCurrencyCode());
            }
            paymentGateway.setTradingCurrency(tradingCurrency);
        }
        if (!StringUtils.isBlank(paymentGatewayDto.getCountryCode())) {
            Country country = countryService.findByCode(paymentGatewayDto.getCountryCode());
            if (country == null) {
                throw new EntityDoesNotExistsException(Country.class, paymentGatewayDto.getCountryCode());
            }
            paymentGateway.setCountry(country);
        }
        if (paymentGatewayDto.getApplicationEL() != null) {
            paymentGateway.setApplicationEL(paymentGatewayDto.getApplicationEL());
        }
        if (paymentGatewayDto.getCardType() != null) {
            paymentGateway.setCardType(paymentGatewayDto.getCardType());
        }
        if (!StringUtils.isBlank(paymentGatewayDto.getScriptInstanceCode())) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(paymentGatewayDto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, paymentGatewayDto.getScriptInstanceCode());
            }
            paymentGateway.setScriptInstance(scriptInstance);
        }
        if (!StringUtils.isBlank(paymentGatewayDto.getType())) {
            paymentGateway.setType(paymentGatewayDto.getType());
        }
        if (!StringUtils.isBlank(paymentGatewayDto.getDescription())) {
            paymentGateway.setDescription(paymentGatewayDto.getDescription());
        }
        if (paymentGatewayDto.getPaymentMethodType() != null) {
            paymentGateway.setPaymentMethodType(paymentGatewayDto.getPaymentMethodType());
        }

        if (!StringUtils.isBlank(paymentGatewayDto.getMarchandId())) {
            paymentGateway.setMarchandId(paymentGatewayDto.getMarchandId());
        }
        if (!StringUtils.isBlank(paymentGatewayDto.getSecretKey())) {
            paymentGateway.setSecretKey(paymentGatewayDto.getSecretKey());
        }
        if (!StringUtils.isBlank(paymentGatewayDto.getApiKey())) {
            paymentGateway.setApiKey(paymentGatewayDto.getApiKey());
        }

        if (!StringUtils.isBlank(paymentGatewayDto.getWebhooksKeyId())) {
            paymentGateway.setWebhooksKeyId(paymentGatewayDto.getWebhooksKeyId());
        }
        if (!StringUtils.isBlank(paymentGatewayDto.getWebhooksSecretKey())) {
            paymentGateway.setWebhooksSecretKey(paymentGatewayDto.getWebhooksSecretKey());
        }

        if (!StringUtils.isBlank(paymentGatewayDto.getProfile())) {
            paymentGateway.setProfile(paymentGatewayDto.getProfile());
        }
        if (!StringUtils.isBlank(paymentGatewayDto.getImplementationClassName())) {
            paymentGateway.setImplementationClassName(paymentGatewayDto.getImplementationClassName());
        }

        if (!StringUtils.isBlank(paymentGatewayDto.getSellerCode())) {
            Seller seller = sellerService.findByCode(paymentGatewayDto.getSellerCode());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, paymentGatewayDto.getSellerCode());
            }
            paymentGateway.setSeller(seller);
        }

        if (paymentGatewayDto.getBankCoordinates() != null) {
            if (paymentGateway.getBankCoordinates() == null) {
                paymentGateway.setBankCoordinates(new BankCoordinates());
            }
            BankCoordinates bankCoordinates = paymentGateway.getBankCoordinates();
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getBankCode())) {
                bankCoordinates.setBankCode(paymentGatewayDto.getBankCoordinates().getBankCode());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getBranchCode())) {
                bankCoordinates.setBranchCode(paymentGatewayDto.getBankCoordinates().getBranchCode());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getAccountNumber())) {
                bankCoordinates.setAccountNumber(paymentGatewayDto.getBankCoordinates().getAccountNumber());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getKey())) {
                bankCoordinates.setKey(paymentGatewayDto.getBankCoordinates().getKey());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getIban())) {
                bankCoordinates.setIban(paymentGatewayDto.getBankCoordinates().getIban());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getBic())) {
                bankCoordinates.setBic(paymentGatewayDto.getBankCoordinates().getBic());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getAccountOwner())) {
                bankCoordinates.setAccountOwner(paymentGatewayDto.getBankCoordinates().getAccountOwner());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getBankName())) {
                bankCoordinates.setBankName(paymentGatewayDto.getBankCoordinates().getBankName());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getBankId())) {
                bankCoordinates.setBankId(paymentGatewayDto.getBankCoordinates().getBankId());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getIssuerNumber())) {
                bankCoordinates.setIssuerNumber(paymentGatewayDto.getBankCoordinates().getIssuerNumber());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getIssuerName())) {
                bankCoordinates.setIssuerName(paymentGatewayDto.getBankCoordinates().getIssuerName());
            }
            if (!StringUtils.isBlank(paymentGatewayDto.getBankCoordinates().getIcs())) {
                bankCoordinates.setIcs(paymentGatewayDto.getBankCoordinates().getIcs());
            }
        }

        try {
            populateCustomFields(paymentGatewayDto.getCustomFields(), paymentGateway, true, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        return paymentGateway;
    }
}