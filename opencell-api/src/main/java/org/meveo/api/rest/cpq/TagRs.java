package org.meveo.api.rest.cpq;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.cpq.TagTypeDto;
import org.meveo.api.dto.response.cpq.GetTagDtoResponse;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


/**
 * @author Tarik F.
 * @version 10.0
 *
 */
@Path("/cpq/tags")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TagRs extends IBaseRs {

	/**
	 * @param tagDto
	 * @return
	 */
	@POST
	@Path("/")
    @Operation(summary = "This endpoint allows to create new tag",
    tags = { "Tag" },
    description ="Creating a new tag",
    responses = {
            @ApiResponse(responseCode="200", description = "the Tag successfully added",
            		content = @Content(schema = @Schema(implementation = GetTagDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for TagDto.The required params are : code, name, tagTypeCode",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "400", description = "No tag type is found for the parameter tagTypeCode", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	
	Response createTag(@Parameter(description = "tag dto for a new insertion", required = true) TagDto tagDto);
	
	@PUT
	@Path("/")
    @Operation(summary = "This endpoint allows to update an existing Tag",
    description ="Updating an existing Tag",
    tags = { "Tag" },
    responses = {
            @ApiResponse(responseCode="200", description = "the Tag successfully updated",
            		content = @Content(schema = @Schema(implementation = GetTagDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for TagDto.The required params are : code, name, tagTypeCode",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "400", description = "No tag type is found for the parameter tagTypeCode", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response updateTag(@Parameter(description = "tag dto for updating an existing tag", required = true) TagDto tagDto);
	
	@DELETE
	@Path("/{codeTag}")
    @Operation(summary = "This endpoint allows to  delete an existing Tag",
    description ="Deleting an existing Tag with its code",
    tags = { "Tag" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Tag successfully deleted",
            		content = @Content(schema = @Schema(implementation = ActionStatusEnum.class))),
            @ApiResponse(responseCode = "400", description = "No Tag found for the codeTag parameter", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "400", description = "Impossible to delete a Tag, because it contains product", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response deleteTag(@Parameter(description = "contain the code of tag te be deleted by its code", required = true) @PathParam("codeTag") String codeTag);

	@GET
	@Path("/")
    @Operation(summary = "This endpoint allows to find a tag by codeTag parameter",
    description ="Find a tag type by codeTag parameter",
    tags = { "Tag" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Tag successfully retrieved",
            		content = @Content(schema = @Schema(implementation = GetTagDtoResponse.class))),
    })
	Response findByCode(@Parameter(description = "retrieving a tag with its code") @QueryParam("codeTag") String codeTag);
	
	@POST
	@Path("/tagType")
    @Operation(summary = "This endpoint allows to create new tag type",
    description ="Creating a new tag type",
    tags = { "Tag" },
    responses = {
            @ApiResponse(responseCode="200", description = "the Tag type successfully added"),
            @ApiResponse(responseCode = "412", description = "missing required paramter for TagDto.The required parameter is  code",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class)))
    })
	Response createTagType(@Parameter(description = "tag type dto for new insertion", required = true)TagTypeDto tagTypeDto);
	
	@PUT
	@Path("/tagType")
    @Operation(summary = "This endpoint allows to update new tag type",
    description ="updating a new tag type",
    tags = { "Tag" },
    responses = {
            @ApiResponse(responseCode="200", description = "the Tag type successfully updated"),
            @ApiResponse(responseCode = "412", description = "missing required paramter for TagDto.The required parameter is  code",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "400", description = "No tag type was found with the code", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response updateTagType(@Parameter(description = "tag type dto for updating an existing tag type", required = true) TagTypeDto tagTypeDto);
	
	@GET
	@Path("/tagType")
    @Operation(summary = "This endpoint allows to retrieve a tag type",
    description ="Retrieving a tag type with code tag type parameter",
    tags = { "Tag" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Tag type successfully found"),
            @ApiResponse(responseCode = "400", description = "No tag type was found with the code", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response findTagTypeBycode(@Parameter(description = "code tag type for retrieving an existing one", required = true) @QueryParam("codeTagType") String codeTagType);


	@DELETE
	@Path("/{codeTagType}")
    @Operation(summary = "This endpoint allows to  delete an existing Tag type",
    description ="Deleting an existing Tag type",
    tags = { "Tag" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Tag type successfully deleted",
            		content = @Content(schema = @Schema(implementation = ActionStatusEnum.class))),
            @ApiResponse(responseCode = "404", description = "No Tag type found for the tagCode parameter", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "400", description = "Impossible to delete a Tag type,it is attached to a Tag", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response deleteTagType(@Parameter(description = "tagCode", required = true) @PathParam("codeTagType") String tagCode);
	
}
