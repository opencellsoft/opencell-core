package org.meveo.apiv2.billing.impl;

import static java.util.Optional.ofNullable;
import static org.meveo.model.billing.InvoiceStatusEnum.DRAFT;
import static org.meveo.model.billing.InvoiceStatusEnum.NEW;
import static org.meveo.model.billing.InvoiceStatusEnum.REJECTED;
import static org.meveo.model.billing.InvoiceStatusEnum.SUSPECT;
import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;

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
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.billing.QuarantineBillingRunDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceSubTotalsDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.InvoiceTypeRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.GenerateInvoiceInput;
import org.meveo.apiv2.billing.GenerateInvoiceResult;
import org.meveo.apiv2.billing.ImmutableFile;
import org.meveo.apiv2.billing.ImmutableInvoice;
import org.meveo.apiv2.billing.ImmutableInvoiceMatchedOperation;
import org.meveo.apiv2.billing.ImmutableInvoiceSubTotals;
import org.meveo.apiv2.billing.ImmutableInvoices;
import org.meveo.apiv2.billing.InvoiceExchangeRateInput;
import org.meveo.apiv2.billing.InvoiceInput;
import org.meveo.apiv2.billing.InvoiceLineInput;
import org.meveo.apiv2.billing.InvoiceLinesInput;
import org.meveo.apiv2.billing.InvoiceLinesToDuplicate;
import org.meveo.apiv2.billing.InvoiceLinesToRemove;
import org.meveo.apiv2.billing.InvoiceLinesToReplicate;
import org.meveo.apiv2.billing.InvoiceMatchedOperation;
import org.meveo.apiv2.billing.InvoicePatchInput;
import org.meveo.apiv2.billing.Invoices;
import org.meveo.apiv2.billing.RejectReasonInput;
import org.meveo.apiv2.billing.ValidateInvoiceDto;
import org.meveo.apiv2.billing.resource.InvoiceResource;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.apiv2.billing.service.InvoiceSubTotalsApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.billing.*;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.billing.impl.InvoiceSubTotalsService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;

public class InvoiceResourceImpl implements InvoiceResource {

	private static final String PPL_CREATION = "PPL_CREATION";

	@Inject
	private InvoiceApiService invoiceApiService;

	@Inject
    private InvoiceSubTotalsService invoiceSubTotalsService;

	@Inject
	private AccountOperationService accountOperationService;

	@Inject
	private MatchingCodeService matchingCodeService;
	
	@Inject
	private InvoiceSubTotalsApiService invoiceSubTotalsApiService;

	private static final InvoiceMapper invoiceMapper = new InvoiceMapper();
	
	private static final InvoiceSubTotalsMapper invoiceSubTotalMapper = new InvoiceSubTotalsMapper();

	@Inject
	private GenericPagingAndFilteringUtils genericPagingAndFilteringUtils;

	@Transactional
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
		long apiLimit = genericPagingAndFilteringUtils.getLimit(limit != null ? limit.intValue() : null);
		List<Invoice> invoicesEntity = invoiceApiService.list(offset, apiLimit, sort, orderBy, filter);
		return buildInvoicesReturn(offset, apiLimit, filter, request, invoicesEntity);
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

		Set<InvoiceMatchedOperation> result = new HashSet<>();

