package org.meveo.api.rest.account;

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
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.account.AccessesResponseDto;
import org.meveo.api.dto.response.account.GetAccessResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/access")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface AccessRs extends IBaseRs {

    /**
     * Create a new access
     * 
     * @param postData Access data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(AccessDto postData);

    /**
     * Update an existing access
     * 
     * @param postData Access data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(AccessDto postData);

    /**
     * Search for an access with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @return Access
     */
    @GET
    @Path("/")
    GetAccessResponseDto find(@QueryParam("accessCode") String accessCode, @QueryParam("subscriptionCode") String subscriptionCode);

    /**
     * Remove an access with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @return Request processing status
     */
    @DELETE
    @Path("/{accessCode}/{subscriptionCode}")
    ActionStatus remove(@PathParam("accessCode") String accessCode, @PathParam("subscriptionCode") String subscriptionCode);

    /**
     * List Access filtered by subscriptionCode.
     * 
     * @param subscriptionCode Subscription code
     * @return A list of accesses
     */
    @GET
    @Path("/list")
    AccessesResponseDto listBySubscription(@QueryParam("subscriptionCode") String subscriptionCode);

    /**
     * Create new or update an existing access
     * 
     * @param postData data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(AccessDto postData);

}
