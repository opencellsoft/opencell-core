package org.meveo.util;

import java.util.Properties;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.model.billing.Country;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MandatStateEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.payments.impl.IngenicoGatewayPayment;

import com.ingenico.connect.gateway.sdk.java.ApiException;
import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.Marshaller;
import com.ingenico.connect.gateway.sdk.java.defaultimpl.DefaultMarshaller;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.BankAccountIban;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.CreateMandateRequest;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.CreateMandateResponse;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.GetMandateResponse;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandateAddress;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandateContactDetails;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandateCustomer;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandatePersonalInformation;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandatePersonalName;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandateResponse;

public class MyTest {

	public static void main(String[] args) {
		//IngenicoGatewayPayment ingenico=new IngenicoGatewayPayment();
		CustomerAccount customerAccount=new CustomerAccount();
		
		ContactInformation ci=new ContactInformation();
		ci.setEmail("rachidweb@gmail.com");
		
		Address address=new Address();
		address.setAddress1("2578");
		address.setCity("Paris");
		Country country=new Country();
		country.setDescription("France");
		country.setCountryCode("FR");
		address.setCountry(country);
		address.setZipCode("20000");
		
		Name name=new Name();
		name.setFirstName("rac");
		name.setLastName("AIT");
		Title title=new Title();
		title.setDescription("Mr");
		name.setTitle(title);
		
		customerAccount.setContactInformation(ci);
		customerAccount.setAddress(address);
		customerAccount.setName(name);
		customerAccount.setExternalRef1("cust1");
		createMandate(customerAccount, "FR7630001007941234567890185", "BPIAB0000000001521FD02032");
		checkMandat("BPIAB0000000001521FD02032", null);
	}
	
    public static void createMandate(CustomerAccount customerAccount,String iban,String mandateReference) throws BusinessException {
    	try {
    		BankAccountIban bankAccountIban=new BankAccountIban(); 
    		bankAccountIban.setIban(iban);
 
    		MandateContactDetails contactDetails=new MandateContactDetails();
    		if(customerAccount.getContactInformation() != null ) {
    			contactDetails.setEmailAddress(customerAccount.getContactInformation().getEmail()); 
    		}
    		
    		MandateAddress address=new MandateAddress();
    		if (customerAccount.getAddress() != null) {
    		address.setCity(customerAccount.getAddress().getCity());
    		address.setCountryCode(customerAccount.getAddress().getCountry() == null ? null : customerAccount.getAddress().getCountry().getCountryCode());
    		address.setStreet(customerAccount.getAddress().getAddress1());
    		address.setZip(customerAccount.getAddress().getZipCode());
    		}
    		MandatePersonalName name = new MandatePersonalName();
    		MandatePersonalInformation personalInformation =new MandatePersonalInformation();
    		if (customerAccount.getName() != null) {
    			name.setFirstName("-");
    			name.setSurname(customerAccount.getName().getLastName()); 
    			personalInformation.setTitle(customerAccount.getName().getTitle() == null ? "" : customerAccount.getName().getTitle().getDescription());
    		}  
    		personalInformation.setName(name);
    		MandateCustomer customer=new MandateCustomer();
    		customer.setBankAccountIban(bankAccountIban);
    		customer.setContactDetails(contactDetails);
    		customer.setMandateAddress(address);
    		customer.setPersonalInformation(personalInformation);
    		customer.setCompanyName(customerAccount.getName().getLastName());
    		
    		CreateMandateRequest body = new CreateMandateRequest();
    		body.setUniqueMandateReference(mandateReference);
    		body.setCustomer(customer);
    		body.setCustomerReference(customerAccount.getExternalRef1()); 
    		body.setRecurrenceType("RECURRING");
    		body.setSignatureType("UNSIGNED");
    		getClient();
    		CreateMandateResponse response = client.merchant("bpifrance").mandates().create(body); 
    		System.out.println(response.getMandate().getStatus());
    	} catch (ApiException ev) {
    		ev.printStackTrace();

    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    }
    /** The client. */
    private  static Client client = null;
    
    private static Marshaller marshaller = null;
    private static void getClient() {
    	Properties pros=new Properties();
    	pros.setProperty("connect.api.authorizationType", "V1HMAC");
    	pros.setProperty("connect.api.connectTimeout", "5000");
    	pros.setProperty("connect.api.endpoint.host", "eu.preprod.api-ingenico.com");
    	pros.setProperty("connect.api.endpoint.scheme", "https");
    	pros.setProperty("connect.api.integrator", "");
    	pros.setProperty("connect.api.socketTimeout", "300000"); 
        CommunicatorConfiguration communicatorConfiguration = new CommunicatorConfiguration(pros);
        communicatorConfiguration.setApiKeyId("7a6495ada4604559");
        communicatorConfiguration.setSecretApiKey("G5HkI4/lsx3rnMx2mMjtQMBDuxAvugUTXL2+Po6h0Xs=");
         client = Factory.createClient(communicatorConfiguration);
         marshaller = DefaultMarshaller.INSTANCE;
    }
    
   
    public static MandatInfoDto checkMandat(String mandatReference, String mandateId) throws BusinessException {
    	getClient();
    	MandatInfoDto mandatInfoDto=new MandatInfoDto();
    	GetMandateResponse response = client.merchant("bpifrance").mandates().get(mandatReference); 
    	MandateResponse mandatResponse=response.getMandate();
    	if(mandatResponse!=null) { 
    		if("WAITING_FOR_REFERENCE".equals(mandatResponse.getStatus())) {
    			mandatInfoDto.setState(MandatStateEnum.waitingForReference); 
    		}else {
    			mandatInfoDto.setState(MandatStateEnum.valueOf(mandatResponse.getStatus().toLowerCase()));
    		}
    		mandatInfoDto.setReference(mandatResponse.getUniqueMandateReference());
    	}  
System.out.println(mandatInfoDto.getState());
    	return mandatInfoDto;

    } 

}
