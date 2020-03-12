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

package org.meveo.api.rest.custom;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.custom.CustomTableDataDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.custom.CustomTableWrapperDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Rest API for custom table data management
 * 
 * @author Andrius Karpavicius
 **/
@Path("/customTable")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomTableRs extends IBaseRs {

    /**
     * Append data to a custom table
     *
     * @param dto Custom table data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus append(CustomTableDataDto dto);

    /**
     * Update existing data in a custom table
     * 
     * @param dto Custom table data. 'id' field is used to identify an existing record.
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(CustomTableDataDto dto);

    /**
     * Remove an existing data from a custom table.
     * 
     * @param dto Custom table data. 'id' field is used to identify an existing record. If no 'id' values are passed, will delete all the records in a table.
     * @return Request processing status
     */
    @DELETE
    @Path("/")
    ActionStatus remove(CustomTableDataDto dto);

    /**
     * Search in custom tables
     * 
     * @param customTableCode Custom table code - can be either db table's name or a custom entity template code
     * @param pagingAndFiltering Paging and search criteria
     * @return Custom table data
     */
    @POST
    @Path("/list/{customTableCode}")
    CustomTableDataResponseDto list(@PathParam("customTableCode") String customTableCode, PagingAndFiltering pagingAndFiltering);

    /**
     * Append or update data in a custom table
     * 
     * @param dto Custom table data. 'id' field is used to identify an existing record. Presence of 'id' field will be treated as update operation.
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CustomTableDataDto dto);

    /**
     * Mark records as enabled in a custom table. Applies only to those custom tables that contain a field 'disabled'
     * 
     * @param dto Custom table data. 'id' field is used to identify an existing record.
     * @return Request processing status
     */
    @POST
    @Path("/enable")
    ActionStatus enable(CustomTableDataDto dto);

    /**
     * Mark records as disabled in a custom table. Applies only to those custom tables that contain a field 'disabled'
     *
     * @param dto Custom table data. 'id' field is used to identify an existing record.
     * @return Request processing status
     */
    @POST
    @Path("/disable")
    ActionStatus disable(CustomTableDataDto dto);

    /**
     * Search in custom tables using CustomTableWrapper
     *
     * @param customTableWrapperDto Custom table wrapper dto
     * @return Custom table data
     */
    @POST
    @Path("/listFromWrapper")
    CustomTableDataResponseDto listFromWrapper(CustomTableWrapperDto customTableWrapperDto);

}