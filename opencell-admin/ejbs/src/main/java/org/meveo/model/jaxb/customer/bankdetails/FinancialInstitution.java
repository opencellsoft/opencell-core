package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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