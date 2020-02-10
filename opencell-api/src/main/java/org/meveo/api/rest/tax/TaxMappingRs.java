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
import org.meveo.api.dto.response.tax.TaxMappingListResponseDto;
import org.meveo.api.dto.response.tax.TaxMappingResponseDto;
import org.meveo.api.dto.tax.TaxMappingDto;
import org.meveo.api.rest.IBaseRs;

/**
 * REST interface definition of Tax mapping API
 **/
@Path("/taxMapping")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface TaxMappingRs extends IBaseRs {

    /**
     * Create a new Tax mapping
     * 
     * @param dto The Tax mapping's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(TaxMappingDto dto);

    /**
     * Search for a Tax mapping with a given id
     * 
     * @param id The Tax mapping's id
     * @return A Tax mapping's data
     */
    @GET
    @Path("/")
    TaxMappingResponseDto find(@QueryParam("id") String id);

    /**
     * Search Tax mapping by matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of Tax mappings
     */
    @GET
    @Path("/list")
    public TaxMappingListResponseDto searchGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Search for Tax mapping by matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of Tax mappings
     */
    @POST
    @Path("/list")
    public TaxMappingListResponseDto searchPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Update an existing Tax mapping
     * 
     * @param dto The Tax mapping's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(TaxMappingDto dto);

    /**
     * Remove an existing Tax mapping with a given id
     * 
     * @param id The Tax mapping's id
     * @return Request processing status
     */
    @DELETE
    @Path("/{id}")
    public ActionStatus remove(@PathParam("id") String id);

    /**
     * Create new or update an existing Tax mapping
     * 
     * @param dto The Tax mapping's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(TaxMappingDto dto);
}