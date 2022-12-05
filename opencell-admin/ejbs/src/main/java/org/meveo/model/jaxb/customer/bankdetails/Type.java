package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "codeOrProprietary" })
@XmlRootElement(name = "Tp")
public class Type {
    @XmlElement(name = "CdOrPrtry", required = true)
    protected CodeOrProprietary codeOrProprietary;
    
    public CodeOrProprietary getCodeOrProprietary() {
        return codeOrProprietary;
    }
    public void setCodeOrProprietary(CodeOrProprietary codeOrProprietary) {
        this.codeOrProprietary = codeOrProprietary;
    }  
}