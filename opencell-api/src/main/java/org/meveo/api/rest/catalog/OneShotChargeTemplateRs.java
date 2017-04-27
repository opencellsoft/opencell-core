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
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.OneShotChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/oneShotChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OneShotChargeTemplateRs extends IBaseRs {

    /**
     * Create one shot charge template.
     * 
     * @param postData The one shot charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    public ActionStatus create(OneShotChargeTemplateDto postData);

    /**
     * Update one shot charge template.
     * 
     * @param postData The one shot charge template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    public ActionStatus update(OneShotChargeTemplateDto postData);

    /**
     * Search one shot charge templatewith a given code.
     * 
     * @param oneShotChargeTemplateCode The one shot charge template's code
     * @return
     */
    @GET
    @Path("/")
    public GetOneShotChargeTemplateResponseDto find(@QueryParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

    /**
     * List one shot charge template with the following filters.
     * 
     * @param languageCode
     * @param countryCode
     * @param currencyCode
     * @param sellerCode
     * @param date
     * @return 
     */
    @GET
    @Path("/list")
    public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(@QueryParam("languageCode") String languageCode, @QueryParam("countryCode") String countryCode,
            @QueryParam("currencyCode") String currencyCode, @QueryParam("sellerCode") String sellerCode, @QueryParam("date") String date);

    /**
     * Remove one shot charge tesmplate with a given code.
     * 
     * @param oneShotChargeTemplateCode The one shot charge template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{oneShotChargeTemplateCode}")
    public ActionStatus remove(@PathParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

    /**
     * Create new or update an existing ###
     * 
     * @param postData The exemple's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(OneShotChargeTemplateDto postData);
}
