package org.meveo.api.rest.catalog;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.dto.response.catalog.GetListUnitOfMeasureResponseDto;
import org.meveo.api.dto.response.catalog.GetUnitOfMeasureResponseDto;
import org.meveo.api.rest.IBaseRs;

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
/**
 * @author Mounir Bahije
 **/

@Path("/catalog/unitOfMeasure")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UnitOfMeasureRs extends IBaseRs {

    /**
     * Create a new unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(UnitOfMeasureDto postData);

    /**
     * Update an existing unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(UnitOfMeasureDto postData);

    /**
     * Search for a unitOfMeasure with a given code
     * 
     * @param unitOfMeasureCode The unitOfMeasure's code
     * @return A unitOfMeasure
     */
    @GET
    @Path("/")
    GetUnitOfMeasureResponseDto find(@QueryParam("unitOfMeasureCode") String unitOfMeasureCode);

    /**
     * Remove an existing unitOfMeasure with a given code
     * 
     * @param unitOfMeasureCode The unitOfMeasure's code
     * @return Request processing status
     */
    @Path("/{code}")
    @DELETE
    ActionStatus delete(@PathParam("code") String unitOfMeasureCode);

    /**
     * Create new or update an existing unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(UnitOfMeasureDto postData);

    /**
     * List all currencies.
     * @return list of all unitOfMeasure/
     */
    @GET
    @Path("/list")
    GetListUnitOfMeasureResponseDto list();


}
