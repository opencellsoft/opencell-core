package org.meveo.service.payments.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ingenico.connect.gateway.sdk.java.Marshaller;
import com.ingenico.connect.gateway.sdk.java.defaultimpl.DefaultMarshaller;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

/**
 * The Class PaypalGatewayPayment.
 *
 * @author anasseh
 * @since 9.2
 */
@PaymentGatewayClass
public class StripeGatewayPayment implements GatewayPaymentInterface {

    /** The log. */
    protected Logger log = LoggerFactory.getLogger(StripeGatewayPayment.class);
    
    /** The payment gateway. */
    private PaymentGateway paymentGateway = null; 
    
    /** The client. */
    private  APIContext context = null;
    
    private Marshaller marshaller = null;

    /**
     * Connect.
     */
    private void connect() {       
        context = new APIContext(paymentGateway.getMarchandId(), paymentGateway.getSecretKey(), "sandbox");
        marshaller = DefaultMarshaller.INSTANCE;
    }

    private ParamBean paramBean() {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        ParamBean paramBean = paramBeanFactory.getInstance();
        return paramBean;
    }

    /**
     * Gets the client.
     *
     * @return the client
     */
    private  APIContext getContext() {
        if (context == null) {
            connect();
        }
        return context;
    }

    /**
     * Gets the client object
     *
     * @return the client object
     */
    @Override
    public  Object getClientObject() {
        if (context == null) {
            connect();
        }
        return context;
    }

    
   
   
   


    @Override
    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

	@Override
	public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
			CreditCardTypeEnum cardType) throws BusinessException {
		// TODO Auto-generated method stub
		return "payPalToke";
	}

	@Override
	public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
		PaymentResponseDto rep= new PaymentResponseDto();
	if (5 == 2) {	
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		// Set redirect URLs
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("http://localhost:3000/cancel");
		redirectUrls.setReturnUrl("http://localhost:3000/process");

		// Set payment details
		Details details = new Details();
		details.setShipping("1");
		details.setSubtotal("5");
		details.setTax("1");

		// Payment amount
		Amount amount = new Amount();
		amount.setCurrency("USD");
		// Total must be equal to sum of shipping, tax and subtotal.
		amount.setTotal("7");
		amount.setDetails(details);

		// Transaction information
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction
		  .setDescription("This is the payment transaction description.");

		// Add transaction to a list
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		// Add payment details
		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setRedirectUrls(redirectUrls);
		payment.setTransactions(transactions);
		
		
		log.info("\n\n\n 1111 payment:"+payment.toJSON());
		 
		// Create payment
		try {
		  Payment createdPayment = payment.create(getContext());
		  log.info("\n\n\n 1111 createdPayment:"+createdPayment.toJSON());

		  Iterator links = createdPayment.getLinks().iterator();
		  while (links.hasNext()) {
		    Links link = (Links) links.next();
		    if (link.getRel().equalsIgnoreCase("approval_url")) {
		    	log.info("\n\n\n approval_url:"+link.getHref());
		    }
		  }
		} catch (PayPalRESTException e) {
			e.printStackTrace();
			log.error(e.getDetails().toJSON());
		}
		
		
		
	}else {
		Payment payment = new Payment();
		payment.setId("PAYID-LZR5JLA5KL272663H431442B");

		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId("QB6QU6NDQQUWS");
		try {
		  Payment createdPayment = payment.execute(getContext(), paymentExecution);
		  
		  log.info("\n\n\n 1111 createdPayment:"+createdPayment.toJSON());
		  System.out.println(createdPayment);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		  System.err.println(e.getDetails());
		}
	}
	return rep;
	}

	@Override
	public PaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
			CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentResponseDto doPaymentSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentResponseDto checkPayment(String paymentID, PaymentMethodEnum paymentMethodType) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelPayment(String paymentID) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PaymentResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
			CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MandatInfoDto checkMandat(String mandatReference, String mandateId) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createInvoice(Invoice invoice) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}


}