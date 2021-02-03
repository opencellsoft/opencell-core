package org.meveo.apiv2.billing.impl;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.billing.ImmutableFile;
import org.meveo.apiv2.billing.ImmutableInvoice;
import org.meveo.apiv2.billing.ImmutableInvoices;
import org.meveo.apiv2.billing.Invoices;
import org.meveo.apiv2.billing.resource.InvoiceResource;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.billing.Invoice;

public class InvoiceResourceImpl implements InvoiceResource {

	@Inject
	private InvoiceApiService invoiceApiService;

	private static final InvoiceMapper invoiceMapper = new InvoiceMapper();

	@Override
	public Response getInvoice(Long id, Request request) {
		Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
		return buildInvoiceResponse(request, invoice);
	}

	private Response buildInvoiceResponse(Request request, Invoice invoice) {
		EntityTag etag = new EntityTag(Integer.toString(invoice.hashCode()));
		Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			builder = Response.ok().tag(etag).entity(toResourceInvoiceWithLink(invoiceMapper.toResource(invoice)));
		}
		CacheControl cc = new CacheControl();
		cc.setMaxAge(1000);
		return builder.cacheControl(cc).build();
	}

	@Override
	public Response getInvoices(Long offset, Long limit, String sort, String orderBy, String filter, Request request) {
		List<Invoice> invoicesEntity = invoiceApiService.list(offset, limit, sort, orderBy, filter);
		EntityTag etag = new EntityTag(Integer.toString(invoicesEntity.hashCode()));
		CacheControl cc = new CacheControl();
		cc.setMaxAge(1000);
		Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder != null) {
			builder.cacheControl(cc);
			return builder.build();
		}
		ImmutableInvoice[] invoiceList = invoicesEntity.stream()
				.map(invoice -> toResourceInvoiceWithLink(invoiceMapper.toResource(invoice)))
				.toArray(ImmutableInvoice[]::new);
		Long invoiceCount = invoiceApiService.getCount(filter);
		Invoices invoices = ImmutableInvoices.builder().addData(invoiceList).offset(offset).limit(limit)
				.total(invoiceCount).build().withLinks(new LinkGenerator.PaginationLinkGenerator(InvoiceResource.class)
						.offset(offset).limit(limit).total(invoiceCount).build());
		return Response.ok().cacheControl(cc).tag(etag).entity(invoices).build();
	}

	private org.meveo.apiv2.billing.Invoice toResourceInvoiceWithLink(org.meveo.apiv2.billing.Invoice invoice) {
		return ImmutableInvoice.copyOf(invoice)
				.withLinks(new LinkGenerator.SelfLinkGenerator(InvoiceResource.class).withId(invoice.getId())
						.withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction().build());
	}

	@Override
	public Response getInvoice(Long invoiceTypeId, String invoiceNumber, Request request) {
		Invoice invoice = invoiceApiService.findByInvoiceNumberAndTypeId(invoiceTypeId, invoiceNumber)
				.orElseThrow(NotFoundException::new);
		return buildInvoiceResponse(request, invoice);
	}

	@Override
	public Response fetchPdfInvoice(@NotNull Long id, Boolean generateIfMissing, Request request) {
		Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
		generateIfMissing = generateIfMissing ==null? false: generateIfMissing;
		byte[] content = invoiceApiService.fetchPdfInvoice(invoice, generateIfMissing)
				.orElseThrow(NotFoundException::new);
		ImmutableFile file = ImmutableFile.builder().id(invoice.getId()).fileName(invoice.getPdfFilename())
				.fileType("PDF").fileContent(content).build();
		return Response.ok().entity(file).build();

	}
}
