package org.meveo.apiv2.billing.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.CdrListResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/mediation/cdrs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MediationResource {

    @POST
    @Path("/registerCdrList")
    @Operation(summary = "Accepts a list of CDR line. This CDR is parsed and created as EDR. CDR is same format use in mediation job", tags = { "Mediation", "CDR",
            "EDR" }, responses = { @ApiResponse(responseCode = "200", description = "the register CDR list successfully processed"),
                    @ApiResponse(responseCode = "400", description = "bad request on register CDR list") })
    CdrListResult registerCdrList(@Parameter(description = "the CdrListInput object", required = true) CdrListInput cdrListInput);
}