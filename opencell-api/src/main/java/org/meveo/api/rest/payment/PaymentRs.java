package org.meveo.api.rest.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.PaymentActionStatus;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import org.meveo.api.dto.payment.PaymentHistoriesDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokenDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.rest.IBaseRs;

@Path("/payment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PaymentRs extends IBaseRs {

    /**
     * Creates automated payment. It also process if a payment is matching or not
     * 
     * @param postData Payment's data
     * @return payment action status
     */
    @POST
    @Path("/create")
    public PaymentActionStatus create(PaymentDto postData);

    /**
     * Returns a list of account operations along with the balance of a customer.
     * 
     * @param customerAccountCode customer account code
     * @return list of customer's response.
     */
    @GET
    @Path("/customerPayment")
    public CustomerPaymentsResponse list(@QueryParam("customerAccountCode") String customerAccountCode);

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

    /************************************************************************************************/
    /**** Payment Gateways ****/
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
     * List Payment Gateways matching a given criteria
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
     * List Payment Gateways matching a given criteria.
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
    PaymentHistoriesDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List invoices matching a given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "transactions" in fields to include transactions and "pdf" to generate/include PDF document
     * @return An invoice list
     */
    @POST
    @Path("/history/list")
    PaymentHistoriesDto listPost(PagingAndFiltering pagingAndFiltering);

}