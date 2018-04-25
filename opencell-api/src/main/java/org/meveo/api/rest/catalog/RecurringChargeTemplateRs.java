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
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.RecurringChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/recurringChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface RecurringChargeTemplateRs extends IBaseRs {

    /**
     * Create a new recurring charge template.
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    public ActionStatus create(RecurringChargeTemplateDto postData);

    /**
     * Find a recurring charge template with a given code.
     * 
     * @param recurringChargeTemplateCode The reccuring charge template's code
     * @return Return a recurringChargeTemplate
     */
    @GET
    @Path("/")
    public GetRecurringChargeTemplateResponseDto find(@QueryParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

    /**
     * Update an existing recurring charge template.
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    public ActionStatus update(RecurringChargeTemplateDto postData);

    /**
     * Remove an existing recurring charge template with a given code.
     * 
     * @param recurringChargeTemplateCode
     * @return Request processing status
     */
    @DELETE
    @Path("/{recurringChargeTemplateCode}")
    public ActionStatus remove(@PathParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

    /**
     * Create new or update an existing recurring charge template
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(RecurringChargeTemplateDto postData);

    /**
     * Enable a Recurring charge template with a given code
     * 
     * @param code Recurring charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Recurring charge template with a given code
     * 
     * @param code Recurring charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}