package org.meveo.api.rest.catalog;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * Web service for managing {@link org.meveo.model.catalog.OfferTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/offerTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OfferTemplateRs extends IBaseRs {

    /**
     * Create offer template.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(OfferTemplateDto postData);

    /**
     * Update offer template.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(OfferTemplateDto postData);

    /**
     * Search offer template with a given code and validity dates. If no validity dates are provided, an offer template valid on a current date will be returned.
     * 
     * @param offerTemplateCode The offer template's code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @param loadOfferServiceTemplate if true loads the services
     * @param loadOfferProductTemplate if true loads the products
     * @param loadServiceChargeTemplate if true load the service charges
     * @param loadProductChargeTemplate if true load the product charges
     * @return Return offerTemplateDto containing offerTemplate
     */
    @Path("/")
    @GET
    GetOfferTemplateResponseDto find(@QueryParam("offerTemplateCode") String offerTemplateCode, @QueryParam("validFrom") @RestDateParam Date validFrom,
            @QueryParam("validTo") @RestDateParam Date validTo, @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF,
            @QueryParam("loadOfferServiceTemplate") @DefaultValue("false") boolean loadOfferServiceTemplate, @QueryParam("loadOfferProductTemplate") @DefaultValue("false") boolean loadOfferProductTemplate,
            @QueryParam("loadServiceChargeTemplate") @DefaultValue("false") boolean loadServiceChargeTemplate, @QueryParam("loadProductChargeTemplate") @DefaultValue("false") boolean loadProductChargeTemplate);

    /**
     * List Offer templates matching filtering and query criteria or code and validity dates.
     * 
     * If neither date is provided, validity dates will not be considered. If only validFrom is provided, a search will return offers valid on a given date. If only validTo date is
     * provided, a search will return offers valid from today to a given date.
     *
     * @param code Offer template code for optional filtering. Deprecated in v. 4.8. Use query instead.
     * @param validFrom Validity range from date. Deprecated in v. 4.8. Use query instead.
     * @param validTo Validity range to date. Deprecated in v. 4.8. Use query instead.
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return A list of offer templates
     */
    @GET
    @Path("/list")
    public GetListOfferTemplateResponseDto listGet(@Deprecated @QueryParam("offerTemplateCode") String code, @Deprecated @QueryParam("validFrom") @RestDateParam Date validFrom,
            @Deprecated @QueryParam("validTo") @RestDateParam Date validTo, @QueryParam("query") String query, @QueryParam("fields") String fields,
            @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy,
            @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * List offerTemplates matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of offer templates
     */
    @POST
    @Path("/list")
    public GetListOfferTemplateResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Remove offer template with a given code and validity dates. If no validity dates are provided, an offer template valid on a current date will be deleted.
     * 
     * @param offerTemplateCode The offer template's code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @return Request processing status
     */
    @Path("/{offerTemplateCode}")
    @DELETE
    ActionStatus remove(@PathParam("offerTemplateCode") String offerTemplateCode, @QueryParam("validFrom") @RestDateParam Date validFrom,
            @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Create or update offer template based on a given code.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(OfferTemplateDto postData);

    /**
     * Enable a Offer template with a given code
     * 
     * @param code Offer template code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Disable a Offer template with a given code
     * 
     * @param code Offer template code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

}