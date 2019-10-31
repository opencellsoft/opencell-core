package org.meveo.api.rest.metric.configuration;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.metric.configuration.MetricConfigurationDto;
import org.meveo.api.dto.response.GetMetricConfigurationResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.metric.configuration.MetricConfiguration}.
 *
 * @author mohamed STITANE
 **/
@Path("/metric/config")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface MetricConfigurationRs extends IBaseRs {
    /**
     * Creates a MetricConfiguration.
     *
     * If the MetricConfiguration code does not exists, a metric configuration record is created
     *
     * @param metricConfigurationDto a metric configuration dto.
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @POST
    @Path("/")
    ActionStatus create(MetricConfigurationDto metricConfigurationDto);

    /**
     * Search metric configuration with a given code.
     *
     * @param code MetricConfiguration code
     * @return {@link org.meveo.api.dto.response.GetMetricConfigurationResponse}.
     */
    @GET
    @Path("/")
    GetMetricConfigurationResponse find(@QueryParam("code") String code);

    /**
     * Update an existing MetricConfiguration.
     *
     * If the MetricConfiguration code exists, a metric configuration record is updated
     *
     * @param metricConfigurationDto a metric configuration dto.
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @PUT
    @Path("/")
    ActionStatus update(MetricConfigurationDto metricConfigurationDto);

    /**
     * Delete metric configuration with a given code.
     *
     * @param code MetricConfiguration code
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @DELETE
    @Path("/")
    ActionStatus remove(@QueryParam("code") String code);
}
