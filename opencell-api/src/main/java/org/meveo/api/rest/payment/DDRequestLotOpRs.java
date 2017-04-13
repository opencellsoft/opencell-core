package org.meveo.api.rest.payment;

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
import org.meveo.model.payments.DDRequestOpStatusEnum;

@Path("/payment/ddrequestLotOp")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DDRequestLotOpRs extends IBaseRs {

	/**
	 * Create a ddrequestLotOp by dto
     *
	 * @param dto
	 * @return
	 */
    @POST
    @Path("/")
    ActionStatus create(DDRequestLotOpDto dto);

    /**
     * List ddrequestLotOps by fromDueDate,toDueDate,status
     *
     * @param fromDueDate
     * @param toDueDate
     * @param status
     * @return
     */
    @GET
    @Path("/list")
    DDRequestLotOpsResponseDto list(@QueryParam("fromDueDate")String fromDueDate,@QueryParam("toDueDate")String toDueDate,@QueryParam("status")DDRequestOpStatusEnum status);

}
