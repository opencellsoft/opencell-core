package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "bicFi" })
@XmlRootElement(name = "FinInstnId")
public class FinancialInstitution {
    @XmlElement(name = "BICFI")
    protected String bicFi;

    public String getBicFi() {
        return bicFi;
    }
    public void setBicFi(String bicFi) {
        this.bicFi = bicFi;
    }
}