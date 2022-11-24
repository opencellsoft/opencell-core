package org.meveo.apiv2.billing;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.billing.service.InvoiceLinesApiService;
import org.meveo.model.billing.AdjustmentStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceLinesApiServiceTest {

    @InjectMocks
    private InvoiceLinesApiService invoiceLinesApiService;

    @Mock
    private InvoiceLineService invoiceLineService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final List<Long> ilIds = asList(1L);


    @Before
    public void setUp() {
    	
    }

    @Test
    public void shouldThrowBusinessExceptionIfStatusNotIgnoreToMark() {
    	
    	//InvoiceType
    	InvoiceType it1 = new InvoiceType();
    	it1.setCode("DRAFT");//SECURITY_DEPOSIT
    	
    	//InvoiceLine
    	InvoiceLine il1 = new InvoiceLine();
    	
    	//securityDepositInvoice
    	Invoice i1 = new Invoice();
    	i1.setId(1L);
    	i1.setInvoiceType(it1);
    	
    	List<InvoiceLine> invoiceLines = new ArrayList<>();
    	
    	i1.setInvoiceLines(invoiceLines);
    
    	when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.NOT_ADJUSTED))).thenReturn(invoiceLines);
    	
        expectedException.expect(BusinessException.class);
        expectedException.expectMessage("Only NOT_ADJUSTED invoice lines can be marked TO_ADJUST");

        invoiceLinesApiService.markInvoiceLinesForAdjustment(false, ilIds);
    }
    
    @Test
    public void shouldThrowBusinessExceptionIfStatusNotIgnoreToUnMark() {
    	
    	//InvoiceType
    	InvoiceType it1 = new InvoiceType();
    	it1.setCode("DRAFT");//SECURITY_DEPOSIT
    	
    	//InvoiceLine
    	InvoiceLine il1 = new InvoiceLine();
    	
    	//securityDepositInvoice
    	Invoice i1 = new Invoice();
    	i1.setId(1L);
    	i1.setInvoiceType(it1);
    	
    	List<InvoiceLine> invoiceLines = new ArrayList<>();
    	
    	i1.setInvoiceLines(invoiceLines);
    
    	when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.NOT_ADJUSTED))).thenReturn(invoiceLines);
    	
        expectedException.expect(BusinessException.class);
        expectedException.expectMessage("Only TO_ADJUST invoice lines can be marked NOT_ADJUSTED");

        invoiceLinesApiService.unmarkInvoiceLinesForAdjustment(false, ilIds);
    }
    
    
    @Test
    public void shouldMarkInvoiceLinesForAdjustment() {
    	
    	//InvoiceType
    	InvoiceType it1 = new InvoiceType();
    	it1.setCode("DRAFT");//SECURITY_DEPOSIT
    	
    	//InvoiceLine
    	InvoiceLine il1 = new InvoiceLine();
    	
    	//securityDepositInvoice
    	Invoice i1 = new Invoice();
    	i1.setId(1L);
    	i1.setInvoiceType(it1);
    	
    	il1.setInvoice(i1);
    	
    	List<InvoiceLine> invoiceLines = new ArrayList<>();
    	invoiceLines.add(il1);
    	
    	i1.setInvoiceLines(invoiceLines);
    
    	when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.NOT_ADJUSTED))).thenReturn(invoiceLines);
    	

        int nbInvoiceLines = invoiceLinesApiService.markInvoiceLinesForAdjustment(false, ilIds);
        assertEquals(1, nbInvoiceLines);
    }
    
    @Test
    public void shouldUnMarkInvoiceLinesForAdjustment() {
    	
    	//InvoiceType
    	InvoiceType it1 = new InvoiceType();
    	it1.setCode("DRAFT");//SECURITY_DEPOSIT
    	
    	//InvoiceLine
    	InvoiceLine il1 = new InvoiceLine();
    	
    	//securityDepositInvoice
    	Invoice i1 = new Invoice();
    	i1.setId(1L);
    	i1.setInvoiceType(it1);
    	
    	il1.setInvoice(i1);
    	
    	List<InvoiceLine> invoiceLines = new ArrayList<>();
    	invoiceLines.add(il1);
    	
    	i1.setInvoiceLines(invoiceLines);
    
    	when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.TO_ADJUST))).thenReturn(invoiceLines);
    	

        int nbInvoiceLines = invoiceLinesApiService.unmarkInvoiceLinesForAdjustment(false, ilIds);
        assertEquals(1, nbInvoiceLines);
    }
    
    
    @Test
    public void shouldThrowBusinessExceptionIfSecurityDeposit3() {
    	
    	//InvoiceType
    	InvoiceType it1 = new InvoiceType();
    	it1.setCode("DRAFT");//SECURITY_DEPOSIT
    	
    	//InvoiceLine
    	InvoiceLine il1 = new InvoiceLine();
    	
    	//securityDepositInvoice
    	Invoice i1 = new Invoice();
    	i1.setId(1L);
    	i1.setInvoiceType(it1);
    	
    	List<InvoiceLine> invoiceLines = new ArrayList<>();
    	invoiceLines.add(il1);
    	
    	i1.setInvoiceLines(invoiceLines);
    
    	when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.NOT_ADJUSTED))).thenReturn(invoiceLines);
    	
        expectedException.expect(BusinessException.class);
        expectedException.expectMessage("Only NOT_ADJUSTED invoice lines can be marked TO_ADJUST");

        invoiceLinesApiService.markInvoiceLinesForAdjustment(false, ilIds);
    }


    
}