		ofNullable(accountOperations).orElse(Collections.emptyList())
				.forEach(accountOperation -> {
					AccountOperation invoiceAo = accountOperationService.findById(accountOperation.getId(), List.of("matchingAmounts"));

					ofNullable(invoiceAo.getMatchingAmounts()).orElse(Collections.emptyList())
							.forEach(matchingAmount -> {
								MatchingCode matchingCode = matchingCodeService.findById(matchingAmount.getMatchingCode().getId(), List.of("matchingAmounts"));
								ofNullable(matchingCode.getMatchingAmounts()).orElse(Collections.emptyList())
										.forEach(matchingAmountAo -> {
											switch (accountOperation.getTransactionCategory()) {
												// For payment history, the rule is : when we hava an AO DEBIT we should have AO payment CREDIT and vice versa
												case DEBIT:
													if (matchingAmountAo.getAccountOperation().getTransactionCategory() == OperationCategoryEnum.CREDIT &&
															!PPL_CREATION.equals(matchingAmountAo.getAccountOperation().getCode()) &&
															!matchingAmountAo.getAccountOperation().getId().equals(accountOperation.getId())) {
														result.add(toResponse(matchingAmountAo.getAccountOperation(), matchingAmountAo, invoice));
													}
													break;
												case CREDIT:
													if (matchingAmountAo.getAccountOperation().getTransactionCategory() == OperationCategoryEnum.DEBIT &&
															!PPL_CREATION.equals(matchingAmountAo.getAccountOperation().getCode()) &&
															!matchingAmountAo.getAccountOperation().getId().equals(accountOperation.getId())) {
														result.add(toResponse(matchingAmountAo.getAccountOperation(), matchingAmountAo, invoice));
													}
													break;
												default:
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
				.matchingAmountId(matchingAmountPrimary.getId())
				.paymentCode(accountOperation.getCode())
				.paymentDescription(ofNullable(accountOperation.getDescription()).orElse(""))
				.paymentStatus(accountOperation.getMatchingStatus() != null ? accountOperation.getMatchingStatus().getLabel() : "")
				.paymentDate(matchingAmountPrimary.getMatchingCode().getMatchingDate())
				.paymentMethod(accountOperation.getPaymentMethod() != null ? accountOperation.getPaymentMethod().getLabel() : "")
				.paymentRef(ofNullable(accountOperation.getReference()).orElse(""))
				.amount(matchingAmountPrimary.getMatchingAmount())
				.percentageCovered(matchingAmountPrimary.getMatchingAmount().divide(invoice.getAmountWithTax(), 12, RoundingMode.HALF_UP))
				.matchingType(matchingCode.getMatchingType() != null ? matchingCode.getMatchingType().getLabel() : "")
				.matchingDate(matchingCode.getMatchingDate())
				.rejectedCode(accountOperation.getRejectedPayment() != null ?  accountOperation.getRejectedPayment().getRejectedCode() : "")
				.rejectedDescription(accountOperation.getRejectedPayment() != null ?  accountOperation.getRejectedPayment().getRejectedDescription() : "")
				.transactionalAmount(accountOperation.getTransactionalAmount())
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
	public Response deleteInvoicePdf(@NotNull Long id) {
		Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
			invoiceApiService.deleteInvoicePdf(invoice);
		return Response.ok().build();
	}

	@Override
	public Response deleteInvoiceXml(@NotNull Long id) {
		Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
		invoiceApiService.deleteInvoiceXml(invoice);
		return Response.ok().build();
	}

	@Override
	public Response createBasicInvoices(BasicInvoice basicInvoice) {
        Invoice invoice = invoiceApiService.create(basicInvoice);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, invoice.getId()).build())
                .entity(toResourceInvoiceWithLink(invoiceMapper.toResource(invoice)))
                .build();
	}

	@Transactional
	@Override
	public Response addInvoiceLines(Long id, InvoiceLinesInput invoiceLinesInput) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceLinesInput = invoiceApiService.createLines(invoice, invoiceLinesInput);
		if(invoiceLinesInput.getSkipValidation() == null || !invoiceLinesInput.getSkipValidation()) {
			invoiceApiService.rebuildInvoice(invoice);
		}
		return Response.ok().entity(invoiceLinesInput).build();
	}

	@Transactional
	@Override
	public Response updateInvoiceLine(Long id, Long lineId, InvoiceLineInput invoiceLineInput) {
		Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
		invoiceApiService.updateLine(invoice, invoiceLineInput, lineId);
		if(invoiceLineInput.getSkipValidation() == null || !invoiceLineInput.getSkipValidation()) {
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
	public Response rejectInvoiceLine(Long id, RejectReasonInput invoiceLinesReject) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.rejectInvoice(invoice, invoiceLinesReject);
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
	public Response cancelInvoice(Long id, RatedTransactionAction rtAction) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.cancelInvoice(invoice, rtAction);
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
		if(invoice.getInvoiceLines() == null
				|| (invoice.getInvoiceLines() != null && invoice.getInvoiceLines().isEmpty())){
			throw new ActionForbiddenException("invoices with no invoice line can not be updated.") ;
		}
		invoiceApiService.update(invoice, invoiceMapper.toEntity(invoiceResource), invoiceResource);
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build())
                .build();
	}

	@Transactional
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
        return Response.ok().entity(buildSuccessResponse("invoice", invoiceMapper.toResource(adjInvoice))).build();
    }

    private Map<String, Object> buildSuccessResponse(String label, Object object) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put(label, object);
        return response;
    }
    
    @Override
    public Response duplicateInvoiceLines(Long id, InvoiceLinesToDuplicate invoiceLinesToDuplicate) {
        Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
        List<Long> idsInvoiceLineForInvoice = new ArrayList<>();
        for(InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
            idsInvoiceLineForInvoice.add(invoiceLine.getId());
        }
        int sizeInvoiceLineIds = invoiceLinesToDuplicate.getInvoiceLineIds().size();
        if(sizeInvoiceLineIds == 0){
            throw new MissingParameterException("The following parameters are required or contain invalid values: invoiceLineIds");
        }

        if(!InvoiceStatusEnum.NEW.equals(invoice.getStatus()) 
                && !InvoiceStatusEnum.DRAFT.equals(invoice.getStatus())
                && !InvoiceStatusEnum.SUSPECT.equals(invoice.getStatus())
                && !InvoiceStatusEnum.REJECTED.equals(invoice.getStatus())){
            throw new MeveoApiException("The invoice should have one of these statuses: NEW, DRAFT, SUSPECT or REJECTED");
        }
        invoice = invoiceApiService.duplicateInvoiceLines(invoice, invoiceLinesToDuplicate.getInvoiceLineIds());
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("invoice", invoice);
        return Response.ok(response).build();
    }

	@Override
	public Response quarantineInvoice(Long id, QuarantineBillingRunDto quarantineBillingRunDto) {
		Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
		if(!InvoiceStatusEnum.DRAFT.equals(invoice.getStatus())
                && !InvoiceStatusEnum.SUSPECT.equals(invoice.getStatus())
                && !InvoiceStatusEnum.REJECTED.equals(invoice.getStatus())) {
			throw new ActionForbiddenException("Only possible for invoices in DRAFT/REJECTED/SUSPECT statuses") ;
		}

		Long quarantineBillingRunId = invoiceApiService.quarantineInvoice(invoice, quarantineBillingRunDto);
		
		
		Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("quarantineBillingRunId", quarantineBillingRunId);
        return Response.ok(response).build();
	}

	@Override
	public Response refreshRate(Long invoiceId) {
		Optional<Invoice> refreshedInvoice = invoiceApiService.refreshRate(invoiceId);
		Map<String, Object> response;
		if(refreshedInvoice.isPresent()) {
			response = buildSuccessResponse("message",
					"Exchange rate successfully refreshed");
		} else {
			response = buildSuccessResponse("message",
					"Last applied rate and trading currency current rate are equals");
		}
		return Response.ok(response).build();
	}

	@Override
	public Response calculateSubTotals(Long invoiceId) {
		Invoice invoice = invoiceApiService.findById(invoiceId).orElseThrow(NotFoundException::new);
		try {
		var invoiceSubtotalsList = invoiceSubTotalsApiService.calculateSubTotals(invoice);
		  return Response
	                .created(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, invoice.getId()).build())
	                .entity(toResourceInvoiceSubTotalsWithLink(invoiceSubTotalMapper.toResources(invoiceSubtotalsList)))
	                .build();
		}catch(Exception e) {
		    throw new BusinessApiException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
		}
	}
	
	@Override
    public Response addSubTotals(InvoiceSubTotalsDto invoiceSubTotals) {	    
	    List<InvoiceSubTotals> lstInvoiceSubTotals = invoiceSubTotalsService.addSubTotals(invoiceSubTotals);
        Long invoiceTypeId = invoiceSubTotals.getInvoiceType().getId();
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(InvoiceTypeRs.class, invoiceTypeId).build())
                .entity(toResourceInvoiceSubTotalsWithLink(invoiceSubTotalMapper.toResources(lstInvoiceSubTotals)))
                .build();
    }
	
