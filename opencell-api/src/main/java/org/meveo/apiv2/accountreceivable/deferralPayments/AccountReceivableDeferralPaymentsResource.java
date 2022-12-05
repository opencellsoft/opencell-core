package org.meveo.apiv2.accountreceivable.deferralPayments;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.AcountReceivable.DeferralPayments;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;

@Path("/accountReceivable/deferralPayments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AccountReceivableDeferralPaymentsResource {
    @POST
    @Path("/")
    @Operation(summary = "Create a defferal Payment",
            tags = {"Post"},
            description = "Create a defferal Payment",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "defferal Payment is successfully created"),
                    @ApiResponse(responseCode = "403",
                            description = "user has not habilitation to create a paymentDeferral."),
                    @ApiResponse(responseCode = "422",
                            description = "maximum deferral count per invoice is exceeded."),
                    @ApiResponse(responseCode = "409",
                            description = "payment deferral already exists ")
            })
    Response create(@Parameter(required = true, description = "Deferral Payments information") DeferralPayments deferralPayments);

}
