package org.meveo.apiv2.article.impl;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.article.*;
import org.meveo.apiv2.article.service.AccountingArticleApiService;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.service.accountingscheme.AccountingCodeMappingService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.*;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.ws.rs.NotFoundException;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountingArticleApiServiceTest {

    @InjectMocks
    private AccountingArticleApiService accountingArticleApiService;

    @Mock
    private AccountingCodeMappingService accountingCodeMappingService;

    @Mock
    private AccountingArticleService accountingArticleService;

    @Mock
    private TradingCountryService tradingCountryService;

    @Mock
    private TradingCurrencyService tradingCurrencyService;

    @Mock
    private SellerService sellerService;

    @Mock
    private AccountingCodeService accountingCodeService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AccountingCodeMappingInput accountingCodeMapping;

    @Before
    public void setUp() {
        AccountingArticle accountingArticle = new AccountingArticle();
        accountingArticle.setId(1L);
        accountingArticle.setCode("DISC-STD");
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setCode("MAIN_SELLER");
        TradingCountry country = new TradingCountry();
        country.setCode("FR");
        country.setId(1L);
        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setId(1L);
        tradingCurrency.setCurrencyCode("EUR");

        AccountingCodeMapping accountingCodeMappingItem = ImmutableAccountingCodeMapping.builder()
                .accountingArticleCode("DISC-STD")
                .sellerCode("MAIN_SELLER")
                .sellerCountryCode("FR")
                .billingCountryCode("FR")
                .billingCurrencyCode("EUR")
                .accountingCode("ACC_CODE")
                .criteriaElValue("EL value")
                .build();
        accountingCodeMapping = ImmutableAccountingCodeMappingInput
                .builder()
                .accountingArticleCode("DISC-STD")
                .accountingCodeMappings(Arrays.asList(accountingCodeMappingItem))
                .build();

        when(sellerService.findByCode("MAIN_SELLER")).thenReturn(seller);
        when(accountingArticleService.findByCode("DISC-STD")).thenReturn(accountingArticle);
        when(tradingCountryService.findByCode("FR")).thenReturn(country);
        when(tradingCurrencyService.findByTradingCurrencyCode("EUR")).thenReturn(tradingCurrency);
        when(accountingCodeService.findByCode(anyString())).thenReturn(new AccountingCode());
    }

    @Test
    public void shouldCreateAccountingCodeMapping() {
        List<org.meveo.model.accountingScheme.AccountingCodeMapping> entities
                = accountingArticleApiService.createAccountingCodeMappings(accountingCodeMapping);
        assertTrue(nonNull(entities));
        assertTrue(!entities.isEmpty());
        assertEquals("DISC-STD", entities.get(0).getAccountingArticle().getCode());
    }

    @Test
    public void shouldFailsIfAccountingArticleNotFound() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Accounting article with code DISC-STD does not exits");

        when(accountingArticleService.findByCode("DISC-STD")).thenReturn(null);
        accountingArticleApiService.createAccountingCodeMappings(accountingCodeMapping);
    }

    @Test
    public void shouldFailsIfSellerNotFound() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Seller with code MAIN_SELLER does not exits");

        when(sellerService.findByCode("MAIN_SELLER")).thenReturn(null);
        accountingArticleApiService.createAccountingCodeMappings(accountingCodeMapping);
    }

    @Test
    public void shouldFailsIfSellerCountryNotFound() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Seller country with code FR does not exits");

        AccountingCodeMapping accountingCodeMapping = ImmutableAccountingCodeMapping.builder()
                .sellerCode("MAIN_SELLER")
                .sellerCountryCode("FR")
                .billingCountryCode("ENG")
                .billingCurrencyCode("EUR")
                .accountingCode("ACC_CODE")
                .criteriaElValue("EL value")
                .build();
        AccountingCodeMappingInput accountingCodeMappingInput = ImmutableAccountingCodeMappingInput
                .builder()
                .accountingArticleCode("DISC-STD")
                .accountingCodeMappings(Arrays.asList(accountingCodeMapping))
                .build();
        when(tradingCountryService.findByCode("FR")).thenReturn(null);
        when(tradingCountryService.findByCode("ENG")).thenReturn(new TradingCountry());

        accountingArticleApiService.createAccountingCodeMappings(accountingCodeMappingInput);
    }

    @Test
    public void shouldFailsIfBillingCountryNotFound() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Billing country with code ENG does not exits");

        AccountingCodeMapping accountingCodeMapping = ImmutableAccountingCodeMapping.builder()
                .sellerCode("MAIN_SELLER")
                .sellerCountryCode("FR")
                .billingCountryCode("ENG")
                .billingCurrencyCode("EUR")
                .accountingCode("ACC_CODE")
                .criteriaElValue("EL value")
                .build();
        AccountingCodeMappingInput accountingCodeMappingInput = ImmutableAccountingCodeMappingInput
                .builder()
                .accountingArticleCode("DISC-STD")
                .accountingCodeMappings(Arrays.asList(accountingCodeMapping))
                .build();
        when(tradingCountryService.findByCode("ENG")).thenReturn(null);

        accountingArticleApiService.createAccountingCodeMappings(accountingCodeMappingInput);
    }

    @Test
    public void shouldFailsIfBillingCurrencyNotFound() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Trading currency with code EUR does not exits");

        when(tradingCurrencyService.findByTradingCurrencyCode("EUR")).thenReturn(null);
        accountingArticleApiService.createAccountingCodeMappings(accountingCodeMapping);
    }

    @Test(expected = BusinessException.class)
    public void shouldThrowBusinessExceptionIfAnErrorOccurred() {
        doThrow(new BusinessException()).when(accountingCodeMappingService).create(any());
        accountingArticleApiService.createAccountingCodeMappings(accountingCodeMapping);
    }
}