package org.meveo.service.script.payment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.client.utils.DateUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

import com.ingenico.connect.gateway.sdk.java.ApiException;
import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.DeclinedRefundException;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.Marshaller;
import com.ingenico.connect.gateway.sdk.java.defaultimpl.DefaultMarshaller;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.AmountOfMoney;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.BankAccountIban;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.ContactDetailsBase;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.OrderStatusOutput;
import com.ingenico.connect.gateway.sdk.java.domain.errors.definitions.APIError;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.AddressPersonal;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.PersonalName;
import com.ingenico.connect.gateway.sdk.java.domain.refund.RefundRequest;
import com.ingenico.connect.gateway.sdk.java.domain.refund.RefundResponse;
import com.ingenico.connect.gateway.sdk.java.domain.refund.definitions.BankRefundMethodSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.refund.definitions.RefundCustomer;
import com.ingenico.connect.gateway.sdk.java.domain.refund.definitions.RefundReferences;
 
 
public class CustomIngenicoGatewayRefundScript extends PaymentScript {

	 
	private static final long serialVersionUID = 1L;
	
	 /** The payment gateway. */
    private PaymentGateway paymentGateway = null; 
	
	
	  /** The client. */
    private  Client client = null;
    
    private Marshaller marshaller = null;
    
    private RecordedInvoiceService recordedInvoiceService =(RecordedInvoiceService) getServiceInterface("RecordedInvoiceService");
    
    private MatchingCodeService matchingCodeService =(MatchingCodeService) getServiceInterface("MatchingCodeService"); 
   private AccountOperationService accountOperationService =(AccountOperationService) getServiceInterface("AccountOperationService"); 
   
    @Override
    public void doRefundSepa(Map<String, Object> methodContext) throws BusinessException {  
      
    	Map<String,Object> contextAdditionnal=(Map<String,Object> )methodContext.get(CONTEXT_ADDITIONAL_INFOS); 

    	String customerAccountCode =(String)contextAdditionnal.get("customerAccountCode"); 
    	DDPaymentMethod preferredMethod = (DDPaymentMethod)methodContext.get(CONTEXT_TOKEN);
    	Long ctsAmount=(Long)methodContext.get(CONTEXT_AMOUNT_CTS);
    	Long aoToRefundId=(Long)contextAdditionnal.get("aoToPayOrRefund");
    	AccountOperation ao=null;
    	if(aoToRefundId!=null) {
    	 ao=accountOperationService.findById(aoToRefundId);
    	}
    	Long refundAOId = (Long)contextAdditionnal.get("createdAO");
    	paymentGateway=(PaymentGateway)methodContext.get("PAYMENT_GATEWAY");
    	log.info("RecordedInvoice ID ==="+ao.getId());
    	
    	methodContext.put(PaymentScript.RESULT_PAYMENT_STATUS, PaymentStatusEnum.NOT_PROCESSED);  
    	if(ao!=null && ao instanceof RecordedInvoice ) {
          log.info("OK1----");
    		RecordedInvoice recordedInvoice=(RecordedInvoice)ao; 
    		log.info("RecordedInvoice ID ==="+ao.getId()+"Status=="+recordedInvoice.getMatchingStatus());
    		//credit note 
    		Invoice creditNote=recordedInvoice.getInvoice()!=null?recordedInvoice.getInvoice():null; 
    		if(creditNote!=null) {
              log.info("OK2----");
    			//commercial invoice  
    			if(!creditNote.getLinkedInvoices().isEmpty()) {
                  log.info("OK3----");
    				if(creditNote.getLinkedInvoices().size()>1) {
    					throw new BusinessException("the credit note "+creditNote.getInvoiceNumber() +" has more than one orignal invoice");
    				}
    				Invoice originalInvoice = creditNote.getLinkedInvoices().iterator().next();

    				if(originalInvoice!=null) {
                      log.info("OK4----");
    					//AO of commercial invoice
    					RecordedInvoice linkedAo=recordedInvoiceService.findByInvoiceId(originalInvoice.getId());
    					if(linkedAo==null) {
    						throw new BusinessException("the original invoice linked to the credit note "+creditNote.getInvoiceNumber() +" has no recorded invoice AO");
    					}
    					//get the payment AO
    					AccountOperation payment=getPaymentMatchedOperation( linkedAo);
    					if(payment!=null) {
    						PaymentResponseDto doPaymentResponseDto = IngenicoRefundSepa(preferredMethod,ctsAmount,payment.getReference());
    						methodContext.put(PaymentScript.RESULT_PAYMENT_STATUS,doPaymentResponseDto.getPaymentStatus());
    				        methodContext.put(PaymentScript.RESULT_PAYMENT_ID,doPaymentResponseDto.getPaymentID());
    				        methodContext.put(PaymentScript.RESULT_TRANSACTION_ID,doPaymentResponseDto.getTransactionId());
    				        methodContext.put(PaymentScript.RESULT_ERROR_MSG,doPaymentResponseDto.getErrorMessage());
    				        methodContext.put(PaymentScript.RESULT_ERROR_CODE,doPaymentResponseDto.getErrorCode());
    					}else {
    						log.info("no payment....");  
    						try {
    							AccountOperation refundAo=accountOperationService.findById(refundAOId);
    							if(refundAo!=null) {
    								if (refundAo.getMatchingStatus() == MatchingStatusEnum.P || refundAo.getMatchingStatus() == MatchingStatusEnum.L) {
    									matchingCodeService.unmatchingOperationAccount(refundAo);
    								}
    								accountOperationService.remove(refundAOId);
    							}
    							log.info("RecordedInvoice NO payment ==="+ao.getId()+"Status=="+recordedInvoice.getMatchingStatus());
    							List<Long> listReferenceToMatch = new ArrayList<Long>();
    							listReferenceToMatch.add(recordedInvoice.getId());
    							listReferenceToMatch.add(linkedAo.getId());
    							log.info("matching the recorded invoice AO id= "+recordedInvoice.getId()+" with the linked AO id = "+linkedAo.getId());
    							matchingCodeService.matchOperations(null, customerAccountCode, listReferenceToMatch, null, MatchingTypeEnum.A);
    							log.info("Matching done.....");
    							methodContext.put(PaymentScript.RESULT_PAYMENT_STATUS, PaymentStatusEnum.ACCEPTED); 

    						} catch (BusinessException | NoAllOperationUnmatchedException | UnbalanceAmountException e) {
    							log.error("Error during matching the linked invoice AO with the credit note AO:", e);
    							throw new BusinessException("Error during matching the linked invoice AO ="+originalInvoice.getInvoiceNumber()+" with the credit note AO number ="+creditNote.getInvoiceNumber());
    						}
    					}	
    				}	
    			}   
    		}
    	} else {  
    		throw new BusinessException("the account operation is null or is not a recorded invoice AO");
    	}

    }
    
   
    public PaymentResponseDto IngenicoRefundSepa(DDPaymentMethod paymentMethod, Long ctsAmount,String paymentId) throws BusinessException {
      if(paymentId==null) {
    	  throw new BusinessException("The payment should be not null");
      }
    	PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto(); 
    	doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.NOT_PROCESSED); 
    	CustomerAccount customerAccount = paymentMethod.getCustomerAccount();
    	AmountOfMoney amountOfMoney = new AmountOfMoney();
    	amountOfMoney.setAmount(ctsAmount);
    	amountOfMoney.setCurrencyCode(customerAccount.getTradingCurrency().getCurrencyCode());
    	
