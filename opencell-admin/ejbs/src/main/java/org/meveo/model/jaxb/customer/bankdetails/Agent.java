package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "finInstnId" })
@XmlRootElement(name = "Agt")
public class Agent {
    @XmlElement(name = "FinInstnId", required = true)
    protected FinancialInstitution finInstnId;

    public FinancialInstitution getFinInstnId() {
        return finInstnId;
    }
    public void setFinInstnId(FinancialInstitution finInstnId) {
        this.finInstnId = finInstnId;
    }
}