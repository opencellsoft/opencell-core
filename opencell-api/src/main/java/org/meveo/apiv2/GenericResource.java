package org.meveo.apiv2;

import io.swagger.annotations.*;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/generic")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Retrieve some of the properties of your model with the entity name, record id and the requested fields"
        + "this is not an alternative to GrahQl, just a tool to simplify some objects retrieval."
        + "for example: if the wanted fields are {code, description} and the requested entity is instance of customer having 2 as id,"
        + "then you should call /admin/generic/Customer/2 and add {fields = [code, description]} in the request body")
public interface GenericResource {

    @GET
    @Path("/{entityName}/{id}")
    @ApiOperation(value = "Generic single endpoint to retrieve resources by ID", notes = "specify the entity name, the record id, and as body, the list of the wanted fields")
    @ApiResponses({
            @ApiResponse(code = 200, message = "field succefully retrieved"),
            @ApiResponse(code = 400, message = "bad request when input not well formed")
    })
    GenericResponseDto get(@PathParam("entityName") String entityName, @ApiParam("The id here is the database primary key of the wanted record") @PathParam("id") Long id,
           @ApiParam("requestDto carries the wanted fields ex: {fields = [code, description, address.city]}") GenericRequestDto requestDto);
}
