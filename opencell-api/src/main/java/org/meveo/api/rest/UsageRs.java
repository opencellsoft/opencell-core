package org.meveo.api.rest;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.usage.UsageChargeAggregateResponseDto;
import org.meveo.api.dto.usage.UsageResponseDto;
import org.meveo.api.serialize.RestDateParam;


@Path("/usage")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UsageRs extends IBaseRs {



    /**
     * Search for all opened ratedTransactions with a given userAccountCode,fromDate and toDate .
     * 
     * @param userAccountCode user account's code
     * @param fromDate from date
     * @param toDate to date
     * @return usage
     */
    @Path("/")
    @GET
    UsageResponseDto find(@QueryParam("userAccountCode") String userAccountCode, @QueryParam("fromDate") @RestDateParam Date fromDate,
            @QueryParam("toDate") @RestDateParam Date toDate);

    /**
     * @param userAccountCode user account's code
     * @param fromDate from date
     * @param toDate to date
     * @return usage charge aggregate
     */
    @Path("/chargeAggregate")
    @GET
    UsageChargeAggregateResponseDto chargeAggregate(@QueryParam("userAccountCode") String userAccountCode, @QueryParam("fromDate") @RestDateParam Date fromDate,
            @QueryParam("toDate") @RestDateParam Date toDate);

}
