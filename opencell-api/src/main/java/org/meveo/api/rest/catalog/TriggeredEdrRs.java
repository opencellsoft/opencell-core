package org.meveo.api.rest.catalog;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.response.catalog.GetTriggeredEdrResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/triggeredEdr")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TriggeredEdrRs extends IBaseRs {

    /**
     * Create a new triggered edr. template
     * 
     * @param postData The triggered edr. template's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(TriggeredEdrTemplateDto postData);

    /**
     * Update an existing triggered edr. template
     * 
     * @param postData The triggered edr. template's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(TriggeredEdrTemplateDto postData);

    /**
     * Find triggered edr with a given code.
     * 
     * @param triggeredEdrCode The triggered edr's code
     * @return Returns triggeredEdrTemplate
     */
    @Path("/")
    @GET
    GetTriggeredEdrResponseDto find(@QueryParam("triggeredEdrCode") String triggeredEdrCode);

    /**
     * Remove an existing triggered edr template with a given code.
     * 
     * @param triggeredEdrCode The triggered edr's code
     * @return Request processing status
     */
    @Path("/{triggeredEdrCode}")
    @DELETE
    ActionStatus remove(@PathParam("triggeredEdrCode") String triggeredEdrCode);

    /**
     * Create new or update an existing triggered edr template
     * 
     * @param postData The triggered edr template's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(TriggeredEdrTemplateDto postData);
}