    	BankAccountIban bankAccountIban = new BankAccountIban();
    	bankAccountIban.setIban(paymentMethod.getBankCoordinates().getIban());

    	BankRefundMethodSpecificInput bankRefundMethodSpecificInput = new BankRefundMethodSpecificInput();
    	bankRefundMethodSpecificInput.setBankAccountIban(bankAccountIban);

    	PersonalName name = new PersonalName(); 
    	name.setSurname(customerAccount.getName().getLastName());

    	AddressPersonal address = new AddressPersonal();
    	address.setCountryCode(customerAccount.getAddress().getCountry() == null ? null : customerAccount.getAddress().getCountry().getCountryCode());
    	address.setName(name); 

    	ContactDetailsBase contactDetails = new ContactDetailsBase();
    	if(customerAccount.getContactInformation() != null ) {
    		contactDetails.setEmailAddress(customerAccount.getContactInformation().getEmail()); 
    	}
    	contactDetails.setEmailMessageType("html");


    	RefundCustomer customer = new RefundCustomer();
    	customer.setAddress(address);
    	customer.setContactDetails(contactDetails);

    	RefundReferences refundReferences = new RefundReferences(); 
    	refundReferences.setMerchantReference(customerAccount.getId() + "-" + amountOfMoney.getAmount() + "-" + System.currentTimeMillis());


    	RefundRequest body = new RefundRequest();
    	body.setAmountOfMoney(amountOfMoney);
    	body.setBankRefundMethodSpecificInput(bankRefundMethodSpecificInput);
    	body.setCustomer(customer);
    	body.setRefundDate(DateUtils.formatDate(new Date(), "yyyyMMdd"));
    	body.setRefundReferences(refundReferences);
        log.info("PaymentID=== "+paymentId);
        getClient();
        log.info("REQUEST:"+marshaller.marshal(body));
    	

