package org.meveo.apiv2.accountreceivable;


import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.AcountReceivable.CustomerAccountInput;
import org.meveo.apiv2.AcountReceivable.MatchingAccountOperation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

	/**
	 * @param id
	 * @return
	 */
	@PUT
	@Path("/changeStatus")
	@Operation(summary = "mark account operation as exported",  description = "mark account operation as exported",
	responses = {
	@ApiResponse(responseCode = "200", description = "Account operations status are successfully updated"),
	@ApiResponse(responseCode = "404", description = "Account operations don't exist"),
	@ApiResponse(responseCode = "409", description = "Status of account operations can not be updated") })
	Response markExported(@Parameter(description = "id of the Invoice", required = true ) ChangeStatusDto changeStatusDto);
	
	@POST
	@Path("/assignOperation/{id}")
	@Operation(summary = "Assign an account operation to a customer", tags = {
			"AccountOperation"}, description = "Assign an account operation to a customer", responses = {
			@ApiResponse(responseCode = "200", description = "Account operation is successfully assigned"),
			@ApiResponse(responseCode = "400", description = "Action is failed"),
			@ApiResponse(responseCode = "404", description = "Entity does not exist"),
			@ApiResponse(responseCode = "412", description = "Missing parameters")})
	Response assignAccountOperation(@Parameter(description = "Account operation id", required = true) @PathParam("id")
											Long accountOperationId,
									@Parameter(description = "Customer account", required = true)
											CustomerAccountInput customerAccount);

	/**
	 * @since 13.0
	 * @param matchingAO contains data for AccountOperation and Sequence for matching
	 * @return Matching result
	 */
	@POST
	@Path("/matchOperations")
	@Operation(summary = "API to match Account operations",
			tags = {"Post"},
			description = "Process matching for AccountOperations",
			responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully matched"),
					@ApiResponse(responseCode = "404", description = "Entity does not exist"),
                    @ApiResponse(responseCode = "412", description = "Missing parameters"),
                    @ApiResponse(responseCode = "400", description = "Matching action is failed")
			})
	Response matchOperations(MatchingAccountOperation matchingAO);

}
