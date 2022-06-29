package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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