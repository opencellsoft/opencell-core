package org.meveo.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.meveo.api.dto.FilterCDRDto;
import org.meveo.model.rating.CDR;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.List;

/**
 * @author Mohamed CHAOUKI
 **/

@Path("/cdr")
@Tag(name = "cdr", description = "@%cdr")
@Consumes({MediaType.MULTIPART_FORM_DATA})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public interface CdrRs {

    /**
     * listing cdrs.
     *
     * @return list of cdrs
     */
    @POST
    @Path("/list-cdrs")
    @Operation(
            description = " Get all cdrs By pagination.  ",
            operationId = "    GET_allCDR",
            responses = {
                    @ApiResponse(description = " list cdrs ",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = CDR.class
                                    )
                            )
                    )}
    )
    @Consumes({MediaType.APPLICATION_JSON})
    List<CDR> listCDRs(@Parameter(description = "filter", required = false) FilterCDRDto filterCDRDto,
                       @Parameter(description = "size", required = false) @QueryParam("size") int size,
                       @Parameter(description = "page", required = false) @QueryParam("page") int page) throws ParseException;


}
