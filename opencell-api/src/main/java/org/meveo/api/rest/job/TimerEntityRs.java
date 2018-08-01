package org.meveo.api.rest.job;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.GetTimerEntityResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * 
 * @author Manu Liwanag
 * 
 */
@Path("/timerEntity")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TimerEntityRs extends IBaseRs {

    /**
     * Create a new timer schedule
     * 
     * @param postData The timer schedule's data
     * @return Request processing status
     */
    @Path("/create")
    @POST
    ActionStatus create(TimerEntityDto postData);

    /**
     * Update an existing timer schedule
     * 
     * @param postData The timer schedule's data
     * @return Request processing status
     */
    @Path("/update")
    @POST
    ActionStatus update(TimerEntityDto postData);

    /**
     * Create new or update an existing timer schedule with a given code
     * 
     * @param postData The timer schedule's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(TimerEntityDto postData);

    /**
     * Find a timer schedule with a given code
     * 
     * @param timerEntityCode The timer schedule's code
     * @return Return timerEntity
     */
    @Path("/")
    @GET
    GetTimerEntityResponseDto find(@QueryParam("timerEntityCode") String timerEntityCode);

    /**
     * Enable a Timer schedule with a given code
     * 
     * @param code Timer schedule code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Timer schedule with a given code
     * 
     * @param code Timer schedule code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}
