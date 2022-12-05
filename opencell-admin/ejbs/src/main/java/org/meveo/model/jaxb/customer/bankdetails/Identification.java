package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "organisationId", "privateIdentification", "others", "iBAN" })
@XmlRootElement(name = "Id")
public class Identification { 
    @XmlElement(name = "OrgId")
    protected Organisation organisationId;
    @XmlElement(name = "PrvtId")
    protected PrivateIdentification  privateIdentification;
    @XmlElement(name = "Othr")
    protected List<Other> others;
    @XmlElement(name = "IBAN")
    protected String iBAN;

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
        return others;
    }
    public void setOthers(List<Other> others) {
        this.others = others;
    }
    public String getiBAN() {
        return iBAN;
    }
    public void setiBAN(String iBAN) {
        this.iBAN = iBAN;
    }
}