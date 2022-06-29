package org.meveo.model.jaxb.customer.bankdetails;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "accountSwitching", "dateOfSignature" })
@XmlRootElement(name = "AcctSwtchngRef")
public class AccountSwitchingRef {
    @XmlElement(name = "AcctSwtchngId", required = true)
    protected String accountSwitching;    
    @XmlElement(name = "DtOfSgntr", required = true)
    protected Date dateOfSignature;
    
    public String getAccountSwitching() {
        return accountSwitching;
    }
    public void setAccountSwitching(String accountSwitching) {
        this.accountSwitching = accountSwitching;
    }
    public Date getDateOfSignature() {
        return dateOfSignature;
    }
    public void setDateOfSignature(Date dateOfSignature) {
        this.dateOfSignature = dateOfSignature;
    }
}