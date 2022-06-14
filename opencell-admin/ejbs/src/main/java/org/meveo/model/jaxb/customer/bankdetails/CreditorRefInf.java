package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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

    public CreditorRefInf() {
    }
}