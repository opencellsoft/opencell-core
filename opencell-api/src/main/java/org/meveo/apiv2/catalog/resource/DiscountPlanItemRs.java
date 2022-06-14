package org.meveo.apiv2.catalog.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemResponseDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemsResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.restful.pagingFiltering.PagingAndFilteringRest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/catalog/discountPlanItems")
@Tag(name = "DiscountPlanItem", description = "@%DiscountPlanItem")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DiscountPlanItemRs extends IBaseRs {

    /**
     * Create a new discount plan item.
     *
     * @param postData A discount plan item's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    @Operation(
            summary=" Create a new discount plan item. ",
            description=" Create a new discount plan item. ",
            operationId="    POST_DiscountPlanItem_create",
            responses= {
                    @ApiResponse(description=" Request processing status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus create(DiscountPlanItemDto postData);

    /**
     * update an existed discount plan item.
     *
     * @param postData A discount plan item's data
     * @return Request processing status
     */
    @PUT
    @Path("/{discountPlanItemCode}")
    @Operation(
            summary=" update an existed discount plan item.  ",
            description=" update an existed discount plan item.  ",
            operationId="    PUT_DiscountPlanItem_update",
            responses= {
                    @ApiResponse(description=" Request processing status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus update(@Parameter(description = "code of the discount plan item", required = true) @PathParam("discountPlanItemCode") String discountPlanItemCode,
                        DiscountPlanItemDto postData);

    /**
     * Find a discount plan item with a given code.
     *
     * @param discountPlanItemCode A discount plan item's code
     * @return A discount plan item
     */
    @GET
    @Path("/{discountPlanItemCode}")
    @Operation(
            summary=" Find a discount plan item with a given code. ",
            description=" Find a discount plan item with a given code. ",
            operationId="    GET_DiscountPlanItem_search",
            responses= {
                    @ApiResponse(description=" A discount plan item ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= DiscountPlanItemResponseDto.class
                                    )
                            )
                    )}
    )
    DiscountPlanItemResponseDto find(@Parameter(description = "code of the discount plan item", required = true) @PathParam("discountPlanItemCode") String discountPlanItemCode);

    /**
     * remove a discount plan item by code.
     *
     * @param discountPlanItemCode discount plan item
     * @return Request processing status
     */
    @DELETE
    @Path("/{discountPlanItemCode}")
    @Operation(
            summary=" remove a discount plan item by code. ",
            description=" remove a discount plan item by code. ",
            operationId="    DELETE_DiscountPlanItem_{discountPlanItemCode}",
            responses= {
                    @ApiResponse(description=" Request processing status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus remove(@Parameter(description = "code of the discount plan item", required = true) @PathParam("discountPlanItemCode") String discountPlanItemCode);

    /**
     * create/update a discount plan item.
     *
     * @param postData discount plan item
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    @Operation(
            summary=" create/update a discount plan item. ",
            description=" create/update a discount plan item. ",
            operationId="    POST_DiscountPlanItem_createOrUpdate",
            responses= {
                    @ApiResponse(description=" Request processing status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus createOrUpdate(DiscountPlanItemDto postData);

    /**
     * List all discount plan items by current user.
     *
     * @return List of discount plan items
     */
    @GET
    @Path("/list")
    @Operation(
            summary=" List all discount plan items by current user. ",
            description=" List all discount plan items by current user. ",
            operationId="    GET_DiscountPlanItem_list",
            responses= {
                    @ApiResponse(description=" List of discount plan items ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= DiscountPlanItemsResponseDto.class
                                    )
                            )
                    )},
            deprecated = true,
            tags = { "Deprecated" }
    )
    DiscountPlanItemsResponseDto list();

    /**
     * List DiscountPlanItems matching a given criteria
     *
     * @return List of DiscountPlanItems
     */
    @GET
    @Path("")
    @Operation(
            summary=" List DiscountPlanItems matching a given criteria ",
            description=" List DiscountPlanItems matching a given criteria ",
            operationId="    GET_DiscountPlanItem_listGetAll",
            responses= {
                    @ApiResponse(description=" List of DiscountPlanItems ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= DiscountPlanItemsResponseDto.class
                                    )
                            )
                    )}
    )
    DiscountPlanItemsResponseDto list(PagingAndFilteringRest pagingAndFiltering);

    /**
     * Enable a Discount plan item with a given code
     *
     * @param code Discount plan item code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    @Operation(
            summary=" Enable a Discount plan item with a given code  ",
            description=" Enable a Discount plan item with a given code  ",
            operationId="    POST_DiscountPlanItem_{code}_enable",
            responses= {
                    @ApiResponse(description=" Request processing status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Discount plan item with a given code
     *
     * @param code Discount plan item code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    @Operation(
            summary=" Disable a Discount plan item with a given code  ",
            description=" Disable a Discount plan item with a given code  ",
            operationId="    POST_DiscountPlanItem_{code}_disable",
            responses= {
                    @ApiResponse(description=" Request processing status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus disable(@PathParam("code") String code);
}

