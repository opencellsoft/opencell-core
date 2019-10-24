package org.meveo.api.rest.dunning;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dunning.DunningDocumentDto;
import org.meveo.api.dto.dunning.DunningDocumentResponseDto;
import org.meveo.api.dto.dunning.DunningDocumentsListResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.rest.IBaseRs;

/**
 * @author abdelmounaim akadid
 **/
@Path("/dunning/dunningDocument")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DunningDocumentRs extends IBaseRs {

    /**
     * Create a dunningDocument.
     * 
     * @param postData DunningDocument data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(DunningDocumentDto postData);

    /**
     * Add Payments to dunningDocument.
     * 
     * @param postData DunningDocument's data
     * @return Request processing status
     */
    @PUT
    @Path("/addPayments")
    ActionStatus addPayments(DunningDocumentDto postData);
    
    /**
     * Search for a dunningDocument with a given code.
     * 
     * @param dunningDocumentCode The dunningDocument's code
     * @return customer account
     */
    @GET
    @Path("/")
    DunningDocumentResponseDto find(@QueryParam("dunningDocumentCode") String dunningDocumentCode);

    /**
     * List dunningDocuments matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of dunningDocuments
     */
    @POST
    @Path("/list")
    public DunningDocumentsListResponseDto listPost(PagingAndFiltering pagingAndFiltering);

}
