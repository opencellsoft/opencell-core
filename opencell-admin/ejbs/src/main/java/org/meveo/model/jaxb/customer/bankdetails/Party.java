package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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