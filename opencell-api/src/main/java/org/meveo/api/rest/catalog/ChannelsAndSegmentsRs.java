package org.meveo.api.rest.catalog;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.response.catalog.GetListChannelsAndSegmentsResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 */

@Path("/catalog/channelsAndSegments")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ChannelsAndSegmentsRs extends IBaseRs {

    /**
     * Returns all the active channels list
     * 
     * @return A channel list
     */
    @GET
    @Path("/")
    GetListChannelsAndSegmentsResponseDto list(@QueryParam("active") Boolean active);

}
