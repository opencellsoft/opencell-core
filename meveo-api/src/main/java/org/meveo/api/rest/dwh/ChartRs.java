package org.meveo.api.rest.dwh;

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
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.response.dwh.GetChartResponse;
import org.meveo.api.rest.IBaseRs;

@Path("/chart")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ChartRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(ChartDto postData);

    @POST
    @Path("/bar")
    ActionStatus createBarChart(BarChartDto postData);

    @PUT
    @Path("/bar")
    ActionStatus updateBarChart(BarChartDto postData);

    @POST
    @Path("/pie")
    ActionStatus createPieChart(PieChartDto postData);

    @PUT
    @Path("/pie")
    ActionStatus updatePieChart(PieChartDto postData);

    @POST
    @Path("/line")
    ActionStatus createLineChart(LineChartDto postData);

    @PUT
    @Path("/line")
    ActionStatus updateLineChart(LineChartDto postData);

    @PUT
    @Path("/")
    ActionStatus update(ChartDto postData);

    @DELETE
    @Path("/")
    ActionStatus remove(@QueryParam("chartCode") String chartCode);

    @GET
    @Path("/")
    GetChartResponse find(@QueryParam("chartCode") String chartCode);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ChartDto postData);

}
