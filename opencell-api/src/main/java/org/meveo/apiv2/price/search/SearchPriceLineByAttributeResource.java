package org.meveo.apiv2.price.search;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/priceplanmatrixline")
@Tag(name = "PricePlanMatrixLine")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Deprecated
public interface SearchPriceLineByAttributeResource {
    @POST
    @Path("/search")
    @Operation(
            summary = "search price plan using attributes information",
            description = "search price plan using attributes information",
            responses = {
                    @ApiResponse(description = "list of price plan and meta data information"
                    )}
    )
    Response search(Map<String, Object> searchInfo);
}
