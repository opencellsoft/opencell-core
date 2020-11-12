package org.meveo.api.rest.dataCollector;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.DataCollectorDto;
import org.meveo.api.dto.AggregatedDataDto;
import org.meveo.api.dto.response.AggregatedDataResponseDto;
import org.meveo.api.dto.response.DataCollectorResponse;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/dataCollector")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface DataCollectorRs extends IBaseRs {

    /**
     * Create a new data collector using a DataCollectorDto.
     *
     * @param postData DataCollector's data
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(DataCollectorDto postData);

    /**
     * Find a data collector with a given code.
     *
     * @param code Data collector's code
     * @return Data collector information
     */
    @GET
    @Path("/")
    DataCollectorResponse find(@QueryParam("code") String code);

    /**
     * Execute data collector SQL query
     *
     * @param dataCollectorCode Data collector's code
     *  @return action status
     */
    @GET
    @Path("/executeQuery")
    ActionStatus execute(@QueryParam("dataCollectorCode") String dataCollectorCode);

    /**
     * aggregation API : extract aggregated data (like sum, avg)
     *
     * @param aggregationFields aggregation information
     * @return AggregatedData information
     */
    @POST
    @Path("/data")
    AggregatedDataResponseDto aggregatedData(AggregatedDataDto aggregationFields);
}