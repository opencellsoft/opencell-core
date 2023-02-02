package org.meveo.apiv2.billing;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
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

	Invoice invoice = new Invoice();

	@Test
	public void shouldThrowBusinessExceptionIfStatusNotIgnoreToMark() {

		// InvoiceType
		InvoiceType it1 = new InvoiceType();
		it1.setCode("DRAFT");

		// InvoiceLine
		InvoiceLine il1 = new InvoiceLine();

		// securityDepositInvoice
		invoice = new Invoice();
		invoice.setId(1L);
		invoice.setInvoiceType(it1);

		List<InvoiceLine> invoiceLines = new ArrayList<>();
		invoiceLines.add(il1);

		invoice.setInvoiceLines(invoiceLines);
		
		List<Long> idList = invoiceLines
	            .stream()
	            .map(InvoiceLine::getId)
	            .collect(Collectors.toList());

		expectedException.expect(BusinessException.class);
		expectedException.expectMessage("Only NOT_ADJUSTED invoice lines can be marked TO_ADJUST");

		invoiceLinesApiService.markInvoiceLinesForAdjustment(false, ilIds);
	}

	@Test
	public void shouldThrowBusinessExceptionIfStatusNotIgnoreToUnMark() {

		// InvoiceType
		InvoiceType it1 = new InvoiceType();
		it1.setCode("DRAFT");

		// InvoiceLine
		InvoiceLine il1 = new InvoiceLine();

		// securityDepositInvoice
		invoice = new Invoice();
		invoice.setId(1L);
		invoice.setInvoiceType(it1);

		List<InvoiceLine> invoiceLines = new ArrayList<>();
		invoiceLines.add(il1);

		invoice.setInvoiceLines(invoiceLines);

		expectedException.expect(BusinessException.class);
		expectedException.expectMessage("Only TO_ADJUST invoice lines can be marked NOT_ADJUSTED");

		invoiceLinesApiService.unmarkInvoiceLinesForAdjustment(false, ilIds);
	}

	@Test
	public void shouldMarkInvoiceLinesForAdjustment() {

		// InvoiceType
		InvoiceType it1 = new InvoiceType();
		it1.setCode("DRAFT");

		// InvoiceLine
		InvoiceLine il1 = new InvoiceLine();

		// securityDepositInvoice
		invoice = new Invoice();
		invoice.setId(1L);
		invoice.setInvoiceType(it1);

		il1.setInvoice(invoice);

		List<InvoiceLine> invoiceLines = new ArrayList<>();
		invoiceLines.add(il1);

		invoice.setInvoiceLines(invoiceLines);
		

		when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.NOT_ADJUSTED)))
				.thenReturn(invoiceLines);

		int nbInvoiceLines = invoiceLinesApiService.markInvoiceLinesForAdjustment(false, ilIds);
		assertEquals(1, nbInvoiceLines);
	}

	@Test
	public void shouldUnMarkInvoiceLinesForAdjustment() {

		// InvoiceType
		InvoiceType it1 = new InvoiceType();
		it1.setCode("DRAFT");

		// InvoiceLine
		InvoiceLine il1 = new InvoiceLine();

		// securityDepositInvoice
		invoice = new Invoice();
		invoice.setId(1L);
		invoice.setInvoiceType(it1);

		il1.setInvoice(invoice);

		List<InvoiceLine> invoiceLines = new ArrayList<>();
		invoiceLines.add(il1);

		invoice.setInvoiceLines(invoiceLines);

		when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.TO_ADJUST)))
				.thenReturn(invoiceLines);

		int nbInvoiceLines = invoiceLinesApiService.unmarkInvoiceLinesForAdjustment(false, ilIds);
		assertEquals(1, nbInvoiceLines);
	}

	@Test
	public void shouldThrowBusinessExceptionWhenMarkIfSecurityDepositType() {

		// InvoiceType
		InvoiceType it1 = new InvoiceType();
		it1.setCode("SECURITY_DEPOSIT");

		// InvoiceLine
		InvoiceLine il1 = new InvoiceLine();

		// securityDepositInvoice
		invoice = new Invoice();
		invoice.setId(1L);
		invoice.setInvoiceType(it1);
		il1.setInvoice(invoice);

		List<InvoiceLine> invoiceLines = new ArrayList<>();
		invoiceLines.add(il1);

		invoice.setInvoiceLines(invoiceLines);

		
		when(invoiceLineService.findByIdsAndInvoiceType(any(), Mockito.eq("SECURITY_DEPOSIT"))).thenReturn(invoiceLines);
		expectedException.expect(BusinessException.class);
		expectedException.expectMessage("Security deposit invoices can not be marked for mass adjustment.");

		invoiceLinesApiService.markInvoiceLinesForAdjustment(false, ilIds);
	}

	@Test
	public void shouldThrowBusinessExceptionWhenUnMarkIfSecurityDepositType() {

		// InvoiceType
		InvoiceType it1 = new InvoiceType();
		it1.setCode("SECURITY_DEPOSIT");

		// InvoiceLine
		InvoiceLine il1 = new InvoiceLine();

		// securityDepositInvoice
		invoice = new Invoice();
		invoice.setId(1L);
		invoice.setInvoiceType(it1);
		il1.setInvoice(invoice);

		List<InvoiceLine> invoiceLines = new ArrayList<>();
		invoiceLines.add(il1);

		invoice.setInvoiceLines(invoiceLines);

		when(invoiceLineService.findByIdsAndInvoiceType(any(), Mockito.eq("SECURITY_DEPOSIT"))).thenReturn(invoiceLines);

		expectedException.expect(BusinessException.class);
		expectedException.expectMessage("Security deposit invoices can not be marked for mass adjustment.");

		invoiceLinesApiService.unmarkInvoiceLinesForAdjustment(false, ilIds);
	}
	
	@Test
	public void shouldMarkInvoiceLinesForAdjustmentAndIgnoreStatus() {

		// InvoiceType
		InvoiceType it1 = new InvoiceType();
		it1.setCode("DRAFT");

		// InvoiceLine
		InvoiceLine il1 = new InvoiceLine();

		// securityDepositInvoice
		invoice = new Invoice();
		invoice.setId(1L);
		invoice.setInvoiceType(it1);

		il1.setInvoice(invoice);

		List<InvoiceLine> invoiceLines = new ArrayList<>();
		invoiceLines.add(il1);

		invoice.setInvoiceLines(invoiceLines);
		

		when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.NOT_ADJUSTED)))
				.thenReturn(invoiceLines);

		int nbInvoiceLines = invoiceLinesApiService.markInvoiceLinesForAdjustment(true, ilIds);
		assertEquals(1, nbInvoiceLines);
	}

	@Test
	public void shouldUnMarkInvoiceLinesForAdjustmentAndIgnoreStatus() {

		// InvoiceType
		InvoiceType it1 = new InvoiceType();
		it1.setCode("DRAFT");

		// InvoiceLine
		InvoiceLine il1 = new InvoiceLine();

		// securityDepositInvoice
		invoice = new Invoice();
		invoice.setId(1L);
		invoice.setInvoiceType(it1);

		il1.setInvoice(invoice);

		List<InvoiceLine> invoiceLines = new ArrayList<>();
		invoiceLines.add(il1);

		invoice.setInvoiceLines(invoiceLines);
		

		when(invoiceLineService.findByIdsAndAdjustmentStatus(any(), Mockito.eq(AdjustmentStatusEnum.TO_ADJUST)))
				.thenReturn(invoiceLines);

		int nbInvoiceLines = invoiceLinesApiService.unmarkInvoiceLinesForAdjustment(true, ilIds);
		assertEquals(1, nbInvoiceLines);
	}

}