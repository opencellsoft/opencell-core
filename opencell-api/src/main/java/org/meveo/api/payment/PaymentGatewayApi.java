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
import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.model.SortOrder;

/**
 * PaymentGatewayDto CRUD.
 * 
 * @author anasseh
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
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

        if (paymentGatewayDto.getType() == PaymentGatewayTypeEnum.CUSTOM && StringUtils.isBlank(paymentGatewayDto.getScriptInstanceCode())) {
            missingParameters.add("scriptInstanceCode");
        }

        handleMissingParameters();

        PaymentGateway paymentGateway = paymentGatewayService.findByCode(code);
        if (paymentGateway != null) {
            throw new EntityAlreadyExistsException(PaymentGateway.class, code);
        }

        ScriptInstance scriptInstance =  null;
        if(paymentGatewayDto.getType() == PaymentGatewayTypeEnum.CUSTOM) {
            scriptInstance = scriptInstanceService.findByCode(paymentGatewayDto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, paymentGatewayDto.getScriptInstanceCode());
            }
        }

        TradingCurrency tradingCurrency = null;
        if (!StringUtils.isBlank(paymentGatewayDto.getTradingCurrencyCode())) {
            tradingCurrencyService.findByTradingCurrencyCode(paymentGatewayDto.getTradingCurrencyCode());
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, paymentGatewayDto.getTradingCurrencyCode());
            }
        }

        Country country = null;
        if (!StringUtils.isBlank(paymentGatewayDto.getCountryCode())) {
            countryService.findByCode(paymentGatewayDto.getCountryCode());
            if (country == null) {
                throw new EntityDoesNotExistsException(Country.class, paymentGatewayDto.getCountryCode());
            }
        }
        paymentGateway = new PaymentGateway();
        paymentGateway.setType(paymentGatewayDto.getType());
        paymentGateway.setApplicationEL(paymentGatewayDto.getApplicationEL());
        paymentGateway.setCardType(paymentGatewayDto.getCardType());
        paymentGateway.setCode(code);
        paymentGateway.setDescription(paymentGatewayDto.getDescription());
        paymentGateway.setPaymentMethodType(paymentGatewayDto.getPaymentMethodType());
        paymentGateway.setScriptInstance(scriptInstance);
        paymentGateway.setCountry(country);
        paymentGateway.setTradingCurrency(tradingCurrency);
        paymentGateway.setMarchandId(paymentGatewayDto.getMarchandId());
        paymentGateway.setSecretKey(paymentGatewayDto.getSecretKey());
        paymentGateway.setApiKey(paymentGatewayDto.getApiKey());
        paymentGateway.setWebhooksKeyId(paymentGatewayDto.getWebhooksKeyId());
        paymentGateway.setWebhooksSecretKey(paymentGatewayDto.getWebhooksSecretKey());
        paymentGateway.setProfile(paymentGatewayDto.getProfile());
        paymentGateway.setImplementationClassName(paymentGatewayDto.getImplementationClassName());
        if (paymentGatewayDto.isDisabled() != null) {
            paymentGateway.setDisabled(paymentGatewayDto.isDisabled());
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

        paymentGateway.setCode(StringUtils.isBlank(paymentGatewayDto.getUpdatedCode()) ? code : paymentGatewayDto.getUpdatedCode());

        try {
            populateCustomFields(paymentGatewayDto.getCustomFields(), paymentGateway, true, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new BusinessException(e.getMessage());
        }

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
        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.DESCENDING, null, pagingAndFiltering, PaymentMethod.class);
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
}