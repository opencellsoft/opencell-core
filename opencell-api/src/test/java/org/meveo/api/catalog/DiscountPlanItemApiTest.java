package org.meveo.api.catalog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.catalog.TradingDiscountPlanItemDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.TradingDiscountPlanItem;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.TradingDiscountPlanItemService;
import org.meveo.util.ApplicationProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DiscountPlanItemApiTest {

	@InjectMocks
	private DiscountPlanItemApi discountPlanItemApi;

	@Mock
	private TradingCurrencyService tradingCurrencyService;

	@Mock
    private DiscountPlanItemService discountPlanItemService;

	@Mock
    private TradingDiscountPlanItemService tradingDiscountPlanItemService;

	@Mock
    @ApplicationProvider
    protected Provider appProvider;

	@Test
	public void shouldCreateTDPI() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingDiscountPlanItemDto data = new TradingDiscountPlanItemDto();
		data.setDiscountPlanItemId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingDiscountValue(BigDecimal.valueOf(100));
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		DiscountPlanItem eDPI = new DiscountPlanItem();
		DiscountPlan eDP = new DiscountPlan();
		eDP.setStatus(DiscountPlanStatusEnum.DRAFT);
		eDPI.setDiscountPlan(eDP);
		Mockito.when(discountPlanItemService.findById(anyLong())).thenReturn(eDPI);
		
		Mockito.when(tradingDiscountPlanItemService.findByDiscountPlanItemAndCurrency(any(DiscountPlanItem.class), any(TradingCurrency.class))).thenReturn(null);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("EUR");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		TradingDiscountPlanItem entity = discountPlanItemApi.createTradingDiscountPlanItem(data);
		Assert.assertNotNull(entity);
	}

	@Test(expected = MissingParameterException.class)
	public void failCreateTDPIMissingDPI() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingDiscountPlanItemDto data = new TradingDiscountPlanItemDto();
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingDiscountValue(BigDecimal.valueOf(100));
		
		discountPlanItemApi.createTradingDiscountPlanItem(data);
		Assert.fail();
	}

	@Test(expected = MissingParameterException.class)
	public void failCreateTDPIMissingTradingCurrency() {

		TradingDiscountPlanItemDto data = new TradingDiscountPlanItemDto();
		data.setDiscountPlanItemId(1L);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingDiscountValue(BigDecimal.valueOf(100));
		
		discountPlanItemApi.createTradingDiscountPlanItem(data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void failCreateTDPITradingCurrencyNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("UDS");

		TradingDiscountPlanItemDto data = new TradingDiscountPlanItemDto();
		data.setDiscountPlanItemId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingDiscountValue(BigDecimal.valueOf(100));
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(null);
		
		discountPlanItemApi.createTradingDiscountPlanItem(data);
		Assert.fail();
	}

	@Test(expected = InvalidParameterException.class)
	public void failCreateTDPISameCurrencyFunctional() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingDiscountPlanItemDto data = new TradingDiscountPlanItemDto();
		data.setDiscountPlanItemId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingDiscountValue(BigDecimal.valueOf(100));
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("USD");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		discountPlanItemApi.createTradingDiscountPlanItem(data);
		Assert.fail();
	}

	@Test
	public void shouldUpdateTDPI() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingDiscountPlanItemDto data = new TradingDiscountPlanItemDto();
		data.setDiscountPlanItemId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingDiscountValue(BigDecimal.valueOf(100));
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		TradingDiscountPlanItem eTDPI = new TradingDiscountPlanItem();
		DiscountPlanItem eDPI = new DiscountPlanItem();
		DiscountPlan eDP = new DiscountPlan();
		eDP.setStatus(DiscountPlanStatusEnum.DRAFT);
		eDPI.setDiscountPlan(eDP);
		eTDPI.setDiscountPlanItem(eDPI);
		eTDPI.setTradingDiscountValue(BigDecimal.valueOf(200));
		Mockito.when(discountPlanItemService.findById(anyLong())).thenReturn(eDPI);
		Mockito.when(tradingDiscountPlanItemService.findById(anyLong())).thenReturn(eTDPI);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("EUR");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		TradingDiscountPlanItem entity = discountPlanItemApi.updateTradingDiscountPlanItem(1L, data);
		Assert.assertNotNull(entity);
	}


	@Test(expected = MissingParameterException.class)
	public void failUpdateTDPIMissingIdTDPI() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingDiscountPlanItemDto data = new TradingDiscountPlanItemDto();
		data.setDiscountPlanItemId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingDiscountValue(BigDecimal.valueOf(100));
		
		discountPlanItemApi.updateTradingDiscountPlanItem(null, data);
		Assert.fail();
	}

	@Test
	public void shouldDeleteTDPI() {

		TradingDiscountPlanItem eTDPI = new TradingDiscountPlanItem();
		DiscountPlanItem eDPI = new DiscountPlanItem();
		DiscountPlan eDP = new DiscountPlan();
		eDP.setStatus(DiscountPlanStatusEnum.DRAFT);
		eDPI.setDiscountPlan(eDP);
		eTDPI.setDiscountPlanItem(eDPI);
		Mockito.when(tradingDiscountPlanItemService.findById(anyLong())).thenReturn(eTDPI);

		discountPlanItemApi.deleteTradingDiscountPlanItem(1L);
		Assert.assertTrue("All good", true );
	}

	@Test(expected = InvalidParameterException.class)
	public void failDeleteTDPIInvalidStatus() {

		TradingDiscountPlanItem eTDPI = new TradingDiscountPlanItem();
		DiscountPlanItem eDPI = new DiscountPlanItem();
		DiscountPlan eDP = new DiscountPlan();
		eDP.setStatus(DiscountPlanStatusEnum.IN_USE);
		eDPI.setDiscountPlan(eDP);
		eTDPI.setDiscountPlanItem(eDPI);
		Mockito.when(tradingDiscountPlanItemService.findById(anyLong())).thenReturn(eTDPI);

		discountPlanItemApi.deleteTradingDiscountPlanItem(1L);
		Assert.fail();
	}
}
