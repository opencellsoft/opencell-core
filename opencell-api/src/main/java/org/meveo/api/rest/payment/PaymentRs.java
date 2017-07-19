package org.meveo.api.rest.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.CardTokenDto;
import org.meveo.api.dto.payment.CardTokenResponseDto;
import org.meveo.api.dto.payment.ListCardTokenResponseDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.rest.IBaseRs;

@Path("/payment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PaymentRs extends IBaseRs {

    /**
     * Creates automated payment. It also process if a payment is matching or not
     * 
     * @param postData Payment's data
     * @return
     */
    @POST
    @Path("/create")
    public ActionStatus create(PaymentDto postData);

    /**
     * Returns a list of account operations along with the balance of a customer
     * 
     * @param customerAccountCode
     * @return
     */
    @GET
    @Path("/customerPayment")
    public CustomerPaymentsResponse list(@QueryParam("customerAccountCode") String customerAccountCode);
    
    /**
     * Tokenize payment card details
     * 
     * @param cardTokenRequestDto
     * @return
     */
    @POST
    @Path("/cardToken")
    public CardTokenResponseDto createCardToken(CardTokenDto postData);
    
    @PUT
    @Path("/cardToken")
	public ActionStatus updateCardToken(CardTokenDto cardTokenRequestDto);
	
    @DELETE
    @Path("/cardToken")
	public ActionStatus removeCardToken(@QueryParam("id")Long id);
	
    @GET
    @Path("/cardToken/list")
	public ListCardTokenResponseDto listCardToken(@QueryParam("customerAccountId")Long customerAccountId,@QueryParam("customerAccountCode")String customerAccountCode);
	
    @GET
    @Path("/cardToken")
	public CardTokenResponseDto findCardToken(@QueryParam("id")Long id);

}
