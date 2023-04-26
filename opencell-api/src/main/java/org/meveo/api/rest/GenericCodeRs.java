package org.meveo.api.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.custom.GenericCodeDto;
import org.meveo.api.dto.custom.GenericCodeResponseDto;
import org.meveo.api.dto.custom.GetGenericCodeResponseDto;
import org.meveo.api.dto.custom.SequenceDto;

import javax.ws.rs.*;

@Path("/genericCode")
@Tag(name = "GenericCode", description = "@%GenericCode")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface GenericCodeRs extends IBaseRs {

    /** 
     * create generic code
     * 
     * @param codeDto
     * @return ActionStatus status of the API web service response
     */
    @POST
    @Path("/")
	@Operation(
			summary="  create  ",
			description="  create  ",
			operationId="    POST_GenericCode_create",
			responses= {
				@ApiResponse(description="ActionStatus response",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(GenericCodeDto codeDto);

    /**
     * update generic code
     * 
     * @param codeDto
     * @return ActionStatus status of the API web service response
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" update  ",
			description=" update  ",
			operationId="    PUT_GenericCode_update",
			responses= {
				@ApiResponse(description="ActionStatus response",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(GenericCodeDto codeDto);

    /**
     * Find a specific generic code using entity class
     * 
     * @param entityClass
     * @return GetGenericCodeResponseDto
     */
    @GET
    @Path("/")
	@Operation(
			summary=" find  ",
			description=" find  ",
			operationId="    GET_GenericCode_search",
			responses= {
				@ApiResponse(description="GetGenericCodeResponseDto response",
						content=@Content(
									schema=@Schema(
											implementation= GetGenericCodeResponseDto.class
											)
								)
				)}
	)
    GetGenericCodeResponseDto find(@QueryParam("entityClass") String entityClass);

    /**
     * Generate generic code
     * 
     * @param codeDto
     * @return GenericCodeResponseDto : generated code & sequence type + pattern
     */
    @POST
    @Path("/generateCode")
	@Operation(
			summary=" getGenericCode  ",
			description=" getGenericCode  ",
			operationId="    POST_GenericCode_generateCode",
			responses= {
				@ApiResponse(description="GenericCodeResponseDto response",
						content=@Content(
									schema=@Schema(
											implementation= GenericCodeResponseDto.class
											)
								)
				)}
	)
    GenericCodeResponseDto getGenericCode(GenericCodeDto codeDto);

    /**
     * createSequence
     * 
     * @param sequenceDto
     * @return ActionStatus status of the API web service response
     */
    @POST
    @Path("/sequence/")
	@Operation(
			summary=" createSequence  ",
			description=" createSequence  ",
			operationId="    POST_GenericCode_sequence_",
			responses= {
				@ApiResponse(description="ActionStatus response",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createSequence(SequenceDto sequenceDto);


	/**
	 * Create or update generic code
	 *
	 * @param genericCodeDto
	 * @return ActionStatus status of the API web service response
	 */
	@POST
	@Path("/createOrUpdate")
	@Operation(
			summary = "Create or update generic code",
			description = "Create  or update",
			operationId = "POST_GenericCode_create_or_update",
			responses = {
					@ApiResponse(description = "ActionStatus response",
							content = @Content(
									schema = @Schema(
											implementation = ActionStatus.class
									)
							)
					)}
	)
	ActionStatus createOrUpdate(GenericCodeDto genericCodeDto);
}
