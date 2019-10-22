package org.meveo.api.rest.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.RefundActionStatus;
import org.meveo.api.dto.payment.RefundDto;
import org.meveo.api.rest.IBaseRs;

/**
 * The Interface RefundRs.
 */

@Path("/refund")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface RefundRs extends IBaseRs {

    /**
     * Creates refund.
     * 
     * @param postData Refund's data
     * @return payment action status
     */
    @POST
    @Path("/")
    public RefundActionStatus createRefund(RefundDto postData);

}
