package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name", "identification" })
@XmlRootElement(name = "Pty")
public class Party {
    @XmlElement(name = "Nm")
    protected String name;    
    @XmlElement(name = "Id")
    protected Identification identification;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Identification getIdentification() {
        return identification;
    }
    public void setIdentification(Identification identification) {
        this.identification = identification;
    } 
}