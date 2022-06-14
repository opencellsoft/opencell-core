package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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