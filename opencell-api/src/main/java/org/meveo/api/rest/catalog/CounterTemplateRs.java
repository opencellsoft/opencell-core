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
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.CounterTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/counterTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CounterTemplateRs extends IBaseRs {

    /**
     * Create counter template.
     * 
     * @param postData counter template
     * @return action status
     */
    @Path("/")
    @POST
    ActionStatus create(CounterTemplateDto postData);

    /**
     * Update counter template.
     * 
     * @param postData counter template
     * @return action status
     */
    @Path("/")
    @PUT
    ActionStatus update(CounterTemplateDto postData);

    /**
     * Search counter template with a given code.
     * 
     * @param counterTemplateCode counter temlate's code
     * @return counter template
     */
    @Path("/")
    @GET
    GetCounterTemplateResponseDto find(@QueryParam("counterTemplateCode") String counterTemplateCode);

    /**
     * Remove counter template with a given code.
     * 
     * @param counterTemplateCode counter template's code
     * @return action status
     */
    @Path("/{counterTemplateCode}")
    @DELETE
    ActionStatus remove(@PathParam("counterTemplateCode") String counterTemplateCode);

    /**
     * @param postData counter template
     * @return action status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(CounterTemplateDto postData);

    /**
     * Enable a Counter template with a given code
     * 
     * @param code Counter template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Counter template with a given code
     * 
     * @param code Counter template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}
