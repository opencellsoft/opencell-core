package org.meveo.api.rest.catalog;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemResponseDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemsResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * CRUD/list discountPlanItem via REST API.
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Aug 2, 2016 11:02:01 AM
 *
 */
@Path("/catalog/discountPlanItem")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DiscountPlanItemRs extends IBaseRs {

    /**
     * Create a new discount plan item.
     *
     * @param postData A discount plan item's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(DiscountPlanItemDto postData);

    /**
     * update an existed discount plan item.
     * 
     * @param postData A discount plan item's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(DiscountPlanItemDto postData);

    /**
     * Find a discount plan item with a given code.
     *
     * @param discountPlanItemCode A discount plan item's code
     * @return A discount plan item
     */
    @Path("/")
    @GET
    DiscountPlanItemResponseDto find(@QueryParam("discountPlanItemCode") String discountPlanItemCode);

    /**
     * remove a discount plan item by code.
     *
     * @param discountPlanItemCode discount plan item
     * @return Request processing status
     */
    @Path("/{discountPlanItemCode}")
    @DELETE
    ActionStatus remove(@PathParam("discountPlanItemCode") String discountPlanItemCode);

    /**
     * create/update a discount plan item.
     *
     * @param postData discount plan item
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(DiscountPlanItemDto postData);

    /**
     * List all discount plan items by current user.
     *
     * @return List of discount plan items
     */
    @Path("/list")
    @GET
    DiscountPlanItemsResponseDto list();

    /**
     * Enable a Discount plan item with a given code
     * 
     * @param code Discount plan item code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Discount plan item with a given code
     * 
     * @param code Discount plan item code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}