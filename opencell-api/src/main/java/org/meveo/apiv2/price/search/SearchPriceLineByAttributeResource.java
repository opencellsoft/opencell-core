package org.meveo.apiv2.price.search;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/priceplanmatrixline")
@Tag(name = "PricePlanMatrixLine")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
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
