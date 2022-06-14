package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "finInstnId" })
@XmlRootElement(name = "DbtrAgt")
public class DebtorAgent {
    @XmlElement(name = "FinInstnId", required = true)
    protected FinancialInstitution finInstnId;    

    public FinancialInstitution getFinInstnId() {
        return finInstnId;
    }
    public void setFinInstnId(FinancialInstitution finInstnId) {
        this.finInstnId = finInstnId;
    }
}