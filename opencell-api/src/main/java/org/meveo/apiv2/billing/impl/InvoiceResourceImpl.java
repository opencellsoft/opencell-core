package org.meveo.apiv2.billing.impl;

import static org.meveo.model.billing.InvoiceStatusEnum.DRAFT;
import static org.meveo.model.billing.InvoiceStatusEnum.NEW;
import static org.meveo.model.billing.InvoiceStatusEnum.REJECTED;
import static org.meveo.model.billing.InvoiceStatusEnum.SUSPECT;


import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.GenerateInvoiceInput;
import org.meveo.apiv2.billing.GenerateInvoiceResult;
import org.meveo.apiv2.billing.ImmutableFile;
import org.meveo.apiv2.billing.ImmutableInvoice;
import org.meveo.apiv2.billing.ImmutableInvoices;
import org.meveo.apiv2.billing.InvoiceInput;
import org.meveo.apiv2.billing.InvoiceLineInput;
import org.meveo.apiv2.billing.InvoiceLinesInput;
import org.meveo.apiv2.billing.InvoiceLinesToRemove;
import org.meveo.apiv2.billing.InvoiceLinesToReplicate;
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
	public Response getInvoiceMatchedOperations(Long invoiceTypeId, String invoiceNumber, Request request) {
		Invoice invoice = invoiceApiService.findByInvoiceNumberAndTypeId(invoiceTypeId, invoiceNumber)
				.orElseThrow(NotFoundException::new);
		List<AccountOperation> accountOperations = accountOperationService.listByInvoice(invoice);
		/*Set<InvoiceMatchedOperation> collect = accountOperations == null ? Collections.EMPTY_SET : accountOperations.stream()
				.map(accountOperation -> accountOperationService.findById(accountOperation.getId(), Arrays.asList("matchingAmounts")))
				.map(accountOperation -> accountOperation.getMatchingAmounts().stream()
						.map(matchingAmount -> matchingCodeService.findById(matchingAmount.getMatchingCode().getId(), Arrays.asList("matchingAmounts")))
						.map(matchingCode -> matchingCode.getMatchingAmounts())
						.flatMap(Collection::stream)
						.filter(matchingAmount -> !accountOperation.getId().equals(matchingAmount.getAccountOperation().getId()))
						.collect(Collectors.toSet())
				)
				.flatMap(Collection::stream)
				.distinct()
				.map(matchingAmount -> toResponse(matchingAmount.getAccountOperation(), matchingAmount, invoice))
				.collect(Collectors.toSet());*/

		// Quick & (may be) dirty fix : above flatMap cause wrong behavoir, i replace it by nested loop (no time to investigate why ! malheureusement)
		Set<InvoiceMatchedOperation> result = new HashSet<>();

		Optional.ofNullable(accountOperations).orElse(Collections.emptyList())
				.forEach(accountOperation -> {
					AccountOperation invoiceAo = accountOperationService.findById(accountOperation.getId(), List.of("matchingAmounts"));

					Optional.ofNullable(invoiceAo.getMatchingAmounts()).orElse(Collections.emptyList())
							.forEach(matchingAmount -> {
								MatchingCode matchingCode = matchingCodeService.findById(matchingAmount.getMatchingCode().getId(), List.of("matchingAmounts"));
								Optional.ofNullable(matchingCode.getMatchingAmounts()).orElse(Collections.emptyList())
										.forEach(matchingAmountAo -> {
											if (!matchingAmountAo.getAccountOperation().getId().equals(accountOperation.getId())) {
												result.add(toResponse(matchingAmountAo.getAccountOperation(), matchingAmountAo, invoice));
											}
										});
							});

				});

		return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(buildResponse(result)).build();
	}

	private Map<String, Object> buildResponse(Set<InvoiceMatchedOperation> collect) {
		Map<String, Object> response = new HashMap<>();
		response.put("actionStatus", Collections.singletonMap("status","SUCCESS"));
		response.put("invoiceMatchedOperations", collect);
		return response;
	}

	private InvoiceMatchedOperation toResponse(AccountOperation accountOperation, MatchingAmount matchingAmountPrimary, Invoice invoice){
		MatchingCode matchingCode = matchingAmountPrimary.getMatchingCode();
		ImmutableInvoiceMatchedOperation.Builder builder = ImmutableInvoiceMatchedOperation.builder();
		return builder
				.paymentId(accountOperation.getId())
				.paymentCode(accountOperation.getCode())
				.paymentDescription(accountOperation.getDescription())
				.paymentStatus(accountOperation.getMatchingStatus() != null ? accountOperation.getMatchingStatus().getLabel() : "")
				.paymentDate(accountOperation.getTransactionDate())
				.paymentMethod(accountOperation.getPaymentMethod() != null ? accountOperation.getPaymentMethod().getLabel() : "")
				.paymentRef(ofNullable(accountOperation.getReference()).orElse(""))
				.amount(accountOperation.getMatchingAmount())
				.percentageCovered(accountOperation.getMatchingAmount().divide(invoice.getAmountWithTax(), 12, RoundingMode.HALF_UP))
				.matchingType(matchingCode.getMatchingType() != null ? matchingCode.getMatchingType().getLabel() : "")
				.matchingDate(matchingCode.getMatchingDate())
				.rejectedCode(accountOperation.getRejectedPayment() != null ?  accountOperation.getRejectedPayment().getRejectedCode() : "")
				.rejectedDescription(accountOperation.getRejectedPayment() != null ?  accountOperation.getRejectedPayment().getRejectedDescription() : "")
				.build();
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
	public Response createBasicInvoices(BasicInvoice basicInvoice) {
        Invoice invoice = invoiceApiService.create(basicInvoice);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, invoice.getId()).build())
                .entity(toResourceInvoiceWithLink(invoiceMapper.toResource(invoice)))
                .build();
	}

	@Override
	public Response addInvoiceLines(Long id, InvoiceLinesInput invoiceLinesInput) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceLinesInput = invoiceApiService.createLines(invoice, invoiceLinesInput);
		if(invoiceLinesInput.getSkipValidation()==null || !invoiceLinesInput.getSkipValidation()) {
			invoiceApiService.rebuildInvoice(invoice);
		}
		return Response.ok().entity(invoiceLinesInput).build();
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
		if(!(REJECTED.equals(status) || SUSPECT.equals(status) || DRAFT.equals(status)|| NEW.equals(status))) {
			throw new ActionForbiddenException("Can only update invoices in statuses NEW/DRAFT/SUSPECT/REJECTED. current invoice status is :"+status.name()) ;
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
	public Response cancelInvoice(Long id) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.cancelInvoice(invoice);
		return Response.created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}

	@Override
	public Response create(InvoiceInput input) {
		Invoice invoiceEntity = invoiceApiService.create(input);
		return Response.created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, invoiceEntity.getId()).build())
				.entity(toResourceInvoiceWithLink(invoiceMapper.toResource(invoiceEntity)))
                .build();
	}
	
	@Override
	public Response update(Long id, org.meveo.apiv2.billing.Invoice invoiceResource) {
		final Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.update(invoice, invoiceMapper.toEntity(invoiceResource), invoiceResource);
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}

	@Override
	public Response find(String invoiceNumber, Request request) {
		Invoice invoice = invoiceApiService.findByCode(invoiceNumber).orElseThrow(NotFoundException::new);
		return buildInvoiceResponse(request, invoice);
	}
	
	@Override
	public Response calculateInvoice(Long id) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.calculateInvoice(invoice);
		return Response.accepted(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}

    @Override
    public Response duplicate(Long invoiceId) {
    	Invoice invoice = invoiceApiService.findById(invoiceId).orElseThrow(NotFoundException::new);
    	
        return Response.ok(toResourceInvoiceWithLink(invoiceMapper.toResource(invoiceApiService.duplicate(invoice)))).build();
    }

	@Override
	public Response generate(GenerateInvoiceInput input) {
		GenerateInvoiceRequestDto invoiceRequest = invoiceMapper.toGenerateInvoiceRequestDto(input);
		List<GenerateInvoiceResult> invoices = invoiceApiService.generate(invoiceRequest, input.getIsDraft()).get();
		return Response.ok().entity(invoices).build();
	}
	
	@Override
    public Response createAdjustment(@NotNull Long id, @NotNull InvoiceLinesToReplicate invoiceLinesToReplicate) {
        Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
        Invoice adjInvoice = invoiceApiService.createAdjustment(invoice, invoiceLinesToReplicate);
        return Response.ok().entity(buildSuccessResponse(invoiceMapper.toResource(adjInvoice))).build();

    }

    private Map<String, Object> buildSuccessResponse(org.meveo.apiv2.billing.Invoice invoiceResource) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("invoice", invoiceResource);
        return response;
    }
}