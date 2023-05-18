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
import org.meveo.api.dto.catalog.ConvertedPricePlanVersionDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.ConvertedPricePlanVersion;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.catalog.impl.ConvertedPricePlanVersionService;
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
    private ConvertedPricePlanVersionService convertedPricePlanVersionService;

	@Mock
    @ApplicationProvider
    protected Provider appProvider;

	@Test
	public void shouldCreateCPPV() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Mockito.when(pricePlanMatrixVersionService.findById(anyLong())).thenReturn(new PricePlanMatrixVersion());
		
		Mockito.when(convertedPricePlanVersionService.findByPricePlanVersionAndCurrency(any(PricePlanMatrixVersion.class), any(TradingCurrency.class))).thenReturn(null);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("EUR");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		ConvertedPricePlanVersion entity = pricePlanMatrixVersionApi.createConvertedPricePlanVersion(data);
		Assert.assertNotNull(entity);
	}

	@Test(expected = MissingParameterException.class)
	public void faiCreateCPPVMissingPPV() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		pricePlanMatrixVersionApi.createConvertedPricePlanVersion(data);
		Assert.fail();
	}

	@Test(expected = MissingParameterException.class)
	public void faiCreateCPPVMissingTradingCurrency() {

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		pricePlanMatrixVersionApi.createConvertedPricePlanVersion(data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void faiCreateCPPVTradingCurrencyNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(null);
		
		pricePlanMatrixVersionApi.createConvertedPricePlanVersion(data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void faiCreateCPPVPricePlanMatrixVersionNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Mockito.when(pricePlanMatrixVersionService.findById(anyLong())).thenReturn(null);
		
		pricePlanMatrixVersionApi.createConvertedPricePlanVersion(data);
		Assert.fail();
	}

	@Test(expected = BusinessException.class)
	public void faiCreateCPPVSameCurrency() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("USD");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		pricePlanMatrixVersionApi.createConvertedPricePlanVersion(data);
		Assert.fail();
	}

	@Test
	public void shouldUpdateCPPV() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		PricePlanMatrixVersion ePPV = new PricePlanMatrixVersion();
		ConvertedPricePlanVersion eCPPV = new ConvertedPricePlanVersion();
		eCPPV.setId(1L);
		eCPPV.setTradingCurrency(eTradingCurrency);
		ePPV.setConvertedPricePlanVersions(new HashSet<>());
		ePPV.getConvertedPricePlanVersions().add(eCPPV);
		
		Mockito.when(pricePlanMatrixVersionService.findById(anyLong())).thenReturn(ePPV);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("EUR");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		ConvertedPricePlanVersion entity = pricePlanMatrixVersionApi.updateConvertedPricePlanVersion(1L, data);
		Assert.assertNotNull(entity);
	}

	@Test(expected = MissingParameterException.class)
	public void faiUpdateCPPVMissingPPV() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		pricePlanMatrixVersionApi.updateConvertedPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test(expected = MissingParameterException.class)
	public void faiUpdateCPPVMissingTradingCurrency() {

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		pricePlanMatrixVersionApi.updateConvertedPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void faiUpdateCPPVTradingCurrencyNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(null);
		
		pricePlanMatrixVersionApi.updateConvertedPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void faiUpdateCPPVPricePlanMatrixVersionNotFound() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Mockito.when(pricePlanMatrixVersionService.findById(anyLong())).thenReturn(null);
		
		pricePlanMatrixVersionApi.updateConvertedPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test(expected = InvalidParameterException.class)
	public void faiUpdateCPPVSameCurrency() {

		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode("USD");

		ConvertedPricePlanVersionDto data = new ConvertedPricePlanVersionDto();
		data.setPricePlanMatrixVersionId(1L);
		data.setTradingCurrency(currencyDto);
		data.setRate(BigDecimal.valueOf(10));
		data.setConvertedPrice(BigDecimal.valueOf(100));
		data.setUseForBillingAccounts(false);
		
		TradingCurrency eTradingCurrency = new TradingCurrency();
		Currency eCurrency = new Currency();
		eCurrency.setCurrencyCode("USD");
		eTradingCurrency.setCurrency(eCurrency);
		
		Mockito.when(tradingCurrencyService.findByTradingCurrencyCodeOrId(anyString(), nullable(Long.class))).thenReturn(eTradingCurrency);
		
		Currency eFunctionalCurrency = new Currency();
		eFunctionalCurrency.setCurrencyCode("USD");
		Mockito.when(appProvider.getCurrency()).thenReturn(eFunctionalCurrency);
		
		pricePlanMatrixVersionApi.updateConvertedPricePlanVersion(1L, data);
		Assert.fail();
	}

	@Test
	public void shouldDeleteCPPV() {

		ConvertedPricePlanVersion eCPPV = new ConvertedPricePlanVersion();
		PricePlanMatrixVersion ePPV = new PricePlanMatrixVersion();
		ePPV.setStatus(VersionStatusEnum.DRAFT);
		eCPPV.setPricePlanMatrixVersion(ePPV);
		Mockito.when(convertedPricePlanVersionService.findById(anyLong())).thenReturn(eCPPV);

		pricePlanMatrixVersionApi.deleteConvertedPricePlanVersion(1L);
		Assert.assertTrue("All good", true );
	}

	@Test(expected = BusinessException.class)
	public void failDeleteCPPVInvalidStatus() {

		ConvertedPricePlanVersion eCPPV = new ConvertedPricePlanVersion();
		PricePlanMatrixVersion ePPV = new PricePlanMatrixVersion();
		ePPV.setStatus(VersionStatusEnum.PUBLISHED);
		eCPPV.setPricePlanMatrixVersion(ePPV);
		Mockito.when(convertedPricePlanVersionService.findById(anyLong())).thenReturn(eCPPV);

		pricePlanMatrixVersionApi.deleteConvertedPricePlanVersion(1L);
		Assert.fail();
	}
}
