package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "documentType3Code" })
@XmlRootElement(name = "CdOrPrtry")
public class CodeOrProprietary {
    @XmlElement(name = "Cd", required = true)
    protected String documentType3Code;
    
    public String getDocumentType3Code() {
        return documentType3Code;
    }
    public void setDocumentType3Code(String documentType3Code) {
        this.documentType3Code = documentType3Code;
    }
}