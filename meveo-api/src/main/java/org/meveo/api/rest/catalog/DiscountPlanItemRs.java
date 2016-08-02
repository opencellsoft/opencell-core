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
import org.meveo.api.rest.security.RSSecured;

/**
 * CRUD/list discountPlanItem via REST API
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Aug 2, 2016 11:02:01 AM
 *
 */
@Path("/catalog/discountPlanItem")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface DiscountPlanItemRs extends IBaseRs {

	/**
	 * create a new discount plan item
	 * @param postData
	 * @return
	 */
    @Path("/")
    @POST
    ActionStatus create(DiscountPlanItemDto postData);

    /**
     * update an existed discount plan item
     * @param postData
     * @return
     */
    @Path("/")
    @PUT
    ActionStatus update(DiscountPlanItemDto postData);

    /**
     * find a discount plan item by code
     * @param discountPlanItemCode
     * @return
     */
    @Path("/")
    @GET
    DiscountPlanItemResponseDto find(@QueryParam("code") String code);

    /**
     * remove a discount plan item by code
     * @param discountPlanItemCode
     * @return
     */
    @Path("/{code}")
    @DELETE
    ActionStatus remove(@PathParam("code") String code);

    /**
     * create/update a discount plan item
     * @param postData
     * @return
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(DiscountPlanItemDto postData);

    /**
     * list all discount plan items by current user
     * @return
     */
    @Path("/list")
    @GET
    DiscountPlanItemsResponseDto list();
}
