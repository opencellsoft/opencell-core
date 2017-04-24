package org.meveo.api.rest.catalog;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/chargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ChargeTemplateRs extends IBaseRs {

	/**
     * Search for a charge template with a given code 
     * 
     * @param chargeTemplateCode The charge template's code
     * @return A charge template
     */
    @GET
    @Path("/")
    GetChargeTemplateResponseDto find(@QueryParam("chargeTemplateCode") String chargeTemplateCode);

}
