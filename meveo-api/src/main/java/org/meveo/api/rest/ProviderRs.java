package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.Provider}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/provider")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ProviderRs extends IBaseRs {

    /**
     * Create provider.
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/")
    ActionStatus create(ProviderDto postData);

    /**
     * Search for provider with a given code.
     * 
     * @param providerCode
     * @return
     */
    @GET
    @Path("/")
    GetProviderResponse find(@QueryParam("providerCode") String providerCode);

    /**
     * Update provider.
     * 
     * @param postData
     * @return
     */
    @PUT
    @Path("/")
    ActionStatus update(ProviderDto postData);

    /**
     * Returns list of trading countries, currencies and languages
     * 
     * @return
     */
    @GET
    @Path("/getTradingConfiguration")
    GetTradingConfigurationResponseDto findTradingConfiguration();

    /**
     * Returns list of invoicing configuration (calendars, taxes, invoice categories, invoice sub categories, billing cycles and termination reasons
     * 
     * @return
     */
    @GET
    @Path("/getInvoicingConfiguration")
    GetInvoicingConfigurationResponseDto findInvoicingConfiguration();

    /**
     * Returns list of customer brands, categories and titles
     * 
     * @return
     */
    @GET
    @Path("/getCustomerConfiguration")
    GetCustomerConfigurationResponseDto findCustomerConfiguration();

    /**
     * Returns list of payment method and credit categories
     * 
     * @return
     */
    @GET
    @Path("/getCustomerAccountConfiguration")
    GetCustomerAccountConfigurationResponseDto findCustomerAccountConfiguration();

    /**
     * Create or update a provider if it doesn't exists
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ProviderDto postData);
}
