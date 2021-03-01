package org.meveo.service.script;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;
import org.meveo.model.AccountEntity;
import org.meveo.api.account.AccountHierarchyApi;
import org.meveo.api.dto.account.*;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.service.script.Script;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.api.dto.billing.*;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.CustomFieldDto;


public class LoadTestCreateCustomerHiarachy extends Script{

    // Customer api
    AccountHierarchyApi accountHierarchyApi = (AccountHierarchyApi) getServiceInterface("AccountHierarchyApi");

    // Subscription
    SubscriptionApi subscriptionapi = (SubscriptionApi) getServiceInterface("SubscriptionApi");

    // Address Dto
    public AddressDto getAddress(){
        AddressDto address = new AddressDto(); // Address Dto 
        address.setAddress1("3 rue passante"); // First line of the address 
        address.setAddress2("Batiment A"); // 2nd line of the address 
        address.setAddress3("Bureau D42"); // 3rd line of the address 
        address.setZipCode("75001"); // Zip code 
        address.setCity("PARIS"); // City 
        address.setCountry("FR");
        return address;
    }

    // Name Dto
    public NameDto getName(){
        NameDto name = new NameDto(); // Name Dto 
        name.setTitle("Cie"); // CRM Title 
        name.setFirstName("Nuage SAS"); // CRM first name 
        name.setLastName("Nuage SAS"); // CRM last name 
        return name;
    }

    // ContactInformation Dto
    public ContactInformationDto getContactInfo(){
        ContactInformationDto contactInformationDto = new ContactInformationDto(); // Contact  infos Dto 
        contactInformationDto.setEmail("OPENCELL@exemple.com"); // Seller Email 
        contactInformationDto.setMobile("+33123546789"); // Mobile phone 
        return contactInformationDto;
    }

    // Payement Dto
    public List<PaymentMethodDto> getPaymentMethod(){
        PaymentMethodDto paymentMethodDto = new PaymentMethodDto(); // Add the Payement Dto 
        PaymentMethodEnum paymentMethodType = PaymentMethodEnum.CHECK; // Payement Method "CHECK"
        paymentMethodDto.setPaymentMethodType(paymentMethodType);
        List<PaymentMethodDto> paymentMethods = new ArrayList<>(); //  Payement method list 
        paymentMethods.add(paymentMethodDto);
        return paymentMethodDto; 
    }

    // CRMAccountHierarchy Dto
    public CRMAccountHierarchyDto getCRMAccountHierarchy( int j){
        // Create a customer account 
            CRMAccountHierarchyDto CRMaccountHierarchyDto = new CRMAccountHierarchyDto(); // Customer DTO 
            CRMaccountHierarchyDto.setCrmAccountType("C_UA"); // Account type 
            CRMaccountHierarchyDto.setCrmParentCode("OPENCELL"); // CrmParentCode 
            CRMaccountHierarchyDto.setCode("OPENCELL-"+j);
            CRMaccountHierarchyDto.setDescription(CRMaccountHierarchyDto.getCode() + " Description"); // Crm Description 
            
            CRMaccountHierarchyDto.setName(getName()); // Push the name Dto
            try {
                Date date = new SimpleDateFormat("yyy-mm-dd").parse("2010-01-10");
                CRMaccountHierarchyDto.setSubscriptionDate(date); // Crm Subscription date  
            }catch (ParseException e) {
                log.error("parse exception in create measured values", e);
            }
            CRMaccountHierarchyDto.setJobTitle("...");
            CRMaccountHierarchyDto.setVatNo("FR12345678901234");
            CRMaccountHierarchyDto.setRegistrationNo("12354678901234");  
            
            CRMaccountHierarchyDto.setAddress(getAddress()); // Push the Address Dto 

            CRMaccountHierarchyDto.setContactInformation(getContactInfo()); // Push the contact infos Dto 

            CRMaccountHierarchyDto.setEmail("fr.fr@fr.com"); // set the Crm Email 
            CRMaccountHierarchyDto.setLanguage("FRA"); // The Crm Language 

            // Create payment method
            CRMaccountHierarchyDto.setPaymentMethods(getPaymentMethod());

            CRMaccountHierarchyDto.setCustomerCategory("CLIENT");

            CRMaccountHierarchyDto.setCurrency("EUR");

            CRMaccountHierarchyDto.setBillingCycle("BC_MONTHLY_1ST");

            CRMaccountHierarchyDto.setCountry("FR");

            CRMaccountHierarchyDto.setElectronicBilling(true);

            CRMaccountHierarchyDto.setCustomFields(null);

            return CRMaccountHierarchyDto; 
    }

