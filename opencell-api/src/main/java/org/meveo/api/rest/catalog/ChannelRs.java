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
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.response.catalog.GetChannelResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/catalog/channel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ChannelRs extends IBaseRs {

    /**
     * Create a new channel
     * 
     * @param postData The channel's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(ChannelDto postData);

    /**
     * Update an existing channel
     * 
     * @param postData The channel's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(ChannelDto postData);

    /**
     * Search for a channel with a given code
     * 
     * @param channelCode The channel's code
     * @return A channel
     */
    @GET
    @Path("/")
    GetChannelResponseDto find(@QueryParam("channelCode") String channelCode);

    /**
     * Remove an existing channel with a given code
     * 
     * @param channelCode The channel's code
     * @return Request processing status
     */
    @Path("/")
    @DELETE
    ActionStatus delete(@QueryParam("channelCode") String channelCode);

    /**
     * Create new or update an existing channel
     * 
     * @param postData The channel's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(ChannelDto postData);

    /**
     * Enable a Channel with a given code
     * 
     * @param code Channel code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Channel with a given code
     * 
     * @param code Channel code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}
