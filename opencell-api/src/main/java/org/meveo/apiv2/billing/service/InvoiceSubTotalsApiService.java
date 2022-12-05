package org.meveo.apiv2.billing.service;

import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubTotals;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceSubTotalsService;

public class InvoiceSubTotalsApiService implements ApiService<InvoiceSubTotals> {
	
	@Inject
	private InvoiceSubTotalsService invoiceSubTotalsService;
	@Inject
	private InvoiceLineService invoiceLineService;

	@Override
	public List<InvoiceSubTotals> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		throw new BusinessApiException("not yet implemented");
	}

	@Override
	public Long getCount(String filter) {
		throw new BusinessApiException("not yet implemented");
	}

	@Override
	public Optional<InvoiceSubTotals> findById(Long id) {
		return Optional.ofNullable(invoiceSubTotalsService.findById(id));
	}

	@Override
	public InvoiceSubTotals create(InvoiceSubTotals baseEntity) {
		throw new BusinessApiException("not yet implemented");
	}

	@Override
	public Optional<InvoiceSubTotals> update(Long id, InvoiceSubTotals baseEntity) {
		throw new BusinessApiException("not yet implemented");
	}

	@Override
	public Optional<InvoiceSubTotals> patch(Long id, InvoiceSubTotals baseEntity) {
		throw new BusinessApiException("not yet implemented");
	}

	@Override
	public Optional<InvoiceSubTotals> delete(Long id) {
		throw new BusinessApiException("not yet implemented");
	}

	@Override
	public Optional<InvoiceSubTotals> findByCode(String code) {
		return Optional.ofNullable(invoiceSubTotalsService.findByCode(code));
		
	}
	
	public List<InvoiceSubTotals> calculateSubTotals(Invoice invoice){
		var invoiceType = invoice.getInvoiceType();
		if(invoiceType == null) throw new BadRequestException("Action is failed");
		var invoiceLines = invoiceLineService.listInvoiceLinesByInvoice(invoice.getId());
		return invoiceSubTotalsService.calculateSubTotals(invoiceType, invoiceLines);
	}

}
