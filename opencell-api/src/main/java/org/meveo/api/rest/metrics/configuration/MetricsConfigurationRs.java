package org.meveo.api.rest.metrics.configuration;

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
import org.meveo.api.dto.metrics.configuration.MetricsConfigurationDto;
import org.meveo.api.dto.response.GetMetricsConfigurationResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.metrics.configuration.MetricsConfiguration;

/**
 * Web service for managing {@link MetricsConfiguration}.
 *
 * @author mohamed STITANE
 **/
@Path("/metrics/config")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface MetricsConfigurationRs extends IBaseRs {
    /**
     * Creates a MetricConfiguration.
     *
     * If the MetricConfiguration code does not exists, a metrics configuration record is created
     *
     * @param metricsConfigurationDto a metrics configuration dto.
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @POST
    @Path("/")
    ActionStatus create(MetricsConfigurationDto metricsConfigurationDto);

    /**
     * Search metrics configuration with a given code.
     *
     * @param code MetricConfiguration code
     * @return {@link GetMetricsConfigurationResponse}.
     */
    @GET
    @Path("/")
    GetMetricsConfigurationResponse find(@QueryParam("code") String code);

    /**
     * Update an existing MetricConfiguration.
     *
     * If the MetricsConfiguration code exists, a metrics configuration record is updated
     *
     * @param metricsConfigurationDto a metrics configuration dto.
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @PUT
    @Path("/")
    ActionStatus update(MetricsConfigurationDto metricsConfigurationDto);

    /**
     * Delete metrics configuration with a given code.
     *
     * @param code MetricConfiguration code
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @DELETE
    @Path("/")
    ActionStatus remove(@QueryParam("code") String code);
}
