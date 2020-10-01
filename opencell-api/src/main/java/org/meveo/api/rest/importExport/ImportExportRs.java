/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.importExport;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.meveo.api.dto.response.utilities.ImportExportRequestDto;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for importing and exporting data to another instance of application.
 * 
 * @author Andrius Karpavicius
 **/
@Path("/importExport")
@Consumes({ MULTIPART_FORM_DATA, APPLICATION_JSON })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface ImportExportRs extends IBaseRs {

    /**
     * Send a file to be imported. ImportExportResponseDto.executionId contains
     * 
     * @param input file containing a list of object for import
     * @return As import is async process, ImportExportResponseDto.executionId contains and ID to be used to query for execution results via a call to
     *         /importExport/checkImportDataResult?id=..
     */
    @POST
    @Path("/importData")
    @Consumes(MULTIPART_FORM_DATA)
    ImportExportResponseDto importData(MultipartFormDataInput input);

    /**
     * Check for execution results for a given execution identifier
     * 
     * @param executionId Returned in /importExport/importData call
     * @return the execution result
     */
    @GET
    @Path("/checkImportDataResult")
    ImportExportResponseDto checkImportDataResult(@QueryParam("executionId") String executionId);

    @POST
    @Path("/exportData")
    ImportExportResponseDto exportData(ImportExportRequestDto importExportRequestDto);

    /**
     * returns an entity list CSV
     */
    @POST
    @Path("/generateEntityList")
    ImportExportResponseDto entityList(ImportExportRequestDto importExportRequestDto);

    @POST
    @Path("/exportDataFromEntityList")
    @Consumes(MULTIPART_FORM_DATA)
    ImportExportResponseDto exportDataFromEntityList(MultipartFormDataInput input);
}