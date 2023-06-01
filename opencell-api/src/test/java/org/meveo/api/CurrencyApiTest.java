package org.meveo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.billing.ExchangeRateDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.ExchangeRateService;
import org.meveo.service.crm.impl.ProviderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyApiTest {

    @InjectMocks
    private CurrencyApi currencyApi;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private TradingCurrencyService tradingCurrencyService;

    @Mock
    private Provider provider;

    @Mock
    private ProviderService providerService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private AuditLogService auditLogService;

    @Before
    public void setUp() {

        when(currencyService.findByCode("MAD")).thenReturn(new Currency());

    }

    @Test
    public void addFunctionalCurrency_withNoCurrencyCode() {
        try {
            currencyApi.addFunctionalCurrency(new CurrencyDto());
        } catch (Exception exception) {
            assertTrue(exception instanceof MissingParameterException);
        }

    }

    @Test
    public void addFunctionalCurrency_withCurrencyCodeNotFound() {
        try {
            CurrencyDto currencyDto = new CurrencyDto();
            currencyDto.setCode("ABCD");
            currencyApi.addFunctionalCurrency(currencyDto);
        } catch (Exception exception) {
            assertTrue(exception instanceof NotFoundException);
        }

    }

    @Test
    public void addFunctionalCurrency() {
        when(providerService.getProviderNoCache()).thenReturn(new Provider());
        when(tradingCurrencyService.findByTradingCurrencyCode(any())).thenReturn(new TradingCurrency());

        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setCode("MAD");
        ActionStatus actionStatus = currencyApi.addFunctionalCurrency(currencyDto);

        verify(providerService).update(any());
        assertEquals(ActionStatusEnum.SUCCESS, actionStatus.getStatus());
    }

    @Test
    public void createCurrency() {
        when(tradingCurrencyService.findByTradingCurrencyCode(any())).thenReturn(null);
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setCode("MAD");
        currencyDto.setSymbol(null);
        currencyApi.create(currencyDto);

        verify(tradingCurrencyService).create(any());
    }

    @Test
    public void updateCurrency() {
        when(tradingCurrencyService.findByTradingCurrencyCode(any())).thenReturn(new TradingCurrency());
        when(currencyService.findByCode(any())).thenReturn(new Currency());
        when(currencyService.update(any())).thenReturn(new Currency());
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setCode("MAD");
        currencyDto.setSymbol(null);
        currencyApi.update(currencyDto);

        verify(tradingCurrencyService).update(any());
        verify(currencyService).update(any());
    }

    @Test
    public void updateExchangeRate() {
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setCurrency(currency);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFromDate(DateUtils.addMonthsToDate(new Date(), 1));
        exchangeRate.setExchangeRate(BigDecimal.ONE);
        exchangeRate.setTradingCurrency(tradingCurrency);
        when(exchangeRateService.findById(any())).thenReturn(exchangeRate);
        when(auditLogService.getActor()).thenReturn("XXX");
        ExchangeRateDto postData = new ExchangeRateDto();
        postData.setExchangeRate(BigDecimal.valueOf(12.34));
        postData.setFromDate(new Date());
        currencyApi.updateExchangeRate(1L, postData);

        verify(exchangeRateService).update(any());
    }
}
