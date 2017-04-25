package org.meveo.api.rest.job;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
     * Create a new timer entity
     * 
     * @param postData The timer entity's data
     * @return Request processing status
     */
    @Path("/create")
    @POST
    ActionStatus create(TimerEntityDto postData);

    /**
     * Update an existing timer entity
     * 
     * @param postData The timer entity's data
     * @return Request processing status
     */
    @Path("/update")
    @POST
    ActionStatus update(TimerEntityDto postData);

    /**
     * Create new or update an existing timer entity with a given code
     * 
     * @param postData The timer entity's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(TimerEntityDto postData);

    /**
     * Find a timer entity with a given code 
     * 
     * @param timerEntityCode The timer entity's code
     * @return Return timerEntity
     */
    @Path("/")
    @GET
    GetTimerEntityResponseDto find(@QueryParam("timerEntityCode") String timerEntityCode);

}
