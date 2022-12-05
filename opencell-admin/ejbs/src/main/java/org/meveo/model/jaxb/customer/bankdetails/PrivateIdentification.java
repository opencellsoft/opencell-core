package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "dtAndPlcOfBirth", "others" })
@XmlRootElement(name = "PrvtId")
public class PrivateIdentification { 
    @XmlElement(name = "DtAndPlcOfBirth")
    protected InfOfBirth dtAndPlcOfBirth;
    @XmlElement(name = "Othr")
    protected List<Other> others;
    
    public InfOfBirth getDtAndPlcOfBirth() {
        return dtAndPlcOfBirth;
    }
    public void setDtAndPlcOfBirth(InfOfBirth dtAndPlcOfBirth) {
        this.dtAndPlcOfBirth = dtAndPlcOfBirth;
    }
    public List<Other> getOthers() {
        return others;
    }
    public void setOthers(List<Other> others) {
        this.others = others;
    }
}