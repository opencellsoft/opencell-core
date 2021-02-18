/**
 * 
 */
package org.meveo.apiv2.billing.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.InvoiceLine;
import org.meveo.apiv2.billing.InvoiceLineInput;
import org.meveo.apiv2.billing.InvoiceLinesInput;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.billing.impl.InvoiceLinesService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.order.OrderService;

public class InvoiceApiService  implements ApiService<Invoice> {
	
	public static final String ADV = "ADV";
	
    @Inject
    private InvoiceService invoiceService;
    
	@Inject
	OrderService orderService;
	
	@Inject InvoiceTypeService invoiceTypeService;

	@Inject
	private InvoiceLinesService invoiceLinesService;
	
	@Inject
	private BusinessEntityService businessEntityService;
	
	
	
	@Override
	public List<Invoice> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, null, null, null);
        return invoiceService.list(paginationConfiguration);
	}

	@Override
	public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, null, null, null);
		return invoiceService.count(paginationConfiguration);
	}

	@Override
	public Optional<Invoice> findById(Long id) {
		return Optional.ofNullable(invoiceService.findById(id));
	}

	@Override
	public Invoice create(Invoice baseEntity) {
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

	/**
	 * @param invoiceTypeId
	 * @param invoiceNumber
	 * @return
	 */
	public Optional<Invoice> findByInvoiceNumberAndTypeId(Long invoiceTypeId, String invoiceNumber) {
		return Optional.ofNullable(invoiceService.findByInvoiceTypeAndInvoiceNumber(invoiceNumber, invoiceTypeId));
	}

	/**
	 * @param id
	 * @param generateIfMissing
	 * @return
	 */
	public Optional<byte[]> fetchPdfInvoice(Invoice invoice, boolean generateIfMissing) {
		return Optional.ofNullable(invoiceService.getInvoicePdf(invoice, generateIfMissing));
		
	}
	
	/**
	 * @param basicInvoice
	 * @return
	 */
	public Invoice create(BasicInvoice basicInvoice) {
		return invoiceService.createAdvancePaymentInvoice(basicInvoice);
	}
	
	/**
	 * @param typeCode
	 * @param invoiceNumber
	 * @return
	 */
	public Optional<Invoice> findByInvoiceNumberAndTypeCode(String typeCode, String invoiceNumber) {
		return Optional.ofNullable(invoiceService.findByInvoiceNumberAndTypeCode(invoiceNumber, typeCode));
	}

	/**
	 * @param offset
	 * @param limit
	 * @param sort
	 * @param orderBy
	 * @param filter
	 * @return
	 */
	public List<Invoice> listAdvancedPaymentInvoices(Long offset, Long limit, String sort, String orderBy,
			String filter) {
		Map<String, Object> filters = new TreeMap<String, Object>();
		InvoiceType advType = invoiceTypeService.findByCode(ADV);
		filters.put("invoiceType", advType);
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), filters, filter, null, null, null);
        return invoiceService.list(paginationConfiguration);
	}

	/**
	 * @param invoice
	 * @param invoiceLines
	 */
	public void createLines(Invoice invoice, InvoiceLinesInput invoiceLinesInput) {
		for(InvoiceLine invoiceLineRessource: invoiceLinesInput.getInvoiceLines()) {
			invoiceLinesService.create(invoice, invoiceLineRessource);
		}
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

}
