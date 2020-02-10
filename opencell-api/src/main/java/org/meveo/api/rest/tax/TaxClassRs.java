package org.meveo.api.rest.tax;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.tax.TaxClassListResponseDto;
import org.meveo.api.dto.response.tax.TaxClassResponseDto;
import org.meveo.api.dto.tax.TaxClassDto;
import org.meveo.api.rest.IBaseRs;

/**
 * REST interface definition of Tax class API
 **/
@Path("/taxClass")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface TaxClassRs extends IBaseRs {

    /**
     * Create a new Tax class
     * 
     * @param dto The Tax class's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(TaxClassDto dto);

    /**
     * Search for a Tax class with a given code
     * 
     * @param code The Tax class's code
     * @return A Tax class's data
     */
    @GET
    @Path("/")
    TaxClassResponseDto find(@QueryParam("code") String code);

    /**
     * Search Tax class by matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of Tax classs
     */
    @GET
    @Path("/list")
    public TaxClassListResponseDto searchGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Search for Tax class by matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of Tax classs
     */
    @POST
    @Path("/list")
    public TaxClassListResponseDto searchPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Update an existing Tax class
     * 
     * @param dto The Tax class's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(TaxClassDto dto);

    /**
     * Remove an existing Tax class with a given code
     * 
     * @param code The Tax class's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
    public ActionStatus remove(@PathParam("code") String code);

    /**
     * Create new or update an existing Tax class
     * 
     * @param dto The Tax class's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(TaxClassDto dto);
}