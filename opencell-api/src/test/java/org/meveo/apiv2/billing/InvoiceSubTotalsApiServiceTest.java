package org.meveo.apiv2.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.invoice.InvoiceSubTotalsDto;
import org.meveo.api.dto.invoice.SubTotalsDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.billing.service.InvoiceSubTotalsApiService;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceSubTotals;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceSubTotalsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class InvoiceSubTotalsApiServiceTest {

	@InjectMocks
	private InvoiceSubTotalsApiService invoiceSubTotalsApiService;
	@Mock
	private InvoiceSubTotalsService invoiceSubTotalsService;
	@Mock
	private InvoiceLineService invoiceLineService;
	
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    
    
    
    public Invoice invoice = null;
    public List<org.meveo.model.billing.InvoiceSubTotals> subTotals = new ArrayList<>();
    
    public List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
    
    private void initInvoiceSubTotals() {
    	org.meveo.model.billing.InvoiceSubTotals i1 = new org.meveo.model.billing.InvoiceSubTotals();
    	i1.setId(1L);
    	i1.setLabel("test_1");
    	i1.setSubTotalEl("#{invoiceLine.getQuantity().intValue() == 10}");
    	org.meveo.model.billing.InvoiceSubTotals i2 = new org.meveo.model.billing.InvoiceSubTotals();
    	i2.setId(2L);
    	i2.setLabel("test_2");
    	i1.setSubTotalEl("#{invoiceLine.getQuantity().intValue() > 10}");
    	org.meveo.model.billing.InvoiceSubTotals i3 = new org.meveo.model.billing.InvoiceSubTotals();
    	i3.setId(3L);
    	i3.setLabel("test_3");
    	i1.setSubTotalEl("true");
    	
    	subTotals.add(i1);
    	subTotals.add(i2);
    	subTotals.add(i3);
    }
    
    private void initInvoiceLines() {
    	InvoiceLine iv1 = new InvoiceLine(null, new BigDecimal(10), new BigDecimal(20), new BigDecimal(25), null, null, null, null, null, null, null);
    	iv1.setTransactionalAmountWithoutTax(new BigDecimal(21));
    	iv1.setTransactionalAmountWithTax(new BigDecimal(26));
    	

    	InvoiceLine iv2 = new InvoiceLine(null, new BigDecimal(20), new BigDecimal(20), new BigDecimal(25), null, null, null, null, null, null, null);
    	iv2.setTransactionalAmountWithTax(new BigDecimal(26));
    	

    	InvoiceLine iv3 = new InvoiceLine(null, new BigDecimal(5), new BigDecimal(20), new BigDecimal(25), null, null, null, null, null, null, null);
    	iv3.setTransactionalAmountWithoutTax(new BigDecimal(21));

    	InvoiceLine iv4 = new InvoiceLine(null, new BigDecimal(15), new BigDecimal(20), new BigDecimal(25), null, null, null, null, null, null, null);
    	
    	invoiceLines.add(iv1);
    	invoiceLines.add(iv2);
    	invoiceLines.add(iv3);
    	invoiceLines.add(iv4);
    }
    
    @Before
    public void setup() {
    	invoice = new Invoice();
    }
    
    @Test
    public void shouldReturn_BadRequestExceotion() {
    	expectedException.expect(BadRequestException.class);
    	expectedException.expectMessage("Action is failed");
    	
    	invoiceSubTotalsApiService.calculateSubTotals(invoice);
    }
    
    @Test
    public void shouldReturn_emptyList() {
        when(invoiceSubTotalsService.findByInvoiceType(any())).thenReturn(Collections.emptyList());
        invoice.setInvoiceType(new InvoiceType());
        invoiceSubTotalsApiService.calculateSubTotals(invoice);
    }
    
    @Test
    public void createSubTotals() {
        InvoiceSubTotalsDto invoiceSubTotalsDto = initInvoiceSubTotalsDto();
        subTotals = invoiceSubTotalsService.addSubTotals(invoiceSubTotalsDto);
        assertEquals(0, subTotals.get(0).getAmountWithoutTax().intValue());
    }
    
    @Test
    public void deleteSubTotals() {
        InvoiceSubTotalsDto invoiceSubTotalsDto = init4DeleteInvoiceSubTotalsDto();
        invoiceSubTotalsService.deleteSubTotals(invoiceSubTotalsDto);
        for(SubTotalsDto subTotalsDto : invoiceSubTotalsDto.getSubTotals()) {
            Long subTotalId = subTotalsDto.getId();           
            InvoiceSubTotals invoiceSubTotal = invoiceSubTotalsService.findById(subTotalId);
            assertEquals(invoiceSubTotal, null);
        }   
    }
    
    private InvoiceSubTotalsDto init4DeleteInvoiceSubTotalsDto() {
        InvoiceSubTotalsDto invoiceSubTotalsDto = new InvoiceSubTotalsDto();
        InvoiceTypeDto invoiceTypeDto = new InvoiceTypeDto();
        invoiceTypeDto.setId(-1L);
        invoiceSubTotalsDto.setInvoiceType(invoiceTypeDto);
        List<SubTotalsDto> subTotalsListDto = new ArrayList<SubTotalsDto>();
        
        SubTotalsDto subTotalsDto1 = new SubTotalsDto();
        subTotalsDto1.setId(1L);        
        subTotalsListDto.add(subTotalsDto1);

        invoiceSubTotalsDto.setSubTotals(subTotalsListDto);
        return invoiceSubTotalsDto;
    }

    private InvoiceSubTotalsDto initInvoiceSubTotalsDto() {
        InvoiceSubTotalsDto invoiceSubTotalsDto = new InvoiceSubTotalsDto();
        InvoiceTypeDto invoiceTypeDto = new InvoiceTypeDto();
        invoiceTypeDto.setId(-1L);
        invoiceSubTotalsDto.setInvoiceType(invoiceTypeDto);
        List<SubTotalsDto> subTotalsListDto = new ArrayList<SubTotalsDto>();
        
        SubTotalsDto subTotalsDto1 = new SubTotalsDto();
        subTotalsDto1.setLabel("test_1");
        subTotalsDto1.setEl("#{invoiceLine.getQuantity().intValue() == 10}");
        subTotalsDto1.setLabel("English label");
        List<LanguageDescriptionDto> listLanguageLabels = new ArrayList<LanguageDescriptionDto>();
        LanguageDescriptionDto languageDescription1 = new LanguageDescriptionDto();
        languageDescription1.setLanguageCode("FRA");
        languageDescription1.setLanguageCode("labelFR");
        LanguageDescriptionDto languageDescription2 = new LanguageDescriptionDto();
        languageDescription2.setLanguageCode("ENG");
        languageDescription2.setLanguageCode("label ENG");
        listLanguageLabels.add(languageDescription1);
        listLanguageLabels.add(languageDescription2);
        subTotalsDto1.setLanguageLabels(listLanguageLabels);

        SubTotalsDto subTotalsDto2 = new SubTotalsDto();
        subTotalsDto2.setLabel("test_1");
        subTotalsDto2.setEl("#{invoiceLine.getQuantity().intValue() == 10}");
        subTotalsDto2.setLabel("English label");
        subTotalsDto2.setLanguageLabels(listLanguageLabels);
        
        subTotalsListDto.add(subTotalsDto1);
        subTotalsListDto.add(subTotalsDto2);

        invoiceSubTotalsDto.setSubTotals(subTotalsListDto);
        return invoiceSubTotalsDto;
    }

    @Test
    public void shouldReturn_listOfInvoiceSubTotals() {
    	initInvoiceSubTotals();
    	initInvoiceLines();
    	invoice.setInvoiceType(new InvoiceType());
    	invoice.setId(1L);

    	when(invoiceSubTotalsService.findByInvoiceType(any())).thenReturn(subTotals);
    	when(invoiceLineService.listInvoiceLinesByInvoice(anyLong())).thenReturn(invoiceLines);
    	when(ValueExpressionWrapper.evaluateExpression("", Boolean.class, invoiceLines.get(0))).then(null);
    	
    	invoiceSubTotalsApiService.calculateSubTotals(invoice);
    	
    	assertEquals(26, subTotals.get(0).getAmountWithoutTax().intValue());
    	assertEquals(21, subTotals.get(0).getAmountWithoutTax().intValue());

    	assertEquals(46, subTotals.get(1).getAmountWithoutTax().intValue());
    	assertEquals(25, subTotals.get(1).getAmountWithoutTax().intValue());
    	

    	assertEquals(82, subTotals.get(1).getAmountWithoutTax().intValue());
    	assertEquals(102, subTotals.get(1).getAmountWithoutTax().intValue());
    	
    }
}
