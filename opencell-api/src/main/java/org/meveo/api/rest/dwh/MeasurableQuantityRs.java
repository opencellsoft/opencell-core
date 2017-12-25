package org.meveo.api.rest.dwh;

import java.util.Date;

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
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dwh.GetListMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.GetMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.dwh.MeasurementPeriodEnum;

@Path("/measurableQuantity")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface MeasurableQuantityRs extends IBaseRs {

    /**
     * @param postData posted data to API
     * @return action status.
     */
    @POST
    @Path("/")
    ActionStatus create(MeasurableQuantityDto postData);

    /**
     * Update Measurable quantity from mesearable quantities.
     * 
     * @param postData posted data.
     * @return actions status.
     */
    @PUT
    @Path("/")
    ActionStatus update(MeasurableQuantityDto postData);

    /**
     * Get Measurable quantity from a given code.
     * 
     * @param code Measureable quantity's code
     * @return
     */
    @GET
    @Path("/")
    GetMeasurableQuantityResponse find(@QueryParam("code") String code);

    /**
     * 
     * @param code code of mesurable value.
     * @param fromDate format yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd
     * @param toDate format yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd
     * @param period period in which mesurable value is calculated.
     * @param mqCode Measureable quantity's code
     * @return mesurable value by date and period.
     */
    @GET
    @Path("/findMVByDateAndPeriod")
    Response findMVByDateAndPeriod(@QueryParam("code") String code, @QueryParam("fromDate") @RestDateParam Date fromDate, @QueryParam("toDate") @RestDateParam Date toDate,
            @QueryParam("period") MeasurementPeriodEnum period, @QueryParam("mqCode") String mqCode);

    /**
     * Remove Measurable quantity with a given code.
     * 
     * @param code Measurable quantity's code
     * @return action status.
     */
    @Path("/{code}")
    @DELETE
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List Measurable quantity with a given code.
     * 
     * @return A list of measurable quantities
     */
    @Path("/list")
    @GET
    GetListMeasurableQuantityResponse list();

}