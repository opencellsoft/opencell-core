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
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.UsageChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/usageChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UsageChargeTemplateRs extends IBaseRs {

    /**
     * Create new usage charge template.
     * 
     * @param postData The usage charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    public ActionStatus create(UsageChargeTemplateDto postData);

    /**
     * Update usage charge template.
     * 
     * @param postData The usage charge template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    public ActionStatus update(UsageChargeTemplateDto postData);

    /**
     * Find an existing usage charge template with a given code.
     * 
     * @param usageChargeTemplateCode The charge template's code
     * @return Returns a usageChargeTemplate
     */
    @GET
    @Path("/")
    public GetUsageChargeTemplateResponseDto find(@QueryParam("usageChargeTemplateCode") String usageChargeTemplateCode);

    /**
     * Remove usage charge template with a given code.
     * 
     * @param usageChargeTemplateCode The charge template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{usageChargeTemplateCode}")
    public ActionStatus remove(@PathParam("usageChargeTemplateCode") String usageChargeTemplateCode);

    /**
     * Create new or update an existing charge template with a given code.
     * 
     * @param postData The usage charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(UsageChargeTemplateDto postData);

    /**
     * Enable a Usage charge template with a given code
     * 
     * @param code Usage charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Usage charge template with a given code
     * 
     * @param code Usage charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}
