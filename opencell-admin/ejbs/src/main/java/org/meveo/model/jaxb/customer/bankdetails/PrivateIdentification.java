package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "dtAndPlcOfBirth", "Others" })
@XmlRootElement(name = "PrvtId")
public class PrivateIdentification { 
    @XmlElement(name = "DtAndPlcOfBirth")
    protected InfOfBirth dtAndPlcOfBirth;
    @XmlElement(name = "Othr")
    protected List<Other> Others;
    
    public InfOfBirth getDtAndPlcOfBirth() {
        return dtAndPlcOfBirth;
    }
    public void setDtAndPlcOfBirth(InfOfBirth dtAndPlcOfBirth) {
        this.dtAndPlcOfBirth = dtAndPlcOfBirth;
    }
    public List<Other> getOthers() {
        return Others;
    }
    public void setOthers(List<Other> others) {
        Others = others;
    }
    
    public PrivateIdentification() {
    }   
}