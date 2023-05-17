package org.meveo.api.catalog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;

import java.math.BigDecimal;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.catalog.TradingPricePlanVersionDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.TradingPricePlanVersion;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.catalog.impl.TradingPricePlanVersionService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.util.ApplicationProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PricePlanMatrixVersionApiTest {

	@InjectMocks
	private PricePlanMatrixVersionApi pricePlanMatrixVersionApi;

	@Mock
	private TradingCurrencyService tradingCurrencyService;

	@Mock
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

	@Mock
    private TradingPricePlanVersionService tradingPricePlanVersionService;

	@Mock
    @ApplicationProvider
    protected Provider appProvider;

	@Test
	public void shouldCreateTPPV() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Mockito.when(pricePlanMatrixVersionService.findById(anyLong())).thenReturn(new PricePlanMatrixVersion());
		
		Mockito.when(tradingPricePlanVersionService.findByPricePlanVersionAndCurrency(any(PricePlanMatrixVersion.class), any(TradingCurrency.class))).thenReturn(null);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("EUR");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		TradingPricePlanVersion entity = pricePlanMatrixVersionApi.createTradingPricePlanVersion(data);
		Assert.assertNotNull(entity);
	}

	@Test(expected = MissingParameterException.class)
	public void failCreateTPPVMissingPPV() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		pricePlanMatrixVersionApi.createTradingPricePlanVersion(data);
		Assert.fail();
	}

	@Test(expected = MissingParameterException.class)
	public void failCreateTPPVMissingTradingCurrency() {

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		pricePlanMatrixVersionApi.createTradingPricePlanVersion(data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void failCreateTPPVTradingCurrencyNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(null);
		
		pricePlanMatrixVersionApi.createTradingPricePlanVersion(data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void failCreateTPPVPricePlanMatrixVersionNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Mockito.when(pricePlanMatrixVersionService.findById(anyLong())).thenReturn(null);
		
		pricePlanMatrixVersionApi.createTradingPricePlanVersion(data);
		Assert.fail();
	}

	@Test(expected = BusinessException.class)
	public void failCreateTPPVSameCurrency() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("USD");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		pricePlanMatrixVersionApi.createTradingPricePlanVersion(data);
		Assert.fail();
	}

	@Test
	public void shouldUpdateTPPV() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		PricePlanMatrixVersion ePPV = new PricePlanMatrixVersion();
		TradingPricePlanVersion eTPPV = new TradingPricePlanVersion();
		eTPPV.setId(1L);
		eTPPV.setTradingCurrency(eTradingCurrency);
		ePPV.setTradingPricePlanMatrixLines(new HashSet<>());
		ePPV.getTradingPricePlanMatrixLines().add(eTPPV);
		
		Mockito.when(pricePlanMatrixVersionService.findById(anyLong())).thenReturn(ePPV);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("EUR");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		TradingPricePlanVersion entity = pricePlanMatrixVersionApi.updateTradingPricePlanVersion(1L, data);
		Assert.assertNotNull(entity);
	}

	@Test(expected = MissingParameterException.class)
	public void failUpdateTPPVMissingPPV() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		pricePlanMatrixVersionApi.updateTradingPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test(expected = MissingParameterException.class)
	public void failUpdateTPPVMissingTradingCurrency() {

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		pricePlanMatrixVersionApi.updateTradingPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void failUpdateTPPVTradingCurrencyNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(null);
		
		pricePlanMatrixVersionApi.updateTradingPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void failUpdateTPPVPricePlanMatrixVersionNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Mockito.when(pricePlanMatrixVersionService.findById(anyLong())).thenReturn(null);
		
		pricePlanMatrixVersionApi.updateTradingPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test(expected = InvalidParameterException.class)
	public void failUpdateTPPVSameCurrency() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		TradingPricePlanVersionDto data = new TradingPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setTradingPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("USD");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		pricePlanMatrixVersionApi.updateTradingPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test
	public void shouldDeleteTPPV() {

		TradingPricePlanVersion eTPPV = new TradingPricePlanVersion();
		PricePlanMatrixVersion ePPV = new PricePlanMatrixVersion();
		ePPV.setStatus(VersionStatusEnum.DRAFT);
		eTPPV.setPricePlanMatrixVersion(ePPV);
		Mockito.when(tradingPricePlanVersionService.findById(anyLong())).thenReturn(eTPPV);

		pricePlanMatrixVersionApi.deleteTradingPricePlanVersion(1L);
		Assert.assertTrue("All good", true );
	}

	@Test(expected = BusinessException.class)
	public void failDeleteTPPVInvalidStatus() {

		TradingPricePlanVersion eTPPV = new TradingPricePlanVersion();
		PricePlanMatrixVersion ePPV = new PricePlanMatrixVersion();
		ePPV.setStatus(VersionStatusEnum.PUBLISHED);
		eTPPV.setPricePlanMatrixVersion(ePPV);
		Mockito.when(tradingPricePlanVersionService.findById(anyLong())).thenReturn(eTPPV);

		pricePlanMatrixVersionApi.deleteTradingPricePlanVersion(1L);
		Assert.fail();
	}
}
