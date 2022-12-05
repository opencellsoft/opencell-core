package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "type", "reference" })
@XmlRootElement(name = "CdtrRefInf")
public class CreditorRefInf {
    @XmlElement(name = "Tp")
    protected Type type;
    @XmlElement(name = "Ref")
    protected String reference;

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
}