	@Override
    public Response deleteSubTotals(InvoiceSubTotalsDto invoiceSubTotals) {        
        invoiceSubTotalsService.deleteSubTotals(invoiceSubTotals);
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        return Response.ok(response).build();
    }
	
	private org.meveo.apiv2.billing.InvoiceSubTotals toResourceInvoiceSubTotalsWithLink(org.meveo.apiv2.billing.InvoiceSubTotals invoiceSubTotal) {
		return ImmutableInvoiceSubTotals.copyOf(invoiceSubTotal)
				.withLinks(new LinkGenerator.SelfLinkGenerator(InvoiceResource.class).withId(invoiceSubTotal.getId())
						.withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction().build());
	}
	
	private List<org.meveo.apiv2.billing.InvoiceSubTotals> toResourceInvoiceSubTotalsWithLink(List<org.meveo.apiv2.billing.InvoiceSubTotals> invoiceSubTotal) {
		var result = new ArrayList<org.meveo.apiv2.billing.InvoiceSubTotals>();
		if (invoiceSubTotal != null) {
            for (org.meveo.apiv2.billing.InvoiceSubTotals invoiceSubTotals : invoiceSubTotal) {
                result.add(toResourceInvoiceSubTotalsWithLink(invoiceSubTotals));
            }
        }
		return result;
	}

	@Override
    public Response updateValidateInvoice(Long id, InvoicePatchInput input) {
        final Invoice invoice = findValidatedInvoiceToUpdate(id);		
        invoiceApiService.updateValidatedInvoice(invoice, input);
        return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(InvoiceResource.class, id).build()).build();
    }

    /**
     * Find validated invoice to patch
     * @param id Invoice Id
     * @return {@link org.meveo.apiv2.billing.Invoice}
     */
    private Invoice findValidatedInvoiceToUpdate(Long id) {
        Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
        final InvoiceStatusEnum status = invoice.getStatus();

        if(!(VALIDATED.equals(status))) {
            throw new ActionForbiddenException("Can only patch VALIDATED invoice. Current invoice status is: " + status.name()) ;
        }

        return invoice;
    }
    
	@Override
	public Response setCustomRate(Long id, InvoiceExchangeRateInput input) {
		Invoice invoice = findInvoiceEligibleToUpdate(id);
		invoiceApiService.setInvoiceExchangeRate(invoice, input.getExchangeRate());
		return Response.ok().build();
	}

	@Override
	public Response validateInvoices(ValidateInvoiceDto validateInvoiceDto) {
		return Response.ok()
				.entity(invoiceApiService.validateInvoices(validateInvoiceDto.getFilters(),
						validateInvoiceDto.getMode(), validateInvoiceDto.getFailOnValidatedInvoice(),
						validateInvoiceDto.getFailOnCanceledInvoice(), validateInvoiceDto.getIgnoreValidationRules(),
						validateInvoiceDto.getGenerateAO()))
				.build();
	}
}