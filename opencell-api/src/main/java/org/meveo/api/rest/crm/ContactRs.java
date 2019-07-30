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

    /**
     * Create a new CRM Contact 
     *
     * @param postData The contact data 
     * @return Request processing status
     */
	@POST
    @Path("/")
    ActionStatus create(ContactDto postData);
	
    /**
     * Update a contact CRM informatopn
     *
     * @param postData The contact data information
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(ContactDto postData);
    
    /**
     * Find a contact with a given code
     *
     * @param code The code of the contact
     * @return Request processing status
     */
    @GET
    @Path("/")
    GetContactResponseDto find(@QueryParam("code") String code);
    
    /**
     * Delete a contact with a given code
     *
     * @param code The code of the contact
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    /**
     * Create or update a CRM contact 
     *
     * @param postData The contact data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ContactDto postData);

    /**
     * Add a tag to a contact with his code
     *
     * @param code The code of the Contact.
     * @param tag The tag to add to the contact
     * @return Request processing status
     */
    @PUT
    @Path("/{code}/{tag}")
    ActionStatus addTag(@PathParam("code") String code, @PathParam("tag") String tag);
    
    /**
     * Delete a tag to a contact with his code
     *
     * @param code The code of the Contact.
     * @param tag The tag to add to the contact
     * @return Request processing status
     */
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
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return List of contacts
     */
    @GET
    @Path("/list")
     ContactsResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);
    
    /**
     * Retrieve a list by using paging and filter option
     *
     * @return Request processing status
     */
    @POST
    @Path("/list")
     ContactsResponseDto listPost(PagingAndFiltering pagingAndFiltering);
    
    /**
     * Import the contact list to a CSV file text
     *
     * @param context The context information text
     * @return Request processing status
     */
    @POST
    @Path("/importCSVText")
    ContactsResponseDto importCSVText(String context);
    
    /**
     * Import the contact list from a file
     *
     * @return Request processing status
     */
    @POST
    @Path("/importCSVFile")
    ActionStatus importCSVFile();
       
 }
