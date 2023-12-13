package org.meveo.apiv2.accountreceivable.accountOperation;


import java.util.Map;
import java.util.Set;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.AcountReceivable.*;
import org.meveo.apiv2.accountreceivable.ChangeStatusDto;

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


	/**
	 * @since 13.0
	 * @param unMatchingAO contains data for AccountOperation for unMatching
	 * @return UnMatching result
	 */
	@POST
	@Path("/unMatchOperations")
	@Operation(summary = "API to match Account operations",
			tags = {"Post"},
			description = "Process unMatching for AccountOperations",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successfully matched"),
					@ApiResponse(responseCode = "404", description = "Entity does not exist"),
					@ApiResponse(responseCode = "400", description = "Matching action is failed")
			})
	Response unMatchOperations(UnMatchingAccountOperation unMatchingAO);

	/**
	 * @param accountOperationId account operation to set litigation
	 * @param litigationInput      litigation input
	 * @return Response
	 */
	@PUT
	@Path("/{id}/litigation")
	@Operation(summary = "Set litigation matching status on an account operation",
			description = "Set litigation matching status on an account operation",
			responses = {
					@ApiResponse(responseCode = "200", description = "litigation successfully set"),
					@ApiResponse(responseCode = "404", description = "Account operations don't exist")})
	Response setLitigation(@PathParam("id") Long accountOperationId,
						   @Parameter(description = "Litigation dto", required = true) LitigationInput litigationInput);
}