     // Payement Dto
    public SubscriptionDto getSubscription(int j){
        // The subscription creation
            SubscriptionDto subscriptionDto = new SubscriptionDto(); // The subscription Dto 

            // Add the services to the subscription
            subscriptionDto.setCode("OPENCELL-SU-"+j);
            subscriptionDto.setDescription(" Souscription " + subscriptionDto.getCode() + " description "); // Subscription Description 
            subscriptionDto.setUserAccount(Code); // The associated user 
            // get the code from the user account code
            String offerTemplateCode= "OF_BASIC"; // the Offer 
            subscriptionDto.setOfferTemplate(offerTemplateCode);
            subscriptionDto.setSeller("OPENCELL"); // The CRm parent code (Seller)
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-12-15T01:23:45");
                subscriptionDto.setSubscriptionDate(date); // The subscription date 
            }catch (ParseException e) {
                log.error("parse exception in create measured values", e);
            }
            subscriptionDto.setTerminationDate(null); // subscription termination date 
            try {
                Date date = new SimpleDateFormat("yyy-mm-dd").parse("2020-12-20");
                subscriptionDto.setEndAgreementDate(date); // End agreement date 
            }catch (ParseException e) {
                log.error("parse exception in create measured values", e);
            }
            subscriptionDto.setStatus(null); // Subscription status 
            subscriptionDto.setTerminationReason(null); // Termination reason 
            subscriptionDto.setCustomFields(null); // Custom fields 
            return subscriptionDto; 
    }

    // OSS service dto  1St service
    public ServiceToActivateDto getOSSservice(){
            ServiceToActivateDto OSSserviceToActivateDto = new ServiceToActivateDto(); // SE_OSS service 
            OSSserviceToActivateDto.setCode("SE_OSS"); // Set the service code 
            OSSserviceToActivateDto.setQuantity(new BigDecimal("10")); // Set the quantity  
            try {
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                OSSserviceToActivateDto.setSubscriptionDate(simpleDateFormat.parse("2019-12-15T01:23:45")); // service Subscription date 
            } catch (Exception e) {
                log.error("parse exception in create measured values", e);
            }
            OSSserviceToActivateDto.setRateUntilDate(null); // Rate until Date 
            OSSserviceToActivateDto.setCustomFields(null); // Custom Fields 
        return OSSserviceToActivateDto;
    }

    // SE_OST service dto 2nd service with CF
    public ServiceToActivateDto getOSTservice(){
            ServiceToActivateDto OSTserviceToActivateDto = new ServiceToActivateDto(); // SE_OST service 
            OSTserviceToActivateDto.setCode("SE_OST"); // Set the service code 
            OSTserviceToActivateDto.setQuantity(new BigDecimal("1"));
            OSTserviceToActivateDto.setRateUntilDate(null); // Rate until date 
            try {
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                OSTserviceToActivateDto.setSubscriptionDate(simpleDateFormat.parse("2019-12-15T01:23:45")); // Subscription date 
            } catch (Exception e) {
                log.error("parse exception in create measured values", e);
            }
            CustomFieldsDto customFieldsDtoSE_OST = new CustomFieldsDto(); // Custom fields 
            List<CustomFieldDto> ListOfCF_SE_OST =  new ArrayList<>(); // Custom fields list 
            CustomFieldDto CF_SE_OST = new CustomFieldDto();
            CF_SE_OST.setCode("CF_SE_DOUBLE"); // Custom field code 
            CF_SE_OST.setDoubleValue(100.00); // Custom field double value 
            ListOfCF_SE_OST.add(CF_SE_OST); // Add the custome field to the list of custom fields  
            customFieldsDtoSE_OST.setCustomField(ListOfCF_SE_OST); // Set the custom field 
            OSTserviceToActivateDto.setCustomFields(customFieldsDtoSE_OST); // Set the custom field 
        return OSTserviceToActivateDto;
    }

    // SE_REC_ADV service dto
    public ServiceToActivateDto getSE_REC_ADVService(){
            ServiceToActivateDto SE_REC_ADVserviceToActivateDto = new ServiceToActivateDto(); // SE_REC_ADV Service Dto 
            SE_REC_ADVserviceToActivateDto.setCode("SE_REC_ADV"); // set the service code 
            SE_REC_ADVserviceToActivateDto.setQuantity(new BigDecimal("1")); // Set the service quantity 
            try {
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                SE_REC_ADVserviceToActivateDto.setSubscriptionDate(simpleDateFormat.parse("2019-12-15T01:23:45")); // Set the subscription date 
            } catch (Exception e) {
                log.error("parse exception in create measured values", e);
            }
            CustomFieldsDto customFieldsDtoSE_REC_ADV = new CustomFieldsDto();  // Custom fields for the SE_REC_ADV service
            List<CustomFieldDto> ListOfCF_SE_REC_ADV =  new ArrayList<>(); // List of custom fields 

            CustomFieldDto CF_SE_REC_ADV1 = new CustomFieldDto(); // 1st custom field 
            CF_SE_REC_ADV1.setCode("CF_SE_BUILD_RUN_SITUATION"); // Custom field code 
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                CF_SE_REC_ADV1.setValuePeriodStartDate(simpleDateFormat.parse("2010-04-19")); // set the custom field start date 
                CF_SE_REC_ADV1.setValuePeriodEndDate(simpleDateFormat.parse("2020-05-19")); // set the custom field ending date  
            } catch (Exception e) {
                log.error("parse exception in create measured values", e);
            }
            CF_SE_REC_ADV1.setValuePeriodPriority(1); // Set the custom field Period Priority 
            CF_SE_REC_ADV1.setStringValue("BUILD"); // Set the custom field string value for the service role 
            ListOfCF_SE_REC_ADV.add(CF_SE_REC_ADV1);

            CustomFieldDto CF_SE_REC_ADV2 = new CustomFieldDto(); // Custom field 
            CF_SE_REC_ADV2.setCode("CF_SE_BUILD_RUN_SITUATION"); // Custom field code 
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = ;
                CF_SE_REC_ADV2.setValuePeriodStartDate(simpleDateFormat.parse("2020-05-20")); // Custom field Period Start date 
                CF_SE_REC_ADV2.setValuePeriodEndDate(simpleDateFormat.parse("2030-05-19")); // Custom field Period End Date 
            } catch (Exception e) {
                log.error("parse exception in create measured values", e);
            }
            CF_SE_REC_ADV2.setValuePeriodPriority(2); // Custom field Period Priority 
            CF_SE_REC_ADV2.setStringValue("RUN"); // Custom field String Value for the role 
            ListOfCF_SE_REC_ADV.add(CF_SE_REC_ADV2); // Add the custom field 

            CustomFieldDto CF_SE_REC_ADV3 = new CustomFieldDto(); // New Custom field 
            CF_SE_REC_ADV3.setCode("CF_SE_BUILD_DOUBLE_VERSION"); // Custom Field code 
            CF_SE_REC_ADV3.setValuePeriodPriority(1); // Set the custom field Period Priority 
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                CF_SE_REC_ADV3.setValuePeriodStartDate(simpleDateFormat.parse("2010-04-19")); // Set the Period Start Date 
                CF_SE_REC_ADV3.setValuePeriodEndDate(simpleDateFormat.parse("2030-04-19")); // Set the Period End Date  
            } catch (Exception e) {
                log.error("parse exception in create measured values", e);
            }
            CF_SE_REC_ADV3.setDoubleValue(5000.00); // Set the Custom field Double value 
            ListOfCF_SE_REC_ADV.add(CF_SE_REC_ADV3); // Add the custom field to the custom field 

            CustomFieldDto CF_SE_REC_ADV4 = new CustomFieldDto(); // Custom field dto 
            CF_SE_REC_ADV4.setCode("CF_SE_RUN_DOUBLE_VERSION"); // Custom field code 
            CF_SE_REC_ADV4.setValuePeriodPriority(1); // Custom field Value Period Priority 
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                CF_SE_REC_ADV4.setValuePeriodStartDate(simpleDateFormat.parse("2010-04-19")); // Custom field period Start date 
                CF_SE_REC_ADV4.setValuePeriodEndDate(simpleDateFormat.parse("2030-04-19")); // Custom field period End date 
            } catch (Exception e) {
                log.error("parse exception in create measured values", e);
            }
            CF_SE_REC_ADV4.setDoubleValue(8000.00); // Custom field Double value 
            ListOfCF_SE_REC_ADV.add(CF_SE_REC_ADV4); // Add the custom  field to the CF list 

            customFieldsDtoSE_REC_ADV.setCustomField(ListOfCF_SE_REC_ADV); // Add the list of custom field too the list for the Parent Custom field 
            SE_REC_ADVserviceToActivateDto.setCustomFields(customFieldsDtoSE_REC_ADV); // Set the custom field 
            SE_REC_ADVserviceToActivateDto.setRateUntilDate(null); // Rate until date  
        return SE_REC_ADVserviceToActivateDto;
    }

    // SE_USG_UNIT service dto
    public ServiceToActivateDto getSE_USG_UNITservice(){
            ServiceToActivateDto SE_USG_UNITserviceToActivateDto = new ServiceToActivateDto(); // Service Dto 
            SE_USG_UNITserviceToActivateDto.setCode("SE_USG_UNIT"); // Set the service code 
            SE_USG_UNITserviceToActivateDto.setQuantity(new BigDecimal("1")); // Set the quantity for the service 
            try {
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                SE_USG_UNITserviceToActivateDto.setSubscriptionDate(simpleDateFormat.parse("2019-12-15T01:23:45")); // Set the subscription date 
            } catch (Exception e) {
                log.error("parse exception in create measured values", e);
            }
            SE_USG_UNITserviceToActivateDto.setRateUntilDate(null); // Set the Rate until date 
            SE_USG_UNITserviceToActivateDto.setCustomFields(null); // Set the custom fields 
        return SE_USG_UNITserviceToActivateDto;
    }

    // Services Dto
    public ActivateServicesRequestDto getServices(int j){
        // The subscription creation
            // Instantiate services
            ActivateServicesRequestDto activateServicesRequestDto = new ActivateServicesRequestDto(); // Services dto 
            activateServicesRequestDto.setSubscription("OPENCELL-SU-"+j); // The associated subscription 
            
            ServicesToActivateDto servicesToActivate = new ServicesToActivateDto(); // Services Dto 
            List<ServiceToActivateDto> ListOfServicesToActivate =  new ArrayList<>(); // List of services 
                       
            ListOfServicesToActivate.add(getOSSservice()); // Add to the Services list 

            ListOfServicesToActivate.add(getOSTservice()); // add the service to the list of services 

            ListOfServicesToActivate.add(getSE_REC_ADVService()); // Add the service to the list of Services to activate 

            ListOfServicesToActivate.add(getSE_USG_UNITservice()); // Add the service to the list of services to activate 

            servicesToActivate.setService(ListOfServicesToActivate); // Set the list of services to activate 
            activateServicesRequestDto.setServicesToActivateDto(servicesToActivate); // Set the services to activate 

            return activateServicesRequestDto; 
    }

    @Override
    public void LoadTestsCrmSubServ(Map<String, Object> methodContext) throws BusinessException{ // Create the CRM method 
        // Max of the loop for the creation of the Customers + Subscription + Activate services
        int MAX = 1000; // for example we create @MAX customers + subscriptions with the same services
        //log.debug(" MAX : " + MAX);
        // maximum value for the loop
        for(int i =0; i < MAX;i++){

            //accountHierarchyApi.createCRMAccountHierarchy(CRMaccountHierarchyDto);
            accountHierarchyApi.createOrUpdateCRMAccountHierarchy(getCRMAccountHierarchy(i)); // Set the CRM hierarchy 
            
            subscriptionapi.createOrUpdate(getSubscription(i)); // Set the subscription to the Api  

            subscriptionapi.activateServices(getServices(i)); // Set the services Dto to the Api 

        }

    }

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException { // The execute method 
        createAccount(methodContext);
    }
}