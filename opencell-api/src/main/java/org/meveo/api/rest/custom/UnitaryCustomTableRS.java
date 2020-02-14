package org.meveo.api.rest.custom;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.custom.UnitaryCustomTableDataDto;
import org.meveo.api.dto.custom.IdentityResponseDTO;
import org.meveo.api.rest.IBaseRs;
import org.meveo.apiv2.models.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/unitaryCustomTable")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface UnitaryCustomTableRS extends IBaseRs {

    /**
     * create data and append it to a custom table
     *
     * @param dto Custom table data
     * @return Request processing status
     */
    @POST
    @Path("/")
    @Operation(summary = "Create a single customTable row", tags = { "unitary-custom-table-data-dto" }, description = "Returns the id of the created table row", responses = {
            @ApiResponse(description = "the created custom table row", content = @Content(schema = @Schema(implementation = UnitaryCustomTableDataDto.class)), responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    IdentityResponseDTO create(UnitaryCustomTableDataDto dto);

    /**
     * Update existing data in a custom table
     *
     * @param dto Custom table data. 'id' field is used to identify an existing record.
     * @return Request processing status
     */
    @PUT
    @Path("/")
    @Operation(summary = "update a single customTable row", tags = { "unitary-custom-table-data-dto" }, description = "update custom table data row", responses = {
            @ApiResponse(description = "row updated succefully", content = @Content(schema = @Schema(implementation = UnitaryCustomTableDataDto.class)), responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    IdentityResponseDTO update(UnitaryCustomTableDataDto dto);

    /**
     * Remove an existing data from a custom table.
     *
     * @param tableName Custom table name.
     * @param id 'id' field is used to identify an existing record.
     * @returnIdentityResponseDTO
     */
    @DELETE
    @Path("/{tableName}/{id}")
    @Operation(summary = "delete a single customTable row", tags = { "unitary-custom-table-data-dto" }, description = "delete custom table data row", responses = {
            @ApiResponse(description = "row deleted succefully", content = @Content(schema = @Schema(implementation = UnitaryCustomTableDataDto.class)), responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    IdentityResponseDTO remove(@PathParam("tableName") String tableName, @PathParam("id") Long id);

    /**
     * Mark a record as enabled in a custom table. Applies only to those custom tables that contain a field 'disabled'
     *
     * @param tableName Custom table name.
     * @param id 'id' field is used to identify an existing record.
     * @returnIdentityResponseDTO
     */
    @POST
    @Path("/{tableName}/{id}/enable")
    @Operation(summary = "enable a single customTable row", tags = { "unitary-custom-table-data-dto" }, description = "update custom table data row", responses = {
            @ApiResponse(description = "row updated succefully", content = @Content(schema = @Schema(implementation = UnitaryCustomTableDataDto.class)), responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    IdentityResponseDTO enable(@PathParam("tableName") String tableName, @PathParam("id") Long id);

    /**
     * Mark a record as disabled in a custom table. Applies only to those custom tables that contain a field 'disabled'
     *
     * @param tableName Custom table name.
     * @param id 'id' field is used to identify an existing record.
     * @returnIdentityResponseDTO
     */
    @POST
    @Path("/{tableName}/{id}/disable")
    @Operation(summary = "enable a single customTable row", tags = { "unitary-custom-table-data-dto" }, description = "update custom table data row", responses = {
            @ApiResponse(description = "row updated succefully", content = @Content(schema = @Schema(implementation = UnitaryCustomTableDataDto.class)), responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    IdentityResponseDTO disable(@PathParam("tableName") String tableName, @PathParam("id") Long id);

}
