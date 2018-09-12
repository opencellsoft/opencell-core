package org.meveo.api.rest.crm;

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
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.crm.ContactsResponseDto;
import org.meveo.api.dto.response.crm.GetContactResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

@Path("/contact")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ContactRs extends IBaseRs {
	@POST
    @Path("/")
    ActionStatus create(ContactDto postData);
	
    @PUT
    @Path("/")
    ActionStatus update(ContactDto postData);
    
    @GET
    @Path("/")
    GetContactResponseDto find(@QueryParam("code") String code);
    
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ContactDto postData);

    @PUT
    @Path("/{code}/{tag}")
    ActionStatus addTag(@PathParam("code") String code, @PathParam("tag") String tag);
    
    @DELETE
    @Path("/{code}/{tag}")
    ActionStatus removeTag(@PathParam("code") String code, @PathParam("tag") String tag);
    
    /**
     * List contacts matching a given criteria
     *
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of contacts
     */
    @GET
    @Path("/list")
    public ContactsResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);
    
    @POST
    @Path("/list")
    public ContactsResponseDto listPost(PagingAndFiltering pagingAndFiltering);
    
    @POST
    @Path("/importLinkedInFromText")
    ContactsResponseDto importLinkedInFromText(String context);
    
    @POST
    @Path("/importLinkedInFile")
    ActionStatus importLinkedInFile();
       
 }
