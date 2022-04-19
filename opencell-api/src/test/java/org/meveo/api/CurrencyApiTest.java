package org.meveo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.crm.impl.*;
import org.meveo.util.*;
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
    private ProviderService providerService;

    @Mock
    @ApplicationProvider
    private Provider appProvider;


    @Before
    public void setUp() {
        when(currencyService.findByCode("MAD")).thenReturn(new Currency());
        when(appProvider.getId()).thenReturn(1L);
    }

    @Test
    public void addFunctionalCurrency_withNoCurrencyCode() {
        try {
             currencyApi.addFunctionalCurrency(new CurrencyDto());
        } catch (Exception exception)
        {
            assertTrue(exception instanceof MissingParameterException);
        }

    }

    @Test
    public void addFunctionalCurrency_withCurrencyCodeNotFound() {
        try {
            CurrencyDto currencyDto = new CurrencyDto();
            currencyDto.setCode("ABCD");
             currencyApi.addFunctionalCurrency(currencyDto);
        } catch (Exception exception)
        {
            assertTrue(exception instanceof NotFoundException);
        }

    }

    @Test
    public void addFunctionalCurrency() {
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setCode("MAD");
        when(providerService.findById(1L)).thenReturn(new Provider());
        ActionStatus actionStatus = currencyApi.addFunctionalCurrency(currencyDto);

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
            CurrencyDto currencyDto = new CurrencyDto();
            currencyDto.setCode("MAD");
            currencyDto.setSymbol(null);
            currencyApi.update(currencyDto);

            verify(tradingCurrencyService).update(any());
            verify(currencyService).update(any());
    }
}