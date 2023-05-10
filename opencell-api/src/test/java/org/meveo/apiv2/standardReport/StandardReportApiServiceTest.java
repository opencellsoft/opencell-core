package org.meveo.apiv2.standardReport;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.standardReport.service.StandardReportApiService;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StandardReportApiServiceTest {

    @InjectMocks
    private StandardReportApiService standardReportApiService;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private RecordedInvoiceService recordedInvoiceService;

    @Mock
    private GenericPagingAndFilteringUtils genericPagingAndFilteringUtils;

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

        assertEquals(1, result.size());
        Object[] agedReceivableResult = result.get(0);
        assertEquals("EUR", agedReceivableResult[12]);
        assertEquals(DunningLevelEnum.R1, agedReceivableResult[8]);
    }

    @Test
    public void shouldReturnListOfAgedReceivablesValidatedInvoices() {
    	
    	List<Object[]> result = new ArrayList<>();
        Object[] agedReceivable = new Object[] {"CA_CODE", ONE,
                new BigDecimal(100), new BigDecimal(80), new BigDecimal(20),
                ZERO, ZERO, ZERO,
                DunningLevelEnum.R1, new Name(new Title(), "TEST", "TEST"),
                "CA_DESCRIPTION", "SELLER_DESCRIPTION", "SELLER_CODE", new Date(), "EUR"};
        result.add(agedReceivable);
    	
    	when(recordedInvoiceService.getAgedReceivables(any(String.class), any(String.class), any(), isNull(), isNull(), any(PaginationConfiguration.class), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull())).thenReturn(result);
    	
    	List<Object[]> testResult = standardReportApiService.list(0L, 50L, null, null, "CA_CODE", startDate, null, null, null, null, "SELLER_CODE", null, null, null, null, null);
    	
    	assertEquals(1, testResult.size());
    	Object[] anElement = testResult.get(0);
    	
    	assertEquals("CA_CODE", (String) anElement[0]);
    	assertEquals(new BigDecimal(100), (BigDecimal) anElement[2]);
    	assertEquals("EUR", anElement[14]);
        assertEquals(DunningLevelEnum.R1, anElement[8]);
    }
    
    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionIfStepInDaysIsMissing() {
        standardReportApiService.list(0l, 5l, null, null, "CA_CODE", startDate, null,null,
                null, null,null,"INV_10000", null, 2, null, null);
        expectedException.expectMessage("StepInDays parameter is mandatory when numberOfPeriods is provided");
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionIfNumberOfPeriodsIsMissing() {
        standardReportApiService.list(0l, 5l, null, null, "CA_CODE", startDate, null,null,
                null, null,null,"INV_10000", null, 2, null, null);
        expectedException.expectMessage("numberOfPeriods parameter is mandatory when stepInDays is provided");
    }
}
