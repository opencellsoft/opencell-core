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
import org.meveo.api.dto.payment.HostedCheckoutStatusResponseDto;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PayByCardOrSepaDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import org.meveo.api.dto.payment.PaymentGatewayRumSequenceDto;
import org.meveo.api.dto.payment.PaymentHistoriesDto;
import org.meveo.api.dto.payment.PaymentHostedCheckoutResponseDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokenDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceItemsDto;
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
import org.meveo.model.payments.PaymentMethodEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Payment", description = "@%Payment")
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
	@Operation(
			summary=" Creates automated payment. It also process if a payment is matching or not Deprecated and replaced by reatePayment using /payment path ",
			description=" Creates automated payment. It also process if a payment is matching or not Deprecated and replaced by reatePayment using /payment path ",
			deprecated=true,
			operationId="    POST_Payment_create",
			responses= {
				@ApiResponse(description=" payment action status ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentActionStatus.class
											)
								)
				)}
	)
    public PaymentActionStatus create(PaymentDto postData);

    /**
     * Creates automated payment. It also process if a payment is matching or not
     *
     * @param postData Payment's data
     * @return payment action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Creates automated payment. It also process if a payment is matching or not ",
			description=" Creates automated payment. It also process if a payment is matching or not ",
			operationId="    POST_Payment_create",
			responses= {
				@ApiResponse(description=" payment action status ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Returns a list of account operations along with the balance of a customer. ",
			description=" Returns a list of account operations along with the balance of a customer. ",
			operationId="    GET_Payment_customerPayment",
			responses= {
				@ApiResponse(description=" list of customer's response. ",
						content=@Content(
									schema=@Schema(
											implementation= CustomerPaymentsResponse.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Add a new card payment method. It will be marked as preferred. ",
			description=" Add a new card payment method. It will be marked as preferred. ",
			operationId="    POST_Payment_cardPaymentMethod",
			responses= {
				@ApiResponse(description=" Token id in payment gateway ",
						content=@Content(
									schema=@Schema(
											implementation= CardPaymentMethodTokenDto.class
											)
								)
				)}
	)
    public CardPaymentMethodTokenDto addCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod);

    /**
     * Update existing card payment method.
     *
     * @param cardPaymentMethod Card payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/cardPaymentMethod")
	@Operation(
			summary=" Update existing card payment method. ",
			description=" Update existing card payment method. ",
			operationId="    PUT_Payment_cardPaymentMethod",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus updateCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod);

    /**
     * Remove card payment method. If it was marked as preferred, some other payment method will be marked as preferred
     *
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/cardPaymentMethod")
	@Operation(
			summary=" Remove card payment method. If it was marked as preferred, some other payment method will be marked as preferred ",
			description=" Remove card payment method. If it was marked as preferred, some other payment method will be marked as preferred ",
			operationId="    DELETE_Payment_cardPaymentMethod",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus removeCardPaymentMethod(@QueryParam("id") Long id);

    /**
     * List available card payment methods for a given customer account identified either by id or by code.
     *
     * @param customerAccountId   Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of card payment methods
     */
    @GET
    @Path("/cardPaymentMethod/list")
	@Operation(
			summary=" List available card payment methods for a given customer account identified either by id or by code. ",
			description=" List available card payment methods for a given customer account identified either by id or by code. ",
			operationId="    GET_Payment_cardPaymentMethod_list",
			responses= {
				@ApiResponse(description=" A list of card payment methods ",
						content=@Content(
									schema=@Schema(
											implementation= CardPaymentMethodTokensDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Retrieve card payment method by its id. ",
			description=" Retrieve card payment method by its id. ",
			operationId="    GET_Payment_cardPaymentMethod",
			responses= {
				@ApiResponse(description=" Card payment DTO ",
						content=@Content(
									schema=@Schema(
											implementation= CardPaymentMethodTokenDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Add a new payment method. It will be marked as preferred. ",
			description=" Add a new payment method. It will be marked as preferred. ",
			operationId="    POST_Payment_paymentMethod",
			responses= {
				@ApiResponse(description=" Token id in payment gateway ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentMethodTokenDto.class
											)
								)
				)}
	)
    public PaymentMethodTokenDto addPaymentMethod(PaymentMethodDto paymentMethod);

    /**
     * Update existing payment method.
     *
     * @param ddPaymentMethod payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/paymentMethod")
	@Operation(
			summary=" Update existing payment method. ",
			description=" Update existing payment method. ",
			operationId="    PUT_Payment_paymentMethod",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus updatePaymentMethod(PaymentMethodDto ddPaymentMethod);

    /**
     * Remove payment method. If it was marked as preferred, some other payment method will be marked as preferred
     *
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/paymentMethod")
	@Operation(
			summary=" Remove payment method. If it was marked as preferred, some other payment method will be marked as preferred ",
			description=" Remove payment method. If it was marked as preferred, some other payment method will be marked as preferred ",
			operationId="    DELETE_Payment_paymentMethod",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List Payment Methods matching a given criteria ",
			description=" List Payment Methods matching a given criteria ",
			operationId="    GET_Payment_paymentMethod_list",
			responses= {
				@ApiResponse(description=" An payment method list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentMethodTokensDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List Payment Methods matching a customer account  ",
			description=" List Payment Methods matching a customer account  ",
			operationId="    GET_Payment_paymentMethod_findByCustomerAccount",
			responses= {
				@ApiResponse(description=" An payment method list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentMethodTokensDto.class
											)
								)
				)}
	)
    public PaymentMethodTokensDto findPaymentMethodByCustomerAccount(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit);

    /**
     * List paymentMethods matching a given criteria
     *
     * @return List of paymentMethods
     */
    @GET
    @Path("/paymentMethod/listGetAll")
	@Operation(
			summary=" List paymentMethods matching a given criteria ",
			description=" List paymentMethods matching a given criteria ",
			operationId="    GET_Payment_paymentMethod_listGetAll",
			responses= {
				@ApiResponse(description=" List of paymentMethods ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentMethodTokensDto.class
											)
								)
				)}
	)
    PaymentMethodTokensDto listGetAll();

    /**
     * List Payment Methods matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return An payment method list
     */
    @POST
    @Path("/paymentMethod/list")
	@Operation(
			summary=" List Payment Methods matching a given criteria ",
			description=" List Payment Methods matching a given criteria ",
			operationId="    POST_Payment_paymentMethod_list",
			responses= {
				@ApiResponse(description=" An payment method list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentMethodTokensDto.class
											)
								)
				)}
	)
    public PaymentMethodTokensDto listPaymentMethodPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Retrieve payment method by its id.
     *
     * @param id Id
     * @return payment DTO
     */
    @GET
    @Path("/paymentMethod")
	@Operation(
			summary=" Retrieve payment method by its id. ",
			description=" Retrieve payment method by its id. ",
			operationId="    GET_Payment_paymentMethod",
			responses= {
				@ApiResponse(description=" payment DTO ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentMethodTokenDto.class
											)
								)
				)}
	)
    public PaymentMethodTokenDto findPaymentMethod(@QueryParam("id") Long id);

    /**
     * Enable a Payment method with a given id
     *
     * @param id Payment method id
     * @return Request processing status
     */
    @POST
    @Path("/paymentMethod/{id}/enable")
	@Operation(
			summary=" Enable a Payment method with a given id ",
			description=" Enable a Payment method with a given id ",
			operationId="    POST_Payment_paymentMethod_{id}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enablePaymentMethod(@PathParam("id") Long id);

    /**
     * Disable a Payment method with a given id
     *
     * @param id Payment method id
     * @return Request processing status
     */
    @POST
    @Path("/paymentMethod/{id}/disable")
	@Operation(
			summary=" Disable a Payment method with a given id ",
			description=" Disable a Payment method with a given id ",
			operationId="    POST_Payment_paymentMethod_{id}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Add a new payment gateway. ",
			description=" Add a new payment gateway. ",
			operationId="    POST_Payment_paymentGateway",
			responses= {
				@ApiResponse(description=" the paymentGateway dto created ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentGatewayResponseDto.class
											)
								)
				)}
	)
    public PaymentGatewayResponseDto addPaymentGateway(PaymentGatewayDto paymentGateway);

    /**
     * Update existing payment gateway.
     *
     * @param paymentGateway payment gateway DTO
     * @return Action status
     */
    @PUT
    @Path("/paymentGateway")
	@Operation(
			summary=" Update existing payment gateway. ",
			description=" Update existing payment gateway. ",
			operationId="    PUT_Payment_paymentGateway",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus updatePaymentGateway(PaymentGatewayDto paymentGateway);

    /**
     * Remove payment gateway.
     *
     * @param code payment gateway's code
     * @return Action status
     */
    @DELETE
    @Path("/paymentGateway")
	@Operation(
			summary=" Remove payment gateway. ",
			description=" Remove payment gateway. ",
			operationId="    DELETE_Payment_paymentGateway",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List payment gateways matching a given criteria ",
			description=" List payment gateways matching a given criteria ",
			operationId="    GET_Payment_paymentGateway_list",
			responses= {
				@ApiResponse(description=" An payment gateway list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentGatewayResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List payment gateways matching a given criteria. ",
			description=" List payment gateways matching a given criteria. ",
			operationId="    POST_Payment_paymentGateway_list",
			responses= {
				@ApiResponse(description=" An payment gateway list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentGatewayResponseDto.class
											)
								)
				)}
	)
    public PaymentGatewayResponseDto listPaymentGatewaysPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Retrieve payment gateway by its code.
     *
     * @param code payment gateway's code
     * @return payment DTO
     */
    @GET
    @Path("/paymentGateway")
	@Operation(
			summary=" Retrieve payment gateway by its code. ",
			description=" Retrieve payment gateway by its code. ",
			operationId="    GET_Payment_paymentGateway",
			responses= {
				@ApiResponse(description=" payment DTO ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentGatewayResponseDto.class
											)
								)
				)}
	)
    public PaymentGatewayResponseDto findPaymentGateway(@QueryParam("code") String code);

    /**
     * Create or update payment gateway.
     *
     * @param paymentGateway payment gateway DTO
     * @return the paymentGateway dto created
     */
    @POST
    @Path("/paymentGateway/createOrUpdate")
	@Operation(
			summary=" Create or update payment gateway. ",
			description=" Create or update payment gateway. ",
			operationId="    POST_Payment_paymentGateway_createOrUpdate",
			responses= {
				@ApiResponse(description=" the paymentGateway dto created ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentGatewayResponseDto.class
											)
								)
				)}
	)
    public PaymentGatewayResponseDto createOrUpdatePaymentGateway(PaymentGatewayDto paymentGateway);

    /**
     * Enable a Payment gateway with a given code
     *
     * @param code Payment gateway code
     * @return Request processing status
     */
    @POST
    @Path("/paymentGateway/{code}/enable")
	@Operation(
			summary=" Enable a Payment gateway with a given code ",
			description=" Enable a Payment gateway with a given code ",
			operationId="    POST_Payment_paymentGateway_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enablePaymentGateway(@PathParam("code") String code);

    /**
     * Disable a Payment gateway with a given code
     *
     * @param code Payment gateway code
     * @return Request processing status
     */
    @POST
    @Path("/paymentGateway/{code}/disable")
	@Operation(
			summary=" Disable a Payment gateway with a given code ",
			description=" Disable a Payment gateway with a given code ",
			operationId="    POST_Payment_paymentGateway_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disablePaymentGateway(@PathParam("code") String code);

    /**
     * Creates a RUM sequence associated to the given payment gateway.
     * @param postData the RUM sequence details
     * @return Request processing status
     */
    @POST
    @Path("/paymentGateway/rumSequence")
	@Operation(
			summary=" Creates a RUM sequence associated to the given payment gateway. ",
			description=" Creates a RUM sequence associated to the given payment gateway. ",
			operationId="    POST_Payment_paymentGateway_rumSequence",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createRumSequence(PaymentGatewayRumSequenceDto postData);

    /**
     * Updates a RUM sequence associated to the given payment gateway.
     * @param postData the RUM sequence details
     * @return Request processing status
     */
    @PUT
    @Path("/paymentGateway/rumSequence")
	@Operation(
			summary=" Updates a RUM sequence associated to the given payment gateway. ",
			description=" Updates a RUM sequence associated to the given payment gateway. ",
			operationId="    PUT_Payment_paymentGateway_rumSequence",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateRumSequence(PaymentGatewayRumSequenceDto postData);

    /**
     * Finds the RUM sequence with the specified code.
     * @param code code of the RUM sequence 
     * @return Request processing status
     */
    @GET
    @Path("/paymentGateway/rumSequence/{code}")
	@Operation(
			summary=" Finds the RUM sequence with the specified code. ",
			description=" Finds the RUM sequence with the specified code. ",
			operationId="    GET_Payment_paymentGateway_rumSequence_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentGatewayRumSequenceResponseDto.class
											)
								)
				)}
	)
    PaymentGatewayRumSequenceResponseDto findRumSequence(@PathParam("code") String code);

    /**
     * Deletes the RUM sequence with the specified code.
     * @param code code of the RUM sequence
     * @return Request processing status
     */
    @DELETE
    @Path("/paymentGateway/rumSequence/{code}")
	@Operation(
			summary=" Deletes the RUM sequence with the specified code. ",
			description=" Deletes the RUM sequence with the specified code. ",
			operationId="    DELETE_Payment_paymentGateway_rumSequence_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus deleteRumSequence(@PathParam("code") String code);

    /**
     * Generates the next RUM sequence number.
     *
     * @param code code of the sequence
     * @return sequence value dto
     */
    @POST
    @Path("/paymentGateway/rumSequence/{code}/next")
	@Operation(
			summary=" Generates the next RUM sequence number. ",
			description=" Generates the next RUM sequence number. ",
			operationId="    POST_Payment_paymentGateway_rumSequence_{code}_next",
			responses= {
				@ApiResponse(description=" sequence value dto ",
						content=@Content(
									schema=@Schema(
											implementation= GenericSequenceValueResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List Payment matching a given criteria. ",
			description=" List Payment matching a given criteria. ",
			operationId="    GET_Payment_history_list",
			responses= {
				@ApiResponse(description=" An invoice list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentHistoriesDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List invoices matching a given criteria. ",
			description=" List invoices matching a given criteria. ",
			operationId="    POST_Payment_history_list",
			responses= {
				@ApiResponse(description=" An invoice list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentHistoriesDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Add a new ddRequest builder. ",
			description=" Add a new ddRequest builder. ",
			operationId="    POST_Payment_ddRequestBuilder",
			responses= {
				@ApiResponse(description=" the ddRequestBuilder dto created ",
						content=@Content(
									schema=@Schema(
											implementation= DDRequestBuilderResponseDto.class
											)
								)
				)}
	)
    public DDRequestBuilderResponseDto addDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder);

    /**
     * Update existing ddRequest builder.
     *
     * @param ddRequestBuilder ddRequest builder DTO
     * @return Action status
     */
    @PUT
    @Path("/ddRequestBuilder")
	@Operation(
			summary=" Update existing ddRequest builder. ",
			description=" Update existing ddRequest builder. ",
			operationId="    PUT_Payment_ddRequestBuilder",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus updateDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder);

    /**
     * Remove ddRequest builder.
     *
     * @param code ddRequest builder's code
     * @return Action status
     */
    @DELETE
    @Path("/ddRequestBuilder")
	@Operation(
			summary=" Remove ddRequest builder. ",
			description=" Remove ddRequest builder. ",
			operationId="    DELETE_Payment_ddRequestBuilder",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List DDRequest Builders matching a given criteria ",
			description=" List DDRequest Builders matching a given criteria ",
			operationId="    GET_Payment_ddRequestBuilder_list",
			responses= {
				@ApiResponse(description=" An ddRequest builder list ",
						content=@Content(
									schema=@Schema(
											implementation= DDRequestBuilderResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List DDRequest Builders matching a given criteria. ",
			description=" List DDRequest Builders matching a given criteria. ",
			operationId="    POST_Payment_ddRequestBuilder_list",
			responses= {
				@ApiResponse(description=" An ddRequest builder list ",
						content=@Content(
									schema=@Schema(
											implementation= DDRequestBuilderResponseDto.class
											)
								)
				)}
	)
    public DDRequestBuilderResponseDto listDDRequestBuildersPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Retrieve ddRequest builder by its code.
     *
     * @param code ddRequest builder's code
     * @return ddRequest builder DTO
     */
    @GET
    @Path("/ddRequestBuilder")
	@Operation(
			summary=" Retrieve ddRequest builder by its code. ",
			description=" Retrieve ddRequest builder by its code. ",
			operationId="    GET_Payment_ddRequestBuilder",
			responses= {
				@ApiResponse(description=" ddRequest builder DTO ",
						content=@Content(
									schema=@Schema(
											implementation= DDRequestBuilderResponseDto.class
											)
								)
				)}
	)
    public DDRequestBuilderResponseDto findDDRequestBuilder(@QueryParam("code") String code);

    /**
     * Create or update ddRequest builder.
     *
     * @param ddRequestBuilder ddRequest builder DTO
     * @return the ddRequestBuilder dto created
     */
    @POST
    @Path("/ddRequestBuilder/createOrUpdate")
	@Operation(
			summary=" Create or update ddRequest builder. ",
			description=" Create or update ddRequest builder. ",
			operationId="    POST_Payment_ddRequestBuilder_createOrUpdate",
			responses= {
				@ApiResponse(description=" the ddRequestBuilder dto created ",
						content=@Content(
									schema=@Schema(
											implementation= DDRequestBuilderResponseDto.class
											)
								)
				)}
	)
    public DDRequestBuilderResponseDto createOrUpdateDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder);

    /**
     * Enable a ddRequest builder with a given code
     *
     * @param code ddRequest builder code
     * @return Request processing status
     */
    @POST
    @Path("/ddRequestBuilder/{code}/enable")
	@Operation(
			summary=" Enable a ddRequest builder with a given code ",
			description=" Enable a ddRequest builder with a given code ",
			operationId="    POST_Payment_ddRequestBuilder_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enableDDRequestBuilder(@PathParam("code") String code);

    /**
     * Disable a ddRequest builder with a given code
     *
     * @param code ddRequest builder code
     * @return Request processing status
     */
    @POST
    @Path("/ddRequestBuilder/{code}/disable")
	@Operation(
			summary=" Disable a ddRequest builder with a given code ",
			description=" Disable a ddRequest builder with a given code ",
			operationId="    POST_Payment_ddRequestBuilder_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
     * @param automaticReturnUrl the automatic return URL (currently only for ATOS Wallet)
     * @param allowedActions the allowed actions (currently only for ATOS Wallet)
     * @param returnContext the return context (currently only for ATOS Wallet)
     * @param isOneShotPayment if true Accept one-time payments for cards else Save payment details to charge your customers later.
     * @param cancelUrl If set, Checkout displays a back button and customers will be directed to this URL if they decide to cancel payment and return to your website.
     * @param authenticationAmount Allows you to send in an authentication amount which can be greater or equal to the order amount. The currency code of the authentication amount should be the same as the currency code of the order amount. In case you don't provide an authentication amount we will use the order amount for the authentication automatically.
     * @param advancedOptions the advanced options (currently only for ATOS Wallet)
     * @param paymentMethodType CARD or DIRECTDEBIT
     * @return the PaymentHostedCheckoutResponseDto
     */
    @GET
    @Path("/paymentGateway/getHostedCheckoutUrl")
	@Operation(
			summary=" Get the Hosted Checkout URL for payment. ",
			description=" Get the Hosted Checkout URL for payment. ",
			operationId="    GET_Payment_paymentGateway_getHostedCheckoutUrl",
			responses= {
				@ApiResponse(description=" the PaymentHostedCheckoutResponseDto ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentHostedCheckoutResponseDto.class
											)
								)
				)}
	)
    PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(@QueryParam("ca") String customerAccountCode,
                                                                 @QueryParam("returnUrl") String returnUrl,
                                                                 @DefaultValue("fr_FR") @QueryParam("locale") String locale,
                                                                 @DefaultValue("100") @QueryParam("amount") String amount,
                                                                 @DefaultValue("EUR") @QueryParam("currencyCode") String currencyCode,
                                                                 @DefaultValue("FINAL_AUTHORIZATION") @QueryParam("authorizationMode") String authorizationMode,
                                                                 @DefaultValue("fr") @QueryParam("countryCode") String countryCode,
                                                                 @DefaultValue("false") @QueryParam("skipAuthentication") Boolean skipAuthentication,
                                                                 @DefaultValue("INGENICO_GC") @QueryParam("gatewayPaymentName") String gatewayPaymentName,
                                                                 @DefaultValue("101") @QueryParam("variant") String variant,
                                                                 @QueryParam("seller") String sellerCode,
                                                                 @QueryParam("automaticReturnUrl") String automaticReturnUrl,
                                                                 @QueryParam("allowedActions") String allowedActions,
                                                                 @QueryParam("returnContext") String returnContext,
                                                                 @QueryParam("authenticationAmount") String authenticationAmount,
                                                                 @DefaultValue("") @QueryParam("advancedOptions") String advancedOptions,
                                                                 @DefaultValue("false") @QueryParam("isOneShotPayment") Boolean isOneShotPayment,
                                                                 @QueryParam("cancelUrl") String cancelUrl,
                                                                 @DefaultValue("CARD")@QueryParam("paymentMethodType")PaymentMethodEnum paymentMethodType
    );
    
    /**
     * Get the Hosted Checkout Status .
     * @param id the hostedCheckout Id
     * @param customerAccountCode the customerAccount Code    
     * @param sellerCode the Seller Code   
     * @return the HostedCheckoutStatusResponseDto
     */
    @GET
    @Path("/paymentGateway/getHostedCheckoutStatus")
    @Operation(
            summary=" Get the Hosted Checkout Status. ",
            description=" Get the Hosted Checkout Status. ",
            operationId="    GET_Payment_paymentGateway_getHostedCheckoutStatus",
            responses= {
                @ApiResponse(description=" the HostedCheckoutStatusResponseDto ",
                        content=@Content(
                                    schema=@Schema(
                                            implementation= HostedCheckoutStatusResponseDto.class
                                            )
                                )
                )}
    )
    HostedCheckoutStatusResponseDto getHostedCheckoutStatus(@QueryParam("id") String id,  
                                                            @QueryParam("ca") String customerAccountCode,                                                                 
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
	@Operation(
			summary=" Create or update payment Schedules template. ",
			description=" Create or update payment Schedules template. ",
			operationId="    POST_Payment_paymentScheduleTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus createOrUpdatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto);

    /**
     * Create  payment Schedules template.
     *
     * @param paymentScheduleTemplateDto payment Schedule Template Dto 
     * @return Request processing status
     */
    @POST
    @Path("/paymentScheduleTemplate")
	@Operation(
			summary=" Create  payment Schedules template. ",
			description=" Create  payment Schedules template. ",
			operationId="    POST_Payment_paymentScheduleTemplate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus createPaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto);

    /**
     * Create  payment Schedules template.
     *
     * @param paymentScheduleTemplateDto payment Schedule Template Dto 
     * @return Request processing status
     */
    @PUT
    @Path("/paymentScheduleTemplate")
	@Operation(
			summary=" Create  payment Schedules template. ",
			description=" Create  payment Schedules template. ",
			operationId="    PUT_Payment_paymentScheduleTemplate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus updatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto);

    /**
     * remove  payment Schedules template.
     *
     * @param paymentScheduleTemplateCode payment Schedule Template Code  to remove
     * @return Request processing status
     */
    @DELETE
    @Path("/paymentScheduleTemplate")
	@Operation(
			summary=" remove  payment Schedules template. ",
			description=" remove  payment Schedules template. ",
			operationId="    DELETE_Payment_paymentScheduleTemplate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus removePaymentScheduleTemplate(@QueryParam("paymentScheduleTemplateCode") String paymentScheduleTemplateCode);

    /**
     * find  payment Schedules template.
     *
     * @param paymentScheduleTemplateCode payment Schedule Template Code  to find
     * @return Request processing status
     */
    @GET
    @Path("/paymentScheduleTemplate")
	@Operation(
			summary=" find  payment Schedules template. ",
			description=" find  payment Schedules template. ",
			operationId="    GET_Payment_paymentScheduleTemplate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentScheduleTemplateResponseDto.class
											)
								)
				)}
	)
    public PaymentScheduleTemplateResponseDto findPaymentScheduleTemplate(@QueryParam("paymentScheduleTemplateCode")  String paymentScheduleTemplateCode);

    /**
     * List  PaymentScheduleTemplate matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria.
     * @return An paymentScheduleTemplate dto list
     */
    @POST
    @Path("/paymentScheduleTemplate/list")
	@Operation(
			summary=" List  PaymentScheduleTemplate matching a given criteria ",
			description=" List  PaymentScheduleTemplate matching a given criteria ",
			operationId="    POST_Payment_paymentScheduleTemplate_list",
			responses= {
				@ApiResponse(description=" An paymentScheduleTemplate dto list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentScheduleTemplatesDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List  PaymentScheduleTemplate matching a given criteria ",
			description=" List  PaymentScheduleTemplate matching a given criteria ",
			operationId="    GET_Payment_paymentScheduleTemplate_list",
			responses= {
				@ApiResponse(description=" An paymentScheduleTemplate dto list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentScheduleTemplatesDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Update  payment Schedules instance. ",
			description=" Update  payment Schedules instance. ",
			operationId="    PUT_Payment_paymentScheduleInstance",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus updatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto);

    /**
     * Find  PaymentScheduleInstance by ID
     *
     * @param id PaymentScheduleInstance ID
     * @return A paymentScheduleInstance dto
     */
    @GET
    @Path("/paymentScheduleInstance")
	@Operation(
			summary=" Find  PaymentScheduleInstance by ID ",
			description=" Find  PaymentScheduleInstance by ID ",
			operationId="    GET_Payment_paymentScheduleInstance",
			responses= {
				@ApiResponse(description=" A paymentScheduleInstance dto ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentScheduleInstanceResponseDto.class
											)
								)
				)}
	)
    public PaymentScheduleInstanceResponseDto findPaymentScheduleInstance(@QueryParam("id") Long id);

    /**
     * List  PaymentScheduleInstance matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria.
     * @return An PaymentScheduleInstance dto list
     */
    @POST
    @Path("/paymentScheduleInstance/list")
	@Operation(
			summary=" List  PaymentScheduleInstance matching a given criteria ",
			description=" List  PaymentScheduleInstance matching a given criteria ",
			operationId="    POST_Payment_paymentScheduleInstance_list",
			responses= {
				@ApiResponse(description=" An PaymentScheduleInstance dto list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentScheduleInstancesDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List  paymentScheduleInstance matching a given criteria ",
			description=" List  paymentScheduleInstance matching a given criteria ",
			operationId="    GET_Payment_paymentScheduleInstance_list",
			responses= {
				@ApiResponse(description=" An paymentScheduleInstance dto list ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentScheduleInstancesDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Terminate  payment Schedules instance. ",
			description=" Terminate  payment Schedules instance. ",
			operationId="    PUT_Payment_paymentScheduleInstance_terminate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus terminatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto);

    /**
     * Cancel  payment Schedules instance.
     *
     * @param paymentScheduleInstanceDto payment Schedule Instance Dto
     * @return Request processing status
     */
    @PUT
    @Path("/paymentScheduleInstance/cancel")
	@Operation(
			summary=" Cancel  payment Schedules instance. ",
			description=" Cancel  payment Schedules instance. ",
			operationId="    PUT_Payment_paymentScheduleInstance_cancel",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Gets a created mandate. ",
			description=" Gets a created mandate. ",
			operationId="    GET_Payment_paymentGateway_checkMandate",
			responses= {
				@ApiResponse(description=" created mandate ",
						content=@Content(
									schema=@Schema(
											implementation= MandatInfoDto.class
											)
								)
				)}
	)
    public MandatInfoDto checkMandate(@QueryParam("mandateReference") String mandateReference,@QueryParam("mandateId") String mandateId,@QueryParam("customerAccountCode") String customerAccountCode);

    
    /**
     * approve SepaDDMandate
     * 
     * @param customerAccountCode
     * @param tokenId
     * @return
     */
    @GET
    @Path("/paymentGateway/approveSepaDDMandate")
	@Operation(
			summary=" approve SepaDDMandate  ",
			description=" approve SepaDDMandate  ",
			operationId="    GET_Payment_paymentGateway_approveSepaDDMandate",
			responses= {
				@ApiResponse(description="ActionStatus response",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus approveSepaDDMandate(@QueryParam("customerAccountCode") String customerAccountCode,@QueryParam("tokenId")String tokenId);



    /**
     * Update Payment schedule instance item, the update is only about amount and requestPaymentDate.
     *
     * @param paymentScheduleInstanceItemsDto a list of paymentScheduleInstanceItemDto
     * @param paymentScheduleInstanceId       the paymentScheduleInstance's Id
     * @return Request processing status
     */
    @PUT
    @Path("/paymentScheduleInstance/{id}/items")
	@Operation(
			summary=" Update Payment schedule instance item, the update is only about amount and requestPaymentDate. ",
			description=" Update Payment schedule instance item, the update is only about amount and requestPaymentDate. ",
			operationId="    PUT_Payment_paymentScheduleInstance_{id}_items",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus replacePaymentScheduleInstanceItem(@PathParam("id") Long paymentScheduleInstanceId, PaymentScheduleInstanceItemsDto paymentScheduleInstanceItemsDto);

   /**
    * Make a real  card payment through gateways like Ingenico or Stripe, and then create the account Operation that can be matched with other accounts Operations
    * 
    * @param payByCardDto
    * @return PaymentResponseDto
    */
    @POST
    @Path("/payByCard")
	@Operation(
			summary=" Make a real payment through gateways like Ingenico or Stripe",
			description=" Make a real payment through gateways like Ingenico or Stripe. ",
			operationId="POST_Payment_payByCard",
			responses= {
				@ApiResponse(description=" payByCard ",
						content=@Content(
									schema=@Schema(
											implementation= PaymentResponseDto.class
											)
								)
				)}
	)
	PaymentResponseDto payByCard(PayByCardOrSepaDto payByCardDto);
    
    /**
     * Make a real  Sepa payment through gateways like Ingenico or Stripe, and then create the account Operation that can be matched with other accounts Operations
     * 
     * @param payBySepaDto
     * @return PaymentResponseDto
     */
     @POST
     @Path("/payBySepa")
 	@Operation(
 			summary=" Make a real payment through gateways like Ingenico or Stripe",
 			description=" Make a real payment through gateways like Ingenico or Stripe. ",
 			operationId="POST_Payment_payBySepa",
 			responses= {
 				@ApiResponse(description=" payBySepa ",
 						content=@Content(
 									schema=@Schema(
 											implementation= PaymentResponseDto.class
 											)
 								)
 				)}
 	)
 	PaymentResponseDto payBySepa(PayByCardOrSepaDto payBySepaDto);
    
    
}
