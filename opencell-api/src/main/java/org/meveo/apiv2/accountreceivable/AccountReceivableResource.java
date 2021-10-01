package org.meveo.apiv2.accountreceivable;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;

@Path("/accountReceivable/accountOperation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AccountReceivableResource {
    @POST
    @Path("/post")
    @Operation(summary = "Refund By SCT",
            tags = {"Post"},
            description = "set the accountingDate field by the value of transactionDate, and set account operations status to POSTED",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Account operations are successfully posted"),
                    @ApiResponse(responseCode = "404",
                            description = "Following account operations does not exist : {accountOperation ids}"),
                    @ApiResponse(responseCode = "409",
                            description = "the sub-accounting period of following account operations are already closed : {accountOperation ids}")
            })
    Response post(Map<String, Set<Long>> accountOperations);

    @POST
    @Path("/forcePosting")
    @Operation(summary = "Refund By SCT",
            tags = {"Post"},
            description = "set the accountingDate field by the value of transactionDate, and set account operations status to POSTED",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Account operations are successfully posted"),
                    @ApiResponse(responseCode = "404",
                            description = "Following account operations does not exist : {accountOperation ids}")
            })
    Response forcePosting(Map<String, Set<Long>> accountOperations);
}
