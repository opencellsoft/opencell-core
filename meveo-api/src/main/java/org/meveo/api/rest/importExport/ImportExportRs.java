package org.meveo.api.rest.importExport;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for importing and exporting data to another instance of application.
 * 
 * @author Andrius Karpavicius
 **/
@Path("/importExport")
@Consumes({ MediaType.MULTIPART_FORM_DATA })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ImportExportRs extends IBaseRs {

    /**
     * Send a file to be imported. ImportExportResponseDto.executionId contains
     * 
     * @param input
     * @return As import is async process, ImportExportResponseDto.executionId contains and ID to be used to query for execution results via a call to
     *         /importExport/checkImportDataResult?id=..
     */
    @POST
    @Path("/importData")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ImportExportResponseDto importData(MultipartFormDataInput input);

    /**
     * Check for execution results for a given execution identifier
     * 
     * @param Id Execution id returned in /importExport/importData call
     * @return
     */
    @GET
    @Path("/checkImportDataResult")
    public ImportExportResponseDto checkImportDataResult(@QueryParam("executionId") String executionId);
}