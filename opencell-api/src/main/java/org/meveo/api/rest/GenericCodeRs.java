package org.meveo.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.custom.GenericCodeDto;
import org.meveo.api.dto.custom.GenericCodeResponseDto;
import org.meveo.api.dto.custom.GetGenericCodeResponseDto;
import org.meveo.api.dto.custom.SequenceDto;

import javax.ws.rs.*;

/**
 *
 */
@Path("/genericCode")
@Tag(name = "GenericCode", description = "@%GenericCode")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface GenericCodeRs extends IBaseRs {

    /** 
     * create
     * 
     * @param codeDto
     * @return
     */
    @POST
    @Path("/")
	@Operation(
			summary="  create  ",
			description="  create  ",
			operationId="    POST_GenericCode_create",
			responses= {
				@ApiResponse(description=" ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(GenericCodeDto codeDto);

    /**
     * update
     * 
     * @param codeDto
     * @return
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" update  ",
			description=" update  ",
			operationId="    PUT_GenericCode_update",
			responses= {
				@ApiResponse(description=" ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(GenericCodeDto codeDto);

    /**
     * find
     * 
     * @param entityClass
     * @return
     */
    @GET
    @Path("/")
	@Operation(
			summary=" find  ",
			description=" find  ",
			operationId="    GET_GenericCode_search",
			responses= {
				@ApiResponse(description=" ",
						content=@Content(
									schema=@Schema(
											implementation= GetGenericCodeResponseDto.class
											)
								)
				)}
	)
    GetGenericCodeResponseDto find(@QueryParam("entityClass") String entityClass);

    /**
     * getGenericCode
     * 
     * @param codeDto
     * @return
     */
    @POST
    @Path("/generateCode")
	@Operation(
			summary=" getGenericCode  ",
			description=" getGenericCode  ",
			operationId="    POST_GenericCode_generateCode",
			responses= {
				@ApiResponse(description=" ",
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
     * @return
     */
    @POST
    @Path("/sequence/")
	@Operation(
			summary=" createSequence  ",
			description=" createSequence  ",
			operationId="    POST_GenericCode_sequence_",
			responses= {
				@ApiResponse(description=" ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createSequence(SequenceDto sequenceDto);
}
