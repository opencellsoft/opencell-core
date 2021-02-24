package org.meveo.api.rest.cpq;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.ContractListResponsDto;
import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetMediaDtoResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * 
 * @author Tarik FA.
 */
@Path("/media")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface MediaRs {

	@POST
	@Operation(
			summary = "Create a new Media",
			tags = {"Media management"},
			description = "creation of a new Media",
			responses = {
					@ApiResponse(responseCode = "200", description = "The media is succeffully created", content = @Content(schema = @Schema(implementation = GetMediaDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "One of the requied parameters is missing ", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
		            @ApiResponse(responseCode = "302", description = "The media already exist", content = @Content(schema = @Schema(implementation = EntityAlreadyExistsException.class))),
		            @ApiResponse(responseCode = "404", description = "composed id of media does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
		   }
			)
	public Response createMedai(@Parameter(required = true, description = "information for a new media") MediaDto mediaDto);
	
	@PUT
	@Operation(
			summary = "update an existing Media",
			tags = {"Media management"},
			description = "update an exsiting  Media",
			responses = {
					@ApiResponse(responseCode = "200", description = "The media is succeffully updated", content = @Content(schema = @Schema(implementation = GetMediaDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "One of the requied parameters is missing ", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
		            @ApiResponse(responseCode = "404", description = "composed id of media does not exist or ServiceTemplate doesn't exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
		   }
			)
	public Response update(@Parameter(required = true, description = "updading for a new media") MediaDto mediaDto);

	@GET
	@Path("/{code}")
	@Operation(
			summary = "get an existing Media",
			tags = {"Media management"},
			description = "get an exsiting  Media",
			responses = {
					@ApiResponse(responseCode = "200", description = "The media is succeffully fetched", content = @Content(schema = @Schema(implementation = GetMediaDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "One of the requied parameters is missing ", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
		            @ApiResponse(responseCode = "404", description = "composed id of media does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
		   }
			)
	public Response findByCode(@Parameter(required = true, description = "media code") @PathParam("code") String code);

	@DELETE
	@Path("/{code}")
	@Operation(
			summary = "delete an existing Media",
			tags = {"Media management"},
			description = "delete an exsiting  Media",
			responses = {
					@ApiResponse(responseCode = "200", description = "The media is succeffully deleted", content = @Content(schema = @Schema(implementation = ActionStatus.class))),
					@ApiResponse(responseCode = "412", description = "One of the requied parameters is missing ", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
		            @ApiResponse(responseCode = "404", description = "composed id of media does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
		   }
			)
	public Response deleteMedia(@Parameter(required = true, description = "media code") @PathParam("code") String code);
 
	
	
}
