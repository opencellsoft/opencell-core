package org.meveo.api.rest.dwh;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;
import org.meveo.model.dwh.MeasurementPeriodEnum;

@Path("/measurableQuantity")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface MeasurableQuantityRs extends IBaseRs {

	@POST
	@Path("/")
	public ActionStatus create(MeasurableQuantityDto postData);

	/**
	 * Update Measurable quantity
	 * 
	 * @param postData
	 * @return
	 */
	@PUT
	@Path("/")
	public ActionStatus update(MeasurableQuantityDto postData);

	@GET
	@Path("/findMVByDateAndPeriod")
	public Response findMVByDateAndPeriod(@QueryParam("code") String code, @QueryParam("fromDate") Date fromDate, @QueryParam("toDate") Date toDate,
			@QueryParam("period") MeasurementPeriodEnum period, @QueryParam("mqCode") String mqCode);

}