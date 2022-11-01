package org.meveo.apiv2.billing.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.apiv2.billing.CdrDtoResponse;
import org.meveo.apiv2.billing.CdrListDtoInput;
import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.ChargeCdrListInput;
import org.meveo.apiv2.billing.ProcessCdrListResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/mediation/cdrs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MediationResource {

    @POST
    @Path("/registerCdrList")
    @Operation(summary = "Accepts a list of CDR lines, parses them and creates EDRs. CDR accepts the same format as in mediation job", tags = { "Mediation", "CDR", "EDR" }, responses = {
            @ApiResponse(responseCode = "200", description = "A list of EDRs, preserving the order of incomming CDRs"), @ApiResponse(responseCode = "400", description = "bad request on register CDR list") })
    ProcessCdrListResult registerCdrList(@Parameter(description = "the CdrListInput object", required = true) CdrListInput cdrListInput);

    @POST
    @Path("/reserveCdrList")
    @Operation(summary = "Accepts a list of CDR lines, parses them, creates EDRs and reserves. CDR accepts the same format as in mediation job", tags = { "Mediation", "CDR", "EDR" }, responses = {
            @ApiResponse(responseCode = "200", description = "A list of EDRs and reservations, preserving the order of incomming CDRs"),
            @ApiResponse(responseCode = "400", description = "bad request on reserve CDR list") })
    ProcessCdrListResult reserveCdrList(@Parameter(description = "the CdrListInput object", required = true) CdrListInput cdrListInput);

    @POST
    @Path("/chargeCdrList")
    @Operation(summary = "Accepts a list of CDR lines, parses them, creates EDRs and rates them. . CDR is same format use in mediation job", tags = { "Mediation", "CDR", "EDR" }, responses = {
            @ApiResponse(responseCode = "200", description = "A list of rated wallet operations, preserving the order of incomming CDRs"),
            @ApiResponse(responseCode = "400", description = "bad request on register CDR list") })
    ProcessCdrListResult chargeCdrList(@Parameter(description = "the ChargeCdrListInput object", required = true) ChargeCdrListInput cdrListInput);

    @POST
    @Operation(summary = "create manuel CDR", description = "allow to create a manuel cdr from api", operationId = "    POST_Mediation_processCdrList", responses = {
            @ApiResponse(description = " return new created CDR id ", content = @Content(schema = @Schema(implementation = ActionStatus.class))) })
    CdrDtoResponse createCDR(CdrListDtoInput cdrs);
    

    @PUT
    @Operation(summary = "update an existing  CDR", description = "allow to update a existing cdr from api allowed for CDR that hava status : ", operationId = "    POST_Mediation_processCdrList", responses = {
            @ApiResponse(description = " status of the operation ", content = @Content(schema = @Schema(implementation = ActionStatus.class))) })
    CdrDtoResponse updateCDR(CdrListDtoInput cdrDto);
}
