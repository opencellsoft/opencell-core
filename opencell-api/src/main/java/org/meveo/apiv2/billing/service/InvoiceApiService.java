/**
 * 
 */
package org.meveo.apiv2.billing.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.billing.ImmutableFile;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.Invoice;
import org.meveo.service.billing.impl.InvoiceService;

public class InvoiceApiService  implements ApiService<Invoice> {
	
    @Inject
    private InvoiceService invoiceService;
	
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

}
