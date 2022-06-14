package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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