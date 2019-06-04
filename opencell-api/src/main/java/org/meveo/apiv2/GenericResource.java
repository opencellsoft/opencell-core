package org.meveo.apiv2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.apiv2.common.LinkGenerator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
            @ApiResponse(code = 200, message = "field succefully retrieved with hypermedia links"),
            @ApiResponse(code = 400, message = "bad request when input not well formed")
    })
    Response get(@PathParam("entityName") String entityName, @ApiParam("The id here is the database primary key of the wanted record") @PathParam("id") Long id,
           @ApiParam("requestDto carries the wanted fields ex: {fields = [code, description]}") GenericRequestDto requestDto);
    
    @ApiOperation(value = "Update a resource by giving it's name and Id", notes = "specify the entity name, the record id, and as body, the list of the fields to update")
    @ApiResponses({
            @ApiResponse(code = 200, message = "resource succefully updated but not content exposed except the hypermedia"),
            @ApiResponse(code = 400, message = "bad request when input not well formed")
    })
    @POST
    @Path("/{entityName}/{id}")
    Response update(@PathParam("entityName") String entityName, @ApiParam("The id here is the database primary key of the record to update") @PathParam("id") Long id,
            @ApiParam("dto the json representation of the object") String dto);
    
    default Link buildLink(String... params){
        return new LinkGenerator.SelfLinkGenerator(GenericResource.class)
                .withGetAction().withPostAction()
                .withDeleteAction().build(params);
    }
}
