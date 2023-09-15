package org.meveo.apiv2.payments.resource;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.billing.resource.InvoiceResource;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.payments.PaymentPlanDto;
import org.meveo.apiv2.payments.service.PaymentPlanApi;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

@Interceptors({ WsRestApiInterceptor.class })
public class PaymentPlanResourceImpl implements PaymentPlanResource {

    @Inject
    private PaymentPlanApi paymentPlanApi;

    @Override
    public Response create(PaymentPlanDto paymentPlanDto) {
        Long createdPPId = paymentPlanApi.create(paymentPlanDto);

        ActionStatus createdStatus = new ActionStatus();
        createdStatus.setStatus(ActionStatusEnum.SUCCESS);
        createdStatus.setEntityId(createdPPId);

        return Response.created(LinkGenerator.getUriBuilderFromResource(PaymentPlanResource.class, createdPPId).build())
                .entity(createdStatus).build();
    }

    @Override
    public Response update(Long id, PaymentPlanDto paymentPlanDto) {
        Long updatedPPId = paymentPlanApi.update(id, paymentPlanDto);

        ActionStatus updatedStatus = new ActionStatus();
        updatedStatus.setStatus(ActionStatusEnum.SUCCESS);
        updatedStatus.setEntityId(updatedPPId);

        return Response.ok().entity(updatedStatus).build();
    }

    @Override
    public Response delete(Long id) {
        paymentPlanApi.delete(id);

        ActionStatus deletedStatus = new ActionStatus();
        deletedStatus.setStatus(ActionStatusEnum.SUCCESS);

        return Response.ok().entity(deletedStatus).build();
    }

    @Override
    public Response activate(Long id) {
        paymentPlanApi.activate(id);

        ActionStatus createdStatus = new ActionStatus();
        createdStatus.setStatus(ActionStatusEnum.SUCCESS);
        createdStatus.setEntityId(id);

        return Response.ok().entity(createdStatus).build();
    }
}