    	try {
    		RefundResponse response = getClient().merchant(paymentGateway.getMarchandId()).payments().refund(paymentId, body);
    		if (response != null) {
    			log.info("doRefundSepa RESPONSE:"+marshaller.marshal(response)); 
    			doPaymentResponseDto.setPaymentID(response.getId());
    			doPaymentResponseDto.setPaymentStatus(mappingStaus(response.getStatus()));
    			if (response.getRefundOutput() != null && response.getRefundOutput().getReferences() != null) {
    				doPaymentResponseDto.setTransactionId(response.getRefundOutput().getReferences().getPaymentReference());
    				doPaymentResponseDto.setBankRefenrence(response.getRefundOutput().getReferences().getPaymentReference()); 
    			}
    			OrderStatusOutput statusOutput = response.getStatusOutput();
    			if (statusOutput != null) {
    				List<APIError> errors = statusOutput.getErrors();
    				if (CollectionUtils.isNotEmpty(errors)) {
    					doPaymentResponseDto.setErrorMessage(errors.toString());
    					doPaymentResponseDto.setErrorCode(errors.get(0).getId()); 
    				}
    			}
    			log.info("doPaymentResponse RESPONSE: "+doPaymentResponseDto); 
    			return doPaymentResponseDto;
    		} else {
    			throw new BusinessException("doRefundSepa response is null");
    		}
    	} catch (DeclinedRefundException e) {
          e.printStackTrace();
    		e.getRefundResult();
    	} catch (ApiException e) {
          e.printStackTrace();
    		log.error("Error on doRefundSepa :",e); 
    	} 
    	return doPaymentResponseDto;
    }

    private AccountOperation getPaymentMatchedOperation(AccountOperation ao) { 
    	AccountOperation matchedAO=null;
    	MatchingCode matchingCode=ao.getMatchingAmounts()!=null && !ao.getMatchingAmounts().isEmpty()?ao.getMatchingAmounts().get(0).getMatchingCode():null;
    	if(matchingCode!=null) { 
    			for(MatchingAmount matching:matchingCode.getMatchingAmounts()) {
    				matchedAO=matching.getAccountOperation();
    				if (matchedAO.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel()) ) {
    					return matchedAO;
    				}
    			
    		}  
    	}
    	return null;
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
    private  Client getClient() {
        if (client == null) {
            connect();
        }
        return client;
    }
    
    
    /**
     * Mapping staus.
     *
     * @param ingenicoStatus the ingenico status
     * @return the payment status enum
     */
    private PaymentStatusEnum mappingStaus(String ingenicoStatus) {
        if (ingenicoStatus == null) {
            return PaymentStatusEnum.ERROR;
        }
        if ("CREATED".equals(ingenicoStatus) || "PAID".equals(ingenicoStatus) || "REFUNDED".equals(ingenicoStatus) || "CAPTURED".equals(ingenicoStatus)) {
            return PaymentStatusEnum.ACCEPTED;
        }
        if (ingenicoStatus.startsWith("PENDING")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("ACCOUNT_VERIFIED")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("AUTHORIZATION_REQUESTED")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("CAPTURE_REQUESTED")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("REJECTED_CAPTURE")) {
            return PaymentStatusEnum.REJECTED;
        }
        if (ingenicoStatus.equals("REVERSED")) {
            return PaymentStatusEnum.ACCEPTED;
        }
        if (ingenicoStatus.equals("CHARGEBACKED")) {
            return PaymentStatusEnum.ACCEPTED;
        }
        if (ingenicoStatus.equals("REFUND_REQUESTED")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("PAYOUT_REQUESTED")) {
            return PaymentStatusEnum.PENDING;
        }
        
        return PaymentStatusEnum.REJECTED;
    }
    
    private void connect() {
        ParamBean paramBean = paramBean();
        //Init properties
        paramBean.getProperty("connect.api.authorizationType", "changeIt");
        paramBean.getProperty("connect.api.connectTimeout", "5000");
        paramBean.getProperty("connect.api.endpoint.host", "changeIt");
        paramBean.getProperty("connect.api.endpoint.scheme", "changeIt");
        paramBean.getProperty("connect.api.integrator", "");
        paramBean.getProperty("connect.api.socketTimeout", "300000");        
        CommunicatorConfiguration communicatorConfiguration = new CommunicatorConfiguration(ParamBean.getInstance().getProperties());
        if(paymentGateway!=null)
        communicatorConfiguration.setApiKeyId(paymentGateway.getApiKey());
        communicatorConfiguration.setSecretApiKey(paymentGateway.getSecretKey());
        client = Factory.createClient(communicatorConfiguration);
        marshaller = DefaultMarshaller.INSTANCE;
    }

    /**
     * Gets the client object
     *
     * @return the client object
     */
    enum ReportTransactionType {
        INV_STD("I"), PAY_DDT("P"), COM_PSP("OCC"),REJ_DDT("R");
        String label;

        ReportTransactionType(String label) {
            this.label = label;
        }

        public static ReportTransactionType fromValue(String value) {
            return Arrays.stream(ReportTransactionType.values())
                    .filter(reportTransactionType -> reportTransactionType.label.equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("no type for the given value " + value));
        }

        public String getLabel() {
            return label;
        }
    }
    
    public  Object getClientObject() {
        if (client == null) {
            connect();
        }
        return client;
    }


	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}


	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	
 
    
}