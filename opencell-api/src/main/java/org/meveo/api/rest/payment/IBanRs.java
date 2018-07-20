package org.meveo.api.rest.payment;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
     * validate the iban
     *
     * @param iban
     * @return validation status
     */

    @GET
    @Path("/validate")
    ActionStatus validate(@QueryParam("iban") String iban);

}
