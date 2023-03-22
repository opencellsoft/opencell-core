package org.meveo.apiv2.billing;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.service.billing.impl.InvoiceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceApiServiceTest {

    @InjectMocks
    private InvoiceApiService invoiceApiService;

    @Mock
    private InvoiceService invoiceService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final List<String> fieldToFetch = asList("tradingCurrency");

    private BigDecimal netToPay = new BigDecimal(11);
    private BigDecimal rate = new BigDecimal(0.96);
    private Date rateDate = new Date();
    private Long invoiceId = 1L;

    @Before
    public void setUp() {
        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setId(2L);
        tradingCurrency.setCurrencyCode("EUR");
        tradingCurrency.setCurrentRate(rate);
        tradingCurrency.setCurrentRateFromDate(rateDate);
        Invoice inv = new Invoice();
        inv.setId(1L);
        inv.setInvoiceNumber("INV_TEST_01");
        inv.setAmountTax(BigDecimal.ONE);
        inv.setAmountWithoutTax(BigDecimal.TEN);
        inv.setAmountWithTax(netToPay);
        inv.setNetToPay(netToPay);
        inv.setAmountWithoutTaxBeforeDiscount(netToPay);
        inv.setTradingCurrency(tradingCurrency);
        inv.setTransactionalAmountWithTax(inv.getAmountWithTax().divide(rate, 12, HALF_UP));
        inv.setNetToPay(inv.getNetToPay().divide(rate, 12, HALF_UP));
        when(invoiceService.refreshConvertedAmounts(any(), any(), any())).thenReturn(inv);
    }

    @Test
    public void shouldRefreshInvoiceAmounts() {
        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setId(2L);
        tradingCurrency.setCurrencyCode("EUR");
        tradingCurrency.setCurrentRate(rate);
        tradingCurrency.setCurrentRateFromDate(rateDate);
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("INV_TEST_01");
        invoice.setAmountTax(BigDecimal.ONE);
        invoice.setAmountWithoutTax(BigDecimal.TEN);
        invoice.setAmountWithTax(netToPay);
        invoice.setNetToPay(netToPay);
        invoice.setAmountWithoutTaxBeforeDiscount(netToPay);
        invoice.setTradingCurrency(tradingCurrency);

        when(invoiceService.findById(invoiceId, fieldToFetch)).thenReturn(invoice);

        Invoice result = invoiceApiService.refreshRate(invoiceId).get();

        BigDecimal convertedNetToPay = netToPay.divide(rate, 12, HALF_UP);
        assertEquals(convertedNetToPay, result.getTransactionalAmountWithTax());
    }

    @Test
    public void shouldNotRefreshInvoiceAmountsIfRateAreEquals() {
        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setId(2L);
        tradingCurrency.setCurrencyCode("EUR");
        tradingCurrency.setCurrentRate(rate);
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("INV_TEST_01");
        invoice.setTradingCurrency(tradingCurrency);
        invoice.setLastAppliedRate(rate);

        when(invoiceService.findById(invoiceId, fieldToFetch)).thenReturn(invoice);

        Optional<Invoice> result = invoiceApiService.refreshRate(invoiceId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldThrowNotFoundExceptionIfInvoiceNotFound() {
        when(invoiceService.findById(invoiceId, fieldToFetch)).thenReturn(null);

        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Invoice not found");

        invoiceApiService.refreshRate(invoiceId);
    }

    @Test
    public void shouldThrowForbiddenExceptionIfStatusNotAllowed() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("INV_TEST_01");
        invoice.setStatus(VALIDATED);

        when(invoiceService.findById(invoiceId, fieldToFetch)).thenReturn(invoice);

        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Refresh rate only allowed for invoices with status : NEW or DRAFT");

        invoiceApiService.refreshRate(invoiceId);
    }
}