package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "organisationId", "privateIdentification", "Others", "iBAN" })
@XmlRootElement(name = "Id")
public class Identification { 
    @XmlElement(name = "OrgId")//, required = true
    protected Organisation organisationId;
    @XmlElement(name = "PrvtId")//, required = true
    protected PrivateIdentification  privateIdentification;
    @XmlElement(name = "Othr")//, required = true
    protected List<Other> Others;
    @XmlElement(name = "IBAN")//, required = true
    protected String iBAN;
    
    public Identification() {
    }

    public Organisation getOrganisationId() {
        return organisationId;
    }
    public void setOrganisationId(Organisation organisationId) {
        this.organisationId = organisationId;
    }
    public PrivateIdentification getPrivateIdentification() {
        return privateIdentification;
    }
    public void setPrivateIdentification(PrivateIdentification privateIdentification) {
        this.privateIdentification = privateIdentification;
    }
    public List<Other> getOthers() {
        return Others;
    }
    public void setOthers(List<Other> others) {
        Others = others;
    }
    public String getiBAN() {
        return iBAN;
    }
    public void setiBAN(String iBAN) {
        this.iBAN = iBAN;
    }
}