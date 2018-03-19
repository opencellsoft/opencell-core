package org.meveo.api.rest.finance;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.finance.ReportExtractResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 * @created 7 Feb 2018
 * @lastModifiedVersion 5.0
 **/
@Path("/finance/reportExtracts")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ReportExtractRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(ReportExtractDto postData);

    @POST
    @Path("/")
    ActionStatus update(ReportExtractDto postData);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ReportExtractDto postData);

    @DELETE
    @Path("/")
    ActionStatus remove(String reportExtractCode);

    @GET
    @Path("/")
    ReportExtractsResponseDto list();

    @GET
    @Path("/")
    ReportExtractResponseDto find(@QueryParam("reportExtractCode") String reportExtractCode);

    @POST
    @Path("/run")
    ActionStatus runReport(RunReportExtractDto postData);

}
