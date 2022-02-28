package org.meveo.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.model.admin.Currency;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.crm.impl.ProviderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyApiTest {

    @InjectMocks
    private CurrencyApi currencyApi;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private Provider provider;

    @Mock
    private ProviderService providerService;



    @Before
    public void setUp() {

        when(currencyService.findByCode("MAD")).thenReturn(new Currency());

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
        when(providerService.findById(any())).thenReturn(new Provider());

            CurrencyDto currencyDto = new CurrencyDto();
            currencyDto.setCode("MAD");
             ActionStatus actionStatus = currencyApi.addFunctionalCurrency(currencyDto);

             verify(providerService).update(any());
            assertEquals(ActionStatusEnum.SUCCESS, actionStatus.getStatus());


    }


}
