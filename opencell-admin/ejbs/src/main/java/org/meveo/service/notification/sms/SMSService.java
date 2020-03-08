package org.meveo.service.notification.sms;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.meveo.commons.utils.ParamBean.getInstance;

import com.twilio.rest.api.v2010.account.Message;
import org.meveo.api.dto.response.notification.SMSInfoResponseDTO;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.crm.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class SMSService {

    private static Logger log = LoggerFactory.getLogger(SMSService.class);

    @Inject
    private CustomerService customerService;

    public SMSInfoResponseDTO send(SMSInfo smsInfo) {
        String phoneNumberTo = fromCustomer(smsInfo.getCustomerCode());
        SMSProviderInfo smsProvider =  new SMSProviderInfo(phoneNumberTo, smsInfo.getBody());
        SMSProvider provider = providerInstance();
        Message message = provider.send(smsProvider);
        log.info(format("Account SID %s sent SMS to %s with status %s",
                message.getAccountSid(), message.getTo(), message.getStatus().toString()));
        return toResponseDto(message);
    }

    private String fromCustomer(String code) {
        Customer customer = ofNullable(customerService.findByCode(code))
                .orElseThrow(() -> new MeveoApiException("Customer not found"));
        return customer.getCustomerAccounts()
                .stream()
                .findFirst()
                .map(CustomerAccount::getContactInformation)
                .map(ContactInformation::getMobile)
                .orElseThrow(() -> new MeveoApiException("Customer contact information is missing"));
    }

    private SMSProvider providerInstance() {
        String accountSid = getInstance().getProperty("twilio.account.sid", "AC6f01746d5c14fa05fb60975c46aa09e3");
        String token = getInstance().getProperty("twilio.auth.token", "df00b21b07f46f763c6d0da154d03fb7");
        String from = getInstance().getProperty("twilio.phoneNumberFrom", "+14242342601");
        return SMSProviderFactory.create(accountSid, token, from);
    }

    private SMSInfoResponseDTO toResponseDto(Message message) {
        SMSInfoResponseDTO response = new SMSInfoResponseDTO();
        response.setStatus(message.getStatus().toString());
        response.setBody(message.getBody());
        response.setUri(message.getUri());
        response.setSentTo(message.getTo());
        response.setPrice(message.getPrice());
        ofNullable(message.getErrorCode()).ifPresent(response::setErrorCode);
        ofNullable(message.getErrorMessage()).ifPresent(response::setErrorMessage);
        return response;
    }
}