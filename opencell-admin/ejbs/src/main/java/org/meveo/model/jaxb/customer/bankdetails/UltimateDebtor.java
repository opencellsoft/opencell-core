package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name", "id" })
@XmlRootElement(name = "UltmtDbtr")
public class UltimateDebtor {     
    @XmlElement(name = "Nm")
    protected String name;
    @XmlElement(name = "Id", required = true)
    protected Identification id;   

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Identification getId() {
        return id;
    }
    public void setId(Identification id) {
        this.id = id;
    }    
}