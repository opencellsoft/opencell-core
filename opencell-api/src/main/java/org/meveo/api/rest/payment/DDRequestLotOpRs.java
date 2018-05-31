package org.meveo.api.rest.payment;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.response.payment.DDRequestLotOpsResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.payments.DDRequestOpStatusEnum;

@Path("/payment/ddrequestLotOp")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DDRequestLotOpRs extends IBaseRs {

	/**
	 * Create a ddrequestLotOp by dto
     *
	 * @param dto DDRequestLotOp Dto
	 * @return Action status
	 */
    @POST
    @Path("/")
    ActionStatus create(DDRequestLotOpDto dto);

    /**
     * List ddrequestLotOps by fromDueDate,toDueDate,status
     *
     * @param fromDueDate Start of search due date interval
     * @param toDueDate End of search due date interval
     * @param status DDRequestOp status
     * @return DDRequestLotOps response 
     */
    @GET
    @Path("/list")
    DDRequestLotOpsResponseDto list(@QueryParam("fromDueDate") @RestDateParam Date fromDueDate,@QueryParam("toDueDate") @RestDateParam Date toDueDate,@QueryParam("status") DDRequestOpStatusEnum status);

}
