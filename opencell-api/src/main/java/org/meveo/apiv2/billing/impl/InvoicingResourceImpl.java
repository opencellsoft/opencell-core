package org.meveo.apiv2.billing.impl;

import static javax.ws.rs.core.Response.ok;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.billing.CancelBillingRunInput;
import org.meveo.apiv2.billing.ExceptionalBillingRun;
import org.meveo.apiv2.billing.ImmutableExceptionalBillingRun;
import org.meveo.apiv2.billing.resource.InvoicingResource;
import org.meveo.apiv2.billing.service.BillingRunApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.billing.impl.InvoiceTypeService;

public class InvoicingResourceImpl implements InvoicingResource {

    @Inject
    private BillingRunApiService invoicingApiService;
    
    @Inject
    private InvoiceTypeService invoiceTypeService;

    private final InvoicingMapper mapper = new InvoicingMapper();

    @Override
    public Response createExceptionalBillingRuns(ExceptionalBillingRun billingRun) {
        if (billingRun.getFilters() == null || billingRun.getFilters().isEmpty()) {
            throw new BadRequestException("Filters are required");
        }

        if (billingRun.getBillingRunTypeEnum() == null) {
            throw new BadRequestException("Billing run type is missing");
        }
        org.meveo.model.billing.BillingRun entity = mapper.toEntity(billingRun);
        if(billingRun.getInvoiceType() != null) {
        	InvoiceType invoiceType=invoiceTypeService.findByCode(billingRun.getInvoiceType());
        	if(invoiceType==null) {
        		throw new EntityDoesNotExistsException("InvoiceType", billingRun.getInvoiceType());
        	}
        	entity.setInvoiceType(invoiceType);
        }
		org.meveo.model.billing.BillingRun billingRunEntity = invoicingApiService.create(entity);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(InvoicingResource.class, billingRunEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(billingRunEntity)))
                .build();
    }

    private ImmutableExceptionalBillingRun toResourceOrderWithLink(ExceptionalBillingRun exceptionBillingRun) {
        return ImmutableExceptionalBillingRun.copyOf(exceptionBillingRun)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(InvoicingResource.class)
                                .withId(exceptionBillingRun.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }

    @Override
    public Response advanceStatus(Long billingRunId, boolean executeInvoicingJob) {
        BillingRun billingRun = invoicingApiService.advancedStatus(billingRunId, executeInvoicingJob)
                .orElseThrow(() -> new NotFoundException("Billing run with id " + billingRunId + " does not exists"));
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Advance billing run status successfully executed\"},\"id\":" + billingRun.getId() + ",\"billingRunStatus\": " + billingRun.getStatus() + ", \"executeInvoicingJob\": " + executeInvoicingJob + "}")
                .build();
    }
    
    @Override
    public Response cancelBillingRun(Long billingRunId, CancelBillingRunInput input) {
        BillingRun billingRun = invoicingApiService.cancelBillingRun(billingRunId, input.getRatedTransactionAction())
                .orElseThrow(() -> new NotFoundException("Billing run with id " + billingRunId + " does not exists"));
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Billing run status successfully canceled\"},\"id\":" + billingRun.getId() + "}")
                .build();
    }

    @Override
    public Response closeInvoiceLines(Long billingRunId, boolean executeInvoicingJob) {
        BillingRun billingRun = invoicingApiService.closeInvoiceLines(billingRunId, executeInvoicingJob)
                .orElseThrow(() -> new NotFoundException("Billing run with id " + billingRunId + " does not exists"));
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Update billing run status successfully\"},\"id\":"
                        + billingRun.getId() + ",\"billingRunStatus\": " + billingRun.getStatus()
                        + ", \"executeInvoicingJob\": " + executeInvoicingJob + "}")
                .build();
    }

	@Override
	public Response enableBillingRun(Long billingRunId) {
		BillingRun billingRun = invoicingApiService.enableBillingRun(billingRunId)
                .orElseThrow(() -> new NotFoundException("Billing run with id " + billingRunId + " does not exists"));
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Billing run successfully enabled\"},\"id\":" + billingRun.getId() + "}")
                .build();
	}

	@Override
	public Response disableBillingRun(Long billingRunId) {
		BillingRun billingRun = invoicingApiService.disableBillingRun(billingRunId)
                .orElseThrow(() -> new NotFoundException("Billing run with id " + billingRunId + " does not exists"));
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Billing run successfully disabled\"},\"id\":" + billingRun.getId() + "}")
                .build();
	}
}