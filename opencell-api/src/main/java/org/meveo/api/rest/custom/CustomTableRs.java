package org.meveo.api.rest.custom;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.custom.CustomTableDataDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.rest.IBaseRs;

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
    @Path("/enable")
    ActionStatus disable(CustomTableDataDto dto);
}