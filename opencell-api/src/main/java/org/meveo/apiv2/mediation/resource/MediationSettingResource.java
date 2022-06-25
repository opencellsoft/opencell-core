package org.meveo.apiv2.mediation.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	            @ApiResponse(responseCode = "404", description = "missing paramters",
	                    content = @Content(schema = @Schema(implementation = ApiException.class)))            
	    })
	Response create(MediationSetting mediationSetting);
}
