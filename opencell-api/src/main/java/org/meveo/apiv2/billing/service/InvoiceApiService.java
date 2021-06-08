/**
 * 
 */
package org.meveo.apiv2.billing.service;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.ImmutableInvoiceLine;
import org.meveo.apiv2.billing.ImmutableInvoiceLinesInput;
import org.meveo.apiv2.billing.InvoiceLine;
import org.meveo.apiv2.billing.InvoiceLineInput;
import org.meveo.apiv2.billing.InvoiceLinesInput;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.Invoice;
import org.meveo.service.billing.impl.InvoiceLinesService;
import org.meveo.service.billing.impl.InvoiceService;

public class InvoiceApiService  implements ApiService<Invoice> {
	
	public static final String ADV = "ADV";
	
    @Inject
    private InvoiceService invoiceService;

	@Inject
	private InvoiceLinesService invoiceLinesService;
	
	
	@Override
	public List<Invoice> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, null, null, null);
        return invoiceService.listWithlinkedInvoices(paginationConfiguration);
	}
	
	@Override
	public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, null, null, null);
		return invoiceService.count(paginationConfiguration);
	}

	@Override
	public Optional<Invoice> findById(Long id) {
		return ofNullable(invoiceService.findById(id));
	}

	@Override
	public Invoice create(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Invoice> update(Long id, Invoice baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Invoice> patch(Long id, Invoice baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Invoice> delete(Long id) {
        Invoice invoice = invoiceService.findById(id);
        if(invoice != null) {
            invoiceService.remove(invoice);
            return Optional.of(invoice);
        }
        return Optional.empty();
	}

	@Override
	public Optional<Invoice> findByCode(String code) {
		throw new BadRequestException("Use invoice number and type");
	}

	/**
	 * @param invoiceTypeId
	 * @param invoiceNumber
	 * @return
	 */
	public Optional<Invoice> findByInvoiceNumberAndTypeId(Long invoiceTypeId, String invoiceNumber) {
		return ofNullable(invoiceService.findByInvoiceTypeAndInvoiceNumber(invoiceNumber, invoiceTypeId));
	}

	/**
	 * @param invoice
	 * @param generateIfMissing
	 * @return
	 */
	public Optional<byte[]> fetchPdfInvoice(Invoice invoice, boolean generateIfMissing) {
		return ofNullable(invoiceService.getInvoicePdf(invoice, generateIfMissing));
		
	}
	
	/**
	 * @param basicInvoice
	 * @return
	 */
	public Invoice create(BasicInvoice basicInvoice) {
		return invoiceService.createBasicInvoice(basicInvoice);
	}
	
	/**
	 * @param typeCode
	 * @param invoiceNumber
	 * @return
	 */
	public Optional<Invoice> findByInvoiceNumberAndTypeCode(String typeCode, String invoiceNumber) {
		return ofNullable(invoiceService.findByInvoiceNumberAndTypeCode(invoiceNumber, typeCode));
	}

	/**
	 * @param invoice
	 * @param invoiceLinesInput InvoiceLinesInput
	 */
	public InvoiceLinesInput createLines(Invoice invoice, InvoiceLinesInput invoiceLinesInput) {
		ImmutableInvoiceLinesInput.Builder result = ImmutableInvoiceLinesInput.builder();
		for(InvoiceLine invoiceLineRessource: invoiceLinesInput.getInvoiceLines()) {
			org.meveo.model.cpq.commercial.InvoiceLine invoiceLine  = invoiceLinesService.create(invoice, invoiceLineRessource);
			invoiceLineRessource =  ImmutableInvoiceLine.copyOf(invoiceLineRessource).withId(invoiceLine.getId());
			result.addInvoiceLines(invoiceLineRessource);
		}
		result.skipValidation(invoiceLinesInput.getSkipValidation());
		return result.build();
	}
	

	/**
	 * @param invoice
	 */
	public void rebuildInvoice(Invoice invoice) {
		invoiceService.rebuildInvoice(invoice, true);
	}

	/**
	 * @param invoice
	 * @param invoiceLineInput
	 * @param lineId 
	 */
	public void updateLine(Invoice invoice, InvoiceLineInput invoiceLineInput, Long lineId) {
		invoiceLinesService.update(invoice, invoiceLineInput.getInvoiceLine(), lineId);
	}

	/**
	 * @param invoice
	 * @param lineId
	 */
	public void removeLine(Invoice invoice, Long lineId) {
		invoiceLinesService.remove(invoice, lineId);
	}

	/**
	 * @param invoice
	 */
	public void rejectInvoice(Invoice invoice) {
		invoiceService.rejectInvoice(invoice);
	}

	/**
	 * @param invoice
	 */
	public void validateInvoice(Invoice invoice) {
		invoiceService.validateInvoice(invoice, true);
	}

	/**
	 * @param invoice
	 */
	public void cancelInvoice(Invoice invoice) {
		invoiceService.cancelInvoice(invoice);
	}

	/**
	 * @param input InvoiceInput
	 * @return
	 */
	public Invoice create(org.meveo.apiv2.billing.InvoiceInput input) {
		return invoiceService.createInvoiceV11(input.getInvoice(), input.getSkipValidation(), input.getIsDraft(), input.getIsVirtual(), input.getIsIncludeBalance(), input.getIsAutoValidation());
	}
	
	public Invoice update(Invoice invoice, Invoice input, org.meveo.apiv2.billing.Invoice invoiceRessource) {
		return invoiceService.update(invoice, input, invoiceRessource);
	}

	/**
	 * @param invoice
	 */
	public void calculateInvoice(Invoice invoice) {
		invoiceService.calculateInvoice(invoice);
	}


}
