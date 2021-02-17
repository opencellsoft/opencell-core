package org.meveo.apiv2.billing.impl;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.ImmutableFile;
import org.meveo.apiv2.billing.ImmutableInvoice;
import org.meveo.apiv2.billing.ImmutableInvoices;
import org.meveo.apiv2.billing.InvoiceLineInput;
import org.meveo.apiv2.billing.InvoiceLinesToRemove;
import org.meveo.apiv2.billing.InvoiceLinesInput;
import org.meveo.apiv2.billing.Invoices;
import org.meveo.apiv2.billing.resource.InvoiceResource;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;

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
		return buildInvoicesReturn(offset, limit, filter, request, invoicesEntity);
	}

	private Response buildInvoicesReturn(Long offset, Long limit, String filter, Request request,
			List<Invoice> invoicesEntity) {
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
		generateIfMissing = generateIfMissing == null? false : generateIfMissing;
		byte[] content = invoiceApiService.fetchPdfInvoice(invoice, generateIfMissing)
				.orElseThrow(NotFoundException::new);
		ImmutableFile file = ImmutableFile.builder().id(invoice.getId()).fileName(invoice.getPdfFilename())
				.fileType("PDF").fileContent(content).build();
		return Response.ok().entity(file).build();

	}

	@Override
	public Response getAdvancedPaymentInvoices(Long offset, Long limit, String sort, String orderBy, String filter,
			Request request) {
		List<Invoice> invoicesEntity = invoiceApiService.listAdvancedPaymentInvoices(offset, limit, sort, orderBy, filter);
		return buildInvoicesReturn(offset, limit, filter, request, invoicesEntity);
	}

	@Override
	public Response createAdvancedPaymentInvoices(BasicInvoice basicInvoice) {
        Invoice invoice = invoiceApiService.create(basicInvoice);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, invoice.getId()).build())
                .entity(toResourceInvoiceWithLink(invoiceMapper.toResource(invoice)))
                .build();
	}

	@Override
	public Response getAdvancedPaymentInvoice(Long id, Request request) {
		Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
		final String typeCode = invoice.getInvoiceType().getCode();
		if(!InvoiceApiService.ADV.equals(typeCode)) {
			throw new NotFoundException("invoice with id '"+id+"' is of type "+typeCode);
		}
		return buildInvoiceResponse(request, invoice);
	}

	@Override
	public Response getAdvancedPaymentInvoice(String invoiceNumber, Request request) {
		Invoice invoice = invoiceApiService.findByInvoiceNumberAndTypeCode(InvoiceApiService.ADV, invoiceNumber)
				.orElseThrow(NotFoundException::new);
		return buildInvoiceResponse(request, invoice);
	}

	@Override
	public Response addInvoiceLines(Long id, InvoiceLinesInput invoiceLinesInput) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.createLines(invoice, invoiceLinesInput);
		if(invoiceLinesInput.getSkipValidation()==null || !invoiceLinesInput.getSkipValidation()) {
			invoiceApiService.rebuildInvoice(invoice);
		}
		return Response.created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}
	
	@Override
	public Response updateInvoiceLine(Long id, Long lineId, InvoiceLineInput invoiceLineInput) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.updateLine(invoice, invoiceLineInput, lineId);
		if(invoiceLineInput.getSkipValidation()==null || !invoiceLineInput.getSkipValidation()) {
			invoiceApiService.rebuildInvoice(invoice);
		}
		return Response.created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}

	private Invoice findInvoiceEligibleToUpdate(Long id) {
		Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
		final InvoiceStatusEnum status = invoice.getStatus();
		if(!(InvoiceStatusEnum.REJECTED.equals(status) || InvoiceStatusEnum.SUSPECT.equals(status) || InvoiceStatusEnum.DRAFT.equals(status))) {
			throw new ActionForbiddenException("Can only update invoices in statuses DRAFT/SUSPECT/REJECTED. current invoice status is :"+status.name()) ;
		}
		return invoice;
	}

	@Override
	public Response removeInvoiceLine(Long id, Long lineId, InvoiceLinesToRemove invoiceLineToRemove) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.removeLine(invoice,lineId);
		if(invoiceLineToRemove==null || invoiceLineToRemove.getSkipValidation()==null || !invoiceLineToRemove.getSkipValidation()) {
			invoiceApiService.rebuildInvoice(invoice);
		}
		return null;
	}

	@Override
	public Response removeInvoiceLines(Long id, InvoiceLinesToRemove invoiceLineToRemove) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		for(Long lineId : invoiceLineToRemove.getIds()) {
			invoiceApiService.removeLine(invoice,lineId);
		}
		
		if(invoiceLineToRemove==null || invoiceLineToRemove.getSkipValidation()==null || !invoiceLineToRemove.getSkipValidation()) {
			invoiceApiService.rebuildInvoice(invoice);
		}
		return null;
	}

	@Override
	public Response rebuildInvoiceLine(Long id) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.rebuildInvoice(invoice);
		return Response.created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}

	@Override
	public Response rejectInvoiceLine(Long id) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.rejectInvoice(invoice);
		return Response.created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}

	@Override
	public Response validateInvoiceLine(Long id) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.validateInvoice(invoice);
		return Response.created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}

	@Override
	public Response cancelInvoiceLine(Long id) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.cancelInvoice(invoice);
		return Response.created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}
}
