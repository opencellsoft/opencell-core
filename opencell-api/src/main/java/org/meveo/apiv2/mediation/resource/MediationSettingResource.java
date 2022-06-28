package org.meveo.apiv2.mediation.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.mediation.EdrVersioningRule;
import org.meveo.apiv2.mediation.EdrVersioningRuleSwapping;
import org.meveo.apiv2.mediation.MediationSetting;
import org.meveo.apiv2.models.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/*
 * @author Tarik FA.
 * @version 13.0.0
 * @category Médiation
 */
@Path("/mediationSetting")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MediationSettingResource {

	@POST
	@Operation(summary = "Create new Mediation Setting",
	   tags = { "Mediation Settings" },
	   description ="create new mediation setting to enabling or disabling edr versioning",
	   responses = {
	            @ApiResponse(responseCode="200", description = "new mediation setting is created"),
	            @ApiResponse(responseCode = "404", description = "missing paramters",  content = @Content(schema = @Schema(implementation = ApiException.class))),
	            @ApiResponse(responseCode = "400", description = "the setting of mediation already exist")
	    })
	Response create(MediationSetting mediationSetting);
	
	@PUT
	@Path("/{mediationRuleId}")
	@Operation(summary = "Update new Mediation Setting",
	   tags = { "Mediation Settings" },
	   description ="update an existing mediation setting to enabling or disabling edr versioning",
	   responses = {
	            @ApiResponse(responseCode="200", description = "An existing mediation setting is updated"),
	            @ApiResponse(responseCode = "404", description = "missing paramters",
	                    content = @Content(schema = @Schema(implementation = ApiException.class)))            
	    })
	Response update(@PathParam("mediationRuleId") Long mediationRuleId, MediationSetting mediationSetting);
	

	@POST
	@Path("/edrVersioningRule")
	@Operation(summary = "Create new Edr Version rule",
	   tags = { "Mediation Settings" },
	   description ="create new Edr Version rule",
	   responses = {
	            @ApiResponse(responseCode="200", description = "new Edr Version rule is created"),
	            @ApiResponse(responseCode = "404", description = "missing paramters",  content = @Content(schema = @Schema(implementation = ApiException.class)))
	    })
	Response createEdrVersionRule(EdrVersioningRule edrVersioningRule);
	

	@PUT
	@Path("/edrVersioningRule/{edrVersionRuleId}")
	@Operation(summary = "Update new Edr Version rule",
	   tags = { "Mediation Settings" },
	   description ="upate an existing Edr Version rule",
	   responses = {
	            @ApiResponse(responseCode="200", description = "Edr Version rule is updated"),
	            @ApiResponse(responseCode = "404", description = "missing paramters",  content = @Content(schema = @Schema(implementation = ApiException.class)))
	    })
	Response updateEdrVersionRule(@PathParam("edrVersionRuleId") Long edrVersionRuleId, EdrVersioningRule edrVersioningRule);
	

	@POST
	@Path("/edrVersioningRule/swapPriority")
	@Operation(summary = "Update new Edr Version rule",
	   tags = { "Mediation Settings" },
	   description ="upate an existing Edr Version rule",
	   responses = {
	            @ApiResponse(responseCode="200", description = "Edr Version rule is updated"),
	            @ApiResponse(responseCode = "404", description = "missing paramters",  content = @Content(schema = @Schema(implementation = ApiException.class)))
	    })
	Response swapPriority(EdrVersioningRuleSwapping edrVersioningRuleSwapping);
	

	@DELETE
	@Path("/edrVersioningRule/{edrVersionRuleId}")
	@Operation(summary = "Remove new Edr Version rule",
	   tags = { "Mediation Settings" },
	   description ="remove an existing Edr Version rule",
	   responses = {
	            @ApiResponse(responseCode="200", description = "Edr Version rule is removed"),
	            @ApiResponse(responseCode = "400", description = "Edr Version doesn't exist",  content = @Content(schema = @Schema(implementation = NotFoundException.class)))
	    })
	Response deleteEdrVersioningRule(@PathParam("edrVersionRuleId") Long edrVersionRuleId);
}
