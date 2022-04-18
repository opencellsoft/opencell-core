package org.meveo.apiv2.standardReport;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.mockito.Mockito.when;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.RunWith;
import org.meveo.api.rest.exception.NotAuthorizedException;
import org.meveo.apiv2.standardReport.service.StandardReportApiService;
import org.meveo.model.billing.*;
import org.meveo.model.payments.*;
import org.meveo.model.shared.*;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;
import java.math.*;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class StandardReportApiServiceTest {

    @InjectMocks
    private StandardReportApiService standardReportApiService;

    @Mock
    private CustomerAccountService customerAccountService;

    @Mock
    private InvoiceService invoiceService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Date startDate = new Date();

    private CustomerAccount customerAccount;

    private Invoice invoice;

    @Before
    public void setUp() {
        customerAccount = new CustomerAccount();
        customerAccount.setCode("CA_CODE");
        invoice = new Invoice();
        invoice.setInvoiceNumber("INV_10000");
        when(customerAccountService.findByCode("CA_CODE")).thenReturn(customerAccount);
        when(invoiceService.findByInvoiceNumber("INV_10000")).thenReturn(invoice);
    }

    @Test
    public void shouldReturnListOfAgedReceivables() {
        List<Object[]> result = new ArrayList<>();
        Object[] agedReceivable = new Object[] {"CA_CODE", ONE,
                new BigDecimal(100), new BigDecimal(80), new BigDecimal(20),
                ZERO, ZERO, ZERO,
                DunningLevelEnum.R1, new Name(new Title(), "TEST", "TEST"),
                "CA_DESCRIPTION", new Date(), "EUR"};
        result.add(agedReceivable);

        Assert.assertEquals(1, result.size());
        Object[] agedReceivableResult = result.get(0);
        Assert.assertEquals("EUR", agedReceivableResult[12]);
        Assert.assertEquals(DunningLevelEnum.R1, agedReceivableResult[8]);
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldThrowExceptionIfInvoiceNumberNotFound() {
        when(invoiceService.findByInvoiceNumber("INV_10000")).thenReturn(null);

        standardReportApiService.list(0l, 5l, null, null, "CA_CODE", startDate,
                null, "INV_10000", 10, 2);
        expectedException.expectMessage("Invoice number : INV_10000 does not exits");
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionIfCustomerAccountNotFound() {
        when(customerAccountService.findByCode("CA_CODE")).thenReturn(null);

        standardReportApiService.list(0l, 5l, null, null, "CA_CODE", startDate,
                null, "INV_10000", 10, 2);
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldThrowExceptionIfStepInDaysIsMissing() {
        standardReportApiService.list(0l, 5l, null, null, "CA_CODE", startDate,
                null, "INV_10000", null, 2);
        expectedException.expectMessage("StepInDays parameter is mandatory when numberOfPeriods is provided");
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldThrowExceptionIfNumberOfPeriodsIsMissing() {
        standardReportApiService.list(0l, 5l, null, null, "CA_CODE", startDate,
                null, "INV_10000", null, 2);
        expectedException.expectMessage("numberOfPeriods parameter is mandatory when stepInDays is provided");
    }
}