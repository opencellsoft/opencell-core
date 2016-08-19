package org.meveo.api.rest;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.usage.UsageResponseDto;
import org.meveo.api.rest.security.RSSecured;


@Path("/usage")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface UsageRs extends IBaseRs {

    

    /**
     * Search for all opened ratedTransactions with a given userAccountCode,fromDate and toDate .
     * 
     * @param userAccountCode,fromDate,toDate
     * @return
     */
    @Path("/")
    @GET
    public UsageResponseDto find(@QueryParam("userAccountCode") String userAccountCode,@QueryParam("fromDate") Date fromDate,@QueryParam("toDate") Date toDate);


}
