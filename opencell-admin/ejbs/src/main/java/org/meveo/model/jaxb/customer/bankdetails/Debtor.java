package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name", "id" })
@XmlRootElement(name = "Dbtr")
public class Debtor {     
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