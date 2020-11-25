/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.payment;

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
import org.meveo.api.dto.PaymentActionStatus;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.DDRequestBuilderDto;
import org.meveo.api.dto.payment.DDRequestBuilderResponseDto;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import org.meveo.api.dto.payment.PaymentGatewayRumSequenceDto;
import org.meveo.api.dto.payment.PaymentHistoriesDto;
import org.meveo.api.dto.payment.PaymentHostedCheckoutResponseDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokenDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceResponseDto;
import org.meveo.api.dto.payment.PaymentScheduleInstancesDto;
import org.meveo.api.dto.payment.PaymentScheduleTemplateDto;
import org.meveo.api.dto.payment.PaymentScheduleTemplateResponseDto;
import org.meveo.api.dto.payment.PaymentScheduleTemplatesDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.payment.PaymentGatewayRumSequenceResponseDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * The Interface PaymentRs.
 * 
 * @author anasseh
 * @author Said Ramli
 * @author Edward P. Legaspi
 * @lastModifiedVersion 9.3
 */

@SuppressWarnings("deprecation")
@Path("/payment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PaymentRs extends IBaseRs {

    /**
     * Creates automated payment. It also process if a payment is matching or not Deprecated and replaced by "reatePayment" using /payment path
     * 
     * @param postData Payment's data
     * @return payment action status
     */
    @POST
    @Deprecated
    @Path("/create")
    public PaymentActionStatus create(PaymentDto postData);

    /**
     * Creates automated payment. It also process if a payment is matching or not
     * 
     * @param postData Payment's data
     * @return payment action status
     */
    @POST
    @Path("/")
    public PaymentActionStatus createPayment(PaymentDto postData);

    /**
     * Returns a list of account operations along with the balance of a customer.
     * 
     * @param customerAccountCode customer account code
     * @param pagingAndFiltering     
     * @return list of customer's response.
     */
    @GET
    @Path("/customerPayment")
    public CustomerPaymentsResponse list(@QueryParam("customerAccountCode") String customerAccountCode,  PagingAndFiltering pagingAndFiltering);

    /************************************************************************************************/
    /**** Card Payment Method ****/
    /************************************************************************************************/

    /**
     * Add a new card payment method. It will be marked as preferred.
     * 
     * @param cardPaymentMethod Card payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/cardPaymentMethod")
    public CardPaymentMethodTokenDto addCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod);

    /**
     * Update existing card payment method.
     * 
     * @param cardPaymentMethod Card payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/cardPaymentMethod")
    public ActionStatus updateCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod);

    /**
     * Remove card payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/cardPaymentMethod")
    public ActionStatus removeCardPaymentMethod(@QueryParam("id") Long id);

    /**
     * List available card payment methods for a given customer account identified either by id or by code.
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of card payment methods
     */
    @GET
    @Path("/cardPaymentMethod/list")
    public CardPaymentMethodTokensDto listCardPaymentMethods(@QueryParam("customerAccountId") Long customerAccountId,
            @QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Retrieve card payment method by its id.
     * 
     * @param id Id
     * @return Card payment DTO
     */
    @GET
    @Path("/cardPaymentMethod")
    public CardPaymentMethodTokenDto findCardPaymentMethod(@QueryParam("id") Long id);

    /************************************************************************************************/
    /**** Payment Methods ****/
    /************************************************************************************************/
    /**
     * Add a new payment method. It will be marked as preferred.
     * 
     * @param paymentMethod payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/paymentMethod")
    public PaymentMethodTokenDto addPaymentMethod(PaymentMethodDto paymentMethod);

    /**
     * Update existing payment method.
     * 
     * @param ddPaymentMethod payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/paymentMethod")
    public ActionStatus updatePaymentMethod(PaymentMethodDto ddPaymentMethod);

    /**
     * Remove payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/paymentMethod")
    public ActionStatus removePaymentMethod(@QueryParam("id") Long id);

    /**
     * List Payment Methods matching a given criteria
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return An payment method list
     */
    @GET
    @Path("/paymentMethod/list")
    public PaymentMethodTokensDto listPaymentMethodGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);
    
    
    /**
     * List Payment Methods matching a customer account
     * 
     * @param customerAccountCode customer account code.
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @return An payment method list
     */
    @GET
    @Path("/paymentMethod/findByCustomerAccount")
    public PaymentMethodTokensDto findPaymentMethodByCustomerAccount(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit);

    /**
     * List Payment Methods matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return An payment method list
     */
    @POST
    @Path("/paymentMethod/list")
    public PaymentMethodTokensDto listPaymentMethodPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Retrieve payment method by its id.
     * 
     * @param id Id
     * @return payment DTO
     */
    @GET
    @Path("/paymentMethod")
    public PaymentMethodTokenDto findPaymentMethod(@QueryParam("id") Long id);

    /**
     * Enable a Payment method with a given id
     * 
     * @param id Payment method id
     * @return Request processing status
     */
    @POST
    @Path("/paymentMethod/{id}/enable")
    ActionStatus enablePaymentMethod(@PathParam("id") Long id);

    /**
     * Disable a Payment method with a given id
     * 
     * @param id Payment method id
     * @return Request processing status
     */
    @POST
    @Path("/paymentMethod/{id}/disable")
    ActionStatus disablePaymentMethod(@PathParam("id") Long id);

    /************************************************************************************************/
    /**** DDRequest Builders ****/
    /************************************************************************************************/
    /**
     * Add a new payment gateway.
     * 
     * @param paymentGateway payment gateway DTO
     * @return the paymentGateway dto created
     */
    @POST
    @Path("/paymentGateway")
    public PaymentGatewayResponseDto addPaymentGateway(PaymentGatewayDto paymentGateway);

    /**
     * Update existing payment gateway.
     * 
     * @param paymentGateway payment gateway DTO
     * @return Action status
     */
    @PUT
    @Path("/paymentGateway")
    public ActionStatus updatePaymentGateway(PaymentGatewayDto paymentGateway);

    /**
     * Remove payment gateway.
     * 
     * @param code payment gateway's code
     * @return Action status
     */
    @DELETE
    @Path("/paymentGateway")
    public ActionStatus removePaymentGateway(@QueryParam("code") String code);

    /**
     * List payment gateways matching a given criteria
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return An payment gateway list
     */
    @GET
    @Path("/paymentGateway/list")
    public PaymentGatewayResponseDto listPaymentGatewaysGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List payment gateways matching a given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return An payment gateway list
     */
    @POST
    @Path("/paymentGateway/list")
    public PaymentGatewayResponseDto listPaymentGatewaysPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Retrieve payment gateway by its code.
     * 
     * @param code payment gateway's code
     * @return payment DTO
     */
    @GET
    @Path("/paymentGateway")
    public PaymentGatewayResponseDto findPaymentGateway(@QueryParam("code") String code);

    /**
     * Create or update payment gateway.
     * 
     * @param paymentGateway payment gateway DTO
     * @return the paymentGateway dto created
     */
    @POST
    @Path("/paymentGateway/createOrUpdate")
    public PaymentGatewayResponseDto createOrUpdatePaymentGateway(PaymentGatewayDto paymentGateway);

    /**
     * Enable a Payment gateway with a given code
     * 
     * @param code Payment gateway code
     * @return Request processing status
     */
    @POST
    @Path("/paymentGateway/{code}/enable")
    ActionStatus enablePaymentGateway(@PathParam("code") String code);

    /**
     * Disable a Payment gateway with a given code
     * 
     * @param code Payment gateway code
     * @return Request processing status
     */
    @POST
    @Path("/paymentGateway/{code}/disable")
    ActionStatus disablePaymentGateway(@PathParam("code") String code);
    
    /**
     * Creates a RUM sequence associated to the given payment gateway.
     * @param postData the RUM sequence details
     * @return Request processing status
     */
    @POST
    @Path("/paymentGateway/rumSequence")
    ActionStatus createRumSequence(PaymentGatewayRumSequenceDto postData);
    
    /**
     * Updates a RUM sequence associated to the given payment gateway.
     * @param postData the RUM sequence details
     * @return Request processing status
     */
    @PUT
    @Path("/paymentGateway/rumSequence")
    ActionStatus updateRumSequence(PaymentGatewayRumSequenceDto postData);
    
    /**
     * Finds the RUM sequence with the specified code.
     * @param code code of the RUM sequence 
     * @return Request processing status
     */
    @GET
    @Path("/paymentGateway/rumSequence/{code}")
    PaymentGatewayRumSequenceResponseDto findRumSequence(@PathParam("code") String code);
    
    /**
     * Deletes the RUM sequence with the specified code.
     * @param code code of the RUM sequence
     * @return Request processing status
     */
    @DELETE
    @Path("/paymentGateway/rumSequence/{code}")
    ActionStatus deleteRumSequence(@PathParam("code") String code);
    
    /**
	 * Generates the next RUM sequence number.
	 * @param code code of the sequence
	 * @return sequence value dto
	 */
	@POST
	@Path("/paymentGateway/rumSequence/{code}/next")
	GenericSequenceValueResponseDto getNextPaymentGatewayRumSequenceNumber(@PathParam("code") String code);

    /**
     * List Payment matching a given criteria.
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "transactions" in fields to include transactions and "pdf" to generate/include PDF document
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return An invoice list
     */
    @GET
    @Path("/history/list")
    PaymentHistoriesDto listPaymentHistoryGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List invoices matching a given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "transactions" in fields to include transactions and "pdf" to generate/include PDF document
     * @return An invoice list
     */
    @POST
    @Path("/history/list")
    PaymentHistoriesDto listPaymentHistoryPost(PagingAndFiltering pagingAndFiltering);
    

    /************************************************************************************************/
    /****                             DDRequest Builder                                          ****/
    /************************************************************************************************/
    /**
     * Add a new ddRequest builder.
     * 
     * @param ddRequestBuilder ddRequest builder DTO
     * @return the ddRequestBuilder dto created
     */
    @POST
    @Path("/ddRequestBuilder")
    public DDRequestBuilderResponseDto addDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder);

    /**
     * Update existing ddRequest builder.
     * 
     * @param ddRequestBuilder ddRequest builder DTO
     * @return Action status
     */
    @PUT
    @Path("/ddRequestBuilder")
    public ActionStatus updateDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder);

    /**
     * Remove ddRequest builder.
     * 
     * @param code ddRequest builder's code
     * @return Action status
     */
    @DELETE
    @Path("/ddRequestBuilder")
    public ActionStatus removeDDRequestBuilder(@QueryParam("code") String code);

    /**
     * List DDRequest Builders matching a given criteria
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return An ddRequest builder list
     */
    @GET
    @Path("/ddRequestBuilder/list")
    public DDRequestBuilderResponseDto listDDRequestBuildersGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List DDRequest Builders matching a given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return An ddRequest builder list
     */
    @POST
    @Path("/ddRequestBuilder/list")
    public DDRequestBuilderResponseDto listDDRequestBuildersPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Retrieve ddRequest builder by its code.
     * 
     * @param code ddRequest builder's code
     * @return ddRequest builder DTO
     */
    @GET
    @Path("/ddRequestBuilder")
    public DDRequestBuilderResponseDto findDDRequestBuilder(@QueryParam("code") String code);

    /**
     * Create or update ddRequest builder.
     * 
     * @param ddRequestBuilder ddRequest builder DTO
     * @return the ddRequestBuilder dto created
     */
    @POST
    @Path("/ddRequestBuilder/createOrUpdate")
    public DDRequestBuilderResponseDto createOrUpdateDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder);

    /**
     * Enable a ddRequest builder with a given code
     * 
     * @param code ddRequest builder code
     * @return Request processing status
     */
    @POST
    @Path("/ddRequestBuilder/{code}/enable")
    ActionStatus enableDDRequestBuilder(@PathParam("code") String code);

    /**
     * Disable a ddRequest builder with a given code
     * 
     * @param code ddRequest builder code
     * @return Request processing status
     */
    @POST
    @Path("/ddRequestBuilder/{code}/disable")
    ActionStatus disableDDRequestBuilder(@PathParam("code") String code);

    /**
     * Get the Hosted Checkout URL for payment.
     *
     * @param customerAccountCode the customerAccount Code
     * @param returnUrl the return Url
     * @param locale the locale
     * @param amount the amount of transaction
     * @param currencyCode the currency Code
     * @param authorizationMode  the authorizationMode
     * @param countryCode the country Code
     * @param skipAuthentication the skipAuthentication boolean
     * @param gatewayPaymentName the gatewayPayment Name
     * @param variant the variant Look
     * @return the PaymentHostedCheckoutResponseDto
     */
    @GET
    @Path("/paymentGateway/getHostedCheckoutUrl")
    public PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(@QueryParam("ca") String customerAccountCode,
                                                                 @QueryParam("returnUrl") String returnUrl,
                                                                 @DefaultValue("fr_FR") @QueryParam("locale") String locale,
                                                                 @DefaultValue("100") @QueryParam("amount") String amount,
                                                                 @DefaultValue("EUR") @QueryParam("currencyCode") String currencyCode,
                                                                 @DefaultValue("FINAL_AUTHORIZATION") @QueryParam("authorizationMode") String authorizationMode,
                                                                 @DefaultValue("fr") @QueryParam("countryCode") String countryCode,
                                                                 @DefaultValue("false") @QueryParam("skipAuthentication") Boolean skipAuthentication,
                                                                 @DefaultValue("INGENICO_GC") @QueryParam("gatewayPaymentName") String gatewayPaymentName,
                                                                 @DefaultValue("101") @QueryParam("variant") String variant,
                                                                 @QueryParam("seller") String sellerCode
    );

    
    /************************************************************************************************/
    /****                           Payment Schedules                                            ****/
    /************************************************************************************************/
    
    /**
     * Create or update payment Schedules template.
     * 
     * @param paymentScheduleTemplateDto payment Schedule Template Dto 
     * @return Request processing status
     */
    @POST
    @Path("/paymentScheduleTemplate/createOrUpdate")
    public ActionStatus createOrUpdatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto);
    /**
     * Create  payment Schedules template.
     * 
     * @param paymentScheduleTemplateDto payment Schedule Template Dto 
     * @return Request processing status
     */
    @POST
    @Path("/paymentScheduleTemplate")
    public ActionStatus createPaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto);
    
    /**
     * Create  payment Schedules template.
     * 
     * @param paymentScheduleTemplateDto payment Schedule Template Dto 
     * @return Request processing status
     */
    @PUT
    @Path("/paymentScheduleTemplate")
    public ActionStatus updatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto);


    /**
     * remove  payment Schedules template.
     * 
     * @param paymentScheduleTemplateCode payment Schedule Template Code  to remove
     * @return Request processing status
     */
    @DELETE
    @Path("/paymentScheduleTemplate")
    public ActionStatus removePaymentScheduleTemplate(@QueryParam("paymentScheduleTemplateCode") String paymentScheduleTemplateCode);
    
    /**
     * find  payment Schedules template.
     * 
     * @param paymentScheduleTemplateCode payment Schedule Template Code  to find
     * @return Request processing status
     */
    @GET
    @Path("/paymentScheduleTemplate")
    public PaymentScheduleTemplateResponseDto findPaymentScheduleTemplate(@QueryParam("paymentScheduleTemplateCode")  String paymentScheduleTemplateCode);
      
    /**
     * List  PaymentScheduleTemplate matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria.
     * @return An paymentScheduleTemplate dto list
     */
    @POST
    @Path("/paymentScheduleTemplate/list")
    public PaymentScheduleTemplatesDto listPaymentScheduleTemplate(PagingAndFiltering pagingAndFiltering);
    
    /**
     * List  PaymentScheduleTemplate matching a given criteria
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "transactions" in fields to include transactions and "pdf" to generate/include PDF document
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return An paymentScheduleTemplate dto list
     */
    @GET
    @Path("/paymentScheduleTemplate/list")
    public PaymentScheduleTemplatesDto listPaymentScheduleTemplate(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    
    /**
     * Update  payment Schedules instance.
     * 
     * @param paymentScheduleInstanceDto payment Schedule Instance Dto 
     * @return Request processing status
     */
    @PUT
    @Path("/paymentScheduleInstance")
    public ActionStatus updatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto);
    
    /**
     * Find  PaymentScheduleInstance by ID
     * 
     * @param id PaymentScheduleInstance ID
     * @return A paymentScheduleInstance dto
     */
    @GET
    @Path("/paymentScheduleInstance")
    public PaymentScheduleInstanceResponseDto findPaymentScheduleInstance(@QueryParam("id") Long id);

    
    /**
     * List  PaymentScheduleInstance matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria.
     * @return An PaymentScheduleInstance dto list
     */
    @POST
    @Path("/paymentScheduleInstance/list")
    public PaymentScheduleInstancesDto listPaymentScheduleInstance(PagingAndFiltering pagingAndFiltering);
    
    /**
     * List  paymentScheduleInstance matching a given criteria
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "transactions" in fields to include transactions and "pdf" to generate/include PDF document
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return An paymentScheduleInstance dto list
     */
    @GET
    @Path("/paymentScheduleInstance/list")
    public PaymentScheduleInstancesDto listPaymentScheduleInstance(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Terminate  payment Schedules instance.
     * 
     * @param paymentScheduleInstanceDto payment Schedule Instance Dto 
     * @return Request processing status
     */
    @PUT
    @Path("/paymentScheduleInstance/terminate")
    public ActionStatus terminatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto);
    
    /**
     * Cancel  payment Schedules instance.
     * 
     * @param paymentScheduleInstanceDto payment Schedule Instance Dto 
     * @return Request processing status
     */
    @PUT
    @Path("/paymentScheduleInstance/cancel")
    public ActionStatus cancelPaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto);
     
    
    
    /**
     * Gets a created mandate.
     * 
     * @param mandateReference mandate reference
     * @param mandateId mandate Id
     * @param customerAccountCode customer account code
     * @return created mandate
     */
    @GET
    @Path("/paymentGateway/checkMandate")
    public MandatInfoDto checkMandate(@QueryParam("mandateReference") String mandateReference,@QueryParam("mandateId") String mandateId,@QueryParam("customerAccountCode") String customerAccountCode);
    
    @GET
    @Path("/paymentGateway/approveSepaDDMandate")
    public ActionStatus approveSepaDDMandate(@QueryParam("customerAccountCode") String customerAccountCode,@QueryParam("tokenId")String tokenId);
    
    
}
