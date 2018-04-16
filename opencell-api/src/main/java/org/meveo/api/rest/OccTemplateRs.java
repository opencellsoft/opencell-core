package org.meveo.api.rest;

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
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.dto.response.GetOccTemplateResponseDto;
import org.meveo.api.dto.response.GetOccTemplatesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Path("/occTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OccTemplateRs extends IBaseRs {

    /**
     * Create OccTemplate.
     * 
     * @param postData posted data to API (account operation template)
     * @return action status.
     */
    @Path("/")
    @POST
    ActionStatus create(OccTemplateDto postData);

    /**
     * Update OccTemplate.
     * 
     * @param postData posted data to API
     * @return action status.
     */
    @Path("/")
    @PUT
    ActionStatus update(OccTemplateDto postData);

    /**
     * Search OccTemplate with a given code.
     * 
     * @param occtemplateCode  code of account operation template
     * @return account operation template
     */
    @Path("/")
    @GET
    GetOccTemplateResponseDto find(@QueryParam("occTemplateCode") String occtemplateCode);

    /**
     * Remove OccTemplate with a given code.
     * 
     * @param occTemplateCode code of account operation template
     * @return action status.
     */
    @Path("/{occTemplateCode}")
    @DELETE
    ActionStatus remove(@PathParam("occTemplateCode") String occTemplateCode);

    /**
     * Create or update OccTemplate.
     * 
     * @param postData posted data
     * @return action status.
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(OccTemplateDto postData);
  
    /**
     * Get List of OccTemplates matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of account operations
     */
    @GET
    @Path("/list")
    public GetOccTemplatesResponseDto listGet(@QueryParam("query") String query,
            @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("accountCode") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Get List of OccTemplates matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of account operations
     */
    @POST
    @Path("/list")
    public GetOccTemplatesResponseDto listPost(PagingAndFiltering pagingAndFiltering);

}
