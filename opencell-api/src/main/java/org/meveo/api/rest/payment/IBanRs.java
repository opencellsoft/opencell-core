package org.meveo.api.rest.payment;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 */
@Path("/iban")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface IBanRs extends IBaseRs {

    /**
     * validate the iban and/or bic
     *
     * @param iban the International Bank Account Number to validate
     * @param bic the Bank Identifier Code to validate
     * @return the status of the validation
     */

    @GET
    @Path("/validate")
    ActionStatus validate(@QueryParam("iban") String iban, @QueryParam("bic") String bic);

}
